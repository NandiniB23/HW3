package application;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

public class UserHomePage {
    
    private final DatabaseHelper databaseHelper;
    private final User user; // Store the user object
    
    // Constructor that accepts DatabaseHelper and User
    public UserHomePage(DatabaseHelper databaseHelper, User user) {
        this.databaseHelper = databaseHelper;
        this.user = user;  // Store the user
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(5);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        // Display a welcome message with the user's username
        Label userLabel = new Label("Welcome, " + user.getUserName() + "!");
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Add more functionality for User Page here

        layout.getChildren().add(userLabel);

        Scene userScene = new Scene(layout, 800, 400);
        primaryStage.setScene(userScene);
        primaryStage.setTitle("User Page");
    }
}