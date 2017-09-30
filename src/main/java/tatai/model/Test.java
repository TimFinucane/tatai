package tatai.model;

/**
 * Base model class for testing.
 */

public abstract class Test {

	private int _score;
		
	public abstract int getRandom();
	
	
	//TODO: Add score counter - potentially store score elsewhere for later access, but if necessary i can do this (Tim)
	//TODO: Add logic for when answer is right and wrong.
	//TODO: generate() the number, be able to return a (maori) string of that number, and verify(Recording) for -
	//TODO:  whether or not it is valid (return boolean).
}