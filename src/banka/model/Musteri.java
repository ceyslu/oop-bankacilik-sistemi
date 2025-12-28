package banka.model;

import banka.hesap.TasarrufHesabi;
import banka.hesap.VadesizHesap;
import banka.islem.IslemGecmisi;

public class Musteri {
    private final String tc;
    private final String adSoyad;
    private final String sifreHash;

    private final VadesizHesap vadesiz;
    private final TasarrufHesabi tasarruf;

    private final IslemGecmisi vadesizGecmis = new IslemGecmisi();
    private final IslemGecmisi tasarrufGecmis = new IslemGecmisi();

    public Musteri(String tc, String adSoyad, String sifreHash, VadesizHesap vadesiz, TasarrufHesabi tasarruf) {
        this.tc = tc;
        this.adSoyad = adSoyad;
        this.sifreHash = sifreHash;
        this.vadesiz = vadesiz;
        this.tasarruf = tasarruf;
    }

    public String getTc() { return tc; }
    public String getAdSoyad() { return adSoyad; }
    public boolean sifreDogruMu(String sifreHash) { return this.sifreHash.equals(sifreHash); }

    public VadesizHesap getVadesiz() { return vadesiz; }
    public TasarrufHesabi getTasarruf() { return tasarruf; }

    public IslemGecmisi getVadesizGecmis() { return vadesizGecmis; }
    public IslemGecmisi getTasarrufGecmis() { return tasarrufGecmis; }
}
