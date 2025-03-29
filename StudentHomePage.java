package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;

public class StudentHomePage {

    private DatabaseHelper databaseHelper;
    private User user;
    private Questions questionList;
    private Question selectedQuestion;  

    // This ListView holds VBoxes, each representing a single question + replies
    private ListView<VBox> questionListView;

    public StudentHomePage(DatabaseHelper databaseHelper, User user) {
        this.databaseHelper = databaseHelper;
        this.user = user;
        this.questionList = new Questions();
        this.selectedQuestion = null; 
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);

        Label studentLabel = new Label("Welcome, " + user.getUserName() + "! This is the Student Home. Ask questions and give answers");
        studentLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField questionField = new TextField();
        questionField.setPromptText("Enter your question...");

        Button submitQuestionButton = new Button("Ask Question");
        Button saveChangesButton = new Button("Save Changes");
        saveChangesButton.setDisable(true);

        questionListView = new ListView<>();
        Button editQuestionButton = new Button("Edit Question");
        editQuestionButton.setDisable(true);
        
        TextField searchQuestions = new TextField();
        searchQuestions.setPromptText("Look for a question?");
        Button searchQuestionsButton = new Button("Search Questions");
        
        // Search for questions by the text in 'searchQuestions'
        searchQuestionsButton.setOnAction(e -> {
            String inquiry = searchQuestions.getText().trim();
            searchQuestionList(inquiry);
        });
        
        // Initial load of all questions
        refreshQuestionList();

