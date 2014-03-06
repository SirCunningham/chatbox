package chatbox;

import java.io.*;
import java.net.Socket;
import java.util.*;

class IOThread extends Thread {

    private String clientName = "@NN";
    private BufferedReader is;
    private PrintStream os;
    private Socket clientSocket;
    private LinkedList<IOThread> threads;

    public IOThread(Socket clientSocket, LinkedList<IOThread> threads) {
        this.clientSocket = clientSocket;
        this.threads = threads;
    }

    public void run() {

        try {
            // Skapa input- och outputströmmar
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            os = new PrintStream(clientSocket.getOutputStream());
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
                for (IOThread thread : threads) {
                    if (thread == this) {
                        clientName = "@" + name;
                        break;
                    } else {
                        thread.os.println("*** A new user " + name
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
                                for (IOThread thread : threads) {
                                    if (thread != this && thread.clientName.equals(words[0])) {
                                        thread.os.println("<" + name + "> " + words[1]);

                                        // Visa att meddelandet har skickats
                                        this.os.println("<" + name + "> " + words[1]);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Skicka publika meddelanden
                    synchronized (this) {
                        for (IOThread thread : threads) {
                            thread.os.println("<" + name + "> " + line);
                        }
                    }
                }
            }
            synchronized (this) {
                for (IOThread thread : threads) {
                    if (thread != this) {
                        thread.os.println("*** The user " + name
                                + " is leaving the chat room !!! ***");
                    }
                }
            }
            os.println("*** Bye " + name + " ***");
            
            // Lämna plats för nya klienter
            synchronized (this) {
                threads.remove(this);
            }
            is.close();
            os.close();
            clientSocket.close();
        } catch (IOException e) {
        }
    }
}