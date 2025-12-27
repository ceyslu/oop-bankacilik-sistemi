import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class GirisEkrani {

    @FXML
    private TextField tcField;

    @FXML
    private PasswordField sifreField;

    @FXML
    private Label mesajLabel;

    @FXML
    public void girisYap() {
        String tc = tcField.getText();
        String sifre = sifreField.getText();

        Musteri musteri = BankaVeritabani.login(tc, sifre);

        if (musteri != null) {
            mesajLabel.setStyle("-fx-text-fill:green;");
            mesajLabel.setText("Giriş başarılı!");
            // BURADA SONRA AnaEkran'a geçeceğiz
        } else {
            mesajLabel.setStyle("-fx-text-fill:red;");
            mesajLabel.setText("TC veya şifre yanlış!");
        }
    }

    @FXML
    public void uyeOl() {
        String tc = tcField.getText();
        String sifre = sifreField.getText();

        boolean basarili = BankaVeritabani.register(tc, sifre);

        if (basarili) {
            mesajLabel.setStyle("-fx-text-fill:green;");
            mesajLabel.setText("Üyelik başarılı! (1000 TL yüklendi)");
        } else {
            mesajLabel.setStyle("-fx-text-fill:red;");
            mesajLabel.setText("Bu TC ile zaten kayıt var!");
        }
    }
}
