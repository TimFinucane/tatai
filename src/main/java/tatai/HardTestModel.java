package tatai;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Controller class for the hard level.
 */

public class HardLevelController extends LevelController{
	
	private static final int MINTESTVALUE = 1;
	private static final int MAXTESTVALUE = 99;
	
	// Returns a random number between the minimum test value and the maximum test value.
	@Override
	public int getRandom() {
		return ThreadLocalRandom.current().nextInt(MINTESTVALUE, MAXTESTVALUE + 1);
	}

}
