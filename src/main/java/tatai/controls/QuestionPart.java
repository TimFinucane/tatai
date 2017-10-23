package tatai.controls;

import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import tatai.model.question.Generatable;
import tatai.model.question.Operator;

import javax.annotation.Nonnull;

/**
 * This is the model for custom question creation. It's a bit weird because its partially model and partially
 * controller
 */
public abstract class QuestionPart extends Region {
    /**
     * A tag is text detailing the appearance, as well as
     * a series of inner tags that have text contributing to this one
     */
    public static class Tag {
        @SafeVarargs
        Tag(QuestionPart part, String text, Pair<Tag, Integer>... tags) {
            this.text = text;
            this.tags = tags;
        }

        public QuestionPart         questionPart;
        public String               text;
        // The array of tags contained inside this tag, as well as
        //  the index to their starting position in the text
        public Pair<Tag, Integer>   tags[];
    }

    static class Range extends QuestionPart {
        public Range() {
            this(1, 9);
        }
        private Range(int min, int max) {
            _minFld = new Spinner<>(1, 99, min);
            _maxFld = new Spinner<>(1, 99, max);

            GridPane layout = new GridPane();
            layout.setHgap(15);
            layout.setVgap(5);

            layout.add(new Label("Min:"), 0, 0);
            layout.add(_minFld, 1, 0);
            layout.add(new Label("Max:"), 0, 1);
            layout.add(_maxFld, 1, 1);

            getChildren().add(layout);

            // Ensure the appearance changes whenever the fields do.
            // These binding things are p. neat
            appearance.bind(Bindings.createObjectBinding(
                    this::regenerate,
                    _minFld.valueProperty(),
                    _maxFld.valueProperty()));
        }

        public Generatable  formQuestion() {
            return new tatai.model.question.Range(new tatai.model.question.Range.Memento(
                    _minFld.getValue(),
                    _maxFld.getValue()));
        }

        @Nonnull
        private Tag         regenerate() {
            return new Tag(this,"(" +
                Integer.toString(_minFld.getValue()) + ", " +
                Integer.toString(_maxFld.getValue()) + ")");
        }

        private Spinner<Integer> _minFld;
        private Spinner<Integer> _maxFld;
    }
    static class Operation extends QuestionPart {
        public Operation() {
            this(new Range(), new Range(), false);
        }
        private Operation(QuestionPart left, QuestionPart right, boolean parenthesised, Operator... operators) {
            // Set operators
            for(Operator op : operators) {
                for(CheckBox box : _operatorBoxes) {
                    if(op.symbol().equals(box.getText()))
                        box.setSelected(true);
                }
            }

            _parenthesisedBox.setSelected(parenthesised);

            // Set parts
            _parts = new QuestionPart[]{ left, right }; // Will always be 2 parts (for now)
            getChildren().add(new VBox(10, _parenthesisedBox, new VBox(5, _operatorBoxes)));

            // Ensure regenerate is called whenever user changes stuff
            appearanceProperty().bind(Bindings.createObjectBinding(
                    this::regenerate,
                    _parts[0].appearanceProperty(),
                    _parts[1].appearanceProperty(),
                    _parenthesisedBox.selectedProperty(),
                    _operatorBoxes[0].selectedProperty(), // I know i know, but its fast and not gonna change lightly
                    _operatorBoxes[1].selectedProperty(),
                    _operatorBoxes[2].selectedProperty(),
                    _operatorBoxes[3].selectedProperty()
            ));

            left.parent = this;
            right.parent = this;
        }

        public void replaceChild(boolean left, QuestionPart part) {
            if(left) {
                _parts[0].parent = null;
                _parts[0] = part;
            } else {
                _parts[1].parent = null;
                _parts[1] = part;
            }
            part.parent = this;
        }

        public Generatable  formQuestion() {
            tatai.model.question.Operation.Memento memento = new tatai.model.question.Operation.Memento();

            memento.first = _parts[0].formQuestion();
            memento.second = _parts[1].formQuestion();

            // Roundabout but simple way of getting boxes to operators
            String operatorSet = "";
            for(CheckBox box : _operatorBoxes) {
                if(box.isSelected())
                    operatorSet += box.getText();
            }
            memento.ops = Operator.Type.createOperators(operatorSet);

            memento.enclosed = _parenthesisedBox.isSelected();

            return new tatai.model.question.Operation(memento);
        }

        @Nonnull
        private Tag         regenerate() {
            int firstIndex = 0;
            int secondIndex = 0;

            StringBuilder builder = new StringBuilder();
            if(_parenthesisedBox.isSelected())
                builder.append('(');

            // First part
            firstIndex = builder.length();
            builder.append(_parts[0].appearanceProperty().getValue().text);

            builder.append('[');
            // Add operators
            for(CheckBox box : _operatorBoxes) {
                if(box.isSelected()) {
                    if(builder.charAt(builder.length() - 1) != '[') // Horrible but short
                        builder.append(", ");

                    builder.append(box.getText());
                }
            }
            builder.append(']');

            // Second part
            secondIndex = builder.length();
            builder.append(_parts[1].appearanceProperty().getValue().text);

            if(_parenthesisedBox.isSelected())
                builder.append(')');

            return new Tag(this, builder.toString(),
                    new Pair<>(_parts[0].appearanceProperty().getValue(), firstIndex),
                    new Pair<>(_parts[1].appearanceProperty().getValue(), secondIndex));
        }

        private QuestionPart[]  _parts;

        private CheckBox        _parenthesisedBox = new CheckBox("Brackets");
        private CheckBox        _operatorBoxes[] = new CheckBox[]{
                new CheckBox(Operator.Type.ADD.symbol()),
                new CheckBox(Operator.Type.SUBTRACT.symbol()),
                new CheckBox(Operator.Type.MULTIPLY.symbol()),
                new CheckBox(Operator.Type.DIVIDE.symbol())};
    }

    @Nonnull
    public static   QuestionPart generate(@Nonnull Generatable generatable) {
        if(generatable instanceof tatai.model.question.Operation)
            return generate(((tatai.model.question.Operation) generatable).memento());
        else if(generatable instanceof tatai.model.question.Range)
            return generate(((tatai.model.question.Range) generatable).memento());
        else
            throw new IllegalArgumentException("Given generatable is not an operation or a range");
    }
    @Nonnull
    private static  QuestionPart generate(@Nonnull tatai.model.question.Range.Memento memento) {
        return new Range(memento.min, memento.max);
    }
    @Nonnull
    private static  QuestionPart generate(@Nonnull tatai.model.question.Operation.Memento memento) {
        return new Operation(
                generate(memento.first),
                generate(memento.second),
                memento.enclosed,
                memento.ops);
    }

    /**
     * A property identifying all the parts in the given part
     */
    public Property<Tag>        appearanceProperty() {
        return appearance;
    }

    /**'
     * Creates a question that can be used to generate examples
     */
    public abstract Generatable formQuestion();

    public Operation            parent() {
        return parent;
    }

    protected Property<Tag> appearance;
    protected Operation     parent = null;
}
