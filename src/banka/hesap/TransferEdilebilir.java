package banka.hesap;

import java.math.BigDecimal;

public interface TransferEdilebilir {
    void transferEt(Hesap hedef, BigDecimal tutar);
}

