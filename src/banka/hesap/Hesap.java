package banka.hesap;

import banka.islem.IslemGecmisi;
import java.math.BigDecimal;
import java.util.ArrayList; // Listeyi kullanmak için gerekli
import java.util.List;      // Listeyi kullanmak için gerekli

// Abstract olduğu için Vadesiz ve Tasarruf buradan miras alır
public abstract class Hesap { 

    private final String hesapNo;
    private BigDecimal bakiye = BigDecimal.ZERO;
    
    // YENİ: Vadesiz ve Tasarruf'un ortak kullanacağı işlem defteri
    protected List<String> kisiselGecmis = new ArrayList<>();

    protected Hesap(String hesapNo) {
        this.hesapNo = hesapNo;
    }

    public String getHesapNo() { return hesapNo; }
    public BigDecimal getBakiye() { return bakiye; }

    public void paraYatir(BigDecimal tutar) {
        if (tutar == null || tutar.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Tutar pozitif olmalı.");
        }
        bakiye = bakiye.add(tutar);
        // Her para girişinde otomatik not alalım
        gecmisEkle("[GELİR] Para Yatırma: +" + tutar + " TL");
    }

    protected void bakiyeAzalt(BigDecimal tutar) {
        bakiye = bakiye.subtract(tutar);
    }
    
    protected void bakiyeKontrol(BigDecimal tutar) {
        if (bakiye.compareTo(tutar) < 0) {
            throw new IllegalArgumentException("Yetersiz bakiye.");
        }
    }

    // --- İŞTE KIRMIZILIĞI GİDEREN METOTLAR BURADA ---

    // 1. Dışarıdan mesaj eklemek için (Banka.java kullanıyor)
    public void gecmisEkle(String mesaj) {
        kisiselGecmis.add(mesaj);
    }

    // 2. Son eklenen mesajı silmek için (Yükleme yaparken kullanıyoruz)
    public void sonGecmisiSil() {
        if (!kisiselGecmis.isEmpty()) {
            kisiselGecmis.remove(kisiselGecmis.size() - 1);
        }
    }

    // 3. Dosyaya kaydederken listeyi vermek için
    public List<String> getGecmisListesi() {
        return kisiselGecmis;
    }

    // --- DİĞER SOYUT METOTLAR ---
    public abstract void aySonuIslemleri(IslemGecmisi gecmis);
    public abstract void paraCek(BigDecimal tutar, IslemGecmisi gecmis);
}