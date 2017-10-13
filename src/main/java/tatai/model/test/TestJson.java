package tatai.model.test;

/**
 * JSon format for a test
 */
public class TestJson {
    public static class Question {
        public int      rounds;
        public int      tries = 0;
        public int      timelimit = 0;
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

    public Prerequisite[]   prerequisites;

    public boolean          randomizeQuestions;
    public Question[]       questions;
}
