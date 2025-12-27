public class TasarrufHesabi extends Hesap {

    public TasarrufHesabi(String hesapNo, double bakiye) {
        super(hesapNo, bakiye);
    }

    @Override
    public void withdraw(double miktar) {
        if (miktar > bakiye) {
            throw new RuntimeException("Yetersiz bakiye");
        }

        bakiye -= miktar;
        islemler.add(new Islem(
                IslemTuru.PARA_CEKME,
                miktar,
                "Tasarruf hesabından çekildi"
        ));
    }
}
