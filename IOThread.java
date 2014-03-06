package chatbox;

import java.io.*;
import java.net.Socket;
import java.util.*;

class IOThread extends Thread {

    private String clientName = "@NN";
    private BufferedReader i;
    private PrintWriter o;
    private Socket clientSocket;
    private LinkedList<IOThread> threads;
    private final Object lock;

    public IOThread(Socket clientSocket, LinkedList<IOThread> threads,
            Object lock) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        this.lock = lock;
    }

    public void run() {

        try {
            // Skapa input- och outputströmmar
            i = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            o = new PrintWriter(clientSocket.getOutputStream(),true);
            String name;
            while (true) {
                o.println("Enter your name.");
                name = i.readLine();
                    // Integrera med GUI, behövs nog inte här!!!
                if (!name.isEmpty()) {
                    break;
                } else {
                    o.println("The name should not be empty.");
                }
            }

            // Ge välkomstmeddelande
            o.println("Welcome " + name
                    + " to our chat room.\nTo leave enter /quit in a new line.");
            synchronized (lock) {
                for (IOThread thread : threads) {
                    if (thread == this) {
                        clientName = "@" + name;
                        break;
                    } else {
                        thread.o.println("*** A new user " + name
                                + " entered the chat room !!! ***");
                    }
                }
            }

            // Starta konversationen
            while (true) {
                String line = i.readLine();
                if (line.startsWith("/quit")) {
                    break;
                }
                
                // Skicka privata meddelanden
                if (line.startsWith("@")) {
                    String[] words = line.split("\\s", 2);
                    if (words.length > 1 && words[1] != null) {
                        words[1] = words[1].trim();
                        if (!words[1].isEmpty()) {
                            synchronized (lock) {
                                for (IOThread thread : threads) {
                                    if (thread != this && thread.clientName.equals(words[0])) {
                                        thread.o.println("<" + name + "> " + words[1]);

                                        // Visa att meddelandet har skickats
                                        this.o.println("<" + name + "> " + words[1]);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Skicka publika meddelanden
                    synchronized (lock) {
                        for (IOThread thread : threads) {
                            thread.o.println("<" + name + "> " + line);
                        }
                    }
                }
            }
            synchronized (lock) {
                for (IOThread thread : threads) {
                    if (thread != this) {
                        thread.o.println("*** The user " + name
                                + " is leaving the chat room !!! ***");
                    }
                }
            }
            o.println("*** Bye " + name + " ***");
            
            // Lämna plats för nya klienter
            synchronized (lock) {
                threads.remove(this);
            }
            i.close();
            o.close();
            clientSocket.close();
        } catch (IOException e) {
        }
    }
}