package chatbox;

import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Tråd för input- och outputströmmar
 */
public class IOThread extends Thread {

    // Fält för strömmar
    private PrintWriter out;
    private BufferedReader in;
    private Socket clientSocket;
    private View view;
    private Controller controller;
    private boolean isClient;
    private volatile boolean isNotRunnable;
    private volatile boolean tabLock = false;

    // Konstruktor
    public IOThread(Socket sock, View view, boolean client) {
        clientSocket = sock;
        if (view.sendMsgButton.getActionListeners().length<1) {
            view.sendMsgButton.addActionListener(new SendMsgButtonListener());
        }
        this.view = view;
        this.isClient = client;
        controller = new Controller(view);
    }

    @Override
    public void run() {

        // Vi kör tills vi är klara
        isNotRunnable = false;

        // Anslut läs- och skrivströmmarna
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            appendToPane(view.chatBoxes.get(view.tabbedPane.getSelectedIndex()),
                    "ERROR: Failed to create an output stream", Color.RED);
            if (isClient) {
                controller.enableConnection();
            }
            return;
        }
        try {
            in = new BufferedReader(new InputStreamReader(
                    clientSocket.getInputStream()));
        } catch (IOException e) {
            appendToPane(view.chatBoxes.get(view.tabbedPane.getSelectedIndex()),
                    "ERROR: Failed to create an input stream", Color.RED);
            if (isClient) {
                controller.enableConnection();
            }
            return;
        }

        // Kommer vi hit har anslutningen gått bra
        if (isClient) {
            appendToPane(view.chatBoxes.get(view.tabbedPane.getSelectedIndex()),
                    "SUCCESS: Connection successful", Color.GREEN);
        } else {
            // Skriv ut IP-nummret från klienten
            appendToPane(view.chatBoxes.get(view.tabbedPane.getSelectedIndex()),
                    String.format("SUCCESS: Connection established with %s",
                    clientSocket.getInetAddress()), Color.GREEN);
        }

        // Här läser vi in klientens budskap
        // Om klienten kopplar ner gör vi det också, och avslutar tråden
        while (!isNotRunnable) {
            try {
                String echo = in.readLine();
                if (echo == null) {
                    appendToPane(view.chatBoxes.get(
                            view.tabbedPane.getSelectedIndex()),
                            String.format("INFO: %s disconnected",
                            clientSocket.getInetAddress()), Color.BLUE);
                    if (isClient) {
                        controller.enableConnection();
                    }
                    isNotRunnable = true;
                } else {
                    appendToPane(view.chatBoxes.get(
                            view.tabbedPane.getSelectedIndex()), echo,
                            new XMLString(echo).toColor());
                }
            } catch (IOException e) {
                appendToPane(view.chatBoxes.get(
                        view.tabbedPane.getSelectedIndex()),
                        "ERROR: Communication failed", Color.RED);
            }
        }

        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            appendToPane(view.chatBoxes.get(view.tabbedPane.getSelectedIndex()),
                    "ERROR: Failed to close connection", Color.RED);
        }
    }

    public void kill() {
        isNotRunnable = true;
    }

    // Inspirerat av http://stackoverflow.com/questions/9650992/how-to-change-text-color-in-the-jtextarea?lq=1
    private void appendToPane(JTextPane chatBox, String msg, Color c) {
        StyledDocument doc = chatBox.getStyledDocument();
        Style style = chatBox.addStyle("I'm a Style", null);
        StyleConstants.setForeground(style, c);

        try {
            doc.insertString(doc.getLength(), "\n" + msg, style);
        } catch (BadLocationException e) {
            JOptionPane.showMessageDialog(null, "String insertion failed.",
                    "Error message", JOptionPane.ERROR_MESSAGE);
        }
    }

    public class SendMsgButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Count of listeners: " + ((JButton) e.getSource()).getActionListeners().length);
            // Skicka och visa i textrutan
            try {
                out.println(String.format("<message sender=\"%s\">"
                        + "<text color=\"%s\">%s</text></message>",
                        view.nameField.getText(), view.color,
                        view.messageField.getText()));
                
                
                appendToPane(
                        view.chatBoxes.get(view.tabbedPane.getSelectedIndex()),
                        String.format("%s: %s", view.nameField.getText(),
                        view.messageField.getText()),
                        Color.decode("#" + view.color));
                view.messageField.setText("");

            } catch (Exception ex) {
                appendToPane(
                        view.chatBoxes.get(view.tabbedPane.getSelectedIndex()),
                        "ERROR: Output stream failed", Color.RED);
            }
        }
    }
}
