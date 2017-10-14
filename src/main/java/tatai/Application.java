package tatai;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import util.Views;

import java.io.IOException;

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

        stage.setScene(new Scene(Views.load("Application", this, null)));

        _stage.setTitle(APP_NAME);
        _stage.show();

        _sidePane.setPrefWidth(55.0);

        homeBtn.setOnAction(event -> home());
        practiceBtn.setOnAction(event -> practice());
        testBtn.setOnAction((event -> test()));
        statsBtn.setOnAction(event -> stats());
    }

//    TODO: Either lock the sidepane buttons or hide them when in test mode.

    // Called when home button pressed
    private void home(){
        throw new NotImplementedException();
    }

    // Called when practice button pressed
    private void practice() {
        throw new NotImplementedException();
    }

    // Called when test button pressed
    private void test(){
        new SelectTestController().display(topPane, this::home);
        testBtn.setStyle("-fx-background-color: #29292D");
    }

    // Called when stats button pressed
    private void stats(){
        throw new NotImplementedException();
    }

    // Called when the info button has been pressed
    private void    info() {
        InfoController info = new InfoController();

        info.display(topPane, this::home);
    }


    @FXML
    private void openPane(MouseEvent e) {

        Timeline timeline = new Timeline();

        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(_sidePane.prefWidthProperty(), 55.0)
                ),
                new KeyFrame(Duration.millis(100.0d),

                        new KeyValue(_sidePane.prefWidthProperty(), 225.0)
                )
        );

        timeline.play();
        homeBtn.setContentDisplay(ContentDisplay.LEFT);
        testBtn.setContentDisplay(ContentDisplay.LEFT);
        practiceBtn.setContentDisplay(ContentDisplay.LEFT);
        statsBtn.setContentDisplay(ContentDisplay.LEFT);
    }

    @FXML
    private void closePane(MouseEvent e) {
        Timeline timeline = new Timeline();
        homeBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        testBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        practiceBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        statsBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(_sidePane.prefWidthProperty(), 225.0)
                ),
                new KeyFrame(Duration.millis(100.0d),

                        new KeyValue(_sidePane.prefWidthProperty(), 55.0)
                )
        );

        timeline.play();
    }

    private static final String APP_NAME = "T\u0101tai";
    private Stage _stage;

    // JavaFX Controls
    @FXML private Button    homeBtn;
    @FXML private Button    practiceBtn;
    @FXML private Button    testBtn;
    @FXML private Button    statsBtn;
    @FXML private AnchorPane topPane;
    @FXML private VBox _sidePane;
}
