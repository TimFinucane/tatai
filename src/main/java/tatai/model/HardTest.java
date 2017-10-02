package tatai.model;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Model class for the hard level.
 */

public class HardTest extends Test {
	
	private static final int MINTESTVALUE = 1;
	private static final int MAXTESTVALUE = 99;

	public String name() {
		return "Hard Test";
	}
	// Returns a random number between the minimum test value and the maximum test value.
	protected int getRandom() {
		return ThreadLocalRandom.current().nextInt(MINTESTVALUE, MAXTESTVALUE + 1);
	}
}
