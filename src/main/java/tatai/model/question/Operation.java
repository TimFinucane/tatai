package tatai.model.question;

import javafx.util.Pair;
import util.NumberConstraint;

/**
 * A generatable part of a question that uses an operator on two input operands
 */
public class Operation implements Generatable {
    public static class Memento {
        public Memento() {}
        public Memento(Generatable first, Generatable second, Operator[] ops, boolean enclosed) {
            this.first = first;
            this.second = second;
            this.ops = ops;
            this.enclosed = enclosed;
        }

        public Generatable first;
        public Generatable second;
        public Operator[]  ops;
        public boolean enclosed;
    }

    public Operation(Memento memento) {
        _first = memento.first;
        _operators = memento.ops;
        _second = memento.second;
        _enclosed = memento.enclosed;
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

    public Memento                  memento() {
        return new Memento(_first, _second, _operators, _enclosed);
    }

    /**
     * Encloses the op string in brackets if the operation generating it has lower precedence than this one
     */
    private String                  tryEnclose(Generatable generatable, String op) {
        if(generatable instanceof Operation && ((Operation) generatable)._op.precedence() < _op.precedence() || _enclosed)
            return "(" + op + ")";
        else
            return op;
    }

    private Generatable _first;
    private Generatable _second;

    private boolean     _enclosed;
    private Operator    _op;
    private Operator[]  _operators;
}