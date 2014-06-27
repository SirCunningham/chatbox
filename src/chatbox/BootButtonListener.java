package chatbox;

import java.awt.event.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.*;

public class BootButtonListener implements ActionListener {

    private final ChatRoom chatRoom;

    public BootButtonListener(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    //REMEMBER TO REMOVE FROM LIST
    @Override
    public void actionPerformed(ActionEvent e) {
        String message = chatRoom.getMessagePane().getText();
        // Vad exakt är det tänkt att loopen skall göra?
        try {
            chatRoom.doc.remove(0, message.length());
            String chatName = (String) chatRoom.getList().getSelectedValue();
            chatRoom.doc.insertString(0, String.format("%s got the boot",
                            chatName), chatRoom.style);
            chatRoom.getSendButton().doClick();
            chatRoom.doc.insertString(0, message, chatRoom.style);
            try {
                chatRoom.kickUser(chatName);               //Döda klienten
                chatRoom.getItems().removeElement(chatName);
            } catch (IOException ex) {
                Logger.getLogger(BootButtonListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (BadLocationException ex) {
        }
    }
}
