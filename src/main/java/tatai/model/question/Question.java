package tatai.model.question;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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

    public Generatable      head() {
        return _question;
    }

    public ObjectProperty<Generatable.Tag>  headTagProperty() {
        return _headTag;
    }

    public void             switchHead(Generatable newHead) {
        if(newHead.parent() != null)
            throw new IllegalArgumentException("The new head to switch to must have no parents");

        _headTag.unbind();

        _question = newHead;

        _headTag.bind(_question.tagProperty());
    }

    /**
     * Checks whether submission is correct
     */
    public boolean 	    verify(String submission) {
        return submission.equalsIgnoreCase(Translator.convert(_curAnswer));
    }

    private ObjectProperty<Generatable.Tag> _headTag = new SimpleObjectProperty<>();
    private int                             _curAnswer;
    private Generatable                     _question;
}
