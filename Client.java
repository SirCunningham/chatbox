package chatbox;

import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;

// add chatRoom.items.addElement(clientSocket.getInetAddress()); when
// *new user is connected (certain msg comes)
// *user changes name
// Gör detta här eller i IOStream!
//OBS OBS OBS
//All data går igenom IOStream, alltid
//Gör krypteringen i IOStream så sker det både för meddelanden och filer automatiskt
//Filer använder en annan klient för gränssnittet är annorlunda där
//OBS OBS OBS
public class Client implements Runnable {

    private Socket clientSocket;
    protected BufferedReader i;
    protected PrintWriter o;
    private final int port;
    private final ChatRoom chatRoom;

    public Client(String host, int port, final ChatRoom chatRoom) {
        this.port = port;
        this.chatRoom = chatRoom;
        // Starta socket för klienten
        try {
            clientSocket = new Socket(host, port);
            i = new BufferedReader(new InputStreamReader(
                    clientSocket.getInputStream()));
            o = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (UnknownHostException e) {
            chatRoom.success = false;
            ChatCreator.showError("Don't know about host.");
        } catch (IOException e) {
            chatRoom.success = false;
            ChatCreator.showError("Couldn't get I/O for the connection to host.");
        } catch (IllegalArgumentException e) {
            chatRoom.success = false;
            ChatCreator.showError("Port out of range.");
        }
    }

    // Skapa tråd för att läsa från servern
    @Override
    public void run() {
        // Håll uppkopplingen tills servern vill avbryta den
        String responseLine;
        if (clientSocket != null && i != null && o != null) {
            try {
                // Skapa lyssnare för att skicka till servern
                class SendButtonListener implements ActionListener {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String msg = String.format("<message sender=\"%s\">"
                                + "<text color=\"%s\">%s</text></message>",
                                chatRoom.getName(), chatRoom.color,
                                Messages.getMessage(chatRoom));
                        if (!msg.equals("")) {
                            o.println(msg);
                        }
                    }
                }

                // Listener for keyrequest
                class KeyRequestButtonListener implements ActionListener {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //detta skickar bara till en person? loop behövs annars...
                        //vet inte vad du menar. o.println skickar till alla?
                        //man bryr sig endast om nycklen från den man markerat,
                        //men skickar keyrequest till alla
                        String chatName = (String) chatRoom.getList().getSelectedValue();
                        if (chatName != null) {
                            String message = (String) JOptionPane.showInputDialog(ChatCreator.frame, "Enter message:",
                                    "Send keyrequest", JOptionPane.INFORMATION_MESSAGE, null, null,
                                    "I request a key for " + String.valueOf(chatRoom.getKeyRequestEncryption()) + " from " + chatName + "!");
                            if (message != null) {

                                o.println(String.format("<message sender=\"%s\">"
                                        + "<text color=\"%s\"><keyrequest "
                                        + "type=\"%s\">%s"
                                        + "</keyrequest></text></message>",
                                        chatRoom.getName(), chatRoom.color,
                                        String.valueOf(chatRoom.getKeyRequestEncryption()), message));
                                //ersatte Messages.getMessage(chatRoom) med message
                                startKeyTimer(chatName);
                            }
                        }
                    }
                }
                // finns redan i ChatRoom, onödig dubblering, ta bort där?
                class SendFileButtonListener2 implements ActionListener {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //Send filerequest to every person in the chat??
                        String chatName = (String) chatRoom.getList().getSelectedValue();
                        if (chatName != null) {
                            o.println(Messages.getFileMessage(chatRoom));
                            startTimer(chatName);
                        }

                    }
                }

                // Stäng av hela programmet
                class CloseButtonListener implements ActionListener {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JButton button = (JButton) e.getSource();
                        int index = ChatCreator.tabbedPane.indexOfComponent(
                                button.getParent().getParent().getParent());
                        if (chatRoom.speedyDelete) {
                            ChatCreator.indices.get(index).doClick();
                            o.println(Messages.getQuitMessage(chatRoom));
                        } else {
                            Object[] options = {"Yes", "No", "Exit ChatRoom"};
                            int reply = JOptionPane.showOptionDialog(
                                    ChatCreator.frame,
                                    String.format(
                                    "Are you sure you want to leave %s?",
                                    ChatCreator.tabbedPane.getTitleAt(index)),
                                    "Confirmation", JOptionPane.YES_NO_CANCEL_OPTION,
                                    JOptionPane.QUESTION_MESSAGE, null,
                                    options, options[0]);
                            if (reply == JOptionPane.YES_OPTION) {
                                ChatCreator.indices.get(index).doClick();
                                o.println(Messages.getQuitMessage(chatRoom));
                            } else if (reply == JOptionPane.CANCEL_OPTION) {
                                ArrayList<String> roomArray = new ArrayList<>();
                                for (String cName : ChatCreator.chatNames) {
                                    roomArray.add(cName);
                                }
                                /*
                                for (String cName : roomArray) {
                                    room.speedyDelete = true;
                                    room.getCloseButton().doClick();
                                }
                                        */
                                System.exit(0);
                            }
                        }
                    }
                }

