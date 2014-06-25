package chatbox;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;

// add chatRoom.items.addElement(clientSocket.getInetAddress()); when
// *new user is connected (certain msg comes)
// *user changes name
// Gör detta här eller i IOStream!
//OBS OBS OBS
//All data går igenom IOStream, alltid
//Gör krypteringen i IOStream så sker det både för meddelanden och filer automatiskt
//Filer använder en annan klient för gränssnittet är annorlunda där
//OBS OBS OBS
public class Client implements Runnable {

    private Socket clientSocket;
    protected BufferedReader i;
    protected PrintWriter o;
    private final int port;
    private final ChatRoom chatRoom;

    public Client(String host, int port, final ChatRoom chatRoom) {
        this.port = port;
        this.chatRoom = chatRoom;
        // Starta socket för klienten
        try {
            clientSocket = new Socket(host, port);
            i = new BufferedReader(new InputStreamReader(
                    clientSocket.getInputStream()));
            o = new PrintWriter(clientSocket.getOutputStream(), true);
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
                class SendButtonListener implements ActionListener {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String msg = Messages.getMessage(chatRoom);
                        if (!msg.equals("")) {
                            o.println(String.format("<message sender=\"%s\">"
                                    + "<text color=\"%s\">%s</text></message>",
                                    chatRoom.getName(), chatRoom.color, msg));
                        }
                    }
                }

                // Listener for keyrequest
                class KeyRequestButtonListener implements ActionListener {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //detta skickar bara till en person? loop behövs annars...
                        //vet inte vad du menar. o.println skickar till alla?
                        //man bryr sig endast om nycklen från den man markerat,
                        //men skickar keyrequest till alla
                        String chatName = (String) chatRoom.getList().getSelectedValue();
                        if (chatName != null) {
                            String message = (String) JOptionPane.showInputDialog(ChatCreator.frame, "Enter message:",
                                    "Send keyrequest", JOptionPane.INFORMATION_MESSAGE, null, null,
                                    "I request a key for " + String.valueOf(chatRoom.getKeyRequestEncryption()) + " from " + chatName + "!");
                            if (message != null) {

                                o.println(String.format("<message sender=\"%s\">"
                                        + "<text color=\"%s\"><keyrequest "
                                        + "type=\"%s\">%s"
                                        + "</keyrequest></text></message>",
                                        chatRoom.getName(), chatRoom.color,
                                        String.valueOf(chatRoom.getKeyRequestEncryption()), message));
                                startKeyTimer(chatName);
                            }
                        }
                    }
                }
                // finns redan i ChatRoom, onödig dubblering, ta bort där?
                class SendFileButtonListener2 implements ActionListener {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //Send filerequest to every person in the chat??
                        String chatName = (String) chatRoom.getList().getSelectedValue();
                        if (chatName != null) {
                            o.println(Messages.getFileMessage(chatRoom));
                            startTimer(chatName);
                        }

                    }
                }

                // Stäng av hela programmet
                class CloseButtonListener implements ActionListener {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JButton button = (JButton) e.getSource();
                        int index = ChatCreator.tabbedPane.indexOfComponent(
                                button.getParent().getParent().getParent());
                        if (chatRoom.speedyDelete) {
                            ChatCreator.indices.get(index).doClick();
                            o.println(Messages.getQuitMessage(chatRoom));
                        } else {
                            Object[] options = {"Yes", "No", "Exit ChatRoom"};
                            int reply = JOptionPane.showOptionDialog(
                                    ChatCreator.frame,
                                    String.format(
                                            "Are you sure you want to leave %s?",
                                            ChatCreator.tabbedPane.getTitleAt(index)),
                                    "Confirmation", JOptionPane.YES_NO_CANCEL_OPTION,
                                    JOptionPane.QUESTION_MESSAGE, null,
                                    options, options[0]);
                            if (reply == JOptionPane.YES_OPTION) {
                                ChatCreator.indices.get(index).doClick();
                                o.println(Messages.getQuitMessage(chatRoom));
                            } else if (reply == JOptionPane.CANCEL_OPTION) {
                                ArrayList<String> roomArray = new ArrayList<>();
                                for (String cName : ChatCreator.chatNames) {
                                    roomArray.add(cName);
                                }
                                /*
                                 for (String cName : roomArray) {
                                 room.speedyDelete = true;
                                 room.getCloseButton().doClick();
                                 }
                                 */
                                System.exit(0);
                            }
                        }
                    }
                }

                // Varför lyssna på closeButton här? Obs! Anonyma lyssnare stängs ej av på slutet! Vilken lyssnare?
                // Obs! Två olika klasser med samma namn - vilken avses??  Båda används - måste fixas (Om du syftar på SendFileButtonListener
                SendButtonListener sendButtonListener = new SendButtonListener();
                chatRoom.getSendButton().addActionListener(sendButtonListener);
                SendFileButtonListener2 sendFileButtonListener = new SendFileButtonListener2();
                chatRoom.getSendFileButton().addActionListener(sendFileButtonListener);
                chatRoom.getCloseButton().addActionListener(new CloseButtonListener());
                chatRoom.getKeyRequestButton().addActionListener(new KeyRequestButtonListener());

                while ((responseLine = i.readLine()) != null
                        && !responseLine.matches(String.format("<message sender=(.*)%s got the boot(.*)</message>",
                                        chatRoom.getName())) && !NotAllowedToConnect(responseLine)) {
                    System.out.println(responseLine);
                    getServerName(responseLine);
                    handleBootedUser(responseLine);
                    handleChangedName(responseLine);
                    handleUserConnect(responseLine);
                    setKeys(responseLine);
                    keyRequest(responseLine);
                    keyResponse(responseLine);
                    fileRequest(responseLine);
                    fileResponse(responseLine);
                    registerChatName(responseLine);
                    addUsers(responseLine);
                    chatRoom.appendToPane(responseLine);
                    if (responseLine.contains("*** Bye")) {
                        break;
                    }
                }

                // send to others instead!?
                chatRoom.appendToPane(String.format("<message sender=\"INFO\">"
                        + "<text color=\"0000ff\">The server has been "
                        + "abandoned!</text><disconnect /></message>"));
                chatRoom.getSendButton().setEnabled(false);
                chatRoom.getSendButton().removeActionListener(sendButtonListener);
                chatRoom.getSendFileButton().removeActionListener(sendFileButtonListener);
                i.close();
                o.close();
                clientSocket.close();
            } catch (IOException e) {
                ChatCreator.showError("Failed to close connection.");
            }
        }
    }

    //REMEMBER HANDLE SERVER CHANGE NAME
    private void getServerName(String responeLine) {
        if (chatRoom.serverName == null && responeLine.matches(String.format("<message sender=\"(.*)\">(.*)Welcome %s!(.*)</message>",
                chatRoom.getNamePane().getText()))) {
            chatRoom.serverName = XMLString.getSenderWithoutColon(responeLine);
        }
    }

    private void handleBootedUser(String responseLine) {
        String bootedUser = null;
        if (chatRoom.serverName != null) {
            bootedUser = XMLString.getBootedUser(responseLine, chatRoom.serverName);
            System.out.println("Booted User:" + bootedUser);
        }
        if (bootedUser != null) {
            chatRoom.getItems().removeElement(bootedUser);
        }
        
    }

    private boolean NotAllowedToConnect(String responseLine) {
        boolean simpleMatch = responseLine.matches(String.format("<message sender=(.*)>"
                + "<text color=(.*)><request reply=\"no\">%s was not allowed to connect!</request></text></message>",
                chatRoom.getName()));
        boolean match = responseLine.matches(String.format("<message sender=(.*)>"
                + "<text color=(.*)>%s was not allowed to connect!</text></message>",
                chatRoom.getName()));
        return (simpleMatch || match);

    }

    private void addUsers(String responseLine) {
        String[] users = XMLString.getUsers(responseLine);
        if (users != null && !users[0].equals("")) {
            chatRoom.getList().removeAll();
            for (String usr : users) {
                if (!chatRoom.getItems().contains(usr) && !chatRoom.getName().equals(usr)) {
                    chatRoom.getItems().addElement(usr);
                }
            }
        }
    }

    private void registerChatName(String responseLine) {
        String sender = XMLString.getSenderWithoutColon(responseLine);
        if (!chatRoom.getItems().contains(sender) && !sender.equals(chatRoom.getName())) {
            chatRoom.getItems().addElement(sender);
        }
    }

    // Start timer when we send a keyRequest
    private void startKeyTimer(final String chatName) {
        //ChatRoom chat = (ChatRoom) chatRoom.getList().getSelectedValue();
        //final String chatName = chat.getName();

        chatRoom.nameKeyResponse.put(chatName,
                Executors.newSingleThreadScheduledExecutor());

        Runnable task = new Runnable() {

            @Override
            public void run() {
                // Check if recived keyresponse - if not, inform the user, else 
                // do nothing

                // No response
                if (!chatRoom.recivedKeyResponse.containsKey(chatName)) {
                    o.println(String.format("<message sender=\"%s\"><text color"
                            + "=\"%s\">I got no key from %s after one minute."
                            + "</text></message>", chatRoom.getName(),
                            chatRoom.color, chatName));
                } else if (!chatRoom.recivedKeyResponse.get(chatName)) {
                    //No keyresponse
                    o.println(String.format("<message sender=\"%s\"><text color"
                            + "=\"%s\">I got no key from %s after one minute."
                            + "</text></message>", chatRoom.getName(),
                            chatRoom.color, chatName));
                }
                chatRoom.recivedKeyResponse.put(chatName, false); // Start over
                chatRoom.nameKeyResponse.get(chatName).shutdown();
            }
        };
        chatRoom.nameKeyResponse.get(chatName).schedule(task, 10,
                TimeUnit.SECONDS);
    }

    // Start timer when we send file
    private void startTimer(final String chatName) {

        chatRoom.nameFileResponse.put(chatName,
                Executors.newSingleThreadScheduledExecutor());

        Runnable task = new Runnable() {

            @Override
            public void run() {
                //Check if recived fileresponse - if not, inform the user, else do nothing

                //No response
                if (!chatRoom.recivedFileResponse.containsKey(chatName)) {
                    o.println(String.format("<message sender=\"%s\"><text color"
                            + "=\"%s\">I got no fileresponse after one minute. "
                            + "It's not a virus, I promise!"
                            + "</text></message>", chatRoom.getName(),
                            chatRoom.color));
                } else if (!chatRoom.recivedFileResponse.get(chatName)) {
                    //No fileresponse
                    o.println(String.format("<message sender=\"%s\"><text color"
                            + "=\"%s\">I got no fileresponse after one minute. "
                            + "It's not a virus, I promise!"
                            + "</text></message>", chatRoom.getName(),
                            chatRoom.color));
                } else {
                    System.out.println(chatRoom.recivedFileResponse.get(chatName));
                }
                chatRoom.recivedFileResponse.put(chatName, false);  // Start over
                chatRoom.nameFileResponse.get(chatName).shutdown();
            }
        };
        //Run after 1 minute
        chatRoom.nameFileResponse.get(chatName).schedule(task, 60, TimeUnit.SECONDS);
    }

    private void handleChangedName(String responseLine) {
        if (responseLine.matches(String.format("<message sender=(.*)I just switched from my old name:(.*)</message>"))) {
            String newName = XMLString.getSenderWithoutColon(responseLine);
            String oldName = XMLString.getOldName(responseLine);
            chatRoom.getItems().removeElement(oldName);
            chatRoom.getItems().addElement(newName);
            if (chatRoom.isServer) {
                chatRoom.isAllowedToConnect.remove(oldName);
                chatRoom.isAllowedToConnect.put(newName, true);
            }

        }
    }
    // Determine if user allowed to connect
    private void handleUserConnect(String html) {
        if (chatRoom.isServer) {
            String sender = XMLString.getSenderWithoutColon(html);
            // Alla får skriva minst en mening i chatten
            //System.out.println(XMLString.getSenderWithoutColon(html));
            //System.out.println(chatRoom.isAllowedToConnect.containsKey(XMLString.getSenderWithoutColon(html)));
            if (!chatRoom.isAllowedToConnect.containsKey(sender) && !sender.equals(chatRoom.getName())) {
                if (html.matches("<message sender=(.*)>(.*)<request>(.*)</request>(.*)</message>")) {
                    dialogMessage(sender, false);
                } else {
                    // Simpel klient
                    dialogMessage(sender, true);
                }
            }
        }
    }

    private void dialogMessage(String sender, boolean isSimple) {
        if (!sender.equals("")) {
            int reply = JOptionPane.showConfirmDialog(ChatCreator.frame,
                    String.format("%s wants to connect. Allow?",
                            sender),
                    "Kill", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.NO_OPTION) {
                if (!isSimple) {
                    o.println(String.format("<message sender=\"%s\">"
                            + "<text color=\"%s\"><request reply=\"no\">%s was not allowed to connect!</request></text></message>",
                            chatRoom.getNamePane().getText(), chatRoom.color, sender));
                } else {
                    o.println(String.format("<message sender=\"%s\">"
                            + "<text color=\"%s\">%s was not allowed to connect!</text></message>",
                            chatRoom.getNamePane().getText(), chatRoom.color, sender));
                }
                chatRoom.isAllowedToConnect.put(sender, false);
            } else {
                String users = chatRoom.getItems().toString();
                System.out.println("Test: " + users);
                o.println(String.format("<message sender=\"%s\">"
                        + "<connected users=\"" + users + "\"></connected><text color=\"%s\">Welcome %s!</text></message>",
                        chatRoom.getNamePane().getText(), chatRoom.color, sender));
                chatRoom.isAllowedToConnect.put(sender, true);
            }
        }

    }

    // Checks if we have recived a fileresponse
    private void fileResponse(String html) {
        if (html.contains("</fileresponse>")) {
            ChatRoom chat = (ChatRoom) chatRoom.getList().getSelectedValue();
            if (chat != null) {
                String name = chat.getName();
                if (!chatRoom.nameFileResponse.get(name).isShutdown()) {
                    chatRoom.recivedFileResponse.put(name, true);
                }
            }
        }
    }

    // Checks if we have recived a keyresponse
    private void keyResponse(String html) {
        if (html.matches("<message sender=(.*)>(.*)<encrypted type=(.*) "
                + "key=(.*)>(.*)</encrypted>(.*)</message>")) {
            String chatName = (String) chatRoom.getList().getSelectedValue();  //Problem if change selected value
            if (chatName != null) {
                if (!chatRoom.nameKeyResponse.get(chatName).isShutdown()) {
                    chatRoom.recivedKeyResponse.put(chatName, true);
                }
            }
        }
    }

    // Checks if we have recived a keyrequest
    private void keyRequest(String html) {
        String chatName = chatRoom.getName();
        String name = XMLString.getSenderWithoutColon(html);
        if (html.contains("</keyrequest>") && !name.equals(chatName)) {
            int reply = JOptionPane.showConfirmDialog(ChatCreator.frame,
                    String.format("%s sends a keyrequest of type %s.\n Send key?",
                            XMLString.getSenderWithoutColon(html), XMLString.getKeyRequestType(html)),
                    "Kill", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                o.println(String.format("<message sender=\"%s\">"
                        + "<text color=\"%s\">Här kommer nyckeln!<encrypted "
                        + "type=\"%s\" key=\"%s\"></encrypted></text></message>",
                        chatName, chatRoom.color,
                        XMLString.getKeyRequestType(html),
                        chatRoom.getKey(XMLString.getKeyRequestType(html))));
            }
        }
    }

    // Checks if we have recived a filerequest
    private void fileRequest(final String html) {
        final String chatName = chatRoom.getName();
        final String name = XMLString.getSender(html).substring(0, chatName.length());
        if (html.contains("</filerequest>") && !name.equals(chatName)) {
            System.out.println("XXX" + html);
            final JProgressBar progressBar;
            final JPanel invisibleContainer;
            final JLabel label;
            final JOptionPane optionPane;
            final JDialog dialog;
            final SwingWorker worker;

            progressBar = new JProgressBar(0, 100);
            progressBar.setValue(0);
            progressBar.setStringPainted(true);
            progressBar.setVisible(false);

            invisibleContainer = new JPanel(new GridLayout(2, 1));
            label = new JLabel("Downloading...");
            label.setBackground(invisibleContainer.getBackground());
            label.setVisible(false);
            invisibleContainer.add(label, BorderLayout.PAGE_START);
            invisibleContainer.add(progressBar, BorderLayout.CENTER);

            String description = chatRoom.getDescriptionPane().getText();
            if (description.equals("File description (optional)")) {
                description = "No description";
            }
            String fileData = String.format("%s sends a filerequest.\n\n"
                    + "File name: %s\nFile size: %s\nFile description: %s\nAccept file?",
                    XMLString.getSenderWithoutColon(html), XMLString.getFileName(html),
                    XMLString.getFileSize(html), XMLString.getFileDescription(html));

            optionPane = new JOptionPane(fileData, JOptionPane.QUESTION_MESSAGE,
                    JOptionPane.YES_NO_OPTION);
            dialog = new JDialog(ChatCreator.frame, "File request", false);
            worker = new SwingWorker<Object, Object>() {

                @Override
                protected Object doInBackground() throws Exception {
                    int progress = 0;
                    setProgress(progress);
                    while (progress < 100) {
                        try {
                            Thread.sleep(ChatCreator.generator.nextInt(100));
                        } catch (InterruptedException e) {
                        }
                        progress += ChatCreator.generator.nextInt(10);
                        setProgress(Math.min(progress, 100));
                    }
                    return null;
                }

                @Override
                protected void done() {
                    dialog.setCursor(null);
                    dialog.dispose();
                }
            };
            optionPane.addPropertyChangeListener(
                    new PropertyChangeListener() {

                        @Override
                        public void propertyChange(PropertyChangeEvent e) {
                            String name = e.getPropertyName();
                            if (e.getSource() == optionPane
                            && name.equals(JOptionPane.VALUE_PROPERTY)) {
                                int reply = (int) optionPane.getValue();
                                if (reply == JOptionPane.YES_OPTION) {
                                    progressBar.setVisible(true);
                                    label.setVisible(true);
                                    dialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                                    worker.execute();

                                    if (reply == JOptionPane.YES_OPTION) {
                                        JFileChooser chooser = new JFileChooser();
                                        //getFileName returnerar null!!
                                        chooser.setSelectedFile(new File(XMLString.getFileName(html)));
                                        int returnVal = chooser.showSaveDialog(ChatCreator.frame);
                                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                                            chatRoom.savePath = chooser.getSelectedFile();
                                            try {
                                                FileWriter fw = new FileWriter(chatRoom.savePath);
                                                chatRoom.bw = new BufferedWriter(fw);
                                            } catch (IOException ex) {
                                                ChatCreator.showError("Failed to save file.");
                                            }
                                            o.println(String.format("<message sender=\"%s\">"
                                                            + "<text color=\"%s\"><fileresponse reply=\"yes\" "
                                                            + "port=\"" + (port + 13)
                                                            + "\">%s</filerespnse></text></message>",
                                                            chatName, chatRoom.color,
                                                            chatRoom.getMessagePane().getText()));
                                            return;
                                        }
                                        o.println(String.format("<message sender=\"%s\">"
                                                        + "<text color=\"%s\"><fileresponse reply=\"no\" "
                                                        + "port=\"" + (port + 13)
                                                        + "\">%s</fileresponse></text></message>",
                                                        chatRoom.getNamePane().getText(), chatRoom.color,
                                                        chatRoom.getMessagePane().getText()));
                                    }
                                } else {
                                    dialog.dispose();
                                }
                            }
                        }
                    });

            dialog.setContentPane(optionPane);
            dialog.getContentPane().add(invisibleContainer);
            dialog.pack();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setLocationRelativeTo(ChatCreator.frame);
            dialog.setResizable(false);
            dialog.setAlwaysOnTop(false);
            dialog.setVisible(true);

            worker.addPropertyChangeListener(new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent e) {
                    String name = e.getPropertyName();
                    if (name.equals("progress")) {
                        SwingWorker worker1 = (SwingWorker) e.getSource();
                        int progress = worker1.getProgress();
                        if (progress == 0) {
                            progressBar.setIndeterminate(true);
                        } else {
                            progressBar.setIndeterminate(false);
                            progressBar.setValue(progress);
                        }
                    }
                }
            });
        }
    }

    // Obtain and save the keys from the sender. Might need to change this - 
    // problem if two people have the same name
    private void setKeys(String responseLine) {
        String sender = XMLString.getSenderWithoutColon(responseLine);
        String[] keys = XMLString.getKeys(responseLine);
        String[] oldKeys = chatRoom.nameToKey.get(sender);
        if (oldKeys != null) {
            if (keys[0].equals("")) {
                keys[0] = oldKeys[0];
            }
            if (keys[1].equals("")) {
                keys[1] = oldKeys[1];
            }
        }
        chatRoom.nameToKey.put(sender, keys);
    }
}
