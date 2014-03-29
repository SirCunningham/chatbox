package chatbox;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.text.*;

public class Controller {

    private final View view;
    private final ArrayList<MessageBox> messageBoxes = new ArrayList<>();
    private final ArrayList<JButton> indices = new ArrayList<>();
    private int tabCount = 1;
    private ImageIcon icon;

    public Controller(View view) {
        this.view = view;
        view.IPPane.addFocusListener(new FieldListener());
        view.IPPane.addKeyListener(new StartListener());
        view.portPane.addFocusListener(new FieldListener());
        view.portPane.addKeyListener(new StartListener());
        view.passPane.addFocusListener(new FieldListener());
        view.passPane.addKeyListener(new StartListener());
        view.userName.addFocusListener(new FieldListener());
        view.userName.addKeyListener(new StartListener());
        view.tabPane.addFocusListener(new FieldListener());
        view.tabPane.addKeyListener(new StartListener());
        view.startButton.addActionListener(new StartButtonListener());
        view.startButton.addKeyListener(new StartListener());
        view.clientButton.addKeyListener(new StartListener());
        view.serverButton.addChangeListener(new ServerButtonListener());
        view.serverButton.addKeyListener(new StartListener());
        view.serverOptions.addItemListener(new ServerOptionsListener());
    }

    public final JPanel createTabPanel() {
        try {
            icon = new ImageIcon(ImageIO.read(new File("closeIcon.png")));
        } catch (IOException e) {
            System.err.println("Filen kunde inte hittas");
            e.printStackTrace();
        }

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
            final MessageBox messageBox = new MessageBox(view);
            messageBox.success = true;
            try {
                final String host = view.IPPane.getText();
                final int port = Integer.parseInt(view.portPane.getText());
                if (view.serverButton.isSelected()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            new Thread(new Server(port, messageBox)).start();
                        }
                    }).start();
                    messageBox.bootPanel.setVisible(true);
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new Thread(new Client(host, port, messageBox)).start();
                    }
                }).start();
            } catch (NumberFormatException ex) {
                // Let the bad ones come here!
                messageBox.success = false;
                System.err.println("Ett fel inträffade1: " + ex);
            } finally {
                if (messageBox.success) {
                    messageBoxes.add(messageBox);
                    int index = view.tabbedPane.getTabCount() - 1;
                    view.tabbedPane.insertTab(null, null, messageBox.mainPanel,
                            view.tabPane.getText(), index);
                    view.tabbedPane.setTabComponentAt(index, createTabPanel());
                    view.tabbedPane.setSelectedIndex(index);
                    tabCount += 1;
                    view.tabPane.setText("Chat " + String.valueOf(tabCount));
                }
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
                //messageBoxes.get(index).kill();
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
}