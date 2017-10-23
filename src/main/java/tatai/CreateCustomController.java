package tatai;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import tatai.controls.CustomQuestionControl;
import tatai.model.question.Question;
import tatai.model.test.TestJson;
import tatai.model.test.TestParser;

import java.io.IOException;

public class CreateCustomController extends Controller{
    private static class Prerequisite {

    }

    public CreateCustomController() {
        loadFxml("CreateCustom");

        questionList.setItems(_questions);

        questionList.setCellFactory(questionView -> new ListCell<Question>(){
            @Override
            protected void updateItem(Question question, boolean bln) {
                super.updateItem(question, bln);
                if (question != null) {
                    textProperty().bind(
                            Bindings.createObjectBinding(() -> question.head().tagProperty().getValue().text)
                    );
                }
            }
        });

        questionList.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends Question> val,
                 Question oldVal,
                 Question newVal) ->
                        Platform.runLater(() -> select(newVal))
        );

        addQuestionBtn.setOnAction(e -> addQuestion());
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

    private void select(Question next) {
        if(next == null) {
            getChildren().remove(_selectedQuestion);
            return;
        }

        if(_selectedQuestion == null)
            _selectedQuestion = new CustomQuestionControl(next);
        else
            _selectedQuestion.switchQuestion(next);

        if(!getChildren().contains(_selectedQuestion))
            getChildren().add(_selectedQuestion);
    }
    private void addQuestion() {
        _questions.add(new Question());
        select(_questions.get(_questions.size() - 1));
    }

    private ListProperty<Question>          _questions = new SimpleListProperty<>(FXCollections.observableArrayList());

    private CustomQuestionControl           _selectedQuestion = null;

    // JavaFX controls
    @FXML private JFXTextField              nameTxt;
    @FXML private CheckBox                  practiceCheck;
    @FXML private CheckBox                  randomizeCheck;
    @FXML private TableView<Prerequisite>   prerequisiteTable;
    @FXML private ListView<Question>        questionList;
    @FXML private JFXButton                 addQuestionBtn;
}
