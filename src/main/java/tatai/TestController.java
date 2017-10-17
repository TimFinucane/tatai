package tatai;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import tatai.model.ScoreKeeper;
import tatai.model.test.Test;
import tatai.model.test.TestJson;
import tatai.model.test.TestParser;

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
	public TestController(ScoreKeeper keeper, TestJson model) {
		_name = model.name;
		_practice = model.practice;

		_scoreKeeper = keeper;

		_model = TestParser.make(model);

	    // Load fxml, set self to act as controller and root
		loadFxml("Test");

		titleLbl.setFont(Font.font(TITLE_TEXT_SIZE));
		titleLbl.setText("Welcome to the " + model.name + " test");

		playbackControl.setVisible(false);
		recorderControl.setVisible(false);
		recorderControl.setDisable(true);

		recognitionLbl.setVisible(false);

		submitBtn.setText("Start");
		submitBtn.setOnAction(e -> {
            titleLbl.setFont(Font.font(TITLE_NUMBERS_SIZE));

		    playbackControl.setVisible(true);
		    recorderControl.setVisible(true);

			nextRound();
		});

		recorderControl.onMediaAvailable(this::mediaAvailable);
		recorderControl.onRecognitionComplete(this::recognize);
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
		playbackControl.setMedia(recorderControl.media());
	}

	/**
	 * Called when the recorder has finished recognizing the text. Handles all
	 * the state
	 */
    private void	recognize(String text) {
		playbackControl.setDisable(false);

        retryLbl.setText("");
		submitBtn.setDisable(false);

    	if(text.equals("")) {
            recognitionLbl.setText("Nothing was recognized");
        }
		else {
            recognitionLbl.setText(text);
        }

    	if(_model.tryAnswer(text))
    		recognitionLbl.setTextFill(SUCCESS_COLOUR);
		else {
			if(_model.hasAnotherTry()) {
			    retryLbl.setText("You can try again by re-clicking the record button");
            } else {
                recorderControl.setDisable(true);
            }
            recognitionLbl.setTextFill(FAILURE_COLOUR);
		}

		recognitionLbl.setVisible(true);
	}

	/**
	 * Sets up the screen for the next round
	 */
	private void	nextRound() {
	    retryLbl.setText("");
        submitBtn.setDisable(true);

		playbackControl.setDisable(true);
		recorderControl.setDisable(false);

		recognitionLbl.setText("");
		playbackControl.dispose();

		titleLbl.setText(_model.nextRound());

        // Prepare user submit options
        if(_model.hasAnotherRound()) {
            submitBtn.setText("Next");
            submitBtn.setOnAction(e -> nextRound());
        } else {
            submitBtn.setText("Finish");
            submitBtn.setOnAction(e -> finish());
        }
	}

	/**
	 * Sets up the screen when complete
	 */
	private void	finish() {
	    if(!_practice)
	        _scoreKeeper.addScore(_name, _model.score());

        titleLbl.setFont(Font.font(TITLE_TEXT_SIZE));
		titleLbl.setText(_model.score() + "/10");

		recognitionLbl.setVisible(false);
		retryLbl.setVisible(false);

		playbackControl.setVisible(false);
		recorderControl.setVisible(false);

		submitBtn.setText("Main Menu");
		submitBtn.setOnAction(e -> exit(ReturnState.FINISHED));
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
    private final String		    _name;
    private final boolean           _practice;
    private Test    				_model;
    private ScoreKeeper             _scoreKeeper;

	// FXML controls
	@FXML private Label             titleLbl;
	@FXML private Label             recognitionLbl;
	@FXML private Label             retryLbl;
	@FXML private JFXButton         submitBtn;
	@FXML private RecorderControl   recorderControl;
	@FXML private PlaybackControl   playbackControl;
}
