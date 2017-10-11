package tatai.model.question;

import javafx.util.Pair;
import util.NumberConstraint;

import java.util.Arrays;

class Operation implements Generatable {
    Operation(Generatable first, Generatable second, Operator[] ops) {
        _first = first;
        _operators = ops;
        _second = second;
    }

    @Override
    public Pair<String, Integer>    generate(NumberConstraint constraint) {
        // Choose op to use
        _op = _operators[(int)(Math.random() * _operators.length)];

        // Start generation
        Pair<String, Integer> right = _second.generate(_op.chooseRight(constraint));
        Pair<String, Integer> left = _first.generate(_op.chooseLeft(constraint, right.getValue()));

        String question = tryEnclose(_first, left.getKey()) + _op.symbol() +
                          tryEnclose(_second, right.getKey());

        return new Pair<>(question, _op.apply(left.getValue(), right.getValue()));
    }

    @Override
    public String                   toString() {
        return "(" + _first.toString() + " " + Arrays.toString(_operators) + " " + _second.toString() + ")";
    }

    /**
     * Encloses the op string in brackets if the operation generating it has lower precedence than this one
     */
    private String                  tryEnclose(Generatable generatable, String op) {
        if( generatable instanceof Operation && ((Operation) generatable)._op.precedence() < _op.precedence())
            return "(" + op + ")";
        else
            return op;
    }

    private Generatable _first;
    private Generatable _second;

    private Operator    _op;
    private Operator[]  _operators;
}