        // Add a new question
        submitQuestionButton.setOnAction(e -> {
            String questionText = questionField.getText().trim();
            if (questionText.isEmpty()) {
                showAlert("Error", "Question cannot be empty. Please enter a question.");
            } else {
                try {
                    databaseHelper.saveQuestion(user.getUserName(), questionText);
                    refreshQuestionList();
                    questionField.clear();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Select a question from the ListView to edit
        questionListView.setOnMouseClicked(e -> {
            int selectedIndex = questionListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < questionList.getAllQuestions().size()) {
                selectedQuestion = questionList.getAllQuestions().get(selectedIndex);
                if (selectedQuestion != null && selectedQuestion.getUserName().equals(user.getUserName())) {
                    editQuestionButton.setDisable(false);
                } else {
                    editQuestionButton.setDisable(true);
                }
            } else {
                selectedQuestion = null;
                editQuestionButton.setDisable(true);
            }
        });

        // Start editing the selected question
        editQuestionButton.setOnAction(e -> {
            if (selectedQuestion == null) {
                showAlert("Error", "No question selected. Please select a question before editing.");
                return;
            }
            questionField.setText(selectedQuestion.getText());
            saveChangesButton.setDisable(false);
            submitQuestionButton.setDisable(true);
        });

        // Save the changes to the edited question
        saveChangesButton.setOnAction(e -> {
            if (selectedQuestion != null) {
                String newQuestionText = questionField.getText();
                if (!newQuestionText.isEmpty()) {
                    try {
                        databaseHelper.updateQuestion(selectedQuestion.getId(), user.getUserName(), newQuestionText);
                        refreshQuestionList();
                        questionField.clear();
                        
                        selectedQuestion = null;
                        editQuestionButton.setDisable(true);
                        saveChangesButton.setDisable(true);
                        submitQuestionButton.setDisable(false);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        // Build the UI
        layout.getChildren().addAll(
            studentLabel,
            questionField,  
            searchQuestions,
            searchQuestionsButton,
            submitQuestionButton,
            saveChangesButton,
            questionListView,
            editQuestionButton
        );

        primaryStage.setScene(new Scene(layout, 800, 600));
        primaryStage.setTitle("Student Home Page");
        primaryStage.show();
    }

    // Similar to refreshQuestionList, but filters the questions by 'inquiry'
    private void searchQuestionList(String inquiry) {
        try {
            questionList = new Questions();
            for (Question q : databaseHelper.getAllQuestionsSortedByTrustedReviewers(user.getUserName())) {
                if (inquiry == null || inquiry.isEmpty() ||
                    q.getText().toLowerCase().contains(inquiry.toLowerCase())) {
                    questionList.addQuestion(q);
                }
            }

            questionListView.getItems().clear();

            if (questionList.getAllQuestions().isEmpty()) {
                VBox emptyBox = new VBox(new Label("No questions available."));
                questionListView.getItems().add(emptyBox);
                questionListView.setDisable(true);
                selectedQuestion = null; 
            } else {
                questionListView.setDisable(false);
                for (Question q : questionList.getAllQuestions()) {
                    VBox questionBox = createQuestionBox(q);
                    questionListView.getItems().add(questionBox);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Refresh the full list of questions
    private void refreshQuestionList() {
        try {
            questionList = new Questions();
            for (Question q : databaseHelper.getAllQuestionsSortedByTrustedReviewers(user.getUserName())) {
                questionList.addQuestion(q);
            }

            questionListView.getItems().clear();

            if (questionList.getAllQuestions().isEmpty()) {
                VBox emptyBox = new VBox(new Label("No questions available."));
                questionListView.getItems().add(emptyBox);
                questionListView.setDisable(true);
                selectedQuestion = null; 
            } else {
                questionListView.setDisable(false);
                for (Question q : questionList.getAllQuestions()) {
                    VBox questionBox = createQuestionBox(q);
                    questionListView.getItems().add(questionBox);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    private VBox createQuestionBox(Question q) throws SQLException {
        VBox questionBox = new VBox(5);
        questionBox.setStyle(
            "-fx-border-color: black; " +
            "-fx-border-width: 1px; " +
            "-fx-border-radius: 5px; " +
            "-fx-padding: 4px; " +
            "-fx-background-color: #f8f8f8;"
        );

        Label questionLabel = new Label(q.getUserName() + " asked: " + q.getText());
        questionLabel.setWrapText(true);

        Button replyButton = new Button("Reply");
        TextField replyField = new TextField();
        replyField.setPromptText("Write a reply...");
        replyField.setVisible(false);

        Button submitReplyButton = new Button("Submit Reply");
        submitReplyButton.setVisible(false);

        ListView<Answer> repliesListView = new ListView<>();
        repliesListView.setPrefHeight(100);
        repliesListView.getItems().addAll(databaseHelper.getRepliesForQuestion(q.getId()).getAnswersForQuestion(q.getId()));

        repliesListView.setCellFactory(lv -> new ListCell<Answer>() {
            @Override
            protected void updateItem(Answer answer, boolean empty) {
                super.updateItem(answer, empty);
                if (empty || answer == null) {
                    setGraphic(null);
                } else {
                	HBox hbox = new HBox(10);

                    // Check if the answer is from the same user who asked the question
                    boolean isSelfReply = answer.getUserName().equals(q.getUserName());
                    String displayUserName = isSelfReply ? answer.getUserName() + " **Clarification**" : answer.getUserName();

                    Label replyLabel = new Label(displayUserName + ": " + answer.getText());
                    hbox.getChildren().add(replyLabel);
                    // Button for answer author
                    if (answer.getUserName().equals(user.getUserName())) {
                        Button editButton = new Button("Edit Answer");
                        editButton.setOnAction(e -> {
                            TextField editField = new TextField(answer.getText());
                            Button saveButton = new Button("Save");
                            HBox editBox = new HBox(5, editField, saveButton);
                            setGraphic(editBox);

                            saveButton.setOnAction(ev -> {
                                String newText = editField.getText().trim();
                                if (!newText.isEmpty()) {
                                    try {
                                        databaseHelper.updateReply(answer.getId(), user.getUserName(), newText);
                                        answer.setText(newText);
                                        updateItem(answer, false);
                                    } catch (SQLException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            });
                        });
                        hbox.getChildren().add(editButton);
                    }

                    // Box to mark correct answer
                    if (q.getUserName().equals(user.getUserName())) {
                        CheckBox selectAnswerCheckbox = new CheckBox("Correct");
                        selectAnswerCheckbox.setSelected(answer.isSelected());

                        selectAnswerCheckbox.setOnAction(e -> {
                            boolean isSelected = selectAnswerCheckbox.isSelected();
                            try {
                                databaseHelper.updateAnswerSelection(answer.getId(), isSelected);
                                answer.setSelected(isSelected);
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        });

                        hbox.getChildren().add(selectAnswerCheckbox);
                    } else if (answer.isSelected()) {
                        Label selectedLabel = new Label("✔️ Selected as Correct");
                        selectedLabel.setStyle("-fx-text-fill: green;");
                        hbox.getChildren().add(selectedLabel);
                    }

                    setGraphic(hbox);
                }
            }
        });


        replyButton.setOnAction(e -> {
            replyField.setVisible(!replyField.isVisible());
            submitReplyButton.setVisible(replyField.isVisible());
        });

        submitReplyButton.setOnAction(e -> {
            String replyText = replyField.getText().trim();
            if (!replyText.isEmpty()) {
                try {
                    Answer newReply = new Answer(0, q.getId(), user.getUserName(), replyText);
                    databaseHelper.saveReply(newReply);

                    repliesListView.getItems().clear();
                    repliesListView.getItems().addAll(databaseHelper.getRepliesForQuestion(q.getId()).getAnswersForQuestion(q.getId()));
                    replyField.clear();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        Button deleteQuestionButton = new Button("Delete");
        deleteQuestionButton.setVisible(q.getUserName().equals(user.getUserName()));
        deleteQuestionButton.setOnAction(e -> {
            try {
                databaseHelper.deleteQuestion(q.getId(), user.getUserName());
                refreshQuestionList();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        questionBox.getChildren().addAll(
            questionLabel,
            replyButton,
            replyField,
            submitReplyButton,
            repliesListView,
            deleteQuestionButton
        );

        return questionBox;
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}