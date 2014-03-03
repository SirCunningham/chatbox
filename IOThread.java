package chatbox;

import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Tråd för input- och outputströmmar
 */
public class IOThread implements Runnable {

    // Fält för strömmar
    private PrintWriter out;
    private BufferedReader in;
    private Socket clientSocket;
    private View view;
    private MessageBox messageBox;
    private boolean isClient;
    private volatile boolean isNotRunnable;

    // Konstruktor
    public IOThread(Socket sock, boolean client, View view, MessageBox messageBox) {
        clientSocket = sock;
        this.view = view;
        this.messageBox = messageBox;
        this.isClient = client;
        messageBox.sendButton.addActionListener(new SendMsgButtonListener());
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
            return;
        }
        try {
            in = new BufferedReader(new InputStreamReader(
                    clientSocket.getInputStream()));
        } catch (IOException e) {
            appendToPane(view.chatBoxes.get(view.tabbedPane.getSelectedIndex()),
                    "ERROR: Failed to create an input stream", Color.RED);
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
            XMLString XMLMsg = new XMLString(msg);
            XMLMsg.handleString();
            msg = XMLMsg.toText();
            doc.insertString(doc.getLength(), "\n" + msg, style);
        } catch (BadLocationException e) {
            JOptionPane.showMessageDialog(null, "String insertion failed.",
                    "Error message", JOptionPane.ERROR_MESSAGE);
        }
    }

    public class SendMsgButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            // Skicka och visa i textrutan
            try {
                if (!messageBox.messageField.getText().equals("")) {
                    out.println(String.format("<message sender=\"%s\">"
                            + "<text color=\"%s\">%s</text></message>",
                            messageBox.nameField.getText(), messageBox.color,
                            messageBox.messageField.getText()));

                    appendToPane(
                            view.chatBoxes.get(view.tabbedPane.getSelectedIndex()),
                            String.format("%s: %s", messageBox.nameField.getText(),
                            messageBox.messageField.getText()),
                            Color.decode("#" + messageBox.color));
                    messageBox.messageField.setText("");
                }
            } catch (Exception ex) {
                appendToPane(
                        view.chatBoxes.get(view.tabbedPane.getSelectedIndex()),
                        "ERROR: Output stream failed", Color.RED);
            }
        }
    }
}
