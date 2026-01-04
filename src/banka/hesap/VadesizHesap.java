package banka.hesap;

import banka.islem.BilgiIslemi;
import banka.islem.IslemGecmisi;
import banka.islem.TransferIslemi;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VadesizHesap extends Hesap {

    // --- YENİ EKLENENLER: Fatura Listesi ve Kişisel Geçmiş ---
    private Map<String, BigDecimal> faturalar = new HashMap<>();
    private List<String> kisiselGecmis = new ArrayList<>();

    public VadesizHesap(String hesapNo) {
        super(hesapNo);
        // Hesap açıldığı anı not alalım
        gecmisEkle("Hesap Oluşturuldu.");
    }

    /* ===================== YENİ: FATURA SİSTEMİ ===================== */
    
    public void faturaKaydet(String faturaTuru, BigDecimal miktar) {
        if (miktar.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Tutar pozitif olmalı.");
        }
        faturalar.put(faturaTuru, miktar);
        gecmisEkle("[FATURA KAYIT] " + faturaTuru + ": " + miktar + " TL");
    }

    public void faturaOde(String faturaTuru) {
        if (!faturalar.containsKey(faturaTuru)) {
            throw new IllegalArgumentException("Kayıtlı fatura bulunamadı.");
        }
        BigDecimal tutar = faturalar.get(faturaTuru);
        
        // Ödeme yap (kendi paraCek metodumuzu kullanıyoruz)
        paraCek(tutar, null); 
        
        faturalar.remove(faturaTuru); // Listeden sil
        gecmisEkle("[FATURA ÖDEME] " + faturaTuru + " ödendi.");
    }

    public Map<String, BigDecimal> getFaturalar() {
        return faturalar;
    }

    /* ===================== MEVCUT: PARA ÇEKME (GÜNCELLENDİ) ===================== */
    
    
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

        // 4. --- YENİ --- Kişisel Geçmişe Yaz (Geldi/Gitti şeklinde)
        gecmisEkle("[GİDER] Hesaptan Çıkış: -" + tutar + " TL");

        // 5. Global Geçmişe Yaz (Eski sistem çalışmaya devam etsin)
        if (gecmis != null) {
            gecmis.ekle(new BilgiIslemi(getHesapNo(), "Para Çekme: " + tutar + " TL"));
        }
    }

    /* ===================== MEVCUT: PARA YATIRMA (GÜNCELLENDİ) ===================== */
    
    @Override
    public void paraYatir(BigDecimal tutar) {
        super.paraYatir(tutar);
        // Para yatınca da geçmişe yazsın (Özellikle 1000 TL bonus için önemli)
        gecmisEkle("[GELİR] Hesaba Giriş: +" + tutar + " TL");
    }

    /* ===================== TRANSFER (AYNEN KORUNDU) ===================== */
    
    public void transferEt(Hesap alici, BigDecimal tutar, IslemGecmisi gecmis) {
        if (alici == null) throw new IllegalArgumentException("Alıcı hesap boş olamaz.");
        
        paraCek(tutar, gecmis);
        alici.paraYatir(tutar);

        gecmisEkle("[TRANSFER] Giden -> " + alici.getHesapNo() + ": -" + tutar + " TL");

        if (gecmis != null) {
            gecmis.ekle(new TransferIslemi(getHesapNo(), alici.getHesapNo(), tutar));
        }
    }

    /* ===================== YENİ: GEÇMİŞİ METİN OLARAK ALMA ===================== */
    // MainFrame'de hata veren kısım burasıydı, şimdi ekliyoruz.
    
    public String getIslemGecmisi() {
        if (kisiselGecmis.isEmpty()) return "Henüz işlem yok.";
        
        StringBuilder sb = new StringBuilder();
        // En son işlem en üstte görünsün diye tersten yazdırıyoruz
        for (int i = kisiselGecmis.size() - 1; i >= 0; i--) {
            sb.append(kisiselGecmis.get(i)).append("\n");
        }
        return sb.toString();
    }
    
    // Yardımcı metot: Listeye ekleme yapar
    private void gecmisEkle(String mesaj) {
        kisiselGecmis.add(mesaj);
    }

    @Override
    public void aySonuIslemleri(IslemGecmisi gecmis) {
        gecmisEkle("[BİLGİ] Ay Sonu Bakiyesi: " + getBakiye());
        if(gecmis != null) 
            gecmis.ekle(new BilgiIslemi(getHesapNo(), "Ay Sonu: " + getBakiye()));
    }
}