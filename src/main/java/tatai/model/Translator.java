package tatai.model;

/**
 * Contains helper method to translate an integer to Maori.
 */
public class Translator {

	private static final String[] onesNames = {
			"",
			"tahi ",
			"rua ",
			"toru ",
			"wha ",
			"rima ",
			"ono ",
			"whitu ",
			"waru ",
			"iwa ",
	};
	
	private static final String ten = "tekau ";
	
	private static final String connector = "ma ";
	
	/**
	 * Returns the Maori translation of an integer.
	 * @param number the number to be translated between 1-99 inclusive.
	 * @return the Maori translation of the specified number.
	 */
	public String convert(int number) {
		String word;
		if (number < 10) {
			word = onesNames[number];
			return word;
		}
		
		else if (number == 10) {
			word = ten;
			return word;
		}
		
		if (number % 10 != 0) {
			word = onesNames[number %10];	
			number /= 10;
			if(number ==1) {
				return ten + connector + word;
			}
			else {
				return onesNames[number] + ten + connector + word;
			}
			
		}
		else {
			number /= 10;
			return onesNames[number] + ten;
		}	
	}

}
