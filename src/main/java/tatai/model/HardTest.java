package tatai.model;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Model class for the hard level.
 */

public class HardTest extends Test {
	
	private static final int MINTESTVALUE = 1;
	private static final int MAXTESTVALUE = 99;
	
	// Returns a random number between the minimum test value and the maximum test value.
	public int getRandom() {
		return ThreadLocalRandom.current().nextInt(MINTESTVALUE, MAXTESTVALUE + 1);
	}

}
