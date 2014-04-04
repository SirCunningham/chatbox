package chatbox;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class Server implements Runnable {

    private final ServerSocket serverSocket;
    private final int port;
    private final MessageBox messageBox;
    ArrayList<MessageBox> messageBoxes = new ArrayList<>();
    private final Object lock = new Object();
    private Socket clientSocket;
    private LinkedList<IOThread> threads;

    public Server(ServerSocket serverSocket, int port,
            final MessageBox messageBox) {
        this.serverSocket = serverSocket;
        this.port = port;
        this.messageBox = messageBox;
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
                    //messageBox.items.addElement(clientSocket.toString());
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
                messageBox.showError("Could not close server.");
            }
        }
    }
    
    public ArrayList<MessageBox> getMessageBoxes() {
        return messageBoxes;
    }
    public void addUser(String user) {
        for (MessageBox msgBox : messageBoxes) {
            if (!msgBox.items.contains(user)) {
                msgBox.items.addElement(user);
            }
        }
    }
}