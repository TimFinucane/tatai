package tatai;

/**
 * Factory class for the level controller. 
 */

public class LevelControllerFactory {

	/**
	 * Returns an instance of the level controller specified. Returns an IllegalArgumentException if input is invalid.
	 * @param level level of the controller.
	 * @return an instance of LevelController specified by the parameter.
	 */
	public static LevelController getLevelController(String level) {
		if("Easy".equalsIgnoreCase(level)) {
			return new EasyLevelController();
		}
		else if("Hard".equalsIgnoreCase(level)) {
			return new HardLevelController();
		}
		else {
			throw new IllegalArgumentException();
		}
	}
	
}
