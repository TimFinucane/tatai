package tatai.controls;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import tatai.model.question.*;
import tatai.model.test.TestJson;
import util.Views;

import java.util.Optional;

/**
 * Allows the user to create their own question
 */
public class CustomQuestionControl extends TitledPane {
    static class RangeControl extends TitledPane {
        public RangeControl(Range range) {
            setText("Range");
            setAlignment(Pos.CENTER);

            GridPane main = new GridPane();
            main.setVgap(10);
            main.setHgap(5);
            main.setAlignment(Pos.CENTER);

            _minFld = new Spinner<>(1, 99, range.minProperty().get());
            _maxFld = new Spinner<>(1, 99, range.maxProperty().get());

            main.add(new Label("Min"), 0, 0);
            main.add(new Label("Max"), 0, 1);
            main.add(_minFld, 1, 0);
            main.add(_maxFld, 1, 1);

            setContent(main);

            range.minProperty().bind(_minFld.valueProperty());
            range.maxProperty().bind(_maxFld.valueProperty());
        }

        private Spinner<Integer> _minFld;
        private Spinner<Integer> _maxFld;

        private Range range;
    }
    static class OperationControl extends TitledPane {
        public OperationControl(Operation operation) {
            setAlignment(Pos.CENTER);
            setText("Operation");

            HBox main = new HBox(10);
            main.setAlignment(Pos.CENTER);

            GridPane grid = new GridPane();
            grid.setVgap(5);
            grid.setHgap(5);
            grid.setAlignment(Pos.CENTER);

            grid.add(_operatorBoxes[0], 0, 0);
            grid.add(_operatorBoxes[1], 1, 0);
            grid.add(_operatorBoxes[2], 0, 1);
            grid.add(_operatorBoxes[3], 1, 1);

            main.getChildren().addAll(_parenthesisedBox, grid);

            setContent(main);

            _operation = operation;

            // Initialize check boxes
            for(Operator op : operation.operatorsProperty()) {
                for(CheckBox box : _operatorBoxes) {
                    if(op.symbol().equals(box.getText())) {
                        box.setSelected(true);
                    }
                }
            }

            _parenthesisedBox.setSelected(_operation.enclosedProperty().getValue());

            _operation.enclosedProperty().bind(_parenthesisedBox.selectedProperty());
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

            // Add constraints to boxes
            for(CheckBox box : _operatorBoxes)
                box.selectedProperty().addListener((observed, old, newVal) -> applyCheckConstraints(box, newVal));
        }

