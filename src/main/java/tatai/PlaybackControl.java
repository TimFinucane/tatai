package tatai;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Paint;

/**
 * A control that plays sound media (no visuals)
 */
public class PlaybackControl extends Region {
    public PlaybackControl() {
        _playbackBtn = new JFXButton( "");

        _playbackBtn.setGraphic(setPlay());
        _playbackBtn.setDisable(true);

        getChildren().add(_playbackBtn);

       // _imageView.setPreserveRatio(true);

        _playbackBtn.setOnAction((ignored) -> {
            if(!_playing) {
                play();
            } else {
                stop();
            }
        });
    }

    /**
     * Sets the media to be played
     */
    public void     setMedia(Media media) {
        dispose();
        _player = new MediaPlayer(media);
        _player.setOnEndOfMedia(this::stopped);

        _playbackBtn.setDisable(false);
    }

    /**
     * Disposes of any media currently used. Must be done if you want to delete the media
     */
    public void     dispose() {
        if(_player != null) {
            _player.dispose();
            _playbackBtn.setDisable(true);
        }
    }

    /**
     * Called when the media has automatically stopped
     */
    private void stopped() {
        stop();
    }

    // Called when the play button is pressed
    private void play() {
        _player.play();
        _playbackBtn.setGraphic(setStop());
        _playing = true;
    }
    // Called when the 'stop' button is pressed
    private void stop() {
        _player.stop();
        _playbackBtn.setGraphic(setPlay());
        _playing = false;
    }

    // Helper methods to display icons.
    private static FontAwesomeIconView setPlay() {
        FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.PLAY);
        icon.setFill(Paint.valueOf("#00e640"));
        icon.setGlyphSize(60);
        return icon;
    }

    private static FontAwesomeIconView setStop() {
        FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.STOP);
        icon.setFill(Paint.valueOf("#f03434"));
        icon.setGlyphSize(60);
        return icon;
    }

    private boolean     _playing = false;
    private MediaPlayer _player = null;
    private Button      _playbackBtn;
}
