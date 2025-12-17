

public class VadesizHesap extends Hesap implements ITransferable, IKrediKullanabilir {
    private double krediBorcu = 0;

    public VadesizHesap(String adSoyad, String hesapNo, double bakiye) {
        super(adSoyad, hesapNo, bakiye);
    }

    @Override
    public void paraCek(double miktar) {
        if (getBakiye() >= miktar) {
            bakiyeAzalt(miktar);
        }
    }

    // Fatura Ödeme Özelliği (Sen istedin)
    public boolean faturaOde(String faturaAdi, double tutar) {
        if (getBakiye() >= tutar) {
            bakiyeAzalt(tutar);
            System.out.println(faturaAdi + " ödendi.");
            return true;
        }
        return false;
    }
1
    @Override
    public boolean transferYap(Hesap aliciHesap, double miktar) {
        if (getBakiye() >= miktar) {
            this.bakiyeAzalt(miktar);
            aliciHesap.bakiyeEkle(miktar);
            return true;
        }
        return false;
    }

    @Override
    public boolean krediBasvurusuYap(double miktar) {
        if (getBakiye() > 1000) { // Kural: 1000 TL üstü parası olana kredi ver
            this.krediBorcu += miktar;
            bakiyeEkle(miktar);
            return true;
        }
        return false;
    }

    @Override
    public void krediTaksitiOde(double miktar) {
        if (getBakiye() >= miktar && krediBorcu >= miktar) {
            bakiyeAzalt(miktar);
            krediBorcu -= miktar;
        }
    }
}