package tatai;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tatai.model.EasyTest;
import tatai.model.HardTest;

import java.io.IOException;

/**
 * The top level class of this project. Is controller of the application/initial window
 */
public class Application extends javafx.application.Application {
    private static String APP_NAME = "T\u0101tai";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        _stage = stage;

        // Load the application fxml, and set self to be its controller
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tatai/Application.fxml"));
        loader.setController(this);

        try {
            _mainScreen = new Scene(loader.load());
        } catch(IOException e) {
            throw new RuntimeException("Unable to load tatai.Application.fxml: " + e.getMessage());
        }

        _stage.setTitle(APP_NAME);
        _stage.setScene(_mainScreen);

        easyBtn.setOnAction((ignored) -> easyTest());
        hardBtn.setOnAction((ignored) -> hardTest());
        infoBtn.setOnAction((ignored) -> info());

        _stage.show();
    }

    // Called when the easy button has been pressed
    private void easyTest() {
    	TestController test = new TestController(new EasyTest());

        _stage.setScene(new Scene(test));
        _stage.show();
    }
    // Called when the hard button has been pressed
    private void hardTest() {
        TestController test = new TestController(new HardTest());

        _stage.setScene(new Scene(test));
        _stage.show();
    }
    // Called when the info button has been pressed
    private void info() {
        throw new NotImplementedException();
    }

    private Scene   _mainScreen;
    private Stage   _stage;

    // JavaFX controls
    @FXML
    private Button  easyBtn;
    @FXML
    private Button  hardBtn;
    @FXML
    private Button  infoBtn;
}
