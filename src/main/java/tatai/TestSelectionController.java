package tatai;

import javafx.scene.layout.VBox;
import util.Views;

public class TestSelectionController extends VBox {
    public TestSelectionController() {
        // Load fxml, set self to act as controller and root
        Views.load("TestSelection", this, this);
    }
}
