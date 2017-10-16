package tatai;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
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
		_model = model;

	    // Load fxml, set self to act as controller and root

		Views.load("Test", this, this);

		_lblTitle.setFont(Font.font(TITLE_TEXT_SIZE ));
		_lblTitle.setText("Welcome to the " + _model.name + " test");

		_playbackControl.setVisible(false);
		_recorderControl.setVisible(false);
		_recorderControl.setDisable(true);

		_btnNext.setVisible(false);
		_lblRecognition.setVisible(false);

		_btnNext.setManaged(false);
		_btnNext.setOnAction(event -> nextRound());

		_btnSubmit.setText("Start");
		_btnSubmit.setOnAction(e -> {
            _lblTitle.setFont(Font.font(TITLE_NUMBERS_SIZE ));

		    _playbackControl.setVisible(true);
		    _recorderControl.setVisible(true);

			_btnNext.setManaged(true);
			_btnNext.setVisible(true);

			nextRound();
		});

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
    		_lblRecognition.setTextFill(SUCCESS_COLOUR);
		else {
			if(_model.hasAnotherTry())
			    _btnSubmit.setText("Retry");
			else
				_recorderControl.setDisable(true);
    		    _lblRecognition.setTextFill(FAILURE_COLOUR);
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

		_lblTitle.setText(_model.nextRound());
	}

	/**
	 * Sets up the screen when complete
	 */
	private void	finish() {
        _lblTitle.setFont(Font.font(TITLE_TEXT_SIZE ));
		_lblTitle.setText(_model.score() + "/10");

		_btnNext.setVisible(false);
		_playbackControl.setVisible(false);
		_recorderControl.setVisible(false);

		_btnSubmit.setText("Finish");
		exit(ReturnState.FINISHED);
	}

	private void                    exit(ReturnState state) {
	    _returnState = state;
	    exit();
    }

    private static final int        TITLE_NUMBERS_SIZE = 96;
	private static final int        TITLE_TEXT_SIZE = 36;

    private static final Paint      SUCCESS_COLOUR = Color.color(0/255.0, 230/255.0, 64/255.0);
	private static final Paint      FAILURE_COLOUR = Color.color(240/255.0, 52/255.0, 52/255.0);

    private ReturnState         	_returnState;
    private Test    				_model;

	// FXML controls
	@FXML private Label 			_lblTitle;
	@FXML private Label 			_lblRecognition;
	@FXML private JFXButton 		_btnSubmit;
	@FXML private JFXButton 		_btnNext;
	@FXML private RecorderControl 	_recorderControl;
	@FXML private PlaybackControl 	_playbackControl;
}
