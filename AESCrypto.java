package chatbox;

import java.io.*;
import javax.crypto.*;
import java.security.*;
import javax.crypto.spec.*;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class AESCrypto {

    private Cipher AEScipher;
    private SecretKeySpec AESkey;
    private String hexDecodeKey;
    private byte[] cipherData;
    private String msg;
    private String encMsg;


    public AESCrypto() throws NoSuchAlgorithmException, NoSuchPaddingException,
            UnsupportedEncodingException {
        KeyGenerator AESgen = KeyGenerator.getInstance("AES");
        AESgen.init(256);
        AESkey = (SecretKeySpec) AESgen.generateKey();
        hexDecodeKey = keyToString(new SecretKeySpec(AESkey.getEncoded(), "AES"));
        AEScipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    }

    public String encrypt(String msg) throws NoSuchAlgorithmException,
            InvalidKeyException, UnsupportedEncodingException,
            IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
        AEScipher.init(Cipher.ENCRYPT_MODE, AESkey);
        return byteArrayToHex(AEScipher.doFinal(msg.getBytes("UTF-8")));
    }

    public String decrypt(String msg, String hexDecodeKey) throws
            InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException,
            NoSuchAlgorithmException, NoSuchPaddingException, DecoderException {
        AEScipher.init(Cipher.DECRYPT_MODE, stringToKey(hexDecodeKey));
        return new String(AEScipher.doFinal(hexToByteArray(msg)));
    }

    public String getDecodeKey() {
        return hexDecodeKey;
    }

    public static String keyToString(SecretKeySpec key) {
        return Hex.encodeHexString(key.getEncoded());
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
