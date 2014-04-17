package chatbox;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;

public class ChatCreator {

    JFrame frame = new JFrame("ChatBox - instant messaging for pros");
    JPanel dialogPanel = new JPanel();
    JRadioButton clientButton = new JRadioButton("Client");
    JRadioButton serverButton = new JRadioButton("Server");
    ButtonGroup buttonGroup = new ButtonGroup();
    JLabel hostLabel = new JLabel("Host:");
    JLabel passLabel = new JLabel("Password:");
    JLabel requestLabel = new JLabel("Request:");
    JTextPane hostPane = new JTextPane();
    JTextPane portPane = new JTextPane();
    JPasswordField passPane = new JPasswordField("4hfJ/dc.5t", 10);
    JTextField requestPane = new JTextField("Let me in!", 15);
    JTextPane namePane = new JTextPane();
    JTextPane tabPane = new JTextPane();
    JButton startButton = new JButton("Join server");
    IconButton closeButton = new IconButton("closeIcon.png");
    JComboBox serverOptions;
    JTabbedPane tabbedPane = new JTabbedPane();
    
    final ArrayList<ChatRoom> messageBoxes = new ArrayList<>();
    final ArrayList<JButton> indices = new ArrayList<>();

    // Skapa GUI
    public ChatCreator() {
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        WindowListener exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                int reply = JOptionPane.showConfirmDialog(frame,
                        "Are you sure you want to exit ChatBox?",
                        "Confirmation", JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        };
        frame.addWindowListener(exitListener);
        
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setPreferredSize(new Dimension(dim.width * 4 / 5, dim.height / 2));
        tabbedPane.setFocusable(false);

        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.addTab("+", null, dialogPanel, "Create a new chat");

        buttonGroup.add(clientButton);
        buttonGroup.add(serverButton);
        clientButton.setSelected(true);
        closeButton.setFocusPainted(false);

        ((AbstractDocument) hostPane.getDocument()).setDocumentFilter(new NewLineFilter(48));
        hostPane.setText("127.0.0.1");
        ((AbstractDocument) portPane.getDocument()).setDocumentFilter(new NewLineFilter(12));
        portPane.setText("4444");
        ((AbstractDocument) namePane.getDocument()).setDocumentFilter(new NewLineFilter(24));
        namePane.setText("User 1000000000");
        ((AbstractDocument) tabPane.getDocument()).setDocumentFilter(new NewLineFilter(24));
        tabPane.setText("Chat 1");

        String[] stringOptions = {"Public", "Protected", "Private", "Secret"};
        serverOptions = new JComboBox(stringOptions);

        passLabel.setVisible(false);
        passPane.setVisible(false);
        
        requestLabel.setVisible(false);
        requestPane.setVisible(false);

        JPanel radioPanel = new JPanel();
        JPanel hostPanel = new JPanel();
        JPanel portPanel = new JPanel();
        JPanel passPanel = new JPanel();
        JPanel namePanel = new JPanel();
        JPanel tabPanel = new JPanel();
        JPanel startPanel = new JPanel();
        JPanel invisibleContainer1 = new JPanel(new GridLayout(1, 1));
        JPanel invisibleContainer2 = new JPanel(new GridLayout(1, 1));
        JPanel invisibleContainer3 = new JPanel(new GridLayout(1, 1));
        JPanel invisibleContainer4 = new JPanel(new GridLayout(1, 1));
        
        radioPanel.add(clientButton);
        radioPanel.add(serverButton);
        hostPanel.add(hostLabel);
        hostPanel.add(hostPane);
        portPanel.add(new JLabel("Port:"));
        portPanel.add(portPane);
        passPanel.add(new JLabel("Type:"));
        passPanel.add(serverOptions);
        invisibleContainer1.add(passLabel);
        invisibleContainer2.add(passPane);
        invisibleContainer3.add(requestLabel);
        invisibleContainer4.add(requestPane);
        passPanel.add(invisibleContainer1);
        passPanel.add(invisibleContainer2);
        passPanel.add(invisibleContainer3);
        passPanel.add(invisibleContainer4);
        namePanel.add(new JLabel("Username:"));
        namePanel.add(namePane);
        tabPanel.add(new JLabel("Chatroom name:"));
        tabPanel.add(tabPane);
        startPanel.add(startButton);
        startPanel.add(closeButton);
        dialogPanel.add(radioPanel);
        dialogPanel.add(hostPanel);
        dialogPanel.add(portPanel);
        dialogPanel.add(passPanel);
        dialogPanel.add(namePanel);
        dialogPanel.add(tabPanel);
        dialogPanel.add(startPanel);

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
}