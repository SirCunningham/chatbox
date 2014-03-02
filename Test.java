package chatbox;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import javax.crypto.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.spec.*;
import java.math.*;
import java.nio.charset.Charset;

public class Test {

    public static void main(String[] args) {
        String test = "";
        String test2 = "";
        try {
            test = encryptCaesar("men det är detta", 5);
            test2 = encryptCaesar("och även detta", 10);

        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        String xmlTest = "<message sender=\"dante\"> <text color=\"asdfasf\"> Detta är inte krypterat "
                + "<encrypted type=\"caesar\" key=\"5\">" + test + "</encrypted>"
                + "<encrypted type=\"caesar\" key=\"10\">" + test2 + "</encrypted></text></message>";
        System.out.println(test);
        System.out.println(decryptCaesar(test, 5));
        String encMsg;

        try {
            encMsg = encryptAES("89", "nyckel");
            System.out.println(encMsg);
            String msg = decryptAES(encMsg, "nyckel");
            System.out.println(msg);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch (NoSuchPaddingException ex) {
            ex.printStackTrace();
        } catch (InvalidKeyException ex) {
            ex.printStackTrace();
        } catch (IllegalBlockSizeException ex) {
            ex.printStackTrace();
        } catch (BadPaddingException ex) {
            ex.printStackTrace();
        } catch (InvalidKeySpecException ex) {
            ex.printStackTrace();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }





    }

    public static String handleString(String xmlMsg) {
        String msg = "";
        for (int i = 0; i < xmlMsg.length(); i++) {
            if (i == xmlMsg.indexOf("<encrypted")) {
                msg += xmlMsg.substring(0, i);
                xmlMsg = xmlMsg.substring(i + 10);
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

    //http://stackoverflow.com/questions/2568841/aes-encryption-java-invalid-key-length
    public static SecretKey getAESKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        String salt = "asdasd3434";
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 2048, 256);
        SecretKey temp = factory.generateSecret(keySpec);
        SecretKey key = new SecretKeySpec(temp.getEncoded(), "AES");
        return key;
    }

    public static String encryptAES(String msg, String key) throws
            NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeySpecException, UnsupportedEncodingException {
        SecretKey secret = getAESKey(key);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        byte[] byteMsg = cipher.doFinal(msg.getBytes("UTF-8"));
        //final String encryptedString = new BASE64Encoder().encode(byteMsg);//Base64.encodeBase64String(cipher.doFinal(msg.getBytes()));
        return stringToHex(new String(byteMsg));
    }

    public static String decryptAES(String encMsg, String key) throws
            NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeySpecException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        encMsg = hexToString(encMsg);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        SecretKey secretKey = getAESKey(key);

        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        System.out.println(new String(encMsg.getBytes()));
        String decMsg = new String(cipher.doFinal(encMsg.getBytes(("UTF-8"))));
        return decMsg;
    }
}
