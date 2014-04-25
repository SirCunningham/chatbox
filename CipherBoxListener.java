package chatbox;


import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.text.AbstractDocument;

public class CipherBoxListener implements ItemListener {

    private final ChatRoom chatRoom;

    public CipherBoxListener(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
    
    //Välj krypteringssystem
    public void itemStateChanged(ItemEvent e) {
        String chosen = String.valueOf(chatRoom.cipherBox.getSelectedItem());
        switch (chosen) {
            case "caesar":
                ((AbstractDocument) chatRoom.keyPane.getDocument()).setDocumentFilter(new NewLineFilter(3, false));
                toggleType(chatRoom.TYPE_CAESAR);
                chatRoom.keyPane.setText(chatRoom.caesarKey);
                break;
            case "AES":
                ((AbstractDocument) chatRoom.keyPane.getDocument()).setDocumentFilter(new NewLineFilter(128));
                toggleType(chatRoom.TYPE_AES);
                chatRoom.keyPane.setText(chatRoom.AES.getDecodeKey());
                break;
            default:
                if (chatRoom.cipherButton.isSelected()) {
                    chatRoom.cipherButton.doClick();
                }
                toggleType(chatRoom.TYPE_NONE);
        }
    }
    public void toggleType(int type) {
        chatRoom.cipherButton.setEnabled(type != chatRoom.TYPE_NONE);
        chatRoom.keyLabel.setVisible(type != chatRoom.TYPE_NONE);
        chatRoom.keyPane.setVisible(type != chatRoom.TYPE_NONE);
        chatRoom.keyPane.setEditable(type != chatRoom.TYPE_AES);
        chatRoom.keyBox.setVisible(type != chatRoom.TYPE_NONE);
    }
}
