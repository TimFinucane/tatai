package tatai;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import tatai.model.test.Scores;

import java.io.IOException;

/**
 * The top level class of this project. Is controller of the application/initial window
 */
public class Application extends javafx.application.Application {
    private static String APP_NAME = "T\u0101tai";

    // Do this as cheap hack so that buttons are same size
    private static String EASY_ENGLISH = "  Simple  ";
    private static String EASY_MAORI = "Ng\u0101wari";
    private static String HARD_ENGLISH = "  Difficult  ";
    private static String HARD_MAORI = "Wh\u0113uaua";
    private static String INFO_ENGLISH = "    Info    ";
    private static String INFO_MAORI = "P\u0101rongo";

    // Determines ratio of window size to text size
    private static double TEXT_WIDTH_DIV = 24;
    private static double TEXT_HEIGHT_DIV = 14;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        _stage = stage;

        // Load the application fxml, and set self to be its controller
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tatai/Application.fxml"));
        loader.setController(this);

        try {
            _mainScreen = new Scene(loader.load());
        } catch(IOException e) {
            throw new RuntimeException("Unable to load tatai.Application.fxml: " + e.getMessage());
        }

        _stage.setTitle(APP_NAME);
        _stage.setScene(_mainScreen);

        // Ensure buttons are right size compared to window
        _mainScreen.widthProperty().addListener( e -> onResize() );
        _mainScreen.heightProperty().addListener( e -> onResize() );

        // Go from english to maori when buttons are hovered
        easyBtn.textProperty().bind(Bindings.when(easyBtn.hoverProperty()).then(EASY_ENGLISH).otherwise(EASY_MAORI));
        hardBtn.textProperty().bind(Bindings.when(hardBtn.hoverProperty()).then(HARD_ENGLISH).otherwise(HARD_MAORI));
        infoBtn.textProperty().bind(Bindings.when(infoBtn.hoverProperty()).then(INFO_ENGLISH).otherwise(INFO_MAORI));

        easyBtn.setOnAction((ignored) -> easyTest());
        hardBtn.setOnAction((ignored) -> hardTest());
        infoBtn.setOnAction((ignored) -> info());

        // Clear scores on shutdown, so only most recent session is shown
        Runtime.getRuntime().addShutdownHook(new Thread(Scores::clear));

        _stage.show();
    }

    // Called when the easy button has been pressed
    private void easyTest() {
    	/*TestController test = new TestController(new Test("EasyTest", 1, 9), (state) ->
                Platform.runLater(() -> testComplete(state, true))
        );

        _stage.setScene(new Scene(test));
        _stage.show();*/
    }
    // Called when the hard button has been pressed
    private void hardTest() {
        /*TestController test = new TestController(new Test("HardTest",), (state) ->
                Platform.runLater(() -> testComplete(state, false))
        );

        _stage.setScene(new Scene(test));
        _stage.show();*/
    }
    // Called when the info button has been pressed
    private void info() {
        InfoController info = new InfoController(() ->
            Platform.runLater(() -> {
            _stage.setScene(_mainScreen);
            _stage.show();
        }));

        _stage.setScene(new Scene(info));
        _stage.show();
    }

    /**
     * Called when the main screen resizes, and adjusts the button sizes as a result
     */
    private void onResize() {
        // Adjust children to be approx. the right size based on window size
        // These numbers are pretty good as they are
        double size = Math.min(_mainScreen.getHeight() / TEXT_HEIGHT_DIV, _mainScreen.getWidth() / TEXT_WIDTH_DIV);

        easyBtn.setStyle("-fx-font-size: " + size);
        hardBtn.setStyle("-fx-font-size: " + size);
        infoBtn.setStyle("-fx-font-size: " + size);
    }

    /**
     * Run (on EDT) when a test is finished and control is relinquished to the application.
     */
    private void testComplete(TestController.ReturnState state, boolean easy) {
        switch(state) {
            case QUIT:
            case FINISHED:
                _stage.setScene(_mainScreen);
                _stage.show();
                break;
            case RETRY:
                if(easy) {
                    easyTest();
                } else {
                    hardTest();
                }
                break;
            case RETRY_HARDER:
                hardTest();
        }
    }

    private Scene   _mainScreen;
    private Stage   _stage;

    // JavaFX controls
    @FXML
    private Button  easyBtn;
    @FXML
    private Button  hardBtn;
    @FXML
    private Button  infoBtn;
}
