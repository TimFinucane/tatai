package tatai.model.test;

import tatai.model.question.Question;

import java.util.ArrayList;

/**
 * Base model class for testing.
 */
public class Test {
	static class QuestionInfo {
	    QuestionInfo(int rounds, int tries, Question question) {
	        this.rounds = rounds;
	        this.tries = tries;
	        this.question = question;
        }

		int			rounds;
		int         tries;
        Question 	question;
	}

	Test(String name, ArrayList<QuestionInfo> questions) {
	    _name = name;
	    _questions = questions;
    }

    public String   	name() {
	    return _name;
    }
    public int 			score() {
        return _score;
    }

	/**
	 * Generates a _random question.
	 * @return the question generated for that round.
	 */
	public String		nextRound() {
		_round++;

		if(_round >= _questions.get(_questionIndex).rounds) {
			_round = 0;
			_questionIndex += 1;

			if(_questionIndex > _questions.size()) {
				Scores.save(_name, _score);
			}
		}

		_tries = _questions.get(_questionIndex).tries;

		return curQuestion().generate();
	}
	
	/**
	 * Method which returns whether or not there are any more rounds
	 * @return true if there is at least one round remaining
	 */
	public boolean 		hasNextRound() {
		return _questionIndex > _questions.size();
	}

	/**
	 * Method which compares the users answer to the expected result.
	 * @param answer the user's response 
	 * @return true if the answer is equal to the expected result.
	 */
	public boolean 		tryAnswer(String answer) {
        // TODO: Throw if tries < 0? Doesn't seem important atm
		if(curQuestion().verify(answer)) {
			_score++;
			_tries = 0;

			return true;
		} else {
		    _tries--;
			return false;
		}
	}
	
	/**
	 * Method which returns whether or not the user has more tries
	 * @return true if they have more tries, false if they don't
	 */
	public boolean 		hasAnotherTry() {
		return _tries > 0;
	}

	/**
	 * Get the current question being used
	 */
	private Question 	curQuestion() {
		return _questions.get(_questionIndex).question;
	}

    private int 	    			_score = 0;

	private int                     _tries;

	private int						_questionIndex = 0;
	private int						_round = 0;
	private ArrayList<QuestionInfo>	_questions;

    private String	    			_name;
    private String                  _author;
}