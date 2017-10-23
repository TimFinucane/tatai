package tatai.model.question;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuestionReader {
    /**
     * Reads the given string, assuming it is in question format.
     * TODO: Produce documentation for question format
     */
    @Nonnull
    public static Question  read(String question) {
        // Stores the generatables that are created throughout
        ArrayList<Generatable> _elements = new ArrayList<>();

        // First parse each number range, add to list, and replace with ints
        Pattern range = Pattern.compile("\\((\\d+) to (\\d+)\\)");
        Matcher matches = range.matcher(question);

        StringBuffer rangeless = new StringBuffer();
        while(matches.find()) {
            matches.appendReplacement(rangeless, Integer.toString(_elements.size()));
            _elements.add(new Range(new Range.Memento(Integer.valueOf(matches.group(1)), Integer.valueOf(matches.group(2)))));
        }
        matches.appendTail(rangeless);

        // Now start reading in ops in order of precedence
        Collection<List<Operator.Type>> operators = Operator.Type.getOperatorPrecedences();
        List<Operator.Type> precedencesList = new ArrayList<>();

        StringBuffer sBuffer = rangeless;
        for(List<Operator.Type> operatorList : operators) {
            precedencesList.addAll(operatorList);
            Pattern highPrecedence = Pattern.compile(opsPattern(precedencesList));

            matches = highPrecedence.matcher(sBuffer.toString());

            StringBuffer opLess = new StringBuffer();
            while(matches.find()) {
                matches.appendReplacement(opLess, Integer.toString(_elements.size()));
                // And create an op
                Generatable left = _elements.get(Integer.valueOf(matches.group(1)));
                Generatable right = _elements.get(Integer.valueOf(matches.group(3)));

                // TODO: Remember parentheses
                _elements.add(new Operation(new Operation.Memento(
                        left,
                        right,
                        Operator.Type.createOperators(matches.group(2)),
                        false)));
            }
            matches.appendTail(opLess);

            sBuffer = opLess;
        }

        // TODO: Read other info at this point, like tries=(int) and time=(double)...
        try {
            return new Question(_elements.get(Integer.valueOf(sBuffer.toString())));
        } catch(NumberFormatException e) {
            throw new IllegalArgumentException("Could not read question, wrong format for string: " + sBuffer.toString());
        }
    }

    /**
     * Produces a pattern that searches for a use of any of the given ops in the form (operand OPERATOR operand)
     */
    @Nonnull
    private static String   opsPattern(List<Operator.Type> ops) {
        StringBuilder output = new StringBuilder("\\(?(\\d+) \\[((?:");
        for(Operator.Type op : ops)
            output.append(Pattern.quote(op.symbol())).append("|");

        output.append(",\\s)+)\\] (\\d+)\\)?");

        return output.toString();
    }
}
