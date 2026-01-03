package banka.ui;

import banka.model.Musteri;
import banka.service.Banka;
import java.awt.*;
import java.math.BigDecimal;
import javax.swing.*;

public class MainFrame extends JFrame {

    private final Banka banka;
    private final Musteri musteri;

    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Bilgileri göstermek için etiketler
    private JLabel lblVadesizBilgi;
    private JLabel lblTasarrufBilgi;

    public MainFrame(Banka banka, Musteri musteri) {
        this.banka = banka;
        this.musteri = musteri;

        String ad = (musteri != null) ? musteri.getAdSoyad() : "Değerli Müşterimiz";
        setTitle("Bankacılık - " + ad);
        
        setSize(420, 680); // Boyutu biraz daha artırdık
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // --- ANA YAPI ---
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // --- SAYFALAR ---
        mainPanel.add(anaMenuOlustur(), "ANA_MENU");
        mainPanel.add(vadesizEkraniOlustur(), "VADESIZ_EKRAN");
        mainPanel.add(virmanEkraniOlustur(), "VIRMAN_EKRAN");       
        mainPanel.add(eftEkraniOlustur(), "EFT_EKRAN");             
        mainPanel.add(faturaKayitEkraniOlustur(), "FATURA_KAYIT");  
        mainPanel.add(faturaOdemeEkraniOlustur(), "FATURA_ODE");    

        mainPanel.add(tasarrufEkraniOlustur(), "TASARRUF_EKRAN");
        mainPanel.add(tasarruftanVadesizeEkraniOlustur(), "TASARRUF_VIRMAN"); 
        mainPanel.add(altinAlimEkraniOlustur(), "ALTIN_AL");        

        mainPanel.add(gecmisEkraniOlustur(), "GECMIS_EKRAN");

        add(mainPanel);
    }

