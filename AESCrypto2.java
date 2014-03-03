package chatbox;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import javax.crypto.*;
import java.security.*;
import javax.crypto.spec.*;

public class AESCrypto2 {

    private Cipher AEScipher;
    private KeyGenerator AESgen;
    private SecretKeySpec AESkey;
    private SecretKeySpec decodeKey;
    private String hexDecodeKey;
    private byte[] cipherData;
    private String msg;
    private String encMsg;

    public static void main(String[] args) {

        try {
            AESCrypto2 a = new AESCrypto2();
            a.encrypt("Hej");
            String msg = a.getEncryptedMsg();
            String hexkey = a.getDecodeKey();
            SecretKeySpec t1 = a.convertDecodeKey(hexkey);
            SecretKeySpec t2 = a.convertDecodeKey(hexkey);
            System.out.println(t1.equals(t2));
            a.decrypt(msg, a.getDecodeKey());
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch (NoSuchPaddingException ex) {
            ex.printStackTrace();
        } catch (InvalidKeyException ex) {
            ex.printStackTrace();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (IllegalBlockSizeException ex) {
            ex.printStackTrace();
        } catch (BadPaddingException ex) {
            ex.printStackTrace();
        }

    }

    public AESCrypto2() throws NoSuchAlgorithmException, NoSuchPaddingException,
            UnsupportedEncodingException {
        AESgen = KeyGenerator.getInstance( "AES");
        AESgen.init(128);
        AESkey = (SecretKeySpec) AESgen.generateKey();
        decodeKey = new SecretKeySpec(AESkey.getEncoded(), "AES");
        hexDecodeKey = stringToHex(new String(decodeKey.getEncoded()));
        AEScipher = Cipher.getInstance("AES/CBC/NoPadding");
    }

    public AESCrypto2(String msg) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException,
            UnsupportedEncodingException, IllegalBlockSizeException,
            BadPaddingException {
        this();
        this.msg = msg;
        encrypt(msg);
    }

    public String encrypt(String msg) throws NoSuchAlgorithmException,
            InvalidKeyException, UnsupportedEncodingException,
            IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
        AEScipher.init(Cipher.ENCRYPT_MODE, AESkey);
        cipherData = AEScipher.doFinal(msg.getBytes("UTF-8"));
        this.msg = msg;
        encMsg = stringToHex(new String(cipherData));
        return encMsg;
    }

    public String decrypt(String msg, String hexDecodeKey) throws
            InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException {
        try {
            AEScipher.init(Cipher.DECRYPT_MODE, convertDecodeKey(hexDecodeKey));
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        byte[] decryptedData = AEScipher.doFinal(hexToString(msg).getBytes("UTF-8"));
        encMsg = msg;
        msg = hexToString(new String(decryptedData));
        return msg;
    }

    public String getEncryptedMsg() {
        return encMsg;
    }

    public String getDecryptedMsg() {
        return msg;
    }

    public String getDecodeKey() {
        return hexDecodeKey;
    }

    public SecretKeySpec getKey() {
        return decodeKey;
    }

    public static SecretKeySpec convertDecodeKey(String hexDecodeKey) throws UnsupportedEncodingException {
        String temp = hexToString(hexDecodeKey);
        byte[] data = temp.getBytes("UTF-8");
        SecretKeySpec key = new SecretKeySpec(data, 0, data.length, "AES");
        return key;
    }

    public static String stringToHex(String msg) throws UnsupportedEncodingException {
        String str = String.format("%x", new BigInteger(1, msg.getBytes("UTF-8"))).toUpperCase();
        System.out.println(str.length());
        return String.format("%x", new BigInteger(1, msg.getBytes("UTF-8"))).toUpperCase();
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
