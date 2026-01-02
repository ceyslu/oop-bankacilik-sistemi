package banka.hesap;

import java.math.BigDecimal;

import banka.islem.IslemGecmisi;

public class VadesizHesap extends Hesap {

    public VadesizHesap(String hesapNo) {
        super(hesapNo);
    }

    /*
     * Vadesiz hesapta:
     * - Para yatirma YOK
     * - Para cekme YOK
     * Tum islemler transfer uzerinden Banka sinifi ile yapilir
     */
public void transferEt(Hesap alici, BigDecimal tutar, banka.islem.IslemGecmisi gecmis) {
    if (tutar == null || tutar.compareTo(BigDecimal.ZERO) <= 0)
        throw new IllegalArgumentException("Tutar pozitif olmali");

    bakiyeKontrol(tutar);
    bakiyeAzalt(tutar);
    alici.bakiyeArtir(tutar);

    // burada TransferIslemi ekle (varsa)
    gecmis.ekle(new banka.islem.BilgiIslemi(getHesapNo(),
            "TRANSFER: " + getHesapNo() + " -> " + alici.getHesapNo() + " | " + tutar + " TL"));
}

    @Override
    public void aySonuIslemleri(IslemGecmisi gecmis) {
        // Vadesiz hesapta ay sonu ozel islem yok
    }
}
