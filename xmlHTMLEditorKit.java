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
    IOThread thr;
    public void insertHTML(IOThread thr, int offset, String html, 
            int popDepth, int pushDepth, HTML.Tag insertTag) throws 
            BadLocationException, IOException {
        String color = new XMLString(html).toHexColor();
        keyRequest(html);
        this.thr = thr;
        this.html = html;
        super.insertHTML((HTMLDocument)thr.messageBox.chatBox.getDocument(), 
                offset, "<font color=\""+color+"\">"+XMLString.showName(html)+
                "</font>", popDepth, pushDepth, insertTag);
    }
    
    public void keyRequest(String html) {
        if (html.indexOf("</keyrequest>")!=-1) {
            int reply = JOptionPane.showConfirmDialog(null,
                    String.format("%s sends a keyrequest of type %s.\n Send key?",
                    XMLString.getSender(html), XMLString.getKeyRequestType(html)),
                    "Kill", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                thr.appendToPane(String.format("<message sender=\"%s\">"
                                + "<text color=\"%s\"><encrypted key=%s type=%s> </encrypted></text></message>",
                                thr.messageBox.namePane.getText(), thr.messageBox.color,
                                thr.messageBox.getKey(XMLString.getKeyRequestType(html)),
                                XMLString.getKeyRequestType(html)));
            } else {
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
