package chatbox;

import java.awt.event.*;
import javax.swing.*;

public class TabButtonListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        int index = ChatCreator.tabbedPane.indexOfTabComponent(button.getParent());
        ChatCreator.tabbedPane.remove(index);
        // behövs nytt index fortfarande?
        if ((index = ChatCreator.indices.indexOf(button)) != -1) {
            ChatCreator.chatRooms.get(index).alive = false;
            for (ChatRoom room : ChatCreator.chatRooms) {
                room.items.removeElement(ChatCreator.chatRooms.get(index));
            }
            ChatCreator.chatRooms.remove(index);
            ChatCreator.indices.remove(index);
        }
    }
}