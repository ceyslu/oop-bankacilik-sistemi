package banka.hesap;

import banka.islem.IslemGecmisi;
import java.math.BigDecimal;

public abstract class Hesap {
    private final String hesapNo;
    private BigDecimal bakiye = BigDecimal.ZERO;

    protected Hesap(String hesapNo) {
        this.hesapNo = hesapNo;
    }

    public String getHesapNo() { return hesapNo; }
    public BigDecimal getBakiye() { return bakiye; }

    public void paraYatir(BigDecimal tutar) {
        if (tutar == null || tutar.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Tutar pozitif olmali.");
        }
        bakiye = bakiye.add(tutar);
    }

    protected void bakiyeAzalt(BigDecimal tutar) {
        bakiye = bakiye.subtract(tutar);
    }
    protected void bakiyeKontrol(BigDecimal tutar) {
        if (bakiye.compareTo(tutar) < 0) {
            throw new IllegalArgumentException("Yetersiz bakiye.");
        }
    }

    public abstract void aySonuIslemleri(IslemGecmisi gecmis);// SADECE dosyadan yukleme icin



}

