package chatbox;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.apache.commons.codec.DecoderException;

public class XMLString {

    private String xmlStr;
    private static AESCrypto AES;
    private ArrayList<String> allowedTags;

    public XMLString(String xmlStr) {
        this.xmlStr = xmlStr;
        allowedTags = new ArrayList<>();
        allowedTags.add("message");
        allowedTags.add("text");
        allowedTags.add("kursiv");
        allowedTags.add("fetstil");
        try {
            AES = new AESCrypto();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }
    public static void main(String[] args) {
        String test = "hej";
        String enc = Encryption.encrypt("caesar", test, "10", AES);
        String msg = "<message sender=\"asd\"><text color=\"asd\"><encrypted type=\"caesar\" key=\"10\">"+enc+"</encrypted><encrypted type=\"AES\" key=\"sdadsadasds\"> gsergserg </encrypted></text></message>";
        int i =msg.indexOf("<encrypted");
        //System.out.println(msg.matches("(.*)<encrypted type=(.*) key=(.*)>(.*)"));
        //System.out.println(handleString(msg));
        String keys[] = getKeys(msg);
        System.out.println(keys[0]);
        System.out.println(keys[1]);
    }
    
    public static String handleString(String xmlStr) {
        String msg = "";
        if (xmlStr.contains("<encrypted")) {
            for (int i = 0; i < xmlStr.length(); i++) {
                if (i == xmlStr.indexOf("<encrypted")) {
                    String rest = xmlStr.substring(i);
                    if (rest.matches("<encrypted type=(.*) key=(.*)>(.*)")) {
                        msg += xmlStr.substring(0, i);
                        xmlStr = xmlStr.substring(i + 10);
                        String temp = xmlStr.substring(xmlStr.indexOf("\"") + 1);
                        String type = temp.substring(0, temp.indexOf("\""));
                        String key = temp.substring(temp.indexOf("key") + 5,
                                temp.indexOf(">") - 1);
                        String encryptedMsg = temp.substring(temp.indexOf(">") + 1,
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
            }
        }
        msg += xmlStr;
        return msg;
    }
    
    public static String[] getKeys(String xmlStr) {
        String[] keys = new String[2];
        keys[0]="";
        keys[1]="";
        if (xmlStr.matches("(.*)<encrypted type=(.*) key=(.*)>(.*)")) {
            int i = xmlStr.indexOf("<encrypted");
            String rest = xmlStr.substring(i);
            //System.out.println(rest);
            if (rest.matches("<encrypted type=(.*) key=(.*)>(.*)")) {
                String fromType = rest.substring(16);
                //.out.println(fromType);
                String type=fromType.substring(1,fromType.indexOf((" "))-1);
                //System.out.println(type);
                String fromKey = fromType.substring(fromType.indexOf(" ")+6);

                String key = fromKey.substring(0, fromKey.indexOf(">")-1);
                switch (type) {
                    case "caesar":
                        keys[0]=key;
                        break;
                    case "AES":
                        keys[1]=key;
                        break;
                }
                rest = fromKey.substring(fromKey.indexOf(">")+1);
                System.out.println(rest);
                String[] otherKey = getKeys(rest);
                if (keys[0].equals("")) {
                    keys[0]=otherKey[0];
                }
                if (keys[1].equals("")) {
                    keys[1]=otherKey[1];
                }
            }
        } 
        return keys;
    }

    public String toText() {
        return xmlStr;
    }

    public static boolean isCorrect(String xmlStr) {
        return xmlStr.matches("<message .*>.*</message>");
    }

    public static String getEncryptedType(String xmlStr) {
        String[] strings = xmlStr.split("type=\"");
        for (String str : strings) {
            if (str.contains("</encrypted>")) {
                return str.substring(0, str.indexOf("\""));
            }
        }
        return null;
    }

    public static String removeKeyRequest(String xmlStr) {
        return xmlStr.replaceAll("<keyrequest.*>.*</keyrequest>", "");
    }

    public static String removeFileRequest(String xmlStr) {
        return xmlStr.replaceAll("<filerequest.*>.*</filerequest>", "");
    }

    public static String getKeyRequestType(String xmlStr) {
        String[] strings = xmlStr.split("keyrequest type=");
        for (String str : strings) {
            if (str.contains("</keyrequest>")) {
                return str.substring(1, str.indexOf(">") - 1);
            }
        }
        return null;
    }

    public static String toHexColor(String xmlStr) {
        if (xmlStr.contains("color")) {
            int index = xmlStr.indexOf("color");
            String hexColor = xmlStr.substring(index + 7, index + 13);
            return hexColor;
        }
        return null;
    }

    public Color toColor() {
        if (xmlStr.contains("color")) {
            int index = xmlStr.indexOf("color");
            String hexColor = xmlStr.substring(index + 7, index + 13);
            return Color.decode("#" + hexColor);
        }
        return null;

    }

    public static String removeBoldEmphTags(String hex) {
        if (hex.matches("<kursiv>.*</kursiv>")) {
            hex = hex.replaceAll("<(|/)kursiv>", "");
        }
        if (hex.matches("<fetstil>.*</fetstil>")) {
            hex = hex.replaceAll("<(|/)fetstil>", "");
        }

        hex = hex.replaceAll("<(\\w*.)>.*</\1>", ""); //Tveksamt
        return hex;
    }



    public static String getSender(String xmlMsg) {
        int index = xmlMsg.indexOf("sender");
        return xmlMsg.substring(index + 8, xmlMsg.indexOf(">") - 1) + ": ";
    }

    public static String showName(String xmlMsg) {
        int i = xmlMsg.indexOf(">");
        for (int k = i + 1; k < xmlMsg.length(); ++k) {
            if (xmlMsg.substring(k, k + 1).equals(">")) {
                xmlMsg = xmlMsg.substring(0, k + 1) + getSender(xmlMsg) + xmlMsg.substring(k + 1);
                return removeBoldEmphTags(xmlMsg);
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

    public static String convertAngle(String hex) {
        hex = hex.replaceAll("&", "&amp;");
        hex = hex.replaceAll("<", "&lt;");
        hex = hex.replaceAll(">", "&gt;");
        return hex;
    }
}