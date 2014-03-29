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

    private Socket clientSocket;
    private BufferedReader i;
    private PrintWriter o;
    private final String host;
    private final int port;
    private final MessageBox messageBox;
    private final JFrame frame;

    public Client(String host, int port, final MessageBox messageBox,
            JFrame frame) {
        this.host = host;
        this.port = port;
        this.messageBox = messageBox;
        this.frame = frame;
    }

    // Skapa tråd för att läsa från servern
    @Override
    public void run() {

        // Starta socket för klienten
        try {
            clientSocket = new Socket(host, port);
            i = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            o = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (UnknownHostException e) {
            messageBox.success = false;
            JOptionPane.showMessageDialog(frame, "Don't know about host.",
                    "Error message", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            messageBox.success = false;
            JOptionPane.showMessageDialog(frame,
                    "Couldn't get I/O for the connection to host.",
                    "Error message", JOptionPane.ERROR_MESSAGE);
        }

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
                    messageBox.appendToPane(XMLString.removeKeyRequest(responseLine));  //Skicka inte keyrequest till sig själv!
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
                messageBox.success = false;
                JOptionPane.showMessageDialog(frame,
                        "Couldn't get I/O for closing the streams.",
                        "Error message", JOptionPane.ERROR_MESSAGE);
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
            int reply = JOptionPane.showConfirmDialog(null, String.format("%s sends a filerequest of type %s.\n Send key?",
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