package banka.ui;

import banka.model.Musteri;
import banka.service.Banka;
import banka.util.MetinUtil;
import java.awt.*;
import java.text.ParseException;
import javax.swing.*;
import javax.swing.text.MaskFormatter;

// HATA BURADAYDI: Üstte fazladan açılan class tanımı silindi.
public class LoginFrame extends JFrame {

    private final Banka banka;

    public LoginFrame(Banka banka) {
        this.banka = banka;
        setTitle("Bankacilik Sistemi - Giris");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 420);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Giris Yap", girisPaneli());
        tabs.addTab("Uye Ol", uyeOlPaneli());

        setContentPane(tabs);
    }

    // ------------------- GIRIS PANELI -------------------

    private JPanel girisPaneli() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));

        JTextField adSoyadField = new JTextField();
        JPasswordField sifreField = new JPasswordField();

        form.add(new JLabel("Ad Soyad:"));
        form.add(adSoyadField);
        form.add(new JLabel("Sifre:"));
        form.add(sifreField);

        JLabel mesajLabel = new JLabel(" ");
        mesajLabel.setForeground(new Color(200, 0, 0));

        JButton girisBtn = new JButton("Giris");

        girisBtn.addActionListener(e -> {
            mesajLabel.setForeground(new Color(200, 0, 0));
            mesajLabel.setText(" ");

            try {
                String adSoyad = MetinUtil.titleCase(adSoyadField.getText());
                String sifre = new String(sifreField.getPassword());

                if (adSoyad.isBlank()) {
                    mesajLabel.setText("Ad Soyad bos olamaz.");
                    return;
                }
                MetinUtil.minUzunluk(sifre, 6, "Sifre");

                // UI'da duzeltip gosterelim
                adSoyadField.setText(adSoyad);

                Musteri m = banka.girisYap(adSoyad, sifre);

                // Basarili giris -> MainFrame
                MainFrame main = new MainFrame(banka, m);
                main.setVisible(true);
                dispose();

            } catch (Exception ex) {
                mesajLabel.setText(ex.getMessage());
            }
        });

        p.add(form, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(10, 10));
        bottom.add(mesajLabel, BorderLayout.CENTER);
        bottom.add(girisBtn, BorderLayout.EAST);

        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    // ------------------- UYE OL PANELI -------------------

    private JPanel uyeOlPaneli() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));

        // TC mask: 11 hane, sadece rakam
        JFormattedTextField tcField = new JFormattedTextField(createTcMask());
        tcField.setColumns(11);

        JTextField adSoyadField = new JTextField();
        JPasswordField sifreField = new JPasswordField();

        form.add(new JLabel("TC (11 hane):"));
        form.add(tcField);
        form.add(new JLabel("Ad Soyad:"));
        form.add(adSoyadField);
        form.add(new JLabel("Sifre (min 6):"));
        form.add(sifreField);

        JLabel mesajLabel = new JLabel(" ");
        mesajLabel.setForeground(new Color(200, 0, 0));

        JButton uyeOlBtn = new JButton("Uye Ol");

        uyeOlBtn.addActionListener(e -> {
            mesajLabel.setForeground(new Color(200, 0, 0));
            mesajLabel.setText(" ");

            try {
                String tc = MetinUtil.sadeceRakam(tcField.getText());
                String adSoyad = MetinUtil.titleCase(adSoyadField.getText());
                String sifre = new String(sifreField.getPassword());

                // TC 11 hane
                if (tc.length() != 11) {
                    mesajLabel.setText("TC 11 haneli olmali.");
                    return;
                }

                // ad soyad 2 kelime
                if (adSoyad.isBlank() || adSoyad.split(" ").length < 2) {
                    mesajLabel.setText("Ad Soyad en az 2 kelime olmali.");
                    return;
                }

                // sifre min 6
                MetinUtil.minUzunluk(sifre, 6, "Sifre");

                // UI'da duzeltip gosterelim
                adSoyadField.setText(adSoyad);

                banka.uyeOl(tc, adSoyad, sifre);

                mesajLabel.setForeground(new Color(0, 140, 0));
                mesajLabel.setText("Kayit basarili! Giris Yap sekmesinden giris yapabilirsin.");

            } catch (Exception ex) {
                mesajLabel.setText(ex.getMessage());
            }
        });

        p.add(form, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(10, 10));
        bottom.add(mesajLabel, BorderLayout.CENTER);
        bottom.add(uyeOlBtn, BorderLayout.EAST);

        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    // ------------------- HELPERS -------------------

    private MaskFormatter createTcMask() {
        try {
            MaskFormatter mf = new MaskFormatter("###########");
            mf.setPlaceholderCharacter('_');
            return mf;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public Banka getBanka() {
        return banka;
    }
}