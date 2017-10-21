package tatai.model.test;

import tatai.model.question.Question;

import java.util.List;

class ScoredTest extends Test {
    static class QuestionInfo {
        QuestionInfo(int rounds, int tries, Question question) {
            this.rounds = rounds;
            this.tries = tries;
            this.question = question;
        }

        int			rounds;
        int         tries;
        Question 	question;
    }

    ScoredTest(List<QuestionInfo> questions) {
        _questions = questions;
    }

    public boolean 	    tryAnswer(String answer) {
        _tries--;
        return super.tryAnswer(answer);
    }

    public boolean 		hasAnotherRound() {
        return _questionIndex == (_questions.size() - 1) && _round == _questions.get(_questionIndex).rounds;
    }
    public boolean 		hasAnotherTry() {
        return _tries >= 0;
    }

    protected Question	nextQuestion() {
        _round++;

        if(_round > _questions.get(_questionIndex).rounds) {
            _round = 1;
            _questionIndex += 1;

            if(_questionIndex == _questions.size())
                throw new IllegalStateException("Test is finished. Please do not do this");
        }

        _tries = _questions.get(_questionIndex).tries;

        return _questions.get(_questionIndex).question;
    }

    private int                 _tries;

    private int				    _questionIndex = 0;
    private int				    _round = 0;
    private List<QuestionInfo>  _questions;
}
