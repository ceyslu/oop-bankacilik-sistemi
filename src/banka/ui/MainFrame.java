package banka.ui;

import banka.model.Musteri;
import banka.service.Banka;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame(Banka banka, Musteri aktifMusteri) {
        setTitle("Bankacilik Sistemi - " + aktifMusteri.getAdSoyad());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 480);
        setLocationRelativeTo(null);

        JLabel info = new JLabel(
                "Hosgeldin " + aktifMusteri.getAdSoyad()
                        + " | Vadesiz: " + aktifMusteri.getVadesiz().getHesapNo()
                        + " | Tasarruf: " + aktifMusteri.getTasarruf().getHesapNo(),
                SwingConstants.CENTER
        );
        info.setFont(info.getFont().deriveFont(Font.BOLD, 14f));

        JButton aySonuBtn = new JButton("Ay Sonu Calistir (Tum Musteriler)");
        aySonuBtn.addActionListener(e -> {
            banka.aySonuCalistir();
            JOptionPane.showMessageDialog(this, "Ay sonu islemleri calisti. (Gecmisler guncellendi)");
        });

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        root.add(info, BorderLayout.CENTER);
        root.add(aySonuBtn, BorderLayout.SOUTH);

        setContentPane(root);
    }
}
