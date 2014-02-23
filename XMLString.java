package chatbox;

import java.awt.*;

public class XMLString {

    private String xmlStr;

    public XMLString(String xmlStr) {
        this.xmlStr = xmlStr;
    }

    public String toText() {
        return xmlStr;
    }

    public Color toColor() {
        int index = xmlStr.indexOf("color");
        String hexColor = xmlStr.substring(index + 7, index + 13);
        return Color.decode("#" + hexColor);
    }
}
