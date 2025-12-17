
import java.util.ArrayList;
import java.util.List;

public class Musteri {
    private String adSoyad;
    private String tcNo; // Kullanıcı adı yerine TC kullanalım
    private String sifre;
    
    // Müşterinin sahip olduğu hesaplar listesi (Cüzdanı gibi düşün)
    private List<Hesap> hesaplar;

    public Musteri(String adSoyad, String tcNo, String sifre) {
        this.adSoyad = adSoyad;
        this.tcNo = tcNo;
        this.sifre = sifre;
        this.hesaplar = new ArrayList<>();
    }

    // Hesabı müşteriye ekleme metodu
    public void hesapEkle(Hesap hesap) {
        hesaplar.add(hesap);
    }

    public List<Hesap> getHesaplar() { return hesaplar; }
    public String getTcNo() { return tcNo; }
    public String getSifre() { return sifre; }
    public String getAdSoyad() { return adSoyad; }
}