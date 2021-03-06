package tatai.controls;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.css.PseudoClass;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * A control that plays sound media (no visuals)
 */
public class PlaybackControl extends Region {
    public PlaybackControl() {
        _playbackBtn = new JFXButton( "");

        _playbackBtn.setGraphic(setPlay());

        setId("playback-control");

        getChildren().add(_playbackBtn);
        _playbackBtn.setDisable(true);

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
        pseudoClassStateChanged(PseudoClass.getPseudoClass("stop"), true);
        _playbackBtn.setGraphic(setStop());
        _playing = true;
    }
    // Called when the 'stop' button is pressed
    private void stop() {
        _player.stop();
        pseudoClassStateChanged(PseudoClass.getPseudoClass("stop"), false);
        _playbackBtn.setGraphic(setPlay());
        _playing = false;
    }

    // Helper methods to display icons.
    private static FontAwesomeIconView setPlay() {
        FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.PLAY);
        icon.setGlyphSize(50);
        return icon;
    }

    private static FontAwesomeIconView setStop() {
        FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.STOP);
        icon.setGlyphSize(50);
        return icon;
    }

    private boolean     _playing = false;
    private MediaPlayer _player = null;
    private JFXButton   _playbackBtn;
}
