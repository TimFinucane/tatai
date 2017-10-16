package tatai;

import com.jfoenix.controls.JFXButton;
import com.sun.media.jfxmedia.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Paint;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tatai.model.test.Test;
import tatai.model.test.TestJson;
import tatai.model.test.TestParser;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A window where you can select which test you want to do
 */
public class SelectTestController extends Controller {

    public SelectTestController() {
        loadFxml("SelectTest");
        createBasicTests();

        refreshButtons();

        _btnCreateCustom.setOnAction(event -> createCustom());
        _btnRemoveCustom.setOnAction(event -> removeCustom());
    }

    // Called when create custom button pressed
    private void createCustom() {
	    throw new NotImplementedException();
	    //TODO: creates a custom test.
    }

    // Called when remove custom button pressed
    private void removeCustom() {
	    throw new NotImplementedException();
	    //TODO: removes a custom test
    }

    /**
     * Opens the given test and transfers control to it
     */
    protected void openTest(String name) {
        try {
            Test test = TestParser.read(name);
            _curTest = new TestController(test);
            switchTo(_curTest);
        } catch(FileNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, "There was a problem loading that test! Please try another")
                    .show();
            Logger.logMsg(Logger.ERROR, "Loading of test " + name + " failed: " + e.getMessage());
        }
    }

    /**
     * Displays all tests in the flow pane
     */
    protected void refreshButtons() {
        // TODO: Locking and unlocking would go here

        _paneFlow.getChildren().clear();

        for(String testName : TestParser.listTests()) {
            if(testName.equals("Easy Practice") || testName.equals("Advanced Practice")) {
                continue;
            }
            JFXButton button = new JFXButton(testName);

            AnchorPane.setLeftAnchor(_paneFlow,20.0);
            AnchorPane.setRightAnchor(_paneFlow, 20.0);


            button.setRipplerFill(Paint.valueOf("dddddd"));
            button.setTextFill(Paint.valueOf("ffffff"));

            button.setOnAction(e -> openTest(testName));

            _paneFlow.getChildren().add(button);
        }
    }

    @Override
    protected void onSwitchedBack() {
        // TODO: Switch over the test return state to decide what to do next
    }

    /**
     * Temporary method for creating basic tests if they are not already made
     */
    private static void createBasicTests() {
        if(TestParser.listTests().contains("Easy"))
            return;

        TestJson basic = new TestJson();
        basic.name = "Easy Test";
        basic.questions = new TestJson.Question[1];
        basic.questions[0] = new TestJson.Question();
        basic.questions[0].rounds = 10;
        basic.questions[0].tries = 2;
        basic.questions[0].question = "(1 to 9)";

        try {
            TestParser.makeTest(basic);
        } catch(IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        basic.name = "Hard Test";
        basic.questions = new TestJson.Question[1];
        basic.questions[0] = new TestJson.Question();
        basic.questions[0].rounds = 10;
        basic.questions[0].tries = 2;
        basic.questions[0].question = "(1 to 99)";

        try {
            TestParser.makeTest(basic);
        } catch(IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private TestController  _curTest;

    //    JavaFX Controls
    @FXML private FlowPane  _paneFlow;
    @FXML private Button    _btnCreateCustom;
    @FXML private Button    _btnRemoveCustom;

}



