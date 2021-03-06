package tatai.model.question;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class QuestionReader {
    /**
     * Reads the given string, assuming it is in question format.
     * TODO: Produce documentation for question format
     */
    @Nonnull
    static QuestionPart read(String question) {
        // Stores the generatables that are created throughout
        ArrayList<QuestionPart> _elements = new ArrayList<>();

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
        Collection<List<Operator>> operators = Operator.getOperatorPrecedences();
        List<Operator> precedencesList = new ArrayList<>();

        StringBuffer sBuffer = rangeless;
        for(List<Operator> operatorList : operators) {
            precedencesList.addAll(operatorList);
            Pattern precedence = Pattern.compile(opsPattern(precedencesList));

            while(true){
                matches = precedence.matcher(sBuffer.toString());

                if(!matches.find())
                    break;
                else
                    matches.reset();

                StringBuffer opLess = new StringBuffer();
                while(matches.find()) {
                    // Add text to opLess
                    matches.appendReplacement(opLess, Integer.toString(_elements.size()));

                    // And create an op
                    QuestionPart left = _elements.get(Integer.valueOf(matches.group(1)));
                    QuestionPart right = _elements.get(Integer.valueOf(matches.group(3)));

                    _elements.add(new Operation(
                            left,
                            right,
                            !matches.group(4).isEmpty(), // Parentheses
                            Operator.createOperators(matches.group(2))
                    ));
                }
                // Finish replacement
                matches.appendTail(opLess);
                sBuffer = opLess;
            }
        }

        // TODO: Read other info at this point, like tries=(int) and time=(double)...
        try {
            return _elements.get(Integer.valueOf(sBuffer.toString()));
        } catch(NumberFormatException e) {
            throw new IllegalArgumentException("Could not read question, wrong format for string: " + sBuffer.toString());
        }
    }

    /**
     * Produces a pattern that searches for a use of any of the given ops in the form (operand OPERATOR operand)
     */
    @Nonnull
    private static String   opsPattern(List<Operator> ops) {
        StringBuilder output = new StringBuilder("\\(?(\\d+) \\[((?:");
        for(Operator op : ops)
            output.append(Pattern.quote(op.symbol)).append("|");

        output.append(",\\s)+)\\] (\\d+)(\\)?)");

        return output.toString();
    }
}
