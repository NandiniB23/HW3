package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.util.Random;


public class AdminHomePage {

    private final DatabaseHelper databaseHelper;
    private final User loggedInUser;

    public AdminHomePage(DatabaseHelper databaseHelper, User loggedInUser) {
        this.databaseHelper = databaseHelper;
        this.loggedInUser = loggedInUser;
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(20);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        layout.setPrefWidth(800);

        Label adminLabel = new Label("Hello, Admin " + loggedInUser.getUserName() + "!");
        adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Add the "Logout" button
        Button logOutButton = new Button("Logout");
        logOutButton.setOnAction(a -> {
            new WelcomeLoginPage(databaseHelper).show(primaryStage, loggedInUser);								//LOGOUT BUTTON
        });
        
     // Add a "Reset Password" button for OTP generation
        Button resetPasswordButton = new Button("Reset User Password");
        resetPasswordButton.setOnAction(e -> showResetPasswordPage(primaryStage));
        layout.getChildren().add(resetPasswordButton);


        // Button to manage users (view, delete, and change roles)
        Button manageUsersButton = new Button("Manage Users");
        manageUsersButton.setOnAction(e -> showManageUsersPage(primaryStage));

        layout.getChildren().addAll(adminLabel, manageUsersButton, logOutButton);
        

        Scene adminScene = new Scene(layout, 800, 400);
        primaryStage.setScene(adminScene);
        primaryStage.setTitle("Admin Page");
        primaryStage.show();
    }

    private void showManageUsersPage(Stage primaryStage) {
        VBox layout = new VBox(20);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        // Fetch all users from the database (including their full info)
        List<User> users = databaseHelper.getUserInfo();

        // Display a list of all users with options to change roles and delete users
        for (User user : users) {
            // Skip the current logged-in admin's own role modification and deletion
            if (user.getUserName().equals(this.loggedInUser.getUserName())) {
                continue; // Do not allow the current admin to change their own role or delete
            }

            // Create a GridPane to display user info neatly
            GridPane userInfoGrid = new GridPane();
            userInfoGrid.setVgap(10);
            userInfoGrid.setHgap(20);
            userInfoGrid.setStyle("-fx-padding: 10;");
            userInfoGrid.setAlignment(Pos.CENTER_LEFT);

            // Displaying User Info in the grid
            Label userNameLabel = new Label("Username: ");
            Label userNameValue = new Label(user.getUserName());
            Label fullNameLabel = new Label("Full Name: ");
            Label fullNameValue = new Label(user.getFullName());
            Label emailLabel = new Label("Email: ");
            Label emailValue = new Label(user.getEmail());
            Label roleLabel = new Label("Role: ");
            Label roleValue = new Label(user.getRole());

            // Add labels to the GridPane
            userInfoGrid.add(userNameLabel, 0, 0);
            userInfoGrid.add(userNameValue, 1, 0);
            userInfoGrid.add(fullNameLabel, 0, 1);
            userInfoGrid.add(fullNameValue, 1, 1);
            userInfoGrid.add(emailLabel, 0, 2);
            userInfoGrid.add(emailValue, 1, 2);
            userInfoGrid.add(roleLabel, 0, 3);
            userInfoGrid.add(roleValue, 1, 3);

            // Role selection ComboBox (only "admin" and "user")
            ComboBox<String> roleComboBox = new ComboBox<>();
            roleComboBox.getItems().addAll("admin", "user", "student"); // Added "student"
            roleComboBox.setValue(user.getRole()); // Set the current role of the user
            
            // Button to Change User Role
            Button changeRoleButton = new Button("Change Role");
            changeRoleButton.setOnAction(e -> {
                String newRole = roleComboBox.getValue();
                
                // Ensure there’s at least one admin
                if (newRole.equals("admin") && !hasAtLeastOneAdmin()) {
                    showAlert("Error", "There must always be at least one admin!");
                    return;
                }
                
                // Prevent the admin from removing their own role
                if (user.getUserName().equals(this.loggedInUser.getUserName()) && newRole.equals("user")) {
                    showAlert("Error", "You cannot remove the 'admin' role from your own account.");
                    return;
                }
                
                // Proceed with changing the role
                try {
                    databaseHelper.changeUserRole(user.getUserName(), newRole);
                    showAlert("Success", "Role updated successfully!");
                } catch (SQLException ex) {
                    showAlert("Error", "Failed to update role.");
                }
            });

            // Button to Delete User
            Button deleteUserButton = new Button("Delete User");												//DELETE USER ADMIN COMMAND
            deleteUserButton.setOnAction(event -> {
                // Deny deleting the admin user
                if (databaseHelper.isAdminUser(user.getUserName())) {
                    showAlert("Error", "Admin accounts cannot be deleted!");
                    return;
                }

                // Show confirmation dialog for deletion
                Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);									//DELETE USER ADMIN COMMAND BUTTON
                confirmationAlert.setTitle("Confirm Deletion");
                confirmationAlert.setHeaderText("Are you sure you want to delete user '" + user.getUserName() + "'?");		//ARE YOU SURE NOTIFY
                confirmationAlert.setContentText("This action cannot be undone.");
                
                Optional<ButtonType> result = confirmationAlert.showAndWait();
                
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    boolean success = databaseHelper.deleteUser(user.getUserName());
                    if (success) {
                        showAlert("Success", "User '" + user.getUserName() + "' deleted successfully.");			
                    } else {
                        showAlert("Error", "User '" + user.getUserName() + "' not found.");
                    }
                } else {
                    showAlert("Cancelled", "User deletion canceled.");
                }
            });

