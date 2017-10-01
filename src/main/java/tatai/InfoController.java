package tatai;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.List;

/**
 * Shows statistics about the session score
 */
public class InfoController extends Region {
    /**
     * Creates an Info screen. Once the user is done, notifyFinished will be called
     */
    public InfoController(Runnable notifyFinished) {
        // Load fxml, set self to act as controller and root
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tatai/Info.fxml"));

        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch(IOException e) {
            throw new RuntimeException("Unable to load tatai.Info.fxml: " + e.getMessage());
        }

        highEasyLbl.setTextFill(Color.GREEN);
        avgEasyLbl.setTextFill(Color.GREEN);
        highHardLbl.setTextFill(Color.BLUE);
        avgHardLbl.setTextFill(Color.BLUE);

        mainMenuBtn.setOnAction(() -> _notifyFinished.run());

        loadData();
    }

    private void loadData() {
        // TODO:
    }

    private Runnable _notifyFinished;

    @FXML
    Label   highEasyLbl;
    @FXML
    Label   highHardLbl;
    @FXML
    Label   avgEasyLbl;
    @FXML
    Label   avgHardLbl;
    @FXML
    Button  mainMenuBtn;
}
