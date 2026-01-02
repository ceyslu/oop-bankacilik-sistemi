package banka.ui;

import banka.model.Musteri;
import banka.service.Banka;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class MainFrame extends JFrame {

    private final Banka banka;
    private final Musteri musteri;

    private final JPanel contentPanel = new JPanel(new CardLayout());
    private JLabel lblBilgi;

    public MainFrame(Banka banka, Musteri musteri) {
        this.banka = banka;
        this.musteri = musteri;

        setTitle("Bankacılık Sistemi");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(ustPanel(), BorderLayout.NORTH);
        add(solMenu(), BorderLayout.WEST);
        add(icerikPaneli(), BorderLayout.CENTER);

        guncelleUstBilgi();
    }

    /* ===================== ÜST PANEL ===================== */

    private JPanel ustPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        panel.setBackground(new Color(220, 245, 230));

        lblBilgi = new JLabel();
        lblBilgi.setFont(new Font("Arial", Font.BOLD, 13));

        JButton btnGeri = new JButton("< Geri");
        btnGeri.setFocusPainted(false);
        btnGeri.addActionListener(e -> {
            dispose();
            new LoginFrame(banka).setVisible(true);
        });

        panel.add(lblBilgi, BorderLayout.WEST);
        panel.add(btnGeri, BorderLayout.EAST);
        return panel;
    }

    private void guncelleUstBilgi() {
        lblBilgi.setText(
                "Vadesiz: " + musteri.getVadesiz().getHesapNo() +
                        " | Bakiye: " + musteri.getVadesiz().getBakiye() + " TL"
        );
    }

    /* ===================== SOL MENÜ ===================== */

    private JPanel solMenu() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 6, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(200, 235, 210));

        JButton btnTransfer = menuButon("Transfer (Hesaplarım)");
        JButton btnDiger = menuButon("Transfer (Başka Kişi)");
        JButton btnAltin = menuButon("Altın Al");
        JButton btnGecmis = menuButon("İşlem Geçmişi");

        btnTransfer.addActionListener(e -> panelGoster("TRANSFER"));
        btnDiger.addActionListener(e -> panelGoster("DIGER"));
        btnAltin.addActionListener(e -> panelGoster("ALTIN"));
        btnGecmis.addActionListener(e -> panelGoster("GECMIS"));

        panel.add(btnTransfer);
        panel.add(btnDiger);
        panel.add(btnAltin);
        panel.add(btnGecmis);

        return panel;
    }

    private JButton menuButon(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        return btn;
    }

    /* ===================== İÇERİK PANELİ ===================== */

    private JPanel icerikPaneli() {
        contentPanel.add(bosPanel("Bir işlem seçiniz"), "BOS");
        contentPanel.add(transferPanel(), "TRANSFER");
        contentPanel.add(digerTransferPanel(), "DIGER");
        contentPanel.add(altinPanel(), "ALTIN");
        contentPanel.add(gecmisPanel(), "GECMIS");

        panelGoster("BOS");
        return contentPanel;
    }

    private void panelGoster(String name) {
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, name);
    }

    /* ===================== PANELLER ===================== */

    private JPanel transferPanel() {
        JPanel panel = formPanel("Hesaplarım Arası Transfer");

        JComboBox<String> cmbAlici = new JComboBox<>(new String[]{
                "Tasarruf Hesabı", "Vadesiz Hesap"
        });
        JTextField txtTutar = new JTextField(10);

        JButton btn = new JButton("Transfer Yap");
        btn.addActionListener(e -> {
            try {
                BigDecimal tutar = new BigDecimal(txtTutar.getText());
                boolean vadesizdenTasarrufa =
                        cmbAlici.getSelectedItem().toString().contains("Tasarruf");

                banka.kendiHesaplarimArasiTransfer(musteri, vadesizdenTasarrufa, tutar);
                guncelleUstBilgi();
                JOptionPane.showMessageDialog(this, "Transfer başarılı");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(new JLabel("Alıcı Hesap:"));
        panel.add(cmbAlici);
        panel.add(new JLabel("Tutar (TL):"));
        panel.add(txtTutar);
        panel.add(new JLabel());
        panel.add(btn);

        return panel;
    }

    private JPanel digerTransferPanel() {
        JPanel panel = formPanel("Başka Kişiye Transfer");

        JTextField txtHesapNo = new JTextField(12);
        JTextField txtAd = new JTextField(12);
        JTextField txtTutar = new JTextField(10);

        JButton btn = new JButton("Transfer Yap");
        btn.addActionListener(e -> {
            try {
                BigDecimal tutar = new BigDecimal(txtTutar.getText());

                banka.transferYap(
                        musteri,
                        txtHesapNo.getText(),
                        txtAd.getText(),
                        tutar
                );
                guncelleUstBilgi();
                JOptionPane.showMessageDialog(this, "Transfer başarılı");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(new JLabel("Alıcı Hesap No:"));
        panel.add(txtHesapNo);
        panel.add(new JLabel("Ad Soyad:"));
        panel.add(txtAd);
        panel.add(new JLabel("Tutar (TL):"));
        panel.add(txtTutar);
        panel.add(new JLabel());
        panel.add(btn);

        return panel;
    }

    private JPanel altinPanel() {
        JPanel panel = formPanel("Altın Alım");

        JTextField txtTl = new JTextField(10);
        JTextField txtGram = new JTextField(10);

        JButton btn = new JButton("Altın Al");
        btn.addActionListener(e -> {
            try {
                banka.altinAl(
                        musteri,
                        new BigDecimal(txtTl.getText()),
                        new BigDecimal(txtGram.getText())
                );
                guncelleUstBilgi();
                JOptionPane.showMessageDialog(this, "Altın alımı başarılı");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(new JLabel("TL Tutarı:"));
        panel.add(txtTl);
        panel.add(new JLabel("Gram Fiyatı (TL):"));
        panel.add(txtGram);
        panel.add(new JLabel());
        panel.add(btn);

        return panel;
    }

    private JPanel gecmisPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea area = new JTextArea("İşlem geçmişi burada listelenecek...");
        area.setEditable(false);

        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    private JPanel bosPanel(String text) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.add(new JLabel(text));
        return panel;
    }

    private JPanel formPanel(String title) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 6, 6));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }
}
