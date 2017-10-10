package tatai.model.test;

/**
 * Base model class for testing.
 */
public class Test {
	private int 	    _score = 0;
	private int 	    _roundsRemaining = 10;
	private int 	    _triesRemaining = 2;

	private Question    _question;
	private String	    _name;

	public Test(String name, int minValue, int maxValue) {
	    _name = name;
	    _question = new Question(new Question.Range(minValue, maxValue));
    }

    public String   name() {
	    return _name;
    }

	/**
	 * Generates a random number for the question.
	 * @return the number generated for that round.
	 */
	public String	nextRound() {
		_triesRemaining = 2;
		_roundsRemaining--;
		return _question.generate();
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
		if(_question.verify(answer)) {
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
}