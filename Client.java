package chatbox;

import java.io.*;
import java.net.*;
import javax.swing.*;

// Kill client when the server disconnects to avoid duplicates!!
public class Client extends Thread {

    private String IP;
    private int port;
    private View proj;
    private Controller controller;
    private IOThread thr;

    public Client(String IP, int port, View proj) {
        this.IP = IP;
        this.port = port;
        this.proj = proj;
        controller = new Controller(proj);
    }

    @Override
    public void run() {

        boolean successful = false;
        // Skapa socket för klienten
        try {
            Socket clientSocket = new Socket(IP, port);
            thr = new IOThread(clientSocket, proj, true);
            thr.start();
            successful = true;
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(null, "Don't know about host.",
                    "Error message", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Couldn't get I/O for the connection "
                    + "to host.", "Error message", JOptionPane.ERROR_MESSAGE);
        }

        if (!successful) {
            controller.enableConnection();
        }
    }
    public void kill() {
        thr.kill();
    }
}
