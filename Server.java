package chatbox;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class Server implements Runnable {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private LinkedList<IOThread> threads;
    private final Object lock = new Object();
    private int port;
    private MessageBox messageBox;
    
    // lägg till frame för meddelandena!!!
    public Server(int port, MessageBox messageBox) {
        this.port = port;
        this.messageBox = messageBox;
    }
    
    // Lyssna efter klienter
    public void run() {
        
        // Starta socket för servern
        try {
            serverSocket = new ServerSocket(port);
            // add messageBox.bootPanel.setVisible(true); to some premier client!!!
        } catch (IOException e) {
            messageBox.success = false;
            JOptionPane.showMessageDialog(null, String.format("Couldn't listen "
                    + "on port %d.", port), "Error message",
                    JOptionPane.ERROR_MESSAGE);
        }

        // Skapa tråd för varje klient
        threads = new LinkedList<>();
        if (serverSocket != null) {
            while (true) {
                // gör det möjligt att avsluta när tabben stängs ned i controller/messageBox!!!
                if (5 < 4) {
                    break;
                }
                try {
                    clientSocket = serverSocket.accept();
                    // add messageBox.items.addElement(clientSocket.getInetAddress()); later!!!
                    synchronized (lock) {
                        threads.addLast(new IOThread(clientSocket, threads, lock));
                        threads.getLast().start();
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, String.format("Accept failed "),
                            "Error message", JOptionPane.ERROR_MESSAGE);
                }
            }
            try {
                serverSocket.close();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Could not close server", "Error message",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}