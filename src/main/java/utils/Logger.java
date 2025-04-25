package utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class Logger {
    private static final String PLAIN_LOG = "logs/audit_log.txt";
    private static final String ENCRYPTED_LOG = "logs/audit_log_encrypted.txt";
    private static final String SECRET = "AuditEncryptKeys"; // 16 chars = 128-bit AES key

    public static void logTransaction(String username, String action) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logEntry = timestamp + " - " + username + " - " + action;

        // Write plain log
        try (FileWriter writer = new FileWriter(PLAIN_LOG, true)) {
            writer.write(logEntry + "\n");
        } catch (IOException e) {
            System.out.println("Error writing to plain log: " + e.getMessage());
        }

        // Write encrypted log
        try (FileWriter writer = new FileWriter(ENCRYPTED_LOG, true)) {
            writer.write(encrypt(logEntry) + "\n");
        } catch (Exception e) {
            System.out.println("Error writing to encrypted log: " + e.getMessage());
        }
    }

    private static String encrypt(String strToEncrypt) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES");

        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }
}