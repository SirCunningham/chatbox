package chatbox;

import java.net.*;
import java.io.*;

public class ServerFile extends Thread {

    private ServerSocket serverSocket;

    private final int port;
    private final String file;
    
    public ServerFile(int port, String file) {
        if (port < 65523) {
            this.port = port + 13;
        } else {
            this.port = port - 23;
        }
        this.file = "/home/m/u1m0slem/Desktop/form.html";
    }

    @Override
    public void run() {

        // Setup server socket
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.printf("Could not listen on port %d: " + e, port);
            System.exit(1);
        }
        
        // Listen for client connection, bad try-catch structure!
        // See http://mrbool.com/file-transfer-between-2-computers-with-java/24516
        try (Socket clientSocket = serverSocket.accept()) {
            System.out.println("Accepted connection : " + clientSocket);
            File transferFile = new File(file);
            byte[] bytearray = new byte[(int) transferFile.length()];
            FileInputStream fin = new FileInputStream(transferFile);
            BufferedInputStream bin = new BufferedInputStream(fin);
            bin.read(bytearray, 0, bytearray.length);
            OutputStream os = clientSocket.getOutputStream();
            System.out.println("Sending Files...");
            os.write(bytearray, 0, bytearray.length);
            os.flush();
            //bin.close(); ?
            os.close();
            clientSocket.close();
        } catch (FileNotFoundException e) {
            // Flytta dit man väljer fil!
            System.err.println("File outputstream failed: " + e);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("IO failed: " + e);
            System.exit(1);
        }
    }
}