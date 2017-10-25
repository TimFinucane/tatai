package tatai;

import eu.hansolo.medusa.Gauge;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import tatai.model.user.ScoreKeeper;
import tatai.model.user.User;

/**
 * Class which displays the scores for the current test selected
 */
public class StatsController extends Controller {

    public StatsController(ScoreKeeper scoreKeeper){
        loadFxml("Stats");
        _scores = scoreKeeper.getScores();

        titleLbl.setText("Statistics for ");


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

    private static final String COLOUR_VALUE =  "#22A7F0";
    private static User.Score[]  _scores;

    // JavaFx controls
    @FXML private Gauge     gaugeValue;
    @FXML private VBox      dateBox;
    @FXML private VBox      scoreBox;
    @FXML private VBox      progressBox;
    @FXML private Label     titleLbl;
}
