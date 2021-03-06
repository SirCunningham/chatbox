package chatbox;

import java.awt.event.*;
import javax.swing.*;

public class CreatorListener implements KeyListener {

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            ChatCreator.startButton.doClick();
        } else {
            JComponent source = (JComponent) e.getSource();
            if (source == ChatCreator.clientButton || source == ChatCreator.serverButton
                    || source == ChatCreator.startButton || source == ChatCreator.closeButton) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A:
                    case KeyEvent.VK_KP_LEFT:
                    case KeyEvent.VK_LEFT:
                        ChatCreator.clientButton.setSelected(true);
                        ChatCreator.startButton.setText("Join server");
                        ChatCreator.hostLabel.setEnabled(true);
                        ChatCreator.hostPane.setEnabled(true);
                        break;
                    case KeyEvent.VK_D:
                    case KeyEvent.VK_KP_RIGHT:
                    case KeyEvent.VK_RIGHT:
                        ChatCreator.serverButton.setSelected(true);
                        ChatCreator.startButton.setText("Create server");
                        ChatCreator.hostLabel.setEnabled(false);
                        ChatCreator.hostPane.setEnabled(false);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}