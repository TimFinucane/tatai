package tatai.model.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.media.jfxmedia.logging.Logger;
import util.Files;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Scores {
    public static class Score {
        public Score(String user, int score) {
            this.user = user;
            this.score = score;
            this.date = new Date(); // Now
        }

        String  user;
        int     score;
        Date    date;
    }

    /**
     * Gets all recorded scores
     */
    @Nullable
    public static Score[]   retrieve(String test) {
        try(FileReader reader = new FileReader(Files.scoreFile(test))) {
            return _gson.fromJson(reader, Score[].class);
        } catch(FileNotFoundException e) {
            return null;
        } catch(IOException e) {
            Logger.logMsg(Logger.ERROR, "Unexpected IO exception reading from " + test + " score file");
            return null;
        }
    }

    /**
     * Saves a score
     */
    public static void      save(String user, String test, int score) {
        try(FileWriter writer = new FileWriter(Files.scoreFile(test))) {
            _gson.toJson(new Score(user, score), (Appendable)writer);
        } catch(IOException e) {
            Logger.logMsg(Logger.ERROR, "Unexpected IO exception writing to " + test + " score file");
        }
    }

    /**
     * Clears all scores
     */
    public static void	    clear(String test) {
        Files.scoreFile(test).delete();
    }

    private static Gson     _gson = new GsonBuilder().setDateFormat("yyyy HH:mm").create();
}
