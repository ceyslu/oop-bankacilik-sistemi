public class VadesizHesap extends Hesap {

    public VadesizHesap(String hesapNo, double bakiye) {
        super(hesapNo, bakiye);
    }

    @Override
    public boolean withdraw(double tutar) {
        if (tutar > 0 && getBakiye() >= tutar) {
            return super.withdraw(tutar);
        }
        System.out.println("Vadesiz hesapta yetersiz bakiye.");
        return false;
    }
}
