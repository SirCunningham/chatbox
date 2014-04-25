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
        String getText = chatRoom.messagePane.getText();
        if (chatRoom.cipherButton.isSelected()) {
            if (chatRoom.cipherStart < chatRoom.cipherEnd && chatRoom.cipherEnd <= getText.length()) {
                String text = getText.substring(chatRoom.cipherStart, chatRoom.cipherEnd);
                String type = String.valueOf(chatRoom.cipherBox.getSelectedItem());
                String key = chatRoom.keyPane.getText();
                String keyString = "";
                if (chatRoom.keyBox.isSelected()) {
                    keyString = String.format(" key=\"%s\"", key);
                }
                try {
                    chatRoom.cipherMessage = String.format("%s<encrypted type="
                            + "\"%s\"%s>%s</encrypted>%s",
                            XMLString.convertAngle(getText.substring(0,
                            chatRoom.cipherStart)), type, keyString,
                            XMLString.convertAngle(Encryption.encrypt(type, text, key, chatRoom.AES)), XMLString.convertAngle(
                            getText.substring(chatRoom.cipherEnd)));
                    StyleConstants.setBackground(chatRoom.style, chatRoom.colorObj);
                    chatRoom.doc.remove(chatRoom.cipherStart, chatRoom.cipherEnd - chatRoom.cipherStart);
                    chatRoom.doc.insertString(chatRoom.cipherStart, text, chatRoom.style);
                    StyleConstants.setBackground(chatRoom.style, Color.WHITE);
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            } else {
                chatRoom.cipherButton.doClick();
            }
        } else if (chatRoom.cipherStart < chatRoom.cipherEnd && chatRoom.cipherEnd <= getText.length()) {
            String text = getText.substring(chatRoom.cipherStart, chatRoom.cipherEnd);
            try {
                chatRoom.doc.remove(chatRoom.cipherStart, chatRoom.cipherEnd - chatRoom.cipherStart);
                chatRoom.doc.insertString(chatRoom.cipherStart, text, chatRoom.style);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }
    }
}
