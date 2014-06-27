package chatbox;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

// Starta klient eller server
public class StartButtonListener implements ActionListener {
    
    private static final Object lock = new Object();
    private static int tabCount = 1;
    private ChatRoom chatRoom;

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            chatRoom = new ChatRoom();
            if (chatRoom.isServer) {
                final ServerCreator server = new ServerCreator(chatRoom);
                new Thread(server).start();
                chatRoom.appendToPane(String.format("<message sender=\"INFO\">"
                        + "<text color=\"#339966\">Wait for others to connect...</text></message>"));
                chatRoom.getBootButton().setVisible(true);
            }
            if (chatRoom.success) {
                final ClientCreator client = new ClientCreator(chatRoom);
                new Thread(client).start();
            }
            if (chatRoom.success) {
                ChatCreator.chatNames.add(chatRoom.getName());
                //addUser(chatRoom, ChatCreator.chatRooms);
                if (!chatRoom.isServer) {
                    //funkar ej längre med detta!!
                    chatRoom.o = new Client(chatRoom.host, chatRoom.port, chatRoom).o;
                    try {
                        //Vänta på bekräftelse, fixa riktigt villkor
                        chatRoom.o.println(String.format("<message sender=\"%s\"><text color=\"0000ff\"><request>I beg to be connected!!</request></text></message>", chatRoom.getName()));
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        ChatCreator.showError("You failed to be connected.");
                    }
                    chatRoom.appendToPane(String.format("<message sender=\"SUCCESS\">"
                            + "<text color=\"#00ff00\">Connection successful</text></message>"));
                }
                // send this to others!
                //chatRoom.appendToPane(String.format("<message sender=\"SUCCESS\">"
                // - +"<text color=\"#00ff00\"> Connection established with %s </text></message>", clientSocket.getInetAddress()));
                //addUser2(chatRoom);
                int index = ChatCreator.tabbedPane.getTabCount() - 1;
                ChatCreator.tabbedPane.insertTab(ChatCreator.tabPane.getText(),
                        null, chatRoom.getMainPanel(), ChatCreator.tabPane.getText(), index);
                try {
                    ChatCreator.tabbedPane.setTabComponentAt(index, createTabPanel());
                } catch (IOException ex) {
                    ChatCreator.showError("Bilden kunde inte hittas");
                }

                ChatCreator.tabbedPane.setSelectedIndex(index);
                ChatCreator.namePane.setText("User " + ChatCreator.generator.nextInt(1000000000));
                ChatCreator.tabPane.setText("Chat " + String.valueOf(++tabCount));
            }
        } catch (NumberFormatException ex) {
            ChatCreator.showError("Port is not an integer or not sufficiently small.");
        } catch (NullPointerException ex) {
            // This now happens when chatRoom.o is null
            // Error: close button no longer works!
            ChatCreator.showError("You failed to be connected");
        }
    }
    /*
    private void addUser(String chatName, ArrayList<String> chatNames) {
        synchronized (lock) {
            for (ChatRoom name : chatNames) {
                if (!chatName.getItems().contains(room)) {
                    chatRoom.getItems().addElement(room);
                }
            }
        }
    }

    private void addUser2(ChatRoom chatRoom) {
        for (ChatRoom room : ChatCreator.chatRooms) {
            if (!room.getItems().contains(chatRoom)) {
                room.getItems().addElement(chatRoom);
            }
        }
    }
    /*
    private void addUser(ChatRoom chatRoom, ArrayList<ChatRoom> rooms) {
        synchronized (lock) {
            for (ChatRoom room : rooms) {
                if (!chatRoom.getItems().contains(room)) {
                    chatRoom.getItems().addElement(room);
                }
            }
        }
    }

    private void addUser2(ChatRoom chatRoom) {
        for (ChatRoom room : ChatCreator.chatRooms) {
            if (!room.getItems().contains(chatRoom)) {
                room.getItems().addElement(chatRoom);
            }
        }
    }
    */
    
    
    
    private JPanel createTabPanel() throws IOException {
        ImageIcon icon = new ImageIcon(ImageIO.read(new File("closeIcon.png")));
        JPanel tabPanel = new JPanel(new GridBagLayout());
        tabPanel.setOpaque(false);
        JLabel tabLabel = new JLabel(ChatCreator.tabPane.getText() + " ");
        JButton closeButton = new JButton(icon);
        closeButton.setContentAreaFilled(false);
        closeButton.setOpaque(false);
        closeButton.setPreferredSize(new Dimension(12, 12));
        closeButton.addActionListener(new TabButtonListener(chatRoom));
        ChatCreator.indices.add(closeButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        tabPanel.add(tabLabel, gbc);

        gbc.gridx++;
        gbc.weightx = 0;
        tabPanel.add(closeButton, gbc);
        return tabPanel;
    }
}