package tatai.model;

import org.junit.Test;
import tatai.model.test.Scores;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ScoresTest {
    @Test
    public void TestSingleScore() {
        Scores.save("user", "test", 10);

        Scores.Score[] scores = Scores.retrieve("test");

        Date date = new Date();

        assertNotNull("Retrieved scores is null!", scores);

        assertEquals("There must be a score retrieved when one is saved", scores.length, 1);
        assertTrue("Not saving name correctly", scores[0].user.equals("user"));
        assertEquals("Not saving score correctly", scores[0].score, 10);
        assertEquals("Date is incorrect", scores[0].date, date);
    }

    @Test
    public void TestAppendingScore() throws NullPointerException {
        Scores.save("user", "test", 10);
        Scores.save("user2", "test", 8);

        Scores.Score[] scores = Scores.retrieve("test");

        assertNotNull("Retrieved scores is null!", scores);

        assertEquals("Appending scores doesn't work :(", scores.length, 2);
    }
}
