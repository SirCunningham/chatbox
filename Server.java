package chatbox;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server implements Runnable {

    private final ServerSocket serverSocket;
    private final int port;
    private final ChatRoom chatRoom;
    private final Object lock = new Object();
    private Socket clientSocket;
    private LinkedList<IOThread> threads;

    public Server(ServerSocket serverSocket, int port,
            final ChatRoom chatRoom) {
        this.serverSocket = serverSocket;
        this.port = port;
        this.chatRoom = chatRoom;
    }

    @Override
    public void run() {
        if (serverSocket != null) {
            threads = new LinkedList<>();
            // Lyssna efter klienter
            while (chatRoom.alive) {
                try {
                    clientSocket = serverSocket.accept();
                    // Skapa tråd för varje klient
                    synchronized (lock) {
                        threads.addLast(new IOThread(clientSocket, threads, lock));
                        threads.getLast().start();
                    }
                } catch (SocketTimeoutException e) {
                } catch (IOException e) {
                    chatRoom.showError(String.format("Accept failed on port %d.",
                            port));
                }
            }
            try {
                serverSocket.close();
            } catch (IOException e) {
                chatRoom.showError("Failed to close server.");
            }
        }
    }
}