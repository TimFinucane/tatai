package tatai.select;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Paint;
import tatai.Controller;
import tatai.model.test.TestJson;
import tatai.model.test.TestParser;
import tatai.model.user.User;
import util.Files;

import java.io.FileNotFoundException;

/**
 * A window in which the user can select a test
 */
public abstract class SelectController extends Controller {
    SelectController(User user, String title) {
        loadFxml("select/Select");
        createBasicTests();

        this.user = user;
        this.titleLbl.setText(title);

        refreshButtons();
    }

    /**
     * Does two things: Will return whether or not the given test should be displayed as an option,
     * and (optionally) modifies the (already set up) button based on the test.
     */
    abstract boolean    filter(TestJson test, JFXButton button);

    /**
     * Is called when the given button is pressed
     */
    abstract void       buttonPressed(TestJson test);

    /**
     * Displays all tests in the flow pane
     */
    private void        refreshButtons() {
        flowPane.getChildren().clear();

        for(String testName : Files.listTests()) {
            TestJson info;
            try {
                info = TestParser.read(testName);
            } catch(FileNotFoundException e) {
                continue; /* Skip */
            }

            JFXButton button = new JFXButton(testName);
            button.setRipplerFill(Paint.valueOf("dddddd"));
            button.setTextFill(Paint.valueOf("000000"));
            button.setStyle("-fx-background-color: #00aced");

            button.setOnAction(e -> buttonPressed(info));

            if(!filter(info, button))
                continue;

            // If this model asked to be put in a specific place, put it there
            if(!info.custom && info.order >= 0)
                flowPane.getChildren().add(Math.min(info.order, flowPane.getChildren().size()), button);
            else
                flowPane.getChildren().add(button);
        }
    }

    /**
     * Temporary method for creating basic tests if they are not already made
     */
    private static void createBasicTests() {
        if(Files.listTests().contains("Easy"))
            return;

        TestJson basic = new TestJson();
        basic.custom = false;
        basic.author = "Tatai";
        basic.practice = false;

        basic.name = "Easy";
        basic.order = 0;
        basic.questions = new TestJson.Question[1];
        basic.questions[0] = new TestJson.Question();
        basic.questions[0].rounds = 10;
        basic.questions[0].tries = 2;
        basic.questions[0].question = "(1 to 9) [+] (1 to 9)";
        basic.questions[0].min = 1;
        basic.questions[0].max = 9;

        TestParser.save(basic);

        basic.name = "Hard";
        basic.order = 1;
        basic.randomizeQuestions = true;
        basic.questions = new TestJson.Question[3];
        basic.questions[0] = new TestJson.Question();
        basic.questions[0].rounds = 4;
        basic.questions[0].tries = 2;
        basic.questions[0].question = "(1 to 99) [\uFF0B] (1 to 99)";
        basic.questions[0].min = 1;
        basic.questions[0].max = 99;

        basic.questions[1] = new TestJson.Question();
        basic.questions[1].rounds = 3;
        basic.questions[1].tries = 2;
        basic.questions[1].question = "(1 to 9) [\u00D7, \uFF0B] (1 to 9)";
        basic.questions[1].min = 1;
        basic.questions[1].max = 99;

        basic.questions[2] = new TestJson.Question();
        basic.questions[2].rounds = 3;
        basic.questions[2].tries = 2;
        basic.questions[2].question = "(10 to 99) [\u00F7] (1 to 9)";
        basic.questions[2].min = 1;
        basic.questions[2].max = 99;

        basic.prerequisites = new TestJson.Prerequisite[] { new TestJson.Prerequisite("Easy", 8, 1) };

        TestParser.save(basic);

        basic.randomizeQuestions = false;
        basic.practice = true;
        basic.prerequisites = null;

        basic.name = "Simple";
        basic.order = 0;
        basic.questions = new TestJson.Question[1];
        basic.questions[0] = new TestJson.Question();
        basic.questions[0].rounds = 10;
        basic.questions[0].tries = 2;
        basic.questions[0].question = "(1 to 9)";
        basic.questions[0].min = 1;
        basic.questions[0].max = 9;

        TestParser.save(basic);

        basic.name = "Advanced";
        basic.order = 1;
        basic.questions = new TestJson.Question[1];
        basic.questions[0] = new TestJson.Question();
        basic.questions[0].rounds = 10;
        basic.questions[0].tries = 2;
        basic.questions[0].question = "(1 to 99)";
        basic.questions[0].min = 1;
        basic.questions[0].max = 99;

        TestParser.save(basic);
    }

    protected final User    user;

    //    JavaFX controls
    @FXML private Label     titleLbl;
    @FXML private FlowPane  flowPane;
}