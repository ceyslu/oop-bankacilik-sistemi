package banka.islem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AltinAlimIslemi extends Islem {

    private final BigDecimal gramFiyat;
    private final BigDecimal gram;

    // tlTutar = altın almak için harcanan TL (bakiye düşen tutar)
    public AltinAlimIslemi(String hesapNo, BigDecimal tlTutar, BigDecimal gramFiyat, BigDecimal gram) {
        super(
                hesapNo,
                tlTutar,
                "ALTIN ALIMI: " + gram + " gr @ " + gramFiyat + " TL/gr",
                LocalDateTime.now()
        );
        this.gramFiyat = gramFiyat;
        this.gram = gram;
    }

    public BigDecimal getGramFiyat() {
        return gramFiyat;
    }

    public BigDecimal getGram() {
        return gram;
    }

    @Override
    public String ozet() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'ozet'");
    }
}