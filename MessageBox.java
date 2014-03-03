package chatbox;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.math.BigInteger;
import javax.swing.*;
import javax.swing.text.*;

class MessageBox extends JPanel {

    private String[] cipherString = {"None", "caesar", "AES", "RSA", "blowfish"};
    private View view;
    IconButton colorButton = new IconButton("colorIcon.png");
    JTextField nameField = new JTextField("Dante", 8);
    JTextField messageField = new JTextField("In medio cursu vitae"
            + "nostrae, eram in silva obscura...", 75);
    JButton sendButton = new JButton("Send message");
    JToggleButton cipherButton = new JToggleButton("Encrypt selected");
    JLabel cipherLabel = new JLabel("Cryptosystem:");
    JComboBox cipherBox = new JComboBox(cipherString);
    JLabel keyLabel = new JLabel("Key:");
    JTextField keyField = new JTextField("68", 5);
    JCheckBox keyBox = new JCheckBox("Send key", true);
    String color = Integer.toHexString(Color.BLACK.getRGB()).substring(2);
    int startEnc;
    int endEnc;
    String backup;

    public MessageBox(View view) {
        this.view = view;
        JTextPane cBox = new JTextPane();
        DefaultCaret caret = (DefaultCaret) cBox.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        cBox.setEditable(false);
        cBox.setText("This is where it happens.");
        view.chatBoxes.add(cBox);
        JScrollPane scrollPane = new JScrollPane(cBox);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane);

        JPanel messagePanel = new JPanel();
        colorButton.setBorder(BorderFactory.createEmptyBorder());
        colorButton.addActionListener(new ColorButtonListener());
        nameField.addFocusListener(new FieldListener());
        messageField.addFocusListener(new FieldListener());
        messageField.addKeyListener(new MessageListener());
        messagePanel.add(colorButton);
        messagePanel.add(nameField);
        messagePanel.add(messageField);
        add(messagePanel);

        JPanel buttonPanel = new JPanel();
        cipherButton.setEnabled(false);
        cipherButton.addActionListener(new CipherButtonListener());
        cipherBox.addItemListener(new CipherBoxListener());
        keyLabel.setVisible(false);
        keyField.setVisible(false);
        keyField.addFocusListener(new FieldListener());
        keyBox.setVisible(false);
        buttonPanel.add(sendButton);
        buttonPanel.add(cipherButton);
        buttonPanel.add(cipherLabel);
        buttonPanel.add(cipherBox);
        buttonPanel.add(keyLabel);
        buttonPanel.add(keyField);
        buttonPanel.add(keyBox);
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

    // Välj krypteringssystem
    class CipherBoxListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            String chosen = String.valueOf(cipherBox.getSelectedItem());
            // Use a state vector!!!
            if ("None".equals(chosen)) {
                cipherButton.setEnabled(false);
                keyLabel.setVisible(false);
                keyField.setVisible(false);
                keyBox.setVisible(false);
            } else {
                cipherButton.setEnabled(true);
                keyLabel.setVisible(true);
                keyField.setVisible(true);
                keyBox.setVisible(true);
            }
        }
    }

    class CipherButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            // Bugg: markera krypterad text, hamna utanför index
            if (cipherButton.isSelected()) {
                if (startEnc < endEnc) {
                    backup = messageField.getText();
                    String string = new String();
                    String key = keyField.getText();
                    String keyString = new String();
                    if (keyBox.isSelected()) {
                        keyString = String.format(" key=\"%s\"", key);
                    }
                    try {
                        string += String.format("%s<encrypted type=\"%s\"%s>%s</encrypted>%s",
                                backup.substring(0, startEnc),
                                String.valueOf(cipherBox.getSelectedItem()),
                                keyString, encryptCaesar(backup.substring(startEnc,
                                endEnc), Integer.valueOf(key)), backup.substring(endEnc));
                    } catch (UnsupportedEncodingException ex) {
                        ex.printStackTrace();
                    }
                    messageField.setText(string);
                }
            } else {
                messageField.setText(backup);
            }
        }
    }

    // Välj bakgrundsfärg
    class ColorButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            Color newColor = JColorChooser.showDialog(view.chatBoxPanel,
                    "Choose text color", view.chatBoxPanel.getBackground());
            if (newColor != null) {
                nameField.setForeground(newColor);
                messageField.setForeground(newColor);
                color = Integer.toHexString(newColor.getRGB()).substring(2);
            }
        }
    }

    // Markera textrutor
    class FieldListener implements FocusListener {

        @Override
        public void focusGained(FocusEvent e) {
            JTextField source = (JTextField) e.getSource();
            source.selectAll();
        }

        @Override
        public void focusLost(FocusEvent e) {
            JTextField source = (JTextField) e.getSource();
            if (source == messageField) {
                startEnc = source.getSelectionStart();
                endEnc = source.getSelectionEnd();
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