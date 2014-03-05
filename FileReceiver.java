package chatbox;

import java.io.*;
import java.net.*;

public class FileReceiver extends Thread {

    private FileOutputStream out;
    private InputStream in;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private int n;
    private int port;
    private String file;

    public FileReceiver(int port, String file) {
        this.port = port + 13;
        this.file = file;
    }

    public void run() {

        // Setup server socket:
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.printf("Could not listen on port %d: " + e, port);
            System.exit(1);
        }

        // Listen for client connection:
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.printf("Accept failed on port %d: " + e, port);
            System.exit(1);
        }

        System.out.printf("Accept successful on port %d\n", port);

        // Setup input stream from socket
        try {
            in = clientSocket.getInputStream();
        } catch (IOException e) {
            System.err.println("getInputStream failed: " + e);
            System.exit(1);
        }

        System.out.println("getInputStream successful\n");

        // Setup output stream for file
        try {
            out = new FileOutputStream(new File(file)); // creates file even if out is empty!
        } catch (IOException e) {
            System.err.println("File outputstream failed: " + e);
            System.exit(1);
        }

        try {
            while ((n = in.read()) >= 0) { // read one byte
                out.write((byte) n); // write one byte
            }
        } catch (IOException e) {
            System.err.println("Receive file failed: " + e);
            System.exit(1);
        }

        System.out.println("Finished writing file");

        // Clean up
        try {
            in.close();
            out.close();
            // clientSocket.close();
        } catch (IOException e) {
            System.err.println("Closing streams failed: " + e);
            System.exit(1);
        }
    }
}