                // Varför lyssna på closeButton här? Obs! Anonyma lyssnare stängs ej av på slutet! Vilken lyssnare?
                // Obs! Två olika klasser med samma namn - vilken avses??  Båda används - måste fixas (Om du syftar på SendFileButtonListener
                SendButtonListener sendButtonListener = new SendButtonListener();
                chatRoom.getSendButton().addActionListener(sendButtonListener);
                SendFileButtonListener2 sendFileButtonListener = new SendFileButtonListener2();
                chatRoom.getSendFileButton().addActionListener(sendFileButtonListener);
                chatRoom.getCloseButton().addActionListener(new CloseButtonListener());
                chatRoom.getKeyRequestButton().addActionListener(new KeyRequestButtonListener());

                while ((responseLine = i.readLine()) != null
                        && !responseLine.matches(String.format("<message sender=(.*)%s got the boot(.*)</message>",
                        chatRoom.getName())) && !NotAllowedToConnect(responseLine)) {
                    System.out.println(responseLine);
                    handleUserConnect(responseLine);
                    setKeys(responseLine);
                    keyRequest(responseLine);
                    keyResponse(responseLine);
                    fileRequest(responseLine);
                    fileResponse(responseLine);
                    registerChatName(responseLine);
                    addUsers(responseLine);
                    chatRoom.appendToPane(responseLine);
                    if (responseLine.contains("*** Bye")) {
                        break;
                    }
                }

