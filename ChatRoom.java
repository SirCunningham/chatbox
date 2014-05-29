package chatbox;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import javax.swing.text.*;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.text.html.HTMLDocument;

public class ChatRoom {
    
    static final String[] cipherString = {"None", "caesar", "AES"};
    
    private DefaultListModel items = new DefaultListModel();
    private JList list = new SelectionList(items);
    
    JTextPane chatBox = new JTextPane();
    DefaultStyledDocument doc = new DefaultStyledDocument();
    
    volatile boolean success = true;
    volatile boolean alive = true;
    volatile boolean speedyDelete = false;
    public AESCrypto AES;
    private JPanel mainPanel = new JPanel();
    private LeftPanel leftPanel = new LeftPanel(this);
    private RightPanel rightPanel = new RightPanel(this);
    StyleContext context = new StyleContext();
    Style style = context.addStyle("Default Style", null);
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
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
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
    
    private void disableChat() {
        // disable more fields!!
        getNamePane().setEnabled(false);
        getMessagePane().setEnabled(false);
        getSendButton().setEnabled(false);
        ChatCreator.showError("You have been booted!"); //not an error, but info
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s)", getNamePane().getText(), host);
    }
    
    public String getName() {
        return getNamePane().getText();
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
        return leftPanel.getMessagePane();
    }
    
    public JButton getSendButton() {
        return leftPanel.getSendButton();
    }
    
    public JList getList() {
        return list;
    }
    
    public DefaultListModel getItems() {
        return items;
    }
    
    public JToggleButton getCipherButton() {
        return leftPanel.getCipherButton();
    }
    
    public JTextPane getKeyPane() {
        return leftPanel.getKeyPane();
    }
    
    public JLabel getKeyLabel() {
        return leftPanel.getKeyLabel();
    }
    
    public JCheckBox getKeyBox() {
        return leftPanel.getKeyBox();
    }
    
    public JCheckBox getKeyRequestBox() {
        return leftPanel.getKeyRequestBox();
    }
    
    public JComboBox getCipherBox() {
        return leftPanel.getCipherBox();
    }
    
    public JButton getCloseButton() {
        return rightPanel.getCloseButton();
    }
    
    public JButton getSendFileButton() {
        return rightPanel.getSendFileButton();
    }
    
    public JTextPane getNamePane() {
        return leftPanel.getNamePane();
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

}