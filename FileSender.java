package chatbox;

import java.io.*;
import java.net.*;

public class FileSender extends Thread {

    private FileInputStream in;
    private OutputStream out;
    private Socket fileSocket;
    private int inputByte;
    private long fileSize;
    private long sentBytes;
    private File inFile;
    private String IP;
    private int port;
    private String file;

    public FileSender(String IP, int port, String file) {
        this.IP = IP;
        this.port = port + 13;
        this.file = file;
    }

    public void run() {

        try {
            fileSocket = new Socket(IP, port);
            out = fileSocket.getOutputStream();
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host.\n" + e);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                    + "the connection to host.\n" + e);
            System.exit(1);
        }

        System.out.println("Connection successful!\n");

        inFile = new File(file);
        fileSize = inFile.length();

        try {
            in = new FileInputStream(inFile);
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e);
            System.exit(1);
        }

        try {
            while ((inputByte = in.read()) >= 0) {
                out.write((byte) inputByte);
                System.out.println("sending byte " + ++sentBytes
                        + " (of " + fileSize + ")");
            }
        } catch (IOException e) {
            System.err.println("IO error: " + e);
            System.exit(1);
        }

        try {
            out.close();
            in.close();
            fileSocket.close();
        } catch (IOException e) {
            System.err.println("IO error: " + e);
            System.exit(1);
        }
    }
}
