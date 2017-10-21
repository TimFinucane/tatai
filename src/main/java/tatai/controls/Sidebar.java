package tatai.controls;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import util.Views;

public class Sidebar extends VBox {
    public interface User {
        void home();
        void practice();
        void test();
        void stats();
        void info();
    }

    public Sidebar(User user) {
        Views.load("Sidebar", this, this);

        homeBtn.setOnAction(e -> user.home());
        practiceBtn.setOnAction(e -> user.practice());
        testBtn.setOnAction(e -> user.test());
        statsBtn.setOnAction(e -> user.stats());
        infoBtn.setOnAction(e -> user.info());

        statsBtn.setVisible(false);
        testBtn.setVisible(false);
    }

    /**
     * Unlocks buttons that must have a user to exist. Kinda breaks SRP but oh well
     */
    public void     unlockButtons() {
        statsBtn.setVisible(true);
        testBtn.setVisible(true);

        FadeTransition fadeInStats = new FadeTransition(Duration.seconds(3.0), statsBtn);
        FadeTransition fadeInTest = new FadeTransition(Duration.seconds(3.0), testBtn);

        fadeInStats.setFromValue(0.0);
        fadeInStats.setToValue(1.0);
        fadeInStats.setCycleCount(1);
        fadeInStats.setAutoReverse(false);

        fadeInTest.setFromValue(0.0);
        fadeInTest.setToValue(1.0);
        fadeInTest.setCycleCount(1);
        fadeInTest.setAutoReverse(false);

        new ParallelTransition(fadeInStats, fadeInTest).play();
    }

    /**
     * Opens the side pane to show text
     */
    @FXML
    private void    openPane(MouseEvent e) {
        homeBtn.setContentDisplay(ContentDisplay.LEFT);
        testBtn.setContentDisplay(ContentDisplay.LEFT);
        practiceBtn.setContentDisplay(ContentDisplay.LEFT);
        statsBtn.setContentDisplay(ContentDisplay.LEFT);

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,             new KeyValue(prefWidthProperty(), SIDE_MIN)),
                new KeyFrame(Duration.millis(100.0d),   new KeyValue(prefWidthProperty(), SIDE_MAX))
        );
        timeline.play();
    }

    /**
     * Closes the side pane when hovering has stopped
     */
    @FXML
    private void    closePane(MouseEvent e) {
        homeBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        testBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        practiceBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        statsBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,             new KeyValue(prefWidthProperty(), SIDE_MAX)),
                new KeyFrame(Duration.millis(100.0d),   new KeyValue(prefWidthProperty(), SIDE_MIN))
        );
        timeline.play();
    }

    private static final double SIDE_MIN = 55.0;
    private static final double SIDE_MAX = 200.0;

    // JavaFX Controls
    @FXML private Button    homeBtn;
    @FXML private Button    practiceBtn;
    @FXML private Button    testBtn;
    @FXML private Button    statsBtn;
    @FXML private Button    infoBtn;
}
