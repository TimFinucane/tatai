package tatai;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * A control that plays sound media (no visuals)
 */
public class PlaybackControl extends Region {
    public PlaybackControl() {
        _playbackBtn = new Button( "", _imageView );
        _playbackBtn.setDisable(true);

        getChildren().add(_playbackBtn);

        _imageView.setPreserveRatio(true);
    }

    /**
     * Sets the media to be played
     */
    public void     setMedia(Media media) {
        _player.dispose();
        _player = new MediaPlayer(media);
        _playbackBtn.setDisable(false);
    }

    /**
     * Disposes of any media currently used. Must be done if you want to delete the media
     */
    public void     dispose() {
        _player.dispose();
        _playbackBtn.setDisable(true);
    }

    /**
     * A special resize function which only takes one parameter that determines both the width and height
     *  of the playback control.
     */
    public void     resize(double size) {
        _imageView.setFitHeight(size);
    }

    // Called when the play button is pressed
    private void play() {
        _player.play();
        _imageView.setImage(_stopImage);
        _playing = true;
    }
    // Called when the 'stop' button is pressed
    private void stop() {
        _player.stop();
        _imageView.setImage(_playImage);
        _playing = false;
    }

    private boolean     _playing = false;
    private MediaPlayer _player;
    private Button      _playbackBtn;

    private Image       _playImage = new Image(getClass().getResourceAsStream("/tatai/playback/play.png"));
    private Image       _stopImage = new Image(getClass().getResourceAsStream("/tatai/playback/stop.png"));
    private ImageView   _imageView = new ImageView(_playImage);
}
