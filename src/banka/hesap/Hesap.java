package banka.hesap;

import banka.islem.IslemGecmisi;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

// "implements IHesapIslemleri" diyerek sözleşmeyi kabul ettik.
public abstract class Hesap implements IHesapIslemleri { 

    private final String hesapNo;
    private BigDecimal bakiye = BigDecimal.ZERO;
    
    // Vadesiz ve Tasarruf'un ortak kullanacağı işlem defteri
    protected List<String> kisiselGecmis = new ArrayList<>();

    protected Hesap(String hesapNo) {
        this.hesapNo = hesapNo;
    }

    public String getHesapNo() { return hesapNo; }
    public BigDecimal getBakiye() { return bakiye; }

    // --- Interface'den Gelen Zorunlu Metot (Sözleşme Gereği) ---
    @Override
    public void paraYatir(BigDecimal tutar) {
        if (tutar == null || tutar.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Tutar pozitif olmalı.");
        }
        bakiye = bakiye.add(tutar);
        // Otomatik not al
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

    // --- Ortak Metotlar ---

    public void gecmisEkle(String mesaj) {
        kisiselGecmis.add(mesaj);
    }

    public void sonGecmisiSil() {
        if (!kisiselGecmis.isEmpty()) {
            kisiselGecmis.remove(kisiselGecmis.size() - 1);
        }
    }

    public List<String> getGecmisListesi() {
        return kisiselGecmis;
    }

    public abstract void aySonuIslemleri(IslemGecmisi gecmis);
    
    // --- Interface'den Gelen Diğer Metot ---
    // Bunu abstract yapıyoruz ki her hesap (Vadesiz/Tasarruf) kendine özel doldursun.
    @Override
    public abstract void paraCek(BigDecimal tutar, IslemGecmisi gecmis);
}