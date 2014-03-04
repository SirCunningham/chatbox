package chatbox;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;

public class View {

    JFrame frame = new JFrame("Instant messaging program for pros");
    JPanel mainPanel = new JPanel();
    JPanel leftPanel = new JPanel();
    JPanel rightPanel = new JPanel();
    JPanel dialogPanel = new JPanel();
    JPanel filePanel = new JPanel();
    JPanel fileButtonPanel = new JPanel();
    JPanel fileColorExitPanel = new JPanel();
    JRadioButton clientButton = new JRadioButton("Client");
    JRadioButton serverButton = new JRadioButton("Server");
    ButtonGroup buttonGroup = new ButtonGroup();
    JTextField IPField = new JTextField("127.0.0.1", 25);
    JTextField portField = new JTextField("4444", 24);
    JTextField passField = new JPasswordField("4hfJ/dc.5t", 24);
    JTextField tabField = new JTextField("Chat 1", 24);
    JTextField fileField = new JTextField("filename.txt", 12);
    JTextField descriptionField = new JTextField("File description (optional)",
            25);
    JProgressBar progressBar = new JProgressBar();
    private String[] cipherString = {"None", "caesar", "AES", "RSA", "blowfish"};

    // Possible to do ArrayList.toArray() to add items dynamically
    // Use getSelected...
    String[] items = {"IP 1", "IP 2", "IP 3", "IP 4"};
    JList list = new JList(items);
    JButton startButton = new JButton("Join server");
    JButton connectButton = new JButton("Disconnect [currently: receive]");
    JButton sendButton = new JButton("Send to selected");
    JButton receiveButton = new JButton("Receive [testing only]");
    JButton bootButton = new JButton("Boot selected"); //Add confirmation dialog, only unlocked if server!!
    IconButton fileButton = new IconButton("fileIcon.png");
    IconButton closeButton = new IconButton("closeIcon.png");
    JComboBox fileEncryptions;
    JComboBox serverOptions;
    ImageIcon icon;
    JTabbedPane tabbedPane = new JTabbedPane();

    // Skapa GUI
    public View() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tabbedPane.setFocusable(false);
        connectButton.setEnabled(false);
        sendButton.setEnabled(false); //Enable when file chosen
        fileButton.setBorder(BorderFactory.createEmptyBorder());
        closeButton.setFocusPainted(false);

        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.addTab("+", null, dialogPanel, "Create a new chat");

        try {
            icon = new ImageIcon(ImageIO.read(new File("closeIcon.png")));
        } catch (IOException e) {
            System.err.println("Filen kunde inte hittas");
            e.printStackTrace();
        }

        buttonGroup.add(clientButton);
        buttonGroup.add(serverButton);
        clientButton.setSelected(true);

        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        int[] select = {1, 3};
        list.setSelectedIndices(select);

        fileEncryptions = new JComboBox(cipherString);
        String[] stringOptions = {"Public", "Protected", "Private", "Secret"};
        serverOptions = new JComboBox(stringOptions);

        passLabel.setVisible(false);
        passField.setVisible(false);

        JPanel radioPanel = new JPanel();
        JPanel IPPanel = new JPanel();
        JPanel portPanel = new JPanel();
        JPanel passPanel = new JPanel();
        JPanel tabPanel = new JPanel();
        JPanel startPanel = new JPanel();
        radioPanel.add(clientButton);
        radioPanel.add(serverButton);
        IPPanel.add(new JLabel("IP:"));
        IPPanel.add(IPField);
        portPanel.add(new JLabel("Port:"));
        portPanel.add(portField);
        passPanel.add(new JLabel("Type:"));
        passPanel.add(serverOptions);
        passPanel.add(new JLabel("Password:"));
        passPanel.add(passField);
        tabPanel.add(new JLabel("Chat name:"));
        tabPanel.add(tabField);
        startPanel.add(startButton);
        dialogPanel.add(radioPanel);
        dialogPanel.add(IPPanel);
        dialogPanel.add(portPanel);
        dialogPanel.add(passPanel);
        dialogPanel.add(tabPanel);
        dialogPanel.add(startPanel);
        
        filePanel.add(fileButton);
        filePanel.add(fileField);
        filePanel.add(descriptionField);
        fileButtonPanel.add(sendButton);
        fileButtonPanel.add(new JLabel("Encryption:"));
        fileButtonPanel.add(fileEncryptions);
        fileColorExitPanel.add(connectButton);
        fileColorExitPanel.add(closeButton);
        //panel.add(receiveButton);
        leftPanel.add(tabbedPane);
        rightPanel.add(list);
        rightPanel.add(bootButton);
        rightPanel.add(filePanel);
        rightPanel.add(fileButtonPanel);
        rightPanel.add(progressBar);
        rightPanel.add(fileColorExitPanel);
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        frame.add(leftPanel, BorderLayout.CENTER);
        frame.add(rightPanel, BorderLayout.EAST);
        frame.pack();
        frame.setVisible(true);
    }
}