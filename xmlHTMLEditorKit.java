package chatbox;

import java.io.IOException;

import javax.swing.text.*;
import javax.swing.text.html.*;

public class xmlHTMLEditorKit extends HTMLEditorKit {
    //Måste förbättras

    public void insertHTML(int offset, String html,
            int popDepth, int pushDepth, HTML.Tag insertTag, HTMLDocument doc) throws
            BadLocationException, IOException {
        String color = XMLString.toHexColor(html);
        //thr.keyRequest(html);

        super.insertHTML((HTMLDocument) doc,
                offset, "<font color=\"" + color + "\">" + XMLString.showName(XMLString.handleString(html))
                + "</font>", popDepth, pushDepth, insertTag);
        /*else {
        super.insertHTML((HTMLDocument) chatBox.getDocument(),
        offset, "Något blev fel i meddelandet", popDepth, pushDepth, insertTag);
        }
        /*
        if (thr.timer.isRunning()) {
        System.out.println(thr.timer.getType());
        System.out.println(html);
        System.out.println(XMLString.getEncryptedType(html));
        if (thr.timer.getType().equals(XMLString.getEncryptedType(html))) {
        thr.timer.stop();
        chatRoom.nameToKey.put(XMLString.getSender(html), new ArrayList<String>());
        }
        }
         * 
         */

    }
}