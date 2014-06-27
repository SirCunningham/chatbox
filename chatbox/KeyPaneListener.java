package chatbox;

import javax.swing.event.*;

public class KeyPaneListener implements DocumentListener {

    private final ChatRoom chatRoom;

    KeyPaneListener(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updateKeys();
    }

    @Override
    public void insertUpdate(DocumentEvent arg0) {
        updateKeys();
    }

    private void updateKeys() {
        String keyType = (String) chatRoom.getCipherBox().getSelectedItem();
        String name = chatRoom.getName();
        String[] oldKeys = chatRoom.nameToKey.get(name);
        switch (keyType) {
            case "caesar":
                oldKeys[0] = chatRoom.getKeyPane().getText();
                break;
            case "AES":
                oldKeys[1] = chatRoom.getKeyPane().getText();
                break;
            default:
                break;
        }
        chatRoom.nameToKey.put(name, oldKeys);
    }
}
