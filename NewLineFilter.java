package chatbox;

import java.awt.*;
import javax.swing.text.*;

class NewLineFilter extends DocumentFilter {

    private int charLimit;
    private boolean notOnlyNumbers;

    public NewLineFilter(int charLimit, boolean notOnlyNumbers) {
        this.charLimit = charLimit;
        this.notOnlyNumbers = notOnlyNumbers;
    }

    public NewLineFilter(int charLimit) {
        this(charLimit, true);
    }

    public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
        if ((fb.getDocument().getLength() + text.length()) <= charLimit) {
            if (notOnlyNumbers) {
                super.insertString(fb, offset, text.replaceAll("\\n", ""), attr);
            } else {
                super.insertString(fb, offset, text.replaceAll("\\D", ""), attr);
            }
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