package chatbox;

import java.awt.event.*;

public class CreatorListener implements KeyListener {
    
    private final ChatCreator chatCreator;

    public CreatorListener(ChatCreator chatCreator) {
        this.chatCreator = chatCreator;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                chatCreator.startButton.doClick();
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_KP_LEFT:
            case KeyEvent.VK_LEFT:
                chatCreator.clientButton.setSelected(true);
                chatCreator.startButton.setText("Join server");
                chatCreator.hostLabel.setEnabled(true);
                chatCreator.hostPane.setEnabled(true);
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_KP_RIGHT:
            case KeyEvent.VK_RIGHT:
                chatCreator.serverButton.setSelected(true);
                chatCreator.startButton.setText("Create server");
                chatCreator.hostLabel.setEnabled(false);
                chatCreator.hostPane.setEnabled(false);
                break;
            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
