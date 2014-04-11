package chatbox;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server implements Runnable {

    private final ServerSocket serverSocket;
    private final int port;
    private final ChatRoom chatRoom;
    private ArrayList<ChatRoom> connectedChatRooms;
    private final Object lock = new Object();
    private final Object lock2 = new Object();
    private Socket clientSocket;
    private LinkedList<IOThread> threads;

    public Server(ServerSocket serverSocket, int port,
            final ChatRoom chatRoom) {
        this.serverSocket = serverSocket;
        this.port = port;
        this.chatRoom = chatRoom;
        connectedChatRooms = new ArrayList<>();
        connectedChatRooms.add(chatRoom);
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
    public void addChatRoom(ChatRoom chatRoom) {
        connectedChatRooms.add(chatRoom);
    }
    public ArrayList<ChatRoom> getChatRooms() {
        return connectedChatRooms;
    }
    public void addUser(ChatRoom chatRoom) {
        synchronized (lock2) {
            for (ChatRoom msgBox : connectedChatRooms) {
                if (!chatRoom.items.contains(msgBox)) {
                    chatRoom.items.addElement(msgBox);
                }
            }
        }

    }
}