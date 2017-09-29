package tatai;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * Controller class for Test.fxml
 */

public class TestController implements Initializable{
	
	@FXML
	private Label _label;
	private TestModel _model;
	
	public TestController(TestModel _model) {
		this._model = _model;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		_label.setText(Integer.toString(getLabel()));
		
	}
	
	public int getLabel() {
		return _model.getRandom();
	}
	
	public void setLabel() {
		_label.setText(Integer.toString(getLabel()));
	}
	
	//TODO: Get the string output from recognizer.
	
}
