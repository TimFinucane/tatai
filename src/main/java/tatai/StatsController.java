package tatai;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
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
        _scores = scoreKeeper.getScores(testName);

        titleLbl.setText("Statistics for " + testName);

        displayScores();
    }


    private void displayScores() {
        double average = 0.0;
        int i;
        int min = _scores[0].score;
        int max = _scores[0].score;
        for(i = 0; i < _scores.length; i++) {
            Label score = new Label(Integer.toString(_scores[i].score));
            Label date = new Label(_scores[i].date.toString());

            score.getStyleClass().add("scores-label");
            date.getStyleClass().add("scores-label");
            dateBox.getChildren().add(date);
            scoreBox.getChildren().add(score);

            if(_scores[i].score >= max)
                max = _scores[i].score;
            if(_scores[i].score <= min)
                min = _scores[i].score;

            average += _scores[i].score;
        }
        average /= i;

        gaugeValue.setMaxValue(12.0);
        gaugeValue.setMinValue(10.0);
        gaugeValue.setValue(average);
    }

    private static          ScoreKeeper.Score[] _scores;

    private ScoreKeeper     _scoreKeeper;
    private String          _testName;

    // JavaFx controls
    @FXML private Gauge     gaugeValue;
    @FXML private VBox      dateBox;
    @FXML private VBox      scoreBox;
    @FXML private VBox      progressBox;
    @FXML private Label     titleLbl;
}
