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
    
    // lägg till frame för meddelandena!!!
    public Server(int port) {

        // Starta socket för servern
        try {
            serverSocket = new ServerSocket(port);
            // add messageBox.bootPanel.setVisible(true); to some premier client!!!
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, String.format("Couldn't listen "
                    + "on port %d.", port), "Error message",
                    JOptionPane.ERROR_MESSAGE);
        }
        
        // Börja lyssna efter klienter
        if (serverSocket != null) {
            new Thread(new Server(port)).start();
        }
        
    }
        
    public void run() {

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