package banka.islem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BilgiIslemi extends Islem {
    private final String mesaj;

    public BilgiIslemi(String hesapNo, String mesaj) {
        super(hesapNo, BigDecimal.ZERO, "BILGI", LocalDateTime.now());
        this.mesaj = mesaj;
    }

    public String getMesaj() {
        return mesaj;
    }

    @Override
    public String ozet() {
        return "[" + getTarih() + "] " + getIslemTuru() + " | " + mesaj;
    }
}
