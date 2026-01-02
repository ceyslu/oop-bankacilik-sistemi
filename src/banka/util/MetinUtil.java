package banka.util;

import java.math.BigDecimal;

public final class MetinUtil {
    private MetinUtil() {}

    // "  ceyda   uslu " -> "ceyda uslu"
    public static String temizBosluk(String input) {
        if (input == null) return "";
        return input.trim().replaceAll("\\s+", " ");
    }

    // "ceyda uslu" -> "Ceyda Uslu"
    public static String titleCase(String input) {
        String trimmed = temizBosluk(input);
        if (trimmed.isEmpty()) return "";

        String[] parts = trimmed.split(" ");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String w = parts[i];
            if (w.isEmpty()) continue;

            String lower = w.toLowerCase();
            String first = lower.substring(0, 1).toUpperCase();
            String rest = lower.length() > 1 ? lower.substring(1) : "";

            if (i > 0) sb.append(" ");
            sb.append(first).append(rest);
        }
        return sb.toString();
    }

    // Sadece rakamlari birakir: "123-45" -> "12345"
    public static String sadeceRakam(String input) {
        if (input == null) return "";
        return input.replaceAll("\\D", "");
    }

    // "12,5" -> 12.5 gibi okuyup BigDecimal yapar
    public static BigDecimal parseTutar(String input) {
        try {
            String s = temizBosluk(input).replace(',', '.');
            BigDecimal bd = new BigDecimal(s);
            if (bd.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Tutar pozitif olmali.");
            }
            return bd;
        } catch (Exception e) {
            throw new IllegalArgumentException("Gecersiz tutar.");
        }
    }

    public static void minUzunluk(String value, int min, String alanAdi) {
        if (value == null || value.length() < min) {
            throw new IllegalArgumentException(alanAdi + " en az " + min + " karakter olmali.");
        }
    }

    public static void bosOlmasin(String value, String alanAdi) {
        if (value == null || temizBosluk(value).isEmpty()) {
            throw new IllegalArgumentException(alanAdi + " bos olamaz.");
        }
    }
}
