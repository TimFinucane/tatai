package tatai.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Base model class for testing.
 */
public abstract class Test {

	private static final String FILENAME = "scores.txt";
	
	private int _score = 0;
	private int _testValue;
	private int _roundsRemaining = 10;
	private int _triesRemaining = 2;

	public abstract String name();
	protected abstract int getRandom();

	/**
	 * Generates a random number for the question.
	 * @return the number generated for that round.
	 */
	public int getNextRound() {
		_testValue = getRandom();
		_triesRemaining = 2;
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
			store();
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
	public boolean hasMoreTries() {
		if(_triesRemaining > 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void store() {
		try {
			File file = new File(FILENAME);
			
			//if file doesn't exist, create it
			if(!file.exists()) {
				file.createNewFile();
			}
			
			//append to file
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write(_score);
			
			bw.close();
			fw.close();
			
		} catch(IOException e) {
			throw new RuntimeException("Unable to store the final score" + e.getMessage());
		}
		
	}
	
	public static ArrayList<Integer> retrieveScores() {
		ArrayList<Integer> scores = new ArrayList<Integer>();
		try {
			Scanner sc = new Scanner(new File(FILENAME));
			
			while(sc.hasNext()) {
				if(sc.hasNextInt()) {
					scores.add(sc.nextInt());
				}
			}
			sc.close();
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return scores;
	}

}