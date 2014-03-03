package chatbox;

import java.io.*;
import java.net.*;
import javax.swing.*;

// Kill client when the server disconnects to avoid duplicates!!
public class Client implements Runnable {

    private String IP;
    private int port;
    private View view;
    private MessageBox messageBox;

    public Client(String IP, int port, View view, MessageBox messageBox) {
        this.IP = IP;
        this.port = port;
        this.view = view;
        this.messageBox = messageBox;
    }

    @Override
    public void run() {
        // Skapa socket för klienten
        try {
            Socket clientSocket = new Socket(IP, port);
            Thread thr = new Thread(new IOThread(clientSocket, true, view, messageBox));
            thr.start();
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(null, "Don't know about host.",
                    "Error message", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Couldn't get I/O for the connection "
                    + "to host.", "Error message", JOptionPane.ERROR_MESSAGE);
        }
    }
}