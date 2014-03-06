package chatbox;

import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class Client implements Runnable {

    private BufferedReader i;
    private PrintWriter o;
    private MessageBox messageBox;
    private String host;
    private int port;
    private Socket clientSocket;

    public Client(String host, int port, final MessageBox messageBox) {
        this.host = host;
        this.port = port;
        this.messageBox = messageBox;
    }

    // Skapa tråd för att läsa från servern
    public void run() {

        // Starta socket för klienten
        try {
            clientSocket = new Socket(host, port);
            i = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            o = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (UnknownHostException e) {
            messageBox.success = false;
            JOptionPane.showMessageDialog(null, "Don't know about host.",
                    "Error message", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            messageBox.success = false;
            JOptionPane.showMessageDialog(null,
                    "Couldn't get I/O for the connection "
                    + "to host.", "Error message", JOptionPane.ERROR_MESSAGE);
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
                    public void actionPerformed(ActionEvent e) {
                        o.println(messageBox.getFileMessage());
                    }
                }
                SendButtonListener sendButtonListener = new SendButtonListener();
                messageBox.sendButton.addActionListener(sendButtonListener);
                messageBox.sendFileButton.addActionListener(new SendFileButtonListener());
                while ((responseLine = i.readLine()) != null) {
                    messageBox.appendToPane(responseLine);
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
                // fixa felmeddelanden!!!
                System.err.println("IOException:  " + e);
            }
        }
    }
}