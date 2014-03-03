package chatbox;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;

public class View {

    ArrayList<JTextPane> chatBoxes = new ArrayList<>();
    JFrame frame = new JFrame("Instant messaging program for pros");
    JPanel chatBoxPanel = new JPanel();
    JPanel mainPanel = new JPanel();
    JPanel leftPanel = new JPanel();
    JPanel rightPanel = new JPanel();
    JPanel dialogPanel = new JPanel();
    JPanel IPPanel = new JPanel();
    JPanel portPanel = new JPanel();
    JPanel passPanel = new JPanel();
    JPanel tabPanel = new JPanel();
    JPanel filePanel = new JPanel();
    JPanel fileButtonPanel = new JPanel();
    JPanel fileColorExitPanel = new JPanel();
    JPanel startPanel = new JPanel();
    JRadioButton clientButton = new JRadioButton("Client");
    JRadioButton serverButton = new JRadioButton("Server");
    ButtonGroup buttonGroup = new ButtonGroup();
    final JLabel IPLabel = new JLabel("IP:");
    final JLabel portLabel = new JLabel("Port:");
    final JLabel passLabel = new JLabel("Password:");
    final JLabel tabLabel = new JLabel("Chat name:");
    final JLabel encryptLabel2 = new JLabel("Encryption:");
    final JLabel typeLabel = new JLabel("Type:");
    JTextField IPField = new JTextField("127.0.0.1", 25);
    JTextField portField = new JTextField("4444", 24);
    JTextField passField = new JPasswordField("4hfJ/dc.5t", 24);
    JTextField tabField = new JTextField("Chat 1", 24);
    JTextField fileField = new JTextField("filename.txt", 12);
    JTextField descriptionField = new JTextField("File description (optional)",
            25);
    JProgressBar progressBar = new JProgressBar();
    private String[] cipherString = {"None", "caesar", "AES", "RSA", "blowfish"};
    // Fields must be multi-threaded
    // Possible to do ArrayList.toArray() to add items dynamically
    // Use getSelected...
    String[] items = {"User 1", "User 2", "User 3", "User 4"};
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

        chatBoxPanel.setLayout(new BoxLayout(chatBoxPanel, BoxLayout.Y_AXIS));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        //justera bilden åt höger
        try {
            icon = new ImageIcon(ImageIO.read(new File("closeIcon.png")));
        } catch (IOException e) {
            System.err.println("Filen kunde inte hittas");
            e.printStackTrace();
        }

        //lägg till ruta för deltagare
        //tabbedPane.addTab(null, createChatBox());

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

        dialogPanel.add(startPanel);
        dialogPanel.add(IPPanel);
        dialogPanel.add(portPanel);
        dialogPanel.add(passPanel);
        dialogPanel.add(tabPanel);
        dialogPanel.add(startButton);
        tabbedPane.addTab("+", null, dialogPanel, "Create a new chat");

        chatBoxPanel.add(tabbedPane);
        IPPanel.add(IPLabel);
        IPPanel.add(IPField);
        portPanel.add(portLabel);
        portPanel.add(portField);
        passPanel.add(typeLabel);
        passPanel.add(serverOptions);
        passPanel.add(passLabel);
        passPanel.add(passField);
        tabPanel.add(tabLabel);
        tabPanel.add(tabField);
        filePanel.add(fileButton);
        filePanel.add(fileField);
        filePanel.add(descriptionField);
        fileButtonPanel.add(sendButton);
        fileButtonPanel.add(encryptLabel2);
        fileButtonPanel.add(fileEncryptions);
        fileColorExitPanel.add(connectButton);
        fileColorExitPanel.add(closeButton);
        //panel.add(receiveButton);
        leftPanel.add(chatBoxPanel);
        startPanel.add(clientButton);
        startPanel.add(serverButton);
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