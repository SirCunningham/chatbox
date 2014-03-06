package chatbox;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javax.swing.*;

/**
 * Detta program startar en server som lyssnar på
 * en viss port
 *
 * En ny tråd startas för varje anslutande klient
 * och programmet körs till det stängs av utifrån
 */
public class Server2 implements Runnable {

    private int port;
    private MessageBox messageBox;
    ArrayList<IOThread2> clients;

    public Server2(int port, MessageBox messageBox) {
        this.port = port;
        this.messageBox = messageBox;
        clients = new ArrayList<>();
    }
    public ArrayList<IOThread2> getClients() {
        return clients;
    }

    public void run() {

        // Skapa socket för servern
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            messageBox.bootPanel.setVisible(true);
            // Lyssna efter klienter
            while (true) {
                Socket clientSocket = null;
                // Varje gång en klient ansluter startas en ny tråd av typen
                // 'IOThread', som sedan behandlar resten av kommunikationen
                try {
                    clientSocket = serverSocket.accept();
                    messageBox.items.addElement(clientSocket.getInetAddress());
                    IOThread2 client = new IOThread2(clientSocket, false, messageBox,
                            clientSocket.getInputStream(), clientSocket.getOutputStream());
                    clients.add(client);
                    Thread thr = new Thread(client);
                    thr.start();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null,
                            String.format("Accept failed "
                            + "on port %d.", port), "Error message",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            // serverSocket.close(); if we ever reach this, close with tab?
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, String.format("Couldn't listen "
                    + "on port %d.", port), "Error message",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}