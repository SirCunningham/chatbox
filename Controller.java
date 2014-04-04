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

    private final View view;
    private volatile ArrayList<MessageBox> messageBoxes = new ArrayList<>();
    private final ArrayList<JButton> indices = new ArrayList<>();
    private int tabCount = 1;
    private Random rand = new Random();
    private Object lock = new Object();

    public Controller(View view) {
        this.view = view;
        view.IPPane.addFocusListener(new FieldListener());
        view.IPPane.addKeyListener(new StartListener());
        view.portPane.addFocusListener(new FieldListener());
        view.portPane.addKeyListener(new StartListener());
        view.passPane.addFocusListener(new FieldListener());
        view.passPane.addKeyListener(new StartListener());
        view.namePane.addFocusListener(new FieldListener());
        view.namePane.addKeyListener(new StartListener());
        view.tabPane.addFocusListener(new FieldListener());
        view.tabPane.addKeyListener(new StartListener());
        view.startButton.addActionListener(new StartButtonListener());
        view.startButton.addKeyListener(new StartListener());
        view.clientButton.addKeyListener(new StartListener());
        view.serverButton.addChangeListener(new ServerButtonListener());
        view.serverButton.addKeyListener(new StartListener());
        view.serverOptions.addItemListener(new ServerOptionsListener());
        view.closeButton.addActionListener(new CloseButtonListener());
    }

    public final JPanel createTabPanel() throws IOException {
        ImageIcon icon = new ImageIcon(ImageIO.read(new File("closeIcon.png")));
        JPanel tabPanel = new JPanel(new GridBagLayout());
        tabPanel.setOpaque(false);
        JLabel tabLabel = new JLabel(view.tabPane.getText() + " ");
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
            // move server and client to their classes, initialize before run!
            final MessageBox messageBox = new MessageBox(view);

            try {
                final String host = view.IPPane.getText();
                final int port = Integer.parseInt(view.portPane.getText());
                if (view.serverButton.isSelected()) {
                    // Starta socket för servern
                    final ServerSocket serverSocket;
                    try {
                        serverSocket = new ServerSocket(port);
                        serverSocket.setSoTimeout(100);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                new Thread(new Server(serverSocket, port,
                                        messageBox)).start();
                            }
                        }).start();
                    } catch (IOException ex) {
                        messageBox.success = false;
                        messageBox.showError(String.format("Could not listen on port %d.",
                                port));
                    }
                    // add color here!
                    messageBox.chatBox.setText("This is where it happens. "
                            + "Waiting for others to connect.");
                    messageBox.bootPanel.setVisible(true);
                }
                if (messageBox.success) {
                    // Starta socket för klienten
                    final Socket clientSocket;
                    final BufferedReader i;
                    final PrintWriter o;
                    try {
                        clientSocket = new Socket(host, port);
                        i = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        o = new PrintWriter(clientSocket.getOutputStream(), true);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                new Thread(new Client(clientSocket, i, o, port,
                                        messageBox)).start();
                                messageBoxes.add(messageBox);
                                addUser(messageBox, messageBoxes);
                            }
                        }).start();
                    } catch (UnknownHostException ex) {
                        messageBox.success = false;
                        messageBox.showError("Don't know about host.");
                    } catch (IOException ex) {
                        messageBox.success = false;
                        messageBox.showError("Couldn't get I/O for the connection to host.");
                    }
                }
            } catch (NumberFormatException ex) {
                messageBox.success = false;
                messageBox.showError("Port is not a small number!");
            } finally {
                if (messageBox.success) {
                    messageBox.appendToPane(String.format("<message sender=\"SUCCESS\">"
                            + "<text color=\"#00ff00\"> Connection successful </text></message>"));
                    // send this to others!
                    //messageBox.appendToPane(String.format("<message sender=\"SUCCESS\">"
                    //        - +"<text color=\"#00ff00\"> Connection established with %s </text></message>", clientSocket.getInetAddress()));
                    addUser2(String.format("%s (%s)",messageBox.getName(), messageBox.getIP()));
                    int index = view.tabbedPane.getTabCount() - 1;
                    view.tabbedPane.insertTab(null, null, messageBox.mainPanel,
                            view.tabPane.getText(), index);
                    try {
                        view.tabbedPane.setTabComponentAt(index, createTabPanel());
                    } catch (IOException ex) {
                        messageBox.showError("Bilden kunde inte hittas");
                    }
                    
                    view.tabbedPane.setSelectedIndex(index);
                    view.namePane.setText("User " + rand.nextInt(1000000000));
                    view.tabPane.setText("Chat " + String.valueOf(++tabCount));
                }
            }
        }
    }

    public void addUser(MessageBox messageBox, ArrayList<MessageBox> msgBoxes) {
        synchronized (lock) {
            for (MessageBox msgBox : msgBoxes) {
                String nameIP = String.format("%s (%s)", msgBox.getName(), msgBox.getIP());
                if (!messageBox.items.contains(nameIP)) {
                    messageBox.items.addElement(nameIP);
                }
            }
        }

    }
    public void addUser2(String user) {
        for (MessageBox msgBox : messageBoxes) {
            if (!msgBox.items.contains(user)) {
                msgBox.items.addElement(user);
            }
        }
    }

    public class ServerButtonListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            if (view.serverButton.isSelected()) {
                view.startButton.setText("Create server");
                view.IPLabel.setEnabled(false);
                view.IPPane.setEnabled(false);
            } else {
                view.startButton.setText("Join server");
                view.IPLabel.setEnabled(true);
                view.IPPane.setEnabled(true);
            }
        }
    }

    public class ServerOptionsListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            String chosen = String.valueOf(
                    view.serverOptions.getSelectedItem());
            if ("Protected".equals(chosen) || "Secret".equals(chosen)) {
                view.passLabel.setVisible(true);
                view.passPane.setVisible(true);
            } else {
                view.passLabel.setVisible(false);
                view.passPane.setVisible(false);
            }
        }
    }

    public class TabButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            int index = view.tabbedPane.indexOfTabComponent(button.getParent());
            view.tabbedPane.remove(index);
            if ((index = indices.indexOf(button)) != -1) {
                // only kills after new client joins!!!
                messageBoxes.get(index).alive = false;
                messageBoxes.remove(index);
                System.out.println(messageBoxes.size());
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
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                view.startButton.doClick();
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
            int reply = JOptionPane.showConfirmDialog(view.frame, "Are you sure you "
                    + "want to quit?", "Confirmation",
                    JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {

                System.exit(0);
            } else {
                JOptionPane.showMessageDialog(view.frame, "Good choice. "
                        + "Everyone's finger can slip!");
            }
        }
    }
}
