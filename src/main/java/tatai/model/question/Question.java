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
    static public class Memento {
        private Memento(String text, int answer, int tries) {
            this.text = text;
            this.answer = answer;
            this.tries = tries;
        }

        private String  text;
        private int     answer;
        private int     tries;
    }

    public Question(TestJson.Question questionInfo) {
        setup(questionInfo);
        generate();
    }
    public Question(TestJson.Question questionInfo, Memento memento) {
        setup(questionInfo);

        _tries = memento.tries;
        _curText = memento.text;
        _curAnswer = memento.answer;
    }

    /***
     * Generates a new text and answer
     */
    public void             generate() {
        _tries = _maxTries;

        Pair<String, Integer> pair = _question.generate(new NumberConstraint(_min, _max));
        _curAnswer = pair.getValue();
        _curText = pair.getKey();
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

    public String           text() {
        return _curText;
    }
    public double           timelimit() {
        return _timelimit;
    }
    public boolean          correct() {
        return _correct;
    }

    public Memento          memento() {
        return new Memento(_curText, _curAnswer, _tries);
    }

    /**
     * The tag property of the head. Will update self on a head switch.
     */
    public ObjectProperty<QuestionPart.Tag> tagProperty() {
        return _headTag;
    }

    /**
     * Switches the question's head to this one
     */
    public void             switchHead(QuestionPart newHead) {
        if(newHead.parent() != null)
            throw new IllegalArgumentException("The new head to switch to must have no parents");

        _headTag.unbind();

        _question = newHead;

        _headTag.bind(_question.tagProperty());
    }

    /**
     * Gets the head generatable of the question
     */
    public QuestionPart     head() {
        return _question;
    }

    private void            setup(TestJson.Question questionInfo) {
        _question = QuestionReader.read(questionInfo.question);
        _maxTries = questionInfo.tries;
        _timelimit = questionInfo.timelimit;

        _max = questionInfo.max;
        _min = questionInfo.min;

        _headTag.bind(_question.tagProperty());
    }

    private boolean         _correct = false;

    private int             _curAnswer;
    private String          _curText = "";
    private int             _tries;

    private ObjectProperty<QuestionPart.Tag> _headTag = new SimpleObjectProperty<>();

    private int             _min;
    private int             _max;
    private double          _timelimit; // Store this parameter just in case, but not using it for now
    private int             _maxTries;
    private QuestionPart    _question;
}
