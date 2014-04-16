package chatbox;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

final class ChatRoom {

    volatile boolean success = true;
    volatile boolean alive = true;
    private final String[] cipherString = {"None", "caesar", "AES"};
    public ChatCreator chatCreator;
    public AESCrypto AES;
    JPanel mainPanel = new JPanel();
    JPanel leftPanel = new JPanel();
    JPanel rightPanel = new JPanel();
    IconButton colorButton = new IconButton("colorIcon.png");
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
    JList list = new JList(items);
    JScrollPane listPane = new JScrollPane(list);
    JPanel bootPanel = new JPanel();
    JButton bootButton = new JButton("Boot selected");
    JPanel infoPanel = new JPanel();
    JPanel filePanel = new JPanel();
    JPanel fileButtonPanel = new JPanel();
    IconButton fileButton = new IconButton("fileIcon.png");
    JTextPane filePane = new JTextPane();
    JTextPane fileSizePane = new JTextPane();
    JTextPane descriptionPane = new JTextPane();
    JButton sendFileButton = new JButton("Send file to selected");
    JButton receiveFileButton = new JButton("Receive [test!]");
    IconButton closeButton = new IconButton("closeIcon.png");
    JProgressBar progressBar = new JProgressBar();
    JComboBox fileEncryptions;
    private String filePath;
    private static final int TYPE_NONE = 0;
    private static final int TYPE_CAESAR = 1;
    private static final int TYPE_AES = 2;
    HashMap<String, ArrayList<String>> nameToKey = new HashMap<>();

    public ChatRoom(ChatCreator chatCreator) {
        list.setSelectionModel(new DefaultListSelectionModel() {

            private static final long serialVersionUID = 1L;
            boolean gestureStarted = false;

            @Override
            public void setSelectionInterval(int index0, int index1) {
                if (!gestureStarted) {
                    if (isSelectedIndex(index0)) {
                        super.removeSelectionInterval(index0, index1);
                    } else {
                        super.addSelectionInterval(index0, index1);
                    }
                }
                gestureStarted = true;
            }

            @Override
            public void setValueIsAdjusting(boolean isAdjusting) {
                if (isAdjusting == false) {
                    gestureStarted = false;
                }
            }
        });
        listPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        bootButton.addActionListener(new BootButtonListener());
        JLabel infoLabel1 = new JLabel("Host: " + chatCreator.hostPane.getText());
        JLabel infoLabel2 = new JLabel("Port: " + chatCreator.portPane.getText());
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
        filePane.addFocusListener(new FieldListener());
        filePane.setText("filename.txt");
        ((AbstractDocument) fileSizePane.getDocument()).setDocumentFilter(new NewLineFilter(16));
        fileSizePane.setEditable(false);
        fileSizePane.setText("0");
        ((AbstractDocument) descriptionPane.getDocument()).setDocumentFilter(new NewLineFilter(128));
        descriptionPane.addFocusListener(new FieldListener());
        descriptionPane.setText("File description (optional)");
        fileEncryptions = new JComboBox(cipherString);

        filePanel.add(fileButton);
        filePanel.add(filePane);
        filePanel.add(fileSizePane);
        filePanel.add(descriptionPane);
        filePanel.add(sendFileButton);
        fileButtonPanel.add(receiveFileButton); //Testing only!
        fileButtonPanel.add(new JLabel("Encryption:"));
        fileButtonPanel.add(fileEncryptions);
        fileButtonPanel.add(closeButton);
        rightPanel.add(filePanel);
        rightPanel.add(fileButtonPanel);
        rightPanel.add(progressBar); //Temporary only - add to dialog!

        filePane.addFocusListener(new FieldListener());
        descriptionPane.addFocusListener(new FieldListener());
        sendFileButton.addActionListener(new SendFileButtonListener());
        receiveFileButton.addActionListener(new ReceiveFileButtonListener());
        fileButton.addActionListener(new FileButtonListener());

        this.chatCreator = chatCreator;
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
        chatBox.addKeyListener(new TabListener());
        appendToPane(String.format("<message sender=\"INFO\">"
                            + "<text color=\"#339966\">This is where it happens.</text></message>"));
        JScrollPane scrollPane = new JScrollPane(chatBox);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        leftPanel.add(scrollPane);

        JPanel messagePanel = new JPanel();
        colorButton.setBorder(BorderFactory.createEmptyBorder());
        colorButton.addActionListener(new ColorButtonListener());
        ((AbstractDocument) namePane.getDocument()).setDocumentFilter(new NewLineFilter(32));
        String name = chatCreator.namePane.getText();
        namePane.setText(name.isEmpty() ? "Nomen nescio" : name);
        namePane.addFocusListener(new FieldListener());
        ((AbstractDocument) messagePane.getDocument()).setDocumentFilter(new NewLineFilter(256));
        messagePane.setText("In medio cursu vitae nostrae, eram in silva obscura...");
        messagePane.addFocusListener(new FieldListener());
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
        cipherButton.addActionListener(new CipherButtonListener());
        cipherBox.addItemListener(new CipherBoxListener());
        keyLabel.setVisible(false);
        keyPane.setVisible(false);
        keyPane.addFocusListener(new FieldListener());
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
        return String.format("%s (%s)",namePane.getText(), chatCreator.hostPane.getText());
    }
    public String getName() {
        return namePane.getText();
    }
    public String getIP() {
        return chatCreator.hostPane.getText();
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
    
    public void showError(String text) {
        JOptionPane.showMessageDialog(chatCreator.frame, text, "Error message",
                JOptionPane.ERROR_MESSAGE);
    }

    public String encryptCaesar(String text, int shift) throws UnsupportedEncodingException {
        char[] chars = text.toCharArray();
        for (int i = 0; i < text.length(); i++) {
            char c = chars[i];
            //Tag inte med control characters
            if (c >= 32 && c <= 127) {
                int x = c - 32;
                x = (x + shift) % 96;
                if (x < 0) {
                    x += 96;
                }
                chars[i] = (char) (x + 32);
            }
        }
        //stackoverflow.com/questions/923863/converting-a-string-to-hexadecimal-in-java
        String msg = new String(chars);
        return String.format("%x", new BigInteger(1, msg.getBytes("UTF-8"))).toUpperCase();  //UTF-8 krav, men då fungerar inte åäö
    }

    public void toggleType(int type) {
        cipherButton.setEnabled(type != TYPE_NONE);
        keyLabel.setVisible(type != TYPE_NONE);
        keyPane.setVisible(type != TYPE_NONE);
        keyPane.setEditable(type != TYPE_AES);
        keyBox.setVisible(type != TYPE_NONE);
    }

    class BootButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String message = messagePane.getText();
            int i = list.getSelectedIndex();
            try {
                doc.remove(0, message.length());
                ChatRoom msgBox = (ChatRoom) items.getElementAt(i);
                doc.insertString(0, 
                        String.format("%s got the boot",
                        msgBox.getName()), style);
                sendButton.doClick();                  //Do something else if no connection, or make it work solo!
                doc.insertString(0, message, style);
                msgBox.alive = false;                  //Döda klienten
                for (ChatRoom mBox : chatCreator.messageBoxes) {
                    mBox.items.removeElement(msgBox);
                }
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
            while (i >= 0) {
                i = list.getSelectedIndex();
            }
        }
    }

