package tatai.model.question;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.util.Pair;
import util.NumberConstraint;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

/**
 * A generatable part of a question that uses an operator on two input operands
 */
public class Operation extends QuestionPart {
    private class TagBinding extends ObjectBinding<Tag> {
        public Tag computeValue() {
            return generateTag();
        }

        void rebind() {
            getDependencies().clear();
            bind(_operands.get(0).tagProperty(), _operands.get(1).tagProperty(), _operators, _enclosed);
        }
    }

    public Operation(QuestionPart first, QuestionPart second, boolean enclosed, Operator... ops) {
        first.parent = this;
        second.parent = this;

        _operands = new SimpleListProperty<>(FXCollections.observableArrayList(first, second));
        _operators = new SimpleListProperty<>(FXCollections.observableArrayList(ops));
        _enclosed.setValue(enclosed);

        _binding.rebind();
        bindPart(_binding);
    }

    @Override
    public Pair<String, Integer>    generate(NumberConstraint constraint) {
        // Choose op to use
        _op = _operators.get((int)(Math.random() * _operators.size()));

        // Start generation
        Pair<String, Integer> right = _operands.get(1).generate(_op.chooseRight(constraint));
        Pair<String, Integer> left = _operands.get(0).generate(_op.chooseLeft(constraint, right.getValue()));

        String question = tryEnclose(_operands.get(0), left.getKey()) + _op.symbol +
                          tryEnclose(_operands.get(1), right.getKey());

        return new Pair<>(question, _op.apply(left.getValue(), right.getValue()));
    }

    @Nonnull
    private Tag                      generateTag() {
        int firstIndex;
        int secondIndex;

        Tag firstTag = _operands.get(0).tagProperty().getValue();
        Tag secondTag = _operands.get(1).tagProperty().getValue();

        StringBuilder builder = new StringBuilder();

        if(_enclosed.get())
            builder.append('(');

        // First part
        firstIndex = builder.length();
        builder.append(firstTag.text);

        builder.append(" [");
        builder.append(_operators.stream().map(op -> op.symbol).collect(Collectors.joining(", ")));
        builder.append("] ");

        // Second part
        secondIndex = builder.length();
        builder.append(secondTag.text);

        if(_enclosed.get())
            builder.append(')');

        return new Tag(this, builder.toString(),
                new Pair<>(firstTag, firstIndex),
                new Pair<>(secondTag, secondIndex));
    }

    /**
     * Replaces the QuestionPart at operand index (0 = left, 1 = right for binary) with the new given part
     */
    public void replace(int operand, QuestionPart part) {
        if(_operands.get(operand).parent == this)
            _operands.get(operand).parent = null;

        _operands.set(operand, part);

        part.parent = this;

        _binding.rebind();
    }

    /**
     * Replaces the old with the new, if the old exists as a direct child of this
     */
    public void replace(QuestionPart oldPart, QuestionPart newPart) {
        for(int i = 0; i < _operands.size(); ++i)
            if(_operands.get(i) == oldPart)
                replace(i, newPart);
    }

    /**
     * Encloses the op string in brackets if the operation generating it has lower precedence than this one
     */
    private String                      tryEnclose(QuestionPart questionPart, String op) {
        if(questionPart instanceof Operation && ((Operation) questionPart)._op.precedence < _op.precedence)
            return "(" + op + ")";
        else
            return op;
    }

    public BooleanProperty              enclosedProperty() {
        return _enclosed;
    }
    public ListProperty<Operator>       operatorsProperty() {
        return _operators;
    }
    public ListProperty<QuestionPart>    operandsProperty() { return _operands; }

    private Operator                    _op;

    private TagBinding                  _binding = new TagBinding();

    private ListProperty<QuestionPart>   _operands;
    private BooleanProperty             _enclosed = new SimpleBooleanProperty();
    private ListProperty<Operator>      _operators;
}