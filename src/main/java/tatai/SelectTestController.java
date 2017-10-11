package tatai;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


import java.io.IOException;

/**
 * A window where you can select which test you want to do
 */
public class SelectTestController extends AnchorPane{

//    JavaFX Controls
    @FXML private Button _btnEasy;
    @FXML private Button _btnIntermediate;
    @FXML private Button _btnAdvanced;
    @FXML private Button _btnCustom;
    @FXML private Button _btnCreateCustom;
    @FXML private Button _btnRemoveCustom;

	public SelectTestController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tatai/SelectTest.fxml"));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load tatai.SelectTest.fxml: " + e.getMessage());
        }

        _btnEasy.setOnAction(event -> easyTest());
        _btnIntermediate.setOnAction(event -> intermediateTest());
        _btnAdvanced.setOnAction(event -> advancedTest());
        _btnCustom.setOnAction(event -> customTest());
        _btnCreateCustom.setOnAction(event -> createCustom());
        _btnRemoveCustom.setOnAction(event -> removeCustom());
    }

//    Called when easy button pressed.
    private void easyTest() {
	    throw new NotImplementedException();
    }

//    Called when intermediate button pressed.
    private void intermediateTest() {
	    throw new NotImplementedException();
    }

//    Called when advanced button pressed
    private void advancedTest() {
	    throw new NotImplementedException();
    }

//    Called when custom button pressed
    private void customTest() {
	    throw new NotImplementedException();
    }

//    Called when create custom button pressed
    private void createCustom() {
	    throw new NotImplementedException();
    }

//    Called when remove custom button pressed
    private void removeCustom() {
	    throw new NotImplementedException();
    }
}
