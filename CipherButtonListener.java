package chatbox;

import java.awt.*;
import java.awt.event.*;
import javax.swing.text.*;

public class CipherButtonListener implements ActionListener {

    private final ChatRoom chatRoom;

    public CipherButtonListener(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        chatRoom.lockDocument = true;
        String text = chatRoom.getMessagePane().getText();
        int length = text.length();
        if (chatRoom.getCipherButton().isSelected()) {
            if (chatRoom.cipherStart < chatRoom.cipherEnd && chatRoom.cipherEnd <= length) {
                String cipherText = text.substring(chatRoom.cipherStart, chatRoom.cipherEnd);
                String type = String.valueOf(chatRoom.getCipherBox().getSelectedItem());
                String key = chatRoom.getKeyPane().getText();
                String keyString = "";
                if (chatRoom.getKeyBox().isSelected()) {
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
                    //inte nöjd med denna funktionalitet!
                    chatRoom.getMessagePane().setText(chatRoom.cipherMessage);
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            } else {
                chatRoom.getCipherButton().doClick();
            }
        } else {
            try {
                int pos = chatRoom.getMessagePane().getCaretPosition();
                chatRoom.doc.remove(0, length);
                chatRoom.doc.insertString(0, text, chatRoom.style);
                chatRoom.getMessagePane().setCaretPosition(pos);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }
        chatRoom.lockDocument = false;
    }
}