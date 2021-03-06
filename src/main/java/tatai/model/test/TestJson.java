package tatai.model.test;

import tatai.model.question.Range;

/**
 * JSon format for a test
 */
public class TestJson {
    public static class Question {
        public Question() {
            min = 1;
            max = 99;
            rounds = 1;
            tries = 2;
            timelimit = -1.0;
            question = new Range().tagProperty().getValue().text;
        }
        public Question(String question, int rounds, int tries, double timelimit, int min, int max) {
            this.min = min;
            this.max = max;
            this.question = question;
            this.rounds = rounds;
            this.tries = tries;
            this.timelimit = timelimit;
        }

        public int      min;
        public int      max;
        public int      rounds;
        public int      tries;
        public double   timelimit;
        public String   question;
    }

    /**
     * Info about a prerequisite for the game
     */
    public static class Prerequisite {
        public Prerequisite() {}
        public Prerequisite(String name, int score, int times) {
            this.name = name;
            this.score = score;
            this.times = times;
        }

        public String   name;
        public int      score;
        public int      times;
    }

    public int              rounds() {
        int sum = 0;
        for(Question question : questions)
            sum += question.rounds;

        return sum;
    }

    public String           name;
    public String           author = "";

    public boolean          practice = false; // Practice questions don't save and go on forever
    public boolean          custom = true;

    public int              order = -1; // Order it should be displayed in. Non-custom only

    public Prerequisite[]   prerequisites;

    public boolean          randomizeQuestions;
    public Question[]       questions;
}
