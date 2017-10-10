package tatai.model.test;

import tatai.model.Translator;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Base model class for testing.
 */
public class Test {
	private int 	_score = 0;
	private int 	_testValue;
	private int 	_roundsRemaining = 10;
	private int 	_triesRemaining = 2;

	private int     _minValue;
	private int     _maxValue;
	private String	_name;

	public Test(String name, int minValue, int maxValue) {
	    _name = name;
	    _minValue = minValue;
	    _maxValue = maxValue;
    }

    public String   name() {
	    return _name;
    }

	/**
	 * Generates a random number for the question.
	 * @return the number generated for that round.
	 */
	public int 		nextRound() {
		_testValue = random();
		_triesRemaining = 2;
		_roundsRemaining--;
		return _testValue;
	}
	
	/**
	 * Method which returns whether or not there are any more rounds
	 * @return true if there is at least one round remaining
	 */
	public boolean 	hasNextRound() {
		if(_roundsRemaining > 0) {
			return true;
		}
		else {
			Scores.save(_name, _score);
			return false;
		}
	}
	
	/**
	 * Method that returns the user's score.
	 * @return the user's score.
	 */
	public int 		getScore() {
		return _score;
	}
	
	/**
	 * Method which compares the users answer to the expected result.
	 * @param answer the user's response 
	 * @return true if the answer is equal to the expected result.
	 */
	public boolean 	verify(String answer) {
		if(answer.equalsIgnoreCase(Translator.convert(_testValue))) {
			_score++;
			return true;
		}
		else {
			_triesRemaining--;
			return false;
		}
	}
	
	/**
	 * Method which returns whether or not the user has more tries
	 * @return true if they have more tries, false if they don't
	 */
	public boolean 	hasMoreTries() {
		return _triesRemaining > 0;
	}

	private int     random() {
        return ThreadLocalRandom.current().nextInt(_minValue, _maxValue + 1);
    }
}