package tatai;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import tatai.model.Test;

import java.io.IOException;

/**
 * A test window, to which you can pass specifications for the type of test
 */
public class TestController extends VBox {
    // Determines the ratio of window size to number/control text size
    private static double NUMBER_HEIGHT_DIV = 5;
    private static double NUMBER_WIDTH_DIV = 3;
    private static double CONTROL_HEIGHT_DIV = 15;
    private static double CONTROL_WIDTH_DIV = 12;

	public TestController(Test model) {
	    _model = model;

	    // Load fxml, set self to act as controller and root
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/tatai/Test.fxml"));

		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch(IOException e) {
			throw new RuntimeException("Unable to load tatai.Test.fxml: " + e.getMessage());
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

    @Override
    public void    resize(double width, double height) {
        super.resize(width, height);

        // Adjust children to be approx. the right size based on window size
        // These numbers are pretty good as they are
        double numberSize = Math.min(getHeight()/NUMBER_HEIGHT_DIV, getWidth()/NUMBER_WIDTH_DIV);
        double controlSize = Math.min(getHeight()/CONTROL_HEIGHT_DIV, getWidth()/CONTROL_WIDTH_DIV);

        numberLbl.setStyle("-fx-font-size: " + numberSize);
        playbackCntrl.setStyle("-fx-font-size: " + controlSize);
        recorderCntrl.setStyle("-fx-font-size: " + controlSize);
    }

	private Test    _model;

    // FXML controls
    @FXML
    private Label           numberLbl;
    @FXML
    private PlaybackControl playbackCntrl;
    @FXML
    private RecorderControl recorderCntrl;
}
