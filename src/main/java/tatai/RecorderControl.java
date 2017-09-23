package tatai;

import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import tatai.model.Recording;


/**
 * A control that allows you to record media, and gives access to the
 * recorded item
 */
public class RecorderControl extends Region {
    public RecorderControl() {
        button = new Button("Play");
        getChildren().add(button);
        button.setOnAction((ignored) -> {
            if(recording == null || recording.stopped()) {
                start();
            } else {
                stop();
            }
        });
    }

    /**
     * Runs the arg when a new piece of media becomes available
     */
    public void     onMediaAvailable(Runnable runnable) {
        mediaAvailableEvent = runnable;
    }

    /**
     * Runs the arg when a recording has started
     */
    public void     onRecordingStarted(Runnable runnable) { startEvent = runnable; }

    // Getters
    public Media    media() {
        return recording.output();
    }
    public String   filename() {
        return recording.outputName();
    }

    // Starts a recording
    private void    start() {
        if(recording != null && !recording.stopped()) {
            recording.stop();
        }
        button.setText("Start");
        recording = Recording.start();

        if(startEvent != null) {
            startEvent.run();
        }
    }
    // Stops that recording
    private void    stop() {
        button.setText("Stop");
        recording.stop();

        if(mediaAvailableEvent != null) {
            mediaAvailableEvent.run();
        }
    }

    private Button      button;
    private Recording   recording = null;

    private Runnable    mediaAvailableEvent = null;
    private Runnable    startEvent = null;
}
