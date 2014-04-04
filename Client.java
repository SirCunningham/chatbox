package chatbox;

import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

// add messageBox.items.addElement(clientSocket.getInetAddress()); when
// *new user is connected (certain msg comes)
// *user changes name
// Gör detta här eller i IOThread!
public class Client implements Runnable {

    private final Socket clientSocket;
    private final BufferedReader i;
    private final PrintWriter o;
    private final int port;
    private final MessageBox messageBox;

    public Client(Socket clientSocket, BufferedReader i, PrintWriter o,
            int port, final MessageBox messageBox) {
        this.clientSocket = clientSocket;
        this.i = i;
        this.o = o;
        this.port = port;
        this.messageBox = messageBox;
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
                        String msg = messageBox.getMessage();
                        if (!msg.equals("")) {
                            o.println(msg);
                        }

                    }
                }
                class SendFileButtonListener implements ActionListener {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        o.println(messageBox.getFileMessage());
                    }
                }
                // Stäng av hela programmet
                class CloseButtonListener implements ActionListener {
                    // stäng av alla tabbar, gör detta i varje tabb!
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int reply = JOptionPane.showConfirmDialog(messageBox.view.frame,
                                "Are you sure you want to quit?", "Confirmation",
                                JOptionPane.YES_NO_OPTION);
                        if (reply == JOptionPane.YES_OPTION) {
                            o.println(messageBox.getQuitMessage());
                            System.exit(0);
                        } else {
                            JOptionPane.showMessageDialog(messageBox.view.frame,
                                    "Good choice. Everyone's finger can slip!");
                        }
                    }
                }
                SendButtonListener sendButtonListener = new SendButtonListener();
                messageBox.sendButton.addActionListener(sendButtonListener);
                messageBox.sendFileButton.addActionListener(new SendFileButtonListener());
                messageBox.closeButton.addActionListener(new CloseButtonListener());
                while ((responseLine = i.readLine()) != null && messageBox.alive) {
                    keyRequest(responseLine);
                    messageBox.appendToPane(
                            XMLString.removeKeyRequest(XMLString.removeFileRequest(responseLine)));  //Skicka inte key- eller filerequest till sig själv!
                    if (responseLine.indexOf("*** Bye") != -1) {                                          
                        break;
                    }
                }
                // send to others instead!?
                
                messageBox.appendToPane(String.format("<message sender=\"INFO\">"
                        + "<text color=\"0000ff\">%s har loggat ut!<disconnect /></text></message>", messageBox.namePane.getText()));
                messageBox.sendButton.setEnabled(false);
                messageBox.sendButton.removeActionListener(sendButtonListener);
                i.close();
                o.close();
                clientSocket.close();
            } catch (IOException e) {
                messageBox.showError("Failed to close connection.");
            }
        }
    }

    public void keyRequest(String html) {
        if (html.indexOf("</keyrequest>") != -1) {
            int reply = JOptionPane.showConfirmDialog(null,
                    String.format("%s sends a keyrequest of type %s.\n Send key?",
                    XMLString.getSender(html), XMLString.getKeyRequestType(html)),
                    "Kill", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                o.println(String.format("<message sender=\"%s\">"
                        + "<text color=\"%s\">Här kommer nyckeln!<encrypted key=\"%s\" type=\"%s\"></encrypted></text></message>",
                        messageBox.namePane.getText(), messageBox.color,
                        messageBox.getKey(XMLString.getKeyRequestType(html)),
                        XMLString.getKeyRequestType(html)));
            }
        }
    }

    public void fileRequest(String html) {
        if (html.indexOf("</filrequest>") != -1) {
            int reply = JOptionPane.showConfirmDialog(null, String.format("%s sends a filerequest of type %s.\n Receive file?",
                    XMLString.getSender(html), XMLString.getKeyRequestType(html)),
                    "Kill", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                o.println(String.format("<message sender=\"%s\">"
                        + "<text color=\"%s\"><fileresponse reply=\"yes\" port=\"" + (port + 13) + "\">%s</filerespnse></text></message>",
                        messageBox.namePane.getText(), messageBox.color, messageBox.messagePane.getText()));
            } else {
                o.println(String.format("<message sender=\"%s\">"
                        + "<text color=\"%s\"><fileresponse reply=\"no\" port=\"" + (port + 13) + "\">%s</fileresponse></text></message>",
                        messageBox.namePane.getText(), messageBox.color,messageBox.messagePane.getText()));
            }
        }
    }
}