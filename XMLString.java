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

    public String decryptCaesar(String text, int shift) {
        char[] chars = text.toCharArray();
        for (int i = 0; i < text.length(); i++) {
            char c = chars[i];
            if (c >= 32 && c <= 127) {
                int x = c - 32;
                x = (x - shift) % 96;
                if (x < 0) {
                    x += 96;
                }
                chars[i] = (char) (x + 32);
            }
        }
        return new String(chars);
    }
}
