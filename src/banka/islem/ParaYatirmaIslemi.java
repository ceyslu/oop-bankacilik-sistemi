package banka.islem;

import java.math.BigDecimal;

public class ParaYatirmaIslemi extends Islem {
    private final String hesapNo;
    private final BigDecimal tutar;
    private final String neden;

    public ParaYatirmaIslemi(String hesapNo, BigDecimal tutar, String neden) {
        super("Para yatirma");
        this.hesapNo = hesapNo;
        this.tutar = tutar;
        this.neden = neden;
    }

    @Override
    public String ozet() {
        String n = (neden == null || neden.isBlank()) ? "" : " (" + neden + ")";
        return getZaman() + " | " + hesapNo + " | + " + tutar + " TL" + n;
    }
}
