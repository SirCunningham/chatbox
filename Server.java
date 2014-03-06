package chatbox;

import java.io.*;
import java.net.*;
import javax.swing.*;

public class Server {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private final IOThread[] threads;
    
    // Lägg till frame för meddelandena!!!
    public Server(int portNumber, int maxClientsCount, MessageBox messagebox) {

        // Starta socket för servern
        try {
            serverSocket = new ServerSocket(portNumber);
            // add messageBox.bootPanel.setVisible(true); to some premier client!!!
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, String.format("Couldn't listen "
                    + "on port %d.", portNumber), "Error message",
                    JOptionPane.ERROR_MESSAGE);
        }

        // Skapa tråd för varje klient
        threads = new IOThread[maxClientsCount];
        if (serverSocket != null) {
            while (true) {
                // Gör det möjligt att avsluta när tabben stängs ned!!!
                if (5 < 4) {
                    break;
                }
                try {
                    clientSocket = serverSocket.accept();
                    // add messageBox.items.addElement(clientSocket.getInetAddress()); later!!!
                    int i = 0;
                    for (i = 0; i < maxClientsCount; i++) {
                        if (threads[i] == null) {
                            (threads[i] = new IOThread(clientSocket, threads)).start();
                            break;
                        }
                    }
                    if (i == maxClientsCount) {
                        // HTML-kod!!! Maybe this part can be removed!!!
                        try (PrintStream os = new PrintStream(clientSocket.getOutputStream())) {
                            os.println("Server too busy. Try later.");
                        }
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, String.format("Accept failed "
                            + "on port %d.", portNumber), "Error message",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            try {
                serverSocket.close();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Could not close server", "Error message",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}