package chatbox;

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
        if (source == chatRoom.namePane) {
            name = chatRoom.namePane.getText();
        }
        source.selectAll();
    }

    @Override
    public void focusLost(FocusEvent e) {
        JTextComponent source = (JTextComponent) e.getSource();
        if (source == chatRoom.messagePane && !chatRoom.cipherButton.isSelected()) {
            chatRoom.cipherStart = source.getSelectionStart();
            chatRoom.cipherEnd = source.getSelectionEnd();
        } else if (source == chatRoom.namePane && !name.equals(chatRoom.namePane.getText())) {
            if (chatRoom.namePane.getText().isEmpty()) {
                chatRoom.namePane.setText("Nomen nescio");
                if (name.equals("Nomen nescio")) {
                    return;
                }
            }
            String message = chatRoom.messagePane.getText();
            try {
                chatRoom.doc.remove(0, message.length());
                chatRoom.doc.insertString(0, "I just switched from my old name: " + name, chatRoom.style);
                chatRoom.sendButton.doClick();
                chatRoom.doc.insertString(0, message, chatRoom.style);
            } catch (BadLocationException ex) {
                System.out.println("test");
                ex.printStackTrace();
            }
            source.select(0, 0);
        } else if (source == chatRoom.keyPane) {
            source.setText(source.getText()); //redundant?
        }
    }
}