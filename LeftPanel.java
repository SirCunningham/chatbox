package chatbox;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

public class LeftPanel extends JPanel {
    
    private JButton colorButton = new IconButton("colorIcon.png");
    private JTextPane namePane = new JTextPane();
    private JTextPane messagePane;
    private JTextPane chatBox = new JTextPane();
    private JButton sendButton = new JButton("Send message");
    private JToggleButton cipherButton = new JToggleButton("Encrypt selected");
    private JLabel cipherLabel = new JLabel("Encryption:");
    private JComboBox cipherBox = new JComboBox(ChatRoom.cipherString);
    private JLabel keyLabel = new JLabel("Key:");
    private JTextPane keyPane = new JTextPane();
    private JCheckBox keyBox = new JCheckBox("Send key", true);
    private JCheckBox keyRequestBox = new JCheckBox("Send keyrequest", false);
    
    public LeftPanel(ChatRoom chatRoom) {
        messagePane = new JTextPane(chatRoom.doc);
        
        DefaultCaret caret = (DefaultCaret) chatBox.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        chatBox.setEditable(false);
        chatBox.setContentType("text/html");
        xmlHTMLEditorKit kit = new xmlHTMLEditorKit();
        HTMLDocument doc2 = new HTMLDocument();
        chatBox.setEditorKit(kit);
        chatBox.setDocument(doc2);
        chatBox.addKeyListener(new TabListener(chatRoom));
        chatRoom.appendToPane(String.format("<message sender=\"INFO\">"
                + "<text color=\"#339966\">This is where it happens.</text></message>"));
        JScrollPane scrollPane = new JScrollPane(chatBox);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane);
        
        JPanel messagePanel = new JPanel();
        colorButton.setBorder(BorderFactory.createEmptyBorder());
        colorButton.addActionListener(new ColorButtonListener(chatRoom));
        ((AbstractDocument) namePane.getDocument()).setDocumentFilter(new NewLineFilter(32));
        String name = ChatCreator.namePane.getText();
        namePane.setText(name.isEmpty() ? "Nomen nescio" : name);
        namePane.addFocusListener(new StatusListener(chatRoom));
        ((AbstractDocument) messagePane.getDocument()).setDocumentFilter(new NewLineFilter(256));
        messagePane.setText("In medio cursu vitae nostrae, eram in silva obscura...");
        messagePane.getDocument().addDocumentListener(new MessageDocListener(chatRoom));
        messagePane.addFocusListener(new StatusListener(chatRoom));
        messagePane.addKeyListener(new MessageListener(chatRoom));
        messagePanel.add(colorButton);
        messagePanel.add(namePane);
        messagePanel.add(messagePane);
        messagePanel.add(sendButton);
        add(messagePanel);
        
        JPanel buttonPanel = new JPanel();
        JPanel invisibleContainer1 = new JPanel(new GridLayout(1, 1));
        JPanel invisibleContainer2 = new JPanel(new GridLayout(1, 1));
        JPanel invisibleContainer3 = new JPanel(new GridLayout(1, 1));
        cipherButton.setEnabled(false);
        cipherButton.addActionListener(new CipherButtonListener(chatRoom));
        cipherBox.addItemListener(new CipherBoxListener(chatRoom));
        keyLabel.setVisible(false);
        keyPane.setVisible(false);
        keyPane.addFocusListener(new StatusListener(chatRoom));
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
        add(buttonPanel);
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
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
    
    public JTextPane getMessagePane() {
        return messagePane;
    }

    public JButton getSendButton() {
        return sendButton;
    }
    
    public JTextPane getNamePane() {
        return namePane;
    }
    
    public JTextPane getChatBox() {
        return chatBox;
    }
}