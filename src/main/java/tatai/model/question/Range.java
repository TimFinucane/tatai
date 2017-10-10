package tatai.model.question;

import javafx.util.Pair;

class Range extends Generator {
    Range(int min, int max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Produces a _random number between the minimum and maximum
     */
    public Pair<String, Integer> generate() {
        int answer = _random.nextInt(max + 1 - min) + min;
        return new Pair<>(Integer.toString(answer), answer);
    }

    private int min;
    private int max; // inclusive
}