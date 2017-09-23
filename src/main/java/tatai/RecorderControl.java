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
        _mediaAvailableEvent = runnable;
    }

    /**
     * Runs the arg when a _recording has started
     */
    public void     onRecordingStarted(Runnable runnable) { _startEvent = runnable; }

    // Getters
    public Media    media() {
        return _recording.media();
    }

    // Starts a _recording
    private void    start() {
        if( _recording != null && !_recording.stopped()) {
            _recording.stop();
        }
        _button.setText("Start");
        _recording = Recording.start();

        if(_startEvent != null) {
            _startEvent.run();
        }
    }
    // Stops that _recording
    private void    stop() {
        _button.setText("Stop");
        _recording.stop();

        if(_mediaAvailableEvent != null) {
            _mediaAvailableEvent.run();
        }
    }

    private Button      _button;
    private Recording   _recording = null;

    private Runnable    _mediaAvailableEvent = null;
    private Runnable    _startEvent = null;
}
