package tatai;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.Views;

/**
 * Implements some basic funcitonality for switching from one controller to another.
 * Currently assumes the controller is also a root of type VBox. This might need changing in future
 * but atm is fine.
 */
public abstract class Controller extends VBox {
    /**
     * Displays this controller on the given stage.
     * @param onExit when the controller is ready to release control, onExit is called.
     */
    public void display(Stage stage, Runnable onExit) {
        _stage = stage;
        _onExit = onExit;

        _scene = new Scene(this);
        _stage.setScene(_scene);
    }

    // Loads the given FXML with this being root and controll
    protected void  loadFxml(String name) {
        Views.load(name, this, this);
    }

    /**
     * Switch to the given controller from this one
     */
    protected void  switchTo(Controller controller) {
        controller.display(_stage, this::switchFrom);
    }

    /**
     * Called if this controller has been returned control of a stage
     */
    protected void  onSwitchedBack() {}

    /**
     * Exits this controller
     */
    protected void  exit() {
        Platform.runLater(_onExit);
    }

    private void    switchFrom() {
        _stage.setScene(_scene);
        onSwitchedBack();
    }

    private Runnable    _onExit;
    private Stage       _stage;
    private Scene       _scene;
}
