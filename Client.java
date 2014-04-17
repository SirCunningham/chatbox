package chatbox;

import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javax.swing.*;

// add chatRoom.items.addElement(clientSocket.getInetAddress()); when
// *new user is connected (certain msg comes)
// *user changes name
// Gör detta här eller i IOThread!
public class Client implements Runnable {

    private final Socket clientSocket;
    private final BufferedReader i;
    private final PrintWriter o;
    private final int port;
    private final ChatRoom chatRoom;
    private ArrayList<ChatRoom> connectedChatRooms;

    public Client(Socket clientSocket,
            int port, final ChatRoom chatRoom) throws IOException {
        this.clientSocket = clientSocket;
        this.i = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.o = new PrintWriter(clientSocket.getOutputStream(), true);
        this.port = port;
        this.chatRoom = chatRoom;
        connectedChatRooms = new ArrayList<>();
        connectedChatRooms.add(chatRoom);
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
                        String msg = chatRoom.getMessage();
                        if (!msg.equals("")) {
                            o.println(msg);
                        }

                    }
                }
                class SendFileButtonListener implements ActionListener {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        o.println(chatRoom.getFileMessage());
                    }
                }
                // Stäng av hela programmet
                class CloseButtonListener implements ActionListener {
                    // stäng av alla tabbar, gör detta i varje tabb!
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int reply = JOptionPane.showConfirmDialog(chatRoom.chatCreator.frame,
                                "Are you sure you want to quit?", "Confirmation",
                                JOptionPane.YES_NO_OPTION);
                        if (reply == JOptionPane.YES_OPTION) {
                            o.println(chatRoom.getQuitMessage());
                            System.exit(0);
                        } else {
                            JOptionPane.showMessageDialog(chatRoom.chatCreator.frame,
                                    "Good choice. Everyone's finger can slip!");
                        }
                    }
                }
                SendButtonListener sendButtonListener = new SendButtonListener();
                chatRoom.sendButton.addActionListener(sendButtonListener);
                chatRoom.sendFileButton.addActionListener(new SendFileButtonListener());
                chatRoom.closeButton.addActionListener(new CloseButtonListener());
                while ((responseLine = i.readLine()) != null && chatRoom.alive) {
                    keyRequest(responseLine);
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
                chatRoom.showError("Failed to close connection.");
            }
        }
    }

    public void keyRequest(String html) {
        if (html.contains("</keyrequest>")) {
            int reply = JOptionPane.showConfirmDialog(null,
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
            int reply = JOptionPane.showConfirmDialog(null, String.format("%s sends a filerequest of type %s.\n Receive file?",
                    XMLString.getSender(html), XMLString.getKeyRequestType(html)),
                    "Kill", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                o.println(String.format("<message sender=\"%s\">"
                        + "<text color=\"%s\"><fileresponse reply=\"yes\" port=\"" + (port + 13) + "\">%s</filerespnse></text></message>",
                        chatRoom.namePane.getText(), chatRoom.color, chatRoom.messagePane.getText()));
            } else {
                o.println(String.format("<message sender=\"%s\">"
                        + "<text color=\"%s\"><fileresponse reply=\"no\" port=\"" + (port + 13) + "\">%s</fileresponse></text></message>",
                        chatRoom.namePane.getText(), chatRoom.color,chatRoom.messagePane.getText()));
            }
        }
    }
}