package banka.islem;

import java.math.BigDecimal;

public class UcretIslemi extends Islem {
    private final String hesapNo;
    private final BigDecimal tutar;
    private final String neden;

    public UcretIslemi(String hesapNo, BigDecimal tutar, String neden) {
        super("Ucret");
        this.hesapNo = hesapNo;
        this.tutar = tutar;
        this.neden = neden;
    }

    @Override
    public String ozet() {
        return getZaman() + " | " + hesapNo + " | - " + tutar + " TL (" + neden + ")";
    }
}

