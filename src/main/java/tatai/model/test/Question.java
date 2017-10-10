package tatai.model.test;

import tatai.model.Translator;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A question uses a specification to generate a specific string for the user to answer
 */
class Question {
    static class Range {
        Range(int min, int max) {
            this.min = min;
            this.max = max;
        }

        /**
         * Produces a random number between the minimum and maximum
         */
        int     random() {
            return ThreadLocalRandom.current().nextInt(min, max + 1);
        }

        private int min;
        private int max; // inclusive
    }

    /**
     * Should only be created by a Test
     */
    Question(Range range) {
        this._range = range;
    }

    /**
     * Generates a question
     */
    String   generate() {
        _curAnswer = _range.random();
        return Integer.toString(_curAnswer);
    }

    /**
     * Checks whether submission is correct
     */
    boolean 	verify(String submission) {
        return submission.equalsIgnoreCase(Translator.convert(_curAnswer));
    }

    private int     _curAnswer;

    private Range   _range;
}
