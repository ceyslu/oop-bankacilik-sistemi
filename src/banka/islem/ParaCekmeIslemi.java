package banka.islem;

import java.math.BigDecimal;

public class ParaCekmeIslemi extends Islem {
    private final String hesapNo;
    private final BigDecimal tutar;

    public ParaCekmeIslemi(String hesapNo, BigDecimal tutar) {
        super("Para cekme");
        this.hesapNo = hesapNo;
        this.tutar = tutar;
    }

    @Override
    public String ozet() {
        return getZaman() + " | " + hesapNo + " | - " + tutar + " TL";
    }
}
