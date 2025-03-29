package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserLoginPage {

    private final DatabaseHelper databaseHelper;

    public UserLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Username");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        // Buttons
        Button studentLoginButton = new Button("Student Login");
        Button adminLoginButton = new Button("Admin Login");
        Button userLoginButton = new Button("User Login");
        Button backButton = new Button("Back");
        Button forgotPasswordButton = new Button("Forgot Password?");

        // Student login action
        studentLoginButton.setOnAction(e -> {
            String userName = userNameField.getText();
            String password = passwordField.getText();
            try {
                String role = databaseHelper.getUserRole(userName);
                if ("student".equals(role) && databaseHelper.login(new User(userName, password, role))) {
                    new StudentHomePage(databaseHelper, new User(userName, password, role)).show(primaryStage);
                } else {
                    errorLabel.setText("Invalid student credentials.");
                }
            } catch (Exception ex) {
                errorLabel.setText("Error: " + ex.getMessage());
            }
        });

        // Admin login action
        adminLoginButton.setOnAction(e -> {
            String userName = userNameField.getText();
            String password = passwordField.getText();
            try {
                String role = databaseHelper.getUserRole(userName);
                if ("admin".equals(role) && databaseHelper.login(new User(userName, password, role))) {
                    new AdminHomePage(databaseHelper, new User(userName, password, role)).show(primaryStage);
                } else {
                    errorLabel.setText("Invalid admin credentials.");
                }
            } catch (Exception ex) {
                errorLabel.setText("Error: " + ex.getMessage());
            }
        });

        // User login action
        userLoginButton.setOnAction(e -> {
            String userName = userNameField.getText();
            String password = passwordField.getText();
            try {
                String role = databaseHelper.getUserRole(userName);
                if ("user".equals(role) && databaseHelper.login(new User(userName, password, role))) {
                    new UserHomePage(databaseHelper, new User(userName, password, role)).show(primaryStage);
                } else {
                    errorLabel.setText("Invalid user credentials.");
                }
            } catch (Exception ex) {
                errorLabel.setText("Error: " + ex.getMessage());
            }
        });

        // Back button action
        backButton.setOnAction(e -> new SetupLoginSelectionPage(databaseHelper).show(primaryStage));

        // Forgot Password button action
        forgotPasswordButton.setOnAction(e -> showForgotPasswordPage(primaryStage));

        // Layout
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, passwordField, studentLoginButton, adminLoginButton, userLoginButton, forgotPasswordButton, backButton, errorLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("User Login");
        primaryStage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String generateOneTimePassword() {
        // Generate a random 6-digit OTP
        int otp = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(otp);
    }

    private void showForgotPasswordPage(Stage primaryStage) {
        // Forgot Password page layout
        VBox layout = new VBox(20);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Label to inform the user
        Label infoLabel = new Label("Enter your username to reset your password:");
        
        // TextField for entering the username (or email)
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your Username");
        
        // Button to generate OTP
        Button generateOtpButton = new Button("Generate OTP");
        generateOtpButton.setOnAction(e -> {
            String userName = usernameField.getText();
            try {
                // Check if user exists
                String role = databaseHelper.getUserRole(userName);
                if (role == null) {
                    showAlert("Error", "User not found!");
                    return;
                }

                // Generate and store OTP
                String oneTimePassword = generateOneTimePassword();
                boolean success = databaseHelper.setOneTimePassword(userName, oneTimePassword);
                if (success) {
                    System.out.println("Generated OTP for " + userName + ": " + oneTimePassword);
                    showAlert("Success", "An OTP has been sent to your registered email address.");
                    showOtpVerificationPage(primaryStage, userName);
                } else {
                    showAlert("Error", "Failed to generate OTP. Please try again.");
                }
            } catch (Exception ex) {
                showAlert("Error", "An error occurred: " + ex.getMessage());
            }
        });

        // Add components to layout
        layout.getChildren().addAll(infoLabel, usernameField, generateOtpButton);

        Scene forgotPasswordScene = new Scene(layout, 400, 250);
        primaryStage.setScene(forgotPasswordScene);
        primaryStage.setTitle("Forgot Password");
    }

    private void showOtpVerificationPage(Stage primaryStage, String userName) {
        // OTP verification page layout
        VBox layout = new VBox(20);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        Label otpLabel = new Label("Enter the OTP sent to your email:");
        TextField otpField = new TextField();
        otpField.setPromptText("Enter OTP");

        Button verifyOtpButton = new Button("Verify OTP");
        verifyOtpButton.setOnAction(e -> {
            String enteredOtp = otpField.getText();
            try {
                String storedOtp = databaseHelper.getOneTimePassword(userName);

                if (storedOtp != null && storedOtp.equals(enteredOtp)) {
                    showPasswordResetPage(primaryStage, userName);
                } else {
                    showAlert("Error", "Invalid OTP. Please try again.");
                }
            } catch (Exception ex) {
                showAlert("Error", "An error occurred: " + ex.getMessage());
            }
        });

        layout.getChildren().addAll(otpLabel, otpField, verifyOtpButton);

        Scene otpVerificationScene = new Scene(layout, 400, 250);
        primaryStage.setScene(otpVerificationScene);
        primaryStage.setTitle("Verify OTP");
    }

    private void showPasswordResetPage(Stage primaryStage, String userName) {
        // Password reset page layout
        VBox layout = new VBox(20);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label newPasswordLabel = new Label("Enter your new password:");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");

        Button resetPasswordButton = new Button("Reset Password");
        resetPasswordButton.setOnAction(e -> {
            String newPassword = newPasswordField.getText();
            try {
                boolean success = databaseHelper.updatePassword(userName, newPassword);
                if (success) {
                    databaseHelper.clearOneTimePassword(userName);
                    showAlert("Success", "Your password has been reset successfully.");
                    new UserLoginPage(databaseHelper).show(primaryStage);
                } else {
                    showAlert("Error", "Failed to reset password. Please try again.");
                }
            } catch (Exception ex) {
                showAlert("Error", "An error occurred: " + ex.getMessage());
            }
        });

        layout.getChildren().addAll(newPasswordLabel, newPasswordField, resetPasswordButton);

        Scene passwordResetScene = new Scene(layout, 400, 250);
        primaryStage.setScene(passwordResetScene);
        primaryStage.setTitle("Reset Password");
    }
}