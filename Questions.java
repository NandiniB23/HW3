package application;

import java.util.ArrayList;
import java.util.List;

public class Questions {
    private List<Question> questionList;

    public Questions() {
        this.questionList = new ArrayList<>();
    }

    
    public void addQuestion(Question question) {
        questionList.add(question);
    }
    

    public List<Question> getAllQuestions() {
        return questionList;
    }

    
}