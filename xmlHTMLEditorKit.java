package chatbox;

import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

public class xmlHTMLEditorKit extends HTMLEditorKit {
    //Måste förbättras
    public void insertHTML(IOThread thr, int offset, String html,
            int popDepth, int pushDepth, HTML.Tag insertTag) throws
            BadLocationException, IOException {
        String color = new XMLString(html).toHexColor();
        thr.keyRequest(html);
        super.insertHTML((HTMLDocument) thr.messageBox.chatBox.getDocument(),
                offset, "<font color=\"" + color + "\">" + XMLString.showName(html)
                + "</font>", popDepth, pushDepth, insertTag);

        if (thr.timer.isRunning()) {
            System.out.println(thr.timer.getType());
            System.out.println(html);
            System.out.println(XMLString.getEncryptedType(html));
            if (thr.timer.getType().equals(XMLString.getEncryptedType(html))) {
                thr.timer.foundType(true);
                thr.timer.stop();
            }
        }
    }

}
