package banka.service;

import banka.hesap.TasarrufHesabi;
import banka.hesap.VadesizHesap;
import banka.islem.BilgiIslemi;
import banka.islem.IslemGecmisi;
import banka.model.Musteri;
import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Banka {

    private final Map<String, Musteri> musteriler = new HashMap<>();
    private final IslemGecmisi islemGecmisi = new IslemGecmisi();

    private static final String DOSYA_YOLU = "data/musteriler.csv";

    public Banka() {
        dosyadanYukle();
    }

    public Map<String, Musteri> getMusteriler() {
        return musteriler;
    }

    public IslemGecmisi getIslemGecmisi() {
        return islemGecmisi;
    }

    /* ===================== GIRIS / UYELIK ===================== */

    public Musteri girisYap(String adSoyad, String sifreHash) {
        if (adSoyad == null || adSoyad.isBlank()) {
            throw new IllegalArgumentException("Ad Soyad boş olamaz.");
        }
        if (sifreHash == null || sifreHash.isBlank()) {
            throw new IllegalArgumentException("Şifre boş olamaz.");
        }

        String hedef = adSoyad.trim().toLowerCase();

        for (Musteri m : musteriler.values()) {
            String mevcut = m.getAdSoyad().trim().toLowerCase();
            if (mevcut.equals(hedef)) {
                if (!m.sifreDogruMu(sifreHash)) {
                    throw new IllegalArgumentException("Şifre hatalı.");
                }
                return m;
            }
        }
        throw new IllegalArgumentException("Kullanıcı bulunamadı.");
    }

    public Musteri uyeOl(String tc, String adSoyad, String sifreHash) {
        if (tc == null || tc.isBlank()) throw new IllegalArgumentException("TC boş olamaz.");
        if (adSoyad == null || adSoyad.isBlank()) throw new IllegalArgumentException("Ad Soyad boş olamaz.");
        if (sifreHash == null || sifreHash.isBlank()) throw new IllegalArgumentException("Şifre boş olamaz.");

        if (musteriler.containsKey(tc)) {
            throw new IllegalArgumentException("Bu TC ile kayıt zaten var.");
        }

        String hedef = adSoyad.trim().toLowerCase();
        for (Musteri x : musteriler.values()) {
            if (x.getAdSoyad().trim().toLowerCase().equals(hedef)) {
                throw new IllegalArgumentException("Bu ad soyad ile kayıt zaten var.");
            }
        }

        int sira = musteriler.size() + 1;
        String vNo = "100" + String.format("%03d", sira);
        String tNo = "200" + String.format("%03d", sira);

        VadesizHesap vadesiz = new VadesizHesap(vNo);
        TasarrufHesabi tasarruf = new TasarrufHesabi(tNo);

        // --- 1. ADIM: HOŞ GELDİN BONUSU ---
        // Her iki hesaba da 1000'er TL yatırıyoruz.
        vadesiz.paraYatir(new BigDecimal("1000"));
        tasarruf.paraYatir(new BigDecimal("1000"));

        Musteri yeni = new Musteri(tc, adSoyad, sifreHash, vadesiz, tasarruf);
        musteriler.put(tc, yeni);
        dosyayaKaydet();

        return yeni;
    }

    /* ===================== DOSYA ISLEMLERI ===================== */

    private void dosyadanYukle() {
        File f = new File(DOSYA_YOLU);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] p = line.split(";");
                if (p.length < 7) continue;

                try {
                    String tc = p[0];
                    String adSoyad = p[1];
                    String sifreHash = p[2];

                    String vNo = p[3];
                    BigDecimal vB = new BigDecimal(p[4]);

                    String tNo = p[5];
                    BigDecimal tB = new BigDecimal(p[6]);

                    BigDecimal gram = BigDecimal.ZERO;
                    if (p.length >= 8) gram = new BigDecimal(p[7]);

                    VadesizHesap vadesiz = new VadesizHesap(vNo);
                    if (vB.compareTo(BigDecimal.ZERO) > 0) {
                        vadesiz.paraYatir(vB);
                    }

                    TasarrufHesabi tasarruf = new TasarrufHesabi(tNo);
                    if (tB.compareTo(BigDecimal.ZERO) > 0) {
                        tasarruf.paraYatir(tB);
                    }
                    tasarruf.altinGramAyarla(gram);

                    Musteri m = new Musteri(tc, adSoyad, sifreHash, vadesiz, tasarruf);
                    musteriler.put(tc, m);
                    
                } catch (Exception e) {
                    System.err.println("Satır okuma hatası: " + line + " -> " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Dosya genel hatası: " + e.getMessage());
        }
    }

    private void dosyayaKaydet() {
        File f = new File(DOSYA_YOLU);
        File parent = f.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();

        try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
            for (Musteri m : musteriler.values()) {
                VadesizHesap v = m.getVadesiz();
                TasarrufHesabi t = m.getTasarruf();

                pw.println(
                        m.getTc() + ";" +
                                m.getAdSoyad() + ";" +
                                m.getSifreHash() + ";" +
                                v.getHesapNo() + ";" +
                                v.getBakiye() + ";" +
                                t.getHesapNo() + ";" +
                                t.getBakiye() + ";" +
                                t.getAltinGram()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Dosyaya kaydetme hatası: " + e.getMessage(), e);
        }
    }

    /* ===================== ISLEM MANTIKLARI ===================== */

    // 1. Kendi Hesapları Arası Transfer
    public void kendiHesaplarimArasiTransfer(Musteri musteri, boolean vadesizdenTasarrufa, BigDecimal tutar) {
        if (vadesizdenTasarrufa) {
            // Vadesiz -> Tasarruf
            musteri.getVadesiz().paraCek(tutar, islemGecmisi);
            musteri.getTasarruf().paraYatir(tutar);
            
            islemGecmisi.ekle(new BilgiIslemi(musteri.getVadesiz().getHesapNo(), 
                "Virman: Vadesiz -> Tasarruf (" + tutar + " TL)"));
        } else {
            // Tasarruf -> Vadesiz
            musteri.getTasarruf().paraCek(tutar, islemGecmisi);
            musteri.getVadesiz().paraYatir(tutar);
            
            islemGecmisi.ekle(new BilgiIslemi(musteri.getTasarruf().getHesapNo(), 
                "Virman: Tasarruf -> Vadesiz (" + tutar + " TL)"));
        }
        dosyayaKaydet();
    }

    // 2. Başkasına Transfer (Vadesizden gider)
    public void transferYap(Musteri gonderen, String aliciHesapNo, String aliciAdSoyad, BigDecimal tutar) {
        Musteri alici = null;
        for (Musteri m : musteriler.values()) {
            if (m.getVadesiz().getHesapNo().equals(aliciHesapNo)) {
                alici = m;
                break;
            }
        }

        if (alici == null) {
            throw new IllegalArgumentException("Alıcı hesap numarası bulunamadı.");
        }
        if (!alici.getAdSoyad().equalsIgnoreCase(aliciAdSoyad)) {
            throw new IllegalArgumentException("Alıcı Ad Soyad bilgisi uyuşmuyor.");
        }
        if (gonderen.getTc().equals(alici.getTc())) {
            throw new IllegalArgumentException("Kendinize bu menüden transfer yapamazsınız.");
        }

        gonderen.getVadesiz().paraCek(tutar, islemGecmisi); 
        alici.getVadesiz().paraYatir(tutar);                

        islemGecmisi.ekle(new BilgiIslemi(gonderen.getVadesiz().getHesapNo(), 
            "EFT Giden -> " + alici.getAdSoyad() + ": " + tutar + " TL"));

        dosyayaKaydet();
    }

    // 3. Altın Al (Vadesizden TL düşer, Tasarrufa Altın ekler)
    public void altinAl(Musteri musteri, BigDecimal tlTutar, BigDecimal gramFiyat) {
        musteri.getVadesiz().paraCek(tlTutar, islemGecmisi);
        musteri.getTasarruf().altinAl(tlTutar, gramFiyat, islemGecmisi);
        dosyayaKaydet();
    }
}