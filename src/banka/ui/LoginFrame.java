package banka.ui;

import banka.model.Musteri;
import banka.service.Banka;
import banka.util.MetinUtil;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import javax.swing.*;
import javax.swing.text.MaskFormatter;

public class LoginFrame extends JFrame {

    private final Banka banka;
    private JTabbedPane tabs;

    public LoginFrame(Banka banka) {
        this.banka = banka;
        setTitle("Bankacilik Sistemi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Pencere boyutu
        setSize(450, 550); 
        
        setLocationRelativeTo(null);
        setResizable(false); 

        tabs = new JTabbedPane();
        tabs.addTab("Giriş Yap", girisPaneli());
        tabs.addTab("Üye Ol", uyeOlPaneli());

        setContentPane(tabs);
    }

    // ------------------- GİRİŞ YAP PANELİ -------------------
    private JPanel girisPaneli() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));

        JLabel lblTc = new JLabel("TC Kimlik:");
        JFormattedTextField tcField = new JFormattedTextField(createTcMask());
        tcField.setPreferredSize(new Dimension(250, 35));

        JLabel lblSifre = new JLabel("Şifre (6 Rakam):");
        JPasswordField sifreField = new JPasswordField();
        sifreField.setPreferredSize(new Dimension(250, 35));
        
        // ÖZEL AYAR: Şifre alanını sadece rakam ve max 6 hane yap
        sadeceRakamVeLimit(sifreField, 6);

        form.add(lblTc);
        form.add(tcField);
        form.add(lblSifre);
        form.add(sifreField);

        // Ortalamak için
        JPanel formWrapper = new JPanel(new GridBagLayout());
        formWrapper.add(form);

        JPanel bottom = new JPanel(new GridLayout(2, 1, 5, 5));
        JLabel mesajLabel = new JLabel(" ");
        mesajLabel.setForeground(new Color(200, 0, 0));
        mesajLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JButton girisBtn = new JButton("Giriş Yap");
        girisBtn.setFont(new Font("Arial", Font.BOLD, 14));
        girisBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        girisBtn.setPreferredSize(new Dimension(100, 40));

        bottom.add(mesajLabel);
        bottom.add(girisBtn);

        p.add(formWrapper, BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);

        girisBtn.addActionListener(e -> {
            mesajLabel.setText(" ");
            try {
                String tc = MetinUtil.sadeceRakam(tcField.getText()); 
                String sifre = new String(sifreField.getPassword());

                // KONTROLLER
                if (tc.length() != 11) {
                    mesajLabel.setText("TC Kimlik 11 haneli olmalıdır.");
                    return;
                }
                
                // ARTIK KESİN KURAL: ŞİFRE TAM 6 HANE OLMALI
                if (sifre.length() != 6) {
                    mesajLabel.setText("Şifre 6 haneli olmalıdır.");
                    return;
                }

                Musteri m = null;
                for (Musteri musteri : banka.getMusteriler().values()) {
                    if (musteri.getTc().equals(tc) && musteri.sifreDogruMu(sifre)) {
                        m = musteri;
                        break;
                    }
                }

                if (m == null) {
                    mesajLabel.setText("TC veya Şifre hatalı!");
                    return;
                }

                MainFrame main = new MainFrame(banka, m);
                main.setVisible(true);
                dispose();

            } catch (Exception ex) {
                mesajLabel.setText("Hata: " + ex.getMessage());
            }
        });

        return p;
    }

    // ------------------- ÜYE OL PANELİ -------------------
    private JPanel uyeOlPaneli() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));

        JLabel lblTc = new JLabel("TC Kimlik (11 Haneli):");
        JFormattedTextField tcField = new JFormattedTextField(createTcMask());
        tcField.setPreferredSize(new Dimension(250, 30));

        JLabel lblAd = new JLabel("Ad Soyad:");
        JTextField adSoyadField = new JTextField();
        adSoyadField.setPreferredSize(new Dimension(250, 30));

        JLabel lblPass1 = new JLabel("Şifre Belirle (6 Rakam):");
        JPasswordField sifreField = new JPasswordField();
        sifreField.setPreferredSize(new Dimension(250, 30));
        
        // ÖZEL AYAR: Şifre alanını sadece rakam ve max 6 hane yap
        sadeceRakamVeLimit(sifreField, 6);

        JLabel lblPass2 = new JLabel("Şifre (Tekrar):");
        JPasswordField sifreTekrarField = new JPasswordField();
        sifreTekrarField.setPreferredSize(new Dimension(250, 30));
        
        // ÖZEL AYAR: Tekrar alanını da kısıtla
        sadeceRakamVeLimit(sifreTekrarField, 6);

        form.add(lblTc);
        form.add(tcField);
        form.add(lblAd);
        form.add(adSoyadField);
        form.add(lblPass1);
        form.add(sifreField);
        form.add(lblPass2);
        form.add(sifreTekrarField);

        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.add(form, BorderLayout.NORTH);

        JPanel bottom = new JPanel(new GridLayout(2, 1, 5, 5));
        JLabel mesajLabel = new JLabel(" ");
        mesajLabel.setForeground(new Color(200, 0, 0));
        mesajLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton uyeOlBtn = new JButton("Kayıt Ol");
        uyeOlBtn.setFont(new Font("Arial", Font.BOLD, 14));
        uyeOlBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        uyeOlBtn.setPreferredSize(new Dimension(100, 40));

        bottom.add(mesajLabel);
        bottom.add(uyeOlBtn);

        p.add(formWrapper, BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);

        uyeOlBtn.addActionListener(e -> {
            mesajLabel.setForeground(new Color(200, 0, 0));
            mesajLabel.setText(" ");
            try {
                String tc = MetinUtil.sadeceRakam(tcField.getText());
                String adSoyad = MetinUtil.titleCase(adSoyadField.getText());
                String sifre = new String(sifreField.getPassword());
                String sifreTekrar = new String(sifreTekrarField.getPassword());

                if (!sifre.equals(sifreTekrar)) {
                    mesajLabel.setText("Şifreler uyuşmuyor!");
                    return;
                }
                if (tc.length() != 11) {
                    mesajLabel.setText("TC 11 haneli olmalı.");
                    return;
                }
                if (adSoyad.split(" ").length < 2) {
                    mesajLabel.setText("Ad ve Soyad giriniz.");
                    return;
                }
                
                // ŞİFRE TAM 6 HANE KONTROLÜ
                if (sifre.length() != 6) {
                    mesajLabel.setText("Şifre 6 haneli RAKAM olmalı.");
                    return;
                }

                // Kayıt işlemi
                banka.uyeOl(tc, adSoyad, sifre);

                // POP-UP MESAJI
                JOptionPane.showMessageDialog(this, 
                    "Tebrikler! Kaydınız başarıyla oluşturuldu.\n\n" +
                    "🎁 Hesabınıza 2000 TL Hoşgeldin Bonusu Tanımlandı:\n" +
                    "   • 1000 TL Vadesiz Hesap\n" +
                    "   • 1000 TL Tasarruf Hesabı\n\n" +
                    "Şimdi giriş yapabilirsiniz.", 
                    "Kayıt Başarılı", 
                    JOptionPane.INFORMATION_MESSAGE);

                // Temizlik
                tcField.setValue(null);
                adSoyadField.setText("");
                sifreField.setText("");
                sifreTekrarField.setText("");
                mesajLabel.setText(" ");

                // Giriş sekmesine yönlendir
                tabs.setSelectedIndex(0);

            } catch (Exception ex) {
                mesajLabel.setText(ex.getMessage());
            }
        });

        return p;
    }

    // --- YARDIMCI METOTLAR ---

    // Bu metot, şifre kutusuna sadece RAKAM girilmesini ve MAX UZUNLUĞU sağlar
    private void sadeceRakamVeLimit(JPasswordField field, int limit) {
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                
                // Backspace (Silme) tuşuna izin ver, yoksa silemezler
                if (c == KeyEvent.VK_BACK_SPACE) {
                    return;
                }

                // Rakam değilse yazma
                if (!Character.isDigit(c)) {
                    e.consume(); // Tuş vuruşunu yut (yazma)
                    return;
                }

                // Limit dolduysa yazma
                if (field.getPassword().length >= limit) {
                    e.consume(); // Tuş vuruşunu yut
                }
            }
        });
    }

    private MaskFormatter createTcMask() {
        try {
            MaskFormatter mf = new MaskFormatter("###########");
            mf.setPlaceholderCharacter('_');
            return mf;
        } catch (ParseException e) {
            return new MaskFormatter();
        }
    }
}