    // Välj krypteringssystem
    class CipherBoxListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            String chosen = String.valueOf(cipherBox.getSelectedItem());
            switch (chosen) {
                case "caesar":
                    ((AbstractDocument) keyPane.getDocument()).setDocumentFilter(new NewLineFilter(8, false));
                    toggleType(TYPE_CAESAR);
                    keyPane.setText(caesarKey);
                    break;
                case "AES":
                    ((AbstractDocument) keyPane.getDocument()).setDocumentFilter(new NewLineFilter(128));
                    toggleType(TYPE_AES);
                    keyPane.setText(AES.getDecodeKey());
                    break;
                default:
                    if (cipherButton.isSelected()) {
                        cipherButton.doClick();
                    }
                    toggleType(TYPE_NONE);
            }
        }
    }

    class CipherButtonListener implements ActionListener {

        private String encrypt(String type, String text, String key) {
            switch (type) {
                case "caesar":
                    try {
                        return encryptCaesar(text, Integer.valueOf(key));
                    } catch (UnsupportedEncodingException ex) {
                        ex.printStackTrace();
                    }
                case "AES":
                    try {
                        return AES.encrypt(text);
                    } catch (NoSuchAlgorithmException | InvalidKeyException |
                            UnsupportedEncodingException |
                            IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException ex) {
                        ex.printStackTrace();
                    }
                    break;
            }
            return null;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (cipherButton.isSelected()) {
                if (cipherStart < cipherEnd) {
                    String getText = messagePane.getText();
                    String text = getText.substring(cipherStart, cipherEnd);
                    String type = String.valueOf(cipherBox.getSelectedItem());
                    String key = keyPane.getText();
                    String keyString = "";
                    if (keyBox.isSelected()) {
                        keyString = String.format(" key=\"%s\"", key);
                    }
                    try {
                        cipherMessage = String.format("%s<encrypted type="
                                + "\"%s\"%s>%s</encrypted>%s",
                                XMLString.convertAngle(getText.substring(0,
                                cipherStart)), type, keyString,
                                XMLString.convertAngle(encrypt(type, text, key)), XMLString.convertAngle(
                                getText.substring(cipherEnd)));
                        StyleConstants.setBackground(style, colorObj);
                        doc.remove(cipherStart, cipherEnd - cipherStart);
                        doc.insertString(cipherStart, text, style);
                        StyleConstants.setBackground(style, Color.WHITE);
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    cipherButton.doClick();
                }
            } else {
                String text = messagePane.getText().substring(cipherStart,
                        cipherEnd);
                try {
                    doc.remove(cipherStart, cipherEnd - cipherStart);
                    doc.insertString(cipherStart, text, style);
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    // Välj bakgrundsfärg
    class ColorButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            Color newColor = JColorChooser.showDialog(chatCreator.frame,
                    "Choose text color", chatCreator.frame.getBackground());
            if (newColor != null) {
                colorObj = newColor;
                color = Integer.toHexString(colorObj.getRGB()).substring(2);
                namePane.setForeground(colorObj);
                String message = messagePane.getText();
                StyleConstants.setForeground(style, colorObj);
                try {
                    doc.remove(0, message.length());
                    doc.insertString(0, "I just changed to a new color: " + color, style);
                    sendButton.doClick(); //do something else if no connection, or make it work solo!
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
                }
            }
        }
    }

    // Markera textrutor
    class FieldListener implements FocusListener {

        private String name;

        @Override
        public void focusGained(FocusEvent e) {
            JTextComponent source = (JTextComponent) e.getSource();
            if (source == namePane) {
                name = namePane.getText();
            }
            source.selectAll();
        }

        @Override
        public void focusLost(FocusEvent e) {
            JTextComponent source = (JTextComponent) e.getSource();
            if (source == messagePane && !cipherButton.isSelected()) {
                cipherStart = source.getSelectionStart();
                cipherEnd = source.getSelectionEnd();
            } else if (source == namePane && !name.equals(namePane.getText())) {
                if (namePane.getText().isEmpty()) {
                    namePane.setText("Nomen nescio");
                    if (name.equals("Nomen nescio")) {
                        return;
                    }
                }
                String message = messagePane.getText();
                try {
                    doc.remove(0, message.length());
                    doc.insertString(0, "I just switched from my old name: " + name, style);
                    sendButton.doClick(); //do something else if no connection, or make it work solo!
                    doc.insertString(0, message, style);
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
                source.select(0, 0);
            } else if (source == keyPane) {
                source.setText(source.getText());
            }
        }
    }

    public class FileButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();

            int returnVal = chooser.showOpenDialog(chatCreator.frame);
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
    
    class TabListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int index = chatCreator.tabbedPane.getSelectedIndex();
            switch (e.getKeyCode()) {
                case KeyEvent.VK_ENTER:
                    sendButton.doClick();
                    break;
                case KeyEvent.VK_A:
                case KeyEvent.VK_KP_LEFT:
                case KeyEvent.VK_LEFT:
                    if (index > 0) {
                        chatCreator.tabbedPane.setSelectedIndex(index - 1);
                    }
                    break;
                case KeyEvent.VK_D:
                case KeyEvent.VK_KP_RIGHT:
                case KeyEvent.VK_RIGHT:
                    chatCreator.tabbedPane.setSelectedIndex(index + 1);
                    break;
                default:
                    break;
            }
            messagePane.requestFocusInWindow();
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    // Mottag fil med server
    public class ReceiveFileButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int reply = JOptionPane.showConfirmDialog(null, "<fileData>\nAccept file?",
                    "File request", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(null, "Good for you!");
            } else {
                JOptionPane.showMessageDialog(null, "Your loss!");
            }
            /*
            try {
            FileReceiver thr = new FileReceiver(Integer.parseInt(
            chatCreator.portPane.getText()), filePane.getText());
            thr.start();
            } catch (Exception ex) {
            System.err.println("Ett fel inträffade4: " + ex);
            }
             */
        }
    }

    // Inspirerat av http://stackoverflow.com/questions/9650992/how-to-change-text-color-in-the-jtextarea?lq=1
    public void appendToPane(String msg) {

        try {
            xmlHTMLEditorKit kit = (ChatRoom.xmlHTMLEditorKit) chatBox.getEditorKit();
            HTMLDocument doc1 = (HTMLDocument) chatBox.getDocument();
            try {
                kit.insertHTML(doc1.getLength(), msg, 0, 0, null);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } catch (BadLocationException e) {
            showError("String insertion failed.");
        }

    }

    public String getFileMessage() {
        String description = descriptionPane.getText();
        if ("File description (optional)".equals(description)) {
            description = "No description";
        }
        String fileData = String.format("File name: %s\nFile size: %s\n"
                + "File description: %s\nAccept file?", filePane.getText(),
                fileSizePane.getText(), description);
        String message = messagePane.getText();
        /*
        appendToPane(String.format("<message sender=\"%s\"><filerequest namn=\"%s\" size=\"%s\">%s</filerequest></message>",
        namePane.getText(), filePane.getText(), fileSizePane.getText(), description));
         * 
         */
        return String.format("<message sender=\"%s\"><filerequest namn=\"%s\" size=\"%s\">%s</filerequest></message>",
                namePane.getText(), filePane.getText(), fileSizePane.getText(), description);
    }
    
    public String getQuitMessage() {
        return String.format("<message sender=\"%s\"><text color=\"%s\">I just left.</text><disconnect /></message>", namePane.getText(),color);
    }

    public String getMessage() {
        try {
            String message;
            if (cipherButton.isSelected()) {
                message = cipherMessage;
                cipherButton.doClick();
            } else {
                message = XMLString.convertAngle(messagePane.getText());
            }
            messagePane.setText("");
            String name = namePane.getText();
            if (!message.isEmpty()) {
                if (!keyRequestBox.isSelected()) {
                    return String.format("<message sender=\"%s\">"
                            + "<text color=\"%s\">%s</text></message>",
                            name, color,
                            message);
                }
                return String.format("<message sender=\"%s\">"
                        + "<text color=\"%s\"><keyrequest "
                        + "type=\"%s\">%s"
                        + "</keyrequest></text></message>",
                        name, color,
                        String.valueOf(cipherBox.getSelectedItem()),message);
                /*
                timer.setType(String.valueOf(chatRoom.cipherBox.getSelectedItem()));
                timer.start();
                 * 
                 */


                /*
                appendToPane(String.format("<message sender=\"%s\">"
                + "<text color=\"%s\">%s</text></message>",
                name, chatRoom.color,
                message));
                 * 
                 */
            }
            /*
            if (message.contains("terminate my ass")) {
            appendToPane("<message sender=\"INFO\">"
            + "<text color=\"0000FF\">Med huvudet före!!!<disconnect /></text></message>");
            //kill();
            }
             * 
             */
        } catch (Exception ex) {
            appendToPane(String.format("<message sender=\"ERROR\">"
                    + "<text color=\"#ff0000\">Output stream failed</text></message>"));
        }
        System.out.println("Shouldn't be here!");
        return "";
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
                //denna knapp blev disabled någon gång - lokalisera detta fel!
                sendButton.doClick(); //do something else if no connection, or make it work solo!
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

    public class xmlHTMLEditorKit extends HTMLEditorKit {
        //Måste förbättras

        public void insertHTML(int offset, String html,
                int popDepth, int pushDepth, HTML.Tag insertTag) throws
                BadLocationException, IOException {
            String color = XMLString.toHexColor(html);
            //thr.keyRequest(html);

            super.insertHTML((HTMLDocument) chatBox.getDocument(),
                    offset, "<font color=\"" + color + "\">" + XMLString.showName(html)
                    + "</font>", popDepth, pushDepth, insertTag);
            /*else {
            super.insertHTML((HTMLDocument) chatBox.getDocument(),
            offset, "Något blev fel i meddelandet", popDepth, pushDepth, insertTag);
            }
            /*
            if (thr.timer.isRunning()) {
            System.out.println(thr.timer.getType());
            System.out.println(html);
            System.out.println(XMLString.getEncryptedType(html));
            if (thr.timer.getType().equals(XMLString.getEncryptedType(html))) {
            thr.timer.stop();
            chatRoom.nameToKey.put(XMLString.getSender(html), new ArrayList<String>());
            }
            }
             * 
             */
            
        }
    }
}
