import java.time.LocalDateTime;

public class Islem {

    private IslemTuru tur;
    private double miktar;
    private LocalDateTime tarih;
    private String aciklama;

    public Islem(IslemTuru tur, double miktar, String aciklama) {
        this.tur = tur;
        this.miktar = miktar;
        this.aciklama = aciklama;
        this.tarih = LocalDateTime.now();
    }

    public IslemTuru getTur() {
        return tur;
    }

    public double getMiktar() {
        return miktar;
    }

    public LocalDateTime getTarih() {
        return tarih;
    }

    public String getAciklama() {
        return aciklama;
    }
}
