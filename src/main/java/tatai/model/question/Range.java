package tatai.model.question;

import javafx.util.Pair;
import util.NumberConstraint;

/**
 * Specifies generation of a random number between min and max (inclusive)
 */
public class Range implements Generatable {
    public static class Memento {
        public Memento(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public int min;
        public int max;
    }

    public Range(Memento memento) {
        _min = memento.min;
        _max = memento.max;
    }

    /**
     * Produces a number between the minimum and maximum
     */
    @Override
    public Pair<String, Integer> generate(NumberConstraint constraint) {
        int answer = constraint.generate(_min, _max);
        return new Pair<>(Integer.toString(answer), answer);
    }

    public Memento memento() {
        return new Memento(_min, _max);
    }

    private int _min;
    private int _max; // inclusive
}