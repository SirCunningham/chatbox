package chatbox;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.util.ArrayList;

public class Controller {

    private final ChatCreator chatCreator;
    private int tabCount = 1;
    private final Object lock = new Object();

    public Controller(ChatCreator chatCreator) {
        this.chatCreator = chatCreator;
        chatCreator.frame.addWindowListener(new ExitListener(chatCreator));
        chatCreator.hostPane.addFocusListener(new TextFieldListener());
        chatCreator.hostPane.addKeyListener(new CreatorListener(chatCreator));
        chatCreator.portPane.addFocusListener(new TextFieldListener());
        chatCreator.portPane.addKeyListener(new CreatorListener(chatCreator));
        chatCreator.passPane.addFocusListener(new TextFieldListener());
        chatCreator.passPane.addKeyListener(new CreatorListener(chatCreator));
        chatCreator.requestPane.addFocusListener(new TextFieldListener());
        chatCreator.requestPane.addKeyListener(new CreatorListener(chatCreator));
        chatCreator.namePane.addFocusListener(new TextFieldListener());
        chatCreator.namePane.addKeyListener(new CreatorListener(chatCreator));
        chatCreator.tabPane.addFocusListener(new TextFieldListener());
        chatCreator.tabPane.addKeyListener(new CreatorListener(chatCreator));
        chatCreator.startButton.addActionListener(new StartButtonListener());
        chatCreator.startButton.addKeyListener(new CreatorListener(chatCreator));
        chatCreator.clientButton.addKeyListener(new CreatorListener(chatCreator));
        chatCreator.serverButton.addItemListener(new ServerButtonListener());
        chatCreator.serverButton.addKeyListener(new CreatorListener(chatCreator));
        chatCreator.serverOptions.addItemListener(new ServerOptionsListener());
        chatCreator.closeButton.addActionListener(new CloseButtonListener());
    }

    public final JPanel createTabPanel() throws IOException {
        ImageIcon icon = new ImageIcon(ImageIO.read(new File("closeIcon.png")));
        JPanel tabPanel = new JPanel(new GridBagLayout());
        tabPanel.setOpaque(false);
        JLabel tabLabel = new JLabel(chatCreator.tabPane.getText() + " ");
        JButton closeButton = new JButton(icon);
        closeButton.setContentAreaFilled(false);
        closeButton.setOpaque(false);
        closeButton.setPreferredSize(new Dimension(12, 12));
        closeButton.addActionListener(new TabButtonListener());
        chatCreator.indices.add(closeButton);

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

    // Starta klient eller server
    public class StartButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            final ChatRoom chatRoom = new ChatRoom(chatCreator);
            try {
                if (chatRoom.isServer) {
                    final Server server = new Server(chatRoom);
                    new Thread(server).start();
                    chatRoom.appendToPane(String.format("<message sender=\"INFO\">"
                            + "<text color=\"#339966\">Wait for others to connect...</text></message>"));
                    chatRoom.bootPanel.setVisible(true);
                }
                if (chatRoom.success) {
                    final Client client = new Client(chatRoom);
                    new Thread(client).start();
                    chatCreator.chatRooms.add(chatRoom);
                    addUser(chatRoom, chatCreator.chatRooms);
                }
            } catch (NumberFormatException ex) {
                chatRoom.success = false;
                chatRoom.showError("Port is not a small number!");
            } finally {
                if (chatRoom.success) {
                    chatRoom.appendToPane(String.format("<message sender=\"SUCCESS\">"
                            + "<text color=\"#00ff00\">Connection successful</text></message>"));
                    // send this to others!
                    //chatRoom.appendToPane(String.format("<message sender=\"SUCCESS\">"
                    // - +"<text color=\"#00ff00\"> Connection established with %s </text></message>", clientSocket.getInetAddress()));
                    addUser2(chatRoom);
                    int index = chatCreator.tabbedPane.getTabCount() - 1;
                    chatCreator.tabbedPane.insertTab(chatCreator.tabPane.getText(),
                            null, chatRoom.mainPanel, chatCreator.tabPane.getText(), index);
                    try {
                        chatCreator.tabbedPane.setTabComponentAt(index, createTabPanel());
                    } catch (IOException ex) {
                        chatRoom.showError("Bilden kunde inte hittas");
                    }

                    chatCreator.tabbedPane.setSelectedIndex(index);
                    chatCreator.namePane.setText("User " + ChatCreator.generator.nextInt(1000000000));
                    chatCreator.tabPane.setText("Chat " + String.valueOf(++tabCount));
                }
            }
        }
    }

    private void addUser(ChatRoom chatRoom, ArrayList<ChatRoom> roomes) {
        synchronized (lock) {
            for (ChatRoom room : roomes) {
                if (!chatRoom.items.contains(room)) {
                    chatRoom.items.addElement(room);
                }
            }
        }
    }

    private void addUser2(ChatRoom chatRoom) {
        for (ChatRoom room : chatCreator.chatRooms) {
            if (!room.items.contains(chatRoom)) {
                room.items.addElement(chatRoom);
            }
        }
    }

    public class ServerButtonListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                chatCreator.startButton.setText("Create server");
                chatCreator.hostLabel.setEnabled(false);
                chatCreator.hostPane.setEnabled(false);
                chatCreator.host = chatCreator.hostPane.getText();
                chatCreator.hostPane.setText("127.0.0.1");
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                chatCreator.startButton.setText("Join server");
                chatCreator.hostLabel.setEnabled(true);
                chatCreator.hostPane.setEnabled(true);
                chatCreator.hostPane.setText(chatCreator.host);
            }
        }
    }

    public class ServerOptionsListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            String chosen = String.valueOf(
                    chatCreator.serverOptions.getSelectedItem());
            if ("Protected".equals(chosen) || "Secret".equals(chosen)) {
                chatCreator.passLabel.setVisible(true);
                chatCreator.passPane.setVisible(true);
            } else {
                chatCreator.passLabel.setVisible(false);
                chatCreator.passPane.setVisible(false);
            }
            if ("Private".equals(chosen) || "Secret".equals(chosen)) {
                chatCreator.requestLabel.setVisible(true);
                chatCreator.requestPane.setVisible(true);
            } else {
                chatCreator.requestLabel.setVisible(false);
                chatCreator.requestPane.setVisible(false);
            }
        }
    }

    public class TabButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            int index = chatCreator.tabbedPane.indexOfTabComponent(button.getParent());
            chatCreator.tabbedPane.remove(index);
            // behövs nytt index fortfarande?
            if ((index = chatCreator.indices.indexOf(button)) != -1) {
                chatCreator.chatRooms.get(index).alive = false;
                for (ChatRoom room : chatCreator.chatRooms) {
                    room.items.removeElement(chatCreator.chatRooms.get(index));
                }
                chatCreator.chatRooms.remove(index);
                chatCreator.indices.remove(index);
            }
        }
    }

    // Stäng av hela programmet
    public class CloseButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ArrayList<ChatRoom> roomArray = new ArrayList<>();
            for (ChatRoom room : chatCreator.chatRooms) {
                roomArray.add(room);
            }
            for (ChatRoom room : roomArray) {
                room.closeButton.doClick();
            }
            if (chatCreator.chatRooms.isEmpty()) {
                int reply = JOptionPane.showConfirmDialog(chatCreator.frame,
                        "Are you sure you want to exit ChatBox?",
                        "Confirmation", JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION) {
                    System.exit(0);
                } else {
                    JOptionPane.showMessageDialog(chatCreator.frame,
                            "Good choice. Everyone's finger can slip!");
                }
            }
        }
    }
}