            // Add the user info grid, role changing ComboBox, and delete button to the layout
            VBox userActionBox = new VBox(10);
            userActionBox.getChildren().addAll(userInfoGrid, roleComboBox, changeRoleButton, deleteUserButton);
            layout.getChildren().add(userActionBox);
        }

        // Back button to return to the admin page
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> show(primaryStage));

        layout.getChildren().add(backButton);

        Scene manageUsersScene = new Scene(layout, 800, 600);
        primaryStage.setScene(manageUsersScene);
        primaryStage.setTitle("Manage Users");
    }

    // Check if there's at least one admin in the system
    private boolean hasAtLeastOneAdmin() {
        try {
            List<User> users = databaseHelper.getAllUsers();
            for (User user : users) {
                if ("admin".equals(user.getRole())) {
                    return true;
                }
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to check admins.");
        }
        return false;
    }

    // Utility method to show an alert with a given title and message
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showResetPasswordPage(Stage primaryStage) {
        VBox layout = new VBox(20);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        // Prompt the admin to input the username for OTP reset
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Username");
        
        Button generateOtpButton = new Button("Generate OTP");
        generateOtpButton.setOnAction(e -> {
            String userName = userNameField.getText();
            try {
                // Generate the OTP
                String oneTimePassword = generateOneTimePassword();

                // Store OTP in the database
                boolean success = databaseHelper.setOneTimePassword(userName, oneTimePassword);
                if (success) {
                    // Send OTP to the user (here, we just print it for now)
                    System.out.println("One-time password for user " + userName + ": " + oneTimePassword);

                    // Show confirmation to the admin
                    showAlert("Success", "One-time password generated and sent to user " + userName);
                } else {
                    showAlert("Error", "Failed to generate one-time password. User not found.");
                }
            } catch (Exception ex) {
                showAlert("Error", "An error occurred: " + ex.getMessage());
            }
        });
        
        layout.getChildren().addAll(userNameField, generateOtpButton);

        Scene resetPasswordScene = new Scene(layout, 400, 200);
        primaryStage.setScene(resetPasswordScene);
        primaryStage.setTitle("Reset Password");
    }

    private String generateOneTimePassword() {
        int length = 8;  // You can customize the length
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            password.append(characters.charAt(index));
        }
        return password.toString();
    }


    
    
}