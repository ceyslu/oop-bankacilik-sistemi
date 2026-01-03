package banka.hesap;

import banka.islem.BilgiIslemi;
import banka.islem.IslemGecmisi;
import banka.islem.TransferIslemi;
import java.math.BigDecimal;

public class VadesizHesap extends Hesap {

    public VadesizHesap(String hesapNo) {
        super(hesapNo);
    }

    /* ===================== PARA ÇEKME (Zorunlu) ===================== */
    // Banka sınıfı transfer yaparken veya altın alırken bu metodu kullanır.
    
    
    public void paraCek(BigDecimal tutar, IslemGecmisi gecmis) {
        // 1. Tutar Kontrolü
        if (tutar == null || tutar.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Tutar pozitif olmalı.");
        }

        // 2. Bakiye Kontrolü
        if (getBakiye().compareTo(tutar) < 0) {
            throw new IllegalArgumentException("Yetersiz Bakiye (Vadesiz)!");
        }

        // 3. Parayı Düş
        bakiyeAzalt(tutar);

        // 4. Geçmişe İşle
        if (gecmis != null) {
            gecmis.ekle(new BilgiIslemi(
                getHesapNo(), 
                "Hesaptan Çıkış: " + tutar + " TL"
            ));
        }
    }

    /* ===================== TRANSFER (Opsiyonel) ===================== */
    // Eğer sadece VadesizHesap üzerinden transfer çağrılırsa diye bırakıyoruz.
    // Ancak Banka sınıfı genellikle manuel yapıyor.
    public void transferEt(Hesap alici, BigDecimal tutar, IslemGecmisi gecmis) {
        if (alici == null) {
            throw new IllegalArgumentException("Alıcı hesap boş olamaz.");
        }
        
        // Kendi paraCek metodumuzu kullanarak bakiyeyi düşüyoruz
        paraCek(tutar, gecmis);

        // Alıcıya ekle
        alici.paraYatir(tutar);

        // Özel transfer logu
        gecmis.ekle(
                new TransferIslemi(
                        getHesapNo(),
                        alici.getHesapNo(),
                        tutar
                )
        );
    }

    /* ===================== AY SONU ===================== */

    @Override
    public void aySonuIslemleri(IslemGecmisi gecmis) {
        // Vadesiz hesapta ay sonu için ekstra bir işlem yok (faiz vb.)
        // Sadece bilgi logu atabiliriz
        gecmis.ekle(new BilgiIslemi(getHesapNo(), "Ay Sonu Özeti: " + getBakiye() + " TL"));
    }

    public Object getIslemGecmisi() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}