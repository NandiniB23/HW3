package application;


import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * The SetupAdmin class handles the setup process for creating an administrator account.
 * This is intended to be used by the first user to initialize the system with admin credentials.
 */
public class AdminSetupPage {
	
    private final DatabaseHelper databaseHelper;

    public AdminSetupPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	// Input fields for userName and password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Admin userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);

        
        
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
            
            // Stores the checking of a valid userName
            String userValidation = UserNameRecognizer.checkForValidUserName(userName);
            System.out.println(userValidation);
            errorUserNameLabel.setText(userValidation);
            
            // Checks to see if password is valid
            String passwordValidation = PasswordEvaluator.evaluatePassword(password);
            System.out.println(passwordValidation);
            errorPasswordLabel.setText(passwordValidation);
            
            // Returns an error message if either userName and password is empty
            if (userName.trim().isEmpty() || password.trim().isEmpty()) {
            	return;
            }
            
          
            
            // Returns an error message if the password or userName is invalid
            if (!passwordValidation.trim().isEmpty()|| (!userValidation.trim().isEmpty())) {
            	return;
            }
            
           
            
            
            try {
            	// Create a new User object with admin role and register in the database
            	User user=new User(userName, password, "admin");
                databaseHelper.register(user);
                System.out.println("Administrator setup completed.");
                
                // Navigates to  Setup Login Selection Page screen so the first user (Admin) can log in again
                new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        // Removed parameters in this order: userNameField, passwordField, setupButton
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        
        // Gathers the userName, password, login button and error labels for general and userName/password validation errors
        layout.getChildren().addAll(userNameField, passwordField, setupButton, errorUserNameLabel, errorPasswordLabel);


        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Administrator Setup");
        primaryStage.show();
    }
}