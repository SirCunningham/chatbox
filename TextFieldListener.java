package chatbox;

import java.awt.event.*;
import javax.swing.text.*;

public class TextFieldListener implements FocusListener {

    @Override
    public void focusGained(FocusEvent e) {
        JTextComponent source = (JTextComponent) e.getSource();
        source.selectAll();
    }

    @Override
    public void focusLost(FocusEvent e) {
        JTextComponent source = (JTextComponent) e.getSource();
        source.select(0, 0);
    }
}