        /**
         * Ensures that the user can never have no checkboxes selected
         */
        private void    applyCheckConstraints(CheckBox box, Boolean newVal) {
            // If the user is turning a box on the constraint holds
            if(newVal)
                return;

            // If another box is checked then the constraint holds
            for(CheckBox opBox : _operatorBoxes) {
                if(opBox.isSelected() && opBox != box)
                    return;
            }

            // The constraint doesnt hold. Block the user action
            // TODO: Message user?
            Platform.runLater(() -> box.setSelected(true));
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
     * Allows the user to modify the given question (in string form)
     */
    public CustomQuestionControl(TestJson.Question question) {
        Views.load("CustomQuestion", this, this);

        // Initialize allowable range for spinner
        triesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 2));
        roundsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 1));

        switchQuestion(question);

        updateFlow();
        select(_question.head());

        addBtn.setOnAction(event -> addOperation());
        deleteBtn.setOnAction(event -> remove());
        generateBtn.setOnAction(event -> generate());
    }

    /**
     * Changes to represent given question
     */
    public void                                 switchQuestion(TestJson.Question question) {
        if(_question != null)
            _question.headTagProperty().removeListener(_tagListener);

        _question = QuestionReader.read(question.question);
        switchRoot(_question.head());

        triesSpinner.getValueFactory().setValue(question.tries);
        roundsSpinner.getValueFactory().setValue(question.rounds);

        _question.headTagProperty().addListener(_tagListener);

        _output.unbind();
        _output.bind(Bindings.createObjectBinding(
                this::createQuestion,
                _question.headTagProperty(),
                triesSpinner.valueProperty(),
                roundsSpinner.valueProperty()));
    }
    public ObjectProperty<TestJson.Question>    outputProperty() {
        return _output;
    }

    /**
     * Updates the textflow with new tag structure
     */
    private void            updateFlow() {
        textFlow.getChildren().clear();

        updateFlow(_question.headTagProperty().getValue(), 0);
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

        depth += 1;

        // Colour text before the first tag as the root tag
        if(startOf(rootTag.tags[0]) > 0)
            addText(rootTag, 0, startOf(rootTag.tags[0]), depth);

        updateFlow(rootTag.tags[0].getKey(), depth);

        for(int i = 1; i < rootTag.tags.length; i++) {
            // Colour space between previous and this tag as root
            if(endOf(rootTag.tags[i-1]) < startOf(rootTag.tags[i]))
                addText(rootTag, endOf(rootTag.tags[i-1]), startOf(rootTag.tags[i]), depth);

            updateFlow(rootTag.tags[i].getKey(), depth);
        }

        // Colour text after last tag
        if(endOf(rootTag.tags[rootTag.tags.length-1]) < rootTag.text.length())
            addText(rootTag, endOf(rootTag.tags[rootTag.tags.length-1]), rootTag.text.length(), depth);
    }

    /**
     * Switches to viewing the QuestionPart the user clicked on
     */
    private void            select(Generatable part) {
        opBox.getChildren().remove(_selectedControl);
        _selected = part;

        if(part instanceof Range)
            _selectedControl = new RangeControl((Range)part);
        else
            _selectedControl = new OperationControl((Operation)part);

        opBox.getChildren().add(_selectedControl);
    }

    /**
     * Appends the given tag to the textFlow
     * @param depth Used for colour coordination
     */
    private void            addText(Generatable.Tag tag, int depth) {
        addText(tag, 0, tag.text.length(), depth);
    }
    /**
     * Appends the text from start to end in the given tag to the textFlow
     * @param depth Used for colour coordination
     */
    private void            addText(Generatable.Tag tag, int start, int end, int depth) {
        Label text = new Label(tag.text.substring(start, end));
        text.setPrefWidth(text.getMinWidth());
        text.setPrefHeight(text.getMinHeight());

        Color c = colours[depth % colours.length];
        text.setBackground(new Background(new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY)));

        // Ensure that when the user clicks this bit, we select it
        text.setOnMouseClicked(event -> select(tag.owner));

        textFlow.getChildren().add(text);
    }

    /**
     * Adds an operation that takes the selected QuestionPart as the left child, and submits itself in its place
     */
    private void            addOperation() {
        Operation oldParent = _selected.parent();
        Operation newOp = new Operation(_selected, new Range(), true, Operator.Type.ADD.create());

        if(_selected == _question.head())
            switchRoot(newOp);
        else {
            oldParent.replace(_selected, newOp);
            select(newOp);
        }
    }
    /**
     * Removes the selected questionPart.
     * If the part is a range, then the entire parent operation + that range is deleted
     */
    private void            remove() {
        // Special care must be taken
        if(_selected == _question.head()) {
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
            Optional<ButtonType> button = new Alert(Alert.AlertType.WARNING,
                    "This will delete the operation too. Are you sure?",
                    ButtonType.YES, ButtonType.NO)
                    .showAndWait();

            if(button.isPresent() && button.get() == ButtonType.YES) {
                // Choose NOT selected to save
                Generatable saved = _selected.parent().operandsProperty().get(0) == _selected ?
                        _selected.parent().operandsProperty().get(1) :
                        _selected.parent().operandsProperty().get(0);

                Operation op = _selected.parent();

                if(op == _question.head())
                    switchRoot(saved);
                else
                    op.parent().replace(op, saved);

                select(saved);
            }
        } else if(_selected instanceof Operation) {
            Generatable saved = ((Operation)_selected).operandsProperty().get(0);

            _selected.parent().replace(_selected, saved);

            select(saved);
        }
    }

    /**
     * Generates an example question for the user
     */
    private void            generate() {
        generateLbl.setText(_question.generate());
    }

    /**
     * Changes the question's root Generatable part
     */
    private void            switchRoot(Generatable root) {
        _question.switchHead(root);

        updateFlow();
    }

    private int             startOf(Pair<Generatable.Tag, Integer> tag) {
        return tag.getValue();
    }
    private int             endOf(Pair<Generatable.Tag, Integer> tag) {
        return tag.getValue() + tag.getKey().text.length();
    }

    /**
     * Creates the new/modified TestJson Question
     */
    public TestJson.Question                createQuestion() {
        TestJson.Question output = new TestJson.Question();
        output.question = _question.headTagProperty().getValue().text;
        output.tries = triesSpinner.getValue();
        output.rounds = roundsSpinner.getValue();

        return output;
    }

    // TODO: Give to CSS to choose?
    // Colours for the text flow
    private static final Color colours[] = new Color[]{
            Color.BLANCHEDALMOND,
            Color.ALICEBLUE,
            Color.ROSYBROWN,
            Color.AZURE
    };

    private ChangeListener<Generatable.Tag> _tagListener = (ObservableValue<? extends Generatable.Tag> obs,
                                                            Generatable.Tag oldTag,
                                                            Generatable.Tag newTag) ->
                                                                updateFlow();
    private ObjectProperty<TestJson.Question> _output = new SimpleObjectProperty<>();

    private Generatable     _selected;
    private Region          _selectedControl;
    private Question        _question;

    @FXML private VBox              opBox;
    @FXML private FlowPane          textFlow;
    @FXML private JFXButton         addBtn;
    @FXML private JFXButton         deleteBtn ;
    @FXML private Spinner<Integer>  triesSpinner;
    @FXML private Spinner<Integer>  roundsSpinner;
    @FXML private JFXButton         generateBtn;
    @FXML private Label             generateLbl;
}
