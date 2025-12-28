package banka.hesap;

import java.math.BigDecimal;

public class AltinCuzdan {
    private BigDecimal gram = BigDecimal.ZERO;

    public BigDecimal getGram() {
        return gram;
    }

    public void gramEkle(BigDecimal eklenecek) {
        if (eklenecek == null || eklenecek.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Gram pozitif olmali.");
        }
        gram = gram.add(eklenecek);
    }
}
