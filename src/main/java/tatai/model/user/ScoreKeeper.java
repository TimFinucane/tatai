package tatai.model.user;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;

import java.util.Arrays;

/**
 * Gives capability for storing and retrieving scores
 */
public class ScoreKeeper {
    public ScoreKeeper(User user, String testName) {
        _user = user;
        _test = testName;

        updateIndex();

        // Ensure index is always correct
        _index.bind(Bindings.createObjectBinding(this::updateIndex, _user.testScoresProperty()));
    }

    public User.Score[] getScores() {
        return scores().scores;
    }
    public void         addScore(int score) {
        User.TestScores testScores = scores();
        testScores.scores = Arrays.copyOf(testScores.scores, testScores.scores.length + 1);
        testScores.scores[testScores.scores.length - 1] = new User.Score(score);
        _user.testScoresProperty().set(_index.get(), testScores);
    }

    /**
     * Gives the index field the correct index based on the test name
     */
    private int     updateIndex() {
        for(int i = 0; i < _user.testScoresProperty().getSize(); i++) {
            if(_user.testScoresProperty().get(i).test.equals(_test)) {
                return i;
            }
        }

        // At this point one doesn't exist, so create it
        _user.testScoresProperty().add(new User.TestScores(_test));
        return _user.testScoresProperty().getSize() - 1;
    }
    private User.TestScores scores() {
        return _user.testScoresProperty().get(_index.get());
    }

    private User            _user;

    private String          _test;
    private IntegerProperty _index;
}
