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
        this.thr = thr;
        this.html = html;
        keyRequest(html);
        super.insertHTML((HTMLDocument)thr.messageBox.chatBox.getDocument(), 
                offset, "<font color=\""+color+"\">"+XMLString.showName(html)+
                "</font>", popDepth, pushDepth, insertTag);
        
        if (thr.timer.isRunning()) {
            System.out.println(thr.timer.getType());
            System.out.println(XMLString.getEncryptedType(html));
            if (thr.timer.getType().equals(XMLString.getEncryptedType(html))) {
                thr.timer.foundType(true);
                thr.timer.stop();
            }
        }
    }

    public void keyRequest(String html) {
        if (html.indexOf("</keyrequest>")!=-1) {
            int reply = JOptionPane.showConfirmDialog(null,
                    String.format("%s sends a keyrequest of type %s.\n Send key?",
                    XMLString.getSender(html), XMLString.getKeyRequestType(html)),
                    "Kill", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                thr.appendToPane(String.format("<message sender=\"%s\">"
                                + "<text color=\"%s\"><encrypted key=\"%s\" type=\"%s\"> </encrypted></text></message>",
                                thr.messageBox.namePane.getText(), thr.messageBox.color,
                                thr.messageBox.getKey(XMLString.getKeyRequestType(html)),
                                XMLString.getKeyRequestType(html)));
            } else {
            }
        }
    }
}
