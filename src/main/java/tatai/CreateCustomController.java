package tatai;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import tatai.controls.CustomQuestionControl;
import tatai.model.test.TestJson;
import tatai.model.test.TestParser;
import tatai.model.user.User;
import util.Files;
import util.SafeTextFieldTableCellIntegerFormatter;

import java.util.Arrays;
import java.util.Optional;

public class CreateCustomController extends Controller {

    /**
     * Used by the table view to hold prerequisite information
     */
    public static class Prerequisite {
        Prerequisite() {
            name.setValue("");
            score.setValue(0);
            times.setValue(0);
        }
        Prerequisite(String name, int score, int times) {
            this.name.setValue(name);
            this.score.setValue(score);
            this.times.setValue(times);
        }
        Prerequisite(TestJson.Prerequisite prereq) {
            this(prereq.name, prereq.score, prereq.times);
        }

        public TestJson.Prerequisite    output() {
            TestJson.Prerequisite prereq = new TestJson.Prerequisite();
            prereq.name = name.getValue();
            prereq.score = score.getValue();
            prereq.times = times.getValue();

            return prereq;
        }

        // These are used for reflexive purposes
        @SuppressWarnings("unused")
        public StringProperty   nameProperty() {
            return name;
        }
        @SuppressWarnings("unused")
        public IntegerProperty  scoreProperty() {
            return score;
        }
        @SuppressWarnings("unused")
        public IntegerProperty  timesProperty() {
            return times;
        }

        public StringProperty   name = new SimpleStringProperty();
        public IntegerProperty  score = new SimpleIntegerProperty();
        public IntegerProperty  times = new SimpleIntegerProperty();
    }

    /**
     * Creates a new test
     */
    public CreateCustomController(User author) {
        loadFxml("CreateCustom");
        _author = author;

        // Questions setup
        questionList.setItems(_questions);
        questionList.setCellFactory(questionView -> new ListCell<TestJson.Question>(){
            @Override
            protected void updateItem(TestJson.Question question, boolean bln) {
                super.updateItem(question, bln);
                setText(question == null ? "" : question.question);
            }
        });
        questionList.getSelectionModel().selectedIndexProperty().addListener(
                (observableValue, oldVal, newVal) -> Platform.runLater(() -> selectQuestion(newVal.intValue())));

        // Prerequisite table setup
        TableColumn<Prerequisite, String> nameCol = new TableColumn<>("Test Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<Prerequisite, Integer> scoreCol = new TableColumn<>("Score");
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        scoreCol.setCellFactory(c -> new SafeTextFieldTableCellIntegerFormatter<>(0));

        TableColumn<Prerequisite, Integer> timesCol = new TableColumn<>("Times");
        timesCol.setEditable(true);
        timesCol.setCellValueFactory(new PropertyValueFactory<>("times"));
        timesCol.setCellFactory(c -> new SafeTextFieldTableCellIntegerFormatter<>(0));

        prerequisiteTable.setPlaceholder(new Label("No prerequisites"));
        prerequisiteTable.setItems(_prerequisites);
        prerequisiteTable.getColumns().addAll(nameCol, scoreCol, timesCol);

        addPrerequisiteBtn.setOnAction(e -> addPrerequisite());
        deletePrerequisiteBtn.setOnAction(e -> deletePrerequisite());

        addQuestionBtn.setOnAction(e -> addQuestion());
        deleteQuestionBtn.setOnAction(e -> deleteQuestion());

        createBtn.setOnAction(e -> createTest());

        addQuestion(); // Start with one question
    }

    /**
     * Modifies the given test
     */
    public CreateCustomController(User author, TestJson test) {
        this(author);

        randomizeCheck.setSelected(test.randomizeQuestions);
        practiceCheck.setSelected(test.practice);
        nameTxt.setText(test.name);

        _questions.setAll(test.questions);
        _prerequisites.setAll(Arrays.stream(test.prerequisites).map(Prerequisite::new).toArray(Prerequisite[]::new));
    }

    @Override
    protected boolean exit(ReturnState state) {
        if(state != ReturnState.FINISHED) {
            boolean willReturn = new Alert(Alert.AlertType.WARNING,
                    "Are you sure you want to exit and lose all modifications?",
                    ButtonType.YES, ButtonType.NO)
                    .showAndWait()
                    .filter(type -> type == ButtonType.YES).isPresent();

            return willReturn && super.exit(state); // Will only try to exit if willReturn is true. KEEP THAT IN MIND PLS.
        } else {
            return super.exit(state);
        }
    }

    private void createTest() {
        // Check constraints
        if(nameTxt.getText().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "You haven't named your test!").show();
            return;
        }

        if(Files.testFile(nameTxt.getText()).exists()) {
            Optional<ButtonType> type = new Alert(Alert.AlertType.WARNING,
                    "This will overwrite a previous test. Are you sure you want to do this?",
                    ButtonType.YES, ButtonType.NO)
                    .showAndWait();

            if(!type.isPresent() || type.get() != ButtonType.YES)
                return;
        }

        if(_questions.size() == 0) {
            new Alert(Alert.AlertType.ERROR, "Can't create a test with no questions!").show();
            return;
        }

        for(Prerequisite req : _prerequisites) {
            if(!Files.testFile(req.name.getValue()).exists()) {
                new Alert(Alert.AlertType.ERROR, "Can't have a requisite for the test: " + req.name +
                        ", it doesn't exist").show();
                return;
            }
            if(req.score.getValue() < 0 || req.times.getValue() < 0) {
                new Alert(Alert.AlertType.ERROR, "Requisite Scores and Times must be 1 or greater!").show();
                return;
            }
        }

        // Make it
        TestJson newTest = new TestJson();
        newTest.name = nameTxt.getText();
        newTest.author = _author.username;
        newTest.custom = true;
        newTest.order = -1; // Is custom so no order
        newTest.practice = practiceCheck.isSelected();
        newTest.randomizeQuestions = randomizeCheck.isSelected();

        newTest.questions = _questions.toArray(new TestJson.Question[0]);
        newTest.prerequisites = _prerequisites.stream().map(Prerequisite::output).toArray(TestJson.Prerequisite[]::new);

        TestParser.save(newTest);

        exit(ReturnState.FINISHED);
    }

