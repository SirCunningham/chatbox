package chatbox;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

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

    public void handleString() {
        String msg = "";
        for (int i = 0; i < xmlStr.length(); i++) {
            if (i == xmlStr.indexOf("<encrypted")) {
                msg += xmlStr.substring(0, i);
                xmlStr = xmlStr.substring(i + 10);
                String temp = xmlStr.substring(xmlStr.indexOf("\"") + 1);
                String type = temp.substring(0, temp.indexOf("\""));
                String key = temp.substring(temp.indexOf("key") + 5,
                        temp.indexOf(">") - 1);
                String encryptedMsg = temp.substring(temp.indexOf(">") + 1,
                        temp.indexOf("</encrypted>"));
                if (type.equals("caesar")) {
                    msg += decryptCaesar(encryptedMsg, Integer.valueOf(key));
                }
                xmlStr = " " + xmlStr.substring(xmlStr.indexOf("</encrypted>") + 12);
                i = 0;
            }
        }
        msg += xmlStr;
        xmlStr = msg;
    }

    public static String decryptCaesar(String text, int shift) {
        text = hexToString(text);
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

    //stackoverflow.com/questions/15749475/java-string-hex-to-string-ascii-with-accentuation
    public static String hexToString(String hex) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 0; i < hex.length(); i += 2) {
            String str = hex.substring(i, i + 2);
            int byteVal = Integer.parseInt(str, 16);
            baos.write(byteVal);
        }
        String s = new String(baos.toByteArray(), Charset.forName("UTF-8"));
        return s;
    }
}
