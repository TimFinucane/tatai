package tatai.model.test;

import tatai.model.question.Question;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Base model class for testing.
 */
public class Test {
    public static class Memento {
        private Memento(int index, int round) {
            this.index = index;
            this.round = round;
        }

        public int index;
        public int round;
    }

    public Test(TestJson info) {
        _questions = info.questions;
        _randomize = info.randomizeQuestions;
        name = info.name;

        reset();
    }
    public Test(TestJson info, Memento memento) {
        this(info);
        _curIndex = memento.index;
        _curRound = memento.round;
    }

	public int          			score() {
	    return _score;
    }

	/**
	 * Gives the next question to display
	 */
	public Question		            nextRound() {
        if(_curQuestion.correct())
	        _score++;

	    if(_curRound == _questions[_curIndex].rounds) { // Next question
            _curIndex++;
            _curRound = 0;
            _curQuestion = new Question(_questions[_curIndex]);
        } else { // Current question
            _curRound++;
            _curQuestion.reset();
        }

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
	    _curIndex = 0;
	    _curRound = 0;

        if(_randomize) {
            ArrayList<TestJson.Question> list = new ArrayList<>(Arrays.asList(_questions));
            Collections.shuffle(list);

            _questions = list.toArray(new TestJson.Question[0]);
        }

        _curQuestion = new Question(_questions[0]);
    }

    public Memento                  memento() {
        return new Memento(_curIndex, _curRound);
    }

    public final String             name;

    private int 	    			_score = 0;

    private Question                _curQuestion;
    private int                     _curIndex = 0;
    private int                     _curRound = -1;

    private boolean                 _randomize;
    private TestJson.Question       _questions[];
}