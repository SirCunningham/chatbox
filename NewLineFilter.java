package chatbox;

import javax.swing.text.*;

class NewLineFilter extends DocumentFilter {

    public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
        fb.insertString(offset, text.replaceAll("\\n", ""), attr);
    }

    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attr) throws BadLocationException {
        fb.insertString(offset, text.replaceAll("\\n", ""), attr);
    }
}