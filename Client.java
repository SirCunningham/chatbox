package chatbox;

import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class Client implements Runnable {

    private BufferedReader i;
    private PrintWriter o;
    private MessageBox messageBox;
    private Socket clientSocket;
    private volatile boolean closed = false;

    public Client(String host, int port, final MessageBox messageBox) {
        this.messageBox = messageBox;

        // Starta socket för klienten
        try {
            clientSocket = new Socket(host, port);
            i = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            o = new PrintWriter(clientSocket.getOutputStream());
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
        System.out.println("A0");

        // Kommunicera med servern
        if (clientSocket != null && i != null && o != null) {
            try {
                System.out.println("AA");
                
                // Skapa tråd för att läsa från servern
                new Thread(new Client(host, port, messageBox)).start();
                
                System.out.println("AB");
                
                // Skapa lyssnare för att skicka till servern
                class SendButtonListener implements ActionListener {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("AC");
                        o.println(messageBox.getMessage());
                    }
                }
                SendButtonListener sendButtonListener = new SendButtonListener();
                messageBox.sendButton.addActionListener(sendButtonListener);
                
                // Avsluta uppkopplingen när det blir dags
                while (!closed) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
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

    // Skapa tråd för att läsa från servern
    public void run() {

        // Håll uppkopplingen tills servern vill avbryta den
        String responseLine;
        if (clientSocket != null && i != null && o != null) {
            try {
                while ((responseLine = i.readLine()) != null) {
                    messageBox.appendToPane(responseLine);
                    if (responseLine.indexOf("*** Bye") != -1) {
                        break;
                    }
                }
                closed = true;
            } catch (IOException e) {
                // fixa felmeddelanden!!!
                System.err.println("IOException:  " + e);
            }
        }
    }
}