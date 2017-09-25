package tatai;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
        // Load the application fxml, and set self to be its controller
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tatai/Application.fxml"));
        loader.setController(this);

        Parent root;
        try {
            root = loader.load();
        } catch(IOException e) {
            throw new RuntimeException("Unable to load tatai.Application.fxml: " + e.getMessage());
        }

        _scene = new Scene(root);
        stage.setTitle(APP_NAME);
        stage.setScene(_scene);

        easyBtn.setOnAction((ignored) -> easyTest());
        hardBtn.setOnAction((ignored) -> hardTest());
        infoBtn.setOnAction((ignored) -> info());

        stage.show();
    }

    // Called when the easy button has been pressed
    private void easyTest() {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/tatai/Test.fxml"));
        LevelController controller = LevelControllerFactory.getLevelController("Easy");
        loader.setController(controller);
        
        Stage stage = (Stage) easyBtn.getScene().getWindow();
       
        Parent root;
        try {
        	root = loader.load();
        } catch(IOException e) {
        	throw new RuntimeException("Unable to load tatai.Test.fxml");
        }
        
        _scene = new Scene(root);
        stage.setTitle(APP_NAME);
        stage.setScene(_scene);
        
        stage.show();
    }
    // Called when the hard button has been pressed
    private void hardTest() {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/tatai/Test.fxml"));
        LevelController controller = LevelControllerFactory.getLevelController("Hard");
        loader.setController(controller);
        
        Stage stage = (Stage) easyBtn.getScene().getWindow();
       
        Parent root;
        try {
        	root = loader.load();
        } catch(IOException e) {
        	throw new RuntimeException("Unable to load tatai.Test.fxml");
        }
        
        _scene = new Scene(root);
        stage.setTitle(APP_NAME);
        stage.setScene(_scene);
        
        stage.show();
    }
    // Called when the info button has been pressed
    private void info() {
        throw new NotImplementedException();
    }

    private Scene   _scene;

    // JavaFX controls
    @FXML
    private Button  easyBtn;
    @FXML
    private Button  hardBtn;
    @FXML
    private Button  infoBtn;
}
