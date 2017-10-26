package tatai;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import util.Files;

/**
 * Handles logging the user in, and getting the resulting username
 */
// TODO: Show existing users?
public class LoginController extends Controller {
    public LoginController() {
        loadFxml("Login");

        usernameFld.textProperty().addListener((observable, oldVal, newVal) -> onTextChange(newVal));
        signInBtn.setOnAction((e) -> onSubmit());
    }

    /**
     * For when a user is already logged in and may want to change
     */
    public LoginController(String curName) {
        this();
        loginLbl.setText("Switch User");
        usernameFld.setText(curName);
    }

    public String   name() {
        return _name;
    }

    private void    onSubmit() {
        // Check if we can use this name
        if(validate(usernameFld.getText()))
            _name = usernameFld.getText();
        else {
            // TODO: Have requirements
            new Alert(Alert.AlertType.ERROR, "Username is invalid, please have a username", ButtonType.OK)
                    .showAndWait();
            return;
        }

        // Check whether user wants to create a new profile
        if(!Files.userFile(_name).exists())
            new Alert(Alert.AlertType.CONFIRMATION,
                    "You will be creating a new account. Is this what you want?",
                    ButtonType.YES, ButtonType.NO).showAndWait().filter(response -> ButtonType.YES == response)
                    .ifPresent((e) -> exit(ReturnState.FINISHED));
        else
            exit(ReturnState.FINISHED);
    }

    private void    onTextChange(String newText) {
        if(validate(newText))
            if(Files.listUsers().contains(newText))
                indicatorIcon.setFill(Color.BLUE);
            else
                indicatorIcon.setFill(Color.GREEN);
        else
            indicatorIcon.setFill(Color.RED);
    }

    /**
     * Checks whether the username is valid, returns whether or not it is
     */
    private boolean validate(String name) {
        return name.matches("[\\w\\d-_]+");
    }

    private String                      _name;

    @FXML private Label                 loginLbl;
    @FXML private JFXTextField          usernameFld;
    @FXML private FontAwesomeIconView   indicatorIcon;
    @FXML private JFXButton             signInBtn;
}
