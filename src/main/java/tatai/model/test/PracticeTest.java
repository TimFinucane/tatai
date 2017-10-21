package tatai.model.test;

import tatai.model.question.Question;

import java.util.ArrayList;

public class PracticeTest extends Test {
    PracticeTest(ArrayList<Question> questions) {
        _questions = questions;
    }

    public boolean      hasAnotherTry() {
        return true;
    }
    public boolean 		hasAnotherRound() {
        return true;
    }

    protected Question  nextQuestion() {
        return _questions.get((int)Math.floor(Math.random() * _questions.size()));
    }

    private ArrayList<Question> _questions;
}
