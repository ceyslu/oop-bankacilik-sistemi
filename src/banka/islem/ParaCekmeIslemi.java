package banka.islem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ParaCekmeIslemi extends Islem {

    public ParaCekmeIslemi(String hesapNo, BigDecimal tutar) {
        super(hesapNo, tutar, "PARA_CEKME", LocalDateTime.now());
    }

    
    public String ozet() {
        return "[" + getTarih() + "] PARA CEKME | Hesap: "
                + getHesapNo() + " | Tutar: " + getTutar() + " TL";
    }
}

