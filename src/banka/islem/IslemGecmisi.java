package banka.islem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IslemGecmisi {
    private final List<Islem> islemler = new ArrayList<>();

    public void ekle(Islem islem) {
        islemler.add(islem);
    }

    public List<Islem> tumu() {
        return Collections.unmodifiableList(islemler);
    }
}
