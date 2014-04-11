package chatbox;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.text.*;
import java.util.Random.*;

public class Controller {

    private final ChatCreator chatCreator;
    static volatile ArrayList<ChatRoom> messageBoxes = new ArrayList<>();
    private final ArrayList<JButton> indices = new ArrayList<>();
    private int tabCount = 1;
    private final Random rand = new Random();
    private final Object lock = new Object();

    public Controller(ChatCreator chatCreator) {
        this.chatCreator = chatCreator;
        chatCreator.hostPane.addFocusListener(new FieldListener());
        chatCreator.hostPane.addKeyListener(new StartListener());
        chatCreator.portPane.addFocusListener(new FieldListener());
        chatCreator.portPane.addKeyListener(new StartListener());
        chatCreator.passPane.addFocusListener(new FieldListener());
        chatCreator.passPane.addKeyListener(new StartListener());
        chatCreator.requestPane.addFocusListener(new FieldListener());
        chatCreator.requestPane.addKeyListener(new StartListener());
        chatCreator.namePane.addFocusListener(new FieldListener());
        chatCreator.namePane.addKeyListener(new StartListener());
        chatCreator.tabPane.addFocusListener(new FieldListener());
        chatCreator.tabPane.addKeyListener(new StartListener());
        chatCreator.startButton.addActionListener(new StartButtonListener());
        chatCreator.startButton.addKeyListener(new StartListener());
        chatCreator.clientButton.addKeyListener(new StartListener());
        chatCreator.serverButton.addChangeListener(new ServerButtonListener());
        chatCreator.serverButton.addKeyListener(new StartListener());
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
        indices.add(closeButton);

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

    public class FieldListener implements FocusListener {

        @Override
        public void focusGained(FocusEvent e) {
            JTextComponent source = (JTextComponent) e.getSource();
            source.selectAll();
        }

        @Override
        public void focusLost(FocusEvent e) {
            JTextComponent source = (JTextComponent) e.getSource();
            source.select(0, 0);
        }
    }

    // Starta klient eller server
    public class StartButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            final ChatRoom chatRoom = new ChatRoom(chatCreator);
            try {
                final String host = chatCreator.hostPane.getText();
                final int port = Integer.parseInt(chatCreator.portPane.getText());
                final boolean isServer = chatCreator.serverButton.isSelected();
                if (isServer) {
                    chatCreator.hostPane.setText("127.0.0.1");
                        
                    Server server = new Server(port, chatRoom);
                    new Thread(server).start();
                    chatRoom.appendToPane(String.format("<message sender=\"INFO\">"
                            + "<text color=\"#339966\">Wait for others to connect...</text></message>"));
                    chatRoom.bootPanel.setVisible(true);
                }
                if (chatRoom.success) {
                    Client client = new Client(host, port, chatRoom, isServer);
                    new Thread(client).start();
                    messageBoxes.add(chatRoom);
                    addUser(chatRoom, messageBoxes);
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
                    //        - +"<text color=\"#00ff00\"> Connection established with %s </text></message>", clientSocket.getInetAddress()));
                    addUser2(chatRoom);
                    int index = chatCreator.tabbedPane.getTabCount() - 1;
                    chatCreator.tabbedPane.insertTab(null, null, chatRoom.mainPanel,
                            chatCreator.tabPane.getText(), index);
                    try {
                        chatCreator.tabbedPane.setTabComponentAt(index, createTabPanel());
                    } catch (IOException ex) {
                        chatRoom.showError("Bilden kunde inte hittas");
                    }

                    chatCreator.tabbedPane.setSelectedIndex(index);
                    chatCreator.namePane.setText("User " + rand.nextInt(1000000000));
                    chatCreator.tabPane.setText("Chat " + String.valueOf(++tabCount));
                }
            }
        }
    }

    private void addUser(ChatRoom chatRoom, ArrayList<ChatRoom> msgBoxes) {
        synchronized (lock) {
            for (ChatRoom msgBox : msgBoxes) {
                if (!chatRoom.items.contains(msgBox)) {
                    chatRoom.items.addElement(msgBox);
                }
            }
        }

    }

    private void addUser2(ChatRoom chatRoom) {
        for (ChatRoom msgBox : messageBoxes) {
            if (!msgBox.items.contains(chatRoom)) {
                msgBox.items.addElement(chatRoom);
            }
        }
    }

    public class ServerButtonListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            if (chatCreator.serverButton.isSelected()) {
                chatCreator.startButton.setText("Create server");
                chatCreator.hostLabel.setEnabled(false);
                chatCreator.hostPane.setEnabled(false);
            } else {
                chatCreator.startButton.setText("Join server");
                chatCreator.hostLabel.setEnabled(true);
                chatCreator.hostPane.setEnabled(true);
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
            if ((index = indices.indexOf(button)) != -1) {
                messageBoxes.get(index).alive = false;
                for (ChatRoom msgBox : messageBoxes) {
                    msgBox.items.removeElement(messageBoxes.get(index));
                }
                messageBoxes.remove(index);
                indices.remove(index);
            }
        }
    }

    class StartListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_ENTER:
                    chatCreator.startButton.doClick();
                    break;
                case KeyEvent.VK_A:
                case KeyEvent.VK_KP_LEFT:
                case KeyEvent.VK_LEFT:
                    chatCreator.clientButton.setSelected(true);
                    chatCreator.startButton.setText("Join server");
                    chatCreator.hostLabel.setEnabled(true);
                    chatCreator.hostPane.setEnabled(true);
                    break;
                case KeyEvent.VK_D:
                case KeyEvent.VK_KP_RIGHT:
                case KeyEvent.VK_RIGHT:
                    chatCreator.serverButton.setSelected(true);
                    chatCreator.startButton.setText("Create server");
                    chatCreator.hostLabel.setEnabled(false);
                    chatCreator.hostPane.setEnabled(false);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    // Stäng av hela programmet
    public class CloseButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int reply = JOptionPane.showConfirmDialog(chatCreator.frame, "Are you sure you "
                    + "want to quit?", "Confirmation",
                    JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {

                System.exit(0);
            } else {
                JOptionPane.showMessageDialog(chatCreator.frame, "Good choice. "
                        + "Everyone's finger can slip!");
            }
        }
    }
}
