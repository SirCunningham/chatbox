package chatbox;

import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// För kryptering av filer, se:
// https://stackoverflow.com/questions/16911632/java-file-encryption
public class FileClient implements Runnable {

    private Socket clientSocket;
    protected BufferedReader i;
    protected OutputStream o;
    private final ChatRoom chatRoom;

    public FileClient(String host, int port, final ChatRoom chatRoom) {
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
    
    // Start timer when we send file
    private void startTimer(final String chatName) {

        chatRoom.nameFileResponse.put(chatName,
                Executors.newSingleThreadScheduledExecutor());

        Runnable task = new Runnable() {

            @Override
            public void run() {
                //Check if received fileresponse - if not, inform the user, else do nothing

                //No response
                if (!chatRoom.receivedFileResponse.containsKey(chatName)) {
                    chatRoom.o.println(String.format("<message sender=\"%s\"><text color"
                            + "=\"%s\">I got no fileresponse after one minute. "
                            + "It's not a virus, I promise!"
                            + "</text></message>", chatRoom.getName(),
                            chatRoom.color));
                    chatRoom.fileAcceptance = ChatRoom.NO_FILE;
                } else if (!chatRoom.receivedFileResponse.get(chatName)) {
                    //No fileresponse
                    chatRoom.o.println(String.format("<message sender=\"%s\"><text color"
                            + "=\"%s\">I got no fileresponse after one minute. "
                            + "It's not a virus, I promise!"
                            + "</text></message>", chatRoom.getName(),
                            chatRoom.color));
                    chatRoom.fileAcceptance = ChatRoom.NO_FILE;
                } else {
                    // Kolla om fileresponse reply="yes" innan något skickas!
                    chatRoom.fileAcceptance = ChatRoom.ACCEPTED_FILE;
                    chatRoom.getSendFileButton().doClick();
                    System.out.println(chatRoom.receivedFileResponse.get(chatName));
                }
                chatRoom.receivedFileResponse.put(chatName, false);  // Start over
                chatRoom.nameFileResponse.get(chatName).shutdown();
            }
        };
        //Run after 1 minute, now 10 seconds because of hack...
        chatRoom.nameFileResponse.get(chatName).schedule(task, 10, TimeUnit.SECONDS);
    }

    // Skapa tråd för att läsa från servern
    @Override
    public void run() {
        // Håll uppkopplingen tills servern vill avbryta den
        String responseLine;
        if (clientSocket != null && i != null && o != null) {
            try {
                // Skapa lyssnare för att skicka filer till servern
                class SendFileButtonListener implements ActionListener {

                    @Override
                    public void actionPerformed(ActionEvent e) {                       
                        // Send filerequest to every selected person in the chat
                        if (chatRoom.fileAcceptance == ChatRoom.NO_FILE) {
                            List<String> names = chatRoom.getList().getSelectedValuesList();
                            chatRoom.o.println(String.format("<message sender=\"%s\"><text color=\"%s\"><fileUsers users=\"%s\">%s</fileUsers></text></message>",
                                    chatRoom.getName(), chatRoom.color, names, "I just sent a filerequest to some of my friends."));
                            
                            String chatName = (String) chatRoom.getList().getSelectedValue();
                            if (chatName != null) {
                                chatRoom.o.println(Messages.getFileMessage(chatRoom));
                                startTimer(chatName);
                            }
                            chatRoom.fileAcceptance = ChatRoom.PROPOSED_FILE;
                        } else if (chatRoom.fileAcceptance == ChatRoom.ACCEPTED_FILE) {
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
                            } finally {
                                chatRoom.fileAcceptance = ChatRoom.NO_FILE;
                            }
                        }
                    }
                }

                SendFileButtonListener sendFileButtonListener = new SendFileButtonListener();
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
