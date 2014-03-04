package chatbox;

import java.io.IOException;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

public class xmlHTMLEditorKit extends HTMLEditorKit {
    
    public void insertHTML(HTMLDocument doc, int offset, String html, 
            int popDepth, int pushDepth, HTML.Tag insertTag) throws 
            BadLocationException, IOException {
        String color = new XMLString(html).toHexColor();
        super.insertHTML(doc, offset, "<font color=\""+color+"\">"+html+
                "</font>", popDepth, pushDepth, insertTag);
    }
}
