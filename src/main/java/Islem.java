import java.time.LocalDateTime;

public class Islem {

    private IslemTuru tur;
    private double tutar;
    private LocalDateTime tarih;
    private String hedefHesapNo;

    public Islem(IslemTuru tur, double tutar, String hedefHesapNo) {
        this.tur = tur;
        this.tutar = tutar;
        this.hedefHesapNo = hedefHesapNo;
        this.tarih = LocalDateTime.now();
    }

    public IslemTuru getTur() {
        return tur;
    }

    public double getTutar() {
        return tutar;
    }

    public LocalDateTime getTarih() {
        return tarih;
    }

    public String getHedefHesapNo() {
        return hedefHesapNo;
    }
}

