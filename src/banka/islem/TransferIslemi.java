package banka.islem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransferIslemi extends Islem {

    private final String aliciHesapNo;

    public TransferIslemi(String gonderenHesapNo, String aliciHesapNo, BigDecimal tutar) {
        super(gonderenHesapNo, tutar, "TRANSFER", LocalDateTime.now());
        this.aliciHesapNo = aliciHesapNo;
    }

    public String getAliciHesapNo() {
        return aliciHesapNo;
    }

    public String ozet() {
        return "[" + getTarih() + "] TRANSFER | "
                + getHesapNo() + " -> " + aliciHesapNo
                + " | Tutar: " + getTutar() + " TL";
    }
}
