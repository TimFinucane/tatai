package util;

import com.sun.media.jfxmedia.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;

import javax.annotation.Nullable;
import java.io.IOException;

public class Views {
    /**
     * Loads an FXML file
     * @param name The name of the fxml file (just name, not path)
     * @param controller The object you wish to act as controller
     * @param root If not null, the object you wish to be set as root
     * @return The root of the fxml view
     */
    public static Parent load(String name, Object controller, @Nullable Parent root) {
        FXMLLoader loader = new FXMLLoader(controller.getClass().getResource("/tatai/" + name + ".fxml"));

        loader.setController(controller);
        if(root != null)
            loader.setRoot(root);

        try {
            return loader.load();
        } catch(IOException e) {
            new Alert(Alert.AlertType.ERROR,"Problem loading " + name + " FXML. Please contact system admin")
            .showAndWait();
            Logger.logMsg(Logger.ERROR, e.getMessage());

            throw new IllegalArgumentException("Could not load the " + name + " FXML " + e.getMessage());
        }
    }
}
