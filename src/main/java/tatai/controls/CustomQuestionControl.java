package tatai.controls;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Pair;
import tatai.model.question.*;

/**
 * Allows the user to create their own question
 */
public class CustomQuestionControl extends TitledPane {
    static class RangeControl extends VBox {
        public RangeControl(Range range) {
            setSpacing(10);
            setAlignment(Pos.CENTER);

            _minFld = new Spinner<>(1, 99, range.minProperty().get());
            _maxFld = new Spinner<>(1, 99, range.maxProperty().get());

            getChildren().addAll(_minFld, _maxFld);

            range.minProperty().bind(_minFld.valueProperty());
            range.maxProperty().bind(_maxFld.valueProperty());
        }

        private Spinner<Integer> _minFld;
        private Spinner<Integer> _maxFld;

        private Range range;
    }
    static class OperationControl extends HBox {
        public OperationControl(Operation operation) {
            setSpacing(10);
            setAlignment(Pos.CENTER);

            GridPane grid = new GridPane();
            grid.setVgap(5);
            grid.setHgap(5);
            grid.setAlignment(Pos.CENTER);

            grid.add(_operatorBoxes[0], 0, 0);
            grid.add(_operatorBoxes[1], 1, 0);
            grid.add(_operatorBoxes[2], 0, 1);
            grid.add(_operatorBoxes[3], 1, 1);

            getChildren().addAll(_parenthesisedBox, grid);

            _operation = operation;

            // Initialize check boxes
            for(Operator op : operation.operatorsProperty()) {
                for(CheckBox box : _operatorBoxes) {
                    if(op.symbol().equals(box.getText())) {
                        box.setSelected(true);
                    }
                }
            }

            _operation.enclosedProperty().bindBidirectional(_parenthesisedBox.selectedProperty());
            _operation.operatorsProperty().bind(Bindings.createObjectBinding(() -> {
                        ObservableList<Operator> ops = FXCollections.observableArrayList();
                        for(CheckBox box : _operatorBoxes) {
                            for(Operator.Type type : Operator.Type.values()) {
                                if(box.getText().equals(type.symbol()) && box.isSelected())
                                    ops.add(type.create());
                            }
                        }
                        return ops;
                    },
                    _operatorBoxes[0].selectedProperty(),
                    _operatorBoxes[1].selectedProperty(),
                    _operatorBoxes[2].selectedProperty(),
                    _operatorBoxes[3].selectedProperty()
            ));
        }

        private Operation       _operation;

        private CheckBox        _parenthesisedBox = new CheckBox("Brackets");
        private CheckBox        _operatorBoxes[] = new CheckBox[]{
                new CheckBox(Operator.Type.ADD.symbol()),
                new CheckBox(Operator.Type.SUBTRACT.symbol()),
                new CheckBox(Operator.Type.MULTIPLY.symbol()),
                new CheckBox(Operator.Type.DIVIDE.symbol())};
    }

    /**
     * Creates an all new question
     */
    public CustomQuestionControl() {
        this(new Range());
    }

    /**
     * Allows the user to modify the given question (in string form)
     */
    public CustomQuestionControl(Question question) {
        this(question.head());
    }
    private CustomQuestionControl(Generatable root) {
        setText("Question");
        setCollapsible(false);

        _root = root;

        setAlignment(Pos.CENTER);
        _main.setAlignment(Pos.CENTER);

        VBox left = new VBox(15, _textFlow, new HBox(20, _addBtn, _deleteBtn));
        left.setAlignment(Pos.CENTER);

        _main.getChildren().add(left);

        setContent(_main);

        select(_root);

        // Ensure text flow is updated properly
        _root.tagProperty().addListener(
                (ObservableValue<? extends Generatable.Tag> obs,
                 Generatable.Tag oldTag,
                 Generatable.Tag newTag) ->
                updateFlow()
        );

        updateFlow();

        _addBtn.setOnAction(event -> addOperation());
        _deleteBtn.setOnAction(event -> remove());
    }

    public void switchQuestion(Question question) {
        switchRoot(question.head());
    }

