package tatai.model.test;

import tatai.model.question.Question;
import tatai.model.question.QuestionReader;

/**
 * Base model class for testing.
 */
public class Test {
	public Test(String name, int minValue, int maxValue) {
	    _name = name;
	    _question = QuestionReader.read("(0 to 9) [+, \u00D7] (1 to 99)");
    }

    public String   name() {
	    return _name;
    }
    public int 		score() {
        return _score;
    }

	/**
	 * Generates a _random question.
	 * @return the question generated for that round.
	 */
	public String	nextRound() {
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
	 * Method which compares the users answer to the expected result.
	 * @param answer the user's response 
	 * @return true if the answer is equal to the expected result.
	 */
	public boolean 	tryAnswer(String answer) {
		if(_question.verify(answer)) {
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
	public boolean 	hasAnotherTry() {
		return _question.hasAnotherTry();
	}

    private int 	    _score = 0;
    private int 	    _roundsRemaining = 10;

    private Question    _question;
    private String	    _name;
}