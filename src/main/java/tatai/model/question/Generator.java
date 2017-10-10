package tatai.model.question;

import javafx.util.Pair;

import java.util.Random;

/**
 * Can generate a string (the question) along with an integer (the answer)
 */
abstract class Generator {
    abstract Pair<String, Integer> generate();

    protected Random _random = new Random();
}