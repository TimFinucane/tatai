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

    public String       name;
    public String       author = "";


    public boolean      randomizeQuestions;
    public Question[]   questions;
}
