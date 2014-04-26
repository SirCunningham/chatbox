package chatbox;

import java.awt.event.*;
import javax.swing.text.*;

public class CipherBoxListener implements ItemListener {

    private final ChatRoom chatRoom;

    public CipherBoxListener(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    //Välj krypteringssystem
    @Override
    public void itemStateChanged(ItemEvent e) {
        String chosen = String.valueOf(chatRoom.cipherBox.getSelectedItem());
        switch (chosen) {
            case "caesar":
                ((AbstractDocument) chatRoom.keyPane.getDocument()).setDocumentFilter(new NewLineFilter(3, false));
                toggleType(ChatRoom.TYPE_CAESAR);
                chatRoom.keyPane.setText(chatRoom.caesarKey);
                break;
            case "AES":
                ((AbstractDocument) chatRoom.keyPane.getDocument()).setDocumentFilter(new NewLineFilter(128));
                toggleType(ChatRoom.TYPE_AES);
                chatRoom.keyPane.setText(chatRoom.AES.getDecodeKey());
                break;
            default:
                if (chatRoom.cipherButton.isSelected()) {
                    chatRoom.cipherButton.doClick();
                }
                toggleType(ChatRoom.TYPE_NONE);
        }
    }

    public void toggleType(int type) {
        chatRoom.cipherButton.setEnabled(type != ChatRoom.TYPE_NONE);
        chatRoom.keyLabel.setVisible(type != ChatRoom.TYPE_NONE);
        chatRoom.keyPane.setVisible(type != ChatRoom.TYPE_NONE);
        chatRoom.keyPane.setEditable(type != ChatRoom.TYPE_AES);
        chatRoom.keyBox.setVisible(type != ChatRoom.TYPE_NONE);
    }
}