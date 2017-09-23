package tatai.model;

import javafx.scene.media.Media;

import java.io.File;
import java.io.IOException;

/**
 * Class for recording from device input and saving that input to a file
 */
public class Recording {
    private static String TEMP_FOLDER = "tmp/";
    private static String SOUND_LOG = TEMP_FOLDER + "sound.log";
    private static String SOUND_FILE = TEMP_FOLDER + "sound.mp3";

    private static String COMMAND = "ffmpeg -y -f alsa -ac 1 -i default " + SOUND_FILE;

    private Recording() {
        try {
            ProcessBuilder pb = new ProcessBuilder( COMMAND.split(" ") );

            pb.redirectError(getLog());
            pb.redirectOutput();

            recording = pb.start();
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
        return new Recording();
    }

    /**
     * Stops the recording, and gives a Media of the sound alone
     * @return The recording just made
     */
    public void     stop() {
        if(recording != null) {
            recording.destroy();
        }

        media = new Media(new File("").toURI().toString() + SOUND_FILE);
    }
    public boolean  stopped() {
        return !recording.isAlive();
    }

    public Media    output() {
        return media;
    }
    /**
     * Get the relative path to the sound file
     */
    public String   outputName() {
        return SOUND_LOG;
    }
    public File     getLog() {
        return new File(SOUND_LOG);
    }

    private Media			media = null;
    private Process         recording = null;
}
