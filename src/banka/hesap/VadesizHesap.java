package banka.hesap;

import banka.islem.BilgiIslemi;
import banka.islem.IslemGecmisi;
import banka.islem.TransferIslemi;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class VadesizHesap extends Hesap {

    // Kisisel gecmis listesini buradan SİLDİK, çünkü Hesap.java'dan geliyor.
    private Map<String, BigDecimal> faturalar = new HashMap<>();

    public VadesizHesap(String hesapNo) {
        super(hesapNo);
        // "gecmisEkle" artık Hesap sınıfından miras alındığı için direkt çalışır.
        gecmisEkle("Hesap Oluşturuldu.");
    }

    /* ===================== FATURA SİSTEMİ (KORUNDU) ===================== */
    
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
        
        // Ödeme yap (Mevcut paraCek metodunu kullanır)
        paraCek(tutar, null); 
        
        faturalar.remove(faturaTuru); // Listeden sil
        gecmisEkle("[FATURA ÖDEME] " + faturaTuru + " ödendi.");
    }

    public Map<String, BigDecimal> getFaturalar() {
        return faturalar;
    }

    /* ===================== PARA ÇEKME (KORUNDU) ===================== */
    
    @Override
    public void paraCek(BigDecimal tutar, IslemGecmisi gecmis) {
        // 1. Tutar Kontrolü
        if (tutar == null || tutar.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Tutar pozitif olmalı.");
        }

        // 2. Bakiye Kontrolü
        if (getBakiye().compareTo(tutar) < 0) {
            throw new IllegalArgumentException("Yetersiz Bakiye (Vadesiz)!");
        }

        // 3. Parayı Düş (Hesap sınıfındaki metot)
        bakiyeAzalt(tutar);

        // 4. Kişisel Geçmişe Yaz (Hesap sınıfındaki metot)
        gecmisEkle("[GİDER] Hesaptan Çıkış: -" + tutar + " TL");

        // 5. Global Geçmişe Yaz
        if (gecmis != null) {
            gecmis.ekle(new banka.islem.ParaCekmeIslemi(getHesapNo(), tutar));
        }
    }

    /* ===================== PARA YATIRMA (KORUNDU) ===================== */
    
    @Override
    public void paraYatir(BigDecimal tutar) {
        super.paraYatir(tutar); // Hesap.java'daki paraYatir çalışır (Bakiye artar + Geçmişe eklenir)
        // Ekstra bir şey yapmaya gerek yok, super metot hallediyor.
    }

    /* ===================== TRANSFER (KORUNDU) ===================== */
    // MainFrame bu metodu kullanıyor, o yüzden kesinlikle kalmalı!
    public void transferEt(Hesap alici, BigDecimal tutar, IslemGecmisi gecmis) {
        if (alici == null) throw new IllegalArgumentException("Alıcı hesap boş olamaz.");
        
        paraCek(tutar, gecmis);
        alici.paraYatir(tutar);

        gecmisEkle("[TRANSFER] Giden -> " + alici.getHesapNo() + ": -" + tutar + " TL");

        if (gecmis != null) {
            gecmis.ekle(new TransferIslemi(getHesapNo(), alici.getHesapNo(), tutar));
        }
    }

    /* ===================== EKRANA YAZDIRMA ===================== */
    
    // MainFrame'in geçmişi göstermek için çağırdığı metot
    public String getIslemGecmisi() {
        if (kisiselGecmis.isEmpty()) return "Henüz işlem yok.";
        
        StringBuilder sb = new StringBuilder();
        // En son işlem en üstte görünsün diye tersten yazdırıyoruz
        for (int i = kisiselGecmis.size() - 1; i >= 0; i--) {
            sb.append(kisiselGecmis.get(i)).append("\n");
        }
        return sb.toString();
    }

    @Override
    public void aySonuIslemleri(IslemGecmisi gecmis) {
        gecmisEkle("[BİLGİ] Ay Sonu Bakiyesi: " + getBakiye());
        if(gecmis != null) 
            gecmis.ekle(new BilgiIslemi(getHesapNo(), "Ay Sonu: " + getBakiye()));
    }
}