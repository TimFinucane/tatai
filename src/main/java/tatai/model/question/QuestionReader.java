package tatai.model.question;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuestionReader {
    public static Question read(String question) {
        // Stores the generatables that are created throughout
        ArrayList<Generatable>  _elements = new ArrayList<>();

        // First parse each number range, add to list, and replace with ints
        Pattern range = Pattern.compile("\\((\\d+) to (\\d+)\\)");
        Matcher matches = range.matcher(question);

        StringBuffer rangeless = new StringBuffer();
        while(matches.find()) {
            matches.appendReplacement(rangeless, Integer.toString(_elements.size()));
            _elements.add(new Range(Integer.valueOf(matches.group(1)), Integer.valueOf(matches.group(2))));
        }
        matches.appendTail(rangeless);

        // Now start reading in ops in order of precedence
        Pattern highPrecedence = Pattern.compile(opsPattern(Operation.Operator.MULTIPLY, Operation.Operator.DIVIDE));

        matches = highPrecedence.matcher(rangeless.toString());

        StringBuffer multless = new StringBuffer();
        while(matches.find()) {
            matches.appendReplacement(multless, Integer.toString(_elements.size()));
            // And create an op
            Generatable left = _elements.get(Integer.valueOf(matches.group(1)));
            Generatable right = _elements.get(Integer.valueOf(matches.group(3)));

            _elements.add(new Operation(left, right, readOps(matches.group(2))));
        }
        matches.appendTail(multless);

        // And finally do addition/multiplication
        Pattern lowPrecedence = Pattern.compile(opsPattern(Operation.Operator.PLUS, Operation.Operator.MINUS));

        matches = lowPrecedence.matcher(multless.toString());

        StringBuffer opless = new StringBuffer();
        while(matches.find()) {
            matches.appendReplacement(opless, Integer.toString(_elements.size()));
            // And create an op
            Generatable left = _elements.get(Integer.valueOf(matches.group(1)));
            Generatable right = _elements.get(Integer.valueOf(matches.group(3)));

            _elements.add(new Operation(left, right, readOps(matches.group(2))));
        }
        matches.appendTail(opless);

        // TODO: Read other info at this point, like tries=(int) and time=(double)...

        return new Question(_elements.get(Integer.valueOf(opless.toString())));
    }

    /**
     * Reads an array of operators in the form ([+|-|*|/], )*
     * outputs which of those exist
     */
    private static Operation.Operator[]     readOps(String ops) {
        ArrayList<Operation.Operator> outOps = new ArrayList<>();
        for(Operation.Operator value : Operation.Operator.values())
            if(ops.contains(value.symbol))
                outOps.add(value);

        return (Operation.Operator[])outOps.toArray();
    }

    /**
     * Produces a pattern that searches for a use of any of the given ops in the form (operand OPERATOR operand)
     */
    private static String                   opsPattern(Operation.Operator... ops) {
        StringBuilder output = new StringBuilder("\\(?(\\d+) \\[((?:");
        for(Operation.Operator op : ops)
            output.append(Pattern.quote(op.symbol)).append("|");

        output.append("|,\\s)+)\\] (\\d+)\\)?");

        return output.toString();
    }
}
