package tatai;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.util.converter.IntegerStringConverter;
import tatai.controls.CustomQuestionControl;
import tatai.model.test.TestJson;
import tatai.model.test.TestParser;
import util.Files;

import java.io.IOException;

public class CreateCustomController extends Controller {
    /**
     * Makes a safe version which prevents throwing exceptions at basic user error
     */
    public static class SafeIntegerStringConverter extends IntegerStringConverter {
        @Override
        public Integer fromString(String str) {
            try {
                return super.fromString(str);
            } catch(NumberFormatException e) {
                return -1;
            }
        }
    }

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

        public StringProperty   nameProperty() {
            return name;
        }
        public IntegerProperty scoreProperty() {
            return score;
        }
        public IntegerProperty timesProperty() {
            return times;
        }

        public StringProperty   name = new SimpleStringProperty();
        public IntegerProperty  score = new SimpleIntegerProperty();
        public IntegerProperty  times = new SimpleIntegerProperty();
    }

    public CreateCustomController() {
        loadFxml("CreateCustom");

        // Questions setup
        questionList.setItems(_questions);
        questionList.setCellFactory(questionView -> new ListCell<TestJson.Question>(){
            @Override
            protected void updateItem(TestJson.Question question, boolean bln) {
                super.updateItem(question, bln);
                if (question != null) {
                    setText(question.question);
                }
            }
        });
        questionList.getSelectionModel().selectedIndexProperty().addListener(
                (ObservableValue<? extends Number> val,
                 Number oldVal,
                 Number newVal) ->
                    Platform.runLater(() -> selectQuestion(newVal.intValue()))
        );

        // Prerequisite setup
        TableColumn<Prerequisite, String> nameCol = new TableColumn<>("Test Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setOnEditCommit(
                (TableColumn.CellEditEvent<Prerequisite, String> cell) ->
                    cell.getTableView().getItems().get(cell.getTablePosition().getRow()).name.set(cell.getNewValue())
                );


        TableColumn<Prerequisite, Integer> scoreCol = new TableColumn<>("Score");
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        scoreCol.setCellFactory(TextFieldTableCell.forTableColumn(new SafeIntegerStringConverter()));
        scoreCol.setOnEditCommit(
                (TableColumn.CellEditEvent<Prerequisite, Integer> cell) ->
                        intEditCommit(cell, cell.getTableView().getItems().get(cell.getTablePosition().getRow()).score)
                );

        TableColumn<Prerequisite, Integer> timesCol = new TableColumn<>("Times");
        timesCol.setEditable(true);
        timesCol.setCellValueFactory(new PropertyValueFactory<>("times"));
        timesCol.setCellFactory(TextFieldTableCell.forTableColumn(new SafeIntegerStringConverter()));
        timesCol.setOnEditCommit(
                (TableColumn.CellEditEvent<Prerequisite, Integer> cell) ->
                        intEditCommit(cell, cell.getTableView().getItems().get(cell.getTablePosition().getRow()).times)
                );

        prerequisiteTable.setPlaceholder(new Label("No prerequisites"));
        prerequisiteTable.setItems(_prerequisites);
        prerequisiteTable.getColumns().addAll(nameCol, scoreCol, timesCol);

        addPrerequisiteBtn.setOnAction(e -> addPrerequisite());
        addQuestionBtn.setOnAction(e -> addQuestion());
        createBtn.setOnAction(e -> createTest());

        addQuestion(); // Start with one question
    }

    private void createTest() {
        // Check constraints
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
        newTest.custom = true;
        newTest.order = -1;
        newTest.practice = practiceCheck.isSelected();
        newTest.randomizeQuestions = randomizeCheck.isSelected();

        newTest.questions = (TestJson.Question[])_questions.toArray();
        newTest.prerequisites = (TestJson.Prerequisite[])_prerequisites.stream().map(Prerequisite::output).toArray();

        try {
            TestParser.save(newTest);
        } catch(IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void selectQuestion(int next) {
        if(next < 0) {
            customPane.getChildren().clear();
            return;
        }

        if(_questionListener != null)
            _selectedQuestion.outputProperty().removeListener(_questionListener);

        _selectedQuestion = new CustomQuestionControl(_questions.get(next));

        _questionListener = (observable, oldValue, newValue) -> _questions.set(next, newValue);
        _selectedQuestion.outputProperty().addListener(_questionListener);

        customPane.getChildren().setAll(_selectedQuestion);
    }
    private void addQuestion() {
        _questions.add(new TestJson.Question());
        selectQuestion(_questions.size() - 1);
    }
    private void addPrerequisite() {
        _prerequisites.add(new Prerequisite());
    }

    private void intEditCommit(TableColumn.CellEditEvent<Prerequisite, Integer> cell, IntegerProperty property) {
        if(cell.getNewValue() < 0) { // Indicates an invalid result
            // workaround for refreshing rendered values
            cell.getTableView().getColumns().get(0).setVisible(false);
            cell.getTableView().getColumns().get(0).setVisible(true);
        } else {
            property.setValue(cell.getNewValue());
        }
    }

    private ListProperty<Prerequisite>          _prerequisites = new SimpleListProperty<>(FXCollections.observableArrayList());
    private ListProperty<TestJson.Question>     _questions = new SimpleListProperty<>(FXCollections.observableArrayList());
    private CustomQuestionControl               _selectedQuestion = null;
    private ChangeListener<TestJson.Question>   _questionListener = null;

    // JavaFX controls
    @FXML private JFXTextField                  nameTxt;
    @FXML private CheckBox                      practiceCheck;
    @FXML private CheckBox                      randomizeCheck;
    @FXML private TableView<Prerequisite>       prerequisiteTable;
    @FXML private ListView<TestJson.Question>   questionList;
    @FXML private JFXButton                     addQuestionBtn;
    @FXML private JFXButton                     addPrerequisiteBtn;
    @FXML private Pane                          customPane;
    @FXML private JFXButton                     createBtn;
}
