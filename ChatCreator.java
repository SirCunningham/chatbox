package chatbox;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;

final public class ChatCreator {

    static final JFrame frame = new JFrame("ChatBox - instant messaging for pros");
    static final JPanel dialogPanel = new JPanel();
    static final JRadioButton clientButton = new JRadioButton("Client");
    static final JRadioButton serverButton = new JRadioButton("Server");
    static final ButtonGroup buttonGroup = new ButtonGroup();
    static final JLabel hostLabel = new JLabel("Host:");
    static final JLabel passLabel = new JLabel("Password:");
    static final JLabel requestLabel = new JLabel("Request:");
    static final JTextPane hostPane = new JTextPane();
    static final JTextPane portPane = new JTextPane();
    static final JPasswordField passPane = new JPasswordField("4hfJ/dc.5t", 10);
    static final JTextField requestPane = new JTextField("Let me in!", 15);
    static final JTextPane namePane = new JTextPane();
    static final JTextPane tabPane = new JTextPane();
    static final JButton startButton = new JButton("Join server");
    static final JButton closeButton = new IconButton("closeIcon.png");
    static final JComboBox serverOptions;
    static final JTabbedPane tabbedPane = new JTabbedPane();
    static String host = "127.0.0.1";
    static final ArrayList<ChatRoom> chatRooms = new ArrayList<>();
    static final ArrayList<JButton> indices = new ArrayList<>();
    static final Random generator = new Random();

    // Statisk initialisering
    static {
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setPreferredSize(new Dimension(dim.width * 4 / 5, dim.height / 2));
        tabbedPane.setFocusable(false);

        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.addTab("+", null, dialogPanel, "Create a new chat");

        buttonGroup.add(clientButton);
        buttonGroup.add(serverButton);
        clientButton.setSelected(true);
        closeButton.setFocusPainted(false);

        ((AbstractDocument) hostPane.getDocument()).setDocumentFilter(new NewLineFilter(48));
        hostPane.setText(host);
        ((AbstractDocument) portPane.getDocument()).setDocumentFilter(new NewLineFilter(5, false));
        portPane.setText(Integer.toString(generator.nextInt(65536 - 1024) + 1024));
        ((AbstractDocument) namePane.getDocument()).setDocumentFilter(new NewLineFilter(24));
        namePane.setText("User " + generator.nextInt(1000000000));
        ((AbstractDocument) tabPane.getDocument()).setDocumentFilter(new NewLineFilter(24));
        tabPane.setText("Chat 1");

        String[] stringOptions = {"Public", "Protected", "Private", "Secret"};
        serverOptions = new JComboBox(stringOptions);

        passLabel.setVisible(false);
        passPane.setVisible(false);

        requestLabel.setVisible(false);
        requestPane.setVisible(false);

        JPanel radioPanel = new JPanel();
        JPanel hostPanel = new JPanel();
        JPanel portPanel = new JPanel();
        JPanel passPanel = new JPanel();
        JPanel namePanel = new JPanel();
        JPanel tabPanel = new JPanel();
        JPanel startPanel = new JPanel();
        JPanel invisibleContainer1 = new JPanel(new GridLayout(1, 1));
        JPanel invisibleContainer2 = new JPanel(new GridLayout(1, 1));
        JPanel invisibleContainer3 = new JPanel(new GridLayout(1, 1));
        JPanel invisibleContainer4 = new JPanel(new GridLayout(1, 1));

        radioPanel.add(clientButton);
        radioPanel.add(serverButton);
        hostPanel.add(hostLabel);
        hostPanel.add(hostPane);
        portPanel.add(new JLabel("Port:"));
        portPanel.add(portPane);
        passPanel.add(new JLabel("Type:"));
        passPanel.add(serverOptions);
        invisibleContainer1.add(passLabel);
        invisibleContainer2.add(passPane);
        invisibleContainer3.add(requestLabel);
        invisibleContainer4.add(requestPane);
        passPanel.add(invisibleContainer1);
        passPanel.add(invisibleContainer2);
        passPanel.add(invisibleContainer3);
        passPanel.add(invisibleContainer4);
        namePanel.add(new JLabel("Username:"));
        namePanel.add(namePane);
        tabPanel.add(new JLabel("Chatroom name:"));
        tabPanel.add(tabPane);
        startPanel.add(startButton);
        startPanel.add(closeButton);
        dialogPanel.add(radioPanel);
        dialogPanel.add(hostPanel);
        dialogPanel.add(portPanel);
        dialogPanel.add(passPanel);
        dialogPanel.add(namePanel);
        dialogPanel.add(tabPanel);
        dialogPanel.add(startPanel);

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    // Förbjud instansiering
    private ChatCreator() {
        throw new AssertionError();
    }

    public static void showError(String text) {
        JOptionPane.showMessageDialog(frame, text, "Error message",
                JOptionPane.ERROR_MESSAGE);
    }
}