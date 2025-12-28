package banka.model;

public class Musteri {
    private final String tc;
    private final String adSoyad;
    private final String sifreHash;

    public Musteri(String tc, String adSoyad, String sifreHash) {
        this.tc = tc;
        this.adSoyad = adSoyad;
        this.sifreHash = sifreHash;
    }

    public String getTc() { return tc; }
    public String getAdSoyad() { return adSoyad; }

    public boolean sifreDogruMu(String sifreHash) {
        return this.sifreHash.equals(sifreHash);
    }
}
