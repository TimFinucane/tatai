package tatai;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * Abstract class for the controller of the Test.fxml.
 */

public abstract class LevelController implements Initializable{

	@FXML
	private Label _label;
	private int _score;
	
	public abstract int getRandom();
	
	//Makes the number to be translated visible to the user on initialization of the test.
	public void initialize(URL location, ResourceBundle resources) {
		_label.setText(Integer.toString(getRandom()));
	}
	
	
	
	//TODO: Add score counter.
	//TODO: Add logic for when answer is right and wrong.
}