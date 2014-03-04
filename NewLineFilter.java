package chatbox;

import java.awt.*;
import javax.swing.text.*;

class NewLineFilter extends DocumentFilter {
    
    private int charLimit;
    
    public NewLineFilter(int charLimit) {
        this.charLimit = charLimit;
    }

    public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
        if ((fb.getDocument().getLength() + text.length()) <= charLimit) {
            super.insertString(fb, offset, text.replaceAll("\\n", ""), attr);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attr) throws BadLocationException {
        if ((fb.getDocument().getLength() + text.length()) <= charLimit) {
            super.replace(fb, offset, length, text.replaceAll("\\n", ""), attr);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }
}