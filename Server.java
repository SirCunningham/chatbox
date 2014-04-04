package chatbox;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class Server implements Runnable {

    private final ServerSocket serverSocket;
    private final int port;
    private final MessageBox messageBox;
    private final JFrame frame;
    private final Object lock = new Object();
    private Socket clientSocket;
    private LinkedList<IOThread> threads;

    public Server(ServerSocket serverSocket, int port,
            final MessageBox messageBox, JFrame frame) {
        this.serverSocket = serverSocket;
        this.port = port;
        this.messageBox = messageBox;
        this.frame = frame;
    }

    // Lyssna efter klienter
    @Override
    public void run() {
        if (serverSocket != null) {
            threads = new LinkedList<>();
            while (messageBox.alive) {
                try {
                    // Skapa tråd för varje klient
                    clientSocket = serverSocket.accept();
                    messageBox.items.addElement(clientSocket.toString());
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