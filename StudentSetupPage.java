package application;

import java.sql.SQLException;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StudentSetupPage {
	 private final DatabaseHelper databaseHelper;

	    public StudentSetupPage(DatabaseHelper databaseHelper) {
	        this.databaseHelper = databaseHelper;
	    }

	    public void show(Stage primaryStage) {
	    	// Input fields for userName and password
	        TextField userNameField = new TextField();
	        userNameField.setPromptText("Enter Student userName");
	        userNameField.setMaxWidth(250);

	        PasswordField passwordField = new PasswordField();
	        passwordField.setPromptText("Enter Password");
	        passwordField.setMaxWidth(250);

	        Button setupButton = new Button("Setup");
	        
	        setupButton.setOnAction(a -> {
	        	// Retrieve user input
	            String userName = userNameField.getText();
	            String password = passwordField.getText();
	            try {
	            	// Create a new User object with student role and register in the database
	            	User user=new User(userName, password, "admin");
	                databaseHelper.register(user);
	                System.out.println("Student setup completed.");
	                
	                // Navigate to the Welcome Login Page
	                new WelcomeLoginPage(databaseHelper).show(primaryStage,user);
	            } catch (SQLException e) {
	                System.err.println("Database error: " + e.getMessage());
	                e.printStackTrace();
	            }
	        });

	        VBox layout = new VBox(10, userNameField, passwordField, setupButton);
	        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

	        primaryStage.setScene(new Scene(layout, 800, 400));
	        primaryStage.setTitle("Student Setup");
	        primaryStage.show();
	    }
}