package tatai;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class StatsController extends Controller {

    public StatsController(){
        loadFxml("Stats");
    }

//    JavaFx Controls
    @FXML private VBox datesBox;
    @FXML private VBox scoresBox;
    @FXML private VBox progressBox;
}
