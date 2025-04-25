package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class SecurityUtils {

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public static boolean verifyPassword(String password, String hashedPassword) {
        return hashPassword(password).equals(hashedPassword);
    }

    private static final String HMAC_ALGO = "HmacSHA256";

    // Generate MAC to ensure integrity
    public static String generateMac(String data, SecretKey macKey) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(macKey);
            byte[] macBytes = mac.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(macBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error generating MAC", e);
        }
    }

    // Verify MAC before processing data
    public static boolean verifyMac(String data, String receivedMac, SecretKey macKey) {
        String expectedMac = generateMac(data, macKey);
        return expectedMac.equals(receivedMac);
    }
}
