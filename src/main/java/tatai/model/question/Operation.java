package tatai.model.question;

import javafx.util.Pair;
import util.NumberGenerator;

import java.util.Arrays;

class Operation implements Generatable {
    enum Operator {
        PLUS("+", 1),
        MINUS("\u2212", 1),
        MULTIPLY("\u00D7", 2),
        DIVIDE("\u00F7", 2);

        Operator(String symbol, int importance) {
            this.symbol = symbol;
            this.importance = importance;
        }

        /**
         * Creates an Operator from its string form
         */
        static Operator fromString(String str) {
            switch(str) {
                case "+":
                    return PLUS;
                case "-":
                    return MINUS;
                case "*":
                    return MULTIPLY;
                case "/":
                    return DIVIDE;
            }
            throw new RuntimeException("Operation provided is not an operation (this is as confusing for you" +
                    " as it is for me");
        }

        /**
         * Applies the given operation to the left and right sides to produce a result
         */
        int apply(int left, int right) {
            switch(this) {
                case PLUS:
                    return left + right;
                case MINUS:
                    return left - right;
                case MULTIPLY:
                    return left * right;
                case DIVIDE:
                    return left / right;
            }
            throw new RuntimeException("Operation provided is not an operation (this is as confusing for you" +
                    " as it is for me");
        }

        String  symbol;
        int     importance;
    }

    Operation(Generatable first, Generatable second, Operator[] ops) {
        _first = first;
        _operators = ops;
        _second = second;
    }

    @Override
    public Pair<String, Integer>    generate(NumberGenerator number) {
        // Choose op to use
        _chosenOp = _operators[(int)(Math.random() * _operators.length)];

        // Start generation
        Pair<String, Integer> left;
        Pair<String, Integer> right;

        if(_chosenOp == Operator.DIVIDE) {
            right = _second.generate(number);
            left = _first.generate(number.divisibleBy(right.getValue())); // Ensure an integer is produced
        } else {
            left = _first.generate(number);
            right = _second.generate(number);
        }

        String question = tryEnclose(_first, left.getKey()) + _chosenOp.symbol + tryEnclose(_second, right.getKey());

        return new Pair<>(question, _chosenOp.apply(left.getValue(), right.getValue()));
    }

    @Override
    public String                   toString() {
        return "(" + _first.toString() + " " + Arrays.toString(_operators) + " " + _second.toString() + ")";
    }

    /**
     * Encloses the op string in brackets if the operation generating it has lower precedence than this one
     */
    private String                  tryEnclose(Generatable generatable, String op) {
        if( generatable instanceof Operation && ((Operation) generatable)._chosenOp.importance < _chosenOp.importance)
            return "(" + op + ")";
        else
            return op;
    }

    private Generatable _first;
    private Generatable _second;

    private Operator    _chosenOp;
    private Operator[]  _operators;
}