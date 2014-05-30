package chatbox;

import java.awt.event.*;
import java.io.*;
import java.net.*;

public class FileClient implements Runnable {

    private Socket clientSocket;
    protected InputStream i;
    protected OutputStream o;
    private final int port;
    private final ChatRoom chatRoom;
    private final String file;

    public FileClient(String host, int port, final ChatRoom chatRoom) {
        this.port = port;
        this.chatRoom = chatRoom;
        this.file = "/home/m/u1m0slem/Desktop/form.html";
        // Starta socket för klienten
        try {
            clientSocket = new Socket(host, port);
            i = clientSocket.getInputStream();
            o = clientSocket.getOutputStream();
        } catch (UnknownHostException e) {
            chatRoom.success = false;
            ChatCreator.showError("Don't know about host.");
        } catch (IOException e) {
            chatRoom.success = false;
            ChatCreator.showError("Couldn't get I/O for the connection to host.");
        } catch (IllegalArgumentException e) {
            chatRoom.success = false;
            ChatCreator.showError("Port out of range.");
        }
    }

    // Skapa tråd för att läsa från servern
    @Override
    public void run() {
        // Håll uppkopplingen tills servern vill avbryta den
        if (clientSocket != null && i != null && o != null) {
            try {
                // Skapa lyssnare för att skicka till servern
                class SendFileButtonListener3 implements ActionListener {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        FileInputStream fin = null;
                        try {
                            File transferFile = new File(file);
                            byte[] bytearray = new byte[(int) transferFile.length()];
                            fin = new FileInputStream(transferFile);
                            BufferedInputStream bin = new BufferedInputStream(fin);
                            bin.read(bytearray, 0, bytearray.length);
                            o.write(bytearray, 0, bytearray.length);
                            o.flush();
                        } catch (FileNotFoundException ex) {
                            ChatCreator.showError("Failed to find file.");
                        } catch (IOException ex) {
                                ChatCreator.showError("Failed with I/O.");
                        } finally {
                            try {
                                fin.close();
                            } catch (IOException ex) {
                                ChatCreator.showError("Failed to close file stream.");
                            }
                        }
                    }
                }
                
                while (chatRoom.alive) {
                    int filesize = 1022386;
                    int bytesRead;
                    int currentTot = 0;

                    byte[] bytearray = new byte[filesize];
                    FileOutputStream fos = new FileOutputStream("/home/6/u1uk0zn6/Desktop/highscore.html");
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    bytesRead = i.read(bytearray, 0, bytearray.length);
                    currentTot = bytesRead;

                    do {
                        bytesRead = i.read(bytearray, currentTot, (bytearray.length - currentTot));
                        if (bytesRead >= 0) {
                            currentTot += bytesRead;
                        }
                    } while (bytesRead > -1);

                    bos.write(bytearray, 0, currentTot);
                    bos.flush();
                    bos.close();
                }

                SendFileButtonListener3 sendFileButtonListener = new SendFileButtonListener3();
                chatRoom.getSendFileButton().addActionListener(sendFileButtonListener);

                chatRoom.appendToPane(String.format("<message sender=\"INFO\">"
                        + "<text color=\"0000ff\">It is no longer possible "
                        + "to send or receive files!</text><disconnect /></message>"));
                chatRoom.getFileButton().setEnabled(false);
                chatRoom.getSendFileButton().removeActionListener(sendFileButtonListener);
                i.close();
                o.close();
                clientSocket.close();
            } catch (IOException e) {
                ChatCreator.showError("Failed to close connection.");
            }
        }
    }

}