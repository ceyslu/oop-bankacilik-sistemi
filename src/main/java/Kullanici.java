public class Kullanici {

    private String tc;
    private String sifre;
    private List<Hesap> hesaplar;

    public Kullanici(String tc, String sifre) {
        this.tc = tc;
        this.sifre = sifre;
        this.hesaplar = new ArrayList<>();
    }

    public String getTc() {
        return tc;
    }

    public String getSifre() {
        return sifre;
    }

    public List<Hesap> getHesaplar() {
        return hesaplar;
    }

    public void hesapEkle(Hesap hesap) {
        hesaplar.add(hesap);
    }
}
