package tatai;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import tatai.model.user.ScoreKeeper;
import tatai.model.user.User;

/**
 * Class which displays the scores for the current test selected
 */
// TODO: Better way of displaying stats. Maybe add some gauges/graphs ??
// TODO: Add other stats such as if the user is better at multiplication than addition etc.
public class StatsController extends Controller {
    public StatsController(ScoreKeeper scoreKeeper){
        dateBox.getStylesheets().add("/tatai/stylesheets/DarkMode.css");
        scoreBox.getStylesheets().add("/tatai/stylesheets/DarkMode.css");

        _scoreKeeper = scoreKeeper;

        loadFxml("Stats");

        displayScores();
    }

    private void displayScores() {
        User.Score[] scores = _scoreKeeper.getScores();
        for(User.Score curScore : scores) {
            Label date = new Label(curScore.date.toString());
            Label score = new Label(Integer.toString(curScore.score));
            dateBox.getChildren().add(date);
            scoreBox.getChildren().add(score);
        }
    }

    private ScoreKeeper _scoreKeeper;

    // JavaFx controls
    @FXML private VBox dateBox;
    @FXML private VBox scoreBox;
    @FXML private VBox progressBox;
}
