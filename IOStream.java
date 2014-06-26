package chatbox;

import java.io.*;
import java.net.Socket;
import java.util.*;

class IOStream extends Thread {

    protected final String clientName = "NN";
    private String chatName = "";
    private BufferedReader i;
    private PrintWriter o;
    protected final Socket clientSocket;
    protected final LinkedList<IOStream> streams;
    protected final Object lock;
    private final ChatRoom chatRoom;
    protected InputStream bi;
    protected OutputStream bo;
    boolean alive = true;

    public IOStream(Socket clientSocket, LinkedList<IOStream> streams,
            Object lock, ChatRoom chatRoom) {
        this.clientSocket = clientSocket;
        this.streams = streams;
        this.lock = lock;
        this.chatRoom = chatRoom;
    }

    @Override
    public void run() {
        try {
            // Skapa input- och outputströmmar
            i = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            o = new PrintWriter(clientSocket.getOutputStream(), true);

            // Ge välkomstmeddelande, flyttat till annan del av programmet
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
            while (!clientSocket.isClosed()) {
                String line = i.readLine();
                chatName = XMLString.getSenderWithoutColon(line);
                if (line == null) {
                    break;
                }

                // Skicka privata meddelanden         
                String msg = XMLString.getMessage(line);
                if (msg != null && msg.startsWith("@")) {
                    List<String> names = Arrays.asList(chatRoom.getItems().toString().split("\\s*(,|[|])\\s*"));
                    int index = msg.indexOf("\\S", 1);
                    String[] words = msg.split("\\s+", index);
                    if (words.length > 1) {
                        for (String word : words) {
                            if (names.contains(word)) {
                                synchronized (lock) {
                                    for (IOStream stream : streams) {
                                        //clientName är fel!
                                        if (stream != this && stream.clientName.equals(word)) {
                                            stream.o.println(line);

                                            // Visa att meddelandet har skickats
                                            this.o.println(line);
                                            break;
                                        }
                                    }
                                }
                            } else {
                                break;
                            }
                        }
                    }
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
            // Flyttat till annan del av programmet
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
    
    public void kill() {
        alive = false;
    }
    public String getChatName() {
        return chatName;
    }
    
    
    public Socket getClientSocket() {
        return clientSocket;
    }
    public BufferedReader getInputStream() {
        return i;
    }
    public PrintWriter getOutputStream() {
        return o;
    }
    
}
