package tatai;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import tatai.model.test.Scores;

import java.util.ArrayList;
import java.util.Map;

/**
 * Shows statistics about the session score
 */
public class InfoController extends Controller {
    private static double TITLE_HEIGHT_DIV = 15;
    private static double TITLE_WIDTH_DIV = 9;
    private static double SMALL_HEIGHT_DIV = 24;
    private static double SMALL_WIDTH_DIV = 15;

    /**
     * Creates an Info screen. Once the user is done, notifyFinished will be called
     */
    public InfoController() {
        loadFxml("Info");

        highEasyLbl.setTextFill(Color.GREEN);
        avgEasyLbl.setTextFill(Color.GREEN);
        highHardLbl.setTextFill(Color.BLUE);
        avgHardLbl.setTextFill(Color.BLUE);

        mainMenuBtn.setOnAction((e) -> exit());

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
        Map<String, ArrayList<Integer>> data = Scores.retrieve();

        int easyHigh = 0;
        int easyCount = 0;
        double easySum = 0;

        int hardHigh = 0;
        int hardCount = 0;
        double hardSum = 0;

        for(Map.Entry<String, ArrayList<Integer>> entry : data.entrySet()) {
            if(entry.getKey().equals("Easy Test")) {
                easySum = entry.getValue().stream().mapToInt(Integer::valueOf).sum();
                easyHigh = entry.getValue().stream().mapToInt(Integer::valueOf).max().getAsInt();
                easyCount = entry.getValue().size();
            } else if(entry.getKey().equals("Hard Test")) {
                hardSum = entry.getValue().stream().mapToInt(Integer::valueOf).sum();
                hardHigh = entry.getValue().stream().mapToInt(Integer::valueOf).max().getAsInt();
                hardCount = entry.getValue().size();
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

    @FXML private Label   titleLbl;
    @FXML private Label   infoLbl;
    @FXML private Label   highLbl;
    @FXML private Label   avgLbl;

    @FXML private Label   highEasyLbl;
    @FXML private Label   highHardLbl;
    @FXML private Label   avgEasyLbl;
    @FXML private Label   avgHardLbl;
    @FXML private Button  mainMenuBtn;
}
