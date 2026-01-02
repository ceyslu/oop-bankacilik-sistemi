package banka.service;

import banka.hesap.TasarrufHesabi;
import banka.hesap.VadesizHesap;
import banka.model.Musteri;
import banka.util.Sifreleme;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Banka {

public void vadesizParaYatir(Musteri m, BigDecimal tutar, String neden) {
    m.getVadesiz().paraYatir(tutar);
    m.getVadesizGecmis().ekle(new banka.islem.ParaYatirmaIslemi(m.getVadesiz().getHesapNo(), tutar, neden));
    dosyayaKaydet();
}

public void tasarrufParaYatir(Musteri m, BigDecimal tutar, String neden) {
    m.getTasarruf().paraYatir(tutar);
    m.getTasarrufGecmis().ekle(new banka.islem.ParaYatirmaIslemi(m.getTasarruf().getHesapNo(), tutar, neden));
    dosyayaKaydet();
}

public void vadesizParaCek(Musteri m, BigDecimal tutar) {
    m.getVadesiz().paraCek(tutar, m.getVadesizGecmis());
    dosyayaKaydet();
}

public void transferYap(Musteri gonderen, String aliciAdSoyad, BigDecimal tutar) {
    Musteri alan = adSoyadIle.get(temizle(aliciAdSoyad));
    if (alan == null) throw new IllegalArgumentException("Alici bulunamadi.");

    gonderen.getVadesiz().transferEt(alan.getVadesiz(), tutar, gonderen.getVadesizGecmis());
    // alan tarafina da bilgi islemi ekleyelim
    alan.getVadesizGecmis().ekle(new banka.islem.BilgiIslemi(alan.getVadesiz().getHesapNo(),
            "Gelen transfer: " + gonderen.getAdSoyad() + " | " + tutar + " TL"));

    dosyayaKaydet();
}

public void altinAl(Musteri m, BigDecimal tlTutar, BigDecimal gramFiyat) {
    m.getTasarruf().altinAl(tlTutar, gramFiyat, m.getTasarrufGecmis());
    dosyayaKaydet();
}








    private static final String DOSYA_YOLU = "data/musteriler.csv";

    private final Map<String, Musteri> tcIle = new HashMap<>();
    private final Map<String, Musteri> adSoyadIle = new HashMap<>();

    private int vadesizNo = 100000;
    private int tasarrufNo = 200000;

    public Banka() {
        dosyadanYukle();
    }

    public Musteri uyeOl(String tc, String adSoyad, String sifre) {
        tc = temizle(tc);
        adSoyad = temizle(adSoyad);

        if (tc.isEmpty() || adSoyad.isEmpty() || sifre == null || sifre.isEmpty()) {
            throw new IllegalArgumentException("TC, Ad Soyad ve Sifre bos olamaz.");
        }
        if (tcIle.containsKey(tc)) throw new IllegalArgumentException("Bu TC zaten kayitli.");
        if (adSoyadIle.containsKey(adSoyad)) throw new IllegalArgumentException("Bu Ad Soyad zaten kayitli.");

        VadesizHesap v = new VadesizHesap(String.valueOf(++vadesizNo));
        TasarrufHesabi t = new TasarrufHesabi(String.valueOf(++tasarrufNo));

        // Yeni uye bonusu: vadesize 1000 TL
        v.paraYatir(new BigDecimal("1000"));

        String hash = Sifreleme.sha256(sifre);
        Musteri m = new Musteri(tc, adSoyad, hash, v, t);

        tcIle.put(tc, m);
        adSoyadIle.put(adSoyad, m);

        dosyayaKaydet(); // <-- KALICI KAYIT
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

    public void aySonuCalistir() {
        for (Musteri m : tcIle.values()) {
            m.getVadesiz().aySonuIslemleri(m.getVadesizGecmis());
            m.getTasarruf().aySonuIslemleri(m.getTasarrufGecmis());
        }
        dosyayaKaydet(); // ay sonu bakiye degistirebilir
    }

    // ---- DOSYA ISLEMLERI ----

    private void dosyadanYukle() {
        File f = new File(DOSYA_YOLU);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // tc;adSoyad;sifreHash;vNo;vBakiye;tNo;tBakiye;altinGram
                String[] p = line.split(";");
                if (p.length != 8) continue;

                String tc = p[0];
                String adSoyad = p[1];
                String sifreHash = p[2];

                String vNo = p[3];
                BigDecimal vBakiye = new BigDecimal(p[4]);

                String tNo = p[5];
                BigDecimal tBakiye = new BigDecimal(p[6]);

                BigDecimal altinGram = new BigDecimal(p[7]);

                VadesizHesap v = new VadesizHesap(vNo);
                TasarrufHesabi t = new TasarrufHesabi(tNo);

                // bakiye yuklemek icin pratik: paraYatir ile sifirdan ekleyelim
                if (vBakiye.compareTo(BigDecimal.ZERO) > 0) v.paraYatir(vBakiye);
                if (tBakiye.compareTo(BigDecimal.ZERO) > 0) t.paraYatir(tBakiye);
                if (altinGram.compareTo(BigDecimal.ZERO) > 0) t.altinGramAyarla(altinGram);

                Musteri m = new Musteri(tc, adSoyad, sifreHash, v, t);

                tcIle.put(tc, m);
                adSoyadIle.put(adSoyad, m);

                // sayaçları güncelle (en büyük hesapNo'yu takip edelim)
                vadesizNo = Math.max(vadesizNo, sayiyaCevir(vNo, 100000));
                tasarrufNo = Math.max(tasarrufNo, sayiyaCevir(tNo, 200000));
            }
        } catch (Exception e) {
            // dosya bozuksa uygulama yine de acilsin
            System.out.println("Dosyadan yukleme hatasi: " + e.getMessage());
        }
    }

    private void dosyayaKaydet() {
        File f = new File(DOSYA_YOLU);
        f.getParentFile().mkdirs();

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, false), StandardCharsets.UTF_8))) {
            for (Musteri m : tcIle.values()) {
                String line = String.join(";",
                        m.getTc(),
                        m.getAdSoyad(),
                        // sifre hash'i direkt sakliyoruz
                        getSifreHash(m),
                        m.getVadesiz().getHesapNo(),
                        m.getVadesiz().getBakiye().toPlainString(),
                        m.getTasarruf().getHesapNo(),
                        m.getTasarruf().getBakiye().toPlainString(),
                        m.getTasarruf().getAltinGram().toPlainString()
                );
                bw.write(line);
                bw.newLine();
            }
        } catch (Exception e) {
            System.out.println("Dosyaya kaydetme hatasi: " + e.getMessage());
        }
    }

    // Musteri sifreHash private oldugu icin burada almak icin kucuk bir hile:
    // En temiz yol: Musteri'ye getSifreHash() eklemek. Onu ekleyelim:
    private String getSifreHash(Musteri m) {
        return m.getSifreHash();
    }

    private int sayiyaCevir(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }

    private String temizle(String s) {
        return s == null ? "" : s.trim();
    }
}
