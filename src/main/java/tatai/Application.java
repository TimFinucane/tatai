package tatai;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sun.util.resources.cldr.se.CurrencyNames_se;
import util.Views;
import javafx.util.Duration;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * The top level class of this project. Is controller of the application/initial window
 */
public class Application extends javafx.application.Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void     start(Stage stage) {
        _stage = stage;

        _scene = new Scene(Views.load("Application", this, null));
        _scene.getStylesheets().add("/tatai/stylesheets/DarkMode.css");

        colourLbl.setVisible(false);
        colourToggle.setVisible(false);
        testBtn.setVisible(false);
        statsBtn.setVisible(false);

        stage.setScene(_scene);

        _stage.setTitle(APP_NAME);
        _stage.show();

        _sidePane.setPrefWidth(55.0);

        homeBtn.setOnAction(event -> home());
        practiceBtn.setOnAction(event -> practice());

        signInBtn.setOnAction(event -> next());

    }

    // TODO: Either lock the sidepane buttons or hide them when in test mode.
    // TODO: Fix overlap if test or practice button pressed multiple times.
    // Called when home button pressed
    private void home(){
        throw new NotImplementedException();
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
    private void    info() {
        // TODO: New info controller
        throw new NotImplementedException();
    }


    //TODO: Give sign in/out view a dedicated fxml and controller. Move this logic to that controller.
    // Called when the sign in button pressed
    private void next() {
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(200.0d), new KeyValue(signInLbl.prefHeightProperty(), 0.0) ),
                new KeyFrame(Duration.ZERO, new KeyValue(colourLbl.prefHeightProperty(), 0.0) ),
                new KeyFrame(Duration.millis(200.0d), new KeyValue(colourLbl.prefHeightProperty(), LABEL_MAX) ),
                new KeyFrame(Duration.ZERO, new KeyValue(statsBtn.prefHeightProperty(), 0.0) ),
                new KeyFrame(Duration.millis(200.0d), new KeyValue(statsBtn.prefHeightProperty(), SIDE_MIN) ),
                new KeyFrame(Duration.ZERO, new KeyValue(testBtn.prefHeightProperty(), 0.0) ),
                new KeyFrame(Duration.millis(200.0d), new KeyValue(testBtn.prefHeightProperty(), SIDE_MIN) )
        );

        usernameTxt.setVisible(false);
        colourToggle.setVisible(true);
        colourLbl.setVisible(true);

        signInBtn.setText("Sign In");

        signInBtn.setOnAction(event -> signIn());

        timeline.play();
    }



    /**
     * Once the user has signed in they now have access to the test and stats modules
     */
    private void signIn() {

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

        //TODO: Send this username string to any tests/stats the user does.
        String username = usernameTxt.getText();

        testBtn.setOnAction((event -> test()));
        statsBtn.setOnAction(event -> stats());
        changeColor(colourToggle.isSelected());
    }

    //TODO: Make sure all children that are added know what stylesheet to add.
    // Called when the colour change button pressed
    private void changeColor(boolean dark) {
        _isDark = dark;
        if(_isDark == true) {
            _scene.getStylesheets().clear();
            _scene.getStylesheets().add("/tatai/stylesheets/LightMode.css");
            topPane.setStyle("-fx-background-color: #DADFE1");
        }
        else if(_isDark == false){
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

        _sidePane.toFront();

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO, new KeyValue(_sidePane.prefWidthProperty(), SIDE_MIN) ),
                new KeyFrame(Duration.millis(100.0d), new KeyValue(_sidePane.prefWidthProperty(), SIDE_MAX) )
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
                new KeyFrame(Duration.ZERO,             new KeyValue(_sidePane.prefWidthProperty(), SIDE_MAX) ),
                new KeyFrame(Duration.millis(100.0d),   new KeyValue(_sidePane.prefWidthProperty(), SIDE_MIN) )
        );
        timeline.play();
    }

    private boolean _isDark = true;
    private static final String APP_NAME = "T\u0101tai";
    private static final double SIDE_MIN = 55.0;
    private static final double SIDE_MAX = 200.0;
    private static final double LABEL_MAX = 30.0;

    private Stage           _stage;
    private Scene           _scene;

    // JavaFX Controls
    @FXML private Button    homeBtn;
    @FXML private Button    practiceBtn;
    @FXML private Button    testBtn;
    @FXML private Button    statsBtn;
    @FXML private Button    infoBtn;
    @FXML private Button    signInBtn;
    @FXML private JFXTextField usernameTxt;
    @FXML private Label     signInLbl;
    @FXML private Label     colourLbl;
    @FXML private JFXToggleButton   colourToggle;
    @FXML private AnchorPane topPane;
    @FXML private VBox _sidePane;
}