    private void selectQuestion(int next) {
        if(next < 0) {
            getChildren().remove(_selectedQuestion);
            return;
        }

        if(_questionListener != null)
            _selectedQuestion.outputProperty().removeListener(_questionListener);

        getChildren().remove(_selectedQuestion);

        _selectedQuestion = new CustomQuestionControl(_questions.get(next));

        // Change the list view text when the selected question changes
        _questionListener = (observable, oldValue, newValue) -> _questions.set(next, newValue);
        _selectedQuestion.outputProperty().addListener(_questionListener);

        getChildren().add(getChildren().indexOf(createBtn), _selectedQuestion);
    }
    private void addQuestion() {
        _questions.add(new TestJson.Question());
        selectQuestion(_questions.size() - 1);
    }
    private void deleteQuestion() {
        int index = questionList.getSelectionModel().getSelectedIndex();
        if(index >= 0) {
            _questions.remove(index);

            // Stop listening to it
            _selectedQuestion.outputProperty().removeListener(_questionListener);
            _questionListener = null;

            // Display another question if possible
            if(_questions.size() > index)  // The next question
                selectQuestion(index);
            else if(_questions.size() > 0)  // Or the first question
                selectQuestion(0);
            else {                          // Or nothing
                getChildren().remove(_selectedQuestion);
                _selectedQuestion = null;
            }
        }
    }

    private void addPrerequisite() {
        _prerequisites.add(new Prerequisite());
    }
    private void deletePrerequisite() {
        int index = prerequisiteTable.getSelectionModel().getSelectedIndex();
        if(index >= 0) {
            _prerequisites.remove(index);
        }
    }

    private ListProperty<Prerequisite>          _prerequisites = new SimpleListProperty<>(FXCollections.observableArrayList());
    private ListProperty<TestJson.Question>     _questions = new SimpleListProperty<>(FXCollections.observableArrayList());
    private CustomQuestionControl               _selectedQuestion = null;
    private ChangeListener<TestJson.Question>   _questionListener = null;

    private User                                _author;

    // JavaFX controls
    @FXML private JFXTextField                  nameTxt;
    @FXML private CheckBox                      practiceCheck;
    @FXML private CheckBox                      randomizeCheck;
    @FXML private TableView<Prerequisite>       prerequisiteTable;
    @FXML private ListView<TestJson.Question>   questionList;
    @FXML private JFXButton                     addQuestionBtn;
    @FXML private JFXButton                     deleteQuestionBtn;
    @FXML private JFXButton                     addPrerequisiteBtn;
    @FXML private JFXButton                     deletePrerequisiteBtn;
    @FXML private JFXButton                     createBtn;
}
