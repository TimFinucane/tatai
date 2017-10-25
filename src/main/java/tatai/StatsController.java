package tatai;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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


        if(_scores.length > 0) {
            dateBox.getChildren().add(new Label("Date"));
            scoreBox.getChildren().add(new Label("Score"));
            displayScores();
        }
        else
            titleLbl.setText("You have not completed any tests yet!");
    }

    private void displayScores() {

        double average = 0.0;
        int i;
        int min = _scores[0].score;
        int max = _scores[0].score;
        for(i = 0; i < _scores.length; i++) {
            Label score = new Label(Integer.toString(_scores[i].score) );
            Label date = new Label(_scores[i].date.toString());

            score.getStyleClass().add(CSS_CLASS);
            date.getStyleClass().add(CSS_CLASS);
            dateBox.getChildren().add(date);
            scoreBox.getChildren().add(score);

            if(_scores[i].score >= max)
                max = _scores[i].score;
            if(_scores[i].score <= min)
                min = _scores[i].score;

            average += ((_scores[i].score)*1000)/1000;
        }
        average /= i;

        gaugeValue.valueColorProperty().setValue(Color.valueOf(COLOUR_VALUE));
        gaugeValue.setMaxValue(max);
        gaugeValue.setMinValue(min);
        gaugeValue.setValue(average);
    }

    private static final String CSS_CLASS =     "scores-label";
    private static final String COLOUR_VALUE =  "#22A7F0";

    private static ScoreKeeper.Score[]  _scores;

    private ScoreKeeper     _scoreKeeper;
    private String          _testName;

    // JavaFx controls
    @FXML private Gauge     gaugeValue;
    @FXML private VBox      dateBox;
    @FXML private VBox      scoreBox;
    @FXML private VBox      progressBox;
    @FXML private Label     titleLbl;
}
