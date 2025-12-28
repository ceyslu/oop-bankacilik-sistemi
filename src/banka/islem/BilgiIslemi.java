package banka.islem;

public class BilgiIslemi extends Islem {
    private final String hesapNo;
    private final String mesaj;

    public BilgiIslemi(String hesapNo, String mesaj) {
        super("Bilgi");
        this.hesapNo = hesapNo;
        this.mesaj = mesaj;
    }

    @Override
    public String ozet() {
        return getZaman() + " | " + hesapNo + " | " + mesaj;
    }
}

