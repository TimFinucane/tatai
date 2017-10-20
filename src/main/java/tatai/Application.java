package tatai;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
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
    }

    // TODO: Either lock the sidepane buttons or hide them when in test mode.
    // TODO: Fix overlap if test or practice button pressed multiple times.
    // Called when home button pressed. Sends user to login screen
    public void home(){
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
    public void practice() {
        new SelectTestController(true, false).display(centre, this::home);
    }

    // Called when test button pressed
    public void test(){
        new SelectTestController().display(centre, this::home);
    }

    // Called when stats button pressed
    public void stats(){
        new SelectTestController(false, true).display(centre, this::home);
    }

    // Called when the info button has been pressed
    public void info() {
        // TODO: New info controller
        throw new NotImplementedException();
    }

    // TODO: Make sure all children that are added know what stylesheet to add.
    // Called when the colour change button pressed
    private void changeColor(boolean dark) {
        if(dark)
            setUserAgentStylesheet("/tatai/stylesheets/LightMode.css");
        else
            setUserAgentStylesheet("/tatai/stylesheets/DarkMode.css");
    }

    private static final String APP_NAME = "T\u0101tai";

    private String    _user = null;
    private HBox      centre;
    private Sidebar   sidebar;
}
