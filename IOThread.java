package chatbox;

import java.io.*;
import java.net.Socket;
import java.util.*;

class IOThread extends Thread {

    private final String clientName = "@NN";
    private BufferedReader i;
    private PrintWriter o;
    private final Socket clientSocket;
    private final LinkedList<IOThread> threads;
    private final Object lock;

    public IOThread(Socket clientSocket, LinkedList<IOThread> threads,
            Object lock) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        this.lock = lock;
    }

    @Override
    public void run() {
        try {
            // Skapa input- och outputströmmar
            i = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            o = new PrintWriter(clientSocket.getOutputStream(),true);

            String name;
            /*
            while (true) {
                name = i.readLine();
                    // Integrera med GUI, behövs nog inte här!!!
                if (!name.isEmpty()) {
                    break;
                } else {
                    
                }
            }
             * 
             */

            // Ge välkomstmeddelande
            /*
            o.println("<message sender=system> Welcome " + name
                    + " to our chat room.\nTo leave enter /quit in a new line.</message>");
             * 
             */

            /*
            synchronized (lock) {
                for (IOThread thread : threads) {
                    if (thread == this) {
                        clientName = "@" + name;
                        break;
                    } else {
                        thread.o.println("<message sender=system>*** A new user "
                                + " entered the chat room !!! ***</message>");
                    }
                }
            }
             * 
             */


            while (true) {
                String line = i.readLine();
                System.out.println(line);
                if (line == null) {
                    break;
                }
                
                // Skicka privata meddelanden
                if (line != null && line.startsWith("@")) {
                    /*
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
                     * 
                     */
                } else {
                    // Skicka publika meddelanden
                    synchronized (lock) {
                        for (IOThread thread : threads) {
                            if (thread != this && line != null) {
                                thread.o.println(line);
                                if (!line.equals(XMLString.removeKeyRequest(line))) {  //om line innehåller en keyrequest - Utanför for-loop?
                                    //Skapa timer
                                }
                            } else {
                                if (line!=null) {
                                    thread.o.println(XMLString.removeKeyRequest(XMLString.removeFileRequest(line))); //Skicka inte key- eller filerequest till sig själv
                                }
                                  
                            }
                        }
                    }
                }
            }
            synchronized (lock) {
                for (IOThread thread : threads) {
                    if (thread != this) {
                        //thread.o.println("<message sender=system>*** The user " + name
                                //+ " is leaving the chat room !!! ***</message>");
                    }
                }
            }
            //o.println("<message sender=system>*** Bye " + name + " ***</message>");
            
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