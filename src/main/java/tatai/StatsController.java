package tatai;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import tatai.model.ScoreKeeper;

import java.util.Calendar;
import java.util.Date;

/**
 * Class which displays the scores for the current test selected
 */
// TODO: Better way of displaying stats. Maybe add some gauges/graphs ??
// TODO: Add other stats such as if the user is better at multiplication than addition etc.
public class StatsController extends Controller {
    public StatsController(ScoreKeeper scoreKeeper, String testName){

        loadFxml("Stats");
        s = testScores();
        displayScores();
    }

    private ScoreKeeper.Score[] testScores() {
        ScoreKeeper scores = new ScoreKeeper("user1");

        scores.addScore("test", 10);
        scores.addScore("test", 12);

        // Now reset and reload the score keeper

        scores = new ScoreKeeper("user1");

        ScoreKeeper.Score[] out = scores.getScores("test");
        return out;

    }

    private void displayScores() {
        Label date = new Label(s[0].date.toString());
        date.getStyleClass().add("-fx-background-color: ff0000");
        date.getStyleClass().add("-fx-font-size: 18");
        Label score = new Label(Integer.toString(s[0].score));
        score.getStyleClass().add("-fx-background-color: ff0000");
        dateBox.getChildren().add(date);
        scoreBox.getChildren().add(score);
    }

    private static ScoreKeeper.Score[] s;

    private ScoreKeeper _scoreKeeper;
    private String      _testName;

    // JavaFx controls
    @FXML private VBox dateBox;
    @FXML private VBox scoreBox;
    @FXML private VBox progressBox;
}
