package tatai.model.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.media.jfxmedia.logging.Logger;
import util.Files;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;

public class Scores {
    public static class Score {
        public Score(String user, int score) {
            this.user = user;
            this.score = score;
            this.date = new Date(); // Now
        }

        public String  user;
        public int     score;
        public Date    date;
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
     * Saves a score. I hate this function
     */
    public static void      save(String user, String test, int score) {
        File file = Files.scoreFile(test);

        Score[] scores = null;

        // Horrible, but cant find any other way of doing it
        if(file.exists()) {
            try( FileReader reader = new FileReader(file) ) {
                List<Score> list = new ArrayList<>();
                Collections.addAll(list, _gson.fromJson(reader, Score[].class));
                list.add(new Score(user, score));
                scores = list.toArray(new Score[0]);
            } catch(FileNotFoundException e) {
                // Cant happen so thanks.
            } catch(IOException e) {
                Logger.logMsg(Logger.ERROR, "Unexpected IO exception writing to " + test + " score file: "
                        + e.getMessage());
            }
        } else {
            scores = new Score[]{ new Score(user, score) };
        }

        try(FileWriter writer = new FileWriter(file, false)) {
            _gson.toJson(scores, writer);
        } catch(IOException e) {
            Logger.logMsg(Logger.ERROR, "Unexpected IO exception writing to " + test + " score file: "
                    + e.getMessage());
        }
    }

    /**
     * Clears all scores
     */
    public static void	    clear(String test) {
        Files.scoreFile(test).delete();
    }

    private static Gson     _gson = new GsonBuilder().setDateFormat("yyyy MMM dd HH:mm").setLenient().create();
}
