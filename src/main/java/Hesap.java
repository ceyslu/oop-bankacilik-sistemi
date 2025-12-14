package main.java;





public class Hesap {

    private String hesapNo;
    private double bakiye;

    public Hesap(String hesapNo, double bakiye) {
        this.hesapNo = hesapNo;
        this.bakiye = bakiye;
    }

    public String getHesapNo() {
        return hesapNo;
    }

    public double getBakiye() {
        return bakiye;
    }

    // yatirma ksımı
    public void deposit(double tutar) {
        if (tutar > 0) {
            bakiye += tutar;
        } else {
            System.out.println("Yatırılacak tutar pozitif olmalıdır.");
        }
    }

    // çekem başlanngıcı
    public boolean withdraw(double tutar) {
        if (tutar > 0 && bakiye >= tutar) {
            bakiye -= tutar;
            return true;
        } else {
            System.out.println("Yetersiz bakiye veya gecersiz tutar.");
            return false;
        }
    }
}
