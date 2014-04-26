package chatbox;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server implements Runnable {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private LinkedList<IOThread> threads;
    private final Object lock = new Object();
    private final int port;
    private final ChatRoom chatRoom;

    public Server(int port, final ChatRoom chatRoom) {
        this.port = port;
        this.chatRoom = chatRoom;

        // Starta socket för servern
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(100);
        } catch (IOException e) {
            chatRoom.success = false;
            ChatCreator.showError(String.format("Could not listen on port %d.",
                    port));
        } catch (IllegalArgumentException e) {
            chatRoom.success = false;
            ChatCreator.showError(String.format("Port %d is out of range.", port));
        }
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
                    ChatCreator.showError(String.format("Accept failed on port %d.",
                            port));
                }
            }
            try {
                serverSocket.close();
            } catch (IOException e) {
                ChatCreator.showError("Failed to close server.");
            }
        }
    }
}