package chatbox;

import java.awt.*;
import java.awt.event.*;
import javax.swing.text.*;

// Markera textrutor
public class StatusListener implements FocusListener {

    private String name;
    private final ChatRoom chatRoom;

    public StatusListener(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    @Override
    public void focusGained(FocusEvent e) {
        JTextComponent source = (JTextComponent) e.getSource();
        if (source == chatRoom.getNamePane()) {
            name = chatRoom.getNamePane().getText();
        }
        source.selectAll();
    }

    @Override
    public void focusLost(FocusEvent e) {
        JTextComponent source = (JTextComponent) e.getSource();
        if (source == chatRoom.getMessagePane() && !chatRoom.getCipherButton().isSelected()) {
            chatRoom.cipherStart = source.getSelectionStart();
            chatRoom.cipherEnd = source.getSelectionEnd();
            source.setText(source.getText());
        } else if (source == chatRoom.getNamePane()) {
            String nameProposal = chatRoom.getNamePane().getText().replaceAll("\\s+", " ").trim();
            if (chatRoom.getItems().contains(nameProposal)) {
                // Varning och omstart om namnet redan finns, men items innehåller bara chatBoxes!!
            }
            chatRoom.getNamePane().setText(nameProposal);
            if (!name.equals(nameProposal)) {
                if (nameProposal.isEmpty()) {
                    chatRoom.getNamePane().setText("Nomen nescio");
                    if (name.equals("Nomen nescio")) {
                        return;
                    }
                }
                String message = chatRoom.getMessagePane().getText();
                try {
                    chatRoom.statusUpdate = true;
                    //bättre lösning än hacklösningen, men kräver att keyRequest inte håller på
                    //chatRoom.o.println("I just switched from my old name: " + name);
                    chatRoom.doc.remove(0, message.length());
                    chatRoom.doc.insertString(0, "I just switched from my old name: " + name, chatRoom.style);
                    chatRoom.getSendButton().doClick();
                    chatRoom.doc.insertString(0, message, chatRoom.style);
                    if (chatRoom.getCipherButton().isSelected()) {
                        String text = message.substring(chatRoom.cipherStart, chatRoom.cipherEnd);
                        StyleConstants.setBackground(chatRoom.style, chatRoom.colorObj);
                        chatRoom.doc.remove(chatRoom.cipherStart, chatRoom.cipherEnd - chatRoom.cipherStart);
                        chatRoom.doc.insertString(chatRoom.cipherStart, text, chatRoom.style);
                        StyleConstants.setBackground(chatRoom.style, Color.WHITE);
                    }
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                } finally {
                    chatRoom.statusUpdate = false;
                }
                source.select(0, 0);
            }
        } else {
            source.setText(source.getText());
        }
    }
}