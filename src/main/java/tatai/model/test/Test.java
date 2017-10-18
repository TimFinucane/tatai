package tatai.model.test;

import tatai.model.question.Question;

/**
 * Base model class for testing.
 */
public abstract class Test {
	public int          			score() {
	    return _score;
    }

	/**
	 * Generates a _random question.
	 * @return the question generated for that round.
	 */
	public String			        nextRound() {
	    _curQuestion = nextQuestion();
	    return _curQuestion.generate();
    }
	
	/**
	 * Method which returns whether or not there are any more rounds
	 * @return true if there is at least one round remaining
	 */
	public abstract boolean 		hasAnotherRound();

	/**
	 * Method which compares the users answer to the expected result.
	 * @param answer the user's response 
	 * @return true if the answer is equal to the expected result.
	 */
	public boolean 					tryAnswer(String answer) {
        if(!hasAnotherTry())
            throw new IllegalStateException("No more tries left, cannot try again!");

		if(_curQuestion.verify(answer)) {
			_score++;

			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Method which returns whether or not the user has more tries
	 * @return true if they have more tries, false if they don't
	 */
	public abstract boolean 		hasAnotherTry();

    /**
     * Calculates the next question to use
     */
	protected abstract Question     nextQuestion();

	private int                     _tries = 0;
    private Question                _curQuestion = null;
    private int 	    			_score = 0;
}