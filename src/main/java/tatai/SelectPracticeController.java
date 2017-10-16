package tatai;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Paint;
import tatai.model.test.TestJson;
import tatai.model.test.TestParser;

import java.io.IOException;

/**
 * A window where you can select which practice test you want to do
 */
public class SelectPracticeController extends SelectTestController {

    public SelectPracticeController() {
        loadFxml("SelectTest");
        createBasicTests();
        refreshButtons();
    }

    @Override
    protected void refreshButtons() {
        _btnRemoveCustom.setVisible(false);
        _btnCreateCustom.setVisible(false);
        _paneFlow.getChildren().clear();

        for(String testName : TestParser.listTests()) {
            if(testName.equals("Easy Practice") || testName.equals("Advanced Practice")) {
                JFXButton button = new JFXButton(testName);

                AnchorPane.setLeftAnchor(_paneFlow,20.0);
                AnchorPane.setRightAnchor(_paneFlow, 20.0);

                button.setRipplerFill(Paint.valueOf("dddddd"));
                button.setTextFill(Paint.valueOf("ffffff"));

                button.setOnAction(e -> openTest(testName));

                _paneFlow.getChildren().add(button);
            }
        }
    }

    /**
     * Temporary method for creating basic practice tests if they are not already made
     */
    private static void createBasicTests() {
        if(TestParser.listTests().contains("Easy Practice") || TestParser.listTests().contains("Advanced Practice"))
            return;

        TestJson basic = new TestJson();
        basic.name = "Easy Practice";
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

        basic.name = "Advanced Practice";
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
    @FXML private FlowPane _paneFlow;
    @FXML private JFXButton _btnCreateCustom;
    @FXML private JFXButton _btnRemoveCustom;
}
