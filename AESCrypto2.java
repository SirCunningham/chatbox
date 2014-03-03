package chatbox;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import javax.crypto.*;
import java.security.*;
import java.util.Arrays;
import javax.crypto.spec.*;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

public class AESCrypto2 {

    private Cipher AEScipher;
    private KeyGenerator AESgen;
    private SecretKeySpec AESkey;
    private SecretKeySpec decodeKey;
    private String hexDecodeKey;
    private String decodeKey64;
    private byte[] cipherData;
    private String msg;
    private String encMsg;

    public static void main(String[] args) {
        try {

            AESCrypto2 a = new AESCrypto2();
            a.encrypt("Hej");
            String msg = a.getEncryptedMsg();
            String hexkey = a.getDecodeKey();
            SecretKeySpec t1;
            try {
                t1 = a.convertDecodeKey(hexkey);
                SecretKeySpec t2 = a.convertDecodeKey(hexkey);
                System.out.println(t1.equals(t2));
                a.decrypt(msg, a.getDecodeKey());
            } catch (DecoderException ex) {
                ex.printStackTrace();
            }
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
        AESgen = KeyGenerator.getInstance("AES");
        AESgen.init(256);
        AESkey = (SecretKeySpec) AESgen.generateKey();
        decodeKey = new SecretKeySpec(AESkey.getEncoded(), "AES");

        hexDecodeKey = stringToHex(new String(decodeKey.getEncoded()));
        AEScipher = Cipher.getInstance("AES");
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
        cipherData = AEScipher.doFinal(handleMsg(msg));
        this.msg = msg;
        encMsg = stringToHex(new String(cipherData));
        return encMsg;
    }

    public String decrypt(String msg, String hexDecodeKey) throws
            InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, DecoderException {
        try {
            AEScipher.init(Cipher.DECRYPT_MODE, convertDecodeKey(hexDecodeKey));
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        byte[] decryptedData = AEScipher.doFinal(handleMsg(hexToString(msg)));
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

    public static byte[] handleMsg(String msg) throws UnsupportedEncodingException {
        byte[] tempByte = msg.getBytes(Charset.forName("UTF-8"));
        if (tempByte.length % 16 != 0) {
            byte[] msgByte = Arrays.copyOf(tempByte, tempByte.length + 16 - (tempByte.length % 16));
            return msgByte;
        }
        return tempByte;
    }

    public static SecretKeySpec convertDecodeKey(String hexDecodeKey) throws UnsupportedEncodingException, DecoderException {
        String temp = hexToString(hexDecodeKey);
        byte[] data = handleMsg(temp);
        SecretKeySpec key = new SecretKeySpec(data, 0, data.length, "AES");
        return key;
    }

    public static String stringToHex(String msg) {
        return new String(Hex.encodeHex(msg.getBytes()));
    }

    public static String hexToString(String msg) throws DecoderException {
        return new String(Hex.decodeHex(msg.toCharArray()));
    }
    /*
    public static String stringToHex(String msg) throws UnsupportedEncodingException {
    String str = String.format("%x", new BigInteger(1, msg.getBytes(Charset.forName("UTF-8")))).toUpperCase();
    System.out.println(str.length());
    return String.format("%x", new BigInteger(1, msg.getBytes(Charset.forName("UTF-8")))).toUpperCase();
    }
    
    //stackoverflow.com/questions/15749475/java-string-hex-to-string-ascii-with-accentuation
    public static String hexToString(String hex) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    for (int i = 0; i < hex.length(); i += 2) {
    String str = "";
    if (i + 2 < hex.length()) {
    str = hex.substring(i, i + 2);
    } else {
    str = hex.substring(i, i + 1);
    }
    int byteVal = Integer.parseInt(str, 16);
    baos.write(byteVal);
    }
    String s = new String(baos.toByteArray(), Charset.forName("UTF-8"));
    return s;
    }
     * 
     */
}
