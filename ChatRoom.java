package chatbox;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

public class ChatRoom {
    
    volatile boolean success = true;
    volatile boolean alive = true;
    volatile boolean speedyDelete = false;
    private final String[] cipherString = {"None", "caesar", "AES"};
    public AESCrypto AES;
    private JPanel mainPanel = new JPanel();
    private JPanel leftPanel = new JPanel();
    private JPanel rightPanel = new JPanel();
    private JButton colorButton = new IconButton("colorIcon.png");
    DefaultStyledDocument doc = new DefaultStyledDocument();
    private JTextPane namePane = new JTextPane();
    private JTextPane messagePane = new JTextPane(doc);
    private JTextPane chatBox = new JTextPane();
    StyleContext context = new StyleContext();
    Style style = context.addStyle("Default Style", null);
    private JButton sendButton = new JButton("Send message");
    private JToggleButton cipherButton = new JToggleButton("Encrypt selected");
    private JLabel cipherLabel = new JLabel("Encryption:");
    private JComboBox cipherBox = new JComboBox(cipherString);
    private JLabel keyLabel = new JLabel("Key:");
    private JTextPane keyPane = new JTextPane();
    private JCheckBox keyBox = new JCheckBox("Send key", true);
    private JCheckBox keyRequestBox = new JCheckBox("Send keyrequest", false);
    int startEnc;
    int endEnc;
    String backup;
    Color colorObj = Color.BLACK;
    String color = Integer.toHexString(Color.BLACK.getRGB()).substring(2);
    String cipherMessage;
    String caesarKey = Integer.toString((int) (Math.random() * 72 + 1));
    int cipherStart;
    int cipherEnd;
    DefaultListModel items = new DefaultListModel();
    private JList list = new SelectionList(items);
    private JScrollPane listPane = new JScrollPane(list);
    private JPanel bootPanel = new JPanel();
    private JButton bootButton = new JButton("Boot selected");
    private JPanel infoPanel = new JPanel();
    private JPanel filePanel = new JPanel();
    private JPanel fileButtonPanel = new JPanel();
    private JButton fileButton = new IconButton("fileIcon.png");
    private JTextPane filePane = new JTextPane();
    private JTextPane fileSizePane = new JTextPane();
    private JTextPane descriptionPane = new JTextPane();
    private JButton sendFileButton = new JButton("Send file to selected");
    private JButton progressBarButton = new JButton("NEW Receive [test!]");
    private JButton closeButton = new IconButton("closeIcon.png");
    private JComboBox fileEncryptions;
    String filePath;
    static final int TYPE_NONE = 0;
    static final int TYPE_CAESAR = 1;
    static final int TYPE_AES = 2;
    HashMap<String, String[]> nameToKey = new HashMap<>();    //String[] is a vector with two components; the first is the Caesar key and the second the AES key
    final String host;
    final int port;
    final boolean isServer;
    PrintWriter o;
    
    volatile boolean statusUpdate = false;
    volatile boolean lockDocument = false;

    public ChatRoom() throws NumberFormatException {
        host = ChatCreator.hostPane.getText();
        port = Integer.parseInt(ChatCreator.portPane.getText());
        isServer = ChatCreator.serverButton.isSelected();
        
        listPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        bootButton.addActionListener(new BootButtonListener(this));
        JLabel infoLabel1 = new JLabel("Host: " + host);
        JLabel infoLabel2 = new JLabel("Port: " + port);
        infoPanel.add(infoLabel1);
        infoPanel.add(infoLabel2);
        bootPanel.add(bootButton);
        bootPanel.setVisible(false);
        rightPanel.add(listPane);
        rightPanel.add(bootPanel);
        rightPanel.add(infoPanel);
        
        fileButton.addActionListener(new FileButtonListener(this));
        fileButton.setBorder(BorderFactory.createEmptyBorder());
        sendFileButton.addActionListener(new SendFileButtonListener(this));
        sendFileButton.setEnabled(false);
        progressBarButton.addActionListener(new ProgressBarButtonListener(this));
        closeButton.setFocusPainted(false);
        
        ((AbstractDocument) filePane.getDocument()).setDocumentFilter(new NewLineFilter(32));
        filePane.addFocusListener(new StatusListener(this));
        filePane.setText("filename.txt");
        ((AbstractDocument) fileSizePane.getDocument()).setDocumentFilter(new NewLineFilter(16));
        fileSizePane.setEditable(false);
        fileSizePane.setText("0");
        ((AbstractDocument) descriptionPane.getDocument()).setDocumentFilter(new NewLineFilter(128));
        descriptionPane.addFocusListener(new StatusListener(this));
        descriptionPane.setText("File description (optional)");
        fileEncryptions = new JComboBox(cipherString);
        
        filePanel.add(fileButton);
        filePanel.add(filePane);
        filePanel.add(fileSizePane);
        filePanel.add(descriptionPane);
        filePanel.add(sendFileButton);
        fileButtonPanel.add(progressBarButton); // temporary - testing only!
        fileButtonPanel.add(new JLabel("Encryption:"));
        fileButtonPanel.add(fileEncryptions);
        fileButtonPanel.add(closeButton);
        rightPanel.add(filePanel);
        rightPanel.add(fileButtonPanel);
        
        try {
            AES = new AESCrypto();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        mainPanel.setFocusable(true);
        mainPanel.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mousePressed(MouseEvent e) {
                JComponent source = (JComponent) e.getSource();
                source.requestFocusInWindow();
            }
        });
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        
        DefaultCaret caret = (DefaultCaret) chatBox.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        chatBox.setEditable(false);
        chatBox.setContentType("text/html");
        xmlHTMLEditorKit kit = new xmlHTMLEditorKit();
        HTMLDocument doc2 = new HTMLDocument();
        chatBox.setEditorKit(kit);
        chatBox.setDocument(doc2);
        chatBox.addKeyListener(new TabListener(this));
        appendToPane(String.format("<message sender=\"INFO\">"
                + "<text color=\"#339966\">This is where it happens.</text></message>"));
        JScrollPane scrollPane = new JScrollPane(chatBox);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        leftPanel.add(scrollPane);
        
