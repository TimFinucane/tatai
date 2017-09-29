package tatai;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Model class for the easy level.
 */

public class EasyTestModel extends TestModel {

	private static final int MINTESTVALUE = 1;
	private static final int MAXTESTVALUE = 9;
	
	// Returns a random number between the minumum test value and the maximum test value.
	public int getRandom() {
		return ThreadLocalRandom.current().nextInt(MINTESTVALUE, MAXTESTVALUE + 1);
	}
	
	
}