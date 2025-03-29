package application;

public class Answer {
    private int id;
    private int questionId;
    private String userName;
    private String text;
    private boolean isSelected;

    public Answer(int id, int questionId, String userName, String text) {
        this.id = id;
        this.questionId = questionId;
        this.userName = userName;
        this.text = text;
        this.isSelected = false;  // Default to false
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    // Existing getters and setters below remain unchanged
    public int getId() {
        return id;
    }

    public int getQuestionId() {
        return questionId;
    }

    public String getUserName() {
        return userName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
