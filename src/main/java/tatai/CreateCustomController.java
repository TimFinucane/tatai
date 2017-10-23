package tatai;

import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import tatai.model.test.TestJson;
import tatai.model.test.TestParser;

import java.io.IOException;

public class CreateCustomController extends Controller{

    public CreateCustomController() {
        loadFxml("CreateCustom");
    }

    private void createTest() {
        TestJson newTest = new TestJson();
        newTest.name = nameTxt.getText();

        try {
            TestParser.save("", newTest);
        } catch(IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Ensures the field must contain a number
     */
    private void validateAsNumber(JFXTextField field, String oldValue, String newValue) {
        if(!newValue.matches("^[1-9][0-9]?$")) {
            field.setText(oldValue);
        }
    }

    // JavaFX controls
    @FXML private JFXTextField nameTxt;
}
