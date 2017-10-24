package tatai;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import util.Views;

import java.util.function.Consumer;

/**
 * Implements some basic funcitonality for switching from one controller to another.
 */
public abstract class Controller extends VBox {
    public enum ReturnState {
        QUIT, // TODO: Use this to notify early exit if ever needed.
        FINISHED
    }

    /**
     * Displays this controller on the given stage.
     */
    public void     display(AnchorPane root) {
        root.getChildren().add(this);
        AnchorPane.setBottomAnchor(this, 0.0);
        AnchorPane.setTopAnchor(this, 0.0);
        AnchorPane.setLeftAnchor(this, 0.0);
        AnchorPane.setRightAnchor(this, 0.0);
    }

    /**
     * Exits this controller.
     * @return whether or not the controller actually relinquished control
     */
    public boolean      exit() {
        return exit(ReturnState.QUIT);
    }
    protected boolean   exit(ReturnState state) {
        // Try to exit child screen. If we can't, then don't bother
        if(_child != null && !_child.exit(state))
            return false;

        pane().getChildren().remove(this);

        if(_onExit != null)
            _onExit.accept(state);

        return true;
    }

    /**
     * Notifies the runnable when the controller has exited
     */
    public void     onExit(Consumer<ReturnState> runnable) {
        _onExit = runnable;
    }

    protected void  displayChild(Controller controller) {
        _child = controller;

        AnchorPane parent = pane();
        Consumer<ReturnState> childExit = controller._onExit;

        parent.getChildren().remove(this);
        controller.onExit((state) -> {
            display(parent);
            _child = null;

            if(childExit != null)
                childExit.accept(state);
        });

        controller.display(parent);
    }

    // Loads the given FXML with this being root and controller
    protected void  loadFxml(String name) {
        Views.load(name, this, this);
    }

    protected AnchorPane    pane() {
        return (AnchorPane)getParent();
    }

    private Controller              _child;
    private Consumer<ReturnState>   _onExit;
}
