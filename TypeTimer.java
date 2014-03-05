package chatbox;

import java.awt.event.ActionListener;
import javax.swing.Timer;

public class TypeTimer extends Timer {

    private String type;
    private boolean foundType = false;

    TypeTimer(int delay, ActionListener listener, String type) {
        super(delay, listener);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void foundType(boolean foundtype) {
        this.foundType = foundtype;
    }

    public boolean isFound() {
        return foundType;
    }
}
