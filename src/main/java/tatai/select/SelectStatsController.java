package tatai.select;

import com.jfoenix.controls.JFXButton;
import tatai.model.test.TestJson;
import tatai.model.user.ScoreKeeper;
import tatai.model.user.User;

public class SelectStatsController extends SelectController {
    public SelectStatsController(User user) {
        super(user, "Select a test to view your stats");
    }
    @Override
    protected boolean   filter(TestJson test, JFXButton button) {
        return !test.practice && new ScoreKeeper(user, test.name).getScores().length != 0;
    }
    @Override
    protected void      buttonPressed(TestJson test) {
        displayChild(new tatai.StatsController(new ScoreKeeper(user, test.name), test));
    }

}
