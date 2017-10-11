package tatai.model.test;

/**
 * JSon format for a test
 */
public class TestJson {
    public static class Question {
        int     rounds;
        int     tries = 0;
        int     timelimit = 0;
        String  question;
    }

    String          name;
    String          author = "";

    Question[]      questions;
}
