package tatai.model;

import org.junit.Test;
import tatai.model.test.Scores;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

public class ScoresTest {

    @Test
    public void TestSingleScore() {
        Scores.save("user", "test", 10);

        Scores.Score[] scores = Scores.retrieve("test");

        Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        Date date = new Date(cal.getTimeInMillis());

        assertNotNull("Retrieved scores is null!", scores);

        assertEquals("There must be a score retrieved when one is saved", 1, scores.length);
        assertTrue("Not saving name correctly", scores[0].user.equals("user"));
        assertEquals("Not saving score correctly", 10, scores[0].score);
        assertEquals("Date is incorrect", date, scores[0].date);

        Scores.clear("test");
    }

    @Test
    public void TestAppendingScore() throws NullPointerException {
        Scores.save("user", "test2", 10);
        Scores.save("user2", "test2", 8);

        Scores.Score[] scores = Scores.retrieve("test2");

        assertNotNull("Retrieved scores is null!", scores);

        assertEquals("Appending scores doesn't work :(", 2, scores.length);

        //Scores.clear("test2");
    }
}
