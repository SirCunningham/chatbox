package chatbox;

import java.awt.event.*;
import javax.swing.text.*;

public class BootButtonListener implements ActionListener {

    private final ChatRoom chatRoom;

    public BootButtonListener(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String message = chatRoom.messagePane.getText();
        int i = chatRoom.list.getSelectedIndex();
        try {
            chatRoom.doc.remove(0, message.length());
            ChatRoom room = (ChatRoom) chatRoom.items.getElementAt(i);
            chatRoom.doc.insertString(0,
                    String.format("%s got the boot",
                    room.getName()), chatRoom.style);
            chatRoom.sendButton.doClick();
            chatRoom.doc.insertString(0, message, chatRoom.style);
            room.alive = false;                  //Döda klienten
            for (ChatRoom mBox : ChatCreator.chatRooms) {
                mBox.items.removeElement(room);
            }
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
        while (i >= 0) {
            i = chatRoom.list.getSelectedIndex();
        }
    }
}