package main.database;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Hasher {

    public Hasher() { }

    public static String hashPassword(String passwordClearText, byte[] salt) throws NoSuchAlgorithmException {
        String hashedPassword = null;

        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(salt);

        byte[] bytes = md.digest(passwordClearText.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(Integer.toString(((aByte & 0xff)) + 0x100, 16).substring(1));
        }

        hashedPassword = sb.toString();

        return hashedPassword;
    }

    public static byte[] getSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }
}
