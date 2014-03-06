package chatbox;

import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class Client implements Runnable {

    private BufferedReader i;
    private PrintWriter o;
    private BufferedReader in;
    private PrintWriter out;
    private Socket clientSocket;
    private boolean closed = false;

    public Client(String host, int portNumber, BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;

        // Starta socket för klienten
        try {
            clientSocket = new Socket(host, portNumber);
            i = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            o = new PrintWriter(clientSocket.getOutputStream());
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(null, "Don't know about host.",
                    "Error message", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Couldn't get I/O for the connection "
                    + "to host.", "Error message", JOptionPane.ERROR_MESSAGE);
        }

        // Kommunicera med servern
        if (clientSocket != null && i != null && o != null) {
            try {
                // Skapa tråd för att läsa från servern
                new Thread(new Client(host, portNumber, in, out)).start();

                // Skicka data till servern
                while (!closed) {
                    o.println(in.readLine());
                }
                i.close();
                o.close();
                clientSocket.close();
            } catch (IOException e) {
                // fixa felmeddelanden!!!
                System.err.println("IOException:  " + e);
            }
        }
    }

    // Skapa tråd för att läsa från servern
    public void run() {

        // Håll uppkopplingen tills servern vill avbryta den
        String responseLine;
        try {
            while ((responseLine = i.readLine()) != null) {
                out.println(responseLine);
                if (responseLine.indexOf("*** Bye") != -1) {
                    break;
                }
            }
            closed = true;
        } catch (IOException e) {
            // fixa felmeddelanden!!!
            System.err.println("IOException:  " + e);
        }
    }
}