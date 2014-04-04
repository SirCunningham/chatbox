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
                class closeButtonListener implements ActionListener {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        o.println(messageBox.getQuitMessage());
                    }
                }
                SendButtonListener sendButtonListener = new SendButtonListener();
                messageBox.sendButton.addActionListener(sendButtonListener);
                messageBox.sendFileButton.addActionListener(new SendFileButtonListener());
                messageBox.closeButton.addActionListener(new closeButtonListener());
                while ((responseLine = i.readLine()) != null) {
                    keyRequest(responseLine);
                    messageBox.appendToPane(
                            XMLString.removeKeyRequest(XMLString.removeFileRequest(responseLine)));  //Skicka inte key- eller filerequest till sig själv!
                    if (responseLine.indexOf("*** Bye") != -1) {                                          
                        break;
                    }
                }
                messageBox.sendButton.setEnabled(false);
                messageBox.sendButton.removeActionListener(sendButtonListener);
                i.close();
                o.close();
                clientSocket.close();
            } catch (IOException e) {
                messageBox.error("Couldn't get I/O for closing the streams.");
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