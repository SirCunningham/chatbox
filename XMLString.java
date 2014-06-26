package chatbox;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.apache.commons.codec.DecoderException;

public class XMLString {

    private final String xmlStr;
    private static AESCrypto AES;
    private final ArrayList<String> allowedTags;

    public XMLString(String xmlStr) {
        this.xmlStr = xmlStr;
        allowedTags = new ArrayList<>();
        allowedTags.add("message");
        allowedTags.add("text");
        allowedTags.add("kursiv");
        allowedTags.add("fetstil");
        /*
         try {
         AES = new AESCrypto();
         } catch (NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException ex) {
         ex.printStackTrace();
         }
         * 
         */
    }
    
    public static void main(String[] args) {
        String msg = "<message sender=\"INFO\">"
                + "<text color=\"0000ff\"><fileUsers users=\"[asd]\"></fileUsers>The server has been "
                + "abandoned!</text><disconnect /></message>";
        String[] names = getFileUsers(msg);
        System.out.println(names[0]);
    }
    
    public static String[] getFileUsers(String xmlStr) {
        if (xmlStr.matches("(.*)<fileUsers users=(.*)>(.*)</fileUsers>(.*)")) {
            int i = xmlStr.indexOf("<fileUsers users=\"[");
            String rest = xmlStr.substring(i);
            System.out.println(rest);
            i = rest.indexOf("users=\"[");
            rest = rest.substring(i);
            System.out.println(rest);
            i = rest.indexOf("\"[");
            i += 2;
            rest = rest.substring(i);
            i = rest.indexOf("]");
            rest = rest.substring(0,i);
            String[] names = rest.split(", ");
            return names;
        }
        return null;
    }

