package banka.service;

import banka.hesap.TasarrufHesabi;
import banka.hesap.VadesizHesap;
import banka.islem.BilgiIslemi;
import banka.model.Musteri;
import banka.util.MetinUtil;
import banka.util.Sifreleme;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Banka {

    private static final String DOSYA = "data/musteriler.csv";

    private final Map<String, Musteri> tcIle = new HashMap<>();
    private final Map<String, Musteri> adSoyadIle = new HashMap<>();
    private final Map<String, Musteri> hesapNoIle = new HashMap<>();

    private int vadesizNo = 100000;
    private int tasarrufNo = 200000;

    public Banka() {
        dosyadanYukle();
    }

    // ===========================
    // ÜYE OL / GİRİŞ
    // ===========================

    public Musteri uyeOl(String tc, String adSoyad, String sifre) {
        tc = MetinUtil.sadeceRakam(tc);
        adSoyad = MetinUtil.titleCase(adSoyad);

        if (tc.length() != 11)
            throw new IllegalArgumentException("TC 11 haneli olmali.");
        if (adSoyad.isBlank() || adSoyad.split(" ").length < 2)
            throw new IllegalArgumentException("Ad Soyad en az 2 kelime olmali.");
        MetinUtil.minUzunluk(sifre, 6, "Sifre");

        if (tcIle.containsKey(tc))
            throw new IllegalArgumentException("Bu TC zaten kayitli.");
        if (adSoyadIle.containsKey(adSoyad))
            throw new IllegalArgumentException("Bu Ad Soyad zaten kayitli.");

        VadesizHesap v = new VadesizHesap(String.valueOf(++vadesizNo));
        TasarrufHesabi t = new TasarrufHesabi(String.valueOf(++tasarrufNo));

        // Yeni üye bonusu: vadesize 1000 TL
        v.paraYatir(new BigDecimal("1000"));

        String sifreHash = Sifreleme.sha256(sifre);

        Musteri m = new Musteri(tc, adSoyad, sifreHash, v, t);

        tcIle.put(tc, m);
        adSoyadIle.put(adSoyad, m);
        hesapNoIle.put(v.getHesapNo(), m);
        hesapNoIle.put(t.getHesapNo(), m);

        dosyayaKaydet();
        return m;
    }

    public Musteri girisYap(String adSoyad, String sifre) {
        adSoyad = MetinUtil.titleCase(adSoyad);
        MetinUtil.minUzunluk(sifre, 6, "Sifre");

        Musteri m = adSoyadIle.get(adSoyad);
        if (m == null)
            throw new IllegalArgumentException("Kullanici bulunamadi.");

        String hash = Sifreleme.sha256(sifre);
        if (!m.sifreDogruMu(hash))
            throw new IllegalArgumentException("Sifre hatali.");

        return m;
    }

    // ===========================
    // DIŞ TRANSFER (SADECE VADESİZ)
    // ücret/komisyonu sonra ekleyeceğiz
    // ===========================

    public void transferYap(Musteri gonderen, String aliciHesapNo, String aliciAdSoyad, BigDecimal tutar) {
        aliciHesapNo = temizle(aliciHesapNo);
        aliciAdSoyad = MetinUtil.titleCase(aliciAdSoyad);

        if (aliciHesapNo.isEmpty() || aliciAdSoyad.isEmpty())
            throw new IllegalArgumentException("Alici hesap no ve ad soyad bos olamaz.");
        if (tutar == null || tutar.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Tutar pozitif olmali.");

        Musteri alan = hesapNoIle.get(aliciHesapNo);
        if (alan == null)
            throw new IllegalArgumentException("Alici hesap bulunamadi.");

        if (!alan.getAdSoyad().equals(aliciAdSoyad))
            throw new IllegalArgumentException("Alici ad soyad eslesmiyor.");

        // Transfer sadece alıcının Vadesiz hesabına
        if (!alan.getVadesiz().getHesapNo().equals(aliciHesapNo))
            throw new IllegalArgumentException("Transfer sadece Vadesiz hesaplara yapilabilir.");

        // gonderen vadesiz -> alan vadesiz
        gonderen.getVadesiz().transferEt(alan.getVadesiz(), tutar, gonderen.getVadesizGecmis());

        // alıcıya "gelen" kayıt
        alan.getVadesizGecmis().ekle(new BilgiIslemi(
                alan.getVadesiz().getHesapNo(),
                "TRANSFER GELDI: " + gonderen.getAdSoyad() + " (" + gonderen.getVadesiz().getHesapNo() + ") -> " +
                        alan.getVadesiz().getHesapNo() + " | " + tutar + " TL"));

        dosyayaKaydet();
    }

    // ===========================
    // ALTIN AL (Tasarruf hesabı içinden çağırıyoruz)
    // ===========================

    public void altinAl(Musteri musteri, BigDecimal tlTutar, BigDecimal gramFiyat) {
        // TasarrufHesabi zaten her şeyi yapıyor:
        // - TL düşüyor
        // - altın gram artıyor
        // - AltinAlimIslemi geçmişe ekleniyor
        musteri.getTasarruf().altinAl(tlTutar, gramFiyat, musteri.getTasarrufGecmis());
        dosyayaKaydet();
    }

    // ===========================
    // KENDİ HESAPLARIM ARASI TRANSFER (ÜCRETSİZ)
    // (şimdilik basit)
    // ===========================
    public void kendiHesaplarimArasiTransfer(Musteri m, boolean vadesizdenTasarrufa, BigDecimal tutar) {
        if (tutar == null || tutar.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Tutar pozitif olmali.");

        if (vadesizdenTasarrufa) {
            m.getVadesiz().transferEt(m.getTasarruf(), tutar, m.getVadesizGecmis());
            m.getTasarrufGecmis().ekle(new BilgiIslemi(
                    m.getTasarruf().getHesapNo(),
                    "TRANSFER GELDI (Kendi): " + m.getVadesiz().getHesapNo() + " -> " +
                            m.getTasarruf().getHesapNo() + " | " + tutar + " TL"));
        } else {
            // Tasarruf -> Vadesiz (Tasarruf paraCek imzası: (tutar, gecmis))
            m.getTasarruf().paraCek(tutar, m.getTasarrufGecmis());
            m.getVadesiz().paraYatir(tutar);

            m.getVadesizGecmis().ekle(new BilgiIslemi(
                    m.getVadesiz().getHesapNo(),
                    "TRANSFER GELDI (Kendi): " + m.getTasarruf().getHesapNo() + " -> " +
                            m.getVadesiz().getHesapNo() + " | " + tutar + " TL"));
        }

        dosyayaKaydet();
    }

    // ===========================
    // DOSYA
    // Format:
    // tc;adSoyad;sifreHash;vNo;vBakiye;tNo;tBakiye;altinGram
    // ===========================

    private void dosyadanYukle() {
        File f = new File(DOSYA);
        if (!f.exists())
            return;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;

                String[] p = line.split(";");
                if (p.length != 8)
                    continue;

                String tc = p[0];
                String adSoyad = p[1];
                String sifreHash = p[2];

                String vNo = p[3];
                BigDecimal vBakiye = new BigDecimal(p[4]);

                String tNo = p[5];
                BigDecimal tBakiye = new BigDecimal(p[6]);

                BigDecimal altinGram = new BigDecimal(p[7]);

                VadesizHesap v = new VadesizHesap(vNo);
                TasarrufHesabi t = new TasarrufHesabi(tNo);

                if (vBakiye.compareTo(BigDecimal.ZERO) > 0)
                    v.paraYatir(vBakiye);
                if (tBakiye.compareTo(BigDecimal.ZERO) > 0)
                    t.paraYatir(tBakiye);

                // TasarrufHesabi içinde setter var: altinGramAyarla(...)
                if (altinGram.compareTo(BigDecimal.ZERO) > 0) {
                    t.altinGramAyarla(altinGram);
                }

                Musteri m = new Musteri(tc, adSoyad, sifreHash, v, t);

                tcIle.put(tc, m);
                adSoyadIle.put(adSoyad, m);
                hesapNoIle.put(v.getHesapNo(), m);
                hesapNoIle.put(t.getHesapNo(), m);

                vadesizNo = Math.max(vadesizNo, sayiyaCevir(vNo, 100000));
                tasarrufNo = Math.max(tasarrufNo, sayiyaCevir(tNo, 200000));
            }
        } catch (Exception e) {
            System.out.println("Dosyadan yukleme hatasi: " + e.getMessage());
        }
    }

    private void dosyayaKaydet() {
        File f = new File(DOSYA);
        if (f.getParentFile() != null)
            f.getParentFile().mkdirs();

        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(f, false), StandardCharsets.UTF_8))) {
            for (Musteri m : tcIle.values()) {

                BigDecimal altinGram = m.getTasarruf().getAltinGram();

                String line = String.join(";",
                        m.getTc(),
                        m.getAdSoyad(),
                        m.getSifreHash(),
                        m.getVadesiz().getHesapNo(),
                        m.getVadesiz().getBakiye().toPlainString(),
                        m.getTasarruf().getHesapNo(),
                        m.getTasarruf().getBakiye().toPlainString(),
                        altinGram.toPlainString());

                bw.write(line);
                bw.newLine();
            }
        } catch (Exception e) {
            System.out.println("Dosyaya kaydetme hatasi: " + e.getMessage());
        }
    }

    private int sayiyaCevir(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }

    private String temizle(String s) {
        return s == null ? "" : s.trim();
    }
}
