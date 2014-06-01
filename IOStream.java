package chatbox;

import java.io.*;
import java.net.Socket;
import java.util.*;

class IOStream extends Thread {

    protected final String clientName = "@NN";
    private BufferedReader i;
    private PrintWriter o;
    protected final Socket clientSocket;
    protected final LinkedList<IOStream> streams;
    protected final Object lock;
    protected InputStream bi;
    protected OutputStream bo;

    public IOStream(Socket clientSocket, LinkedList<IOStream> streams,
            Object lock) {
        this.clientSocket = clientSocket;
        this.streams = streams;
        this.lock = lock;
    }

    @Override
    public void run() {
        try {
            // Skapa input- och outputströmmar
            i = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            o = new PrintWriter(clientSocket.getOutputStream(), true);

            String name;
            /*
            while (true) {
            name = i.readLine();
            // Integrera med GUI, behövs nog inte här!!!
            if (!name.isEmpty()) {
            break;
            } else {
            }}
             **/

            // Ge välkomstmeddelande
            /*
            o.println("<message sender=system> Welcome " + name
            + " to our chat room.\nTo leave enter /quit in a new line.</message>");
             **/

            /*
            synchronized (lock) {
            for (IOStream stream : streams) {
            if (stream == this) {
            clientName = "@" + name;
            break;
            } else {
            stream.o.println("<message sender=system>*** A new user "
            + " entered the chat room !!! ***</message>");
            }}}
             **/

            while (true) {
                String line = i.readLine();
                System.out.println(line);
                if (line == null) {
                    break;
                }

                // Skicka privata meddelanden
                if (line.startsWith("@")) {
                    /*
                    String[] words = line.split("\\s", 2);
                    if (words.length > 1 && words[1] != null) {
                    words[1] = words[1].trim();
                    if (!words[1].isEmpty()) {
                    synchronized (lock) {
                    
                    for (IOStream stream : streams) {
                    if (stream != this && stream.clientName.equals(words[0])) {
                    stream.o.println("<" + name + "> " + words[1]);
                    
                    // Visa att meddelandet har skickats
                    this.o.println("<" + name + "> " + words[1]);
                    break;}}}}}
                     **/
                } else {
                    // Skicka publika meddelanden
                    synchronized (lock) {
                        for (IOStream stream : streams) {
                            //if (stream != this) {
                            stream.o.println(line);
                            //}
                        }
                    }
                }
            }
            synchronized (lock) {
                for (IOStream stream : streams) {
                    if (stream != this) {
                        //stream.o.println("<message sender=system>*** The user " + name
                        //+ " is leaving the chat room !!! ***</message>");
                    }
                }
            }
            //o.println("<message sender=system>*** Bye " + name + " ***</message>");

            // Lämna plats för nya klienter
            synchronized (lock) {
                streams.remove(this);
            }

            i.close();
            o.close();
            clientSocket.close();
        } catch (IOException e) {
        }
    }
}
