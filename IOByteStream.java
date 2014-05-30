package chatbox;

import java.io.*;
import java.net.Socket;
import java.util.*;

class IOByteStream extends IOStream {

    public IOByteStream(Socket clientSocket, LinkedList<IOStream> threads,
            Object lock) {
        super(clientSocket, threads, lock);
    }

    @Override
    public void run() {
        try {
            // Skapa input- och outputströmmar
            bi = clientSocket.getInputStream();
            bo = clientSocket.getOutputStream();

            /*
            String name;
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
            for (IOByteStream thread : threads) {
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
                int bytesRead = bi.read();
                if (bytesRead < 0) {
                    break;
                }

                // Skicka privata meddelanden
                //if (line.startsWith("@")) {
                    /*
                    String[] words = line.split("\\s", 2);
                    if (words.length > 1 && words[1] != null) {
                    words[1] = words[1].trim();
                    if (!words[1].isEmpty()) {
                    synchronized (lock) {
                    for (IOByteStream thread : threads) {
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
                //} else {
                    // Skicka publika meddelanden
                    synchronized (lock) {
                        for (IOStream thread : threads) {
                            if (thread != this) {
                                thread.bo.write(bytesRead);
                            }
                        }
                    }
                //}
            }
            synchronized (lock) {
                for (IOStream thread : threads) {
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

            bi.close();
            bo.close();
            clientSocket.close();
        } catch (IOException e) {
        }
    }
}