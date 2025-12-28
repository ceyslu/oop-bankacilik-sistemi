package banka.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class Sifreleme {
    private Sifreleme() {}

    public static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Hash hatasi", e);
        }
    }
}
