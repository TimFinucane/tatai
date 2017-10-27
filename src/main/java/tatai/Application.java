package tatai;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import tatai.controls.Sidebar;
import tatai.model.user.User;
import tatai.select.SelectStatsController;
import tatai.select.SelectTestController;

import java.util.function.Consumer;

/**
 * The top level class of this project. Is controller of the application/initial window
 * Group Members: Andrew Fairweather and Timothy Finucane
 */
public class Application extends javafx.application.Application implements Sidebar.User {
    public static class Home extends Controller {
        Home(String user) {
            loadFxml("Home");

            welcomeLbl.setText("Welcome, " + user + "!");
            signoutBtn.setOnAction((e) -> Platform.runLater(() -> displayChild(new LoginController(user))));
        }

        @FXML private Label     welcomeLbl;
        @FXML private JFXButton signoutBtn;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Have a sidebar on the left, and a pane in the centre to add our screens
        _sidebar = new Sidebar(this);
        _centre = new AnchorPane();

        BorderPane pane = new BorderPane();
        pane.setLeft(_sidebar);
        pane.setCenter(_centre);

        _scene = new Scene(pane, 600, 400);
        _scene.getStylesheets().add("/tatai/stylesheets/DarkMode.css");

        stage.setScene(_scene);

        stage.setTitle(APP_NAME);
        stage.show();

        _centre.requestFocus(); // And return focus to us

        home();
    }

    // TODO: Either lock the sidepane buttons or hide them when in test mode.
    // Called when home button pressed. Sends user to login screen
    @Override
    public void home(){
        if(_user == null) {
            LoginController login = new LoginController();

            setScreen(login, (state) -> {
                if(state == Controller.ReturnState.FINISHED) {
                    _user = new User(login.name());
                    _sidebar.unlockButtons();
                }
            });
        } else {
            setScreen(new Home(_user.username));
        }
    }

    // Called when practice button pressed
    @Override
    public void practice() {
        setScreen(new SelectTestController.Practice(_user));
    }

    // Called when test button pressed
    @Override
    public void test(){
        setScreen(new SelectTestController.Normal(_user));
    }

    // Called when stats button pressed
    @Override
    public void stats(){
        setScreen(new SelectStatsController(_user));
    }

    // Called when the info button has been pressed
    @Override
    public void info() {
        ButtonType light = new ButtonType("Light");
        ButtonType dark = new ButtonType("Dark");

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Please choose a theme:", light, dark);
        // Anything less than this line here will not allow the user to exit out of the alert
        alert.getDialogPane().getScene().getWindow().setOnCloseRequest((e) -> alert.close());

        alert.showAndWait().ifPresent(type -> {
            if(type == light) {
                _scene.getStylesheets().remove("/tatai/stylesheets/DarkMode.css");
                _scene.getStylesheets().add("/tatai/stylesheets/LightMode.css");
            } else if(type == dark) {
                _scene.getStylesheets().remove("/tatai/stylesheets/LightMode.css");
                _scene.getStylesheets().add("/tatai/stylesheets/DarkMode.css");
            }
        });
    }

    /**
     * Set the main panel to the given screen
     */
    private void setScreen(Controller controller) {
        setScreen(controller, (e) -> {});
    }

    /**
     * Set the main panel to the given screen, running the given runnable when it exits
     */
    private void setScreen(Controller controller, Consumer<Controller.ReturnState> onExit) {
        // Try to exit previous screen. If we can't, then dont bother
        if(_curScreen != null && !_curScreen.exit())
            return;

        _curScreen = controller;

        controller.display(_centre);
        controller.onExit((state) -> {
            onExit.accept(state);

            _curScreen = null;

            if(state == Controller.ReturnState.FINISHED)
                home();
        });
    }

    private static final String APP_NAME = "T\u0101tai";

    private Controller  _curScreen = null;

    private User        _user = null;

    private Scene       _scene;
    private AnchorPane  _centre;
    private Sidebar     _sidebar;
}
