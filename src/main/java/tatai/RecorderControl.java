package tatai;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.paint.Paint;
import tatai.model.Recognizer;
import tatai.model.Recording;

import java.util.function.Consumer;


/**
 * A control that allows you to record media, and gives access to the
 * recorded item
 */
public class RecorderControl extends Region {

    public RecorderControl() {

        _button = new JFXButton("");
        _button.setGraphic(setRecord());

        getChildren().add(_button);

        // Play when the button is pressed first, then Stop on the second time, repeat.
        _button.setOnAction((ignored) -> {
            if( _recording == null || _recording.stopped()) {
                start();
            } else {
                stop();
            }
        });

        // Clean up on exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if(_recording != null) {
                _recording.clean();
            }
        }));

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

    /**
     * A special resize function which only takes one parameter that determines both the width and height
     *  of the icons control.
     */
  //  public void     resize(double size) {
   //     _imageView.setFitHeight(size);
  // }

    // Starts a recording. Is called when button is pressed
    private void    start() {
        if( _recording != null && !_recording.stopped()) {
            _recording.stop();
        }
        //_imageView.setImage(_stopImage);
        _button.setGraphic(setStop());
        _recording = Recording.start();

        if(_recordingStarted != null) {
            _recordingStarted.run();
        }
    }
    // Stops that recording. Called when button pressed again
    private void    stop() {
        //_imageView.setImage(_recordImage);
        _button.setGraphic(setRecord());
        _recording.stop();

        if(_mediaAvailable != null) {
            _mediaAvailable.run();
        }

        // Start recognizing the sequence
        Recognizer.recognize(_recording.fileName(), _recognizerCompleted);
    }

//    Just some helper methods to create the icon to be displayed.
    private static FontAwesomeIconView setRecord() {
        FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.CIRCLE);
        icon.setFill(Paint.valueOf("#f03434"));
        icon.setGlyphSize(60);
        return icon;
    }

    private static FontAwesomeIconView setStop() {
        FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.STOP);
        icon.setFill(Paint.valueOf("#f03434"));
        icon.setGlyphSize(60);
        return icon;
    }

    private Button      _button;
    private Recording   _recording = null;

    private Runnable            _mediaAvailable = null;
    private Runnable            _recordingStarted = null;
    private Consumer<String>    _recognizerCompleted = null;

    //private Image       _recordImage = new Image(getClass().getResourceAsStream("/tatai/icons/record.png"));
    //private Image       _stopImage = new Image(getClass().getResourceAsStream("/tatai/icons/stop.png"));
    //private ImageView   _imageView = new ImageView(_recordImage);
}
