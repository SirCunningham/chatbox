package chatbox;

import java.io.UnsupportedEncodingException;
import javax.crypto.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.spec.*;


public class Test {

    public static void main(String[] args) {
        String test = encryptCaesar("men det är detta", 5);
        String test2 = encryptCaesar("och även detta", 10);
        String xmlTest = "<message sender=\"dante\"> <text color=\"asdfasf\"> Detta är inte krypterat "
                + "<encrypted type=\"caesar\" key=\"5\">" + test + "</encrypted>"
                + "<encrypted type=\"caesar\" key=\"10\">" + test2 + "</encrypted></text></message>";
        System.out.println("<message></message>");
        try {
            String encMsg = encryptAES("Hej", "nyckel");
            String msg = decryptAES(encMsg,"nyckel");
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

    public static String encryptCaesar(String text, int shift) {
        char[] chars = text.toCharArray();
        for (int i = 0; i < text.length(); i++) {
            char c = chars[i];
            //Tag inte med control characters
            if (c >= 32 && c <= 127) {
                int x = c - 32;
                x = (x + shift) % 96;
                if (x < 0) {
                    x += 96;
                }
                chars[i] = (char) (x + 32);
            }
        }
        return new String(chars);
    }

    public static String decryptCaesar(String text, int shift) {
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
    //http://stackoverflow.com/questions/2568841/aes-encryption-java-invalid-key-length

    public static SecretKey getAESKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        String salt = "awefåoäj#¤(#¤?\"=!\"##¤#ia343å023iå3irjWFOEfm3R33OIJWFA"
                + "WEÖFs";
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 1024, 256);  
        SecretKey temp = factory.generateSecret(keySpec);
        SecretKey key = new SecretKeySpec(temp.getEncoded(), "AES");
        return key;
    }

    public static String encryptAES(String msg, String key) throws
            NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeySpecException, UnsupportedEncodingException {
        SecretKey secret = getAESKey(key);

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        byte[] byteMsg = cipher.doFinal(msg.getBytes("UTF-8"));
        //final String encryptedString = new BASE64Encoder().encode(byteMsg);//Base64.encodeBase64String(cipher.doFinal(msg.getBytes()));
        return new String(byteMsg);
    }

    public static String decryptAES(String encMsg, String key) throws
            NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeySpecException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKey secretKey = getAESKey(key);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        System.out.println(encMsg.getBytes().length);
        String decMsg = new String(cipher.doFinal(encMsg.getBytes()));
        return decMsg;
    }
}
