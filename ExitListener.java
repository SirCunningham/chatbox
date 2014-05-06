package chatbox;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class ExitListener extends WindowAdapter {

    @Override
    public void windowClosing(WindowEvent e) {
        int reply = JOptionPane.showConfirmDialog(ChatCreator.frame,
                "Are you sure you want to exit ChatBox?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
            ArrayList<ChatRoom> roomArray = new ArrayList<>();
            for (ChatRoom room : ChatCreator.chatRooms) {
                roomArray.add(room);
            }
            for (ChatRoom room : roomArray) {
                room.speedyDelete = true;
                room.getCloseButton().doClick();
            }
            System.exit(0);
        }
    }
};