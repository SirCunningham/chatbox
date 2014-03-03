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
            a.encrypt(" Hej2");
            String msg = a.getEncryptedMsg();
            String dstr = a.getDecodeKey();
            try {
                SecretKeySpec key1 = a.getKey();
                SecretKeySpec key2 = a.stringToKey(dstr);
                System.out.println(key1.equals(key2));
            } catch (DecoderException ex) {
                ex.printStackTrace();
            }

            try {
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
        AESgen.init(128);
        AESkey = (SecretKeySpec) AESgen.generateKey();
        decodeKey = new SecretKeySpec(AESkey.getEncoded(), "AES");
        hexDecodeKey = keyToString(decodeKey);
        AEScipher = Cipher.getInstance("AES/ECB/NoPadding");
    }

    public AESCrypto2(String msg) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException,
            UnsupportedEncodingException, IllegalBlockSizeException,
            BadPaddingException {
        this();
        encrypt(msg);
    }

    public String encrypt(String msg) throws NoSuchAlgorithmException,
            InvalidKeyException, UnsupportedEncodingException,
            IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
        AEScipher.init(Cipher.ENCRYPT_MODE, AESkey);

        cipherData = AEScipher.doFinal(handleString(msg.getBytes("UTF-8")));

        this.msg = msg;
        encMsg = stringToHex(new String(cipherData));
        return encMsg;
    }

    public String decrypt(String msg, String hexDecodeKey) throws
            InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException,
            NoSuchAlgorithmException, NoSuchPaddingException, DecoderException {
        AEScipher.init(Cipher.DECRYPT_MODE, stringToKey(hexDecodeKey));
        byte[] decryptedData = AEScipher.doFinal(handleString(stringToByte((hexToString(msg)))));
        encMsg = msg;
        msg = new String(decryptedData);
        System.out.println(msg);
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

    public static byte[] handleString(byte[] b) throws UnsupportedEncodingException {
        byte[] temp = b;
        if (temp.length % 16 != 0) {
            byte[] byteMsg = Arrays.copyOf(temp, temp.length + 16 - (temp.length % 16));
            return byteMsg;
        }
        return temp;
    }

    public static String keyToString(SecretKeySpec key) {
        String decoded = Hex.encodeHexString(key.getEncoded());
        return decoded;
    }

    public static String byteToString(byte[] b) {
        String str = Base64.encodeBase64String(b);
        return base64ToHex(str);
    }

    public static byte[] stringToByte(String str) throws DecoderException {
        String str2 = hexToBase64(str);
        return Base64.decodeBase64(str2);
    }

    public static SecretKeySpec stringToKey(String key) throws DecoderException {
        byte[] decodedKey = Hex.decodeHex(key.toCharArray());
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    public static String hexToBase64(String hex) throws DecoderException {
        byte[] decode = Hex.decodeHex(hex.toCharArray());
        return Base64.encodeBase64String(decode);
    }

    public static String base64ToHex(String base64) {
        byte[] decode = Base64.decodeBase64(base64);
        return Hex.encodeHexString(decode);
    }

    public static String stringToHex(String msg) throws UnsupportedEncodingException {
        return Hex.encodeHexString(msg.getBytes("UTF-8"));
    }

    public static String hexToString(String msg) throws DecoderException {
        return new String(Hex.decodeHex(msg.toCharArray()));
    }
}
