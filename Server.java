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
    private View view;
    private Controller controller;

    public Server(int port, View view) {
        this.port = port;
        this.view = view;
        controller = new Controller(view);
    }

    public void run() {

        // Skapa socket för servern
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            // Lyssna efter klienter
            while (true) {
                Socket clientSocket = null;
                // Varje gång en klient ansluter startas en ny tråd av typen
                // 'IOThread', som sedan behandlar resten av kommunikationen
                try {
                    clientSocket = serverSocket.accept();
                    IOThread thr = new IOThread(clientSocket, view, false);
                    thr.run();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null,
                            String.format("Accept failed "
                            + "on port %d.", port), "Error message",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, String.format("Couldn't listen "
                    + "on port %d.", port), "Error message",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            controller.enableConnection();
        }
    }

}