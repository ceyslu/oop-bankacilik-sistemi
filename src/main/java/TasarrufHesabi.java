

public class TasarrufHesabi extends Hesap {
    public TasarrufHesabi(String adSoyad, String hesapNo, double bakiye) {
        super(adSoyad, hesapNo, bakiye);
    }

    @Override
    public void paraCek(double miktar) {
        if (getBakiye() >= miktar) {
            bakiyeAzalt(miktar);
        }
    }
}