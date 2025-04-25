package utils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

public class EncryptionUtils {

    // ** RSA Encryption (For Authentication) **
    public static String encryptRSA(String data, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting with RSA", e);
        }
    }

    public static String decryptRSA(String encryptedData, PrivateKey privateKey) {
        try {
            if (encryptedData == null || encryptedData.isEmpty()) {
                throw new IllegalArgumentException("ERROR: Attempted to decrypt an empty message.");
            }

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting with RSA", e);
        }
    }

    // ** AES Encryption (For Secure Communication) **
    public static String encryptAES(String data, SecretKey secretKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] iv = new byte[16];  // Generate a random IV
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // Return IV + encrypted data
            return Base64.getEncoder().encodeToString(iv) + ":" + Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting with AES", e);
        }
    }

    public static String decryptAES(String encryptedData, SecretKey secretKey) {
        try {
            String[] parts = encryptedData.split(":");
            byte[] iv = Base64.getDecoder().decode(parts[0]);
            byte[] cipherText = Base64.getDecoder().decode(parts[1]);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

            return new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting with AES", e);
        }
    }

    // ** AES Key Encryption (For Secure Key Exchange) **
    public static String encryptAESKey(SecretKey secretKey, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(secretKey.getEncoded()));
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting AES key with RSA", e);
        }
    }

    public static SecretKey decryptAESKey(String encryptedKey, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decodedKey = cipher.doFinal(Base64.getDecoder().decode(encryptedKey));
            return new SecretKeySpec(decodedKey, "AES");
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting AES key with RSA", e);
        }
    }
}
