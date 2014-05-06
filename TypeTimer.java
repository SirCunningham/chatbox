package chatbox;

import java.awt.event.ActionListener;
import javax.swing.Timer;

public class TypeTimer extends Timer {

    private String type;
    private boolean foundType = false;

    TypeTimer(int delay, ActionListener listener, String type) {
        super(delay, listener);
        this.setRepeats(false);
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

    /**
    TypeTimer timer = new TypeTimer(10 * 1000, new TimerListener(), null);
    timer.setRepeats(false);

    public class TimerListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            out.println(String.format("<message sender=\"%s\">"
                    + "<text color=\"%s\">Jag fick ingen nyckel av "
                    + "typen %s inom en minut och antar nu att ni inte har implementerat "
                    + "detta!</text></message>",
                    chatRoom.getNamePane().getText(), chatRoom.color, timer.getType()));

            appendToPane(String.format("<message sender=\"%s\">"
                    + "<text color=\"%s\">Jag fick ingen nyckel av "
                    + "typen %s inom en minut och antar nu att ni inte har implementerat "
                    + "detta!</text></message>", chatRoom.getNamePane().getText(), chatRoom.color, timer.getType()));
        }
    }
    **/