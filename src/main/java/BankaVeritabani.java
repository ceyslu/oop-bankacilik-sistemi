import java.util.ArrayList;
import java.util.List;

public class BankaVeritabani {

    private static List<Musteri> musteriler = new ArrayList<>();

    // ÜYE OL
    public static boolean register(String tc, String sifre) {

        for (Musteri m : musteriler) {
            if (m.getTc().equals(tc)) {
                return false; // aynı TC var
            }
        }

        Musteri yeni = new Musteri(tc, sifre);

        // Başlangıç hesapları
        yeni.hesapEkle(new VadesizHesap("V-" + tc, 1000));
        yeni.hesapEkle(new TasarrufHesabi("T-" + tc, 0));

        musteriler.add(yeni);
        return true;
    }

    // GİRİŞ
    public static Musteri login(String tc, String sifre) {
        for (Musteri m : musteriler) {
            if (m.getTc().equals(tc) && m.getSifre().equals(sifre)) {
                return m;
            }
        }
        return null;
    }

    // KULLANICI BUL
    public static Musteri findByTc(String tc) {
        for (Musteri m : musteriler) {
            if (m.getTc().equals(tc)) {
                return m;
            }
        }
        return null;
    }

    // KULLANICILAR ARASI TRANSFER
    public static boolean transfer(
            Musteri gonderen,
            Hesap gonderenHesap,
            String aliciTc,
            double miktar) {

        Musteri alici = findByTc(aliciTc);
        if (alici == null) return false;
        if (gonderenHesap.getBakiye() < miktar) return false;

        Hesap aliciHesap = alici.getHesaplar().get(0);

        gonderenHesap.withdraw(miktar);
        aliciHesap.deposit(miktar);

        return true;
    }
}
