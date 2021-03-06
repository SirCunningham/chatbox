package chatbox;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.*;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.HTMLDocument;

public class ChatRoom {

    static final int TYPE_NONE = 0;
    static final int TYPE_CAESAR = 1;
    static final int TYPE_AES = 2;
    static final String[] cipherString = {"None", "caesar", "AES"};
    private final JPanel mainPanel = new JPanel();
    private final LeftPanel leftPanel;
    private final RightPanel rightPanel;
    private final DefaultListModel items = new DefaultListModel();
    private final JList list = new SelectionList(items);
    volatile boolean success = true;
    volatile boolean alive = true;
    volatile boolean speedyDelete = false;
    volatile boolean statusUpdate = false;
    volatile boolean lockDocument = false;
    
    static final int NO_FILE = 0;
    static final int PROPOSED_FILE = 1;
    static final int ACCEPTED_FILE = 2;
    volatile int fileAcceptance = NO_FILE;
    
    JTextPane chatBox = new JTextPane();
    DefaultStyledDocument doc = new DefaultStyledDocument();
    public static AESCrypto AES;
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
    int fileSize;
    File savePath;
    BufferedWriter bw;

    HashMap<String, String[]> nameToKey = new HashMap<>();      //String[] is a vector with two components; the first is the Caesar key and the second the AES key
    HashMap<String, ScheduledExecutorService> nameFileResponse = new HashMap<>();
    HashMap<String, ScheduledExecutorService> nameKeyResponse = new HashMap<>();
    HashMap<String, Boolean> receivedFileResponse = new HashMap<>();
    HashMap<String, Boolean> recivedKeyResponse = new HashMap<>();
    HashMap<String, Boolean> isAllowedToConnect = new HashMap<>();
    final String host;
    final int port;
    final boolean isServer;
    PrintWriter o;
    Server server;
    Object lock = new Object();
    
    String serverName = null;
    
    public ChatRoom() throws NumberFormatException {
        host = ChatCreator.hostPane.getText();
        port = Integer.parseInt(ChatCreator.portPane.getText());
        isServer = ChatCreator.serverButton.isSelected();

        leftPanel = new LeftPanel(this);
        rightPanel = new RightPanel(this);

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
        nameToKey.put(getName(), getKeys());
    }

    // Inspirerat av http://stackoverflow.com/questions/9650992/how-to-change-text-color-in-the-jtextarea?lq=1
    public final void appendToPane(String msg) {

        try {
            xmlHTMLEditorKit kit = (xmlHTMLEditorKit) chatBox.getEditorKit();
            HTMLDocument doc1 = (HTMLDocument) chatBox.getDocument();
            try {
                if (getKeys() != null) {
                    kit.insertHTML(this, doc1.getLength(), msg, 0, 0, null);
                    chatBox.setCaretPosition(chatBox.getDocument().getLength());
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } catch (BadLocationException e) {
            ChatCreator.showError("String insertion failed.");
        }
    }

    //skicka <disconnect> med servern till en klient som blir kickad och anropa detta!!
    private void disableChat() {
        //disable more fields!!
        getNamePane().setEnabled(false);
        getMessagePane().setEnabled(false);
        getSendButton().setEnabled(false);
        ChatCreator.showError("You have been booted!"); //not an error, but info
    }

    public void setServer(Server server) {
        if (isServer) {
            this.server = server;
        }
    }

    public Socket getClientSocket(String chatName) {
        if (isServer) {
            return server.getClientSocket(chatName);
        }
        return null;
    }

    public void kickUser(String chatName) throws IOException {
        if (isServer) {
            //server.getOutputStream(chatName).close();
            //server.getClientSocket(chatName).getInputStream().close();
            //server.getClientSocket(chatName).getOutputStream().close();
            //server.getClientSocket(chatName).close();
            //server.getInputStream(chatName).close();
            //alive = false;
        }
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

    public String[] getKeys() {
        if (AES != null) {
            String keys[] = {caesarKey, AES.getDecodeKey()};
            return keys;
        }
        String keys[] = {caesarKey, ""};
        return keys;
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

    public JButton getBootButton() {
        return rightPanel.getBootButton();
    }

    public JButton getKeyRequestButton() {
        return rightPanel.getKeyRequestButton();
    }

    public JButton getFileButton() {
        return rightPanel.getFileButton();
    }

    public String getKeyRequestEncryption() {
        return rightPanel.getKeyRequestEncryption();
    }

    public JButton getProgressBarButton() {
        return rightPanel.getProgressBarButton();
    }
}
