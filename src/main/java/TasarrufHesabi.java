public class TasarrufHesabi extends Hesap {

    private static final double MIN_BAKIYE = 500;

    public TasarrufHesabi(String hesapNo, double bakiye) {
        super(hesapNo, bakiye);
    }

    @Override
    public boolean withdraw(double tutar) {
        if (tutar > 0 && getBakiye() - tutar >= MIN_BAKIYE) {
            return super.withdraw(tutar);
        }
        System.out.println("Tasarruf hesabında minimum bakiye 500 TL olmalıdır.");
        return false;
    }
}