    public static String[] getUsers(String xmlStr) {
        boolean matches = xmlStr.matches("(.*)<connected users=(.*)>(.*)</connected>(.*)");
        if (matches) {
            int i = xmlStr.indexOf("<connected users=\"");
            String rest = xmlStr.substring(i);
            rest = rest.substring(rest.indexOf("[") + 1);
            String users = rest.substring(0, rest.indexOf("]"));
            System.out.println(users);
            return users.split(", ");
        }
        return null;
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
                                    if (AES == null) {
                                        AES = new AESCrypto();
                                    }
                                    msg += AES.decrypt(encryptedMsg, key);
                                } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException | DecoderException ex) {
                                    ex.printStackTrace();
                                }
                                break;
                        }
                        xmlStr = xmlStr.substring(xmlStr.indexOf("</encrypted>") + 12);
                        i = 0;
                    }

                }
            }
        }
        msg += xmlStr;
        return msg;
    }

    public static String decryptString(String xmlStr, String[] keys) {
        //If keys included in message, encrypt that part
        xmlStr = handleString(xmlStr);
        if (xmlStr.matches("(.*)<encrypted type=(.*)>(.*)")) {
            String msg = "";
            int i = xmlStr.indexOf("<encrypted");
            msg += xmlStr.substring(0, i);
            String rest = xmlStr.substring(i);
            System.out.println(rest);
            if (rest.matches("<encrypted type=\"(.*)\">(.*)")) {
                int k = rest.indexOf("</encrypted>");
                String type = rest.substring(rest.indexOf("\"") + 1, rest.indexOf("\">"));
                String enc = rest.substring(rest.indexOf(">") + 1, k);
                switch (type) {
                    case "caesar":
                        if (!keys[0].equals("")) {
                            msg += decryptCaesar(enc, Integer.valueOf(keys[0]));
                        } else {
                            msg += enc;
                        }
                        break;
                    case "AES":
                        try {
                            if (!keys[1].equals("")) {
                                if (AES == null) {
                                    AES = new AESCrypto();
                                }
                                msg += AES.decrypt(enc, keys[1]);
                            } else {
                                msg += enc;
                            }
                        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException | DecoderException ex) {
                        }
                        break;
                }
                rest = rest.substring(k + 12);
                msg += decryptString(rest, keys);
                xmlStr = msg;
            }
        }
        return xmlStr;

    }
    
    public static String getOldName(String xmlStr) {
        if (xmlStr.matches(String.format("<message sender=(.*)I just switched from my old name: (.*)</message>"))) {
            int i = xmlStr.indexOf("I just switched from my old name: ");
            String rest = xmlStr.substring(i);
            i = rest.indexOf(":");
            rest = rest.substring(i+2);
            String oldName = rest.substring(0,rest.indexOf("</"));
            return oldName;
        }
        return "";
    }

    public static String[] getKeys(String xmlStr) {
        String[] keys = new String[2];
        keys[0] = "";
        keys[1] = "";
        if (xmlStr.matches("(.*)<encrypted type=(.*) key=(.*)>(.*)")) {
            int i = xmlStr.indexOf("<encrypted");
            String rest = xmlStr.substring(i);
            if (rest.matches("<encrypted type=(.*) key=(.*)>(.*)")) {

                String fromType = rest.substring(16);
                String type = fromType.substring(1, fromType.indexOf((" ")) - 1);
                String fromKey = fromType.substring(fromType.indexOf(" ") + 6);

                String key = fromKey.substring(0, fromKey.indexOf(">") - 1);
                switch (type) {
                    case "caesar":
                        keys[0] = key;
                        break;
                    case "AES":
                        keys[1] = key;
                        break;
                }
                rest = fromKey.substring(fromKey.indexOf(">") + 1);
                String[] otherKey = getKeys(rest);
                if (keys[0].equals("")) {
                    keys[0] = otherKey[0];
                }
                if (keys[1].equals("")) {
                    keys[1] = otherKey[1];
                }
            }
        }
        return keys;
    }
    
    public static String getBootedUser(String xmlStr, String serverName) {
        if (xmlStr.matches(String.format("<message sender=\"%s\"><text color=(.*)>(.*) got the boot(.*)</text></message>", serverName))) {
            int i = xmlStr.indexOf("color=");
            String rest = xmlStr.substring(i);
            i = rest.indexOf(">");
            rest = rest.substring(i+1);
            i = rest.indexOf(" got the boot");
            return rest.substring(0, i);
        }
        return null;
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
        if (xmlMsg != null) {
            if (xmlMsg.matches("<message sender=(.*)>(.*)</message>")) {
                int index = xmlMsg.indexOf("sender");
                return xmlMsg.substring(index + 8, xmlMsg.indexOf(">") - 1) + ": ";
            }
        }
        return null;
    }
    
    public static String getMessage(String xmlMsg) {
        if (xmlMsg != null) {
            if (xmlMsg.matches("(.*)<text(.*)>(.*)</text>(.*)")) {
                String[] strings = xmlMsg.split("<text(.*?)>");
                for (String str : strings) {
                    if (str.contains("</text>")) {
                        return str.substring(0, str.indexOf("</text>"));
                    }
                }
            }
        }
        return null;
    }
    
    public static int indexOf(Pattern pattern, String string) {
        Matcher matcher = pattern.matcher(string);
        return matcher.find() ? matcher.start() : -1;
    }
    
    public static String getFileName(String xmlMsg) {
        if (xmlMsg != null) {
            if (xmlMsg.matches("(.*)<filerequest(.*)name=(.*)>(.*)</filerequest>(.*)")) {
                int index = xmlMsg.indexOf("name");
                String relMsg = xmlMsg.substring(index + 6);
                return relMsg.substring(0, indexOf(Pattern.compile("\"( |>)"), relMsg));
            }
        }
        return null;
    }
    
    public static String getFileSize(String xmlMsg) {
        if (xmlMsg != null) {
            if (xmlMsg.matches("(.*)<filerequest(.*)size=(.*)>(.*)</filerequest>(.*)")) {
                int index = xmlMsg.indexOf("size");
                String relMsg = xmlMsg.substring(index + 6);
                return relMsg.substring(0, indexOf(Pattern.compile("\"( |>)"), relMsg));
            }
        }
        return null;
    }
    
    public static String getFileDescription(String xmlMsg) {
        if (xmlMsg != null) {
            if (xmlMsg.matches("(.*)<filerequest(.*)>(.*)</filerequest>(.*)")) {
                String[] strings = xmlMsg.split("<filerequest(.*?)>");
                for (String str : strings) {
                    if (str.contains("</filerequest>")) {
                        return str.substring(0, str.indexOf("</filerequest>"));
                    }
                }
            }
        }
        return null;
    }

    public static String getSenderWithoutColon(String xmlMsg) {
        String withColon = getSender(xmlMsg);
        if (withColon == null) {
            return withColon;
        }
        return withColon.substring(0, withColon.length() - 2);
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

    private static String decryptCaesar(String text, int shift) {
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
        //inte nöjd med denna funktionalitet!
        //hex = hex.replaceAll("<", "&lt;");
        //hex = hex.replaceAll(">", "&gt;");
        return hex;
    }
}
