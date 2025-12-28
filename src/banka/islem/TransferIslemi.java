package banka.islem;

import java.math.BigDecimal;

public class TransferIslemi extends Islem {
    private final String gonderen;
    private final String alan;
    private final BigDecimal tutar;

    public TransferIslemi(String gonderen, String alan, BigDecimal tutar) {
        super("Transfer");
        this.gonderen = gonderen;
        this.alan = alan;
        this.tutar = tutar;
    }

    @Override
    public String ozet() {
        return getZaman() + " | " + gonderen + " -> " + alan + " | " + tutar + " TL";
    }
}

