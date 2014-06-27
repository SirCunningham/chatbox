package chatbox;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;

//Välj bakrundsfärg
public class ColorButtonListener implements ActionListener {

    public ChatRoom chatRoom;

    public ColorButtonListener(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Color newColor = JColorChooser.showDialog(ChatCreator.frame,
                "Choose text color", ChatCreator.frame.getBackground());
        if (newColor != null && !newColor.equals(chatRoom.colorObj)) {
            chatRoom.colorObj = newColor;
            chatRoom.color = Integer.toHexString(chatRoom.colorObj.getRGB()).substring(2);
            chatRoom.getNamePane().setForeground(chatRoom.colorObj);
            String message = chatRoom.getMessagePane().getText();
            StyleConstants.setForeground(chatRoom.style, chatRoom.colorObj);
            try {
                chatRoom.statusUpdate = true;
                //bättre lösning än hacklösningen, men kräver att keyRequest inte håller på
                //chatRoom.o.println("I just changed to a new color: " + chatRoom.color);
                chatRoom.doc.remove(0, message.length());
                chatRoom.doc.insertString(0, "I just changed to a new color: " + chatRoom.color, chatRoom.style);
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
        }
    }
}