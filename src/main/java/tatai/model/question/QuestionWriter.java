package tatai.model.question;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Arrays;

/**
 * Adds capability for creating questions through converting
 * code to a question-readable string.
 */
public class QuestionWriter {
    /**
     * Creates a Question range, based on the given min and max values
     */
    public static String    createRange(int min, int max) {
        return "(" + Integer.toString(min) + " to " + Integer.toString(max) + ")";
    }

    /**
     * Creates an operation string, where left and right are operands. It could be any of the given operators,
     * and may or may not be enclosed in parentheses.
     */
    public static String    createOperation(String left, String right, String[] operators, boolean enclosed) {
        String ops = Arrays.toString(operators);
        String result = left + " " + ops + " " + right;

        if(enclosed)
            return "(" + result + ")";
        else
            return result;
    }

    /**
     * Stores the string created through this QuestionWriter, so that it can be accessed later
     */
    public static void      storeQuestion(String question) {
        throw new NotImplementedException();
    }
}
