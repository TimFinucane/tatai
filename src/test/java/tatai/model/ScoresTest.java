package tatai.model;

import org.junit.Test;
import tatai.model.user.ScoreKeeper;
import tatai.model.user.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ScoresTest {

    @Test
    public void TestSingleScore() {
        User user = new User("user1");

        ScoreKeeper scores = new ScoreKeeper(user, "test");

        scores.addScore(10);

        // Now reset and reload the score keeper

        scores = new ScoreKeeper(user, "test");

        User.Score[] out = scores.getScores();

        assertNotNull("Retrieved scores is null!", out);

        assertEquals("There must be a score retrieved when one is saved", 1, out.length);
        assertEquals("Not saving score correctly", 10, out[0].score);

        user.clear("test");
    }
}
