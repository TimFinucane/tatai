package tatai.model;

/**
 * Base model class for testing.
 */

public abstract class Test {

	private int _score = 0;
	private int _testValue;
	private boolean _hasMultipleTries = true;	
	
	public abstract int getRandom();
	
	/**
	 * Generates a random number for the question.
	 * @return maori translation of that number.
	 */
	public String generate() {
		_testValue = getRandom();
		return Translator.convert(_testValue);
	}
	
	/**
	 * Method which compares the users answer to the expected result.
	 * @param answer the users response 
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
	public boolean hasMultpleTries() {
		return _hasMultipleTries;
	}
	
}