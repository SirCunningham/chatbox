package chatbox;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;

public class CipherButtonListener implements ActionListener {

    private final ChatRoom chatRoom;

    public CipherButtonListener(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public void actionPerformed(ActionEvent e) {
        chatRoom.lockDocument = true;
        String text = chatRoom.messagePane.getText();
        int length = text.length();
        if (chatRoom.cipherButton.isSelected()) {
            if (chatRoom.cipherStart < chatRoom.cipherEnd && chatRoom.cipherEnd <= length) {
                String cipherText = text.substring(chatRoom.cipherStart, chatRoom.cipherEnd);
                String type = String.valueOf(chatRoom.cipherBox.getSelectedItem());
                String key = chatRoom.keyPane.getText();
                String keyString = "";
                if (chatRoom.keyBox.isSelected()) {
                    keyString = String.format(" key=\"%s\"", key);
                }
                try {
                    chatRoom.cipherMessage = String.format("%s<encrypted type="
                            + "\"%s\"%s>%s</encrypted>%s",
                            XMLString.convertAngle(text.substring(0,
                            chatRoom.cipherStart)), type, keyString,
                            XMLString.convertAngle(Encryption.encrypt(type,
                            cipherText, key, chatRoom.AES)), XMLString.convertAngle(
                            text.substring(chatRoom.cipherEnd)));
                    StyleConstants.setBackground(chatRoom.style, chatRoom.colorObj);
                    chatRoom.doc.remove(chatRoom.cipherStart, chatRoom.cipherEnd - chatRoom.cipherStart);
                    chatRoom.doc.insertString(chatRoom.cipherStart, cipherText, chatRoom.style);
                    StyleConstants.setBackground(chatRoom.style, Color.WHITE);
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            } else {
                chatRoom.cipherButton.doClick();
            }
        } else {
            try {
                int pos = chatRoom.messagePane.getCaretPosition();
                chatRoom.doc.remove(0, length);
                chatRoom.doc.insertString(0, text, chatRoom.style);
                chatRoom.messagePane.setCaretPosition(pos);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }
        chatRoom.lockDocument = false;
    }
}