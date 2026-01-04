package banka.service;

import banka.hesap.TasarrufHesabi;
import banka.hesap.VadesizHesap;
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
    // YENİ: Geçmişi kaydedeceğimiz dosya yolu
    private static final String ISLEM_DOSYASI = "data/islemler.csv";

    public Banka() {
        dosyadanYukle();
        gecmisYukle(); // Program açılınca geçmişi geri getir
    }

    public Map<String, Musteri> getMusteriler() { return musteriler; }
    public IslemGecmisi getIslemGecmisi() { return islemGecmisi; }

    /* ===================== GİRİŞ / ÜYELİK ===================== */

    public Musteri girisYap(String adSoyad, String sifreHash) {
        if (adSoyad == null || adSoyad.isBlank()) throw new IllegalArgumentException("İsim boş olamaz.");
        String hedef = adSoyad.trim().toLowerCase();
        for (Musteri m : musteriler.values()) {
            if (m.getAdSoyad().trim().toLowerCase().equals(hedef)) {
                if (!m.sifreDogruMu(sifreHash)) throw new IllegalArgumentException("Şifre hatalı.");
                return m;
            }
        }
        throw new IllegalArgumentException("Kullanıcı bulunamadı.");
    }

    public Musteri uyeOl(String tc, String adSoyad, String sifreHash) {
        if (musteriler.containsKey(tc)) throw new IllegalArgumentException("Kayıtlı TC.");

        int sira = musteriler.size() + 1;
        String vNo = "100" + String.format("%03d", sira);
        String tNo = "200" + String.format("%03d", sira);

        VadesizHesap vadesiz = new VadesizHesap(vNo);
        TasarrufHesabi tasarruf = new TasarrufHesabi(tNo);

        // BONUS YÜKLEME
        vadesiz.paraYatir(new BigDecimal("1000"));
        tasarruf.paraYatir(new BigDecimal("1000"));

        // Bonus mesajlarını özelleştir
        vadesiz.sonGecmisiSil(); 
        vadesiz.gecmisEkle("[PROMOSYON] HOŞ GELDİN BONUSU | +1000 TL");
        
        tasarruf.sonGecmisiSil(); 
        tasarruf.gecmisEkle("[PROMOSYON] HOŞ GELDİN BONUSU | +1000 TL");

        Musteri yeni = new Musteri(tc, adSoyad, sifreHash, vadesiz, tasarruf);
        musteriler.put(tc, yeni);
        
        dosyayaKaydet(); // Müşteriyi ve geçmişi kaydet
        return yeni;
    }

    /* ===================== İŞLEMLER ===================== */

    public void kendiHesaplarimArasiTransfer(Musteri musteri, boolean vadesizdenTasarrufa, BigDecimal tutar) {
        if (vadesizdenTasarrufa) {
            musteri.getVadesiz().paraCek(tutar, islemGecmisi);
            musteri.getVadesiz().sonGecmisiSil();
            musteri.getVadesiz().gecmisEkle("[VİRMAN] GİDER: TASARRUF HESABINA | -" + tutar + " TL");

            musteri.getTasarruf().paraYatir(tutar);
            musteri.getTasarruf().sonGecmisiSil();
            musteri.getTasarruf().gecmisEkle("[VİRMAN] GELİR: VADESİZ HESAPTAN | +" + tutar + " TL");
        } else {
            musteri.getTasarruf().paraCek(tutar, islemGecmisi);
            musteri.getTasarruf().sonGecmisiSil();
            musteri.getTasarruf().gecmisEkle("[VİRMAN] GİDER: VADESİZ HESABA | -" + tutar + " TL");

            musteri.getVadesiz().paraYatir(tutar);
            musteri.getVadesiz().sonGecmisiSil();
            musteri.getVadesiz().gecmisEkle("[VİRMAN] GELİR: TASARRUF HESABINDAN | +" + tutar + " TL");
        }
        dosyayaKaydet();
    }

    public void transferYap(Musteri gonderen, String aliciHesapNo, String aliciAdSoyad, BigDecimal tutar) {
        Musteri alici = null;
        for (Musteri m : musteriler.values()) {
            if (m.getVadesiz().getHesapNo().equals(aliciHesapNo)) {
                alici = m;
                break;
            }
        }

        if (alici == null) throw new IllegalArgumentException("Alıcı bulunamadı.");
        if (!alici.getAdSoyad().equalsIgnoreCase(aliciAdSoyad)) throw new IllegalArgumentException("İsim uyuşmuyor.");
        if (gonderen.getTc().equals(alici.getTc())) throw new IllegalArgumentException("Kendine EFT yapamazsın.");

        // GÖNDEREN
        gonderen.getVadesiz().paraCek(tutar, islemGecmisi);
        gonderen.getVadesiz().sonGecmisiSil();
        gonderen.getVadesiz().gecmisEkle("[EFT GİDER] ALICI: " + alici.getAdSoyad().toUpperCase() + " | -" + tutar + " TL");

        // ALICI
        alici.getVadesiz().paraYatir(tutar);
        alici.getVadesiz().sonGecmisiSil();
        alici.getVadesiz().gecmisEkle("[EFT GELİR] GÖNDEREN: " + gonderen.getAdSoyad().toUpperCase() + " | +" + tutar + " TL");

        dosyayaKaydet();
    }

    public void altinAl(Musteri musteri, BigDecimal tlTutar, BigDecimal gramFiyat) {
        musteri.getVadesiz().paraCek(tlTutar, islemGecmisi);
        musteri.getVadesiz().sonGecmisiSil();
        musteri.getVadesiz().gecmisEkle("[YATIRIM] ALTIN ALIMI İÇİN GİDER | -" + tlTutar + " TL");

        musteri.getTasarruf().altinAl(tlTutar, gramFiyat, islemGecmisi);
        dosyayaKaydet();
    }

    /* ===================== DOSYA SİSTEMİ (MÜŞTERİ + GEÇMİŞ) ===================== */

    private void dosyadanYukle() {
        File f = new File(DOSYA_YOLU);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(";");
                if (p.length < 7) continue;
                try {
                    String tc = p[0], ad = p[1], sifre = p[2], vNo = p[3], tNo = p[5];
                    BigDecimal vB = new BigDecimal(p[4]), tB = new BigDecimal(p[6]);
                    BigDecimal gr = (p.length>=8) ? new BigDecimal(p[7]) : BigDecimal.ZERO;
                    
                    VadesizHesap v = new VadesizHesap(vNo);
                    if(vB.compareTo(BigDecimal.ZERO)>0) { 
                        v.paraYatir(vB);
                        v.sonGecmisiSil(); // Yüklemedeki otomatik mesajı sil
                        v.gecmisEkle("[SİSTEM] Bakiye Yüklendi: " + vB + " TL");
                    }
                    
                    TasarrufHesabi t = new TasarrufHesabi(tNo);
                    if(tB.compareTo(BigDecimal.ZERO)>0) { 
                        t.paraYatir(tB);
                        t.sonGecmisiSil();
                        t.gecmisEkle("[SİSTEM] Bakiye Yüklendi: " + tB + " TL");
                    }
                    t.altinGramAyarla(gr);

                    musteriler.put(tc, new Musteri(tc, ad, sifre, v, t));
                } catch (Exception e) {}
            }
        } catch (Exception e) {}
    }

    // YENİ METOT: GEÇMİŞ YÜKLEME
    private void gecmisYukle() {
        File f = new File(ISLEM_DOSYASI);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Dosya formatı: HESAP_NO;MESAJ
                String[] parts = line.split(";", 2);
                if (parts.length < 2) continue;

                String hesapNo = parts[0];
                String mesaj = parts[1];

                // Hesabı bul ve mesajı içine koy
                for (Musteri m : musteriler.values()) {
                    if (m.getVadesiz().getHesapNo().equals(hesapNo)) {
                        m.getVadesiz().gecmisEkle(mesaj);
                    } else if (m.getTasarruf().getHesapNo().equals(hesapNo)) {
                        m.getTasarruf().gecmisEkle(mesaj);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Geçmiş yüklenemedi: " + e.getMessage());
        }
    }

    private void dosyayaKaydet() {
        // 1. Müşterileri Kaydet (Eski kod)
        File f = new File(DOSYA_YOLU);
        File parent = f.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();

        try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
            for (Musteri m : musteriler.values()) {
                pw.println(m.getTc()+";"+m.getAdSoyad()+";"+m.getSifreHash()+";"+
                    m.getVadesiz().getHesapNo()+";"+m.getVadesiz().getBakiye()+";"+
                    m.getTasarruf().getHesapNo()+";"+m.getTasarruf().getBakiye()+";"+m.getTasarruf().getAltinGram());
            }
        } catch (Exception e) {}

        // 2. Geçmişi Kaydet (YENİ EKLENDİ)
        gecmisKaydet();
    }

    // YENİ METOT: GEÇMİŞ KAYDETME
    private void gecmisKaydet() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ISLEM_DOSYASI))) {
            for (Musteri m : musteriler.values()) {
                // Vadesiz hesaptaki her satırı dosyaya yaz
                for (String msg : m.getVadesiz().getGecmisListesi()) {
                    pw.println(m.getVadesiz().getHesapNo() + ";" + msg);
                }
                // Tasarruf hesabındaki her satırı dosyaya yaz
                for (String msg : m.getTasarruf().getGecmisListesi()) {
                    pw.println(m.getTasarruf().getHesapNo() + ";" + msg);
                }
            }
        } catch (Exception e) {
            System.err.println("Geçmiş kaydedilemedi: " + e.getMessage());
        }
    }
}