                // send to others instead!?
                chatRoom.appendToPane(String.format("<message sender=\"INFO\">"
                        + "<text color=\"0000ff\">The server has been "
                        + "abandoned!</text><disconnect /></message>"));
                chatRoom.getSendButton().setEnabled(false);
                chatRoom.getSendButton().removeActionListener(sendButtonListener);
                chatRoom.getSendFileButton().removeActionListener(sendFileButtonListener);
                i.close();
                o.close();
                clientSocket.close();
            } catch (IOException e) {
                ChatCreator.showError("Failed to close connection.");
            }
        }
    }
    
    private boolean NotAllowedToConnect(String responseLine) {
        boolean simpleMatch = responseLine.matches(String.format("<message sender=(.*)>"
                + "<text color=(.*)><request reply=\"no\">%s was not allowed to connect!</request></text></message>",
                 chatRoom.getName()));
        boolean match = responseLine.matches(String.format("<message sender=(.*)>"
                + "<text color=(.*)>%s was not allowed to connect!</text></message>",
                 chatRoom.getName()));
        return (simpleMatch || match);
        
    }
    private void addUsers(String responseLine) {
        String[] users = XMLString.getUsers(responseLine);
        if (users != null) {
            for (String usr : users) {
                if (!chatRoom.getItems().contains(usr)) {
                    chatRoom.getItems().addElement(usr);
                }
            }
        }
    }
    
    private void registerChatName(String responseLine) {
        String sender = XMLString.getSenderWithoutColon(responseLine);
        if (!chatRoom.getItems().contains(sender)) {
            chatRoom.getItems().addElement(sender);
        }
    }

    // Start timer when we send a keyRequest
    private void startKeyTimer(final String chatName) {
        //ChatRoom chat = (ChatRoom) chatRoom.getList().getSelectedValue();
        //final String chatName = chat.getName();

        chatRoom.nameKeyResponse.put(chatName,
                Executors.newSingleThreadScheduledExecutor());

        Runnable task = new Runnable() {

            @Override
            public void run() {
                // Check if recived keyresponse - if not, inform the user, else 
                // do nothing

                // No response
                if (!chatRoom.recivedKeyResponse.containsKey(chatName)) {
                    o.println(String.format("<message sender=\"%s\"><text color"
                            + "=\"%s\">I got no key from %s after one minute."
                            + "</text></message>", chatRoom.getName(),
                            chatRoom.color, chatName));
                } else if (!chatRoom.recivedKeyResponse.get(chatName)) {
                    //No keyresponse
                    o.println(String.format("<message sender=\"%s\"><text color"
                            + "=\"%s\">I got no key from %s after one minute."
                            + "</text></message>", chatRoom.getName(),
                            chatRoom.color, chatName));
                }
                chatRoom.recivedKeyResponse.put(chatName, false); // Start over
                chatRoom.nameKeyResponse.get(chatName).shutdown();
            }
        };
        chatRoom.nameKeyResponse.get(chatName).schedule(task, 10,
                TimeUnit.SECONDS);
    }

    // Start timer when we send file
    private void startTimer(final String chatName) {


        chatRoom.nameFileResponse.put(chatName,
                Executors.newSingleThreadScheduledExecutor());

        Runnable task = new Runnable() {

            @Override
            public void run() {
                //Check if recived fileresponse - if not, inform the user, else do nothing

                //No response
                if (!chatRoom.recivedFileResponse.containsKey(chatName)) {
                    o.println(String.format("<message sender=\"%s\"><text color"
                            + "=\"%s\">I got no fileresponse after one minute. "
                            + "It's not a virus, I promise!"
                            + "</text></message>", chatRoom.getName(),
                            chatRoom.color));
                } else if (!chatRoom.recivedFileResponse.get(chatName)) {
                    //No fileresponse
                    o.println(String.format("<message sender=\"%s\"><text color"
                            + "=\"%s\">I got no fileresponse after one minute. "
                            + "It's not a virus, I promise!"
                            + "</text></message>", chatRoom.getName(),
                            chatRoom.color));
                } else {
                    System.out.println(chatRoom.recivedFileResponse.get(chatName));
                }
                chatRoom.recivedFileResponse.put(chatName, false);  // Start over
                chatRoom.nameFileResponse.get(chatName).shutdown();
            }
        };
        //Run after 1 minute
        chatRoom.nameFileResponse.get(chatName).schedule(task, 60, TimeUnit.SECONDS);
    }

    // Determine if user allowed to connect
    private void handleUserConnect(String html) {
        if (chatRoom.isServer) {
            String sender = XMLString.getSenderWithoutColon(html);
            // Alla får skriva minst en mening i chatten
            if (!chatRoom.isAllowedToConnect.containsKey(sender) && !sender.equals(chatRoom.getName())) {
                if (html.matches("<message sender=(.*)>(.*)<request>(.*)</request>(.*)</message>")) {
                    dialogMessage(sender, false);
                } else {
                    // Simpel klient
                    dialogMessage(sender, true);
                }
            }
        }
    }

    private void dialogMessage(String sender, boolean isSimple) {
        int reply = JOptionPane.showConfirmDialog(ChatCreator.frame,
                String.format("%s wants to connect. Allow?",
                sender),
                "Kill", JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.NO_OPTION) {
            if (!isSimple) {
                o.println(String.format("<message sender=\"%s\">"
                        + "<text color=\"%s\"><request reply=\"no\">%s was not allowed to connect!</request></text></message>",
                        chatRoom.getNamePane().getText(), chatRoom.color, sender));
            } else {
                o.println(String.format("<message sender=\"%s\">"
                        + "<text color=\"%s\">%s was not allowed to connect!</text></message>",
                        chatRoom.getNamePane().getText(), chatRoom.color, sender));
            }
            chatRoom.isAllowedToConnect.put(sender, false);
        } else {
            String users = chatRoom.getItems().toString();
            o.println(String.format("<message sender=\"%s\">"
                        + "<connected users=\""+users+"\"></connected><text color=\"%s\">Welcome %s!</text></message>",
                        chatRoom.getNamePane().getText(), chatRoom.color, sender));
            chatRoom.isAllowedToConnect.put(sender, true);
        }


    }

    // Checks if we have recived a fileresponse
    private void fileResponse(String html) {
        if (html.contains("</fileresponse>")) {
            ChatRoom chat = (ChatRoom) chatRoom.getList().getSelectedValue();
            if (chat != null) {
                String name = chat.getName();
                if (!chatRoom.nameFileResponse.get(name).isShutdown()) {
                    chatRoom.recivedFileResponse.put(name, true);
                }
            }
        }
    }

    // Checks if we have recived a keyresponse
    private void keyResponse(String html) {
        if (html.matches("<message sender=(.*)>(.*)<encrypted type=(.*) "
                + "key=(.*)>(.*)</encrypted>(.*)</message>")) {
            String chatName = (String) chatRoom.getList().getSelectedValue();  //Problem if change selected value
            if (chatName != null) {
                if (!chatRoom.nameKeyResponse.get(chatName).isShutdown()) {
                    chatRoom.recivedKeyResponse.put(chatName, true);
                }
            }
        }
    }

    // Checks if we have recived a keyrequest
    private void keyRequest(String html) {
        String chatName = chatRoom.getName();
        String name = XMLString.getSenderWithoutColon(html);
        if (html.contains("</keyrequest>") && !name.equals(chatName)) {
            int reply = JOptionPane.showConfirmDialog(ChatCreator.frame,
                    String.format("%s sends a keyrequest of type %s.\n Send key?",
                    XMLString.getSenderWithoutColon(html), XMLString.getKeyRequestType(html)),
                    "Kill", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                o.println(String.format("<message sender=\"%s\">"
                        + "<text color=\"%s\">Här kommer nyckeln!<encrypted "
                        + "type=\"%s\" key=\"%s\"></encrypted></text></message>",
                        chatName, chatRoom.color,
                        XMLString.getKeyRequestType(html),
                        chatRoom.getKey(XMLString.getKeyRequestType(html))));
            }
        }
    }

    // Checks if we have recived a filerequest
    private void fileRequest(String html) {
        String chatName = chatRoom.getName();

        if (html.contains("</filerequest>")) {
            String name = XMLString.getSender(html).substring(0, chatName.length());
            if (!name.equals(chatName)) {
                int reply = JOptionPane.showConfirmDialog(ChatCreator.frame,
                        String.format("%s sends a filerequest of type %s."
                        + "\n Receive file?",
                        XMLString.getSender(html), XMLString.getKeyRequestType(html)),
                        "Kill", JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION) {
                    o.println(String.format("<message sender=\"%s\">"
                            + "<text color=\"%s\"><fileresponse reply=\"yes\" "
                            + "port=\"" + (port + 13)
                            + "\">%s</filerespnse></text></message>",
                            chatName, chatRoom.color,
                            chatRoom.getMessagePane().getText()));
                } else {
                    o.println(String.format("<message sender=\"%s\">"
                            + "<text color=\"%s\"><fileresponse reply=\"no\" "
                            + "port=\"" + (port + 13)
                            + "\">%s</fileresponse></text></message>",
                            chatRoom.getNamePane().getText(), chatRoom.color,
                            chatRoom.getMessagePane().getText()));
                } 
            }

        }
    }

    // Obtain and save the keys from the sender. Might need to change this - 
    // problem if two people have the same name
    private void setKeys(String responseLine) {
        String sender = XMLString.getSenderWithoutColon(responseLine);
        String[] keys = XMLString.getKeys(responseLine);
        String[] oldKeys = chatRoom.nameToKey.get(sender);
        if (oldKeys != null) {
            if (keys[0].equals("")) {
                keys[0] = oldKeys[0];
            }
            if (keys[1].equals("")) {
                keys[1] = oldKeys[1];
            }
        }
        chatRoom.nameToKey.put(sender, keys);
    }
}