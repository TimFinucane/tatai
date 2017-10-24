package tatai.model.test;

/**
 * JSon format for a test
 */
public class TestJson {
    public static class Question {
        public Question() {
            rounds = 1;
            tries = 2;
            timelimit = 0.0;
            question = new tatai.model.question.Question().headTagProperty().getValue().text;
        }
        public Question(String question, int rounds, int tries, double timelimit) {
            this.question = question;
            this.rounds = rounds;
            this.tries = tries;
            this.timelimit = timelimit;
        }

        public int      rounds;
        public int      tries;
        public double   timelimit;
        public String   question;
    }

    /**
     * Info about a prerequisite for the game
     */
    public static class Prerequisite {
        public String   name;
        public int      score;
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
