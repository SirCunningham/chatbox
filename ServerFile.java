package chatbox;

import java.net.*;
import java.io.*;

public class ServerFile {

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(15123);
        try (Socket socket = serverSocket.accept()) {
            System.out.println("Accepted connection : " + socket);
            File transferFile = new File("/home/m/u1m0slem/Desktop/form.html");
            byte[] bytearray = new byte[(int) transferFile.length()];
            FileInputStream fin = new FileInputStream(transferFile);
            BufferedInputStream bin = new BufferedInputStream(fin);
            bin.read(bytearray, 0, bytearray.length);
            OutputStream os = socket.getOutputStream();
            System.out.println("Sending Files...");
            os.write(bytearray, 0, bytearray.length);
            os.flush();
        }
        System.out.println("File transfer complete");
    }
}