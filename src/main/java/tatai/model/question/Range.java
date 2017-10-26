package tatai.model.question;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Pair;
import util.NumberConstraint;

import javax.annotation.Nonnull;

/**
 * Specifies generation of a random number between min and max (inclusive)
 */
public class Range extends QuestionPart {
    private class TagBinding extends ObjectBinding<Tag> {
        public Tag computeValue() {
            return generateTag();
        }

        void rebind() {
            getDependencies().clear();
            bind(_min, _max);
        }
    }

    public Range() {
        this(1, 9);
    }
    Range(int min, int max) {
        _min.setValue(min);
        _max.setValue(max);

        TagBinding binding = new TagBinding();
        binding.rebind();
        bindPart(binding);
    }

    /**
     * Produces a number between the minimum and maximum
     */
    @Override
    public Pair<String, Integer> generate(NumberConstraint constraint) throws NumberConstraint.ConstraintException {
        int answer = constraint.generate(_min.getValue(), _max.getValue());
        return new Pair<>(Integer.toString(answer), answer);
    }

    @Nonnull
    private Tag generateTag() {
        return new Tag(this,"(" + Integer.toString(_min.get()) + " to " + Integer.toString(_max.get()) + ")");
    }

    public IntegerProperty  minProperty() {
        return _min;
    }
    public IntegerProperty  maxProperty() {
        return _max;
    }

    private IntegerProperty _min = new SimpleIntegerProperty();
    private IntegerProperty _max = new SimpleIntegerProperty(); // inclusive
}