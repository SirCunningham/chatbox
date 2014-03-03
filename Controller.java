package chatbox;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;

public class Controller {

    private View view;
    private ArrayList<Thread> clients = new ArrayList<>();
    private ArrayList<TabButton> tabButtons = new ArrayList<>();
    private int tabCount = 1;
    private String filePath;

    public Controller(View view) {
        this.view = view;
        view.IPField.addFocusListener(new FieldListener());
        view.portField.addFocusListener(new FieldListener());
        view.passField.addFocusListener(new FieldListener());
        view.tabField.addFocusListener(new FieldListener());
        view.fileField.addFocusListener(new FieldListener());
        view.descriptionField.addFocusListener(new FieldListener());
        view.startButton.addActionListener(new StartButtonListener());
        view.serverButton.addChangeListener(new ServerButtonListener());
        view.connectButton.addActionListener(new ConnectButtonListener());
        view.sendButton.addActionListener(new SendButtonListener());
        view.receiveButton.addActionListener(new ReceiveButtonListener());
        view.fileButton.addActionListener(new FileButtonListener());
        view.closeButton.addActionListener(new CloseButtonListener());
        view.serverOptions.addItemListener(new ServerOptionsListener());
    }

    public final JPanel createTabPanel() {        
        JPanel pnlTab = new JPanel(new GridBagLayout());
        pnlTab.setOpaque(false);
        JLabel lblTitle = new JLabel(view.tabField.getText() + " ");
        TabButton btnClose = new TabButton(view.icon,
                view.tabbedPane.getTabCount() - 2);
        tabButtons.add(btnClose);
        btnClose.setPreferredSize(new Dimension(12, 12));
        btnClose.addActionListener(new TabButtonListener());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        pnlTab.add(lblTitle, gbc);

        gbc.gridx++;
        gbc.weightx = 0;
        pnlTab.add(btnClose, gbc);
        return pnlTab;
    }

    public class FieldListener implements FocusListener {

        @Override
        public void focusGained(FocusEvent e) {
            JTextField source = (JTextField) e.getSource();
            source.selectAll();
        }

        @Override
        public void focusLost(FocusEvent e) {
            JTextField source = (JTextField) e.getSource();
            source.select(0, 0);
        }
    }
    
    // Starta klient eller server
    public class StartButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            boolean success = true;
            MessageBox messageBox = new MessageBox(view);
            try {
                int port = Integer.parseInt(view.portField.getText());
                if (view.serverButton.isSelected()) {
                    view.startButton.setBackground(Color.RED);
                    Thread thr = new Thread(new Server(port, view, messageBox));
                    thr.start();
                } else {
                    view.startButton.setBackground(Color.GREEN);
                    Thread thr = new Thread(new Client(view.IPField.getText(),
                            port, view, messageBox));
                    thr.start();
                    clients.add(thr);
                }
            } catch (Exception ex) {
                success = false;
                System.err.println("Ett fel inträffade1: " + ex);
            } finally {
                if (success) {
                    //kill idling threads here!
                    int index = view.tabbedPane.getTabCount() - 1;
                    view.tabbedPane.insertTab(null, null, messageBox, view.tabField.getText(), index);
                    view.tabbedPane.setTabComponentAt(index, createTabPanel());
                    view.tabbedPane.setSelectedIndex(index);
                    tabCount += 1;
                    view.tabField.setText("Chat " + String.valueOf(tabCount));
                    view.startButton.setBackground(null);
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
                view.IPField.setEnabled(false);
            } else {
                view.startButton.setText("Join server");
                view.IPLabel.setEnabled(true);
                view.IPField.setEnabled(true);
            }
        }
    }

    // Skicka fil med klient
    public class SendButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                FileSender thr = new FileSender(view.IPField.getText(),
                        Integer.parseInt(view.portField.getText()),
                        view.fileField.getText());
                thr.start();
            } catch (Exception ex) {
                System.err.println("Ett fel inträffade2: " + ex);
            }
        }
    }

    // Mottag fil med server
    public class ReceiveButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                FileReceiver thr = new FileReceiver(Integer.parseInt(
                        view.portField.getText()),
                        view.fileField.getText());
                thr.start();
            } catch (Exception ex) {
                System.err.println("Ett fel inträffade4: " + ex);
            }
        }
    }

    // Stäng av hela programmet
    public class CloseButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int reply = JOptionPane.showConfirmDialog(null, "Are you sure you "
                    + "want to quit?", "Confirmation",
                    JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                System.exit(0);
            } else {
                JOptionPane.showMessageDialog(null, "Good choice. "
                        + "Everyone's finger can slip!");
            }
        }
    }

    public class ConnectButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int reply = JOptionPane.showConfirmDialog(null,
                    String.format("File name: %s\nFile description: %s\n"
                    + "File size: unknown\nAccept file and kill?",
                    view.fileField.getText(),
                    view.descriptionField.getText()),
                    "Kill", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(null, "Hello killer!");
            } else {
                JOptionPane.showMessageDialog(null, "Goodbye!");
            }
        }
    }

    public class FileButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();

            int returnVal = chooser.showOpenDialog(view.chatBoxPanel);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                filePath = file.getAbsolutePath();
                view.fileField.setText(file.getName());
            }
        }
    }

    //Gör så att den tomma platsen ockuperas!
    public class ServerOptionsListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            String chosen = String.valueOf(
                    view.serverOptions.getSelectedItem());
            if ("Protected".equals(chosen) || "Secret".equals(chosen)) {
                view.passLabel.setVisible(true);
                view.passField.setVisible(true);
            } else {
                view.passLabel.setVisible(false);
                view.passField.setVisible(false);
            }
        }
    }

    public class TabButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            TabButton button = (TabButton) e.getSource();
            int index = button.getIndex();
            if (index == view.tabbedPane.getTabCount() - 2 && index != 0) {
                view.tabbedPane.setSelectedIndex(index - 1);
            }
            view.tabbedPane.remove(index);
            tabButtons.remove(index);
            view.chatBoxes.remove(index);
            for (TabButton button1 : tabButtons) {
                if (button1.getIndex() > index) {
                    button1.setIndex(button1.getIndex() - 1);
                }
            }
        }
    }
}