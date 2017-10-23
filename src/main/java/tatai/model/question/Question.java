package tatai.model.question;

import javafx.util.Pair;
import tatai.model.Translator;
import util.NumberConstraint;

/**
 * A question uses a specification to generate a specific string for the user to answer
 */
public class Question {
    public Question() {
        this(new Range());
    }
    public Question(Generatable question) {
        _question = question;
    }

    /**
     * Generates a question string and waits for answer
     */
    public String       generate() {
        Pair<String, Integer> pair = _question.generate(new NumberConstraint());
        _curAnswer = pair.getValue();
        return pair.getKey();
    }

    public Generatable  head() {
        return _question;
    }

    /**
     * Checks whether submission is correct
     */
    public boolean 	verify(String submission) {
        return submission.equalsIgnoreCase(Translator.convert(_curAnswer));
    }

    private int     _curAnswer;

    private Generatable _question;
}
