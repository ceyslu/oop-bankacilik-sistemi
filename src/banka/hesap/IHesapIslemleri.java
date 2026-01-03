package banka.hesap;

import banka.islem.IslemGecmisi;
import java.math.BigDecimal;

// Rapordaki "Interface" maddesini doldurmak için gerekli
public interface IHesapIslemleri {
    void paraYatir(BigDecimal tutar);
    void paraCek(BigDecimal tutar, IslemGecmisi gecmis);
}