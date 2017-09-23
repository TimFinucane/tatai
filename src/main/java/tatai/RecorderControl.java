package tatai;

import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import tatai.model.Recognizer;
import tatai.model.Recording;

import java.util.function.Consumer;


/**
 * A control that allows you to record media, and gives access to the
 * recorded item
 */
public class RecorderControl extends Region {
    public RecorderControl() {
        _button = new Button("Play");
        getChildren().add(_button);
        _button.setOnAction((ignored) -> {
            if( _recording == null || _recording.stopped()) {
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
        _mediaAvailable = runnable;
    }
    public void     onRecognitionComplete(Consumer<String> handler) { _recognizerCompleted = handler; }
    /**
     * Runs the arg when a _recording has started
     */
    public void     onRecordingStarted(Runnable runnable) { _recordingStarted = runnable; }

    /**
     * Gets a Media object with the audio contained
     */
    public Media    media() {
        return _recording.media();
    }

    // Starts a recording. Is called when button is pressed
    private void    start() {
        if( _recording != null && !_recording.stopped()) {
            _recording.stop();
        }
        _button.setText("Start");
        _recording = Recording.start();

        if(_recordingStarted != null) {
            _recordingStarted.run();
        }
    }
    // Stops that recording. Called when button pressed again
    private void    stop() {
        _button.setText("Stop");
        _recording.stop();

        if(_mediaAvailable != null) {
            _mediaAvailable.run();
        }

        // Start recognizing the sequence
        Recognizer.recognize(_recording.fileName(), _recognizerCompleted);
    }

    private Button      _button;
    private Recording   _recording = null;

    private Runnable            _mediaAvailable = null;
    private Runnable            _recordingStarted = null;
    private Consumer<String>    _recognizerCompleted = null;
}
