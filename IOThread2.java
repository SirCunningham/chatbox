package chatbox;

import java.io.*;
import java.net.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.*;

/**
 * Tråd för input- och outputströmmar
 */
public class IOThread2 implements Runnable {

    // Fält för strömmar
    private InputStream i;
    private OutputStream o;
    private PrintWriter out;
    private BufferedReader in;
    private Socket clientSocket;
    TypeTimer timer;
    boolean hasSentKeyRequest = false;
    private MessageBox messageBox;
    private boolean isClient;
    private volatile boolean isNotRunnable;
    // Konstruktor
    public IOThread2(Socket sock, boolean client, MessageBox messageBox,
            InputStream i, OutputStream o) {
        this.i = i;
        this.o = o;
        clientSocket = sock;
        timer = new TypeTimer(10 * 1000, new TimerListener(), null);
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
        out = new PrintWriter(o, true);
        in = new BufferedReader(new InputStreamReader(i));

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
                    messageBox.items.removeElement(clientSocket.getInetAddress());
                } else {
                    appendToPane(echo);
                    if (echo.contains("you got the boot")) {
                        appendToPane("<message sender=\"INFO\">"
                                + "<text color=\"0000FF\">Du blev utsparkad!!!<disconnect /></text></message>");
                        kill();
                    }
                }
            } catch (IOException e) {
                appendToPane(String.format("<message sender=\"ERROR\">"
                        + "<text color=\"#ff0000\"> Communication failed </text></message>"));
            }


        }
        try {
            out.close();
            in.close();
            clientSocket.close();
        } catch (IOException e) {
            appendToPane(String.format("<message sender=\"ERROR\">"
                    + "<text color=\"#ff0000\"> Failed to close connection </text></message>"));
        }
    }

    public void kill() {
        isNotRunnable = true;
    }

    // Inspirerat av http://stackoverflow.com/questions/9650992/how-to-change-text-color-in-the-jtextarea?lq=1
    public void appendToPane(String msg) {

        try {
            MessageBox.xmlHTMLEditorKit kit = (MessageBox.xmlHTMLEditorKit) messageBox.chatBox.getEditorKit();
            HTMLDocument doc = (HTMLDocument) messageBox.chatBox.getDocument();
            try {
                kit.insertHTML(this, messageBox, doc.getLength(), msg, 0, 0, null);
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
            out.println(String.format("<message sender=\"%s\">"
                    + "<text color=\"%s\">Jag fick ingen nyckel av "
                    + "typen %s inom en minut och antar nu att ni inte har implementerat "
                    + "detta!</text></message>",
                    messageBox.namePane.getText(), messageBox.color, timer.getType()));

            appendToPane(String.format("<message sender=\"%s\">"
                    + "<text color=\"%s\">Jag fick ingen nyckel av "
                    + "typen %s inom en minut och antar nu att ni inte har implementerat "
                    + "detta!</text></message>", messageBox.namePane.getText(), messageBox.color, timer.getType()));
        }
    }

    public class SendMsgButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            // Skicka och visa i textrutan
            try {
                String message;
                if (messageBox.cipherButton.isSelected()) {
                    message = messageBox.cipherMessage;
                    messageBox.cipherButton.doClick();
                } else {
                    message = XMLString.convertAngle(messageBox.messagePane.getText());
                }
                String name = messageBox.namePane.getText();
                if (!message.isEmpty()) {
                    if (!messageBox.keyRequestBox.isSelected()) {
                        out.println(String.format("<message sender=\"%s\">"
                                + "<text color=\"%s\">%s </text></message>",
                                name, messageBox.color,
                                message));
                    } else {
                        out.println(String.format("<message sender=\"%s\">"
                                + "<text color=\"%s\"><keyrequest "
                                + "type=\"%s\">"
                                + "%s</keyrequest></text></message>",
                                name, messageBox.color,
                                String.valueOf(messageBox.cipherBox.getSelectedItem()),
                                message));
                        timer.setType(String.valueOf(messageBox.cipherBox.getSelectedItem()));
                        timer.start();

                    }
                    appendToPane(String.format("<message sender=\"%s\">"
                            + "<text color=\"%s\">%s</text></message>",
                            name, messageBox.color,
                            message));
                    messageBox.messagePane.setText("");
                }
                if (message.contains("terminate my ass")) {
                    appendToPane("<message sender=\"INFO\">"
                            + "<text color=\"0000FF\">Med huvudet före!!!<disconnect /></text></message>");
                    kill();
                }
            } catch (Exception ex) {
                appendToPane(String.format("<message sender=\"ERROR\">"
                        + "<text color=\"#ff0000\">Output stream failed</text></message>"));
            }
        }
    }

    public void keyRequest(String html) {
        if (html.indexOf("</keyrequest>") != -1) {
            int reply = JOptionPane.showConfirmDialog(null,
                    String.format("%s sends a keyrequest of type %s.\n Send key?",
                    XMLString.getSender(html), XMLString.getKeyRequestType(html)),
                    "Kill", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                appendToPane(String.format("<message sender=\"%s\">"
                        + "<text color=\"%s\"><encrypted key=\"%s\" type=\"%s\">Här kommer nyckeln!</encrypted></text></message>",
                        messageBox.namePane.getText(), messageBox.color,
                        messageBox.getKey(XMLString.getKeyRequestType(html)),
                        XMLString.getKeyRequestType(html)));
                out.println(String.format("<message sender=\"%s\">"
                        + "<text color=\"%s\"><encrypted key=\"%s\" type=\"%s\">Här kommer nyckeln!</encrypted></text></message>",
                        messageBox.namePane.getText(), messageBox.color,
                        messageBox.getKey(XMLString.getKeyRequestType(html)),
                        XMLString.getKeyRequestType(html)));
            }
        }
    }
}
