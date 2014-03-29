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

    public Server(int port, final MessageBox messageBox, JFrame frame) {
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
            serverSocket.setSoTimeout(100);
        } catch (IOException e) {
            messageBox.success = false;
            JOptionPane.showMessageDialog(frame, String.format("Could not "
                    + "listen on port %d.", port), "Error message",
                    JOptionPane.ERROR_MESSAGE);
        }

        // Skapa tråd för varje klient
        if (serverSocket != null) {
            threads = new LinkedList<>();
            while (messageBox.alive) {
                try {
                    clientSocket = serverSocket.accept();
                    synchronized (lock) {
                        threads.addLast(new IOThread(clientSocket, threads, lock));
                        threads.getLast().start();
                    }
                } catch (SocketTimeoutException e) {
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(frame, String.format("Accept "
                            + "failed on port %d.", port), "Error message",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            try {
                serverSocket.close();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Could not close server.",
                        "Error message", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}