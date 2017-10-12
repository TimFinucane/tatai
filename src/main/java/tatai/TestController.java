package tatai;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import tatai.model.test.Test;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * A test window, to which you can pass specifications for the type of test
 */
public class TestController extends AnchorPane {
	enum ReturnState {
		QUIT, // TODO: Use this to notify early exit if ever needed
		FINISHED,
		RETRY,
		RETRY_HARDER
	}

	private Test _model;
	private Consumer<ReturnState> _notifyReturn;

//	JavaFX Controls
	@FXML private Label _lblNumber;
	@FXML private Label _lblRecognition;
	@FXML private JFXButton _btnSubmit;
	@FXML private JFXButton _btnNext;
	@FXML private RecorderControl _recorderControl;
	@FXML private PlaybackControl _playbackControl;

	/**
     * Creates and starts a test.
	 * When the user is ready to finish the test, notifyReturn is called
	 * with the appropriate ReturnState.
     */
	public TestController(Test model, Consumer<ReturnState> notifyReturn) {
	    _model = model;
	    _notifyReturn = notifyReturn;

	    // Load fxml, set self to act as controller and root
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/tatai/Test.fxml"));

		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch(IOException e) {
			throw new RuntimeException("Unable to load tatai.Test.fxml: " + e.getMessage());
		}

		_playbackControl.setVisible(false);
		_recorderControl.setVisible(false);

		_lblNumber.setManaged(false);
		_playbackControl.setManaged(false);
		_recorderControl.setManaged(false);

		_btnSubmit.setText("Start");
		_btnSubmit.setOnAction(e -> {
			_lblNumber.setManaged(true);
			_recorderControl.setManaged(true);
			_recorderControl.setManaged(true);

			_playbackControl.setVisible(true);
			_recorderControl.setVisible(true);

			nextRound();
		});

		_lblRecognition.setText("Welcome to the " + model.name());

		//_recorderControl.onMediaAvailable(this::mediaAvailable);
		//_recorderControl.onRecognitionComplete(this::recognize);
	}

	/**
	 * Called when the recorder has recorded something
	 */
	private void	mediaAvailable() {
		_playbackControl.setMedia(_recorderControl.media());
	}

	/**
	 * Called when the recorder has finished recognizing the text. Handles all
	 * the state
	 */
    private void	recognize(String text) {
		_playbackControl.setDisable(false);

    	if(text.equals("")) {
            _lblRecognition.setText("Nothing was recognized");
        }
		else {
            _lblRecognition.setText(text);
        }
    	if(_model.tryAnswer(text))
    		_lblRecognition.setTextFill(Color.GREEN);
		else {
			if(_model.hasAnotherTry())
			    _btnSubmit.setText("Retry");
			else
				_recorderControl.setDisable(true);
    		    _lblRecognition.setTextFill(Color.RED);
		}

		// Prepare user submit options
		_btnSubmit.setDisable(false);
		if(_model.hasNextRound()) {
			_btnSubmit.setText("Next");
			_btnSubmit.setOnAction(e -> nextRound());
		} else {
			_btnSubmit.setText("Finish");
			_btnSubmit.setOnAction(e -> finish());
		}
	}

	/**
	 * Sets up the screen for the next round
	 */
	private void	nextRound() {
	    _btnSubmit.setText("Submit");

		_playbackControl.setDisable(true);
		_recorderControl.setDisable(false);

		_lblRecognition.setText("");
		_playbackControl.dispose();

		_btnSubmit.setDisable(true);

		_lblNumber.setText(_model.nextRound());
	}

	/**
	 * Sets up the screen when complete
	 */
	private void	finish() {
		_lblNumber.setText(_model.score() + "/10");

		_playbackControl.setVisible(false);
		_recorderControl.setVisible(false);

		_lblRecognition.setTextFill(Color.WHITE);

		_btnSubmit.setText("Finish");
		_btnSubmit.setOnAction((e) -> _notifyReturn.accept(ReturnState.FINISHED));
	}

}
