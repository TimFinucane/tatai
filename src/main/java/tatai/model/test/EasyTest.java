package tatai.model.test;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Model class for the easy level.
 */

public class EasyTest extends Test {

	private static final int MINTESTVALUE = 1;
	private static final int MAXTESTVALUE = 9;

	public String name() {
		return "Easy Test";
	}

	// Returns a random number between the minumum test value and the maximum test value.
	protected int getRandom() {
		return ThreadLocalRandom.current().nextInt(MINTESTVALUE, MAXTESTVALUE + 1);
	}
}