package tatai;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import tatai.model.Test;

import java.io.IOException;

/**
 * A test window, to which you can pass specifications for the type of test
 */
public class TestController extends Region {

	public TestController(Test model) {
	    _model = model;

	    // Load fxml, set self to act as controller and root
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/tatai/Test.fxml"));

		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch(IOException e) {
			throw new RuntimeException("Unable to load tatai.Test.fxml");
		}
	}

    /**
     * Called when the 'continue' or 'start' button has been pressed.
     * Starts a round by presenting a number and waiting for a guess
     */
	private void    startRound() {
    }

    /**
     * Called when the submit button has been pressed.
     * Checks the guess and informs whether or not it
     *  was correct.
     */
    private void    endRound() {
    }



	@FXML
	private Label _label;
	private Test _model;
}
