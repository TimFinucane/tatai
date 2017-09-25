package tatai;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Controller class for the easy level.
 * @author andy
 *
 */

public class EasyLevelController extends LevelController {

	private static final int MINTESTVALUE = 1;
	private static final int MAXTESTVALUE = 9;
	
	// Returns a random number between the minumum test value and the maximum test value.
	@Override
	public int getRandom() {
		return ThreadLocalRandom.current().nextInt(MINTESTVALUE, MAXTESTVALUE + 1);
	}
	
}