package banka.hesap;

import banka.islem.BilgiIslemi;
import banka.islem.IslemGecmisi;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VadesizHesap extends Hesap {

    private Map<String, BigDecimal> faturalar = new HashMap<>();
    private List<String> kisiselGecmis = new ArrayList<>();

    public VadesizHesap(String hesapNo) {
        super(hesapNo);
        gecmisEkle("[SİSTEM] HESAP AÇILIŞI");
    }

    /* ===================== FATURA ===================== */
    
    public void faturaKaydet(String faturaTuru, BigDecimal miktar) {
        if (miktar.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Tutar pozitif olmalı.");
        faturalar.put(faturaTuru, miktar);
        gecmisEkle("[FATURA] KAYIT: " + faturaTuru.toUpperCase() + " | " + miktar + " TL");
    }

    public void faturaOde(String faturaTuru) {
        if (!faturalar.containsKey(faturaTuru)) throw new IllegalArgumentException("Fatura bulunamadı.");
        BigDecimal tutar = faturalar.get(faturaTuru);
        
        paraCek(tutar, null); // Parayı düş
        faturalar.remove(faturaTuru);
        
        // Son eklenen 'ÇEKİM' mesajını silip yerine daha detaylısını yazalım ki çift olmasın
        kisiselGecmis.remove(kisiselGecmis.size() - 1); 
        gecmisEkle("[FATURA] ÖDEME: " + faturaTuru.toUpperCase() + " | -" + tutar + " TL");
    }

    public Map<String, BigDecimal> getFaturalar() { return faturalar; }

    /* ===================== PARA İŞLEMLERİ ===================== */
    
    @Override
    public void paraCek(BigDecimal tutar, IslemGecmisi gecmis) {
        if (tutar == null || tutar.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Hatalı tutar.");
        if (getBakiye().compareTo(tutar) < 0) throw new IllegalArgumentException("Yetersiz Bakiye!");

        bakiyeAzalt(tutar);

        // Standart Çekim Mesajı (Transferlerde bunu ezeceğiz veya detaylandıracağız)
        gecmisEkle("[GİDER] NAKİT ÇIKIŞI | -" + tutar + " TL");

        if (gecmis != null) gecmis.ekle(new BilgiIslemi(getHesapNo(), "Çıkış: " + tutar));
    }

    @Override
    public void paraYatir(BigDecimal tutar) {
        super.paraYatir(tutar);
        gecmisEkle("[GELİR] NAKİT GİRİŞİ | +" + tutar + " TL");
    }

    public void transferEt(Hesap alici, BigDecimal tutar, IslemGecmisi gecmis) {
        if (alici == null) throw new IllegalArgumentException("Alıcı yok.");
        paraCek(tutar, gecmis);
        alici.paraYatir(tutar);
    }

    /* ===================== GEÇMİŞ YÖNETİMİ ===================== */
    
    // BUNU PUBLIC YAPTIK (ÖNEMLİ): Dışarıdan özel mesaj yazılabilsin diye.
    public void gecmisEkle(String mesaj) {
        kisiselGecmis.add(mesaj);
    }

    // Son eklenen satırı silmek için (Transfer detaylarını düzeltirken lazım olacak)
    public void sonGecmisiSil() {
        if (!kisiselGecmis.isEmpty()) {
            kisiselGecmis.remove(kisiselGecmis.size() - 1);
        }
    }

    public String getIslemGecmisi() {
        if (kisiselGecmis.isEmpty()) return "İşlem Yok.";
        StringBuilder sb = new StringBuilder();
        for (int i = kisiselGecmis.size() - 1; i >= 0; i--) {
            sb.append(kisiselGecmis.get(i)).append("\n");
            sb.append("--------------------------------\n");
        }
        return sb.toString();
    }

    @Override
    public void aySonuIslemleri(IslemGecmisi gecmis) {
        gecmisEkle("[EKSTRE] AY SONU BAKİYE: " + getBakiye() + " TL");
    }
}