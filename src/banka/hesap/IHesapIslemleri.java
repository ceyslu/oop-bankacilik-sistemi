package banka.hesap;

import banka.islem.IslemGecmisi;
import java.math.BigDecimal;



// Bu dosya, hesapların uyması gereken kuralları belirler (Interface)
public interface IHesapIslemleri {
    
    // Kural 1: Her hesap para yatırmayı bilmek zorundadır
    void paraYatir(BigDecimal tutar);

    // Kural 2: Her hesap para çekmeyi bilmek zorundadır
    void paraCek(BigDecimal tutar, IslemGecmisi gecmis);




    

}
