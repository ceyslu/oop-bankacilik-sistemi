package banka.islem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public abstract class Islem {
    private final String hesapNo;
    private final BigDecimal tutar;
    private final String islemTuru;
    private final LocalDateTime tarih;

    protected Islem(String hesapNo, BigDecimal tutar, String islemTuru, LocalDateTime tarih) {
        this.hesapNo = hesapNo;
        this.tutar = tutar;
        this.islemTuru = islemTuru;
        this.tarih = tarih;
    }

    public String getHesapNo() { return hesapNo; }
    public BigDecimal getTutar() { return tutar; }
    public String getIslemTuru() { return islemTuru; }
    public LocalDateTime getTarih() { return tarih; }

    public abstract String ozet();
}
