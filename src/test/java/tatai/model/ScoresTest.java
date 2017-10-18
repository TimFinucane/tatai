package tatai.model;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ScoresTest {

    @Test
    public void TestSingleScore() {
        ScoreKeeper scores = new ScoreKeeper("user1");

        scores.addScore("test", 10);

        // Now reset and reload the score keeper

        scores = new ScoreKeeper("user1");

        ScoreKeeper.Score[] out = scores.getScores("test");

        Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        Date date = new Date(cal.getTimeInMillis());

        assertNotNull("Retrieved scores is null!", out);

        assertEquals("There must be a score retrieved when one is saved", 1, out.length);
        assertEquals("Not saving score correctly", 10, out[0].score);
        assertEquals("Date is incorrect", date, out[0].date);

        scores.clear();
    }
}
