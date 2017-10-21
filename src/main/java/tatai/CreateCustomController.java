package tatai;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import tatai.model.test.TestJson;
import tatai.model.test.TestParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateCustomController extends Controller{

    public CreateCustomController() {
        loadFxml("CreateCustom");
        validateInput();

        _btnCreate.setOnAction(event -> {
            // Right now this assumes all input is valid.
            createTest();
        });

        _btnCancel.setOnAction(event -> {
           Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to cancel this custom test creation?",
                    ButtonType.YES, ButtonType.NO);
           alert.showAndWait();
           if(alert.getResult() == ButtonType.YES) {
               exit();
           }
           else {
               alert.close();
           }
        });
    }

    private void validateInput() {

        // TODO: Check for all cases of invalid input.

        List<JFXTextField> input = getInput();
        input.forEach(e -> {
            e.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if(!newValue.matches("^[1-9][0-9]?$|^99$")) {
                        e.setText(newValue.replaceAll("[^\\d]", ""));
                    }
                    if (e.getText().length() > 2) {
                        String s = e.getText().substring(0, 2);
                        e.setText(s);
                    }
                }
            });

        });
    }

    private List<JFXTextField> getInput() {
        List<JFXTextField> input = new ArrayList<>();
        input.add(_txtTime);
        input.add(_txtTries);
        input.add(_txtRounds);
        input.add(_txtMin);
        input.add(_txtMax);

        return input;
    }

    private void createTest() {
        TestJson newTest = new TestJson();
        newTest.name = _txtName.getText();
        newTest.questions = new TestJson.Question[1];
        newTest.questions[0] = new TestJson.Question();
        newTest.questions[0].rounds = Integer.parseInt(_txtRounds.getText());
        newTest.questions[0].tries = Integer.parseInt(_txtTries.getText());
        newTest.questions[0].timelimit = Integer.parseInt(_txtTime.getText());
        newTest.questions[0].question = "(" + _txtMin.getText() + " to " + _txtMax.getText() + ")";

        try {
            TestParser.save("", newTest);
        } catch(IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

//    JavaFX controls
    @FXML private JFXTextField _txtName;
    @FXML private JFXTextField _txtAuthor;
    @FXML private JFXTextField _txtRounds;
    @FXML private JFXTextField _txtTries;
    @FXML private JFXTextField _txtTime;
    @FXML private JFXTextField _txtMin;
    @FXML private JFXTextField _txtMax;
    @FXML private JFXButton _btnCancel;
    @FXML private JFXButton _btnCreate;
}
