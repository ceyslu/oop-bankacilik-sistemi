package banka.ui;

import banka.islem.Islem;
import banka.model.Musteri;
import banka.service.Banka;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class MainFrame extends JFrame {

    private final Banka banka;
    private final Musteri aktif;

    // ekran yenileme icin label/listeler
    private JLabel vadesizBilgi;
    private JLabel tasarrufBilgi;

    private DefaultListModel<String> vadesizListModel = new DefaultListModel<>();
    private DefaultListModel<String> tasarrufListModel = new DefaultListModel<>();

    public MainFrame(Banka banka, Musteri aktifMusteri) {
        this.banka = banka;
        this.aktif = aktifMusteri;

        setTitle("Bankacilik Sistemi - " + aktif.getAdSoyad());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(820, 540);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Vadesiz", vadesizPaneli());
        tabs.addTab("Tasarruf", tasarrufPaneli());
        tabs.addTab("Gecmis", gecmisPaneli());
        tabs.addTab("Ay Sonu", aySonuPaneli());

        setContentPane(tabs);

        ekranGuncelle();
    }

    // -------------------- PANELLER --------------------

    private JPanel vadesizPaneli() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        vadesizBilgi = new JLabel("", SwingConstants.CENTER);
        vadesizBilgi.setFont(vadesizBilgi.getFont().deriveFont(Font.BOLD, 14f));
        root.add(vadesizBilgi, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 1, 10, 10));

        // Para Yatir
        form.add(kart("Para Yatir", panelParaYatir(true)));

        // Para Cek
        form.add(kart("Para Cek", panelParaCek()));

        // Transfer
        form.add(kart("Transfer (Vadesiz -> Baska Musterinin Vadesizi)", panelTransfer()));

        root.add(form, BorderLayout.CENTER);
        return root;
    }

    private JPanel tasarrufPaneli() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        tasarrufBilgi = new JLabel("", SwingConstants.CENTER);
        tasarrufBilgi.setFont(tasarrufBilgi.getFont().deriveFont(Font.BOLD, 14f));
        root.add(tasarrufBilgi, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 1, 10, 10));

        // Para Yatir (Tasarruf)
        form.add(kart("Tasarrufa Para Yatir", panelParaYatir(false)));

        // Altin Al
        form.add(kart("Altin Al (Tasarruf)", panelAltinAl()));

        root.add(form, BorderLayout.CENTER);
        return root;
    }

    private JPanel gecmisPaneli() {
        JPanel root = new JPanel(new GridLayout(1, 2, 10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JList<String> vList = new JList<>(vadesizListModel);
        JList<String> tList = new JList<>(tasarrufListModel);

        root.add(kartScroll("Vadesiz Islem Gecmisi", vList));
        root.add(kartScroll("Tasarruf Islem Gecmisi", tList));

        return root;
    }

    private JPanel aySonuPaneli() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel info = new JLabel("Ay sonu islemleri tum musteriler icin calisir (polimorfizm: aySonuIslemleri())",
                SwingConstants.CENTER);

        JButton btn = new JButton("Ay Sonu Calistir");
        btn.addActionListener(e -> {
            try {
                banka.aySonuCalistir();
                ekranGuncelle();
                JOptionPane.showMessageDialog(this, "Ay sonu islemleri tamamlandi.");
            } catch (Exception ex) {
                hata(ex);
            }
        });

        root.add(info, BorderLayout.CENTER);
        root.add(btn, BorderLayout.SOUTH);
        return root;
    }

    // -------------------- ALT PANEL PARCALARI --------------------

    private JPanel panelParaYatir(boolean vadesizMi) {
        JPanel p = new JPanel(new GridLayout(1, 0, 10, 10));

        JTextField tutarField = new JTextField();
        JButton btn = new JButton("Yatir");

        btn.addActionListener(e -> {
            try {
                BigDecimal tutar = parseTutar(tutarField.getText());
                if (vadesizMi) {
                    banka.vadesizParaYatir(aktif, tutar, "Manuel yatirim");
                } else {
                    banka.tasarrufParaYatir(aktif, tutar, "Manuel yatirim");
                }
                tutarField.setText("");
                ekranGuncelle();
                JOptionPane.showMessageDialog(this, "Para yatirma basarili.");
            } catch (Exception ex) {
                hata(ex);
            }
        });

        p.add(new JLabel("Tutar (TL):"));
        p.add(tutarField);
        p.add(btn);
        return p;
    }

    private JPanel panelParaCek() {
        JPanel p = new JPanel(new GridLayout(1, 0, 10, 10));

        JTextField tutarField = new JTextField();
        JButton btn = new JButton("Cek");

        btn.addActionListener(e -> {
            try {
                BigDecimal tutar = parseTutar(tutarField.getText());
                banka.vadesizParaCek(aktif, tutar);
                tutarField.setText("");
                ekranGuncelle();
                JOptionPane.showMessageDialog(this, "Para cekme basarili.");
            } catch (Exception ex) {
                hata(ex);
            }
        });

        p.add(new JLabel("Tutar (TL):"));
        p.add(tutarField);
        p.add(btn);
        return p;
    }

    private JPanel panelTransfer() {
        JPanel p = new JPanel(new GridLayout(2, 0, 10, 10));

        JTextField aliciAdSoyadField = new JTextField();
        JTextField tutarField = new JTextField();
        JButton btn = new JButton("Gonder");

        btn.addActionListener(e -> {
            try {
                String alici = aliciAdSoyadField.getText().trim();
                BigDecimal tutar = parseTutar(tutarField.getText());
                banka.transferYap(aktif, alici, tutar);

                aliciAdSoyadField.setText("");
                tutarField.setText("");
                ekranGuncelle();
                JOptionPane.showMessageDialog(this, "Transfer basarili.");
            } catch (Exception ex) {
                hata(ex);
            }
        });

        p.add(new JLabel("Alici Ad Soyad:"));
        p.add(aliciAdSoyadField);
        p.add(new JLabel("Tutar (TL):"));
        p.add(tutarField);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(btn, BorderLayout.EAST);

        JPanel container = new JPanel(new BorderLayout(10, 10));
        container.add(p, BorderLayout.CENTER);
        container.add(bottom, BorderLayout.SOUTH);

        return container;
    }

    private JPanel panelAltinAl() {
        JPanel p = new JPanel(new GridLayout(2, 0, 10, 10));

        JTextField tlField = new JTextField();
        JTextField gramFiyatField = new JTextField();
        JButton btn = new JButton("Altin Al");

        btn.addActionListener(e -> {
            try {
                BigDecimal tl = parseTutar(tlField.getText());
                BigDecimal gramFiyat = parseTutar(gramFiyatField.getText());
                banka.altinAl(aktif, tl, gramFiyat);

                tlField.setText("");
                gramFiyatField.setText("");
                ekranGuncelle();
                JOptionPane.showMessageDialog(this, "Altin alim basarili.");
            } catch (Exception ex) {
                hata(ex);
            }
        });

        p.add(new JLabel("TL Tutar:"));
        p.add(tlField);
        p.add(new JLabel("Gram Fiyat (TL):"));
        p.add(gramFiyatField);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(btn, BorderLayout.EAST);

        JPanel container = new JPanel(new BorderLayout(10, 10));
        container.add(p, BorderLayout.CENTER);
        container.add(bottom, BorderLayout.SOUTH);

        return container;
    }

    // -------------------- YARDIMCILAR --------------------

    private void ekranGuncelle() {
        vadesizBilgi.setText(
                "VADESIZ | No: " + aktif.getVadesiz().getHesapNo() +
                        " | Bakiye: " + aktif.getVadesiz().getBakiye() + " TL"
        );

        tasarrufBilgi.setText(
                "TASARRUF | No: " + aktif.getTasarruf().getHesapNo() +
                        " | TL: " + aktif.getTasarruf().getBakiye() +
                        " | Altin: " + aktif.getTasarruf().getAltinGram() + " gr"
        );

        vadesizListModel.clear();
        for (Islem i : aktif.getVadesizGecmis().tumu()) {
            vadesizListModel.addElement(i.ozet());
        }

        tasarrufListModel.clear();
        for (Islem i : aktif.getTasarrufGecmis().tumu()) {
            tasarrufListModel.addElement(i.ozet());
        }
    }

    private BigDecimal parseTutar(String s) {
        try {
            // 12,5 yazarsan 12.5 gibi olsun diye
            s = s.trim().replace(',', '.');
            BigDecimal bd = new BigDecimal(s);
            if (bd.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Tutar pozitif olmali.");
            return bd;
        } catch (Exception e) {
            throw new IllegalArgumentException("Gecersiz tutar.");
        }
    }

    private JPanel kart(String baslik, JComponent icerik) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createTitledBorder(baslik));
        card.add(icerik, BorderLayout.CENTER);
        return card;
    }

    private JPanel kartScroll(String baslik, JComponent icerik) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createTitledBorder(baslik));
        card.add(new JScrollPane(icerik), BorderLayout.CENTER);
        return card;
    }

    private void hata(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
    }
}
