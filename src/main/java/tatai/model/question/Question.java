package tatai.model.question;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Pair;
import tatai.model.Translator;
import tatai.model.test.TestJson;
import util.NumberConstraint;

/**
 * A question uses a specification to generate a specific string for the user to answer
 */
public class Question {
    public Question(TestJson.Question questionInfo) {
        _question = QuestionReader.read(questionInfo.question);
        _maxTries = questionInfo.tries;
        _tries = _maxTries;
        _timelimit = questionInfo.timelimit;

        _headTag.bind(_question.tagProperty());
    }

    /**
     * Generates a question string and waits for answer
     */
    public String           generate() {
        Pair<String, Integer> pair = _question.generate(new NumberConstraint());
        _curAnswer = pair.getValue();
        return pair.getKey();
    }

    /**
     * Compares the users answer to the expected result.
     * @param submission the user's response
     * @return true if the answer is equal to the expected result.
     */
    public boolean 	        tryAnswer(String submission) {
        if(_tries-- == 0)
            throw new IllegalStateException("Can't try to answer this question, you have no tries left!");

        _correct = submission.equalsIgnoreCase(Translator.convert(_curAnswer));
        return _correct;
    }

    /**
     * Returns whether or not the user has more tries
     * @return true if they have more tries, false if they don't
     */
    public boolean          hasAnotherTry() {
        return _tries != 0;
    }

    public double           timelimit() {
        return _timelimit;
    }

    public void             reset() {
        _tries = _maxTries;
    }

    public boolean          correct() {
        return _correct;
    }

    /**
     * The tag property of the head. Will update self on a head switch.
     */
    public ObjectProperty<Generatable.Tag> tagProperty() {
        return _headTag;
    }

    /**
     * Switches the question's head to this one
     */
    public void             switchHead(Generatable newHead) {
        if(newHead.parent() != null)
            throw new IllegalArgumentException("The new head to switch to must have no parents");

        _headTag.unbind();

        _question = newHead;

        _headTag.bind(_question.tagProperty());
    }

    /**
     * Gets the head generatable of the question
     */
    public Generatable      head() {
        return _question;
    }

    private boolean         _correct = false;

    private double          _timelimit;
    private int             _tries;

    private int             _maxTries;

    private ObjectProperty<Generatable.Tag> _headTag = new SimpleObjectProperty<>();
    private int                             _curAnswer;
    private Generatable                     _question;
}
