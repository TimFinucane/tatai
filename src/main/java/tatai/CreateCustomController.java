package tatai;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import tatai.controls.CustomQuestionControl;
import tatai.model.test.TestJson;
import tatai.model.test.TestParser;

import java.io.IOException;

public class CreateCustomController extends Controller{
    private static class Prerequisite {

    }

    public CreateCustomController() {
        loadFxml("CreateCustom");

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

        addQuestionBtn.setOnAction(e -> addQuestion());

        addQuestion(); // Start with one question
    }

    private void createTest() {
        TestJson newTest = new TestJson();
        newTest.name = nameTxt.getText();
        newTest.custom = true;
        newTest.order = -1;
        newTest.practice = practiceCheck.isSelected();
        newTest.randomizeQuestions = randomizeCheck.isSelected();

        newTest.questions = (TestJson.Question[])_questions.toArray();

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

        if(_selectedQuestion == null)
            _selectedQuestion = new CustomQuestionControl(_questions.get(next));
        else
            _selectedQuestion.switchQuestion(_questions.get(next));

        if(_questionListener != null)
            _selectedQuestion.outputProperty().removeListener(_questionListener);

        _questionListener = (observable, oldValue, newValue) -> _questions.set(next, newValue);
        _selectedQuestion.outputProperty().addListener(_questionListener);

        if(!customPane.getChildren().contains(_selectedQuestion))
            customPane.getChildren().setAll(_selectedQuestion);
    }
    private void addQuestion() {
        _questions.add(new TestJson.Question());
        selectQuestion(_questions.size() - 1);
    }

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
    @FXML private Pane                          customPane;
}
