package tatai;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import util.Views;

/**
 * The top level class of this project. Is controller of the application/initial window
 */
public class Application extends javafx.application.Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void     start(Stage stage) {
        _scene = new Scene(Views.load("Application", this, null));
        _scene.getStylesheets().add("/tatai/stylesheets/DarkMode.css");

        testBtn.setVisible(false);
        statsBtn.setVisible(false);

        stage.setScene(_scene);

        stage.setTitle(APP_NAME);
        stage.show();

        _sidePane.setPrefWidth(55.0);

        homeBtn.setOnAction(event -> home());
        practiceBtn.setOnAction(event -> practice());
    }

    // TODO: Either lock the sidepane buttons or hide them when in test mode.
    // TODO: Fix overlap if test or practice button pressed multiple times.
    // Called when home button pressed. Sends user to login screen
    private void home(){
        LoginController login;
        if(_user == null)
            login = new LoginController();
        else
            login = new LoginController(_user);

        // TODO: Account for user exiting without wanting to log in
        login.display(topPane, () -> {
            _user = login.name();
            unlockButtons();
        });
    }

    // Called when practice button pressed
    private void practice() {
        new SelectTestController(true, false).display(topPane, this::home);
        testBtn.setStyle("-fx-background-color: #29292D");
    }

    // Called when test button pressed
    private void test(){
        new SelectTestController().display(topPane, this::home);
        practiceBtn.getStyleClass().add("root");
    }

    // Called when stats button pressed
    private void stats(){
        new SelectTestController(false, true).display(topPane, this::home);
        statsBtn.setStyle("-fx-background-color: #29292D");
    }

    // Called when the info button has been pressed
    private void info() {
        // TODO: New info controller
        throw new NotImplementedException();
    }

    /**
     * Once the user has signed in they now have access to the test and stats modules
     */
    private void unlockButtons() {
        FadeTransition fadeInStats = new FadeTransition(Duration.seconds(3.0), statsBtn);
        FadeTransition fadeInTest = new FadeTransition(Duration.seconds(3.0), testBtn);

        fadeInStats.setFromValue(0.0);
        fadeInStats.setToValue(1.0);
        fadeInStats.setCycleCount(1);
        fadeInStats.setAutoReverse(false);

        fadeInTest.setFromValue(0.0);
        fadeInTest.setToValue(1.0);
        fadeInTest.setCycleCount(1);
        fadeInTest.setAutoReverse(false);

        fadeInTest.play();

        statsBtn.setVisible(true);
        testBtn.setVisible(true);
        fadeInStats.playFromStart();
        fadeInTest.playFromStart();

        testBtn.setOnAction((event -> test()));
        statsBtn.setOnAction(event -> stats());
    }

    //TODO: Make sure all children that are added know what stylesheet to add.
    // Called when the colour change button pressed
    private void changeColor(boolean dark) {
        if(dark) {
            _scene.getStylesheets().clear();
            _scene.getStylesheets().add("/tatai/stylesheets/LightMode.css");
            topPane.setStyle("-fx-background-color: #DADFE1");
        } else {
            _scene.getStylesheets().clear();
            _scene.getStylesheets().add("/tatai/stylesheets/DarkMode.css");
            topPane.setStyle("-fx-background-color: #29292D");
        }
    }

    /**
     * Opens the side pane to show text
     */
    @FXML
    private void openPane(MouseEvent e) {
        homeBtn.setContentDisplay(ContentDisplay.LEFT);
        testBtn.setContentDisplay(ContentDisplay.LEFT);
        practiceBtn.setContentDisplay(ContentDisplay.LEFT);
        statsBtn.setContentDisplay(ContentDisplay.LEFT);

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,             new KeyValue(_sidePane.prefWidthProperty(), SIDE_MIN)),
                new KeyFrame(Duration.millis(100.0d),   new KeyValue(_sidePane.prefWidthProperty(), SIDE_MAX))
        );
        timeline.play();
    }

    /**
     * Closes the side pane when hovering has stopped
     */
    @FXML
    private void closePane(MouseEvent e) {
        homeBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        testBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        practiceBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        statsBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,             new KeyValue(_sidePane.prefWidthProperty(), SIDE_MAX)),
                new KeyFrame(Duration.millis(100.0d),   new KeyValue(_sidePane.prefWidthProperty(), SIDE_MIN))
        );
        timeline.play();
    }

    private static final String APP_NAME = "T\u0101tai";
    private static final double SIDE_MIN = 55.0;
    private static final double SIDE_MAX = 200.0;

    private String          _user = null;

    private Scene           _scene;

    // JavaFX Controls
    @FXML private Button    homeBtn;
    @FXML private Button    practiceBtn;
    @FXML private Button    testBtn;
    @FXML private Button    statsBtn;
    @FXML private Button    infoBtn;

    @FXML private HBox      topPane;
    @FXML private VBox      _sidePane;
}
