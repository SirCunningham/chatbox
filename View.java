package chatbox;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.util.*;

public class View {

    JFrame frame = new JFrame("Instant messaging program for pros");
    JPanel chatBoxPanel = new JPanel();
    JPanel chatBoxButtonPanel = new JPanel();
    JPanel mainPanel = new JPanel();
    JPanel leftPanel = new JPanel();
    JPanel rightPanel = new JPanel();
    JPanel dialogPanel = new JPanel();
    JPanel IPPanel = new JPanel();
    JPanel portPanel = new JPanel();
    JPanel passPanel = new JPanel();
    JPanel tabPanel = new JPanel();
    JPanel networkButtonPanel = new JPanel();
    JPanel filePanel = new JPanel();
    JPanel fileColorExitPanel = new JPanel();
    JPanel startPanel = new JPanel();
    JRadioButton clientButton = new JRadioButton("Client");
    JRadioButton serverButton = new JRadioButton("Server");
    ButtonGroup buttonGroup = new ButtonGroup();
    private final JLabel IPLabel = new JLabel("IP:");
    private final JLabel portLabel = new JLabel("Port:");
    private final JLabel passLabel = new JLabel("Password:");
    private final JLabel tabLabel = new JLabel("Chat name:");
    private final JLabel encryptLabel = new JLabel("Encryption:");
    private final JLabel typeLabel = new JLabel("Type:");
    JTextField IPField = new JTextField("127.0.0.1", 10);
    JTextField portField = new JTextField("4444", 9);
    JTextField passField = new JPasswordField("4hfJ/dc.5t", 9);
    JTextField tabField = new JTextField("Chat 1", 9);
    JTextField nameField = new JTextField("Dante", 5);
    JTextField messageField = new JTextField("In medio cursu vitae"
            + "nostrae, eram in silva obscura...", 40);
    JTextField fileField = new JTextField("filename.txt", 8);
    JTextField descriptionField = new JTextField("File description (optional)",
            15);
    JProgressBar progressBar = new JProgressBar();
    // Fields must be multi-threaded
    
    private final JLabel encLabel = new JLabel("Int:");
    JTextField encField = new JTextField("68", 5);
    
    // Possible to do ArrayList.toArray() to add items dynamically
    // Use getSelected...
    private String[] items = {"User 1", "User 2", "User 3", "User 4"};
    JList list = new JList(items);
    
    ArrayList<JTextPane> chatBoxes= new ArrayList<>();
    JButton sendMsgButton = new JButton("Send message");
    JToggleButton encryptButton = new JToggleButton("Encrypt selected");
    JButton startButton = new JButton("Join server");
    JButton connectButton = new JButton("Disconnect [currently: receive]");
    JButton sendButton = new JButton("Send to selected");
    JButton receiveButton = new JButton("Receive [testing only]");
    JButton bootButton = new JButton("Boot selected"); //Add confirmation dialog, only unlocked if server!!
    IconButton fileButton = new IconButton("fileIcon.png");
    IconButton colorButton = new IconButton("colorIcon.png");
    IconButton closeButton = new IconButton("closeIcon.png");
    JComboBox messageEncryptions;
    JComboBox fileEncryptions;
    JComboBox serverOptions;
    private ImageIcon icon;
    Panel pane = new Panel();
    JTabbedPane tabbedPane = new JTabbedPane();

    // Skapa GUI
    public View() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tabbedPane.setFocusable(false);
        sendMsgButton.setEnabled(false);
        encryptButton.setEnabled(false);
        connectButton.setEnabled(false);
        sendButton.setEnabled(false); //Enable when file chosen
        colorButton.setBorder(BorderFactory.createEmptyBorder());
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
        
        String[] stringEncryptions = {"None", "caesar", "AES", "RSA", "blowfish"};
        messageEncryptions = new JComboBox(stringEncryptions);
        fileEncryptions = new JComboBox(stringEncryptions);
        String[] stringOptions = {"Public", "Protected", "Private", "Secret"};
        serverOptions = new JComboBox(stringOptions);

        passLabel.setVisible(false);
        passField.setVisible(false);
        
        encLabel.setVisible(false);
        encField.setVisible(false);

        chatBoxPanel.add(tabbedPane);
        chatBoxButtonPanel.add(colorButton);
        chatBoxButtonPanel.add(nameField);
        chatBoxButtonPanel.add(messageField);
        chatBoxButtonPanel.add(sendMsgButton);
        chatBoxButtonPanel.add(encryptButton);
        chatBoxButtonPanel.add(encryptLabel);
        chatBoxButtonPanel.add(messageEncryptions);
        chatBoxButtonPanel.add(encLabel);
        chatBoxButtonPanel.add(encField);
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
        networkButtonPanel.add(startButton);
        networkButtonPanel.add(connectButton);
        filePanel.add(fileButton);
        filePanel.add(fileField);
        filePanel.add(descriptionField);
        filePanel.add(sendButton);
        filePanel.add(fileEncryptions);
        fileColorExitPanel.add(closeButton);
        //panel.add(receiveButton);
        leftPanel.add(chatBoxPanel);
        leftPanel.add(chatBoxButtonPanel);
        startPanel.add(clientButton);
        startPanel.add(serverButton);
        dialogPanel.add(startPanel);
        dialogPanel.add(IPPanel);
        dialogPanel.add(portPanel);
        dialogPanel.add(passPanel);
        dialogPanel.add(tabPanel);
        dialogPanel.add(networkButtonPanel);
        rightPanel.add(list);
        rightPanel.add(bootButton);
        rightPanel.add(filePanel);
        rightPanel.add(progressBar);
        rightPanel.add(fileColorExitPanel);
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        frame.add(leftPanel, BorderLayout.CENTER);
        frame.add(rightPanel, BorderLayout.EAST);
        frame.pack();
        frame.setVisible(true);
    }

    public JScrollPane createChatBox() {
        JTextPane cBox = new JTextPane();
        DefaultCaret caret = (DefaultCaret) cBox.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        cBox.setEditable(false);
        cBox.setText("This is where it happens.");
        chatBoxes.add(cBox);
        JScrollPane scrollPane = new JScrollPane(cBox);
        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        return scrollPane;

    }

    //close tab when close image is clicked
    public void removeTab(int i) {
        tabbedPane.remove(i);
    }

    public JTextField getPortField() {
        return portField;
    }

    public JTextField getIPField() {
        return IPField;
    }

    public JTextField getFileField() {
        return fileField;
    }
    
    public JLabel getPortLabel() {
        return portLabel;
    }

    public JLabel getIPLabel() {
        return IPLabel;
    }
    
    public JLabel getEncLabel() {
        return encLabel;
    }
    
    public JLabel getPassLabel() {
        return passLabel;
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public ImageIcon getIcon() {
        return icon;
    }
}
