package banka.test;

import banka.hesap.VadesizHesap;
import banka.hesap.TasarrufHesabi;
import org.junit.Test;
import org.junit.Assert;
import java.math.BigDecimal;

public class BankaTest {

    /* ============================================================
       BÖLÜM 1: VADESİZ HESAP TESTLERİ (Business Logic)
       ============================================================ */

    @Test
    public void testVadesizParaYatirma() {
        VadesizHesap vh = new VadesizHesap("V-101");
        vh.paraYatir(new BigDecimal("1250.75"));
        Assert.assertEquals(new BigDecimal("1250.75"), vh.getBakiye());
    }

    @Test
    public void testVadesizCokluIslem() {
        VadesizHesap vh = new VadesizHesap("V-101");
        vh.paraYatir(new BigDecimal("2000"));
        vh.paraCek(new BigDecimal("500"), null);
        vh.paraCek(new BigDecimal("250"), null);
        vh.paraYatir(new BigDecimal("100"));
        // 2000 - 500 - 250 + 100 = 1350
        Assert.assertEquals(new BigDecimal("1350"), vh.getBakiye());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testVadesizYetersizBakiye() {
        VadesizHesap vh = new VadesizHesap("V-101");
        vh.paraYatir(new BigDecimal("100"));
        vh.paraCek(new BigDecimal("101"), null); // Hata fırlatmalı
    }

    @Test(expected = IllegalArgumentException.class)
    public void testVadesizNegatifYatirma() {
        VadesizHesap vh = new VadesizHesap("V-101");
        vh.paraYatir(new BigDecimal("-50")); // Güvenlik kontrolü
    }

    /* ============================================================
       BÖLÜM 2: TASARRUF HESABI TESTLERİ (Altın & Mevduat)
       ============================================================ */

    @Test
    public void testTasarrufTemelBakiye() {
        TasarrufHesabi th = new TasarrufHesabi("T-201");
        th.paraYatir(new BigDecimal("5000"));
        Assert.assertEquals(new BigDecimal("5000"), th.getBakiye());
    }

    @Test
    public void testTasarrufAltinGramYonetimi() {
        TasarrufHesabi th = new TasarrufHesabi("T-201");
        BigDecimal gram = new BigDecimal("15.85");
        th.altinGramAyarla(gram);
        // Altın miktarının doğru set edildiğini doğrula
        Assert.assertEquals(gram, th.getAltinGram());
    }

    @Test
    public void testTasarrufKarmaVarlikYonetimi() {
        TasarrufHesabi th = new TasarrufHesabi("T-201");
        th.paraYatir(new BigDecimal("1000"));
        th.altinGramAyarla(new BigDecimal("5"));
        
        // Hem nakit hem altın aynı anda tutarlı olmalı
        Assert.assertEquals(new BigDecimal("1000"), th.getBakiye());
        Assert.assertEquals(new BigDecimal("5"), th.getAltinGram());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTasarrufNegatifAltinGiris() {
        TasarrufHesabi th = new TasarrufHesabi("T-201");
        th.altinGramAyarla(new BigDecimal("-1.5")); // Validasyon kontrolü
    }

    /* ============================================================
       BÖLÜM 3: SINIR DEĞER VE KİMLİK TESTLERİ
       ============================================================ */

    @Test
    public void testHesapNoAtama() {
        VadesizHesap vh = new VadesizHesap("TR-TEST-01");
        Assert.assertEquals("TR-TEST-01", vh.getHesapNo());
    }

    @Test
    public void testBaslangicDegerleri() {
        TasarrufHesabi th = new TasarrufHesabi("T-NEW");
        // Yeni hesapta her şey sıfır olmalı
        Assert.assertEquals(BigDecimal.ZERO, th.getBakiye());
        Assert.assertEquals(BigDecimal.ZERO, th.getAltinGram());
    }
}