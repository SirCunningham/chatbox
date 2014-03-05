package chatbox;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import javax.crypto.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.spec.*;
import java.math.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test extends ArrayList {

    private ArrayList<String> allowedTags = new ArrayList<String>();

    public static void main(String[] args) {
        String test = "123dsf";
        System.out.println(test.replaceAll("\\D", ""));
        String test2 = "3686c9af32225647c73cd4de1e7771022d423b33f14cc58cab6429fb8ea38099";
        System.out.println(test2.length());
        String xmlString = "<message>HELLO!</message> ";
        try {
            test = encryptCaesar("men det är detta", 5);
            test2 = encryptCaesar("och även detta", 10);

        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        String xmlTest = "<message sender=\"dante\"><text color=\"asdfasf\">Detta är inte krypterat "
                + "<encrypted type=\"caesar\" key=\"5\">" + test + "</encrypted>"
                + "<encrypted type=\"caesar\" key=\"10\">" + test2 + "</encrypted></text></message>";
        String xmlTest2 = "<message sender=\"dante\"><text color=\"FF0000\"> <keyrequest type=\"AES\">asdasdasdasd</keyrequest></text></message>";
        System.out.println(handleString(xmlTest));
        System.out.println(removeBoldEmphTags(xmlTest2));
        System.out.println(getEncryptedType(xmlTest));
        System.out.println(getKeyRequestType(xmlTest2));
    }

    public Test() {
        allowedTags.add("message");
        allowedTags.add("test");
        allowedTags.add("kursiv");
        allowedTags.add("fetstil");
    }

    public static String getEncryptedType(String xmlStr) {
        String[] strings = xmlStr.split("<encrypted type=\"");
        for (String str : strings) {
            if (str.indexOf("</encrypted>") != -1) {
                return str.substring(0, str.indexOf("\""));
            }
        }
        return null;
    }

    public static String getKeyRequestType(String xmlStr) {
        String[] strings = xmlStr.split("keyrequest type=");
        for (String str : strings) {
            if (str.indexOf("</keyrequest>") != -1) {
                return str.substring(1, str.indexOf(">") - 1);
            }
        }
        return null;
    }

    public static String removeBoldEmphTags(String hex) {
        hex = hex.replaceAll("<kursiv>", "");
        hex = hex.replaceAll("</kursiv>", "");
        hex = hex.replaceAll("<fetstil>", "");
        hex = hex.replaceAll("</fetstil>", "");
        return hex;
    }

    public static String getSender(String xmlMsg) {
        int i = xmlMsg.indexOf("sender");
        String name = xmlMsg.substring(i + 8, xmlMsg.indexOf(">") - 1);
        return name.substring(0, 1).toUpperCase() + name.substring(1) + ":";
    }

    public static String showName(String xmlMsg) {
        int i = xmlMsg.indexOf(">");
        for (int k = i + 1; k < xmlMsg.length(); ++k) {
            if (xmlMsg.substring(k, k + 1).equals(">")) {
                xmlMsg = xmlMsg.substring(0, k + 1) + getSender(xmlMsg) + xmlMsg.substring(k + 1);
                return xmlMsg;
            }
        }
        return null;
    }

    private static Boolean isError(String test) {
        Matcher m = Pattern.compile("\\<(.+?)\\>").matcher(test);
        while (m.find()) {
            if (m.group(1).matches("([a-zA-ZåäöÅÄÖ\\d]+) message=((\"([a-zA-Zåäö"
                    + "ÅÄÖ\\d+])\")|(\'([a-zA-ZåäöÅÄÖ\\d+])"
                    + "\'))") || m.group(1).matches("/[a-zA-zåäöÅÄÖ\\d]+")
                    || m.group(1).matches("\\?(.+?)\\?")) {
                return false;
            }
        }
        System.out.println("Trasig kod: " + test);
        return true;
    }
    //"<message sender=\"%s\"><text color=\"%s\"><encrypted key=%s type=%s> </encrypted></text></message>"

    public static String handleString(String xmlMsg) {
        String msg = "";
        for (int i = 0; i < xmlMsg.length(); i++) {
            if (i == xmlMsg.indexOf("<encrypted")) {
                msg += xmlMsg.substring(0, i);
                xmlMsg = xmlMsg.substring(i);
                String temp = xmlMsg.substring(xmlMsg.indexOf("\"") + 1);
                String type = temp.substring(0, temp.indexOf("\""));
                String key = temp.substring(temp.indexOf("key") + 5,
                        temp.indexOf(">") - 1);
                String encryptedMsg = temp.substring(temp.indexOf(">") + 1, temp.indexOf("</encrypted>"));
                if (type.equals("caesar")) {
                    msg += decryptCaesar(encryptedMsg, Integer.valueOf(key));
                }
                xmlMsg = " " + xmlMsg.substring(xmlMsg.indexOf("</encrypted>") + 12);
                i = 0;
            }
        }
        msg += xmlMsg;
        return msg;
    }

    public static SecretKeySpec genereateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator AESgen = KeyGenerator.getInstance("AES");
        AESgen.init(128);
        SecretKeySpec AESkey = (SecretKeySpec) AESgen.generateKey();
        return AESkey;
    }

    public static String encryptAES(String msg, SecretKeySpec key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        Cipher AEScipher = Cipher.getInstance("AES");
        AEScipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherData = AEScipher.doFinal(msg.getBytes("UTF-8"));
        return stringToHex(new String(cipherData));
    }

    public static String encryptCaesar(String text, int shift) throws UnsupportedEncodingException {
        char[] chars = text.toCharArray();
        for (int i = 0; i < text.length(); i++) {
            char c = chars[i];

            int x = c;
            x = (x + shift) % 255;
            if (x < 0) {
                x += 255;
            }
            chars[i] = (char) x;
        }
        return stringToHex(new String(chars));
    }

    public static String decryptCaesar(String text, int shift) {
        text = hexToString(text);
        char[] chars = text.toCharArray();
        for (int i = 0; i < text.length(); i++) {
            char c = chars[i];

            int x = c;
            x = (x - shift) % 255;
            if (x < 0) {
                x += 255;
            }
            chars[i] = (char) x;
        }
        return new String(chars);
    }

    //stackoverflow.com/questions/923863/converting-a-string-to-hexadecimal-in-java
    public static String stringToHex(String msg) throws UnsupportedEncodingException {
        return String.format("%x", new BigInteger(1, msg.getBytes("utf-8"))).toUpperCase();
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
    /*
    
    //http://stackoverflow.com/questions/2568841/aes-encryption-java-invalid-key-length
    public static SecretKey getAESKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException {
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    String salt = "asdasd3434";
    KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt.getBytes("UTF-8"), 2048, 128);
    SecretKey temp = factory.generateSecret(keySpec);
    SecretKey key = new SecretKeySpec(temp.getEncoded(), "AES");
    return key;
    }
    
    public static String encryptAES(String msg, String key) throws
    NoSuchAlgorithmException, NoSuchPaddingException,
    InvalidKeyException, IllegalBlockSizeException,
    BadPaddingException, InvalidKeySpecException, UnsupportedEncodingException {
    SecretKey secret = getAESKey(key);
    Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
    cipher.init(Cipher.ENCRYPT_MODE, secret);
    System.out.println(msg.getBytes("UTF-8").length);
    byte[] byteMsg = cipher.doFinal(msg.getBytes());
    //final String encryptedString = new BASE64Encoder().encode(byteMsg);//Base64.encodeBase64String(cipher.doFinal(msg.getBytes()));
    return stringToHex(new String(byteMsg));
    }
    
    public static String decryptAES(String encMsg, String key) throws
    NoSuchAlgorithmException, NoSuchPaddingException,
    InvalidKeySpecException, InvalidKeyException,
    IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
    encMsg = hexToString(encMsg);
    Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
    SecretKey secretKey = getAESKey(key);
    
    cipher.init(Cipher.DECRYPT_MODE, secretKey);
    System.out.println(new String(encMsg.getBytes()));
    String decMsg = new String(cipher.doFinal(encMsg.getBytes()));
    return decMsg;
    }
     * 
     */
}
