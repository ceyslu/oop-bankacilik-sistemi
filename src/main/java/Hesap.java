import java.util.ArrayList;
import java.util.List;

public class Hesap {

    private String hesapNo;
    private double bakiye;
    private List<Islem> islemGecmisi;

    public Hesap(String hesapNo, double bakiye) {
        this.hesapNo = hesapNo;
        this.bakiye = bakiye;
        this.islemGecmisi = new ArrayList<>();
    }

    public String getHesapNo() {
        return hesapNo;
    }

    public double getBakiye() {
        return bakiye;
    }

    public List<Islem> getIslemGecmisi() {
        return islemGecmisi;
    }

    public void deposit(double tutar) {
        if (tutar > 0) {
            bakiye += tutar;
            islemGecmisi.add(new Islem(IslemTuru.DEPOSIT, tutar, null));
        } else {
            System.out.println("Yatırılacak tutar pozitif olmalıdır.");
        }
    }

    public boolean withdraw(double tutar) {
        if (tutar > 0 && bakiye >= tutar) {
            bakiye -= tutar;
            islemGecmisi.add(new Islem(IslemTuru.WITHDRAW, tutar, null));
            return true;
        }
        return false;
    }
}
