package tatai;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
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
        _stage = stage;

        stage.setScene(new Scene(Views.load("Application", this, null)));

        _stage.setTitle(APP_NAME);
        _stage.show();

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

    private static final String APP_NAME = "T\u0101tai";
    private Stage _stage;

    // JavaFX Controls
    @FXML private Button    homeBtn;
    @FXML private Button    practiceBtn;
    @FXML private Button    testBtn;
    @FXML private Button    statsBtn;
    @FXML private AnchorPane topPane;
}
