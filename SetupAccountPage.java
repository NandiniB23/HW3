package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.*;

import java.sql.SQLException;

public class SetupAccountPage {

    private final DatabaseHelper databaseHelper;

    public SetupAccountPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the Setup Account page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
        // Input fields for userName, password, and invitation code
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);

        TextField inviteCodeField = new TextField();
        inviteCodeField.setPromptText("Enter InvitationCode");
        inviteCodeField.setMaxWidth(250);

        // Label to display error messages for invalid input or registration issues
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        // Label to display error messages for invalid userNames:
        Label errorUserNameLabel = new Label();
        errorUserNameLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        // Label to display error messages for invalid passwords:
        Label errorPasswordLabel = new Label();
        errorPasswordLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button setupButton = new Button("Setup");

        setupButton.setOnAction(a -> {
            // Retrieve user input
            String userName = userNameField.getText();
            String password = passwordField.getText();
            String code = inviteCodeField.getText();

            // Validate userName
            String userValidation = UserNameRecognizer.checkForValidUserName(userName);
            System.out.println(userValidation);
            errorUserNameLabel.setText(userValidation);

            // Validate password
            String passwordValidation = PasswordEvaluator.evaluatePassword(password);
            System.out.println(passwordValidation);
            errorPasswordLabel.setText(passwordValidation);

            try {
                // Check if the user already exists
                if (!databaseHelper.doesUserExist(userName)) {

                    // Validate the invitation code
                    if (databaseHelper.validateInvitationCode(code)) {

                        // Create a new user and register them in the database
                        User user = new User(userName, password, "user");
                        databaseHelper.register(user);

                        // After successful registration, prompt for full name and email
                        showUserDetailsPage(primaryStage, user);

                    } else {
                        errorLabel.setText("Please enter a valid invitation code");
                    }
                }

            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        // Gather the userName, password, login button and error labels for general and userName/password validation errors
        layout.getChildren().addAll(userNameField, passwordField, inviteCodeField, setupButton, errorLabel, errorUserNameLabel, errorPasswordLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }

    /**
     * After successful registration, prompt the user for their full name and email.
     * @param primaryStage The primary stage where the scene will be displayed.
     * @param user The newly created user.
     */
    private void showUserDetailsPage(Stage primaryStage, User user) {
        // Input fields for full name and email address
        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Enter Full Name");
        fullNameField.setMaxWidth(250);

        TextField emailField = new TextField();
        emailField.setPromptText("Enter Email Address");
        emailField.setMaxWidth(250);

        // Label to display error messages for invalid full name or email input
        Label errorDetailsLabel = new Label();
        errorDetailsLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button submitDetailsButton = new Button("Submit Details");

        submitDetailsButton.setOnAction(e -> {
            // Retrieve the full name and email address
            String fullName = fullNameField.getText();
            String email = emailField.getText();

            // Simple validation for full name and email
            if (fullName.isEmpty() || email.isEmpty()) {
                errorDetailsLabel.setText("Both full name and email are required.");
                return;
            }

           

            try {
                // Update the user's details in the database
                user.setFullName(fullName);
                user.setEmail(email);
                databaseHelper.updateUserDetails(user);

                // Navigate to the next page (e.g., WelcomeLoginPage or AdminHomePage)
                new WelcomeLoginPage(databaseHelper).show(primaryStage, user);

            } catch (SQLException ex) {
                System.err.println("Database error: " + ex.getMessage());
                ex.printStackTrace();
                errorDetailsLabel.setText("Error saving details.");
            }
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        // Add fields for full name, email, error message, and submit button
        layout.getChildren().addAll(fullNameField, emailField, submitDetailsButton, errorDetailsLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Complete Your Profile");
        primaryStage.show();
    }
}