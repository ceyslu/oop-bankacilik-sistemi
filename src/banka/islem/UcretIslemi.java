package banka.islem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UcretIslemi extends Islem {

    private final String neden;

    public UcretIslemi(String hesapNo, BigDecimal tutar, String neden) {
        super(hesapNo, tutar, "UCRET KESILDI: " + neden, LocalDateTime.now());
        this.neden = (neden == null) ? "" : neden;
    }

    public String getNeden() {
        return neden;
    }

    @Override
    public String ozet() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'ozet'");
    }
}