        JPanel messagePanel = new JPanel();
        colorButton.setBorder(BorderFactory.createEmptyBorder());
        colorButton.addActionListener(new ColorButtonListener(this));
        ((AbstractDocument) namePane.getDocument()).setDocumentFilter(new NewLineFilter(32));
        String name = ChatCreator.namePane.getText();
        namePane.setText(name.isEmpty() ? "Nomen nescio" : name);
        namePane.addFocusListener(new StatusListener(this));
        ((AbstractDocument) messagePane.getDocument()).setDocumentFilter(new NewLineFilter(256));
        messagePane.setText("In medio cursu vitae nostrae, eram in silva obscura...");
        messagePane.getDocument().addDocumentListener(new MessageDocListener(this));
        messagePane.addFocusListener(new StatusListener(this));
        messagePane.addKeyListener(new MessageListener(this));
        messagePanel.add(colorButton);
        messagePanel.add(namePane);
        messagePanel.add(messagePane);
        messagePanel.add(sendButton);
        leftPanel.add(messagePanel);
        
        JPanel buttonPanel = new JPanel();
        JPanel invisibleContainer1 = new JPanel(new GridLayout(1, 1));
        JPanel invisibleContainer2 = new JPanel(new GridLayout(1, 1));
        JPanel invisibleContainer3 = new JPanel(new GridLayout(1, 1));
        cipherButton.setEnabled(false);
        cipherButton.addActionListener(new CipherButtonListener(this));
        cipherBox.addItemListener(new CipherBoxListener(this));
        keyLabel.setVisible(false);
        keyPane.setVisible(false);
        keyPane.addFocusListener(new StatusListener(this));
        keyBox.setVisible(false);
        buttonPanel.add(keyRequestBox);
        buttonPanel.add(cipherButton);
        buttonPanel.add(cipherLabel);
        buttonPanel.add(cipherBox);
        invisibleContainer1.add(keyLabel);
        invisibleContainer2.add(keyPane);
        invisibleContainer3.add(keyBox);
        buttonPanel.add(invisibleContainer1);
        buttonPanel.add(invisibleContainer2);
        buttonPanel.add(invisibleContainer3);
        leftPanel.add(buttonPanel);
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s)", namePane.getText(), host);
    }
    
    public String getName() {
        return namePane.getText();
    }
    
    public String getKey(String type) {
        switch (type) {
            case "caesar":
                return caesarKey;
            case "AES":
                return AES.getDecodeKey();
            default:
                return null;
        }
    }
    
    public JPanel getMainPanel() {
        return mainPanel;
    }
    
    public JTextPane getMessagePane() {
        return messagePane;
    }
    
    public JButton getSendButton() {
        return sendButton;
    }
    
    public JList getList() {
        return list;
    }
    
    public JToggleButton getCipherButton() {
        return cipherButton;
    }
    
    public JTextPane getKeyPane() {
        return keyPane;
    }
    
    public JLabel getKeyLabel() {
        return keyLabel;
    }
    
    public JCheckBox getKeyBox() {
        return keyBox;
    }
    
    public JCheckBox getKeyRequestBox() {
        return keyRequestBox;
    }
    
    public JComboBox getCipherBox() {
        return cipherBox;
    }
    
    public JButton getCloseButton() {
        return closeButton;
    }
    
    public JButton getSendFileButton() {
        return sendFileButton;
    }
    
    public JTextPane getNamePane() {
        return namePane;
    }
    
    public JTextPane getDescriptionPane() {
        return descriptionPane;
    }
    
    public JTextPane getFilePane() {
        return filePane;
    }
    
    public JTextPane getFileSizePane() {
        return fileSizePane;
    }
    
    public JPanel getBootPanel() {
        return bootPanel;
    }
    
    public JTextPane getChatBox() {
        return chatBox;
    }

    private void disableChat() {
        // disable more fields!!
        namePane.setEnabled(false);
        messagePane.setEnabled(false);
        sendButton.setEnabled(false);
        ChatCreator.showError("You have been booted!"); //not an error, but info
    }

    // Inspirerat av http://stackoverflow.com/questions/9650992/how-to-change-text-color-in-the-jtextarea?lq=1
    public final void appendToPane(String msg) {
        
        try {
            xmlHTMLEditorKit kit = (xmlHTMLEditorKit) chatBox.getEditorKit();
            HTMLDocument doc1 = (HTMLDocument) chatBox.getDocument();
            try {
                kit.insertHTML(doc1.getLength(), msg, 0, 0, null, this);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
        } catch (BadLocationException e) {
            ChatCreator.showError("String insertion failed.");
        }
    }
}