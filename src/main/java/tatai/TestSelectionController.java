package tatai;

import com.sun.media.jfxmedia.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import tatai.model.test.Test;
import tatai.model.test.TestParser;
import util.Views;

import java.io.FileNotFoundException;

public class TestSelectionController extends Controller {
    public TestSelectionController() {
        // Load fxml, set self to act as controller and root
        Views.load("TestSelection", this, this);

        // Add tests
        refreshButtons();
    }

    private void refreshButtons() {
        // TODO: Locking and unlocking would go here
        flowPane.getChildren().clear();

        for(String testName : TestParser.listTests()) {
            Button button = new Button(testName);
            button.setOnAction(e -> openTest(testName));

            flowPane.getChildren().add(button);
        }
    }

    private void openTest(String name) {
        try {
            Test test = TestParser.make(TestParser.read(name));
            _curTest = new TestController(test);

            switchTo(_curTest);
        } catch(FileNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, "There was a problem loading that test! Please try another")
                .show();
            Logger.logMsg(Logger.ERROR, "Loading of test " + name + " failed: " + e.getMessage());
        }
    }

    @Override
    protected void onSwitchedBack() {
        // TODO: Switch over the test return state to decide what to do next

        refreshButtons();
    }

    private TestController  _curTest;

    @FXML private FlowPane  flowPane;
    @FXML private Button    deleteBtn;
}
