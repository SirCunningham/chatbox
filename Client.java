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

    public Client(String host, int portNumber, final MessageBox messageBox) {
        this.messageBox = messageBox;

        // Starta socket för klienten
        try {
            clientSocket = new Socket(host, portNumber);
            i = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            o = new PrintWriter(clientSocket.getOutputStream());
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(null, "Don't know about host.",
                    "Error message", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Couldn't get I/O for the connection "
                    + "to host.", "Error message", JOptionPane.ERROR_MESSAGE);
        }

        // Kommunicera med servern
        if (clientSocket != null && i != null && o != null) {
            try {
                // Skapa tråd för att läsa från servern
                new Thread(new Client(host, portNumber, messageBox)).start();
                
                // Skapa lyssnare för att skicka till servern
                class SendButtonListener implements ActionListener {

                    @Override
                    public void actionPerformed(ActionEvent e) {
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