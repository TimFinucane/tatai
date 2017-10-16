package tatai;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import tatai.model.test.Test;
import util.Views;

/**
 * A test window, to which you can pass specifications for the type of test
 */
public class TestController extends Controller {
	enum ReturnState {
		QUIT, // TODO: Use this to notify early exit if ever needed
		FINISHED,
		RETRY,
		RETRY_HARDER
	}

	/**
     * Creates and starts a test.
	 * When the user is ready to finish the test, notifyReturn is called
	 * with the appropriate ReturnState.
     */
	public TestController(Test model) {

//	    TODO: Fix playback control position and resizing.

		_model = model;

	    // Load fxml, set self to act as controller and root
		Views.load("Test", this, this);
		_playbackControl.setVisible(false);
		_recorderControl.setVisible(false);
		_btnNext.setVisible(false);
		_lblRecognition.setVisible(false);

		_lblNumber.setManaged(false);
		_playbackControl.setManaged(false);
		_recorderControl.setManaged(false);
		_btnNext.setOnAction(event -> nextRound());

		_btnSubmit.setText("Start");
		_btnSubmit.setOnAction(e -> {
			_lblNumber.setManaged(true);
			_playbackControl.setManaged(true);
			_recorderControl.setManaged(true);

			_lblTitle.setVisible(false);
			_btnNext.setVisible(true);
			_playbackControl.setVisible(true);
			_recorderControl.setVisible(true);

			nextRound();
		});

		_lblTitle.setText("Welcome to the " + model.name());

		_recorderControl.onMediaAvailable(this::mediaAvailable);
		_recorderControl.onRecognitionComplete(this::recognize);
	}

    /**
     * Gets the state in which the TestController exited
     */
	public ReturnState returnState() {
	    return _returnState;
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
    		_lblRecognition.setTextFill(Paint.valueOf("#00e640"));
		else {
			if(_model.hasAnotherTry())
			    _btnSubmit.setText("Retry");
			else
				_recorderControl.setDisable(true);
    		    _lblRecognition.setTextFill(Paint.valueOf("#f03434"));
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
		_lblTitle.setText(_model.score() + "/10");

		_btnNext.setVisible(false);
		_playbackControl.setVisible(false);
		_recorderControl.setVisible(false);

		//_lblRecognition.setTextFill(Color.WHITE);

		_btnSubmit.setText("Finish");
		exit(ReturnState.FINISHED);
	}

	private void                    exit(ReturnState state) {
	    _returnState = state;
	    exit();
    }

    private ReturnState         	_returnState;
    private Test    				_model;

	// FXML controls
	@FXML private Label 			_lblNumber;
	@FXML private Label 			_lblRecognition;
	@FXML private JFXButton 		_btnSubmit;
	@FXML private JFXButton 		_btnNext;
	@FXML private RecorderControl 	_recorderControl;
	@FXML private PlaybackControl 	_playbackControl;
	@FXML private Label 			_lblTitle;
}
