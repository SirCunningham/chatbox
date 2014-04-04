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
/*
public class IOThread2 {

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

}
 * 
 */