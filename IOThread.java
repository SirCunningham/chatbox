package chatbox;

import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;
import javax.swing.text.html.HTMLDocument;

/**
 * Tråd för input- och outputströmmar
 */
public class IOThread implements Runnable {

    // Fält för strömmar
    private PrintWriter out;
    private BufferedReader in;
    private Socket clientSocket;

    TypeTimer timer;
    public MessageBox messageBox;
    private boolean isClient;
    private volatile boolean isNotRunnable;

    // Konstruktor
    public IOThread(Socket sock, boolean client, MessageBox messageBox) {
        clientSocket = sock;
        timer = new TypeTimer(10*1000, new TimerListener(), null);
        timer.setRepeats(false);
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
            appendToPane(String.format("<message sender=\"ERROR\">"
                    + "<text color=\"#ff0000\"> Failed to create an output stream </text></message>"));
            return;
        }
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            appendToPane(String.format("<message sender=\"ERROR\">"
                    + "<text color=\"#ff0000\"> Failed to create an input stream </text></message>"));
            return;
        }

        // Kommer vi hit har anslutningen gått bra
        if (isClient) {
            appendToPane(String.format("<message sender=\"SUCCESS\">"
                    + "<text color=\"#00ff00\"> Connection successful </text></message>"));
        } else {
            // Skriv ut IP-nummret från klienten
            appendToPane(String.format("<message sender=\"SUCCESS\">"
                    + "<text color=\"#00ff00\"> Connection established with %s </text></message>", clientSocket.getInetAddress()));
        }

        // Här läser vi in klientens budskap
        // Om klienten kopplar ner gör vi det också, och avslutar tråden
        while (!isNotRunnable) {
            try {
                String echo = in.readLine();
                if (echo == null) {
                    isNotRunnable = true;
                    appendToPane(String.format("<message sender=\"INFO\">"
                            + "<text color=\"0000ff\">%s har loggat ut!<disconnect /></text></message>", messageBox.namePane.getText()));

                    if (!isClient) {
                        messageBox.items.removeElement(clientSocket.getInetAddress());
                    }
                } else {
                    appendToPane(echo);
                    if (echo.contains("you got the boot")) {
                        appendToPane("<message sender=\"INFO\">"
                            + "<text color=\"ff0000\">Du blev utsparkad!!!<disconnect /></text></message>");
                        kill();
                    }
                }
            } catch (IOException e) {
                appendToPane(String.format("<message sender=\"ERROR\">"
                        + "<text color=\"#ff0000\"> Communication failed </text></message>"));
            }


        }
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            appendToPane(String.format("<message sender=\"ERROR\">"
                    + "<text color=\"#ff0000\"> Failed to close connection </text></message>"));
        }
    }
    public MessageBox getMessageBox() {
        return messageBox;
    }

    public void kill() {
        isNotRunnable = true;
    }

    // Inspirerat av http://stackoverflow.com/questions/9650992/how-to-change-text-color-in-the-jtextarea?lq=1
    public void appendToPane(String msg) {
        try {
            XMLString XMLMsg = new XMLString(msg);
            XMLMsg.handleString();
            msg = XMLMsg.toText();

            xmlHTMLEditorKit kit = (xmlHTMLEditorKit) messageBox.chatBox.getEditorKit();
            HTMLDocument doc = (HTMLDocument) messageBox.chatBox.getDocument();
            try {
                kit.insertHTML(this, doc.getLength(), msg, 0, 0, null);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } catch (BadLocationException e) {
            JOptionPane.showMessageDialog(null, "String insertion failed.",
                    "Error message", JOptionPane.ERROR_MESSAGE);
        }

    }
    public class TimerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        }
    }
    public class SendMsgButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            // Skicka och visa i textrutan
            try {
                if (!messageBox.messagePane.getText().equals("")) {
                    if (!messageBox.keyRequestBox.isSelected()) {
                        out.println(String.format("<message sender=\"%s\">"
                                + "<text color=\"%s\">%s </text></message>",
                                messageBox.namePane.getText(), messageBox.color,
                                XMLString.convertAngle(messageBox.messagePane.getText())));
                    } else {
                        timer.start();
                        out.println(String.format("<message sender=\"%s\">"
                                + "<text color=\"%s\"><keyrequest "
                                + "type=\"%s\">"
                                + "%s</keyrequest></text></message>",
                                messageBox.namePane.getText(), messageBox.color,
                                String.valueOf(messageBox.cipherBox.getSelectedItem()),
                                XMLString.convertAngle(messageBox.messagePane.getText())));
                    }
                    appendToPane(String.format("<message sender=\"%s\">"
                            + "<text color=\"%s\">%s</text></message>",
                            messageBox.namePane.getText(), messageBox.color,
                            XMLString.convertAngle(messageBox.messagePane.getText())));
                    messageBox.messagePane.setText("");
                }
            } catch (Exception ex) {
                appendToPane(String.format("<message sender=\"ERROR\">"
                        + "<text color=\"#ff0000\">Output stream failed</text></message>"));
            }
        }
    }
}
