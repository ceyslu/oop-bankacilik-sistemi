package banka.islem;

import java.time.LocalDateTime;

public abstract class Islem {
    private final LocalDateTime zaman = LocalDateTime.now();
    private final String aciklama;

    protected Islem(String aciklama) {
        this.aciklama = aciklama;
    }

    public LocalDateTime getZaman() { return zaman; }
    public String getAciklama() { return aciklama; }

    public abstract String ozet();
}
