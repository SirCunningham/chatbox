package chatbox;

import java.io.*;
import java.net.*;
import javax.swing.*;

/**
 * Detta program startar en server som lyssnar på
 * en viss port
 *
 * En ny tråd startas för varje anslutande klient
 * och programmet körs till det stängs av utifrån
 */
public class Server implements Runnable {

    private int port;
    private MessageBox messageBox;

    public Server(int port, MessageBox messageBox) {
        this.port = port;
        this.messageBox = messageBox;
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
                    Thread thr = new Thread(new IOThread(clientSocket, false, messageBox));
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