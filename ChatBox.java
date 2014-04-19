package chatbox;

//Utsparkning: Skicka <disconnect> med servern till en klient som blir kickad, skriv "chatRoom.disableChat();" i kod!?
//Color switch does not work when encrypted, sends wrong message!!!
//Scroll does not always work - and lags!

//Problems: tab switching does not work without server, no left end, no focus when click on tab
//Trådbugg: ibland får nya klienter en tom rad vid intro
//Återskapad bugg: server dör för sent, påverkar nyskapade tabbar
//Bugg: sendknappen låser sig ibland, antagligen samma fel som ovan, utloggningsmeddelanden missas också ibland
//Lösning: adapter??

//Skriv IPv6-adress, då ser man att programmet ej är multitrådat - gammal implementation bättre!
//Kolla portars tal! Kan inte skriva a eller d!!

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.util.ArrayList;

public class ChatBox {

    private static int tabCount = 1;
    private static final Object lock = new Object();

    public ChatBox() {
        ChatCreator.frame.addWindowListener(new ExitListener());
        ChatCreator.hostPane.addFocusListener(new TextFieldListener());
        ChatCreator.hostPane.addKeyListener(new CreatorListener());
        ChatCreator.portPane.addFocusListener(new TextFieldListener());
        ChatCreator.portPane.addKeyListener(new CreatorListener());
        ChatCreator.passPane.addFocusListener(new TextFieldListener());
        ChatCreator.passPane.addKeyListener(new CreatorListener());
        ChatCreator.requestPane.addFocusListener(new TextFieldListener());
        ChatCreator.requestPane.addKeyListener(new CreatorListener());
        ChatCreator.namePane.addFocusListener(new TextFieldListener());
        ChatCreator.namePane.addKeyListener(new CreatorListener());
        ChatCreator.tabPane.addFocusListener(new TextFieldListener());
        ChatCreator.tabPane.addKeyListener(new CreatorListener());
        ChatCreator.startButton.addActionListener(new StartButtonListener());
        ChatCreator.startButton.addKeyListener(new CreatorListener());
        ChatCreator.clientButton.addKeyListener(new CreatorListener());
        ChatCreator.serverButton.addItemListener(new ServerButtonListener());
        ChatCreator.serverButton.addKeyListener(new CreatorListener());
        ChatCreator.serverOptions.addItemListener(new ServerOptionsListener());
        ChatCreator.closeButton.addActionListener(new CloseButtonListener());
    }

    public final JPanel createTabPanel() throws IOException {
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
    
    public static void main(String[] args) {
        new ChatBox();
    }

    // Starta klient eller server
    public class StartButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            // throw NumberFormatException
            final ChatRoom chatRoom = new ChatRoom();
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
                    ChatCreator.chatRooms.add(chatRoom);
                    // don't add before you know it's good!!!
                    addUser(chatRoom, ChatCreator.chatRooms);
                }
            } catch (NumberFormatException ex) {
                chatRoom.success = false;
                ChatCreator.showError("Port is not an integer or not sufficiently small.");
            } finally {
                if (chatRoom.success) {
                    chatRoom.appendToPane(String.format("<message sender=\"SUCCESS\">"
                            + "<text color=\"#00ff00\">Connection successful</text></message>"));
                    // send this to others!
                    //chatRoom.appendToPane(String.format("<message sender=\"SUCCESS\">"
                    // - +"<text color=\"#00ff00\"> Connection established with %s </text></message>", clientSocket.getInetAddress()));
                    addUser2(chatRoom);
                    int index = ChatCreator.tabbedPane.getTabCount() - 1;
                    ChatCreator.tabbedPane.insertTab(ChatCreator.tabPane.getText(),
                            null, chatRoom.mainPanel, ChatCreator.tabPane.getText(), index);
                    try {
                        ChatCreator.tabbedPane.setTabComponentAt(index, createTabPanel());
                    } catch (IOException ex) {
                        ChatCreator.showError("Bilden kunde inte hittas");
                    }

                    ChatCreator.tabbedPane.setSelectedIndex(index);
                    ChatCreator.namePane.setText("User " + ChatCreator.generator.nextInt(1000000000));
                    ChatCreator.tabPane.setText("Chat " + String.valueOf(++tabCount));
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
        for (ChatRoom room : ChatCreator.chatRooms) {
            if (!room.items.contains(chatRoom)) {
                room.items.addElement(chatRoom);
            }
        }
    }

    public class ServerButtonListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                ChatCreator.startButton.setText("Create server");
                ChatCreator.hostLabel.setEnabled(false);
                ChatCreator.hostPane.setEnabled(false);
                ChatCreator.host = ChatCreator.hostPane.getText();
                ChatCreator.hostPane.setText("127.0.0.1");
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                ChatCreator.startButton.setText("Join server");
                ChatCreator.hostLabel.setEnabled(true);
                ChatCreator.hostPane.setEnabled(true);
                ChatCreator.hostPane.setText(ChatCreator.host);
            }
        }
    }

    public class ServerOptionsListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            String chosen = String.valueOf(
                    ChatCreator.serverOptions.getSelectedItem());
            if ("Protected".equals(chosen) || "Secret".equals(chosen)) {
                ChatCreator.passLabel.setVisible(true);
                ChatCreator.passPane.setVisible(true);
            } else {
                ChatCreator.passLabel.setVisible(false);
                ChatCreator.passPane.setVisible(false);
            }
            if ("Private".equals(chosen) || "Secret".equals(chosen)) {
                ChatCreator.requestLabel.setVisible(true);
                ChatCreator.requestPane.setVisible(true);
            } else {
                ChatCreator.requestLabel.setVisible(false);
                ChatCreator.requestPane.setVisible(false);
            }
        }
    }

    public class TabButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            int index = ChatCreator.tabbedPane.indexOfTabComponent(button.getParent());
            ChatCreator.tabbedPane.remove(index);
            // behövs nytt index fortfarande?
            if ((index = ChatCreator.indices.indexOf(button)) != -1) {
                ChatCreator.chatRooms.get(index).alive = false;
                for (ChatRoom room : ChatCreator.chatRooms) {
                    room.items.removeElement(ChatCreator.chatRooms.get(index));
                }
                ChatCreator.chatRooms.remove(index);
                ChatCreator.indices.remove(index);
            }
        }
    }

    // Stäng av hela programmet
    public class CloseButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ArrayList<ChatRoom> roomArray = new ArrayList<>();
            for (ChatRoom room : ChatCreator.chatRooms) {
                roomArray.add(room);
            }
            for (ChatRoom room : roomArray) {
                room.closeButton.doClick();
            }
            if (ChatCreator.chatRooms.isEmpty()) {
                int reply = JOptionPane.showConfirmDialog(ChatCreator.frame,
                        "Are you sure you want to exit ChatBox?",
                        "Confirmation", JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION) {
                    System.exit(0);
                } else {
                    JOptionPane.showMessageDialog(ChatCreator.frame,
                            "Good choice. Everyone's finger can slip!");
                }
            }
        }
    }
}