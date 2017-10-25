package tatai.select;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import tatai.Controller;
import tatai.model.test.TestJson;
import tatai.model.test.TestParser;
import tatai.model.user.ScoreKeeper;
import tatai.model.user.User;
import util.Files;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

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

    abstract boolean    filter(TestJson test);
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

            if(!filter(info))
                continue;

            JFXButton button = new JFXButton(testName);
            button.setRipplerFill(Paint.valueOf("dddddd"));
            button.setTextFill(Paint.valueOf("ffffff"));

            button.setOnAction(e -> buttonPressed(info));

            // Check whether prerequisites have been fulfilled
            if(!info.practice && info.prerequisites != null) {
                int prerequisitesUnfulfilled = 0;
                TestJson.Prerequisite lastUnfulfilled = null;

                for( TestJson.Prerequisite prerequisite : info.prerequisites ) {
                    User.Score[] prereqScores = new ScoreKeeper(user, prerequisite.name).getScores();
                    if(Arrays.stream(prereqScores).filter(score -> score.score >= prerequisite.score).count() <
                            prerequisite.times) {
                        prerequisitesUnfulfilled++;
                        lastUnfulfilled = prerequisite;
                    }
                }

                if(prerequisitesUnfulfilled > 1) // Skip it
                    continue;
                else if(prerequisitesUnfulfilled == 1) { // Let them see that they dont have to do much more to get it
                    button.setOnAction((e) -> {});
                    button.setTextFill(Paint.valueOf("555555"));
                    button.setRipplerFill(Color.TRANSPARENT);
                    button.setTooltip(new Tooltip("You only need to get better on the " + lastUnfulfilled.name + " test!"));
                }
            }

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
        if( Files.listTests().contains("Easy"))
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
        basic.questions[0].question = "(5 to 9) [+] (1 to 4)";

        try {
            TestParser.save(basic);
        } catch(IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        basic.name = "Hard";
        basic.order = 1;
        basic.randomizeQuestions = true;
        basic.questions = new TestJson.Question[3];
        basic.questions[0] = new TestJson.Question();
        basic.questions[0].rounds = 4;
        basic.questions[0].tries = 2;
        basic.questions[0].question = "(1 to 25) [+] (1 to 75)";

        basic.questions[1] = new TestJson.Question();
        basic.questions[1].rounds = 3;
        basic.questions[1].tries = 2;
        basic.questions[1].question = "(1 to 9) [\u00D7, +] (1 to 9)";

        basic.questions[2] = new TestJson.Question();
        basic.questions[2].rounds = 3;
        basic.questions[2].tries = 2;
        basic.questions[2].question = "(10 to 99) [\u00F7] (1 to 9)";

        basic.prerequisites = new TestJson.Prerequisite[] { new TestJson.Prerequisite("Easy", 8, 1) };

        try {
            TestParser.save(basic);
        } catch(IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        basic.randomizeQuestions = false;
        basic.practice = true;

        basic.name = "Simple";
        basic.order = 0;
        basic.questions = new TestJson.Question[1];
        basic.questions[0] = new TestJson.Question();
        basic.questions[0].rounds = 10;
        basic.questions[0].tries = 2;
        basic.questions[0].question = "(1 to 9)";

        try {
            TestParser.save(basic);
        } catch(IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        basic.name = "Advanced";
        basic.order = 1;
        basic.questions = new TestJson.Question[1];
        basic.questions[0] = new TestJson.Question();
        basic.questions[0].rounds = 10;
        basic.questions[0].tries = 2;
        basic.questions[0].question = "(1 to 99)";

        try {
            TestParser.save(basic);
        } catch(IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    protected final User    user;

    //    JavaFX controls
    @FXML private Label     titleLbl;
    @FXML private FlowPane  flowPane;
}



