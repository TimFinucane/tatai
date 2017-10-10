package tatai.model.question;

import javafx.util.Pair;
import util.NumberGenerator;

/**
 * Can generate a string (the question) along with an integer (the answer)
 */
abstract class Generator {
    abstract Pair<String, Integer> generate(NumberGenerator number);
}