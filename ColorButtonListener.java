package chatbox;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JColorChooser;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;

//Välj bakrundsfärg
public class ColorButtonListener implements ActionListener {

    public ChatRoom chatRoom;

    public ColorButtonListener(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
    public void actionPerformed(ActionEvent e) {
        Color newColor = JColorChooser.showDialog(ChatCreator.frame,
                "Choose text color", ChatCreator.frame.getBackground());
        if (newColor != null) {
            chatRoom.colorObj = newColor;
            chatRoom.color = Integer.toHexString(chatRoom.colorObj.getRGB()).substring(2);
            chatRoom.namePane.setForeground(chatRoom.colorObj);
            String message = chatRoom.messagePane.getText();
            StyleConstants.setForeground(chatRoom.style, chatRoom.colorObj);
            try {
                //hacklösning... ersätt med adapter.o.println(text)!
                chatRoom.statusUpdate = true;
                chatRoom.doc.remove(0, message.length());
                chatRoom.doc.insertString(0, "I just changed to a new color: " + chatRoom.color, chatRoom.style);
                chatRoom.sendButton.doClick();
                chatRoom.doc.insertString(0, message, chatRoom.style);
                if (chatRoom.cipherButton.isSelected()) {
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
