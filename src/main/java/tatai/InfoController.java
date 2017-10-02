package tatai;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import tatai.model.Test;

import java.io.IOException;
import java.util.List;

/**
 * Shows statistics about the session score
 */
public class InfoController extends VBox {
    private static double TITLE_HEIGHT_DIV = 15;
    private static double TITLE_WIDTH_DIV = 9;
    private static double SMALL_HEIGHT_DIV = 24;
    private static double SMALL_WIDTH_DIV = 15;

    /**
     * Creates an Info screen. Once the user is done, notifyFinished will be called
     */
    public InfoController(Runnable notifyFinished) {
        _notifyFinished = notifyFinished;

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

        mainMenuBtn.setOnAction((e) -> _notifyFinished.run());

        loadData();
    }

    /**
     * Override normal resize to also scale elements
     */
    @Override
    public void resize(double width, double height) {
        super.resize(width, height);

        double titleSize = Math.min(getHeight() / TITLE_HEIGHT_DIV, getWidth() / TITLE_WIDTH_DIV);
        double smallSize = Math.min(getHeight() / SMALL_HEIGHT_DIV, getWidth() / SMALL_WIDTH_DIV);

        titleLbl.setStyle("-fx-font-size: " + titleSize);

        infoLbl.setStyle("-fx-font-size: " + smallSize);
        highLbl.setStyle("-fx-font-size: " + smallSize);
        avgLbl.setStyle("-fx-font-size: " + smallSize);
        highEasyLbl.setStyle("-fx-font-size: " + smallSize);
        highHardLbl.setStyle("-fx-font-size: " + smallSize);
        avgEasyLbl.setStyle("-fx-font-size: " + smallSize);
        avgHardLbl.setStyle("-fx-font-size: " + smallSize);
    }

    private void loadData() {
        List<Test.Stat> data = Test.retrieveScores();

        int easyHigh = 0;
        int easyCount = 0;
        double easySum = 0;

        int hardHigh = 0;
        int hardCount = 0;
        double hardSum = 0;

        for(Test.Stat stat : data) {
            if(stat.type.equals("Easy Test")) {
                easyCount++;
                easySum += stat.score;
                easyHigh = stat.score > easyHigh ? stat.score : easyHigh;
            } else {
                hardCount++;
                hardSum += stat.score;
                hardHigh = stat.score > hardHigh ? stat.score : hardHigh;
            }
        }

        highEasyLbl.setText(String.valueOf(easyHigh));
        highHardLbl.setText(String.valueOf(hardHigh));

        if(easyCount != 0) {
            avgEasyLbl.setText(String.valueOf(easySum/easyCount));
        } else {
            avgEasyLbl.setText("0");
        }
        if(hardCount != 0) {
            avgHardLbl.setText(String.valueOf(hardSum/hardCount));
        } else {
            avgHardLbl.setText("0");
        }

    }

    private Runnable _notifyFinished;

    @FXML
    private Label   titleLbl;
    @FXML
    private Label   infoLbl;
    @FXML
    private Label   highLbl;
    @FXML
    private Label   avgLbl;

    @FXML
    private Label   highEasyLbl;
    @FXML
    private Label   highHardLbl;
    @FXML
    private Label   avgEasyLbl;
    @FXML
    private Label   avgHardLbl;
    @FXML
    private Button  mainMenuBtn;
}
