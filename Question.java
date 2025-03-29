package application;

public class Question {
    private int id;
    private String text;
    private String userName;
    private Answers answers; 

    public Question(int id, String userName, String text) {
        this.id = id;
        this.userName =  userName;
        this.text = text;
        this.answers = new Answers();
    }

    
    public int getId() { return id; }
    public String getText() { return text; }
    public String getUserName() { return userName; }
    public Answers getAnswers() { return answers; } 

    public void addAnswer(Answer answer) {
        this.answers.addAnswer(answer); 
    }
}