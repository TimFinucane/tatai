package tatai;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tatai.controls.Sidebar;

/**
 * The top level class of this project. Is controller of the application/initial window
 */
public class Application extends javafx.application.Application implements Sidebar.User {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        setUserAgentStylesheet("/tatai/stylesheets/DarkMode.css");

        // Have a sidebar on the left, and a pane in the centre to add our screens
        _sidebar = new Sidebar(this);
        _centre = new HBox();
        _centre.setPrefWidth(500.0);
        _centre.setAlignment(Pos.CENTER);

        BorderPane pane = new BorderPane();
        pane.setLeft(_sidebar);
        pane.setCenter(_centre);

        stage.setScene(new Scene(pane));

        stage.setTitle(APP_NAME);
        stage.show();

        _centre.requestFocus(); // And return focus to us
    }

    // TODO: Either lock the sidepane buttons or hide them when in test mode.
    // Called when home button pressed. Sends user to login screen
    @Override
    public void home(){
        LoginController login;
        if(_user == null)
            login = new LoginController();
        else
            login = new LoginController(_user);

        // TODO: Account for user exiting without wanting to log in
        setScreen(login, () -> {
            _user = login.name();
            _sidebar.unlockButtons();
        });
    }

    // Called when practice button pressed
    @Override
    public void practice() {
        setScreen(new SelectTestController(true, false));
    }

    // Called when test button pressed
    @Override
    public void test(){
        setScreen(new SelectTestController());
    }

    // Called when stats button pressed
    @Override
    public void stats(){
        setScreen(new SelectTestController(false, true));
    }

    // Called when the info button has been pressed
    @Override
    public void info() {
        ButtonType light = new ButtonType("Light");
        ButtonType dark = new ButtonType("Dark");

        new Alert(Alert.AlertType.INFORMATION, "Please choose a theme:", light, dark)
                .showAndWait().ifPresent(type -> {

            if(type == light)
                setUserAgentStylesheet("/tatai/stylesheets/LightMode.css");
            else if(type == dark)
                setUserAgentStylesheet("/tatai/stylesheets/DarkMode.css");
        });
    }

    /**
     * Set the main panel to the given screen
     */
    private void setScreen(Controller controller) {
        setScreen(controller, () -> {});
    }

    /**
     * Set the main panel to the given screen, running the given runnable when it exits
     * @param controller
     * @param onExit
     */
    private void setScreen(Controller controller, Runnable onExit) {
        if(_curScreen != null)
            _curScreen.exit();

        _curScreen = controller;

        controller.display(_centre);
        controller.onExit(() -> {
            onExit.run();
            home();
        });
    }

    private static final String APP_NAME = "T\u0101tai";

    private Controller  _curScreen = null;

    private String  _user = null;
    private HBox    _centre;
    private Sidebar _sidebar;
}
