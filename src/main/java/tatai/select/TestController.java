package tatai.select;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tatai.CreateCustomController;
import tatai.model.ScoreKeeper;
import tatai.model.test.TestJson;
import util.Views;

/**
 * A SelectController which adds functionality for choosing a test to run
 */
public abstract class TestController extends SelectController {
    public static class Practice extends TestController {
        protected boolean   filter(TestJson test) {
            return test.practice;
        }
        protected void      buttonPressed(TestJson test) {
            displayChild(new tatai.TestController(test));
        }
    }
    public static class Normal extends TestController {
        public Normal(ScoreKeeper keeper) {
            _keeper = keeper;
        }

        protected boolean   filter(TestJson test) {
            return !test.practice;
        }
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

    private TestController() {
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
        displayChild(new CreateCustomController("")); // TODO: Author
    }

    /**
     * Delete a test
     */
    @FXML
    void    deleteCustom(ActionEvent ignored) {
        throw new NotImplementedException();
    }


}
