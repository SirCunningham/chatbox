package chatbox;

import javax.swing.*;
import javax.swing.text.*;

public class RightPanel extends JPanel {
    private JScrollPane listPane;
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
    
    public RightPanel(ChatRoom chatRoom) {
        listPane = new JScrollPane(chatRoom.getList());
        listPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        bootButton.addActionListener(new BootButtonListener(chatRoom));
        JLabel infoLabel1 = new JLabel("Host: " + chatRoom.host);
        JLabel infoLabel2 = new JLabel("Port: " + chatRoom.port);
        infoPanel.add(infoLabel1);
        infoPanel.add(infoLabel2);
        bootPanel.add(bootButton);
        bootPanel.setVisible(false);
        add(listPane);
        add(bootPanel);
        add(infoPanel);

        fileButton.addActionListener(new FileButtonListener(chatRoom));
        fileButton.setBorder(BorderFactory.createEmptyBorder());
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

    public JPanel getBootPanel() {
        return bootPanel;
    }
    
}