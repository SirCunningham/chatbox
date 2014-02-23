package chatbox;

import javax.swing.*;

public class TabButton extends JButton {

    private int index;

    public TabButton(Icon icon, int index) {
        super(icon);
        setContentAreaFilled(false);
        setOpaque(false);
        this.index = index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
