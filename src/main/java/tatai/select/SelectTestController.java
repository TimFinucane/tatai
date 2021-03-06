package tatai.select;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Pair;
import tatai.CreateCustomController;
import tatai.TestController;
import tatai.model.test.TestJson;
import tatai.model.test.TestParser;
import tatai.model.user.ScoreKeeper;
import tatai.model.user.User;
import util.Files;
import util.Views;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

/**
 * A SelectController which adds functionality for choosing a test to run
 */
public abstract class SelectTestController extends SelectController {
    public static class Practice extends SelectTestController {
        public Practice(User user) {super(user);}
        @Override
        protected boolean   filter(TestJson test, JFXButton button) {
            return test.practice && super.filter(test, button);
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
        protected boolean   filter(TestJson test, JFXButton button) {
            if(test.practice || !super.filter(test, button))
                return false;

            // Check if test is saved by user
            if(user.getUnfinishedTest() != null && user.getUnfinishedTest().name.equals(test.name)) {
                button.setTextFill(Color.RED);
                button.setTooltip(new Tooltip("This test has been started but not completed"));
            }

            return true;
        }
        @Override
        protected void      buttonPressed(TestJson test) {
            tatai.TestController controller;

            if(user.getUnfinishedTest() != null && user.getUnfinishedTest().name.equals(test.name))
                controller = new TestController(test, user.getUnfinishedTest().memento);
            else
                controller = new TestController(test);

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

        getChildren().addAll(Views.load("select/TestAdditions", this, null));
    }

    @Override
    protected boolean filter(TestJson test, JFXButton button) {
        if(!filterPrerequisites(test, button))
            return false;

        if(user != null)
            applyColourChange(test, button);

        return true;
    }

    /**
     * Performs the normal filtering (as in Select.filter) for prerequisites.
     * If there are too many prerequisites the button isnt displayed. If there's one
     * prerequisite then it is displayed, but disabled.
     */
    private boolean filterPrerequisites(TestJson test, JFXButton button) {
        // Check whether prerequisites have been fulfilled
        if(test.prerequisites == null)
            return true;

        if(test.prerequisites.length > 0 && user == null)
            return false;

        Pair<Integer, TestJson.Prerequisite> prereqs = countPrerequisites(test);

        // Apply changes based on how close to unlocking the test the user is
        if(prereqs.getKey() > 1) // Skip it
            return false;
        else if(prereqs.getKey() == 1) { // Let them see that they dont have to do much more to get it
            // Check whether THAT prerequisite has been fulfilled first though
            try {
                if(countPrerequisites(TestParser.read(prereqs.getValue().name)).getKey() > 0)
                    return false;
            } catch(FileNotFoundException e) { /* Do nothing, pretend we dont have the prerequisite */ }

            button.setOnAction((e) -> {});
            button.pseudoClassStateChanged(PseudoClass.getPseudoClass("false-disable"), true);
            button.setTooltip(new Tooltip("You only need to get better on the " + prereqs.getValue().name + " test!"));

        }
        return true;
    }

    /**
     * Counts the number of unfulfilled prerequisites the given test has. If there are more then 0, one
     * of them will be returned with the pair
     */
    private Pair<Integer, TestJson.Prerequisite> countPrerequisites(TestJson test) {
        int prerequisitesUnfulfilled = 0;
        TestJson.Prerequisite lastUnfulfilled = null;

        if(test.prerequisites == null)
            return new Pair<>(prerequisitesUnfulfilled, lastUnfulfilled);

        for(TestJson.Prerequisite prerequisite : test.prerequisites) {
            User.Score[] prereqScores = new ScoreKeeper(user, prerequisite.name).getScores();
            // Check whether the prereq has been completed, and if so
            if(Arrays.stream(prereqScores).filter(score -> score.score >= prerequisite.score).count() <
               prerequisite.times) {
                    prerequisitesUnfulfilled++;
                    lastUnfulfilled = prerequisite;
            }
        }

        return new Pair<>(prerequisitesUnfulfilled, lastUnfulfilled);
    }

    /**
     * Changes the colour of the test button based on the high score (the better the greener)
     */
    private void    applyColourChange(TestJson test, JFXButton button) {
        // Make the button greener the more high scores they get
        Optional<Integer> highScore = Arrays.stream(new ScoreKeeper(user, test.name).getScores()).map(score -> score.score).max(Comparator.naturalOrder());
        double maxScore = (double)test.rounds();

        highScore.ifPresent((Integer score) ->
                button.textFillProperty().addListener(new ChangeListener<Paint>() {
                    Color oldVal;

                    @Override
                    public void changed(ObservableValue<? extends Paint> observable, Paint oldValue, Paint newValue) {
                        double modGreen = score / maxScore;
                        double modNorm = 1 - modGreen;

                        Color c = (Color) newValue;

                        Color nextVal = Color.color(
                                c.getRed() * modNorm,
                                c.getGreen() * modNorm + modGreen,
                                c.getRed() * modNorm);

                        if(!newValue.equals(oldVal))
                            Platform.runLater(() -> button.textFillProperty().setValue(nextVal));

                        oldVal = nextVal;
                    }
                }));
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

                if(type.isPresent() && type.get() == ButtonType.YES) {
                    Files.testFile(json.name).delete();

                    // Remove from users
                    for(String username : Files.listUsers()) {
                        new User(username).clear(json.name);
                    }
                }
                exit(ReturnState.FINISHED);
            }
        });
    }
}
