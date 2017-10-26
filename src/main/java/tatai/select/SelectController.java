package tatai.select;

import com.jfoenix.controls.JFXButton;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
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
            button.pseudoClassStateChanged(PseudoClass.getPseudoClass("select-button"), true);

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

    protected final User    user;

    //    JavaFX controls
    @FXML private Label     titleLbl;
    @FXML private FlowPane  flowPane;
}