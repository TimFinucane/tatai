package tatai;

import com.jfoenix.controls.JFXProgressBar;
import eu.hansolo.medusa.Gauge;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import tatai.model.test.TestJson;
import tatai.model.user.ScoreKeeper;
import tatai.model.user.User;

/**
 * Class which displays the scores for the current test selected
 */
public class StatsController extends Controller {

    public StatsController(ScoreKeeper scoreKeeper, TestJson test){
        loadFxml("Stats");
        _test = test;
        _scores = scoreKeeper.getScores();

        displayScores();
    }

    /**
     * This method creates the score labels to be displayed.
     */
    private void displayScores() {

        // Gets the number of rounds in the selected test.
        int maxQuestions = 0;
        for(TestJson.Question q : _test.questions)
            maxQuestions += q.rounds;

        dateBox.getChildren().add(new Label("Date"));
        scoreBox.getChildren().add(new Label("Score /" + maxQuestions ));
        progressBox.getChildren().add(new Label("Result"));

        // Creates labels to show the user their score
        double average = 0.0;
        int i;
        int min = _scores[0].score;
        int max = _scores[0].score;
        for(i = 0; i < _scores.length; i++) {

            Label score = new Label(Integer.toString(_scores[i].score) );
            Label date = new Label(_scores[i].date.toString());

            JFXProgressBar bar = new JFXProgressBar();
            bar.setProgress((double)_scores[i].score/maxQuestions);

            progressBox.getChildren().add(bar);
            dateBox.getChildren().add(date);
            scoreBox.getChildren().add(score);

            if(_scores[i].score >= max)
                max = _scores[i].score;
            if(_scores[i].score <= min)
                min = _scores[i].score;

            average += ((_scores[i].score)*1000)/1000; // Simple way to display 2dp.
        }
        average /= i;

        gaugeValue.valueColorProperty().setValue(Color.valueOf(COLOUR_VALUE));
        gaugeValue.setMaxValue(max);
        gaugeValue.setMinValue(min);
        gaugeValue.setValue(average);
    }

    private static final String COLOUR_VALUE =  "#22A7F0";
    private static User.Score[]  _scores;
    private TestJson _test;

    // JavaFx controls
    @FXML private Gauge     gaugeValue;
    @FXML private VBox      dateBox;
    @FXML private VBox      scoreBox;
    @FXML private VBox      progressBox;
}
