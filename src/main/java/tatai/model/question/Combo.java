package tatai.model.question;

import javafx.util.Pair;
import util.NumberGenerator;

class Combo extends Generator {
    enum Operator {
        PLUS("+", 1),
        MINUS("-", 1),
        MULTIPLY("*", 2),
        DIVIDE("/", 2);

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

    Combo(Generator first, Generator second, Operator[] ops) {
        _first = first;
        _operators = ops;
        _second = second;
    }

    @Override
    Pair<String, Integer>    generate(NumberGenerator number) {
        Pair<String, Integer> left = _first.generate(number);
        Pair<String, Integer> right = _second.generate(number);

        _chosenOp = _operators[(int)(Math.random() * _operators.length)];

        String question = tryEnclose(_first, left.getKey()) + _chosenOp.symbol + tryEnclose(_second, right.getKey());

        return new Pair<>(question, _chosenOp.apply(left.getValue(), right.getValue()));
    }

    /**
     * Encloses the op string in brackets if the operation generating it has lower precedence than this one
     */
    private String                  tryEnclose(Generator generator, String op) {
        if(generator instanceof Combo && ((Combo) generator)._chosenOp.importance < _chosenOp.importance)
            return "(" + op + ")";
        else
            return op;
    }

    private Generator   _first;
    private Generator   _second;

    private Operator    _chosenOp;
    private Operator[]  _operators;
}