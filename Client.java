package chatbox;

import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class Client implements Runnable {

    private MessageBox messageBox;
    private String text;
    private Socket clientSocket;
    private PrintStream os;
    private BufferedReader is;
    private boolean closed = false;

    public Client(String host, int portNumber, MessageBox messageBox) {
        this.messageBox = messageBox;
        this.messageBox.sendButton.addActionListener(new SendMsgButtonListener());

        // Starta socket för klienten
        try {
            clientSocket = new Socket(host, portNumber);
            os = new PrintStream(clientSocket.getOutputStream());
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(null, "Don't know about host.",
                    "Error message", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Couldn't get I/O for the connection "
                    + "to host.", "Error message", JOptionPane.ERROR_MESSAGE);
        }

        // Kommunicera med servern
        if (clientSocket != null && os != null && is != null) {
            try {
                // Skapa tråd för att läsa från servern
                new myThread(new Client(host, portNumber, this.messageBox)).start();
                // Skicka data till servern
                while (!closed) {
                    //Do something useful or sleep?
                }
                os.close();
                is.close();
                clientSocket.close();
            } catch (IOException e) {
                // Fixa felmeddelanden!!!
                System.err.println("IOException:  " + e);
            }
        }
    }

    // Skapa tråd för att läsa från servern
    public void run() {

        // Håll uppkopplingen tills servern vill avbryta den
        String responseLine;
        try {
            while ((responseLine = is.readLine()) != null) {
                System.out.println(responseLine);
                if (responseLine.indexOf("*** Bye") != -1) {
                    break;
                }
            }
            closed = true;
        } catch (IOException e) {
            // Fixa felmeddelanden!!!
            System.err.println("IOException:  " + e);
        }
    }
    
    public class SendMsgButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            
            os.println(messageBox.messagePane.getText());
            messageBox.messagePane.setText("");

        }
    }
}