package tatai;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class TestSelectionController extends VBox {
    public TestSelectionController() {
        // Load fxml, set self to act as controller and root
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tatai/Info.fxml"));

        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch(IOException e) {
            throw new RuntimeException("Unable to load tatai.Info.fxml: " + e.getMessage());
        }
    }
}
