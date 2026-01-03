package banka.islem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public abstract class Islem {
    private final String hesapNo;
    private final BigDecimal tutar;
    private final String aciklama;
    private final LocalDateTime zaman;

    protected Islem(String hesapNo, BigDecimal tutar, String aciklama, LocalDateTime zaman) {
        if (hesapNo == null || hesapNo.isBlank()) {
            throw new IllegalArgumentException("Hesap no bos olamaz.");
        }
        if (tutar == null) {
            throw new IllegalArgumentException("Tutar bos olamaz.");
        }
        if (aciklama == null) {
            throw new IllegalArgumentException("Aciklama bos olamaz.");
        }
        this.hesapNo = hesapNo;
        this.tutar = tutar;
        this.aciklama = aciklama;
        this.zaman = (zaman == null) ? LocalDateTime.now() : zaman;
    }

    public String getHesapNo() {
        return hesapNo;
    }

    public BigDecimal getTutar() {
        return tutar;
    }

    public String getAciklama() {
        return aciklama;
    }

    public LocalDateTime getTarih() {
        return zaman;
    }
     
      public abstract String ozet();

    public String getIslemTuru() {
        return aciklama.split(":")[0];
    }
    @Override
    public String toString() {
        return zaman + " | " + hesapNo + " | " + aciklama + " | " + tutar;
    }
}