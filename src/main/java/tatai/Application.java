package tatai;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The top level class of this project. Is controller of the application/initial window
 */
public class Application extends javafx.application.Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Load the application fxml, and set self to be its controller
        FXMLLoader loader = new FXMLLoader(getClass().getResource( "/tatai/Application.fxml" ));
        loader.setController(this);

        Parent root;
        try {
            root = loader.load();
        } catch( IOException e ) {
            throw new RuntimeException("Unable to load tatai.Application.fxml: " + e.getMessage());
        }

        _scene = new Scene(root);
        stage.setTitle("Tatai"); // TODO: Unicode
        stage.setScene(_scene);

        stage.show();
        // TODO: Logic
    }


    private Scene _scene;
}
