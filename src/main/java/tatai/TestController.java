package tatai;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import tatai.model.EasyTest;
import tatai.model.Test;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * A test window, to which you can pass specifications for the type of test
 */
public class TestController extends VBox {
	enum ReturnState {
		QUIT, // TODO: Use this to notify early exit if ever needed
		FINISHED,
		RETRY,
		RETRY_HARDER
	}

	private static double NUMBER_HEIGHT_DIV = 5;
    private static double NUMBER_WIDTH_DIV = 3;
    private static double CONTROL_HEIGHT_DIV = 15;
    private static double CONTROL_WIDTH_DIV = 12;

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

		playbackCntrl.setVisible(false);
		recorderCntrl.setVisible(false);

		numberLbl.setManaged(false);
		recorderCntrl.setManaged(false);
		playbackCntrl.setManaged(false);

		submitBtn.setText("Start");
		submitBtn.setOnAction(e -> {
			numberLbl.setManaged(true);
			recorderCntrl.setManaged(true);
			playbackCntrl.setManaged(true);

			playbackCntrl.setVisible(true);
			recorderCntrl.setVisible(true);

			nextRound();
		});

		recognitionLbl.setText("Welcome to the " + model.name());

		recorderCntrl.onMediaAvailable(this::mediaAvailable);
		recorderCntrl.onRecognitionComplete(this::recognize);
	}

    @Override
    public void    	resize(double width, double height) {
        super.resize(width, height);

        // Adjust children to be approx. the right size based on window size
        // These numbers are pretty good as they are
        double numberSize = Math.min(getHeight()/NUMBER_HEIGHT_DIV, getWidth()/NUMBER_WIDTH_DIV);
        double controlSize = Math.min(getHeight()/CONTROL_HEIGHT_DIV, getWidth()/CONTROL_WIDTH_DIV);

        numberLbl.setStyle("-fx-font-size: " + numberSize);
        playbackCntrl.setStyle("-fx-font-size: " + controlSize);
        recorderCntrl.setStyle("-fx-font-size: " + controlSize);
    }

	/**
	 * Called when the recorder has recorded something
	 */
	private void	mediaAvailable() {
		playbackCntrl.setMedia(recorderCntrl.media());
	}

	/**
	 * Called when the recorder has finished recognizing the text. Handles all
	 * the state
	 */
    private void	recognize(String text) {
		retryLbl.setVisible(false);

    	if(text.equals("")) {
    		recognitionLbl.setText("Nothing was recognized");
		} else {
			recognitionLbl.setText(text);
		}

    	if(_model.verify(text)) {
    		recognitionLbl.setTextFill(Color.GREEN);
		} else {
			if(_model.hasMoreTries()) {
				retryLbl.setVisible(true);
			} else {
				recorderCntrl.setDisable(true);
			}

    		recognitionLbl.setTextFill(Color.RED);
		}

		// Prepare user submit options
		submitBtn.setDisable(false);
		if(_model.hasNextRound()) {
			submitBtn.setText("Next");
			submitBtn.setOnAction(e -> nextRound());
		} else {
			submitBtn.setText("Finish");
			submitBtn.setOnAction(e -> finish());
		}
	}

	/**
	 * Sets up the screen for the next round
	 */
	private void	nextRound() {
		retryLbl.setVisible(false);

		playbackCntrl.setDisable(true);
		recorderCntrl.setDisable(false);

		recognitionLbl.setText("");
		playbackCntrl.dispose();

		submitBtn.setText("Next");
		submitBtn.setDisable(true);

		numberLbl.setText(Integer.toString(_model.nextRound()));
	}

	/**
	 * Sets up the screen when complete
	 */
	private void	finish() {
		numberLbl.setText(_model.getScore() + "/10");
		retryLbl.setVisible(false);

		playbackCntrl.setVisible(false);
		recorderCntrl.setVisible(false);

		recognitionLbl.setTextFill(Color.BLACK);

		if(_model.getScore() >= 8) {
			recognitionLbl.setText("Well done!");

			// TODO: If there are more tests, determine whether this test model has a harder version through test
			//  interface, instead of this abomination.
			if(_model instanceof EasyTest) {
				harderBtn.setVisible(true);
				harderBtn.setOnAction((e) -> _notifyReturn.accept(ReturnState.RETRY_HARDER));
			}
		} else {
			recognitionLbl.setText("Good try!");
		}

		retryBtn.setVisible(true);
		retryBtn.setOnAction((e) -> _notifyReturn.accept(ReturnState.RETRY));

		submitBtn.setText("Finish");
		submitBtn.setOnAction((e) -> _notifyReturn.accept(ReturnState.FINISHED));
	}

	private Test    				_model;
	private Consumer<ReturnState>	_notifyReturn;

    // FXML controls
	@FXML
	private VBox			testBox;
    @FXML
    private Label           numberLbl;
    @FXML
    private PlaybackControl playbackCntrl;
    @FXML
    private RecorderControl recorderCntrl;
    @FXML
	private Label			recognitionLbl;
    @FXML
	private Label			retryLbl;
    @FXML
	private Button			submitBtn;
    @FXML
	private Button			retryBtn;
    @FXML
	private Button			harderBtn;
}
