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
    private BufferedReader i;
    private PrintWriter o;
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
            if (!chatRoom.isServer) {
                o.println(String.format("<message sender=\"%s\"><text color=\"0000ff\"><request>Jag vill ansluta mig!!!</request></text></message>", chatRoom.getName()));
            }
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
                        o.println(Messages.getFileMessage(chatRoom));
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
                                    room.closeButton.doClick();
                                }
                                System.exit(0);
                            }
                        }
                    }
                }

                SendButtonListener sendButtonListener = new SendButtonListener();
                chatRoom.sendButton.addActionListener(sendButtonListener);
                chatRoom.sendFileButton.addActionListener(new SendFileButtonListener2());
                chatRoom.closeButton.addActionListener(new CloseButtonListener());
                while ((responseLine = i.readLine()) != null && chatRoom.alive) {
                    keyRequest(responseLine);
                    String sender=XMLString.getSender(responseLine);
                    
                    chatRoom.appendToPane(
                            XMLString.removeKeyRequest(XMLString.removeFileRequest(responseLine)));  //Skicka inte key- eller filerequest till sig själv!
                    if (responseLine.contains("*** Bye")) {
                        break;
                    }
                }
                // send to others instead!?

                chatRoom.appendToPane(String.format("<message sender=\"INFO\">"
                        + "<text color=\"0000ff\">%s har loggat ut!<disconnect /></text></message>", chatRoom.namePane.getText()));
                chatRoom.sendButton.setEnabled(false);
                chatRoom.sendButton.removeActionListener(sendButtonListener);
                i.close();
                o.close();
                clientSocket.close();
            } catch (IOException e) {
                ChatCreator.showError("Failed to close connection.");
            }
        }
    }

    public void keyRequest(String html) {
        if (html.contains("</keyrequest>")) {
            int reply = JOptionPane.showConfirmDialog(ChatCreator.frame,
                    String.format("%s sends a keyrequest of type %s.\n Send key?",
                    XMLString.getSender(html), XMLString.getKeyRequestType(html)),
                    "Kill", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                o.println(String.format("<message sender=\"%s\">"
                        + "<text color=\"%s\">Här kommer nyckeln!<encrypted key=\"%s\" type=\"%s\"></encrypted></text></message>",
                        chatRoom.namePane.getText(), chatRoom.color,
                        chatRoom.getKey(XMLString.getKeyRequestType(html)),
                        XMLString.getKeyRequestType(html)));
            }
        }
    }

    public void fileRequest(String html) {
        if (html.contains("</filrequest>")) {
            int reply = JOptionPane.showConfirmDialog(ChatCreator.frame,
                    String.format("%s sends a filerequest of type %s.\n Receive file?",
                    XMLString.getSender(html), XMLString.getKeyRequestType(html)),
                    "Kill", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                o.println(String.format("<message sender=\"%s\">"
                        + "<text color=\"%s\"><fileresponse reply=\"yes\" port=\"" + (port + 13) + "\">%s</filerespnse></text></message>",
                        chatRoom.namePane.getText(), chatRoom.color, chatRoom.messagePane.getText()));
            } else {
                o.println(String.format("<message sender=\"%s\">"
                        + "<text color=\"%s\"><fileresponse reply=\"no\" port=\"" + (port + 13) + "\">%s</fileresponse></text></message>",
                        chatRoom.namePane.getText(), chatRoom.color, chatRoom.messagePane.getText()));
            }
        }
    }
}