package tatai.select;

import tatai.model.test.TestJson;
import tatai.model.user.ScoreKeeper;
import tatai.model.user.User;

public class SelectStatsController extends SelectController {
    public SelectStatsController(User user) {
        super(user, "Select a test to view your stats");
    }
    @Override
    protected boolean   filter(TestJson test) {
        return !test.practice;
    }
    @Override
    protected void      buttonPressed(TestJson test) {
        displayChild(new tatai.StatsController(new ScoreKeeper(user, test.name)));
    }
}
