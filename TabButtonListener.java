package chatbox;

import java.awt.event.*;
import javax.swing.*;

public class TabButtonListener implements ActionListener {
    
    ChatRoom chatRoom;
    public TabButtonListener(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        int index = ChatCreator.tabbedPane.indexOfTabComponent(button.getParent());
        ChatCreator.tabbedPane.remove(index);
        // behövs nytt index fortfarande?
        if ((index = ChatCreator.indices.indexOf(button)) != -1) {
            /*
            ChatCreator.chatNames.get(index)
            for (ChatRoom room : ChatCreator.chatRooms) {
                room.getItems().removeElement(ChatCreator.chatRooms.get(index));
            }
            */
            ChatCreator.chatNames.remove(index);
            ChatCreator.indices.remove(index);
        }
    }
}