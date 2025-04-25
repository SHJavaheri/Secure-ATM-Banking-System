package utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Arrays;

public class KeyManager {
    private static final String PRE_SHARED_KEY = "supersecretkey123"; // Pre-Shared Key
    private static SecretKey masterSecretKey;
    private static SecretKeySpec encryptionKey;
    private static SecretKeySpec macKey;

    // ** AES Encryption with Dynamic IV **
    public static String encryptAES(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[16];  // Generate a random IV
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, encryptionKey, ivSpec);
        byte[] encryptedData = cipher.doFinal(data.getBytes());

        // Return IV + encrypted data
        return Base64.getEncoder().encodeToString(iv) + ":" + Base64.getEncoder().encodeToString(encryptedData);
    }

    public static String decryptAES(String encryptedData) throws Exception {
        String[] parts = encryptedData.split(":");
        byte[] iv = Base64.getDecoder().decode(parts[0]);
        byte[] cipherText = Base64.getDecoder().decode(parts[1]);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, encryptionKey, ivSpec);

        return new String(cipher.doFinal(cipherText));
    }

    // ** Generate the MAC key **
    public static String generateMac(String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(macKey);
        return Base64.getEncoder().encodeToString(mac.doFinal(data.getBytes()));
    }

    // ** Verify the MAC of the received data **
    public static boolean verifyMac(String data, String receivedMac) throws Exception {
        String calculatedMac = generateMac(data);
        return calculatedMac.equals(receivedMac);
    }

    // ** Generate master key and derive encryption & MAC keys **
    public static void generateMasterSecret() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256, new SecureRandom());
            masterSecretKey = keyGen.generateKey();
            deriveKeys(masterSecretKey);
        } catch (Exception e) {
            throw new RuntimeException("Error generating Master Secret key", e);
        }
    }

    private static void deriveKeys(SecretKey masterKey) throws Exception {
        byte[] masterKeyBytes = masterKey.getEncoded();

        // Use SHA-256 to create a hash and split the key
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] fullHash = sha256.digest(masterKeyBytes);

        // Split the hash into two 128-bit keys (AES key size)
        encryptionKey = new SecretKeySpec(Arrays.copyOfRange(fullHash, 0, 16), "AES");
        macKey = new SecretKeySpec(Arrays.copyOfRange(fullHash, 16, 32), "HmacSHA256");
    }

    static {
        if (encryptionKey == null) {
            System.out.println("[KeyManager] Generating new encryption key...");
            generateMasterSecret();
        }
        if (macKey == null) {
            System.out.println("[KeyManager] Generating new MAC key...");
            generateMasterSecret();
        }
    }

    public static SecretKey getEncryptionKey() {
        if (encryptionKey == null) {
            System.err.println("[ERROR] Encryption key is NULL!");
        }
        return encryptionKey;
    }

    public static SecretKey getMacKey() {
        if (macKey == null) {
            System.err.println("[ERROR] MAC key is NULL!");
        }
        return macKey;
    }

    public static String getEncodedMasterSecret() {
        return Base64.getEncoder().encodeToString(masterSecretKey.getEncoded());
    }

    public static SecretKey getMasterSecretKey() {
        return masterSecretKey;
    }

    public static void setMasterSecretKey(String encodedKey) {
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        masterSecretKey = new javax.crypto.spec.SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }
}