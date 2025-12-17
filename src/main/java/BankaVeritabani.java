

import java.util.ArrayList;
import java.util.List;

public class BankaVeritabani {
    // static yaptık ki her yerden aynı listeye ulaşalım (program kapanana kadar silinmez)
    public static List<Musteri> musteriler = new ArrayList<>();
    
    // Şu an sisteme giriş yapmış olan müşteriyi burada tutacağız
    public static Musteri aktifKullanici = null;

    // Test için başlangıçta sahte bir veri ekleyelim
    static {
        Musteri ornekMusteri = new Musteri("Ahmet Yılmaz", "123", "pass");
        // Ahmet'e bir vadesiz hesap açalım
        ornekMusteri.hesapEkle(new VadesizHesap("Ahmet Yılmaz", "TR01", 5000));
        musteriler.add(ornekMusteri);
    }
}