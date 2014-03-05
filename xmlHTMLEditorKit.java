package chatbox;

import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

public class xmlHTMLEditorKit extends HTMLEditorKit {
    //Måste förbättras
    String html;
    public void insertHTML(HTMLDocument doc, int offset, String html, 
            int popDepth, int pushDepth, HTML.Tag insertTag) throws 
            BadLocationException, IOException {
        String color = new XMLString(html).toHexColor();
        keyRequest(html);
        this.html = html;
        super.insertHTML(doc, offset, "<font color=\""+color+"\">"+XMLString.showName(html)+
                "</font>", popDepth, pushDepth, insertTag);
    }
    
    public void keyRequest(String html) {
        if (html.indexOf("<keyrequest>")== -1) {
            int reply = JOptionPane.showConfirmDialog(null,
                    String.format("%s sends a keyrequest of type ",
                    XMLString.getSender(html)),
                    "Kill", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(null, "Hello killer!");
            } else {
                JOptionPane.showMessageDialog(null, "Goodbye!");
            }
        }
    }
    public void setHTML(String html) {
        this.html=html;
    }
    public String getHTML() {
        return html;
    }
}
