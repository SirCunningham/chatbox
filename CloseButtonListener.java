/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatbox;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;

// Stäng av hela programmet
public class CloseButtonListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        ArrayList<String> roomArray = new ArrayList<>();
        for (String chatName : ChatCreator.chatNames) {
            roomArray.add(chatName);
        }
        /*
        for (ChatRoom room : roomArray) {
            room.getCloseButton().doClick();
        }
        */
        if (ChatCreator.chatNames.isEmpty()) {
            int reply = JOptionPane.showConfirmDialog(ChatCreator.frame,
                    "Are you sure you want to exit ChatBox?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                System.exit(0);
            } else {
                JOptionPane.showMessageDialog(ChatCreator.frame,
                        "Good choice. Everyone's finger can slip!");
            }
        }
    }
}