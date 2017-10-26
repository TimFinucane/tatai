package tatai.controls;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import tatai.model.question.*;
import tatai.model.test.TestJson;
import util.NumberConstraint;
import util.Views;

import java.util.Optional;

import static util.SpinnerFixes.assign;
import static util.SpinnerFixes.tieMinMax;

/**
 * Allows the user to create their own question
 */
public class CustomQuestionControl extends TitledPane {

    /**
     * Allows the user to modify the given question (in string form)
     */
    public CustomQuestionControl(TestJson.Question question) {
        Views.load("CustomQuestion", this, this);

        // Initialize allowable range for spinner
        assign(triesSpinner, 1, 1000, 2);
        assign(roundsSpinner, 1, 1000, 1);
        assign(minSpinner, 1, 99, 1);
        assign(maxSpinner, 1, 99, 99);

        // Ensure that min is never greater than max
        tieMinMax(minSpinner, maxSpinner);

        // Don't know whether JavaFX 8u40 is supported. Enforces a number only
        timelimitTxt.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[\\d.]*"))
                Platform.runLater(() -> timelimitTxt.setText(oldValue));
        });

        switchQuestion(question);

        addBtn.setOnAction(event -> addOperation());
        deleteBtn.setOnAction(event -> remove());
        generateBtn.setOnAction(event -> generate());
    }

    /**
     * Changes to represent given question
     */
    private void                                switchQuestion(TestJson.Question question) {
        if(_question != null)
            _question.tagProperty().removeListener(_tagListener);

        _question = new Question(question);
        switchRoot(_question.head());

        // Set question values
        triesSpinner.getValueFactory().setValue(question.tries);
        roundsSpinner.getValueFactory().setValue(question.rounds);
        minSpinner.getValueFactory().setValue(question.min);
        maxSpinner.getValueFactory().setValue(question.max);

        _question.tagProperty().addListener(_tagListener);

        _output.unbind();
        _output.bind(Bindings.createObjectBinding(
                // Create the question info
                () ->new TestJson.Question(
                        _question.tagProperty().getValue().text,
                        triesSpinner.getValue(),
                        roundsSpinner.getValue(),
                        timelimitTxt.getText().equals("") ? -1.0 : Double.parseDouble(timelimitTxt.getText()),
                        minSpinner.getValue(),
                        maxSpinner.getValue()),
                // Relies on these properties
                _question.tagProperty(),
                triesSpinner.valueProperty(),
                roundsSpinner.valueProperty())
        );

        updateFlow();
        select(_question.head());
    }
    public ObjectProperty<TestJson.Question>    outputProperty() {
        return _output;
    }

    /**
     * Updates the textflow with new tag structure
     */
    private void            updateFlow() {
        textFlow.getChildren().clear();
        updateFlow(_question.tagProperty().getValue(), 0);
    }

    /**
     * Updates the textflow with new tag structure
     * @param depth Used for colour coordination, determines colour of given text
     */
    private void            updateFlow(QuestionPart.Tag rootTag, int depth) {
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
    private void            select(QuestionPart part) {
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
    private void            addText(QuestionPart.Tag tag, int depth) {
        addText(tag, 0, tag.text.length(), depth);
    }
    /**
     * Appends the text from start to end in the given tag to the textFlow
     * @param depth Used for colour coordination
     */
    private void            addText(QuestionPart.Tag tag, int start, int end, int depth) {
        Label text = new Label(tag.text.substring(start, end));
        text.setPrefWidth(text.getMinWidth());
        text.setPrefHeight(text.getMinHeight());

        text.pseudoClassStateChanged(PseudoClass.getPseudoClass("flow-text"), true);
        if(depth % 2 == 1)
            text.pseudoClassStateChanged(PseudoClass.getPseudoClass("flow-text-odd"), true);

        // Ensure that when the user clicks this bit, we select it
        text.setOnMouseClicked(event -> select(tag.owner));

        textFlow.getChildren().add(text);
    }

    /**
     * Adds an operation that takes the selected QuestionPart as the left child, and submits itself in its place
     */
    private void            addOperation() {
        Operation oldParent = _selected.parent();
        Operation newOp = new Operation(_selected, new Range(), true, Operator.ADD);

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
            }
        } else {
            if(_selected instanceof Range) {
                // Warn user
                Optional<ButtonType> button = new Alert(Alert.AlertType.WARNING,
                        "This will delete the operation too. Are you sure?",
                        ButtonType.YES, ButtonType.NO).showAndWait();

                if(button.isPresent() && button.get() == ButtonType.YES) {
                    // Replace the operation with the part the user did NOT select
                    QuestionPart saved = _selected.parent().operandsProperty().get(0) == _selected ?
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
                QuestionPart saved = ((Operation) _selected).operandsProperty().get(0);
                _selected.parent().replace(_selected, saved);
                select(saved);
            }
        }
    }

    /**
     * Generates an example question for the user
     */
    private void            generate() {
        generateLbl.setText(_question.head().generate(
                new NumberConstraint(minSpinner.getValue(), maxSpinner.getValue())
        ).getKey());
    }

    /**
     * Changes the question's root QuestionPart part
     */
    private void            switchRoot(QuestionPart root) {
        if(root.parent() != null)
            root.parent().replace(root, new Range()); // Ensures the head we are switching to has no parent

        _question.switchHead(root);
        updateFlow();
    }

    private int             startOf(Pair<QuestionPart.Tag, Integer> tag) {
        return tag.getValue();
    }
    private int             endOf(Pair<QuestionPart.Tag, Integer> tag) {
        return tag.getValue() + tag.getKey().text.length();
    }

    private ChangeListener<QuestionPart.Tag> _tagListener = (ObservableValue<? extends QuestionPart.Tag> obs,
                                                             QuestionPart.Tag oldTag,
                                                             QuestionPart.Tag newTag) ->
                                                                updateFlow();
    private ObjectProperty<TestJson.Question> _output = new SimpleObjectProperty<>();

    private QuestionPart _selected;
    private Region          _selectedControl;
    private Question        _question;

    @FXML private VBox              opBox;
    @FXML private FlowPane          textFlow;
    @FXML private JFXButton         addBtn;
    @FXML private JFXButton         deleteBtn ;
    @FXML private Spinner<Integer>  triesSpinner;
    @FXML private Spinner<Integer>  roundsSpinner;
    @FXML private JFXTextField      timelimitTxt;
    @FXML private JFXButton         generateBtn;
    @FXML private Label             generateLbl;
    @FXML private Spinner<Integer>  minSpinner;
    @FXML private Spinner<Integer>  maxSpinner;
}