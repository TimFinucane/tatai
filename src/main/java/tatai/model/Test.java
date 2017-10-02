package tatai.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Base model class for testing.
 */
public abstract class Test {
	public static class Stat {
		public String 	type;
		public int 		score;
	}

	private static final String FILENAME = "scores.txt";
	
	private int _score = 0;
	private int _testValue;
	private int _roundsRemaining = 10;
	private int _triesRemaining = 2;

	public abstract String name();
	protected abstract int getRandom();

	/**
	 * Gets the list of recent scores
	 */
	public static List<Stat> retrieveScores() {
		ArrayList<Stat> scores = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(FILENAME));

			String line = reader.readLine();
			while(!line.equals("")) {
				String items[] = line.split(",");
				Stat stat = new Stat();

				stat.type = items[0];
				stat.score = Integer.parseInt(items[1]);

				scores.add(stat);

				line = reader.readLine();
			}

			reader.close();
		} catch (FileNotFoundException e) {
			// Do nothing. If the file doesnt exist the intended behaviour is that
			//  no scores have been recorded
		} catch (IOException e) {
			throw new RuntimeException("Unable to read from file properly: " + e.getMessage());
		}
		return scores;
	}

	/**
	 * Generates a random number for the question.
	 * @return the number generated for that round.
	 */
	public int nextRound() {
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

	/**
	 * Stores the current round score
	 */
	private void store() {
		try {
			//append to file
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME, true));

			writer.write(name());
			writer.write(",");
			writer.write(String.valueOf(_score));
			writer.newLine();

			writer.close();
		} catch(IOException e) {
			throw new RuntimeException("Unable to store the final score" + e.getMessage());
		}
	}
}