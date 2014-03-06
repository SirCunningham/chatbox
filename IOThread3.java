package chatbox;

import java.io.*;
import java.net.Socket;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;

class IOThread3 extends Thread {

    private String clientName;
    private BufferedReader is;
    private PrintWriter os;
    private Socket clientSocket;
    private final IOThread3[] threads;
    private int maxClientsCount;

    public IOThread3(Socket clientSocket, IOThread3[] threads) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;
    }

    public void run() {
        int maxClientsCount1 = this.maxClientsCount;
        IOThread3[] threads1 = this.threads;

        try {
            // Skapa input- och outputströmmar
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            os = new PrintWriter(clientSocket.getOutputStream(),true);
            String name;
            while (true) {
                os.println("Enter your name.");
                name = is.readLine();
                // Integrera med GUI!!!
                if (name.isEmpty()) {
                    break;
                } else {
                    os.println("The name should not be empty.");
                }
            }

            // Ge välkomstmeddelande
            os.println("Welcome " + name
                    + " to our chat room.\nTo leave enter /quit in a new line.");
            synchronized (this) {
                for (int i = 0; i < maxClientsCount1; i++) {
                    if (threads1[i] != null && threads1[i] == this) {
                        clientName = "@" + name;
                        break;
                    }
                }
                for (int i = 0; i < maxClientsCount1; i++) {
                    if (threads1[i] != null && threads1[i] != this) {
                        threads1[i].os.println("*** A new user " + name
                                + " entered the chat room !!! ***");
                    }
                }
            }

            // Starta konversationen
            while (true) {
                String line = is.readLine();
                if (line.startsWith("/quit")) {
                    break;
                }

                // Skicka privata meddelanden
                if (line.startsWith("@")) {
                    String[] words = line.split("\\s", 2);
                    if (words.length > 1 && words[1] != null) {
                        words[1] = words[1].trim();
                        if (!words[1].isEmpty()) {
                            synchronized (this) {
                                for (int i = 0; i < maxClientsCount1; i++) {
                                    if (threads1[i] != null && threads1[i] != this
                                            && threads1[i].clientName != null
                                            && threads1[i].clientName.equals(words[0])) {
                                        threads1[i].os.println("<" + name + "> " + words[1]);

                                        // Visa att meddelandet har skickats
                                        this.os.println(">" + name + "> " + words[1]);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Skicka publika meddelanden
                    synchronized (this) {
                        for (int i = 0; i < maxClientsCount1; i++) {
                            if (threads1[i] != null && threads1[i].clientName != null) {
                                threads1[i].os.println("<" + name + "> " + line);
                            }
                        }
                    }
                }
            }
            synchronized (this) {
                for (int i = 0; i < maxClientsCount1; i++) {
                    if (threads1[i] != null && threads1[i] != this
                            && threads1[i].clientName != null) {
                        threads1[i].os.println("*** The user " + name
                                + " is leaving the chat room !!! ***");
                    }
                }
            }
            os.println("*** Bye " + name + " ***");

            // Lämna plats för nya klienter
            synchronized (this) {
                for (int i = 0; i < maxClientsCount1; i++) {
                    if (threads1[i] == this) {
                        threads1[i] = null;
                    }
                }
            }
            is.close();
            os.close();
            clientSocket.close();
        } catch (IOException e) {
        }
    }
}