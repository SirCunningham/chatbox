package chatbox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.text.BadLocationException;

public class Listeners {
    
    public static ActionListener getBootButtonListener(final ChatRoom chatRoom) {
        return new ActionListener() {

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
            
        };
    }
    
}
