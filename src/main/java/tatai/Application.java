package tatai;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import tatai.model.test.Scores;
import tatai.model.test.TestJson;
import tatai.model.test.TestParser;

import javafx.stage.StageStyle;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * The top level class of this project. Is controller of the application/initial window
 */
public class Application extends javafx.application.Application {

    private static final String APP_NAME = "T\u0101tai";
    private Stage _stage;
    private Scene _mainScene;


//    JavaFX Controls
    @FXML private Button _btnHome;
    @FXML private Button _btnPractice;
    @FXML private Button _btnTest;
    @FXML private Button _btnStats;
    @FXML private AnchorPane _viewWindow;

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
            _mainScene = new Scene(loader.load());

        } catch(IOException e) {
            throw new RuntimeException("Unable to load tatai.Application.fxml: " + e.getMessage());
        }
        //_stage.initStyle(StageStyle.UNDECORATED);
        _stage.setTitle(APP_NAME);

        _stage.setScene(_mainScene);

        _stage.show();

        _btnHome.setOnAction(event -> Home());
        _btnPractice.setOnAction(event -> Practice());
        _btnTest.setOnAction((event -> Test()));
        _btnStats.setOnAction(event -> Stats());
    }

//    Called when home button pressed
    private void Home(){
        throw new NotImplementedException();
    }

//    Called when practice button pressed
    private void Practice(){
        throw new NotImplementedException();
    }

//    Called when test button pressed
    private void Test(){
        SelectTestController controller = new SelectTestController();
        _viewWindow.getChildren().add(controller);

    }

//    Called when stats button pressed
    private void Stats(){
        throw new NotImplementedException();
    }

    /**
     * Temporary method for creating basic tests if they are not already made
     */
    private static void createBasicTests() {
        if(TestParser.listTests().contains("Easy Test"))
            return;

        TestJson basic = new TestJson();
        basic.name = "Easy Test";
        basic.questions = new TestJson.Question[1];
        basic.questions[0] = new TestJson.Question();
        basic.questions[0].rounds = 10;
        basic.questions[0].tries = 2;
        basic.questions[0].question = "(1 to 9)";

        try {
            TestParser.makeTest(basic);
        } catch(IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        basic.name = "Hard Test";
        basic.questions = new TestJson.Question[1];
        basic.questions[0] = new TestJson.Question();
        basic.questions[0].rounds = 10;
        basic.questions[0].tries = 2;
        basic.questions[0].question = "(1 to 99)";

        try {
            TestParser.makeTest(basic);
        } catch(IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
