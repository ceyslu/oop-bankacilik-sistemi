
import java.util.ArrayList;
import java.util.List;

public abstract class Hesap implements ITransferable {

    protected String hesapNo;
    protected double bakiye;
    protected List<Islem> islemler;

    public Hesap(String hesapNo, double bakiye) {
        this.hesapNo = hesapNo;
        this.bakiye = bakiye;
        this.islemler = new ArrayList<>();
    }

    public double getBakiye() {
        return bakiye;
    }

    public String getHesapNo() {
        return hesapNo;
    }

    public List<Islem> getIslemler() {
        return islemler;
    }

    public void deposit(double miktar) {
        bakiye += miktar;
        islemler.add(new Islem(
                IslemTuru.PARA_YATIRMA,
                miktar,
                "Para yatırıldı"
        ));
    }

    public abstract void withdraw(double miktar);

    @Override
    public void transfer(Hesap hedef, double miktar) {
        this.withdraw(miktar);
        hedef.deposit(miktar);
    }
}
