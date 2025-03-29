package application;

import java.util.ArrayList;
import java.util.List;

public class Answers {
    private List<Answer> answerList;

    public Answers() {
        this.answerList = new ArrayList<>();
    }

    public void addAnswer(Answer answer) {
        answerList.add(answer);
    }
    

    public List<Answer> getAnswersForQuestion(int questionId) {
        List<Answer> relatedAnswers = new ArrayList<>();
        for (Answer a : answerList) {
            if (a.getQuestionId() == questionId) {
                relatedAnswers.add(a);
            }
        }
        return relatedAnswers;
    }
}