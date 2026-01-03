package banka.islem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ParaYatirmaIslemi extends Islem {

    public ParaYatirmaIslemi(String hesapNo, BigDecimal tutar) {
        super(hesapNo, tutar, "PARA_YATIRMA", LocalDateTime.now());
    }

    @Override
    public String ozet() {
        return "[" + getTarih() + "] PARA YATIRMA | Hesap: "
                + getHesapNo() + " | Tutar: " + getTutar() + " TL";
    }
}
