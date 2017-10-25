package tatai.select;

import tatai.model.ScoreKeeper;
import tatai.model.test.TestJson;

public class SelectStatsController extends SelectController {
    public SelectStatsController(String user) {
        super(user, "Select a test to view your stats");
    }
    @Override
    protected boolean   filter(TestJson test) {
        return !test.practice;
    }
    @Override
    protected void      buttonPressed(TestJson test) {

        displayChild(new tatai.StatsController(new ScoreKeeper(user), test.name));
    }
}
