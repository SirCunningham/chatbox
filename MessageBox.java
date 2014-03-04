package chatbox;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

class MessageBox extends JPanel {

    private String[] cipherString = {"None", "caesar", "AES"};
    private View view;
    private AESCrypto AES;
    IconButton colorButton = new IconButton("colorIcon.png");
    JTextPane namePane = new JTextPane();
    JTextPane messagePane = new JTextPane();
    StyledDocument doc = messagePane.getStyledDocument();
    Style style = messagePane.addStyle("Default Style", null);
    JButton sendButton = new JButton("Send message");
    JToggleButton cipherButton = new JToggleButton("Encrypt selected");
    JLabel cipherLabel = new JLabel("Cryptosystem:");
    JComboBox cipherBox = new JComboBox(cipherString);
    JLabel keyLabel = new JLabel("Key:");
    JTextField keyField = new JTextField("68", 5);
    JCheckBox keyBox = new JCheckBox("Send key", true);
    JCheckBox keyRequestBox = new JCheckBox("Send keyrequest", false);
    int startEnc;
    int endEnc;
    String backup;
    Color colorObj = Color.BLACK;
    String color = Integer.toHexString(Color.BLACK.getRGB()).substring(2);
    String cipherMessage;
    int cipherStart;
    int cipherEnd;
    
    private static final int TYPE_NONE = 0;
    private static final int TYPE_CAESAR = 1;
    private static final int TYPE_AES = 2;
    
    public MessageBox(View view) {
        this.view = view;
        try {
            AES = new AESCrypto();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        JTextPane cBox = new JTextPane();
        DefaultCaret caret = (DefaultCaret) cBox.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        cBox.setEditable(false);
        cBox.setContentType("text/html");
        HTMLEditorKit kit = new HTMLEditorKit();
        HTMLDocument doc = new HTMLDocument();
        
        cBox.setEditorKit(kit);
        cBox.setDocument(doc);
        cBox.setText("This is where it happens.\n1\n2\n3\n4");
        JScrollPane scrollPane = new JScrollPane(cBox);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane);

        JPanel messagePanel = new JPanel();
        colorButton.setBorder(BorderFactory.createEmptyBorder());
        colorButton.addActionListener(new ColorButtonListener());
        namePane.addFocusListener(new FieldListener());
        namePane.setText("Dante");
        ((AbstractDocument) namePane.getDocument()).setDocumentFilter(new NewLineFilter(32));
        messagePane.setText("In medio cursu vitae nostrae, eram in silva obscura...");
        ((AbstractDocument) messagePane.getDocument()).setDocumentFilter(new NewLineFilter(256));
        messagePane.addFocusListener(new FieldListener());
        messagePane.addKeyListener(new MessageListener());
        messagePanel.add(colorButton);
        messagePanel.add(namePane);
        messagePanel.add(messagePane);
        add(messagePanel);

        JPanel buttonPanel = new JPanel();
        cipherButton.setEnabled(false);
        cipherButton.addActionListener(new CipherButtonListener());
        cipherBox.addItemListener(new CipherBoxListener());
        keyLabel.setVisible(false);
        keyField.setVisible(false);
        keyField.addFocusListener(new FieldListener());
        keyBox.setVisible(false);
        keyRequestBox.setVisible(false);
        buttonPanel.add(sendButton);
        buttonPanel.add(cipherButton);
        buttonPanel.add(cipherLabel);
        buttonPanel.add(cipherBox);
        buttonPanel.add(keyLabel);
        buttonPanel.add(keyField);
        buttonPanel.add(keyBox);
        buttonPanel.add(keyRequestBox);
        add(buttonPanel);
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
        keyField.setVisible(type != TYPE_NONE);
        keyField.setEditable(type != TYPE_AES);
        keyBox.setVisible(type != TYPE_NONE);
        keyRequestBox.setVisible(type != TYPE_NONE);
    }

    // Välj krypteringssystem
    class CipherBoxListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            String chosen = String.valueOf(cipherBox.getSelectedItem());
            // Use a state vector!!!
            switch (chosen) {
                case "caesar":
                    toggleType(TYPE_CAESAR);
                    keyField.setText("68");
                    break;
                case "AES":
                    toggleType(TYPE_AES);
                    keyField.setText(AES.getDecodeKey());
                    break;
                default:
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
                        AES.encrypt(text);
                        return AES.getEncryptedMsg();
                    } catch (NoSuchAlgorithmException | InvalidKeyException
                            | UnsupportedEncodingException | IllegalBlockSizeException
                            | BadPaddingException | NoSuchPaddingException ex) {
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
                    String key = keyField.getText();
                    String keyString = new String();
                    if (keyBox.isSelected()) {
                        keyString = String.format(" key=\"%s\"", key);
                    }
                    try {
                        cipherMessage = String.format("%s<encrypted type=\"%s\"%s>%s</encrypted>%s",
                                getText.substring(0, cipherStart), type, keyString,
                                encrypt(type, text, key), getText.substring(cipherEnd));
                        StyleConstants.setBackground(style, colorObj);
                        doc.remove(cipherStart, cipherEnd - cipherStart);
                        doc.insertString(cipherStart, text, style);
                        StyleConstants.setBackground(style, Color.WHITE);
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                String text = messagePane.getText().substring(cipherStart, cipherEnd);
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

            Color newColor = JColorChooser.showDialog(view.frame,
                    "Choose text color", view.frame.getBackground());
            if (newColor != null) {
                colorObj = newColor;
                namePane.setForeground(colorObj);
                messagePane.setForeground(colorObj);
                color = Integer.toHexString(colorObj.getRGB()).substring(2);
                if (cipherButton.isSelected()) {
                    String text = messagePane.getText().substring(cipherStart, cipherEnd);
                    StyleConstants.setBackground(style, colorObj);
                    try {
                        doc.remove(cipherStart, cipherEnd - cipherStart);
                        doc.insertString(cipherStart, text, style);
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    }
                    StyleConstants.setBackground(style, Color.WHITE);
                }
            }
        }
    }

    // Markera textrutor
    class FieldListener implements FocusListener {

        @Override
        public void focusGained(FocusEvent e) {
            JTextComponent source = (JTextComponent) e.getSource();
            source.selectAll();
        }

        @Override
        public void focusLost(FocusEvent e) {
            JTextComponent source = (JTextComponent) e.getSource();
            if (source == messagePane && !cipherButton.isSelected()) {
                cipherStart = source.getSelectionStart();
                cipherEnd = source.getSelectionEnd();
            }
            source.select(0, 0);
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
}