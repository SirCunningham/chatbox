package chatbox;

import java.awt.event.*;

public class TabListener implements KeyListener {

    private final ChatRoom chatRoom;

    public TabListener(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int index = ChatCreator.tabbedPane.getSelectedIndex();
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                chatRoom.sendButton.doClick();
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_KP_LEFT:
            case KeyEvent.VK_LEFT:
                if (index > 0) {
                    ChatCreator.tabbedPane.setSelectedIndex(index - 1);
                } else {
                    // refocus tab 0, but how? same problem as focus in general...
                }
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_KP_RIGHT:
            case KeyEvent.VK_RIGHT:
                ChatCreator.tabbedPane.setSelectedIndex(index + 1);
                break;
            default:
                break;
        }
        chatRoom.messagePane.requestFocusInWindow();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}