    // ========================================================================
    // 1. ANA MENÜ
    // ========================================================================
    private JPanel anaMenuOlustur() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 245, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0); 
        gbc.gridx = 0; 

        JButton btnVadesiz = anaMenuButonu("VADESİZ HESAP");
        btnVadesiz.addActionListener(e -> {
            bilgileriGuncelle();
            cardLayout.show(mainPanel, "VADESIZ_EKRAN");
        });

        JButton btnTasarruf = anaMenuButonu("TASARRUF HESABI");
        btnTasarruf.addActionListener(e -> {
            bilgileriGuncelle();
            cardLayout.show(mainPanel, "TASARRUF_EKRAN");
        });

        JButton btnGecmis = anaMenuButonu("İŞLEM GEÇMİŞİ");
        btnGecmis.addActionListener(e -> cardLayout.show(mainPanel, "GECMIS_EKRAN"));

        gbc.gridy = 0; panel.add(btnVadesiz, gbc);
        gbc.gridy = 1; panel.add(btnTasarruf, gbc);
        gbc.gridy = 2; panel.add(btnGecmis, gbc);

        // Çıkış Butonu
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(panel, BorderLayout.CENTER);
        JButton btnCikis = new JButton("Çıkış Yap");
        btnCikis.addActionListener(e -> {
            dispose();
            new LoginFrame(banka).setVisible(true);
        });
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.add(btnCikis);
        wrapper.add(bottom, BorderLayout.SOUTH);

        return wrapper;
    }

    // ========================================================================
    // 2. VADESİZ HESAP EKRANI (HESAP NO VE BAKİYE BURADA GÖRÜNECEK)
    // ========================================================================
    private JPanel vadesizEkraniOlustur() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);

        // --- ÜST: HESAP NO VE BAKİYE ---
        // Burayı HTML ile şekillendiriyoruz ki alt alta yazsın
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        infoPanel.setBackground(new Color(220, 240, 255));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        lblVadesizBilgi = new JLabel("", SwingConstants.CENTER);
        // Yazı tipi ve rengi
        lblVadesizBilgi.setFont(new Font("Arial", Font.PLAIN, 16));
        
        infoPanel.add(lblVadesizBilgi);
        p.add(infoPanel, BorderLayout.NORTH);

        // --- ORTA: İŞLEMLER ---
        JPanel menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL; 

        // TRANSFERLER
        JLabel lblTransfer = new JLabel("--- PARA TRANSFERLERİ ---", SwingConstants.CENTER);
        lblTransfer.setForeground(Color.GRAY);
        gbc.gridy = 0; menuPanel.add(lblTransfer, gbc);

        JButton btnVirman = kucukButonYap("Hesaplarım Arası Transfer");
        btnVirman.addActionListener(e -> cardLayout.show(mainPanel, "VIRMAN_EKRAN"));
        gbc.gridy = 1; menuPanel.add(btnVirman, gbc);

        JButton btnEft = kucukButonYap("Başka Hesaba Transfer (EFT)");
        btnEft.addActionListener(e -> cardLayout.show(mainPanel, "EFT_EKRAN"));
        gbc.gridy = 2; menuPanel.add(btnEft, gbc);

        // FATURALAR
        gbc.gridy = 3; menuPanel.add(Box.createVerticalStrut(15), gbc); // Boşluk
        JLabel lblFatura = new JLabel("--- FATURA İŞLEMLERİ ---", SwingConstants.CENTER);
        lblFatura.setForeground(Color.GRAY);
        gbc.gridy = 4; menuPanel.add(lblFatura, gbc);

        JButton btnFaturaKaydet = kucukButonYap("Fatura Kaydet");
        btnFaturaKaydet.addActionListener(e -> cardLayout.show(mainPanel, "FATURA_KAYIT"));
        gbc.gridy = 5; menuPanel.add(btnFaturaKaydet, gbc);

        JButton btnFaturaOde = kucukButonYap("Kayıtlı Fatura Öde");
        btnFaturaOde.addActionListener(e -> cardLayout.show(mainPanel, "FATURA_ODE"));
        gbc.gridy = 6; menuPanel.add(btnFaturaOde, gbc);

        p.add(menuPanel, BorderLayout.CENTER);

        // --- ALT: Geri ---
        JButton btnGeri = new JButton("<< Ana Menüye Dön");
        btnGeri.setPreferredSize(new Dimension(380, 45));
        btnGeri.addActionListener(e -> cardLayout.show(mainPanel, "ANA_MENU"));
        p.add(btnGeri, BorderLayout.SOUTH);

        return p;
    }

    // ========================================================================
    // 2.1. VİRMAN EKRANI
    // ========================================================================
    private JPanel virmanEkraniOlustur() {
        return basitFormOlustur("Vadesiz Hesaptan -> Tasarruf Hesabına", "Gönderilecek Tutar (TL):",
            tutar -> {
                banka.kendiHesaplarimArasiTransfer(musteri, true, tutar);
                JOptionPane.showMessageDialog(this, "Tasarruf hesabınıza " + tutar + " TL aktarıldı.");
                bilgileriGuncelle();
                cardLayout.show(mainPanel, "VADESIZ_EKRAN");
            }, "VADESIZ_EKRAN");
    }

    // ========================================================================
    // 2.2. EFT EKRANI (Başka Hesaba Transfer)
    // ========================================================================
    private JPanel eftEkraniOlustur() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Başka Hesaba Transfer (EFT)"));
        
        JTextField txtHesap = new JTextField(15);
        JTextField txtAd = new JTextField(15);
        JTextField txtTutar = new JTextField(15);
        JButton btnGonder = new JButton("Gönder");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5); gbc.anchor = GridBagConstraints.WEST; gbc.gridx=0; gbc.gridy=0;

        p.add(new JLabel("Alıcı Hesap No:"), gbc); gbc.gridy++; p.add(txtHesap, gbc);
        gbc.gridy++; p.add(new JLabel("Alıcı Ad Soyad:"), gbc); gbc.gridy++; p.add(txtAd, gbc);
        gbc.gridy++; p.add(new JLabel("Tutar (TL):"), gbc); gbc.gridy++; p.add(txtTutar, gbc);
        gbc.gridy++; gbc.fill = GridBagConstraints.HORIZONTAL; p.add(btnGonder, gbc);

        btnGonder.addActionListener(e -> {
            try {
                BigDecimal t = new BigDecimal(txtTutar.getText());
                banka.transferYap(musteri, txtHesap.getText(), txtAd.getText(), t);
                JOptionPane.showMessageDialog(this, "Para başarıyla gönderildi.");
                txtHesap.setText(""); txtAd.setText(""); txtTutar.setText("");
                bilgileriGuncelle();
                cardLayout.show(mainPanel, "VADESIZ_EKRAN");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage()); }
        });

        return sarmala(p, "VADESIZ_EKRAN");
    }

    // ========================================================================
    // 2.3. FATURA KAYIT
    // ========================================================================
    private JPanel faturaKayitEkraniOlustur() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Yeni Fatura Kaydet"));
        
        JTextField txtTur = new JTextField(15);
        JTextField txtTutar = new JTextField(15);
        JButton btnKaydet = new JButton("Kaydet");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5); gbc.gridx=0; gbc.gridy=0;
        
        p.add(new JLabel("Fatura Türü (Örn: Elektrik):"), gbc); gbc.gridy++; p.add(txtTur, gbc);
        gbc.gridy++; p.add(new JLabel("Fatura Tutarı (TL):"), gbc); gbc.gridy++; p.add(txtTutar, gbc);
        gbc.gridy++; p.add(btnKaydet, gbc);

        btnKaydet.addActionListener(e -> {
            try {
                BigDecimal t = new BigDecimal(txtTutar.getText());
                musteri.getVadesiz().faturaKaydet(txtTur.getText(), t);
                JOptionPane.showMessageDialog(this, "Fatura kaydedildi: " + txtTur.getText());
                txtTur.setText(""); txtTutar.setText("");
                cardLayout.show(mainPanel, "VADESIZ_EKRAN");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage()); }
        });
        return sarmala(p, "VADESIZ_EKRAN");
    }

    // ========================================================================
    // 2.4. FATURA ÖDEME
    // ========================================================================
    private JPanel faturaOdemeEkraniOlustur() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Kayıtlı Fatura Öde"));
        
        JTextField txtTur = new JTextField(15);
        JButton btnOde = new JButton("Ödeme Yap");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5); gbc.gridx=0; gbc.gridy=0;
        
        p.add(new JLabel("Ödenecek Fatura Türü:"), gbc); gbc.gridy++; p.add(txtTur, gbc);
        gbc.gridy++; p.add(btnOde, gbc);

        btnOde.addActionListener(e -> {
            try {
                musteri.getVadesiz().faturaOde(txtTur.getText());
                JOptionPane.showMessageDialog(this, "Fatura ödendi: " + txtTur.getText());
                txtTur.setText("");
                bilgileriGuncelle();
                cardLayout.show(mainPanel, "VADESIZ_EKRAN");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage()); }
        });
        return sarmala(p, "VADESIZ_EKRAN");
    }

    // ========================================================================
    // 3. TASARRUF EKRANI
    // ========================================================================
    private JPanel tasarrufEkraniOlustur() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        infoPanel.setBackground(new Color(255, 250, 230)); 
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        lblTasarrufBilgi = new JLabel("Yükleniyor...", SwingConstants.CENTER);
        lblTasarrufBilgi.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(lblTasarrufBilgi);
        p.add(infoPanel, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel(new GridBagLayout());
        btnPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0); gbc.gridx = 0; gbc.gridy = 0;

        JButton btnGonder = kucukButonYap("Vadesiz Hesaba Para Gönder");
        btnGonder.addActionListener(e -> cardLayout.show(mainPanel, "TASARRUF_VIRMAN"));
        
        JButton btnAltin = kucukButonYap("Altın Al");
        btnAltin.addActionListener(e -> cardLayout.show(mainPanel, "ALTIN_AL"));

        btnPanel.add(btnGonder, gbc); gbc.gridy++;
        btnPanel.add(btnAltin, gbc);

        p.add(btnPanel, BorderLayout.CENTER);

        JButton btnGeri = new JButton("<< Ana Menüye Dön");
        btnGeri.addActionListener(e -> cardLayout.show(mainPanel, "ANA_MENU"));
        p.add(btnGeri, BorderLayout.SOUTH);

        return p;
    }

    private JPanel tasarruftanVadesizeEkraniOlustur() {
        return basitFormOlustur("Tasarruf Hesabından -> Vadesiz Hesaba", "Aktarılacak Tutar (TL):",
            tutar -> {
                banka.kendiHesaplarimArasiTransfer(musteri, false, tutar); 
                JOptionPane.showMessageDialog(this, "Vadesiz hesaba " + tutar + " TL aktarıldı.");
                bilgileriGuncelle();
                cardLayout.show(mainPanel, "TASARRUF_EKRAN");
            }, "TASARRUF_EKRAN");
    }

    private JPanel altinAlimEkraniOlustur() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Altın Alım İşlemi"));
        
        JTextField txtTutar = new JTextField(15);
        JTextField txtKur = new JTextField("3000", 15);
        JButton btnAl = new JButton("Altın Al");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5); gbc.gridx=0; gbc.gridy=0;

        p.add(new JLabel("Kullanılacak Tutar (TL):"), gbc); gbc.gridy++; p.add(txtTutar, gbc);
        gbc.gridy++; p.add(new JLabel("Altın Gram Kuru (TL):"), gbc); gbc.gridy++; p.add(txtKur, gbc);
        gbc.gridy++; p.add(btnAl, gbc);

        btnAl.addActionListener(e -> {
            try {
                BigDecimal t = new BigDecimal(txtTutar.getText());
                BigDecimal k = new BigDecimal(txtKur.getText());
                musteri.getTasarruf().altinAl(t, k, banka.getIslemGecmisi());
                
                JOptionPane.showMessageDialog(this, "Altın alındı.");
                bilgileriGuncelle();
                cardLayout.show(mainPanel, "TASARRUF_EKRAN");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage()); }
        });
        return sarmala(p, "TASARRUF_EKRAN");
    }

    // ========================================================================
    // 4. GEÇMİŞ EKRANI
    // ========================================================================
    private JPanel gecmisEkraniOlustur() {
        JPanel p = new JPanel(new BorderLayout());
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JButton btnYenile = new JButton("Listeyi Yenile");
        btnYenile.addActionListener(e -> {
            if (musteri != null && musteri.getVadesiz() != null) {
                area.setText("--- VADESİZ HESAP GEÇMİŞİ ---\n" + 
                             musteri.getVadesiz().getIslemGecmisi() + 
                             "\n\n--- TASARRUF HESABI GEÇMİŞİ ---\n" + 
                             musteri.getTasarruf().getIslemGecmisi());
            }
        });

        p.add(new JScrollPane(area), BorderLayout.CENTER);
        
        JPanel bot = new JPanel(new BorderLayout());
        bot.add(btnYenile, BorderLayout.NORTH);
        JButton btnGeri = new JButton("<< Ana Menü");
        btnGeri.addActionListener(e -> cardLayout.show(mainPanel, "ANA_MENU"));
        bot.add(btnGeri, BorderLayout.SOUTH);
        p.add(bot, BorderLayout.SOUTH);
        return p;
    }

    // ========================================================================
    // YARDIMCI METOTLAR
    // ========================================================================
    
    // Basit bir transfer formu oluşturan yardımcı metot
    private JPanel basitFormOlustur(String baslik, String etiket, java.util.function.Consumer<BigDecimal> islem, String geriDonusEkrani) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder(baslik));
        JTextField txt = new JTextField(15);
        JButton btn = new JButton("İşlemi Onayla");
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5); gbc.gridx=0; gbc.gridy=0;
        p.add(new JLabel(etiket), gbc); gbc.gridy++; p.add(txt, gbc); gbc.gridy++; p.add(btn, gbc);

        btn.addActionListener(e -> {
            try {
                islem.accept(new BigDecimal(txt.getText()));
                txt.setText("");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage()); }
        });
        return sarmala(p, geriDonusEkrani);
    }

    private JPanel sarmala(JPanel icerik, String geriDonusKey) {
        JPanel w = new JPanel(new BorderLayout());
        w.add(icerik, BorderLayout.CENTER);
        JButton b = new JButton("<< Geri Dön / İptal");
        b.addActionListener(e -> cardLayout.show(mainPanel, geriDonusKey));
        w.add(b, BorderLayout.SOUTH);
        return w;
    }

    // *** BURASI ÇOK ÖNEMLİ: HESAP NUMARASINI BURADA GÖSTERİYORUZ ***
    private void bilgileriGuncelle() {
        if (musteri == null) return;
        try {
            // Vadesiz Bilgi Güncelleme
            if (lblVadesizBilgi != null && musteri.getVadesiz() != null) {
                String hesapNo = musteri.getVadesiz().getHesapNo();
                String bakiye = musteri.getVadesiz().getBakiye().toString();
                
                // HTML kullanarak alt alta ve renkli yazdırıyoruz
                lblVadesizBilgi.setText(
                    "<html><center>" +
                    "HESAP NO: <font color='blue'><b>" + hesapNo + "</b></font><br>" +
                    "BAKİYE: <font color='green'><b>" + bakiye + " TL</b></font>" +
                    "</center></html>"
                );
            }
            
            // Tasarruf Bilgi Güncelleme
            if (lblTasarrufBilgi != null && musteri.getTasarruf() != null) {
                String tBakiye = musteri.getTasarruf().getBakiye().toString();
                String tAltin = musteri.getTasarruf().getAltinGram().toString();
                
                lblTasarrufBilgi.setText(
                    "<html><center>" +
                    "Tasarruf Bakiye: " + tBakiye + " TL<br>" +
                    "Altın: <b>" + tAltin + " gr</b>" +
                    "</center></html>"
                );
            }
        } catch (Exception e) {}
    }

    private JButton anaMenuButonu(String yazi) {
        JButton btn = new JButton(yazi);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(250, 60));
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton kucukButonYap(String yazi) {
        JButton btn = new JButton(yazi);
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setPreferredSize(new Dimension(300, 45));
        btn.setBackground(new Color(245, 245, 245));
        return btn;
    }
}