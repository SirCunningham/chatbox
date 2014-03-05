package chatbox;

import java.awt.*;
import javax.swing.*;

public class View {

    JFrame frame = new JFrame("Instant messaging program for pros");
    JPanel dialogPanel = new JPanel();
    JRadioButton clientButton = new JRadioButton("Client");
    JRadioButton serverButton = new JRadioButton("Server");
    ButtonGroup buttonGroup = new ButtonGroup();
    JLabel IPLabel = new JLabel("IP:");
    JLabel passLabel = new JLabel("Password:");
    JTextField IPField = new JTextField("127.0.0.1", 29);
    JTextField portField = new JTextField("4444", 28);
    JTextField passField = new JPasswordField("4hfJ/dc.5t", 11);
    JTextField tabField = new JTextField("Chat 1", 24);

    JButton startButton = new JButton("Join server");
    JComboBox serverOptions;
    JTabbedPane tabbedPane = new JTabbedPane();

    // Skapa GUI
    public View() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setPreferredSize(new Dimension(dim.width * 4 / 5, dim.height / 2));
        tabbedPane.setFocusable(false);

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
        JPanel invisibleContainer1 = new JPanel(new GridLayout(1, 1));
        JPanel invisibleContainer2 = new JPanel(new GridLayout(1, 1));
        radioPanel.add(clientButton);
        radioPanel.add(serverButton);
        IPPanel.add(IPLabel);
        IPPanel.add(IPField);
        portPanel.add(new JLabel("Port:"));
        portPanel.add(portField);
        passPanel.add(new JLabel("Type:"));
        passPanel.add(serverOptions);
        invisibleContainer1.add(passLabel);
        invisibleContainer2.add(passField);
        passPanel.add(invisibleContainer1);
        passPanel.add(invisibleContainer2);
        tabPanel.add(new JLabel("Chat name:"));
        tabPanel.add(tabField);
        startPanel.add(startButton);
        dialogPanel.add(radioPanel);
        dialogPanel.add(IPPanel);
        dialogPanel.add(portPanel);
        dialogPanel.add(passPanel);
        dialogPanel.add(tabPanel);
        dialogPanel.add(startPanel);
        
        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
}