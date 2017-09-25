package tatai.model;

/**
 * Contains helper method to translate an integer to Maori.
 */
public class Translator {

	// Starts at 1
	private static final String[] DIGITS = {
			"tahi",
			"rua",
			"toru",
			"wh\u0101",
			"rima",
			"ono",
			"whitu",
			"waru",
			"iwa",
	};

	private static final String TEN = "tekau";

	private static final String CONNECTOR = "m\u0101";

	/**
	 * Returns the Maori translation of an integer.
	 * @param number the number to be translated between 1-99 inclusive.
	 * @return the Maori translation of the specified number.
	 */
	public static String convert(int number) {
		if (number < 10) {
			return DIGITS[number - 1];
		} else if(number % 10 == 0) {
            if(number == 10) {
                return TEN;
            } else {
                return DIGITS[number / 10 - 1] + " " + TEN;
            }
        } else {
		    return convert(number - number%10) + " " + CONNECTOR + " " + convert(number % 10);
        }
	}
}
