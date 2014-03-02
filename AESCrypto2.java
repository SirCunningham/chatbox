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
    private byte[] cipherData;
    private String msg;
    private String encMsg;

    public static void main(String[] args) {

        try {
            AESCrypto2 a = new AESCrypto2("Hej");
            System.out.println(a.getEncryptedMsg());
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

    public AESCrypto2() throws NoSuchAlgorithmException {
        AESgen = KeyGenerator.getInstance("AES");
        AESgen.init(128);
        AESkey = (SecretKeySpec) AESgen.generateKey();
        decodeKey = new SecretKeySpec(AESkey.getEncoded(), "AES");
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
            NoSuchPaddingException, InvalidKeyException,
            UnsupportedEncodingException, IllegalBlockSizeException,
            BadPaddingException {
        Cipher AEScipher = Cipher.getInstance("AES");
        AEScipher.init(Cipher.ENCRYPT_MODE, AESkey);
        cipherData = AEScipher.doFinal(msg.getBytes("UTF-8"));
        encMsg = stringToHex(new String(cipherData));
        return encMsg;
    }
    

    public String decrypt(String msg) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        AEScipher.init(Cipher.DECRYPT_MODE, decodeKey);
        byte[] decryptedData = AEScipher.doFinal(cipherData);
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

    public String stringToHex(String msg) throws UnsupportedEncodingException {
        return String.format("%x", new BigInteger(1, msg.getBytes("utf-8"))).toUpperCase();
    }

    //stackoverflow.com/questions/15749475/java-string-hex-to-string-ascii-with-accentuation
    public String hexToString(String hex) {
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
