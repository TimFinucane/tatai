package tatai.select;

import tatai.model.ScoreKeeper;
import tatai.model.test.TestJson;

public class StatsController extends SelectController {
    public StatsController(ScoreKeeper keeper) {
        _keeper = keeper;
    }

    protected boolean   filter(TestJson test) {
        return !test.practice;
    }

    protected void      buttonPressed(TestJson test) {
        displayChild(new tatai.StatsController(_keeper, test.name));
    }

    private ScoreKeeper _keeper;
}
