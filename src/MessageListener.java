package chatbox;

import java.awt.event.*;

class MessageListener implements KeyListener {

    public final ChatRoom chatRoom;

    public MessageListener(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            chatRoom.getSendButton().doClick();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}