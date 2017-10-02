package tatai;

import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * A control that plays sound media (no visuals)
 */
public class PlaybackControl extends Region {
    public PlaybackControl() {
        _playbackBtn = new Button("Play");
        _playbackBtn.setDisable(true);

        getChildren().add(_playbackBtn);

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
        _playbackBtn.setText("Stop");
        _playing = true;
    }
    // Called when the 'stop' button is pressed
    private void stop() {
        _player.stop();
        _playbackBtn.setText("Play");
        _playing = false;
    }

    private boolean     _playing = false;
    private MediaPlayer _player = null;
    private Button      _playbackBtn;
}
