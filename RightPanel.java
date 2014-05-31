package chatbox;

import java.util.*;
import javax.swing.*;
import javax.swing.text.*;

public class RightPanel extends JPanel {
    private final JScrollPane listPane;
    private final JPanel bootPanel = new JPanel();
    private final JButton bootButton = new JButton("Boot selected");
    private final JButton keyRequestButton = new JButton("Send keyrequest");
    private final JPanel infoPanel = new JPanel();
    private final JPanel filePanel = new JPanel();
    private final JPanel fileButtonPanel = new JPanel();
    private final JButton fileButton = new IconButton("fileIcon.png");
    private final JTextPane filePane = new JTextPane();
    private final JTextPane fileSizePane = new JTextPane();
    private final JTextPane descriptionPane = new JTextPane();
    private final JButton sendFileButton = new JButton("Send file to selected");
    private final JButton progressBarButton = new JButton("NEW Receive [test!]");
    private final JButton closeButton = new IconButton("closeIcon.png");
    private final JComboBox fileEncryptions;
    private final JComboBox keyRequestEncryptions;
    
    public RightPanel(ChatRoom chatRoom) {
        listPane = new JScrollPane(chatRoom.getList());
        listPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        bootButton.addActionListener(new BootButtonListener(chatRoom));
        bootButton.setVisible(false);
        keyRequestEncryptions = new JComboBox(Arrays.copyOfRange(ChatRoom.cipherString, 1, 3));
        JLabel infoLabel1 = new JLabel("Host: " + chatRoom.host);
        JLabel infoLabel2 = new JLabel("Port: " + chatRoom.port);
        infoPanel.add(infoLabel1);
        infoPanel.add(infoLabel2);
        bootPanel.add(bootButton);
        bootPanel.add(keyRequestButton); //skicka till markerade, avmarkera sedan, blockera om inget markerat?
        bootPanel.add(new JLabel("Encryption:"));
        bootPanel.add(keyRequestEncryptions);
        add(listPane);
        add(bootPanel);
        add(infoPanel);

        fileButton.addActionListener(new FileButtonListener(chatRoom));
        fileButton.setBorder(BorderFactory.createEmptyBorder());
        fileButton.setToolTipText("Open file explorer");
        sendFileButton.addActionListener(new SendFileButtonListener(chatRoom));
        sendFileButton.setEnabled(false);
        progressBarButton.addActionListener(new ProgressBarButtonListener(chatRoom));
        closeButton.setFocusPainted(false);

        ((AbstractDocument) filePane.getDocument()).setDocumentFilter(new NewLineFilter(32));
        filePane.addFocusListener(new StatusListener(chatRoom));
        filePane.setText("filename.txt");
        ((AbstractDocument) fileSizePane.getDocument()).setDocumentFilter(new NewLineFilter(16));
        fileSizePane.setEditable(false);
        fileSizePane.setText("0");
        ((AbstractDocument) descriptionPane.getDocument()).setDocumentFilter(new NewLineFilter(128));
        descriptionPane.addFocusListener(new StatusListener(chatRoom));
        descriptionPane.setText("File description (optional)");
        fileEncryptions = new JComboBox(ChatRoom.cipherString);

        filePanel.add(fileButton);
        filePanel.add(filePane);
        filePanel.add(fileSizePane);
        filePanel.add(descriptionPane);
        filePanel.add(sendFileButton);
        fileButtonPanel.add(progressBarButton); // temporary - testing only!
        fileButtonPanel.add(new JLabel("Encryption:"));
        fileButtonPanel.add(fileEncryptions);
        fileButtonPanel.add(closeButton);
        add(filePanel);
        add(fileButtonPanel);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }
    
    public JButton getCloseButton() {
        return closeButton;
    }

    public JButton getSendFileButton() {
        return sendFileButton;
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

    public JButton getBootButton() {
        return bootButton;
    }
    
    public JButton getKeyRequestButton() {
        return keyRequestButton;
    }
    
    public JButton getFileButton() {
        return fileButton;
    }
    
    public String getKeyRequestEncryption() {
        return (String) keyRequestEncryptions.getSelectedItem();
    }
    
    public JButton getProgressBarButton() {
        return progressBarButton;
    }
}