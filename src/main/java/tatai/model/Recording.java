package tatai.model;

import javafx.scene.media.Media;

import java.io.File;
import java.io.IOException;

/**
 * Class for recording from device input and saving that input to a file
 */
public class Recording {
    private static String TEMP_FOLDER = ".tmp/";
    private static String SOUND_LOG = TEMP_FOLDER + "sound.log";
    private static String SOUND_FILE = TEMP_FOLDER + "sound.mp3";

    private static String COMMAND = "ffmpeg -y -f alsa -acodec pcm_s16le -ac 1 -ar 22050 -i default " + SOUND_FILE;

    private Recording() {
        try {
            ProcessBuilder pb = new ProcessBuilder( COMMAND.split(" ") );

            pb.redirectError(new File(SOUND_LOG)); // TODO: Save this log when errors occur
            pb.redirectOutput();

            _recording = pb.start();
        } catch(IOException e) {
            throw new RuntimeException("Starting of recording failed with message: " + e.getMessage());
        }
    }

    /**
     * Starts a new recording. This is used instead of constructor to
     * make syntax clearer
     * @return The recording
     */
    public static Recording start() {
        File file = new File(TEMP_FOLDER);
        file.mkdir();

        return new Recording();
    }

    /**
     * Cleans all temporary files made by the recording
     */
    public void clean() {
        File dir = new File(TEMP_FOLDER);
        for(File child : dir.listFiles()) {
            child.delete();
        }
        dir.delete();
    }

    /**
     * Stops the recording if one is happening
     */
    public void     stop() {
        if(_recording != null) {
            _recording.destroy();
        }

        _media = new Media(new File("").toURI().toString() + SOUND_FILE);
    }
    public boolean  stopped() {
        return !_recording.isAlive();
    }

    public Media    media() {
        return _media;
    }

    /**
     * Gets the string result of running the recognizer on the input
     */
    public String   fileName() {
        return SOUND_FILE;
    }

    private Media			_media = null;
    private Process         _recording = null;
}
