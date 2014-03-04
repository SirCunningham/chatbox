package chatbox;

import java.awt.*;
import javax.swing.*;

public class View {

    JFrame frame = new JFrame("Instant messaging program for pros");
    JPanel leftPanel = new JPanel();
    JPanel rightPanel = new JPanel();
    JPanel dialogPanel = new JPanel();
    JPanel fileColorExitPanel = new JPanel();
    JRadioButton clientButton = new JRadioButton("Client");
    JRadioButton serverButton = new JRadioButton("Server");
    ButtonGroup buttonGroup = new ButtonGroup();
    JLabel IPLabel = new JLabel("IP:");
    JLabel passLabel = new JLabel("Password:");
    JTextField IPField = new JTextField("127.0.0.1", 25);
    JTextField portField = new JTextField("4444", 24);
    JTextField passField = new JPasswordField("4hfJ/dc.5t", 24);
    JTextField tabField = new JTextField("Chat 1", 24);
    JProgressBar progressBar = new JProgressBar();

    JButton startButton = new JButton("Join server");
    IconButton closeButton = new IconButton("closeIcon.png");
    JComboBox serverOptions;
    JTabbedPane tabbedPane = new JTabbedPane();

    // Skapa GUI
    public View() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tabbedPane.setFocusable(false);
        closeButton.setFocusPainted(false);

        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.addTab("+", null, dialogPanel, "Create a new chat");

        buttonGroup.add(clientButton);
        buttonGroup.add(serverButton);
        clientButton.setSelected(true);

        String[] stringOptions = {"Public", "Protected", "Private", "Secret"};
        serverOptions = new JComboBox(stringOptions);

        passLabel.setVisible(false);
        passField.setVisible(false);

        JPanel radioPanel = new JPanel();
        JPanel IPPanel = new JPanel();
        JPanel portPanel = new JPanel();
        JPanel passPanel = new JPanel();
        JPanel tabPanel = new JPanel();
        JPanel startPanel = new JPanel();
        radioPanel.add(clientButton);
        radioPanel.add(serverButton);
        IPPanel.add(IPLabel);
        IPPanel.add(IPField);
        portPanel.add(new JLabel("Port:"));
        portPanel.add(portField);
        passPanel.add(new JLabel("Type:"));
        passPanel.add(serverOptions);
        passPanel.add(passLabel);
        passPanel.add(passField);
        tabPanel.add(new JLabel("Chat name:"));
        tabPanel.add(tabField);
        startPanel.add(startButton);
        dialogPanel.add(radioPanel);
        dialogPanel.add(IPPanel);
        dialogPanel.add(portPanel);
        dialogPanel.add(passPanel);
        dialogPanel.add(tabPanel);
        dialogPanel.add(startPanel);
        
        fileColorExitPanel.add(closeButton);
        leftPanel.add(tabbedPane);
        rightPanel.add(progressBar);
        rightPanel.add(fileColorExitPanel);
        frame.add(leftPanel, BorderLayout.CENTER);
        frame.add(rightPanel, BorderLayout.EAST);
        frame.pack();
        frame.setVisible(true);
    }
}