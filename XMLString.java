package chatbox;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.apache.commons.codec.DecoderException;

public class XMLString {

    private String xmlStr;
    private AESCrypto AES;

    public XMLString(String xmlStr) {
        this.xmlStr = xmlStr;
        try {
            AES = new AESCrypto();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }

    public String toText() {
        return xmlStr;
    }
    
    public String toHexColor() {
        if (xmlStr.indexOf("color")!=-1) {
            int index = xmlStr.indexOf("color");
            String hexColor = xmlStr.substring(index + 7, index + 13);
            return hexColor;
        }
        return null;
    }

    public Color toColor() {
        if (xmlStr.indexOf("color")!=-1) {
            int index = xmlStr.indexOf("color");
            String hexColor = xmlStr.substring(index + 7, index + 13);
            return Color.decode("#" + hexColor);
        }
        return null;

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
                String encryptedMsg = temp.substring(temp.indexOf("&gt") + 1,
                        temp.indexOf("</encrypted>"));
                switch (type) {
                    case "caesar":
                        msg += decryptCaesar(encryptedMsg, Integer.valueOf(key));
                        break;
                    case "AES":
                        try {
                            msg += AES.decrypt(encryptedMsg, key);
                        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException | DecoderException ex) {
                            ex.printStackTrace();
                        }
                        break;
                }
                xmlStr = " " + xmlStr.substring(xmlStr.indexOf("</encrypted>") + 12);
                i = 0;
            }
        }
        msg += xmlStr;
        xmlStr = msg;
    }
    
    public static String getSender(String xmlMsg) {
        int i = xmlMsg.indexOf("sender");
        String name = xmlMsg.substring(i + 8, xmlMsg.indexOf(">") - 1);
        return name.substring(0, 1).toUpperCase() + name.substring(1) + ": ";
    }
    
    public static String showName(String xmlMsg) {
        int i = xmlMsg.indexOf(">");
        for (int k = i + 1; k < xmlMsg.length(); ++k) {
            if (xmlMsg.substring(k, k + 1).equals(">")) {
                xmlMsg = xmlMsg.substring(0, k + 1) + getSender(xmlMsg) + xmlMsg.substring(k + 2);
                return xmlMsg;
            }
        }
        return null;
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