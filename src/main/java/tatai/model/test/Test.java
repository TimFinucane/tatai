package tatai.model.test;

import tatai.model.question.Question;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * Base model class for testing.
 */
public class Test {
    public static class Memento {
        private Memento(int index, int round, int score, int shuffleSeed, Question.Memento questionMemento) {
            this.index = index;
            this.round = round;
            this.score = score;
            this.shuffleSeed = shuffleSeed;
            this.questionMemento = questionMemento;
        }

        private int                 index;
        private int                 round;
        private int                 score;
        private int                 shuffleSeed;
        private Question.Memento    questionMemento;
    }

    public Test(TestJson info) {
        _questions = info.questions;
        _randomize = info.randomizeQuestions;
        name = info.name;

        reset();
    }
    public Test(TestJson info, Memento memento) {
        _questions = info.questions;
        _randomize = info.randomizeQuestions;
        name = info.name;

        reset(memento);
    }

    public int                      score() {
        return _score;
    }

    /**
     * Gives the next question to display
     */
    public Question                 nextRound() {
        if(_curQuestion.correct())
            _score++;

        if(_curRound == _questions[_curIndex].rounds) { // Next question
            _curIndex++;
            _curRound = 0;
            _curQuestion = new Question(_questions[_curIndex]);
        } else { // Current question
            _curRound++;
            _curQuestion.generate();
        }

        return _curQuestion;
    }
    public Question                 curQuestion() {
        return _curQuestion;
    }

    /**
     * Returns whether or not there are any more rounds
     * @return true if there is at least one round remaining
     */
    public boolean                  hasAnotherRound() {
        return !(_curIndex == _questions.length - 1 && _curRound == _questions[_curIndex].rounds);
    }

    /**
     * Resets the test, ready for another go.
     */
    public void                     reset() {
        _score = 0;
        _curIndex = 0;
        _curRound = 0;

        if(_randomize) {
            _shuffleSeed = new Random().nextInt(); // Generate a random number to use as a seed
            shuffleQuestions();
        }
        _curQuestion = new Question(_questions[0]);
    }

    /**
     * Resets the test to the state specified in memento
     */
    private void                    reset(Memento memento) {
        _score = memento.score;
        _curIndex = memento.index;
        _curRound = memento.round;

        if(_randomize) {
            _shuffleSeed = memento.shuffleSeed;
            shuffleQuestions();
        }

        if(memento.questionMemento == null)
            _curQuestion = new Question(_questions[_curIndex]);
        else
            _curQuestion = new Question(_questions[_curIndex], memento.questionMemento);
    }

    private void                    shuffleQuestions() {
        ArrayList<TestJson.Question> list = new ArrayList<>(Arrays.asList(_questions));
        Collections.shuffle(list, new Random(_shuffleSeed)); // Use this shuffle seed to ensure correct numbers

        _questions = list.toArray(new TestJson.Question[0]);
    }

    public Memento                  memento() {
        Question.Memento question = null;

        // Save a question if we have one that has yet to be answered
        if(_curQuestion != null && !_curQuestion.correct())
            question = _curQuestion.memento();

        return new Memento(_curIndex, _curRound, _score, _shuffleSeed, question);
    }

    public final String             name;

    private int                     _score = 0;

    private Question                _curQuestion;
    private int                     _curIndex = 0;
    private int                     _curRound = 0;

    private boolean                 _randomize;
    private TestJson.Question       _questions[];

    private int                     _shuffleSeed = 0; // For if the test is randomized
}