    /**
     * Updates the textflow with new tag structure
     */
    private void            updateFlow() {
        _textFlow.getChildren().clear();

        updateFlow(_root.tagProperty().getValue(), 0);
    }
    /**
     * Updates the textflow with new tag structure
     * @param depth Used for colour coordination, determines colour of given text
     */
    private void            updateFlow(Generatable.Tag rootTag, int depth) {
        if(rootTag.tags == null || rootTag.tags.length == 0) {
            addText(rootTag, depth);
            return;
        }

        // Colour text before the first tag as the root tag
        if(startOf(rootTag.tags[0]) > 0)
            addText(rootTag, 0, startOf(rootTag.tags[0]), depth);

        updateFlow(rootTag.tags[0].getKey(), depth + 1);

        for(int i = 1; i < rootTag.tags.length; i++) {
            // Colour space between previous and this tag as root
            if(endOf(rootTag.tags[i-1]) < startOf(rootTag.tags[i]))
                addText(rootTag, endOf(rootTag.tags[i-1]), startOf(rootTag.tags[i]), depth);

            updateFlow(rootTag.tags[i].getKey(), depth + 1);
        }

        // Colour text after last tag
        if(endOf(rootTag.tags[rootTag.tags.length-1]) < rootTag.text.length())
            addText(rootTag, endOf(rootTag.tags[rootTag.tags.length-1]), rootTag.text.length(), depth);
    }

    /**
     * Switches to viewing the QuestionPart the user clicked on
     */
    private void            select(Generatable part) {
        _main.getChildren().remove(_selectedControl);
        _selected = part;

        if(part instanceof Range)
            _selectedControl = new RangeControl((Range)part);
        else
            _selectedControl = new OperationControl((Operation)part);

        _main.getChildren().add(_selectedControl);
    }

    private void            addText(Generatable.Tag tag, int start, int end, int depth) {
        Text text = new Text(tag.text.substring(start, end));
        text.setFill(colours[depth % colours.length]);

        // Ensure that when the user clicks this bit, we select it
        text.setOnMouseClicked(event -> select(tag.owner));

        _textFlow.getChildren().add(text);
    }
    private void            addText(Generatable.Tag tag, int depth) {
        addText(tag, 0, tag.text.length(), depth);
    }

    /**
     * Adds an operation that takes the selected QuestionPart as the left child, and submits itself in its place
     */
    private void            addOperation() {
        Operation oldParent = _selected.parent();
        Operation newOp = new Operation(_selected, new Range(), false, Operator.Type.ADD.create());

        if(_selected == _root)
            switchRoot(newOp);
        else
            oldParent.replace(_selected, newOp);

        select(newOp);
    }

    public void             switchRoot(Generatable root) {
        _root = root;
        select(_root);

        // Ensure text flow is updated properly
        _root.tagProperty().addListener(
                (ObservableValue<? extends Generatable.Tag> obs,
                 Generatable.Tag oldTag,
                 Generatable.Tag newTag) ->
                        updateFlow()
        );
    }

    /**
     * Removes the selected questionPart.
     * If the part is a range, then the entire parent operation + that range is deleted
     */
    private void            remove() {
        // Special care must be taken
        if(_selected == _root) {
            if(_selected instanceof Range) {
                new Alert(Alert.AlertType.ERROR,
                        "Can't remove this or there won't be anything in your question!");
            } else if(_selected instanceof Operation) {
                switchRoot(((Operation)_selected).operandsProperty().get(0));

                // Sever connection with new _root. Seems odd, but ensures _root has no parent and is top of structure
                ((Operation)_selected).replace(0, new Range());
            }

            return;
        }

        if(_selected instanceof Range) {
            // Warn user
            new Alert(Alert.AlertType.WARNING,
                    "Are you sure you want to delete this operation (and replace it with the other range)")
                    .showAndWait()
                    .filter(type -> type == ButtonType.YES)
                    .ifPresent((type) -> {
                        // Choose NOT selected to save
                        Generatable saved = _selected.parent().operandsProperty().get(0) == _selected ?
                                _selected.parent().operandsProperty().get(1) :
                                _selected.parent().operandsProperty().get(0);

                        Operation op = _selected.parent();

                        if(op == _root)
                            switchRoot(saved);
                        else
                            op.parent().replace(op, saved);

                        select(saved);
                    });
        } else if(_selected instanceof Operation) {
            Generatable saved = ((Operation)_selected).operandsProperty().get(0);

            _selected.parent().replace(_selected, saved);

            select(saved);
        }
    }

    private int             startOf(Pair<Generatable.Tag, Integer> tag) {
        return tag.getValue();
    }
    private int             endOf(Pair<Generatable.Tag, Integer> tag) {
        return tag.getValue() + tag.getKey().text.length();
    }

    private StringProperty  _serializeProperty = new SimpleStringProperty();

    private HBox            _main = new HBox(15);
    private TextFlow        _textFlow = new TextFlow();
    private Button          _addBtn = new Button("Add");
    private Button          _deleteBtn = new Button("Delete");

    private Generatable     _selected;
    private Region          _selectedControl;

    private Generatable     _root;

    // TODO: Give to CSS to choose?
    // Colours for the text flow
    private static final Color colours[] = new Color[]{
            Color.BLANCHEDALMOND,
            Color.ALICEBLUE,
            Color.ROSYBROWN,
            Color.AZURE
    };
}
