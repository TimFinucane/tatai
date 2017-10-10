package tatai.model.question;

import javafx.util.Pair;
import util.NumberGenerator;

class Range extends Generator {
    Range(int min, int max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Produces a _random number between the minimum and maximum
     */
    @Override
    public Pair<String, Integer> generate(NumberGenerator number) {
        number.greaterThanOrEqualTo(min).lessThanOrEqualTo(max);

        int answer = number.generate();
        return new Pair<>(Integer.toString(answer), answer);
    }

    private int min;
    private int max; // inclusive
}