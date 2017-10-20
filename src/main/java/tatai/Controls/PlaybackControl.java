package tatai.Controls;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
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


        getChildren().add(_playbackBtn);
        _playbackBtn.setDisable(true);
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

    /**
     * A special resize function which only takes one parameter that determines both the width and height
     *  of the icons control.
     */
    //public void     resize(double size) {
  //      _imageView.setFitHeight(size);
  //  }

    // Called when the play button is pressed
    private void play() {
        _player.play();
        //_imageView.setImage(_stopImage);
        _playbackBtn.setGraphic(setStop());
        _playing = true;
    }
    // Called when the 'stop' button is pressed
    private void stop() {
        _player.stop();
        _playbackBtn.setGraphic(setPlay());
        //_imageView.setImage(_playImage);
        _playing = false;
    }

//    Helper methods to display icons.
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
    private JFXButton      _playbackBtn;

    //private Image       _playImage = new Image(getClass().getResourceAsStream("/tatai/icons/play.png"));
  //  private Image       _stopImage = new Image(getClass().getResourceAsStream("/tatai/icons/stop.png"));
   // private ImageView   _imageView = new ImageView(_playImage);
}
