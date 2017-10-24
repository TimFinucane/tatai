package tatai.select;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import tatai.CreateCustomController;
import tatai.model.ScoreKeeper;
import tatai.model.test.TestJson;
import util.Files;
import util.Views;

import java.util.Optional;

/**
 * A SelectController which adds functionality for choosing a test to run
 */
public abstract class SelectTestController extends SelectController {
    public static class Practice extends SelectTestController {
        public Practice() {super("");}
        @Override
        protected boolean   filter(TestJson test) {
            return test.practice;
        }
        @Override
        protected void      buttonPressed(TestJson test) {
            displayChild(new tatai.TestController(test));
        }
    }
    public static class Normal extends SelectTestController {
        public Normal(String user) {
            super(user);
            _keeper = new ScoreKeeper(user);
        }
        @Override
        protected boolean   filter(TestJson test) {
            return !test.practice;
        }
        @Override
        protected void      buttonPressed(TestJson test) {
            tatai.TestController controller = new tatai.TestController(test);
            controller.onExit((state) -> {
                if(state == ReturnState.FINISHED)
                    _keeper.addScore(test.name, controller.score());
            });

            displayChild(controller);
        }

        private ScoreKeeper _keeper;
    }

    private SelectTestController(String user) {
        super(user, "Choose a test to play"); // Haha

        // Display test additions in bottom right corner
        Region height = new Region();
        Region width = new Region();

        HBox bottom = new HBox(0, width, Views.load("select/TestAdditions", this, null));
        HBox.setHgrow(width, Priority.SOMETIMES);

        bottom.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(2), new BorderWidths(2))));

        getChildren().addAll(height, bottom);
        setVgrow(height, Priority.SOMETIMES);
    }

    /**
     * Create a custom test
     */
    @FXML
    void    createCustom(ActionEvent ignored) {
        displayChild(new CreateCustomController(user));
    }

    /**
     * Modifies an existing test
     */
    @FXML
    void    editCustom(ActionEvent ignored) {
        displayChild(new SelectController("", "Select a custom test to edit") {
            @Override
            boolean     filter(TestJson test) {
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
        displayChild(new SelectController("", "Select a custom test to delete") {
            @Override
            boolean     filter(TestJson test) {
                return test.custom;
            }
            @Override
            void        buttonPressed(TestJson json) {
                Optional<ButtonType> type = new Alert(Alert.AlertType.WARNING,
                        "Are you sure you want to delete " + json.name + "?",
                        ButtonType.YES, ButtonType.NO)
                        .showAndWait();

                if(type.isPresent() && type.get() == ButtonType.YES)
                    Files.scoreFile(json.name).delete();

                exit(ReturnState.FINISHED);
            }
        });
    }
}
