

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GirisEkrani extends JFrame {
    JTextField tcAlan;
    JPasswordField sifreAlan;

    public GirisEkrani() {
        setTitle("Bankacılık Sistemi - Giriş");
        setSize(350, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1, 10, 10)); // Biraz boşluklu düzen

        // Panel 1: TC Girişi
        JPanel p1 = new JPanel();
        p1.add(new JLabel("TC Kimlik No:"));
        tcAlan = new JTextField(15);
        p1.add(tcAlan);
        add(p1);

        // Panel 2: Şifre Girişi
        JPanel p2 = new JPanel();
        p2.add(new JLabel("Şifre:"));
        sifreAlan = new JPasswordField(15);
        p2.add(sifreAlan);
        add(p2);

        // Butonlar
        JButton girisBtn = new JButton("GİRİŞ YAP");
        JButton kayitBtn = new JButton("YENİ HESAP AÇ (ÜYE OL)");
        
        // Butonları panele ekle
        JPanel p3 = new JPanel();
        p3.add(girisBtn);
        add(p3);
        
        JPanel p4 = new JPanel();
        p4.add(kayitBtn);
        add(p4);

        // --- GİRİŞ BUTONU ---
        girisBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String tc = tcAlan.getText();
                String sifre = new String(sifreAlan.getPassword());
                
                boolean bulundu = false;
                for(Musteri m : BankaVeritabani.musteriler) {
                    if(m.getTcNo().equals(tc) && m.getSifre().equals(sifre)) {
                        BankaVeritabani.aktifKullanici = m;
                        JOptionPane.showMessageDialog(null, "Hoşgeldin " + m.getAdSoyad());
                        bulundu = true;
                        
                        // Ana Ekranı Aç ve burayı kapat
                        new AnaEkran(m);
                        dispose(); 
                        break;
                    }
                }
                if(!bulundu) {
                    JOptionPane.showMessageDialog(null, "Hatalı TC veya Şifre! Önce kayıt olun.");
                }
            }
        });

        // --- KAYIT OL (ÜYE OL) BUTONU ---
        kayitBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Kayıt formunu aç
                String adSoyad = JOptionPane.showInputDialog("Adınız Soyadınız:");
                if(adSoyad == null || adSoyad.isEmpty()) return; // İptal ederse çık

                String yeniTc = JOptionPane.showInputDialog("TC Kimlik No Belirleyin:");
                if(yeniTc == null || yeniTc.isEmpty()) return;

                String yeniSifre = JOptionPane.showInputDialog("Şifre Belirleyin:");
                if(yeniSifre == null || yeniSifre.isEmpty()) return;

                // 1. Yeni Müşteri Oluştur
                Musteri yeniMusteri = new Musteri(adSoyad, yeniTc, yeniSifre);
                
                // 2. Müşteriye Otomatik Vadesiz Hesap Aç (Bakiyesi 0 TL olsun)
                yeniMusteri.hesapEkle(new VadesizHesap(adSoyad, yeniTc + "-1", 0));
                
                // 3. Müşteriye Otomatik Yatırım Hesabı Aç (Altın alabilsin diye)
                yeniMusteri.hesapEkle(new YatirimHesabi(adSoyad, yeniTc + "-YAT", 0, "ALTIN", 30.0));

                // 4. Veritabanına Kaydet
                BankaVeritabani.musteriler.add(yeniMusteri);

                JOptionPane.showMessageDialog(null, "Kayıt Başarılı! Şimdi giriş yapabilirsiniz.");
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new GirisEkrani();
    }
}