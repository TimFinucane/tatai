package tatai.model.question;

import javafx.util.Pair;
import util.NumberConstraint;

/**
 * Can generate a string (the question) along with an integer (the answer)
 */
public interface Generatable {
    Pair<String, Integer> generate(NumberConstraint number);
}