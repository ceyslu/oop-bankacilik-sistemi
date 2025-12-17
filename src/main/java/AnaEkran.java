import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AnaEkran extends JFrame {
    private Musteri musteri;
    private JLabel bakiyeLabel;

    public AnaEkran(Musteri musteri) {
        this.musteri = musteri;
        
        setTitle("Bankacılık İşlemleri - " + musteri.getAdSoyad());
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 1)); // 5 Satırlı bir düzen

        // 1. BİLGİ SATIRI
        bakiyeLabel = new JLabel("Hesaplarınız Yükleniyor...");
        bakiyeGuncelle(); // Bakiyeyi ekrana yaz
        bakiyeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bakiyeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(bakiyeLabel);

        // 2. BUTON: PARA TRANSFERİ
        JButton transferBtn = new JButton("PARA TRANSFERİ YAP");
        add(transferBtn);

        // 3. BUTON: FATURA ÖDE
        JButton faturaBtn = new JButton("FATURA ÖDE");
        add(faturaBtn);

        // 4. BUTON: ALTIN / DÖVİZ AL
        JButton yatirimBtn = new JButton("ALTIN / DÖVİZ AL");
        add(yatirimBtn);
        
        // 5. BUTON: HESAP HAREKETLERİ
        JButton gecmisBtn = new JButton("HESAP HAREKETLERİ");
        add(gecmisBtn);

        // --- BUTON OLAYLARI ---

        // Transfer Butonu
        transferBtn.addActionListener(e -> {
            String hedefAd = JOptionPane.showInputDialog("Para göndereceğiniz kişinin Adı Soyadı:");
            String miktarStr = JOptionPane.showInputDialog("Gönderilecek Tutar (TL):");
            
            if (hedefAd != null && miktarStr != null) {
                double miktar = Double.parseDouble(miktarStr);
                boolean bulundu = false;
                
                // Bankadaki herkesi ara
                for (Musteri m : BankaVeritabani.musteriler) {
                    if (m.getAdSoyad().equalsIgnoreCase(hedefAd)) {
                        // Basitlik olsun diye ilk hesaplar arası transfer yapıyoruz
                        Hesap benimHesap = musteri.getHesaplar().get(0);
                        Hesap onunHesap = m.getHesaplar().get(0);
                        
                        // VadesizHesap sınıfındaki transfer metodunu kullan
                        if (benimHesap instanceof VadesizHesap) {
                            boolean sonuc = ((VadesizHesap) benimHesap).transferYap(onunHesap, miktar);
                            if (sonuc) {
                                JOptionPane.showMessageDialog(null, "Transfer Başarılı!");
                                bakiyeGuncelle();
                            } else {
                                JOptionPane.showMessageDialog(null, "Yetersiz Bakiye!");
                            }
                        }
                        bulundu = true;
                        break;
                    }
                }
                if (!bulundu) JOptionPane.showMessageDialog(null, "Kullanıcı bulunamadı!");
            }
        });

        // Fatura Butonu
        faturaBtn.addActionListener(e -> {
            String miktarStr = JOptionPane.showInputDialog("Fatura Tutarı:");
            if (miktarStr != null) {
                double miktar = Double.parseDouble(miktarStr);
                Hesap h = musteri.getHesaplar().get(0); // İlk hesabı kullan
                
                if (h.getBakiye() >= miktar) {
                    h.bakiyeAzalt(miktar);
                    JOptionPane.showMessageDialog(null, "Fatura Ödendi.");
                    bakiyeGuncelle();
                } else {
                    JOptionPane.showMessageDialog(null, "Bakiye Yetersiz!");
                }
            }
        });

        // Altın Al Butonu
        yatirimBtn.addActionListener(e -> {
            // Müşterinin 2. hesabı varsa ve bu Yatırım Hesabıysa işlem yap
            if (musteri.getHesaplar().size() > 1 && musteri.getHesaplar().get(1) instanceof YatirimHesabi) {
                YatirimHesabi yatirimHesabi = (YatirimHesabi) musteri.getHesaplar().get(1);
                String tlMiktar = JOptionPane.showInputDialog("Kaç TL'lik yatırım yapacaksınız?");
                
                if (tlMiktar != null) {
                    double miktar = Double.parseDouble(tlMiktar);
                    // Ana hesaptan düş
                    if (musteri.getHesaplar().get(0).getBakiye() >= miktar) {
                        musteri.getHesaplar().get(0).bakiyeAzalt(miktar);
                        // Yatırım hesabına ekle (Döviz olarak geçer)
                        yatirimHesabi.bakiyeEkle(miktar); 
                        JOptionPane.showMessageDialog(null, "İşlem Başarılı!");
                        bakiyeGuncelle();
                    } else {
                        JOptionPane.showMessageDialog(null, "TL Bakiyeniz Yetersiz!");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Yatırım Hesabınız Yok! (BankaVeritabani'na ekleyiniz)");
            }
        });
        
        // Geçmiş Butonu
        gecmisBtn.addActionListener(e -> {
             StringBuilder gecmis = new StringBuilder("--- İŞLEM GEÇMİŞİ ---\n");
             for(Islem islem : musteri.getHesaplar().get(0).getIslemGecmisi()) {
                 gecmis.append(islem.toString()).append("\n");
             }
             JOptionPane.showMessageDialog(null, gecmis.toString());
        });

        setVisible(true);
    }

    // Ekrandaki yazıları tazeleyen metod
    private void bakiyeGuncelle() {
        double vadesizBakiye = musteri.getHesaplar().get(0).getBakiye();
        String metin = "Vadesiz: " + vadesizBakiye + " TL";
        
        // Eğer yatırım hesabı varsa onu da göster
        if (musteri.getHesaplar().size() > 1) {
            double yatirimBakiye = musteri.getHesaplar().get(1).getBakiye();
             metin += "  |  Yatırım: " + String.format("%.2f", yatirimBakiye);
        }
        bakiyeLabel.setText(metin);
    }
}