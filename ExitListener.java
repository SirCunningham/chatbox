package chatbox;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class ExitListener extends WindowAdapter {
    
    private final ChatCreator chatCreator;

    public ExitListener(ChatCreator chatCreator) {
        this.chatCreator = chatCreator;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        int reply = JOptionPane.showConfirmDialog(chatCreator.frame,
                "Are you sure you want to exit ChatBox?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
            ArrayList<ChatRoom> roomArray = new ArrayList<>();
            for (ChatRoom room : chatCreator.chatRooms) {
                roomArray.add(room);
            }
            for (ChatRoom room : roomArray) {
                room.speedyDelete = true;
                room.closeButton.doClick();
            }
            System.exit(0);
        }
    }
};
