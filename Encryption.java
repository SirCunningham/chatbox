package chatbox;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Encryption {
    
    public static String encrypt(String type, String text, String key, AESCrypto AES) {
        switch (type) {
            case "caesar":
                try {
                    return encryptCaesar(text, Integer.valueOf(key));
                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                }
            case "AES":
                try {
                    return AES.encrypt(text);
                } catch (NoSuchAlgorithmException | InvalidKeyException |
                        UnsupportedEncodingException |
                        IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException ex) {
                    ex.printStackTrace();
                }
                break;
        }
        return null;
    }
    
    private static String encryptCaesar(String text, int shift) throws UnsupportedEncodingException {
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
        //stackoverflow.com/questions/923863/converting-a-string-to-hexadecimal-in-java
        String msg = new String(chars);
        return String.format("%x", new BigInteger(1, msg.getBytes("UTF-8"))).toUpperCase();  //UTF-8 krav, men då fungerar inte åäö
    }
}
