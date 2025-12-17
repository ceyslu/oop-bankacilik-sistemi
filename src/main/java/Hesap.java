

import java.util.ArrayList;
import java.util.List;

public abstract class Hesap {
    private String adSoyad;
    private String hesapNo;
    private double bakiye;
    private List<Islem> islemGecmisi;

    public Hesap(String adSoyad, String hesapNo, double bakiye) {
        this.adSoyad = adSoyad;
        this.hesapNo = hesapNo;
        this.bakiye = bakiye;
        this.islemGecmisi = new ArrayList<>();
    }

    public String getAdSoyad() { return adSoyad; }
    public String getHesapNo() { return hesapNo; }
    public double getBakiye() { return bakiye; }
    public List<Islem> getIslemGecmisi() { return islemGecmisi; }

    public void bakiyeEkle(double miktar) {
        if(miktar > 0) {
            this.bakiye += miktar;
            this.islemGecmisi.add(new Islem("Para Yatırma", miktar));
        }
    }

    public void bakiyeAzalt(double miktar) {
        if (this.bakiye >= miktar) {
            this.bakiye -= miktar;
            this.islemGecmisi.add(new Islem("Para Çekme/Ödeme", miktar));
        }
    }

    public abstract void paraCek(double miktar);
}