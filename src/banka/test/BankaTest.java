package banka.test;

import banka.hesap.VadesizHesap;
import org.junit.Test;
import org.junit.Assert;
import java.math.BigDecimal;

public class BankaTest {

    // SENARYO 1: Temel Para Yatırma ve Bakiye Kontrolü
    @Test
    public void testParaYatirma() {
        VadesizHesap hesap = new VadesizHesap("123");
        hesap.paraYatir(new BigDecimal("250.50"));
        Assert.assertEquals(new BigDecimal("250.50"), hesap.getBakiye());
    }

    // SENARYO 2: Ardışık İşlemler (Yatırma + Çekme)
    @Test
    public void testCokluIslem() {
        VadesizHesap hesap = new VadesizHesap("123");
        hesap.paraYatir(new BigDecimal("1000"));
        hesap.paraCek(new BigDecimal("300"), null);
        hesap.paraCek(new BigDecimal("200"), null);
        // Beklenen: 1000 - 300 - 200 = 500
        Assert.assertEquals(new BigDecimal("500"), hesap.getBakiye());
    }

    // SENARYO 3: Yetersiz Bakiye Durumu (Hata Bekleniyor)
    @Test(expected = IllegalArgumentException.class)
    public void testYetersizBakiyeHatasi() {
        VadesizHesap hesap = new VadesizHesap("123");
        hesap.paraYatir(new BigDecimal("50"));
        // 50 TL varken 60 TL çekmeye çalışmak hata fırlatmalı
        hesap.paraCek(new BigDecimal("60"), null);
    }

    // SENARYO 4: Negatif Tutar Yatırma Kontrolü (Hata Bekleniyor)
    @Test(expected = IllegalArgumentException.class)
    public void testNegatifParaYatirma() {
        VadesizHesap hesap = new VadesizHesap("123");
        // Eksi para yatırılamamalı
        hesap.paraYatir(new BigDecimal("-100"));
    }

    // SENARYO 5: Bakiyeyi Tam Sıfırlama
    @Test
    public void testTamBakiyeCekme() {
        VadesizHesap hesap = new VadesizHesap("123");
        hesap.paraYatir(new BigDecimal("150"));
        hesap.paraCek(new BigDecimal("150"), null);
        Assert.assertEquals(BigDecimal.ZERO, hesap.getBakiye());
    }
}