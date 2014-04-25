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
    
    private void uncipher(){
        if (!chatRoom.lockDocument && chatRoom.cipherButton.isSelected()) {
            Runnable doHighlight = new Runnable() {

                @Override
                public void run() {
                    chatRoom.cipherButton.doClick();
                }
            };
            SwingUtilities.invokeLater(doHighlight);
        }
    }
}