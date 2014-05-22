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
import java.util.*;
import java.util.concurrent.*;

public class ChatRoom {
    
    static final String[] cipherString = {"None", "caesar", "AES"};
    
    private DefaultListModel items = new DefaultListModel();
    private JList list = new SelectionList(items);
    
    volatile boolean success = true;
    volatile boolean alive = true;
    volatile boolean speedyDelete = false;
    public AESCrypto AES;
    private JPanel mainPanel = new JPanel();
    private JPanel leftPanel = new JPanel();
    private RightPanel rightPanel = new RightPanel(this);
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
    String filePath;
    static final int TYPE_NONE = 0;
    static final int TYPE_CAESAR = 1;
    static final int TYPE_AES = 2;
    HashMap<String, String[]> nameToKey = new HashMap<>();      //String[] is a vector with two components; the first is the Caesar key and the second the AES key
    HashMap<String, ScheduledExecutorService> ipFileResponse = new HashMap<>(); 
    HashMap<String, Boolean> recivedFileResponse = new HashMap<>(); 
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
    
    public DefaultListModel getItems() {
        return items;
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
        return rightPanel.getCloseButton();
    }
    
    public JButton getSendFileButton() {
        return rightPanel.getSendFileButton();
    }
    
    public JTextPane getNamePane() {
        return namePane;
    }
    
    public JTextPane getDescriptionPane() {
        return rightPanel.getDescriptionPane();
    }
    
    public JTextPane getFilePane() {
        return rightPanel.getFilePane();
    }
    
    public JTextPane getFileSizePane() {
        return rightPanel.getFileSizePane();
    }
    
    public JPanel getBootPanel() {
        return rightPanel.getBootPanel();
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