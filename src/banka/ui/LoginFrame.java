package banka.ui;

import banka.model.Musteri;
import banka.service.Banka;
import java.awt.*;
import javax.swing.*;

public class LoginFrame extends JFrame {

    private final Banka banka = new Banka();

    public LoginFrame() {
        setTitle("Bankacilik Sistemi - Giris");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 420);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Giris Yap", girisPaneli());
        tabs.addTab("Uye Ol", uyeOlPaneli());

        add(tabs);
    }

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

        JButton girisBtn = new JButton("Giris");
        JLabel sonuc = new JLabel(" ");

        girisBtn.addActionListener(e -> {
            try {
                String adSoyad = adSoyadField.getText();
                String sifre = new String(sifreField.getPassword());

                Musteri m = banka.girisYap(adSoyad, sifre);
                sonuc.setText("Giris basarili: " + m.getAdSoyad());

                // Sonraki adimda: MainFrame acacagiz
                JOptionPane.showMessageDialog(this, "Hosgeldin, " + m.getAdSoyad());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        p.add(form, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(girisBtn, BorderLayout.EAST);
        bottom.add(sonuc, BorderLayout.CENTER);

        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private JPanel uyeOlPaneli() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));

        JTextField tcField = new JTextField();
        JTextField adSoyadField = new JTextField();
        JPasswordField sifreField = new JPasswordField();

        form.add(new JLabel("TC:"));
        form.add(tcField);
        form.add(new JLabel("Ad Soyad:"));
        form.add(adSoyadField);
        form.add(new JLabel("Sifre:"));
        form.add(sifreField);

        JButton uyeOlBtn = new JButton("Uye Ol");
        JLabel sonuc = new JLabel(" ");

        uyeOlBtn.addActionListener(e -> {
            try {
                String tc = tcField.getText();
                String adSoyad = adSoyadField.getText();
                String sifre = new String(sifreField.getPassword());

                Musteri m = banka.uyeOl(tc, adSoyad, sifre);
                sonuc.setText("Kayit basarili: " + m.getAdSoyad());

                JOptionPane.showMessageDialog(this, "Kayit olustu! Simdi Giris Yap sekmesinden giris yapabilirsin.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        p.add(form, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(uyeOlBtn, BorderLayout.EAST);
        bottom.add(sonuc, BorderLayout.CENTER);

        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }
}
