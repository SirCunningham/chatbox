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
        String chosen = String.valueOf(chatRoom.getCipherBox().getSelectedItem());
        switch (chosen) {
            case "caesar":
                ((AbstractDocument) chatRoom.getKeyPane().getDocument()).setDocumentFilter(new NewLineFilter(3, false));
                toggleType(ChatRoom.TYPE_CAESAR);
                chatRoom.getKeyPane().setText(chatRoom.caesarKey);
                break;
            case "AES":
                ((AbstractDocument) chatRoom.getKeyPane().getDocument()).setDocumentFilter(new NewLineFilter(128));
                toggleType(ChatRoom.TYPE_AES);
                chatRoom.getKeyPane().setText(chatRoom.AES.getDecodeKey());
                break;
            default:
                if (chatRoom.getCipherButton().isSelected()) {
                    chatRoom.getCipherButton().doClick();
                }
                toggleType(ChatRoom.TYPE_NONE);
        }
    }

    public void toggleType(int type) {
        chatRoom.getCipherButton().setEnabled(type != ChatRoom.TYPE_NONE);
        chatRoom.getKeyLabel().setVisible(type != ChatRoom.TYPE_NONE);
        chatRoom.getKeyPane().setVisible(type != ChatRoom.TYPE_NONE);
        chatRoom.getKeyPane().setEditable(type != ChatRoom.TYPE_AES);
        chatRoom.getKeyBox().setVisible(type != ChatRoom.TYPE_NONE);
    }
}