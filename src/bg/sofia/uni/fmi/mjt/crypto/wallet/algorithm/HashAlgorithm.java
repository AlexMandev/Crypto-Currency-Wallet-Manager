package bg.sofia.uni.fmi.mjt.crypto.wallet.algorithm;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashAlgorithm {
    private static final int HASH_BYTE = 0xff;
    private static final String HASHING_ALGORITHM = "SHA-256";
    private static final char LEADING_ZERO = '0';

    public static String hashPassword(String password) {
        String hashedPassword;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(HASHING_ALGORITHM);
            byte[] hashedBytes = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));

            hashedPassword = buildHexString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error occurred while hashing password" , e);
        }

        return hashedPassword;
    }

    private static String buildHexString(byte[] hashedBytes) {
        StringBuilder hashedPassword = new StringBuilder();
        for (byte b : hashedBytes) {
            String hex = Integer.toHexString(b & HASH_BYTE);
            if (hex.length() == 1) {
                hashedPassword.append(LEADING_ZERO);
            }
            hashedPassword.append(hex);
        }
        return hashedPassword.toString();
    }

}
