package chatbox;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server implements Runnable {

    private final ServerSocket serverSocket;
    private final int port;
    private final MessageBox messageBox;
    private final Object lock = new Object();
    private Socket clientSocket;
    private LinkedList<IOThread> threads;

    public Server(ServerSocket serverSocket, int port,
            final MessageBox messageBox) {
        this.serverSocket = serverSocket;
        this.port = port;
        this.messageBox = messageBox;
    }

    @Override
    public void run() {
        if (serverSocket != null) {
            threads = new LinkedList<>();
            // Lyssna efter klienter
            while (messageBox.alive) {
                try {
                    clientSocket = serverSocket.accept();
                    // Skapa tråd för varje klient
                    synchronized (lock) {
                        threads.addLast(new IOThread(clientSocket, threads, lock));
                        threads.getLast().start();
                    }
                } catch (SocketTimeoutException e) {
                } catch (IOException e) {
                    messageBox.showError(String.format("Accept failed on port %d.",
                            port));
                }
            }
            try {
                serverSocket.close();
            } catch (IOException e) {
                messageBox.showError("Failed to close server.");
            }
        }
    }
}