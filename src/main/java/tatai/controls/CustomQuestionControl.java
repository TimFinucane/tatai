package tatai.controls;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
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

    /**
     * Allows the user to modify the given question (in string form)
     */
    public CustomQuestionControl(TestJson.Question question) {
        Views.load("CustomQuestion", this, this);

        // Initialize allowable range for spinner
        triesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 2));
        roundsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 1));

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
            _question.headTagProperty().removeListener(_tagListener);

        _question = QuestionReader.read(question.question);
        _question.headTagProperty().addListener(_tagListener);

        // Set question values
        triesSpinner.getValueFactory().setValue(question.tries);
        roundsSpinner.getValueFactory().setValue(question.rounds);
        timelimitTxt.setText(question.timelimit < 1.0 ? "" : Double.toString(question.timelimit));

        _output.unbind();
        _output.bind(Bindings.createObjectBinding(
                // Create the question info
                () ->new TestJson.Question(
                        _question.headTagProperty().getValue().text,
                        triesSpinner.getValue(),
                        roundsSpinner.getValue(),
                        timelimitTxt.getText().equals("") ? -1.0 : Double.parseDouble(timelimitTxt.getText())),
                // Relies on these properties
                _question.headTagProperty(),
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
                Generatable newRoot = ((Operation)_selected).operandsProperty().get(0);
                // Sever connection with new _root. Seems odd, but ensures _root has no parent and is top of structure
                ((Operation)_selected).replace(0, new Range());
                switchRoot(newRoot);
            }
        } else {
            if(_selected instanceof Range) {
                // Warn user
                Optional<ButtonType> button = new Alert(Alert.AlertType.WARNING,
                        "This will delete the operation too. Are you sure?",
                        ButtonType.YES, ButtonType.NO).showAndWait();

                if(button.isPresent() && button.get() == ButtonType.YES) {
                    // Replace the operation with the part the user did NOT select
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
                Generatable saved = ((Operation) _selected).operandsProperty().get(0);
                _selected.parent().replace(_selected, saved);
                select(saved);
            }
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
    @FXML private JFXTextField      timelimitTxt;
    @FXML private JFXButton         generateBtn;
    @FXML private Label             generateLbl;
}