package banka.hesap;

import banka.islem.BilgiIslemi;
import banka.islem.IslemGecmisi;
import banka.islem.ParaCekmeIslemi;
import banka.islem.TransferIslemi;
import banka.islem.UcretIslemi;

import java.math.BigDecimal;

public class VadesizHesap extends Hesap implements TransferEdilebilir {

    private static final BigDecimal AYLIK_UCRET = new BigDecimal("5");

    public VadesizHesap(String hesapNo) {
        super(hesapNo);
    }

    public void paraCek(BigDecimal tutar, IslemGecmisi gecmis) {
        if (tutar == null || tutar.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Tutar pozitif olmali.");
        }
        bakiyeKontrol(tutar);
        bakiyeAzalt(tutar);
        gecmis.ekle(new ParaCekmeIslemi(getHesapNo(), tutar));
    }

    @Override
    public void transferEt(Hesap hedef, BigDecimal tutar) {
        // Bu metodu Banka üzerinden kullanacağız, burada boş bırakmıyoruz ama
        // UI'da direkt çağırmayacağız.
        throw new UnsupportedOperationException("Transfer islemi Banka uzerinden yapilacak.");
    }

    public void transferEt(Hesap hedef, BigDecimal tutar, IslemGecmisi gecmis) {
        if (hedef == null) throw new IllegalArgumentException("Hedef hesap bos olamaz.");
        if (tutar == null || tutar.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Tutar pozitif olmali.");
        }
        bakiyeKontrol(tutar);
        bakiyeAzalt(tutar);
        hedef.paraYatir(tutar);
        gecmis.ekle(new TransferIslemi(getHesapNo(), hedef.getHesapNo(), tutar));
    }

    @Override
    public void aySonuIslemleri(IslemGecmisi gecmis) {
        if (getBakiye().compareTo(AYLIK_UCRET) >= 0) {
            bakiyeAzalt(AYLIK_UCRET);
            gecmis.ekle(new UcretIslemi(getHesapNo(), AYLIK_UCRET, "Aylik hesap isletim ucreti"));
        } else {
            gecmis.ekle(new BilgiIslemi(getHesapNo(), "Ay sonu: ucret kesilemedi (yetersiz bakiye)"));
        }
    }
}

