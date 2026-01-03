package banka;

import banka.service.Banka;
import banka.ui.LoginFrame;
import javax.swing.SwingUtilities;

public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Banka banka = new Banka();
            new LoginFrame(banka).setVisible(true);
        });
    }
}
