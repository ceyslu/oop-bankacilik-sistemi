package banka.hesap;

import banka.islem.AltinAlimIslemi;
import banka.islem.IslemGecmisi;
import banka.islem.ParaCekmeIslemi;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class TasarrufHesabi extends Hesap {

    private final AltinCuzdan altinCuzdan = new AltinCuzdan();
    private List<String> kisiselGecmis = new ArrayList<>();

    public TasarrufHesabi(String hesapNo) {
        super(hesapNo);
        gecmisEkle("[SİSTEM] TASARRUF HESABI AÇILIŞI");
    }

    public BigDecimal getAltinGram() { return altinCuzdan.getGram(); }

    public void altinAl(BigDecimal tlTutar, BigDecimal gramFiyat, IslemGecmisi gecmis) {
        if (tlTutar.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Hatalı Tutar");
        
        bakiyeKontrol(tlTutar);
        bakiyeAzalt(tlTutar);

        BigDecimal gram = tlTutar.divide(gramFiyat, 4, RoundingMode.HALF_UP);
        altinCuzdan.gramEkle(gram);

        gecmisEkle("[ALTIN] ALIM: " + gram + " GR | -" + tlTutar + " TL");
        
        if (gecmis != null) gecmis.ekle(new AltinAlimIslemi(getHesapNo(), tlTutar, gramFiyat, gram));
    }

    public void paraCek(BigDecimal tutar, IslemGecmisi gecmis) {
        if (tutar.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Hatalı Tutar");
        bakiyeKontrol(tutar);
        bakiyeAzalt(tutar);

        gecmisEkle("[GİDER] ÇIKIŞ | -" + tutar + " TL");

        if (gecmis != null) gecmis.ekle(new ParaCekmeIslemi(getHesapNo(), tutar));
    }

    @Override
    public void paraYatir(BigDecimal tutar) {
        super.paraYatir(tutar);
        gecmisEkle("[GELİR] GİRİŞ | +" + tutar + " TL");
    }

    // --- GEÇMİŞ YÖNETİMİ ---
    
    // PUBLIC yaptık
    public void gecmisEkle(String mesaj) {
        kisiselGecmis.add(mesaj);
    }
    
    public void sonGecmisiSil() {
        if (!kisiselGecmis.isEmpty()) kisiselGecmis.remove(kisiselGecmis.size() - 1);
    }

    public void altinGramAyarla(BigDecimal gram) { altinCuzdan.gramAyarla(gram); }

    public String getIslemGecmisi() {
        if (kisiselGecmis.isEmpty()) return "İşlem Yok.";
        StringBuilder sb = new StringBuilder();
        for (int i = kisiselGecmis.size() - 1; i >= 0; i--) {
            sb.append(kisiselGecmis.get(i)).append("\n--------------------------------\n");
        }
        return sb.toString();
    }

    @Override
    public void aySonuIslemleri(IslemGecmisi gecmis) {
        gecmisEkle("[EKSTRE] TL: " + getBakiye() + " | ALTIN: " + getAltinGram() + " GR");
    }
}