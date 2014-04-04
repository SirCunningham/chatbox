package chatbox;

import java.io.*;
import java.net.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.*;

/**
 * Tråd för input- och outputströmmar
 */

public class IOThread2 implements Runnable {

    // Fält för strömmar
    private final InputStream i;
    private final OutputStream o;
    private PrintWriter out;
    private BufferedReader in;
    private final Socket clientSocket;
    TypeTimer timer;
    boolean hasSentKeyRequest = false;
    private final MessageBox messageBox;
    private final boolean isClient;
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
    }

    public class TimerListener implements ActionListener {

        @Override
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

}


