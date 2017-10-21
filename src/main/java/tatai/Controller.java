package tatai;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import util.Views;

/**
 * Implements some basic funcitonality for switching from one controller to another.
 */
public abstract class Controller extends VBox {
    /**
     * Displays this controller on the given stage.
     */
    public void     display(Pane root) {
        root.getChildren().add(this);
    }

    /**
     * Exits this controller
     */
    public void     exit() {
        parent().getChildren().remove(this);

        if(_onExit != null)
            _onExit.run();
    }

    /**
     * Notifies the runnable when the controller has exited
     */
    public void     onExit(Runnable runnable) {
        _onExit = runnable;
    }

    protected void  displayChild(Controller controller) {
        Pane parent = parent();
        Runnable childExit = controller._onExit;

        parent.getChildren().remove(this);
        controller.onExit(() -> {
            childExit.run();
            display(parent);
        });

        controller.display(parent);
    }

    // Loads the given FXML with this being root and controller
    protected void  loadFxml(String name) {
        Views.load(name, this, this);
    }

    protected Pane  parent() {
        return (Pane)getParent();
    }

    private Runnable    _onExit;
}
