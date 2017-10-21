package tatai.select;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tatai.CreateCustomController;
import tatai.model.ScoreKeeper;
import tatai.model.test.TestJson;
import util.Views;

/**
 * A SelectController which adds functionality for
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
        Region region = new Region();

        getChildren().addAll(region, Views.load("select/TestAdditions", this, null));
        setVgrow(region, Priority.SOMETIMES);
    }

    /**
     * Create a custom test
     */
    @FXML
    void    createCustom(ActionEvent ignored) {
        displayChild(new CreateCustomController());
    }

    /**
     * Delete a test
     */
    @FXML
    void    deleteCustom(ActionEvent ignored) {
        throw new NotImplementedException();
    }


}
