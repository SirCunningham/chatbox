package chatbox;

//Methods:
//encrypt(String data);
//decrypt(String encryptedData);
import javax.crypto.*;
import java.security.*;
import javax.crypto.spec.*;

public class AESCrypto {

    private static byte[] dataToEncrypt = "AES".getBytes();
    private static byte[] keyContent;

// Skapa nyckel
    public static void main(String[] args) {
        try {
            KeyGenerator AESgen = KeyGenerator.getInstance("AES");
            AESgen.init(128);
            SecretKeySpec AESkey = (SecretKeySpec) AESgen.generateKey();
            keyContent = AESkey.getEncoded();
            // Kryptera
            try {
                Cipher AEScipher = Cipher.getInstance("AES");
                try {
                    AEScipher.init(Cipher.ENCRYPT_MODE, AESkey);
                    try {
                        byte[] cipherData = AEScipher.doFinal(dataToEncrypt);
                        // Avkryptera
                        SecretKeySpec decodeKey = new SecretKeySpec(keyContent,
                                "AES");
                        try {
                            AEScipher.init(Cipher.DECRYPT_MODE, decodeKey);
                            try {
                                byte[] decryptedData =
                                        AEScipher.doFinal(cipherData);
                                System.out.println(
                                        "Decrypted: "
                                        + new String(decryptedData));
                            } catch (IllegalBlockSizeException e) {
                                System.err.println("Fel på decryptedData");
                            } catch (BadPaddingException e) {
                                System.err.println("Fel på decryptedData");
                            }
                        } catch (InvalidKeyException e) {
                            System.err.println("Fel på AEScipher.init");
                        }
                    } catch (IllegalBlockSizeException e) {
                        System.err.println("Fel på cipherData");
                    } catch (BadPaddingException e) {
                        System.err.println("Fel på cipherData");
                    }
                } catch (InvalidKeyException e) {
                    System.err.println("Fel på AEScipher");
                }
            } catch (NoSuchPaddingException e) {
                System.err.println("Fel på algoritmen eller något");
            } catch (NoSuchAlgorithmException e) {
                System.err.println("Fel på algoritmen eller något");
            }
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Fel på algoritmen eller något");
        }
    }
}
