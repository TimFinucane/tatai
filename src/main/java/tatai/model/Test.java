package tatai.model;

/**
 * Base model class for testing.
 */

public abstract class Test {

	private int _score = 0;
	private int _testValue;
	private int _roundsRemaining = 10;
	private boolean _hasMultipleTries = true;	
	
	
	public abstract int getRandom();
	
	/**
	 * Generates a random number for the question.
	 * @return the number generated for that round.
	 */
	public int getNextRound() {
		_testValue = getRandom();
		_roundsRemaining--;
		return _testValue;
	}
	
	/**
	 * Method which returns whether or not there are any more rounds
	 * @return true if there is at least one round remaining
	 */
	public boolean hasNextRound() {
		if(_roundsRemaining > 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Method that returns the user's score.
	 * @return the user's score.
	 */
	public int getScore() {
		return _score;
	}
	
	/**
	 * Method which compares the users answer to the expected result.
	 * @param answer the user's response 
	 * @return true if the answer is equal to the expected result.
	 */
	public boolean verify(String answer) {
		if(answer.equalsIgnoreCase(Translator.convert(_testValue))) {
			_score++;
			_hasMultipleTries = true;
			return true;
		}
		else {
			_hasMultipleTries = false;
			return false;
		}
	}
	
	/**
	 * Method which returns whether or not the user is on their first or second try.
	 * @return true if they are on their first true or false if they are on their second try.
	 */
	public boolean hasMultipleTries() {
		return _hasMultipleTries;
	}
	
}