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

public class AESCrypto3 {

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
            AESCrypto3 a = new AESCrypto3();
            a.encrypt("Hello!");
            try {
                a.decrypt(a.getEncryptedMsg(), a.getDecodeKey());
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

    public AESCrypto3() throws NoSuchAlgorithmException, NoSuchPaddingException,
            UnsupportedEncodingException {
        AESgen = KeyGenerator.getInstance("AES");
        AESgen.init(128);
        AESkey = (SecretKeySpec) AESgen.generateKey();
        decodeKey = new SecretKeySpec(AESkey.getEncoded(), "AES");
        hexDecodeKey = keyToString(decodeKey);
        AEScipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    }

    public AESCrypto3(String msg) throws NoSuchAlgorithmException,
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
        cipherData = AEScipher.doFinal(msg.getBytes("UTF-8"));

        this.msg = msg;
        encMsg = byteArrayToHex(cipherData);
        return encMsg;
    }

    public String decrypt(String msg, String hexDecodeKey) throws
            InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException,
            NoSuchAlgorithmException, NoSuchPaddingException, DecoderException {
        SecretKeySpec key = stringToKey(hexDecodeKey);
        AEScipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedData = AEScipher.doFinal(hexToByteArray(msg));
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

    public static String keyToString(SecretKeySpec key) {
        String decoded = Hex.encodeHexString(key.getEncoded());
        return decoded;
    }

    public static SecretKeySpec stringToKey(String key) throws DecoderException {
        byte[] decodedKey = Hex.decodeHex(key.toCharArray());
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    public static String byteArrayToHex(byte[] bytes) throws UnsupportedEncodingException {
        return Hex.encodeHexString(bytes);
    }

    public static byte[] hexToByteArray(String hex) throws DecoderException {
        return Hex.decodeHex(hex.toCharArray());
    }
}
