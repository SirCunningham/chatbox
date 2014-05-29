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
// Gör detta här eller i IOThread!
public class Client implements Runnable {

    private Socket clientSocket;
    protected BufferedReader i;
    protected PrintWriter o;
    private final int port;
    private final ChatRoom chatRoom;
    private static ScheduledExecutorService worker;   //Timer for keyrequest

    public Client(String host, int port, final ChatRoom chatRoom) {
        this.port = port;
        this.chatRoom = chatRoom;
        worker = Executors.newSingleThreadScheduledExecutor();
        // Starta socket för klienten
        try {
            clientSocket = new Socket(host, port);
            i = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
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
                        String msg = Messages.getMessage(chatRoom);
                        if (!msg.equals("")) {
                            o.println(msg);
                        }

                    }
                }
                // finns redan i ChatRoom, onödig dubblering, ta bort där?
                class SendFileButtonListener2 implements ActionListener {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        o.println(Messages.getFileMessage(chatRoom));            //Send filerequest to every person in the chat??
                        startTimer();
                    }
                }

                // Stäng av hela programmet
                class CloseButtonListener implements ActionListener {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JButton button = (JButton) e.getSource();
                        int index = ChatCreator.tabbedPane.indexOfComponent(button.getParent().getParent().getParent());
                        if (chatRoom.speedyDelete) {
                            ChatCreator.indices.get(index).doClick();
                            o.println(Messages.getQuitMessage(chatRoom));
                        } else {
                            Object[] options = {"Yes", "No", "Exit ChatRoom"};
                            int reply = JOptionPane.showOptionDialog(ChatCreator.frame,
                                    String.format("Are you sure you want to leave %s?",
                                    ChatCreator.tabbedPane.getTitleAt(index)),
                                    "Confirmation", JOptionPane.YES_NO_CANCEL_OPTION,
                                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                            if (reply == JOptionPane.YES_OPTION) {
                                ChatCreator.indices.get(index).doClick();
                                o.println(Messages.getQuitMessage(chatRoom));
                            } else if (reply == JOptionPane.CANCEL_OPTION) {
                                ArrayList<ChatRoom> roomArray = new ArrayList<>();
                                for (ChatRoom room : ChatCreator.chatRooms) {
                                    roomArray.add(room);
                                }
                                for (ChatRoom room : roomArray) {
                                    room.speedyDelete = true;
                                    room.getCloseButton().doClick();
                                }
                                System.exit(0);
                            }
                        }
                    }
                }

                SendButtonListener sendButtonListener = new SendButtonListener();
                chatRoom.getSendButton().addActionListener(sendButtonListener);
                chatRoom.getSendFileButton().addActionListener(new SendFileButtonListener2());
                chatRoom.getCloseButton().addActionListener(new CloseButtonListener());
                while ((responseLine = i.readLine()) != null && chatRoom.alive) {
                    System.out.println(responseLine);
                    setKeys(responseLine);
                    keyRequest(responseLine);
                    fileRequest(responseLine);
                    fileResponse(responseLine);
                    chatRoom.appendToPane(
                            XMLString.removeKeyRequest(XMLString.removeFileRequest(responseLine)));  //Skicka inte key- eller filerequest till sig själv!
                    if (responseLine.contains("*** Bye")) {
                        break;
                    }
                }

                // send to others instead!?
                chatRoom.appendToPane(String.format("<message sender=\"INFO\">"
                        + "<text color=\"0000ff\">The server has been abandoned!</text><disconnect /></message>"));
                chatRoom.getSendButton().setEnabled(false);
                chatRoom.getSendButton().removeActionListener(sendButtonListener);
                i.close();
                o.close();
                clientSocket.close();
            } catch (IOException e) {
                ChatCreator.showError("Failed to close connection.");
            }
        }
    }

    // Start timer when we send file
    private void startTimer() {
        ChatRoom chat = (ChatRoom) chatRoom.getList().getSelectedValue();
        final String chatName = chat.getName();
        System.out.println(chatName);

        chatRoom.ipFileResponse.put(chatName, Executors.newSingleThreadScheduledExecutor());

        Runnable task = new Runnable() {

            @Override
            public void run() {
                //Check if recived fileresponse - if not, inform the user, else do nothing

                //No response
                if (!chatRoom.recivedFileResponse.containsKey((chatName))) {
                    o.println(String.format("<message sender=\"%s\"><text color"
                            + "=\"%s\">I got no fileresponse after one minute. It's not a virus, I promise!"
                            + "</text></message>", chatRoom.getName(), chatRoom.color));
                } else if (!chatRoom.recivedFileResponse.get(chatName)) {
                    //No fileresponse
                    o.println(String.format("<message sender=\"%s\"><text color"
                            + "=\"%s\">I got no fileresponse after one minute. It's not a virus, I promise!"
                            + "</text></message>", chatRoom.getName(), chatRoom.color));
                } else {
                    System.out.println(chatRoom.recivedFileResponse.get(chatName));
                }
                chatRoom.recivedFileResponse.put(chatName, false);  // Start over
                chatRoom.ipFileResponse.get(chatName).shutdown();
            }
        };
        //Run after 1 minute
        chatRoom.ipFileResponse.get(chatName).schedule(task, 60, TimeUnit.SECONDS);

    }

    // Checks if we have recived a fileresponse
    private void fileResponse(String html) {
        if (html.contains("</fileresponse>")) {
            ChatRoom chat = (ChatRoom) chatRoom.getList().getSelectedValue();
            if (chat != null) {
                String name = chat.getName();
                if (!chatRoom.ipFileResponse.get(name).isShutdown()) {
                    chatRoom.recivedFileResponse.put(name, true);
                }
            }

        }
    }

    // Checks if we have recived a keyrequest
    private void keyRequest(String html) {
        if (html.contains("</keyrequest>")) {
            int reply = JOptionPane.showConfirmDialog(ChatCreator.frame,
                    String.format("%s sends a keyrequest of type %s.\n Send key?",
                    XMLString.getSender(html), XMLString.getKeyRequestType(html)),
                    "Kill", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                o.println(String.format("<message sender=\"%s\">"
                        + "<text color=\"%s\">Här kommer nyckeln!<encrypted "
                        + "type=\"%s\" key=\"%s\"></encrypted></text></message>",
                        chatRoom.getNamePane().getText(), chatRoom.color,
                        XMLString.getKeyRequestType(html),
                        chatRoom.getKey(XMLString.getKeyRequestType(html))));
            }
        }
    }

    // Checks if we have recived a filerequest
    private void fileRequest(String html) {
        if (html.contains("</filerequest>")) {
            int reply = JOptionPane.showConfirmDialog(ChatCreator.frame,
                    String.format("%s sends a filerequest of type %s.\n Receive file?",
                    XMLString.getSender(html), XMLString.getKeyRequestType(html)),
                    "Kill", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                o.println(String.format("<message sender=\"%s\">"
                        + "<text color=\"%s\"><fileresponse reply=\"yes\" "
                        + "port=\"" + (port + 13)
                        + "\">%s</filerespnse></text></message>",
                        chatRoom.getNamePane().getText(), chatRoom.color,
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

    // Obtain and save the keys from the sender. Might need to change this - 
    // problem if two people have the same name
    private void setKeys(String responseLine) {
        String sender = XMLString.getSender(responseLine);
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
