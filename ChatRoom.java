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
    JPanel mainPanel = new JPanel();
    JPanel leftPanel = new JPanel();
    JPanel rightPanel = new JPanel();
    JButton colorButton = new IconButton("colorIcon.png");
    DefaultStyledDocument doc = new DefaultStyledDocument();
    JTextPane namePane = new JTextPane();
    JTextPane messagePane = new JTextPane(doc);
    JTextPane chatBox = new JTextPane();
    StyleContext context = new StyleContext();
    Style style = context.addStyle("Default Style", null);
    JButton sendButton = new JButton("Send message");
    JToggleButton cipherButton = new JToggleButton("Encrypt selected");
    JLabel cipherLabel = new JLabel("Encryption:");
    JComboBox cipherBox = new JComboBox(cipherString);
    JLabel keyLabel = new JLabel("Key:");
    JTextPane keyPane = new JTextPane();
    JCheckBox keyBox = new JCheckBox("Send key", true);
    JCheckBox keyRequestBox = new JCheckBox("Send keyrequest", false);
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
    JList list = new SelectionList(items);
    JScrollPane listPane = new JScrollPane(list);
    JPanel bootPanel = new JPanel();
    JButton bootButton = new JButton("Boot selected");
    JPanel infoPanel = new JPanel();
    JPanel filePanel = new JPanel();
    JPanel fileButtonPanel = new JPanel();
    JButton fileButton = new IconButton("fileIcon.png");
    JTextPane filePane = new JTextPane();
    JTextPane fileSizePane = new JTextPane();
    JTextPane descriptionPane = new JTextPane();
    JButton sendFileButton = new JButton("Send file to selected");
    JButton progressBarButton = new JButton("NEW Receive [test!]");
    JButton closeButton = new IconButton("closeIcon.png");
    JComboBox fileEncryptions;
    private String filePath;
    static final int TYPE_NONE = 0;
    static final int TYPE_CAESAR = 1;
    static final int TYPE_AES = 2;
    HashMap<String, ArrayList<String>> nameToKey = new HashMap<>();
    
    final String host;
    final int port;
    final boolean isServer;
    
    boolean statusUpdate = false;

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

        fileButton.setBorder(BorderFactory.createEmptyBorder());
        sendFileButton.setEnabled(false);
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

        filePane.addFocusListener(new StatusListener(this));
        descriptionPane.addFocusListener(new StatusListener(this));
        sendFileButton.addActionListener(new SendFileButtonListener());
        progressBarButton.addActionListener(new ProgressBarButtonListener(this));
        fileButton.addActionListener(new FileButtonListener());

        try {
            AES = new AESCrypto();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

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
        colorButton.addActionListener(new ColorButtonListener());
        ((AbstractDocument) namePane.getDocument()).setDocumentFilter(new NewLineFilter(32));
        String name = ChatCreator.namePane.getText();
        namePane.setText(name.isEmpty() ? "Nomen nescio" : name);
        namePane.addFocusListener(new StatusListener(this));
        ((AbstractDocument) messagePane.getDocument()).setDocumentFilter(new NewLineFilter(256));
        messagePane.setText("In medio cursu vitae nostrae, eram in silva obscura...");
        messagePane.addFocusListener(new StatusListener(this));
        messagePane.addKeyListener(new MessageListener());
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

    public String getIP() {
        return host;
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


    public void toggleType(int type) {
        cipherButton.setEnabled(type != TYPE_NONE);
        keyLabel.setVisible(type != TYPE_NONE);
        keyPane.setVisible(type != TYPE_NONE);
        keyPane.setEditable(type != TYPE_AES);
        keyBox.setVisible(type != TYPE_NONE);
    }

    public void disableChat() {
        // disable more!!
        namePane.setEnabled(false);
        messagePane.setEnabled(false);
        sendButton.setEnabled(false);
        ChatCreator.showError("You have been booted!"); //not an error, but info
    }

    // Välj bakgrundsfärg
    class ColorButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Color newColor = JColorChooser.showDialog(ChatCreator.frame,
                    "Choose text color", ChatCreator.frame.getBackground());
            if (newColor != null) {
                colorObj = newColor;
                color = Integer.toHexString(colorObj.getRGB()).substring(2);
                namePane.setForeground(colorObj);
                String message = messagePane.getText();
                StyleConstants.setForeground(style, colorObj);
                try {
                    //hacklösning... ersätt med adapter.o.println(text)!
                    statusUpdate = true;
                    doc.remove(0, message.length());
                    doc.insertString(0, "I just changed to a new color: " + color, style);
                    sendButton.doClick();
                    doc.insertString(0, message, style);
                    if (cipherButton.isSelected()) {
                        String text = message.substring(cipherStart, cipherEnd);
                        StyleConstants.setBackground(style, colorObj);
                        doc.remove(cipherStart, cipherEnd - cipherStart);
                        doc.insertString(cipherStart, text, style);
                        StyleConstants.setBackground(style, Color.WHITE);
                    }
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                } finally {
                    statusUpdate = false;
                }
            }
        }
    }

    public class FileButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();

            int returnVal = chooser.showOpenDialog(ChatCreator.frame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                filePath = file.getAbsolutePath();
                filePane.setText(file.getName());
                fileSizePane.setText(Long.toString(file.length()) + " bytes");
                sendFileButton.setEnabled(true);
            }
        }
    }

    class MessageListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                sendButton.doClick();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    // Inspirerat av http://stackoverflow.com/questions/9650992/how-to-change-text-color-in-the-jtextarea?lq=1
    public final void appendToPane(String msg) {

        try {
            xmlHTMLEditorKit kit = (xmlHTMLEditorKit) chatBox.getEditorKit();
            HTMLDocument doc1 = (HTMLDocument) chatBox.getDocument();
            try {
                kit.insertHTML(doc1.getLength(), msg, 0, 0, null, doc1);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } catch (BadLocationException e) {
            ChatCreator.showError("String insertion failed.");
        }

    }

    // Skicka fil med klient
    public class SendFileButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String description = descriptionPane.getText();
            if ("File description (optional)".equals(description)) {
                description = "No description";
            }
            String fileData = String.format("File name: %s\nFile size: %s\n"
                    + "File description: %s\nAccept file?", filePane.getText(),
                    fileSizePane.getText(), description);
            String message = messagePane.getText();

            appendToPane(String.format("<message sender=\"%s\"><filerequest namn=\"%s\" size=\"%s\">%s</filerequest></message>",
                    namePane.getText(), filePane.getText(), fileSizePane.getText(), description));
            try {
                doc.remove(0, message.length());
                doc.insertString(0, "Filerequest: " + fileData, style);
                sendButton.doClick();
                doc.insertString(0, message, style);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
            /*
             try {
             FileSender thr = new FileSender(chatCreator.hostPane.getText(),
             Integer.parseInt(chatCreator.portPane.getText()),
             filePane.getText());
             thr.start();
             } catch (Exception ex) {
             System.err.println("Ett fel inträffade2: " + ex);
             }
             */
        }
    }
}