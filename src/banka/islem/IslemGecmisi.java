package banka.islem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IslemGecmisi {
    private final List<Islem> islemler = new ArrayList<>();

    public void ekle(Islem islem) {
        if (islem == null) return;
        islemler.add(islem);
    }

    public List<Islem> tumunuGetir() {
        return Collections.unmodifiableList(islemler);
    }

    public List<Islem> hesapIslemleri(String hesapNo) {
        List<Islem> sonuc = new ArrayList<>();
        for (Islem i : islemler) {
            if (i.getHesapNo().equals(hesapNo)) {
                sonuc.add(i);
            }
        }
        return sonuc;
    }

    public void temizle() {
        islemler.clear();
    }
}

