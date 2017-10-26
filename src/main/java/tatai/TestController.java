package tatai;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import tatai.controls.PlaybackControl;
import tatai.controls.RecorderControl;
import tatai.model.question.Question;
import tatai.model.test.Test;
import tatai.model.test.TestJson;

/**
 * A test window, to which you can pass specifications for the type of test
 */
public class TestController extends Controller {
	/**
     * Creates and starts a test.
	 * When the user is ready to finish the test, notifyReturn is called
	 * with the appropriate ReturnState.
     */
	public TestController(TestJson model) {
		_model = new Test(model);
		_practice = model.practice;

		if(!_practice)
			_maxQuestions = model.rounds();

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

			nextRound(_model.curQuestion());
		});

		recorderControl.onMediaAvailable(this::mediaAvailable);
		recorderControl.onRecognitionComplete(this::recognize);
	}
	public TestController(TestJson model, Test.Memento memento) {
		this(model);
		_model = new Test(model, memento);

		titleLbl.setText("Continue the " + model.name + " test");
		submitBtn.setText("Continue");
	}

	/**
	 * Gets the user's score in the test
	 */
	public int			score() {
		return _model.score();
	}

	/**
	 * Gives a Memento of the model for if the TestController exits early
	 */
	public Test.Memento	save() {
		return _model.memento();
	}

    /**
     * Asks user whether they want to exit, just to make sure
     */
	@Override
	protected boolean   exit(ReturnState state) {
		if(state != ReturnState.FINISHED) {
			boolean willReturn = new Alert(Alert.AlertType.WARNING,
					"Are you sure you want to exit this test?",
					ButtonType.YES, ButtonType.NO)
					.showAndWait()
					.filter(type -> type == ButtonType.YES).isPresent();

			return willReturn && super.exit(state); // Will only try to exit if willReturn is true. KEEP THAT IN MIND PLS.
		} else {
			return super.exit(state);
		}
	}

	/**
	 * Called when the recorder has recorded something
	 */
	private void		mediaAvailable() {
		playbackControl.setMedia(recorderControl.media());
	}

	/**
	 * Called when the recorder has finished recognizing the text. Handles all
	 * the state
	 */
    private void		recognize(String text) {
		playbackControl.setDisable(false);

        retryLbl.setText("");
		submitBtn.setDisable(false);

    	if(text.equals("")) {
            recognitionLbl.setText("Nothing was recognized");
        } else {
            recognitionLbl.setText(text);
        }

    	if(_question.tryAnswer(text)) {
            recognitionLbl.setTextFill(SUCCESS_COLOUR);
            recorderControl.setDisable(true);
        } else {
			if(_question.hasAnotherTry()) {
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
	private void		nextRound(Question question) {
		retryLbl.setText("");
		submitBtn.setDisable(true);

		playbackControl.setDisable(true);
		recorderControl.setDisable(false);

		recognitionLbl.setText("");
		playbackControl.dispose();

		_question = question;

		titleLbl.setText(_question.text());

		if(_practice)
			questionNumberLbl.setText(Integer.toString(_questionNumber++) + "/\u221E");
		else
			questionNumberLbl.setText(Integer.toString(_questionNumber++) + "/" + Integer.toString(_maxQuestions));

        // Prepare user submit options
        if(_model.hasAnotherRound()) {
            submitBtn.setText("Next");
            submitBtn.setOnAction(e -> nextRound(_model.nextRound()));
        } else {
            if(_practice)
                _model.reset();
            else {
                submitBtn.setText("Finish");
                submitBtn.setOnAction(e -> finish());
            }
        }
	}

	/**
	 * Sets up the screen when complete
	 */
	private void		finish() {
		questionNumberLbl.setVisible(false);

        titleLbl.setFont(Font.font(TITLE_TEXT_SIZE));
		titleLbl.setText(_model.score() + "/10");

		recognitionLbl.setVisible(false);
		retryLbl.setVisible(false);

		playbackControl.setVisible(false);
		recorderControl.setVisible(false);

		submitBtn.setText("Main Menu");
		submitBtn.setOnAction(e -> exit(ReturnState.FINISHED));
	}

    private static final int        TITLE_NUMBERS_SIZE = 96;
	private static final int        TITLE_TEXT_SIZE = 36;

    private static final Paint      SUCCESS_COLOUR = Color.color(0/255.0, 230/255.0, 64/255.0);
	private static final Paint      FAILURE_COLOUR = Color.color(240/255.0, 52/255.0, 52/255.0);

	private int						_questionNumber = 0;
	private int						_maxQuestions;

	private Question 				_question;
	private boolean                 _practice;
    private Test    				_model;

	// FXML controls
	@FXML private Label             titleLbl;
	@FXML private Label             recognitionLbl;
	@FXML private Label             retryLbl;
	@FXML private JFXButton         submitBtn;
	@FXML private RecorderControl 	recorderControl;
	@FXML private PlaybackControl 	playbackControl;
	@FXML private Label				questionNumberLbl;
}
