package tatai;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
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
        questionList.setCellFactory(customQuestionView -> new ListCell<CustomQuestionControl>(){
            @Override
            protected void updateItem(CustomQuestionControl control, boolean bln) {
                super.updateItem(control, bln);
                if (control != null) {
                    textProperty().bind(control.serializeProperty());
                }
            }
        });

        questionList.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends CustomQuestionControl> val,
                 CustomQuestionControl oldVal,
                 CustomQuestionControl newVal) ->
                        Platform.runLater(() -> questionSelected(oldVal, newVal))
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

        newTest.questions = (TestJson.Question[])_questions.stream().map(control -> {
            TestJson.Question question = new TestJson.Question();
            question.question = control.serializeProperty().getValue();
            return question;
        }).toArray();

        try {
            TestParser.save(newTest);
        } catch(IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void questionSelected(CustomQuestionControl old, CustomQuestionControl next) {
        this.getChildren().remove(old);
        this.getChildren().add(next);
    }
    private void addQuestion() {
        _questions.add(new CustomQuestionControl());
    }

    private static final String             NEW_ITEM = "New";

    private ObservableList<CustomQuestionControl>   _questions = new SimpleListProperty<>(FXCollections.observableArrayList());

    // JavaFX controls
    @FXML private JFXTextField              nameTxt;
    @FXML private CheckBox                  practiceCheck;
    @FXML private CheckBox                  randomizeCheck;
    @FXML private TableView<Prerequisite>   prerequisiteTable;
    @FXML private ListView<CustomQuestionControl>   questionList;
    @FXML private JFXButton                 addQuestionBtn;
}
