package banka.hesap;

import banka.islem.AltinAlimIslemi;
import banka.islem.BilgiIslemi;
import banka.islem.IslemGecmisi;
import banka.islem.ParaCekmeIslemi;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class TasarrufHesabi extends Hesap {

    private final AltinCuzdan altinCuzdan = new AltinCuzdan();

    public TasarrufHesabi(String hesapNo) {
        super(hesapNo);
        // İlk açılış mesajını ekleyelim
        gecmisEkle("Tasarruf Hesabı Oluşturuldu.");
    }

    /* ===================== ALTIN İŞLEMLERİ ===================== */

    public BigDecimal getAltinGram() {
        return altinCuzdan.getGram();
    }

    // Altin alimi (sadece tasarruf hesabinda)
    public void altinAl(BigDecimal tlTutar, BigDecimal gramFiyat, IslemGecmisi gecmis) {
        if (tlTutar == null || tlTutar.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("TL tutari pozitif olmali.");
        }
        if (gramFiyat == null || gramFiyat.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Gram fiyati pozitif olmali.");
        }

        bakiyeKontrol(tlTutar);
        bakiyeAzalt(tlTutar);

        BigDecimal gram = tlTutar.divide(gramFiyat, 4, RoundingMode.HALF_UP);
        altinCuzdan.gramEkle(gram);

        // 1. Kişisel Geçmişe Yaz (Dosyaya kaydolması için)
        gecmisEkle("[YATIRIM] Altın Alındı: " + gram + " gr (Tutar: " + tlTutar + " TL)");

        // 2. Global Geçmişe Yaz (Program çalışırken görünsün diye)
        if (gecmis != null) {
            gecmis.ekle(new AltinAlimIslemi(getHesapNo(), tlTutar, gramFiyat, gram));
        }
    }

    /* ===================== PARA ÇEKME / TRANSFER ===================== */

    // Tasarruf -> Vadesiz TL aktarimi icin
    @Override
    public void paraCek(BigDecimal tutar, IslemGecmisi gecmis) {
        if (tutar == null || tutar.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Tutar pozitif olmali.");
        }

        bakiyeKontrol(tutar);
        bakiyeAzalt(tutar);

        // Kişisel Geçmişe Yaz
        gecmisEkle("[GİDER] Tasarruftan Çıkış: -" + tutar + " TL");

        // Global Geçmişe Yaz
        if (gecmis != null) {
            gecmis.ekle(new ParaCekmeIslemi(getHesapNo(), tutar));
        }
    }

    /* ===================== DOSYADAN YUKLEME ===================== */

    // SADECE dosyadan yukleme icin
    public void altinGramAyarla(BigDecimal gram) {
        altinCuzdan.gramAyarla(gram);
    }

    /* ===================== AY SONU ===================== */

    @Override
    public void aySonuIslemleri(IslemGecmisi gecmis) {
        // Faiz hesaplayıp ekleyelim (Örnek: %1 faiz)
        BigDecimal faiz = getBakiye().multiply(new BigDecimal("0.01"));
        if (faiz.compareTo(BigDecimal.ZERO) > 0) {
            paraYatir(faiz); // paraYatir metodu zaten geçmişe ekleme yapıyor
            gecmisEkle("[FAİZ] Ay Sonu Getirisi: +" + faiz + " TL");
        }

        if (gecmis != null) {
            gecmis.ekle(new BilgiIslemi(getHesapNo(), "Ay Sonu Özeti: " + getBakiye() + " TL | " + getAltinGram() + " gr Altın"));
        }
    }

    /* ===================== EKRANA YAZDIRMA (MainFrame İçin) ===================== */
    
    // MainFrame'in geçmişi göstermek için çağırdığı metot
    public String getIslemGecmisi() {
        // kisiselGecmis listesi Hesap.java'dan miras geliyor
        if (kisiselGecmis.isEmpty()) return "Henüz işlem yok.";
        
        StringBuilder sb = new StringBuilder();
        // En son işlem en üstte görünsün diye tersten yazdırıyoruz
        for (int i = kisiselGecmis.size() - 1; i >= 0; i--) {
            sb.append(kisiselGecmis.get(i)).append("\n");
        }
        return sb.toString();
    }
}