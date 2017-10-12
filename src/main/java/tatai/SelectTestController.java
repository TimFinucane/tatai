package tatai;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tatai.model.test.TestJson;
import tatai.model.test.TestParser;


import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A window where you can select which test you want to do
 */
public class SelectTestController extends AnchorPane{

    private TestController _test;

//    JavaFX Controls
    @FXML private Button _btnEasy;
    @FXML private Button _btnIntermediate;
    @FXML private Button _btnAdvanced;
    @FXML private Button _btnCustom;
    @FXML private Button _btnCreateCustom;
    @FXML private Button _btnRemoveCustom;

	public SelectTestController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tatai/SelectTest.fxml"));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load tatai.SelectTest.fxml: " + e.getMessage());
        }

        createBasicTests();

        _btnEasy.setOnAction(event -> easyTest());
        _btnIntermediate.setOnAction(event -> intermediateTest());
        _btnAdvanced.setOnAction(event -> advancedTest());
        _btnCustom.setOnAction(event -> customTest());
        _btnCreateCustom.setOnAction(event -> createCustom());
        _btnRemoveCustom.setOnAction(event -> removeCustom());
    }

//    Called when easy button pressed.
    private void easyTest() {

	    try{

            _test = new TestController(TestParser.read("Easy Test"), (state) ->
                    Platform.runLater(() -> testComplete(state, true))
            );
            this.getChildren().add(_test);

        }
        catch(FileNotFoundException e) {
	        //TODO: FIlebox
            throw new RuntimeException("Easy test doesn't exist!");
        }


    }

//    Called when intermediate button pressed.
    private void intermediateTest() {
	    throw new NotImplementedException();
    }

//    Called when advanced button pressed
    private void advancedTest() {
	    throw new NotImplementedException();
    }

//    Called when custom button pressed
    private void customTest() {
	    throw new NotImplementedException();
	    //TODO: Opens a custom test.
    }

//    Called when create custom button pressed
    private void createCustom() {
	    throw new NotImplementedException();
	    //TODO: creates a custom test.
    }

//    Called when remove custom button pressed
    private void removeCustom() {
	    throw new NotImplementedException();
	    //TODO: removes a custom test
    }

    private void testComplete(TestController.ReturnState state, boolean easy) {
        switch(state) {
            case QUIT:
            case FINISHED: {
                this.getChildren().remove(_test);
            }

        }
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



