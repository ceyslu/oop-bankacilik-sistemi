package banka.hesap;

import banka.islem.AltinAlimIslemi;
import banka.islem.BilgiIslemi;
import banka.islem.IslemGecmisi;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TasarrufHesabi extends Hesap {

    private final AltinCuzdan altinCuzdan = new AltinCuzdan();

    public TasarrufHesabi(String hesapNo) {
        super(hesapNo);
    }

    public BigDecimal getAltinGram() {
        return altinCuzdan.getGram();
    }

    public void altinAl(BigDecimal tlTutar, BigDecimal gramFiyat, IslemGecmisi gecmis) {
        if (tlTutar == null || tlTutar.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("TL tutari pozitif olmali.");
        }
        if (gramFiyat == null || gramFiyat.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Gram fiyati pozitif olmali.");
        }

        bakiyeKontrol(tlTutar);
        bakiyeAzalt(tlTutar);

        BigDecimal gram = tlTutar.divide(gramFiyat, 4, RoundingMode.HALF_UP);
        altinCuzdan.gramEkle(gram);

        gecmis.ekle(new AltinAlimIslemi(getHesapNo(), tlTutar, gramFiyat, gram));
    }

    @Override
    public void aySonuIslemleri(IslemGecmisi gecmis) {
        gecmis.ekle(new BilgiIslemi(getHesapNo(),
                "Ay sonu ozeti: TL=" + getBakiye() + ", Altin(gram)=" + altinCuzdan.getGram()));
    }
}

