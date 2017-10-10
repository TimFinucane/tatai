package tatai.model.test;

import tatai.model.Translator;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A question uses a specification to generate a specific string for the user to answer
 */
class Question {
    static class Range {
        public Range(int min, int max) {
            this.min = min;
            this.max = max;
        }

        /**
         * Produces a random number between the minimum and maximum
         */
        public int     random() {
            return ThreadLocalRandom.current().nextInt(min, max + 1);
        }

        private int min;
        private int max;
    }

    /**
     * Should only be created by a Test
     */
    Question(Range range) {
        this.range = range;
    }

    /**
     * Generates a question
     */
    public String   generate() {
        curAnswer = range.random();
        return Integer.toString(curAnswer);
    }

    public boolean 	verify(String submission) {
        return submission.equalsIgnoreCase(Translator.convert(curAnswer));
    }

    private int     curAnswer;

    private Range   range;
}
