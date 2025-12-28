package banka.islem;

import java.math.BigDecimal;

public class AltinAlimIslemi extends Islem {
    private final String hesapNo;
    private final BigDecimal tlTutar;
    private final BigDecimal gramFiyat;
    private final BigDecimal gram;

    public AltinAlimIslemi(String hesapNo, BigDecimal tlTutar, BigDecimal gramFiyat, BigDecimal gram) {
        super("Altin alim");
        this.hesapNo = hesapNo;
        this.tlTutar = tlTutar;
        this.gramFiyat = gramFiyat;
        this.gram = gram;
    }

    @Override
    public String ozet() {
        return getZaman() + " | " + hesapNo + " | Altin alindi: " + tlTutar + " TL / " + gramFiyat + " = " + gram + " gr";
    }
}

