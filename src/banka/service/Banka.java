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

    public Banka() { dosyadanYukle(); }
    public Map<String, Musteri> getMusteriler() { return musteriler; }
    public IslemGecmisi getIslemGecmisi() { return islemGecmisi; }

    /* ===================== GİRİŞ / KAYIT ===================== */

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

        // BONUS YÜKLEME (Mesajlar otomatik "NAKİT GİRİŞİ" olarak yazılacak)
        vadesiz.paraYatir(new BigDecimal("1000"));
        tasarruf.paraYatir(new BigDecimal("1000"));
        
        // Bonus mesajını özelleştirelim (İsteğe bağlı, şık durur)
        vadesiz.sonGecmisiSil(); vadesiz.gecmisEkle("[PROMOSYON] HOŞ GELDİN BONUSU | +1000 TL");
        tasarruf.sonGecmisiSil(); tasarruf.gecmisEkle("[PROMOSYON] HOŞ GELDİN BONUSU | +1000 TL");

        Musteri yeni = new Musteri(tc, adSoyad, sifreHash, vadesiz, tasarruf);
        musteriler.put(tc, yeni);
        dosyayaKaydet();
        return yeni;
    }

    /* ===================== TRANSFERLER (İSİMLİ) ===================== */

    // 1. KENDİ HESAPLARIN (VİRMAN)
    public void kendiHesaplarimArasiTransfer(Musteri musteri, boolean vadesizdenTasarrufa, BigDecimal tutar) {
        if (vadesizdenTasarrufa) {
            // Vadesiz -> Tasarruf
            musteri.getVadesiz().paraCek(tutar, islemGecmisi);
            musteri.getVadesiz().sonGecmisiSil(); // Standart mesajı sil
            musteri.getVadesiz().gecmisEkle("[VİRMAN] GİDER: TASARRUF HESABINA | -" + tutar + " TL");

            musteri.getTasarruf().paraYatir(tutar);
            musteri.getTasarruf().sonGecmisiSil();
            musteri.getTasarruf().gecmisEkle("[VİRMAN] GELİR: VADESİZ HESAPTAN | +" + tutar + " TL");

        } else {
            // Tasarruf -> Vadesiz
            musteri.getTasarruf().paraCek(tutar, islemGecmisi);
            musteri.getTasarruf().sonGecmisiSil();
            musteri.getTasarruf().gecmisEkle("[VİRMAN] GİDER: VADESİZ HESABA | -" + tutar + " TL");

            musteri.getVadesiz().paraYatir(tutar);
            musteri.getVadesiz().sonGecmisiSil();
            musteri.getVadesiz().gecmisEkle("[VİRMAN] GELİR: TASARRUF HESABINDAN | +" + tutar + " TL");
        }
        dosyayaKaydet();
    }

    // 2. BAŞKASINA TRANSFER (EFT) - İŞTE İSTEDİĞİN YER BURASI
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

        // --- GÖNDEREN İŞLEMİ ---
        gonderen.getVadesiz().paraCek(tutar, islemGecmisi);
        gonderen.getVadesiz().sonGecmisiSil(); // Otomatik mesajı kaldır
        // Özel mesaj ekle: Alıcının adını BÜYÜK HARFLE yaz
        gonderen.getVadesiz().gecmisEkle("[EFT GİDER] ALICI: " + alici.getAdSoyad().toUpperCase() + " | -" + tutar + " TL");

        // --- ALICI İŞLEMİ ---
        alici.getVadesiz().paraYatir(tutar);
        alici.getVadesiz().sonGecmisiSil(); // Otomatik mesajı kaldır
        // Özel mesaj ekle: Gönderenin adını yaz
        alici.getVadesiz().gecmisEkle("[EFT GELİR] GÖNDEREN: " + gonderen.getAdSoyad().toUpperCase() + " | +" + tutar + " TL");

        dosyayaKaydet();
    }

    // 3. ALTIN AL
    public void altinAl(Musteri musteri, BigDecimal tlTutar, BigDecimal gramFiyat) {
        // Vadesizden çek (Otomatik mesajı silip detaylı yazalım)
        musteri.getVadesiz().paraCek(tlTutar, islemGecmisi);
        musteri.getVadesiz().sonGecmisiSil();
        musteri.getVadesiz().gecmisEkle("[YATIRIM] ALTIN ALIMI İÇİN GİDER | -" + tlTutar + " TL");

        musteri.getTasarruf().altinAl(tlTutar, gramFiyat, islemGecmisi);
        dosyayaKaydet();
    }

    // --- DOSYA İŞLEMLERİ (Aynen Kalıyor) ---
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
                    BigDecimal vB = new BigDecimal(p[4]), tB = new BigDecimal(p[6]), gr = (p.length>=8)?new BigDecimal(p[7]):BigDecimal.ZERO;
                    
                    VadesizHesap v = new VadesizHesap(vNo);
                    if(vB.compareTo(BigDecimal.ZERO)>0) v.paraYatir(vB);
                    
                    TasarrufHesabi t = new TasarrufHesabi(tNo);
                    if(tB.compareTo(BigDecimal.ZERO)>0) t.paraYatir(tB);
                    t.altinGramAyarla(gr);

                    // Açılış mesajlarını temizle (Dosyadan yüklenince geçmiş boş olsun veya özet olsun)
                    // (Şimdilik basit tutuyoruz)

                    musteriler.put(tc, new Musteri(tc, ad, sifre, v, t));
                } catch (Exception e) {}
            }
        } catch (Exception e) {}
    }

    private void dosyayaKaydet() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DOSYA_YOLU))) {
            for (Musteri m : musteriler.values()) {
                pw.println(m.getTc()+";"+m.getAdSoyad()+";"+m.getSifreHash()+";"+
                    m.getVadesiz().getHesapNo()+";"+m.getVadesiz().getBakiye()+";"+
                    m.getTasarruf().getHesapNo()+";"+m.getTasarruf().getBakiye()+";"+m.getTasarruf().getAltinGram());
            }
        } catch (Exception e) {}
    }
}