package chatbox;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

// Starta klient eller server
public class StartButtonListener implements ActionListener {
    
    private static final Object lock = new Object();
    private static int tabCount = 1;

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            final ChatRoom chatRoom = new ChatRoom();
            if (chatRoom.isServer) {
                final Server server = new ServerAdapter(chatRoom);
                new Thread(server).start();
                chatRoom.appendToPane(String.format("<message sender=\"INFO\">"
                        + "<text color=\"#339966\">Wait for others to connect...</text></message>"));
                chatRoom.getBootButton().setVisible(true);
            }
            if (chatRoom.success) {
                final Client client = new ClientAdapter(chatRoom);
                new Thread(client).start();
            }
            if (chatRoom.success) {
                ChatCreator.chatRooms.add(chatRoom);
                addUser(chatRoom, ChatCreator.chatRooms);
                if (!chatRoom.isServer) {
                    chatRoom.o.println(String.format("<message sender=\"%s\"><text color=\"0000ff\"><request>I beg to be connected!!</request></text></message>", chatRoom.getName()));
                    try {
                        //Vänta på bekräftelse, fixa riktigt villkor
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
                addUser2(chatRoom);
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
        }
    }
    
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
    
    private JPanel createTabPanel() throws IOException {
        ImageIcon icon = new ImageIcon(ImageIO.read(new File("closeIcon.png")));
        JPanel tabPanel = new JPanel(new GridBagLayout());
        tabPanel.setOpaque(false);
        JLabel tabLabel = new JLabel(ChatCreator.tabPane.getText() + " ");
        JButton closeButton = new JButton(icon);
        closeButton.setContentAreaFilled(false);
        closeButton.setOpaque(false);
        closeButton.setPreferredSize(new Dimension(12, 12));
        closeButton.addActionListener(new TabButtonListener());
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