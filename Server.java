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
    private final int port;
    private final MessageBox messageBox;
    private final JFrame frame;

    public Server(int port, MessageBox messageBox, JFrame frame) {
        this.port = port;
        this.messageBox = messageBox;
        this.frame = frame;
    }

    // Lyssna efter klienter
    @Override
    public void run() {

        // Starta socket för servern
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            messageBox.success = false;
            JOptionPane.showMessageDialog(frame, String.format("Could not listen "
                    + "on port %d.", port), "Error message",
                    JOptionPane.ERROR_MESSAGE);
        }

        if (serverSocket != null) {
            // Skapa tråd för varje klient
            threads = new LinkedList<>();
            while (true) {
                // gör det möjligt att avsluta när tabben stängs ned i controller/messageBox!!!
                if (5 < 4) {
                    break;
                }
                try {
                    clientSocket = serverSocket.accept();
                    synchronized (lock) {
                        threads.addLast(new IOThread(clientSocket, threads, lock));
                        threads.getLast().start();
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(frame, String.format("Accept failed "
                            + "on port %d.", port), "Error message",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            try {
                serverSocket.close();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Could not close server.", "Error message",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}