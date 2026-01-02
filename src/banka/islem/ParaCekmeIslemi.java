package banka.islem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ParaCekmeIslemi extends Islem {

    public ParaCekmeIslemi(String hesapNo, BigDecimal tutar) {
        super(hesapNo, tutar, "Para Cekme", LocalDateTime.now());
    }

    @Override
    public String ozet() {
        return getTarih() + " | " + getIslemTuru() + " | Hesap: " + getHesapNo() + " | -" + getTutar() + " TL";
    }
}

