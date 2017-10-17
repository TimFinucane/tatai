package tatai.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.media.jfxmedia.logging.Logger;
import util.Files;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

/**
 * Gives capability for storing and retrieving scores
 */
public class ScoreKeeper {
    public static class Score {
        private Score(int score) {
            this.score = score;
        }

        public int  score;
        public Date date = new Date();
    }
    private static class Test {
        public Test(String test, int score) {
            this.test = test;
            scores = new Score[]{new Score(score)};
        }

        public String   test;
        public Score[]  scores;
    }

    public ScoreKeeper(String username) {
        _username = username;
        // Read JSON
        try(FileReader reader = new FileReader(Files.scoreFile(_username))) {
            Collections.addAll(_tests, _gson.fromJson(reader, Test[].class));
        } catch(FileNotFoundException e) {
            /* Do nothing. Doesnt exist yet */
        } catch(IOException e) {
            Logger.logMsg(Logger.ERROR, "Unexpected IO exception reading from " + _username + " score file");
            /* Be worried? */
        }
    }

    public Score[]  getScores(String test) {
        for(Test testInfo : _tests) {
            if(testInfo.test.equals(test))
                return testInfo.scores;
        }

        // Otherwise theres nothing, so return an empty array
        return new Score[0];
    }
    public void     addScore(String test, int score) {
        for(Test testInfo : _tests) {
            if(testInfo.test.equals(test)) {
                // Add score to end of score array
                Score[] scores = Arrays.copyOf(testInfo.scores, testInfo.scores.length + 1);
                scores[scores.length-1] = new Score(score);

                testInfo.scores = scores;

                save();
                return;
            }
        }

        // If no test exists create one
        _tests.add(new Test(test, score));
        save();
    }

    // Removes all scores
    public void             clear() {
        _tests.clear();
        Files.scoreFile(_username).delete();
    }

    private void            save() {
        try(FileWriter writer = new FileWriter(Files.scoreFile(_username))) {
            _gson.toJson(_tests, writer);
        } catch(IOException e) {
            Logger.logMsg(Logger.ERROR, "Error writing to " + _username + " score file");
        }
    }

    private final String        _username;
    // Uses a date format that only goes up to minutes
    private Gson                _gson = new GsonBuilder().setDateFormat("yyyy MMM dd HH:mm").setLenient().create();
    private ArrayList<Test>     _tests = new ArrayList<>();
}
