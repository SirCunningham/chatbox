package chatbox;

import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.JFileChooser;

// För kryptering av filer, se:
// https://stackoverflow.com/questions/16911632/java-file-encryption
public class FileClient implements Runnable {

    private Socket clientSocket;
    protected BufferedReader i;
    protected OutputStream o;
    private final int port;
    private final ChatRoom chatRoom;

    public FileClient(String host, int port, final ChatRoom chatRoom) {
        this.port = port;
        this.chatRoom = chatRoom;
        // Starta socket för klienten
        try {
            clientSocket = new Socket(host, port);
            i = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
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
        String responseLine;
        if (clientSocket != null && i != null && o != null) {
            try {
                // Skapa lyssnare för att skicka till servern
                // OBS!!! Vänta på fileresponse="yes" innan något skickas!
                class SendFileButtonListener3 implements ActionListener {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        File transferFile = new File(chatRoom.filePath);
                        byte[] bytearray = new byte[(int) transferFile.length()];
                        try (FileInputStream fin = new FileInputStream(transferFile)) {
                            BufferedInputStream bin = new BufferedInputStream(fin);
                            bin.read(bytearray, 0, bytearray.length);
                            o.write(bytearray, 0, bytearray.length);
                            o.flush();
                            PrintWriter pw = new PrintWriter(o, true);
                            pw.println("THIS IS A REALLY UGLY HACK BUT IT WORKS");
                        } catch (FileNotFoundException ex) {
                            ChatCreator.showError("Failed to find file.");
                        } catch (IOException ex) {
                            ChatCreator.showError("Failed with I/O.");
                        }
                    }
                }

                SendFileButtonListener3 sendFileButtonListener = new SendFileButtonListener3();
                chatRoom.getSendFileButton().addActionListener(sendFileButtonListener);

                //hacklösning
                while ((responseLine = i.readLine()) != null && chatRoom.alive) {
                    try {
                        if (chatRoom.bw != null) {
                            if (responseLine.equals("THIS IS A REALLY UGLY HACK BUT IT WORKS")) {
                                chatRoom.bw.close();
                                chatRoom.bw = null;
                            } else {
                                chatRoom.bw.write(responseLine);
                                chatRoom.bw.newLine();
                                System.out.println(chatRoom.savePath.length());
                            }
                        }
                    } catch (IOException ex) {
                        ChatCreator.showError("Failed to save file.");
                    }
                }

                chatRoom.appendToPane(String.format("<message sender=\"INFO\">"
                        + "<text color=\"0000ff\">It is no longer possible "
                        + "to send or receive files!</text><disconnect /></message>"));
                chatRoom.getFileButton().setEnabled(false);
                chatRoom.getSendFileButton().removeActionListener(sendFileButtonListener);
                i.close();
                o.close();
                clientSocket.close();
            } catch (FileNotFoundException e) {
                //this should not really be here, as the listener should not be fired at start!!
                ChatCreator.showError("Failed to find file.");
                e.printStackTrace();
            } catch (IOException e) {
                ChatCreator.showError("Failed to close file connection.");
            }
        }
    }
}
