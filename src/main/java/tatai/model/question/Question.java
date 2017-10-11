package tatai.model.question;

import javafx.util.Pair;
import tatai.model.Translator;
import util.NumberConstraint;

/**
 * A question uses a specification to generate a specific string for the user to answer
 */
public class Question {
    public Question(Generatable question) {
        _question = question;
        _maxTries = 2;
    }

    /**
     * Generates a question
     */
    public String   generate() {
        _tries = _maxTries;

        Pair<String, Integer> pair = _question.generate(new NumberConstraint());
        _curAnswer = pair.getValue();
        return pair.getKey();
    }

    /**
     * Checks whether submission is correct
     */
    public boolean 	verify(String submission) {
        // TODO: Throw if tries < 0? Doesn't seem important atm
        _tries--;

        return submission.equalsIgnoreCase(Translator.convert(_curAnswer));
    }

    /**
     * Whether or not the question can be re-attempted
     */
    public boolean  hasAnotherTry() {
        return _tries > 0;
    }

    public String   toString() {
        return _question.toString() + " x" + _maxTries;
    }

    private int     _curAnswer;

    private int     _tries;
    private int     _maxTries;

    private Generatable _question;
}
