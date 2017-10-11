package tatai.model.question;

import java.util.ArrayList;
import java.util.List;

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
     * A list of operator strings representing various operators that can be used in an operation
     */
    public static List<String> operatorList() {
        ArrayList<String> list = new ArrayList<>();

        for(Operator.Type value : Operator.Type.values())
            list.add(value.symbol());

        return list;
    }

    /**
     * Creates an operation string, where left and right are operands. It could be any of the given operators,
     * and may or may not be enclosed in parentheses.
     */
    public static String    createOperation(String left, String right, List<String> operators, boolean enclosed) {
        String ops = operators.toString();
        String result = left + " " + ops + " " + right;

        if(enclosed)
            return "(" + result + ")";
        else
            return result;
    }
}
