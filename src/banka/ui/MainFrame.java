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

    // Bakiyeyi göstermek için etiket
    private JLabel lblVadesizBilgi;

    public MainFrame(Banka banka, Musteri musteri) {
        this.banka = banka;
        this.musteri = musteri;

        // Başlıkta hata olmasın diye kontrol ekledik
        String ad = (musteri != null) ? musteri.getAdSoyad() : "Değerli Müşterimiz";
        setTitle("Bankacılık - " + ad);
        
        setSize(400, 600); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // --- ANA YAPI ---
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // --- SAYFALARI EKLİYORUZ ---
        mainPanel.add(anaMenuOlustur(), "ANA_MENU");
        mainPanel.add(vadesizEkraniOlustur(), "VADESIZ_EKRAN");
        mainPanel.add(virmanEkraniOlustur(), "VIRMAN_EKRAN");
        mainPanel.add(eftEkraniOlustur(), "EFT_EKRAN");
        mainPanel.add(tasarrufEkraniOlustur(), "TASARRUF_EKRAN");
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
        gbc.insets = new Insets(10, 0, 10, 0); 
        gbc.gridx = 0; 

        // Butonlar
        JButton btnVadesiz = ortaBoyButonYap("VADESİZ HESAP");
        btnVadesiz.addActionListener(e -> {
            bilgileriGuncelle(); // Tıklayınca bakiyeyi güncelle
            cardLayout.show(mainPanel, "VADESIZ_EKRAN");
        });

        JButton btnTasarruf = ortaBoyButonYap("TASARRUF HESABI");
        btnTasarruf.addActionListener(e -> cardLayout.show(mainPanel, "TASARRUF_EKRAN"));

        JButton btnGecmis = ortaBoyButonYap("İŞLEM GEÇMİŞİ");
        btnGecmis.addActionListener(e -> cardLayout.show(mainPanel, "GECMIS_EKRAN"));

        gbc.gridy = 0; panel.add(btnVadesiz, gbc);
        gbc.gridy = 1; panel.add(btnTasarruf, gbc);
        gbc.gridy = 2; panel.add(btnGecmis, gbc);

        // Çıkış Butonu
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(panel, BorderLayout.CENTER);
        
        JButton btnCikis = new JButton("Çıkış");
        btnCikis.addActionListener(e -> {
            dispose();
            new LoginFrame(banka).setVisible(true);
        });
        
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setBackground(new Color(240, 245, 250));
        bottom.add(btnCikis);
        wrapper.add(bottom, BorderLayout.SOUTH);

        return wrapper;
    }

    // ========================================================================
    // 2. VADESİZ HESAP EKRANI
    // ========================================================================
    private JPanel vadesizEkraniOlustur() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);

        // Üst Kısım: Bakiye Bilgisi
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        infoPanel.setBackground(new Color(220, 240, 255)); 
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        lblVadesizBilgi = new JLabel("Yükleniyor...", SwingConstants.CENTER);
        lblVadesizBilgi.setFont(new Font("Arial", Font.BOLD, 15));
        infoPanel.add(lblVadesizBilgi);
        p.add(infoPanel, BorderLayout.NORTH);

        // Orta Kısım: Butonlar
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;

        JButton btnTransfer = ortaBoyButonYap("PARA TRANSFERİ");
        JButton btnFatura = ortaBoyButonYap("FATURA YATIR");

        // TRANSFER SEÇENEKLERİ
        btnTransfer.addActionListener(e -> {
            String[] secenekler = {"Kendi Hesaplarım Arası", "Başka Hesaba (EFT)"};
            int secim = JOptionPane.showOptionDialog(this, 
                    "Transfer türünü seçiniz:", "Transfer", 
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, 
                    null, secenekler, secenekler[0]);

            if (secim == 0) cardLayout.show(mainPanel, "VIRMAN_EKRAN");
            else if (secim == 1) cardLayout.show(mainPanel, "EFT_EKRAN");
        });

        // Fatura butonu
        btnFatura.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Ödenecek fatura bulunamadı.");
        });

        gbc.gridy = 0; buttonPanel.add(btnTransfer, gbc);
        gbc.gridy = 1; buttonPanel.add(btnFatura, gbc);
        p.add(buttonPanel, BorderLayout.CENTER);

        // Alt Kısım: Geri Dön
        JButton btnGeri = new JButton("<< Ana Menüye Dön");
        btnGeri.setPreferredSize(new Dimension(380, 40));
        btnGeri.addActionListener(e -> cardLayout.show(mainPanel, "ANA_MENU"));
        p.add(btnGeri, BorderLayout.SOUTH);

        return p;
    }

    // ========================================================================
    // 3. VİRMAN EKRANI
    // ========================================================================
    private JPanel virmanEkraniOlustur() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Hesaplarım Arası Transfer"));
        
        JTextField txtTutar = new JTextField(15);
        JButton btnGonder = new JButton("Transfer Yap");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0; gbc.gridy = 0;
        
        p.add(new JLabel("Vadesiz -> Tasarruf Hesabına"), gbc);
        
        gbc.gridy = 1; p.add(new JLabel("Tutar (TL):"), gbc);
        gbc.gridy = 2; p.add(txtTutar, gbc);
        gbc.gridy = 3; p.add(btnGonder, gbc);

        btnGonder.addActionListener(e -> {
            try {
                BigDecimal tutar = new BigDecimal(txtTutar.getText());
                if(banka != null) {
                    banka.kendiHesaplarimArasiTransfer(musteri, true, tutar);
                    JOptionPane.showMessageDialog(this, "Transfer Başarılı!");
                    txtTutar.setText("");
                    bilgileriGuncelle();
                    cardLayout.show(mainPanel, "VADESIZ_EKRAN"); 
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
            }
        });

        // İptal Butonu
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(p, BorderLayout.CENTER);
        JButton btnGeri = new JButton("<< İptal");
        btnGeri.addActionListener(e -> cardLayout.show(mainPanel, "VADESIZ_EKRAN"));
        wrapper.add(btnGeri, BorderLayout.SOUTH);
        return wrapper;
    }

    // ========================================================================
    // 4. EFT EKRANI
    // ========================================================================
    private JPanel eftEkraniOlustur() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Başka Hesaba Transfer (EFT)"));

        JTextField txtHesap = new JTextField(15);
        JTextField txtAd = new JTextField(15);
        JTextField txtTutar = new JTextField(15);
        JButton btnGonder = new JButton("Parayı Gönder");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 0;

        p.add(new JLabel("Alıcı Hesap No:"), gbc);
        gbc.gridy++; p.add(txtHesap, gbc);

        gbc.gridy++; p.add(new JLabel("Alıcı Ad Soyad:"), gbc);
        gbc.gridy++; p.add(txtAd, gbc);

        gbc.gridy++; p.add(new JLabel("Tutar (TL):"), gbc);
        gbc.gridy++; p.add(txtTutar, gbc);

        gbc.gridy++; gbc.fill = GridBagConstraints.HORIZONTAL;
        p.add(btnGonder, gbc);

        btnGonder.addActionListener(e -> {
            try {
                BigDecimal tutar = new BigDecimal(txtTutar.getText());
                if(banka != null) {
                    banka.transferYap(musteri, txtHesap.getText(), txtAd.getText(), tutar);
                    JOptionPane.showMessageDialog(this, "Para Gönderildi.");
                    txtHesap.setText(""); txtAd.setText(""); txtTutar.setText("");
                    bilgileriGuncelle();
                    cardLayout.show(mainPanel, "VADESIZ_EKRAN");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(p, BorderLayout.CENTER);
        JButton btnGeri = new JButton("<< İptal");
        btnGeri.addActionListener(e -> cardLayout.show(mainPanel, "VADESIZ_EKRAN"));
        wrapper.add(btnGeri, BorderLayout.SOUTH);
        return wrapper;
    }

    // ========================================================================
    // DİĞER EKRANLAR
    // ========================================================================
    private JPanel tasarrufEkraniOlustur() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel("Tasarruf Ekranı", SwingConstants.CENTER));
        JButton btnGeri = new JButton("<< Ana Menü");
        btnGeri.addActionListener(e -> cardLayout.show(mainPanel, "ANA_MENU"));
        p.add(btnGeri, BorderLayout.NORTH);
        return p;
    }

    private JPanel gecmisEkraniOlustur() {
        JPanel p = new JPanel(new BorderLayout());
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        // --- HATA ÇÖZÜMÜ ---
        // VadesizHesap sınıfında 'getIslemGecmisi' metodu olmadığı için
        // burada o kodu çağırmıyoruz, sadece statik bir yazı gösteriyoruz.
        area.setText("İşlem geçmişi özelliği henüz VadesizHesap sınıfına eklenmedi.");
        // -------------------
        
        p.add(new JScrollPane(area), BorderLayout.CENTER);
        
        JButton btnGeri = new JButton("<< Ana Menü");
        btnGeri.addActionListener(e -> cardLayout.show(mainPanel, "ANA_MENU"));
        
        p.add(btnGeri, BorderLayout.SOUTH);
        return p;
    }

    // ========================================================================
    // YARDIMCI METOTLAR
    // ========================================================================
    private void bilgileriGuncelle() {
        // Eğer veri yoksa güncelleme yapma
        if (musteri == null || musteri.getVadesiz() == null) {
            return;
        }

        try {
            String bakiye = "0.00";
            if (musteri.getVadesiz().getBakiye() != null) {
                bakiye = musteri.getVadesiz().getBakiye().toString();
            }
            String no = musteri.getVadesiz().getHesapNo();
            
            if (lblVadesizBilgi != null) {
                lblVadesizBilgi.setText("Hesap: " + no + "  |  Bakiye: " + bakiye + " TL");
            }
        } catch (Exception e) {
            // Hata olursa program çökmesin
        }
    }

    private JButton ortaBoyButonYap(String yazi) {
        JButton btn = new JButton(yazi);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(220, 50));
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        return btn;
    }
}