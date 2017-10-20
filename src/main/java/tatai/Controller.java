package tatai;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import util.Views;

/**
 * Implements some basic funcitonality for switching from one controller to another.
 */
public abstract class Controller extends VBox {
    /**
     * Displays this controller on the given stage.
     * @param onExit when the controller is ready to release control, onExit is called.
     */
    public void display(Pane root, Runnable onExit) {
        _root = root;
        _onExit = onExit;
        _root.getChildren().add(this);
    }

    // Loads the given FXML with this being root and controller
    protected void  loadFxml(String name) {
        Views.load(name, this, this);
    }

    /**
     * Switch to the given controller from this one
     */
    protected void  switchTo(Controller controller) {
        _root.getChildren().remove(this);
        _child = controller;
        controller.display(_root, this::switchFrom);
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

    /**
     * Called when child has released control to this
     */
    private void    switchFrom() {
        _root.getChildren().remove(_child);
        _root.getChildren().add(this);

        onSwitchedBack();
    }

    private Runnable    _onExit;
    private Pane        _root;

    private Parent      _child = null;
}
