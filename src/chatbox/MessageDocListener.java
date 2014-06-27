package chatbox;

import javax.swing.*;
import javax.swing.event.*;

public class MessageDocListener implements DocumentListener {

    private final ChatRoom chatRoom;

    public MessageDocListener(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        uncipher();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        uncipher();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }

    private void uncipher() {
        if (!chatRoom.lockDocument && chatRoom.getCipherButton().isSelected()) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    chatRoom.getCipherButton().doClick();
                }
            });
        }
    }
}