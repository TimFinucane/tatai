package tatai.model.question;

import javafx.util.Pair;

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

    public Pair<String, Integer>    generate() {
        Pair<String, Integer> left = _first.generate();
        Pair<String, Integer> right = _second.generate();

        _chosenOp = _operators[_random.nextInt(_operators.length)];

        StringBuilder output = new StringBuilder();

        // If either operation has a lower precedence, surround it in brackets
        if(_first instanceof Combo && ((Combo) _first)._chosenOp.importance < _chosenOp.importance)
            output.append("(").append(left.getKey()).append(")");
        else
            output.append(left.getKey());

        output.append(" ").append(_chosenOp.symbol).append(" ");

        if(_second instanceof Combo && ((Combo) _second)._chosenOp.importance < _chosenOp.importance)
            output.append("(").append(right.getKey()).append(")");
        else
            output.append(right.getKey());

        return new Pair<>(output.toString(), _chosenOp.apply(left.getValue(), right.getValue()));
    }

    private Generator   _first;
    private Generator   _second;

    private Operator    _chosenOp;
    private Operator[]  _operators;
}