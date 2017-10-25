package tatai.select;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import tatai.CreateCustomController;
import tatai.model.test.TestJson;
import tatai.model.user.ScoreKeeper;
import tatai.model.user.User;
import util.Files;
import util.Views;

import java.util.Arrays;
import java.util.Optional;

/**
 * A SelectController which adds functionality for choosing a test to run
 */
public abstract class SelectTestController extends SelectController {
    public static class Practice extends SelectTestController {
        public Practice(User user) {super(user);}
        @Override
        protected boolean   select(TestJson test) {
            return test.practice;
        }
        @Override
        protected void      buttonPressed(TestJson test) {
            displayChild(new tatai.TestController(test));
        }
    }
    public static class Normal extends SelectTestController {
        public Normal(User user) {
            super(user);
        }
        @Override
        protected boolean   select(TestJson test) {
            return !test.practice;
        }
        @Override
        protected void      buttonPressed(TestJson test) {
            tatai.TestController controller = new tatai.TestController(test);
            controller.onExit((state) -> {
                if(state == ReturnState.FINISHED)
                    new ScoreKeeper(user, test.name).addScore(controller.score());
                else
                    user.saveTest(test.name, controller.save());
            });

            displayChild(controller);
        }
    }

    private SelectTestController(User user) {
        super(user, "Choose a test to play"); // Haha

        // Display test additions in bottom right corner
        Region height = new Region();
        Region width = new Region();

        HBox bottom = new HBox(0, width, Views.load("select/TestAdditions", this, null));
        HBox.setHgrow(width, Priority.SOMETIMES);

        getChildren().addAll(height, bottom);
        setVgrow(height, Priority.SOMETIMES);
    }

    protected abstract boolean select(TestJson test);

    @Override
    protected final boolean filter(TestJson test, JFXButton button) {
        if(!select(test))
            return false;

        // Check whether prerequisites have been fulfilled
        int prerequisitesUnfulfilled = 0;
        TestJson.Prerequisite lastUnfulfilled = null;

        for(TestJson.Prerequisite prerequisite : test.prerequisites) {
            User.Score[] prereqScores = new ScoreKeeper(user, prerequisite.name).getScores();
            if( Arrays.stream(prereqScores).filter(score -> score.score >= prerequisite.score).count() <
                    prerequisite.times) {
                prerequisitesUnfulfilled++;
                lastUnfulfilled = prerequisite;
            }
        }

        if(prerequisitesUnfulfilled > 1) // Skip it
            return false;
        else if(prerequisitesUnfulfilled == 1) { // Let them see that they dont have to do much more to get it
            button.setOnAction((e) -> {});
            button.setTextFill(Paint.valueOf("555555"));
            button.setRipplerFill(Color.TRANSPARENT);
            button.setTooltip(new Tooltip("You only need to get better on the " + lastUnfulfilled.name + " test!"));
        }

        return true;
    }

    /**
     * Create a custom test
     */
    @FXML
    void    createCustom(ActionEvent ignored) {
        if(user == null) {
            new Alert(Alert.AlertType.ERROR, "You must log in to complete this action.").show();
            return;
        }

        displayChild(new CreateCustomController(user));
    }

    /**
     * Modifies an existing test
     */
    @FXML
    void    editCustom(ActionEvent ignored) {
        if(user == null) {
            new Alert(Alert.AlertType.ERROR, "You must log in to complete this action.").show();
            return;
        }

        displayChild(new SelectController(user, "Select a custom test to edit") {
            @Override
            boolean     filter(TestJson test, JFXButton button) {
                return test.custom;
            }
            @Override
            void        buttonPressed(TestJson json) {
                displayChild(new CreateCustomController(user, json));
            }
        });
    }

    /**
     * Delete a test
     */
    @FXML
    void    deleteCustom(ActionEvent ignored) {
        if(user == null) {
            new Alert(Alert.AlertType.ERROR, "You must log in to complete this action.").show();
            return;
        }

        displayChild(new SelectController(user, "Select a custom test to delete") {
            @Override
            boolean     filter(TestJson test, JFXButton button) {
                return test.custom;
            }
            @Override
            void        buttonPressed(TestJson json) {
                Optional<ButtonType> type = new Alert(Alert.AlertType.WARNING,
                        "Are you sure you want to delete " + json.name + "?",
                        ButtonType.YES, ButtonType.NO)
                        .showAndWait();

                if(type.isPresent() && type.get() == ButtonType.YES)
                    Files.testFile(json.name).delete();

                exit(ReturnState.FINISHED);
            }
        });
    }
}
