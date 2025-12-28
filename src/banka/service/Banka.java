package banka.service;

import banka.model.Musteri;
import banka.util.Sifreleme;

import java.util.HashMap;
import java.util.Map;

public class Banka {

    private final Map<String, Musteri> tcIle = new HashMap<>();
    private final Map<String, Musteri> adSoyadIle = new HashMap<>();

    public Musteri uyeOl(String tc, String adSoyad, String sifre) {
        tc = temizle(tc);
        adSoyad = temizle(adSoyad);

        if (tc.isEmpty() || adSoyad.isEmpty() || sifre == null || sifre.isEmpty()) {
            throw new IllegalArgumentException("TC, Ad Soyad ve Sifre bos olamaz.");
        }
        if (tcIle.containsKey(tc)) {
            throw new IllegalArgumentException("Bu TC zaten kayitli.");
        }
        if (adSoyadIle.containsKey(adSoyad)) {
            throw new IllegalArgumentException("Bu Ad Soyad zaten kayitli.");
        }

        String hash = Sifreleme.sha256(sifre);
        Musteri m = new Musteri(tc, adSoyad, hash);

        tcIle.put(tc, m);
        adSoyadIle.put(adSoyad, m);

        return m;
    }

    public Musteri girisYap(String adSoyad, String sifre) {
        adSoyad = temizle(adSoyad);

        if (adSoyad.isEmpty() || sifre == null || sifre.isEmpty()) {
            throw new IllegalArgumentException("Ad Soyad ve Sifre bos olamaz.");
        }

        Musteri m = adSoyadIle.get(adSoyad);
        if (m == null) throw new IllegalArgumentException("Kullanici bulunamadi.");

        String hash = Sifreleme.sha256(sifre);
        if (!m.sifreDogruMu(hash)) throw new IllegalArgumentException("Sifre hatali.");

        return m;
    }

    private String temizle(String s) {
        return s == null ? "" : s.trim();
    }
}
