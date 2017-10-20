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
        sidebar = new Sidebar(this);
        centre = new HBox();
        centre.setPrefWidth(500.0);
        centre.setAlignment(Pos.CENTER);

        BorderPane pane = new BorderPane();
        pane.setLeft(sidebar);
        pane.setCenter(centre);

        stage.setScene(new Scene(pane));

        stage.setTitle(APP_NAME);
        stage.show();

        centre.requestFocus(); // And return focus to us
    }

    // TODO: Either lock the sidepane buttons or hide them when in test mode.
    // TODO: Fix overlap if test or practice button pressed multiple times.
    // Called when home button pressed. Sends user to login screen
    @Override
    public void home(){
        clearCentre();

        LoginController login;
        if(_user == null)
            login = new LoginController();
        else
            login = new LoginController(_user);

        // TODO: Account for user exiting without wanting to log in
        login.display(centre, () -> {
            _user = login.name();
            sidebar.unlockButtons();
        });
    }

    // Called when practice button pressed
    @Override
    public void practice() {
        clearCentre();

        new SelectTestController(true, false).display(centre, this::home);
    }

    // Called when test button pressed
    @Override
    public void test(){
        clearCentre();

        new SelectTestController().display(centre, this::home);
    }

    // Called when stats button pressed
    @Override
    public void stats(){
        clearCentre();

        new SelectTestController(false, true).display(centre, this::home);
    }

    // Called when the info button has been pressed
    @Override
    public void info() {
        clearCentre();

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

    private void clearCentre() {
        centre.getChildren().clear();
    }

    private static final String APP_NAME = "T\u0101tai";

    private String    _user = null;
    private HBox      centre;
    private Sidebar   sidebar;
}
