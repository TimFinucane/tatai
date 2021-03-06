package tatai.model.user;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.media.jfxmedia.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import tatai.model.test.Test;
import util.Files;

import java.io.*;
import java.util.Date;

/**
 * Tracks user information
 */
public class User {
    public static class     Score implements Comparable<Score>{
        Score(int score) {
            this.score = score;
        }

        public int  score;
        public Date date = new Date();

        @Override
        public int compareTo(Score o) {
            if(this.score < o.score) {
                return 1;
            }
            if(this.score == o.score) {
                return 0;
            }
            else
                return -1;
        }
    }
    static class            TestScores {
        TestScores(String test) {
            this.test = test;
            this.scores = new Score[0];
        }

        String      test;
        Score[]     scores;
    }
    public static class     UnfinishedTest {
        private UnfinishedTest(String name, Test.Memento memento) {
            this.name = name;
            this.memento = memento;
        }

        public String          name;
        public Test.Memento    memento;
    }
    private static class    UserInf {
        UnfinishedTest  savedTest;
        TestScores[]    tests;
    }

    /**
     * Tries to make or load a user with the given name.
     *
     * @throws IllegalArgumentException if there
     */
    public User(String username) throws IllegalArgumentException {
        // Check name conforms
        if(!username.matches("[\\w\\d-_]+")) {
            throw new IllegalArgumentException("Improper username! Must only contain letters and numbers " + username);
        }

        this.username = username;

        // Read file, or if it doesn't exist create it
        if(Files.listUsers().contains(this.username)) {
            try {
                UserInf inf = _gson.fromJson(new BufferedReader(new FileReader(Files.userFile(username))), UserInf.class);

                if(inf != null) {
                    _savedTest = inf.savedTest;
                    _testScores = new SimpleListProperty<>(FXCollections.observableArrayList(inf.tests));
                }
            } catch(FileNotFoundException e) {
                // Can't happen.
            }
        } else {
            save();
        }

        // Save on updates
        _testScores.addListener(((observable, oldValue, newValue) -> Platform.runLater(this::save)));
    }

    /**
     * Saves a test for the user to complete later
     */
    public void                     saveTest(String test, Test.Memento memento) {
        _savedTest = new UnfinishedTest(test, memento);
        save();
    }

    /**
     * Gets the user's unfinished test, if there is one.
     * If there isn't one, returns null
     */
    public UnfinishedTest           getUnfinishedTest() {
        return _savedTest;
    }

    public final String             username;

    ListProperty<TestScores>        testScoresProperty() {
        return _testScores;
    }

    /**
     * Removes all records of the given test from the user
     */
    public void                     clear(String name) {
        for(int i = 0; i < _testScores.getSize(); i++) {
            if(_testScores.get(i).test.equals(name)) {
                _testScores.remove(i);
                return;
            }
        }
    }

    /**
     * Saves the state of the user
     */
    private void                    save() {
        UserInf userInf = new UserInf();
        userInf.savedTest = _savedTest;
        userInf.tests = _testScores.toArray(new TestScores[0]);
        try(FileWriter fileWriter = new FileWriter(Files.userFile(username));
            BufferedWriter writer = new BufferedWriter(fileWriter)) {
            _gson.toJson(userInf, writer);
        } catch(IOException e) {
            Logger.logMsg(Logger.ERROR, "Unable to save user state: " + e.getMessage());
        }
    }

    private Gson                        _gson = new GsonBuilder().setDateFormat("yyyy MMM dd HH:mm").setLenient().create();

    private UnfinishedTest              _savedTest = null;
    private ListProperty<TestScores>    _testScores = new SimpleListProperty<>(FXCollections.observableArrayList());
}
