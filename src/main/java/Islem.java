

import java.time.LocalDateTime;

public class Islem {
    private String islemTipi;
    private double miktar;
    private LocalDateTime tarih;

    public Islem(String islemTipi, double miktar) {
        this.islemTipi = islemTipi;
        this.miktar = miktar;
        this.tarih = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return tarih.getHour() + ":" + tarih.getMinute() + " - " + islemTipi + ": " + miktar + " TL";
    }
}
