package tatai.model.question;

import javafx.util.Pair;
import util.NumberConstraint;

class Range implements Generatable {
    public Range(int min, int max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Produces a _random number between the minimum and maximum
     */
    @Override
    public Pair<String, Integer> generate(NumberConstraint constraint) {
        int answer = constraint.generate(min, max);
        return new Pair<>(Integer.toString(answer), answer);
    }

    @Override
    public String toString() {
        return "(" + min + " to " + max + ")";
    }

    private int min;
    private int max; // inclusive
}