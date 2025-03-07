package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.sql.SQLException;

import databasePart1.DatabaseHelper;

/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or quit the application.
 */
public class WelcomeLoginPage {
	
    private final DatabaseHelper databaseHelper;

    public WelcomeLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    /**
     * Displays the welcome page.
     * @param primaryStage the primary stage
     * @param user the currently authenticated user
     */
    public void show(Stage primaryStage, User user) {
    	
        VBox layout = new VBox(5);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
    	
        Label welcomeLabel = new Label("Welcome, " + user.getUserName() + "!!");
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    	
        // Button to navigate to the user's respective page based on their role.
        Button continueButton = new Button("Continue to your Page");
        continueButton.setOnAction(a -> {
            String username = user.getUserName();
            String role = user.getRole();
            System.out.println("User role: " + role);
            
            try {
                // Optionally, assign the user role in the database (if needed).
                databaseHelper.assignUserRole(username, role);
                
                if ("admin".equalsIgnoreCase(role)) {
                    new AdminHomePage().show(primaryStage, databaseHelper);
                } else if ("user".equalsIgnoreCase(role)) {
                    new UserHomePage().show(primaryStage, databaseHelper, user);
                } else {
                    // If additional roles are implemented, handle them here.
                    System.err.println("Unknown role: " + role);
                }
            } catch (SQLException e) {
                System.err.println("Database Error: " + e.getMessage());
                e.printStackTrace();
            }
        });
    	
        // Button to quit the application.
        Button quitButton = new Button("Quit");
        quitButton.setOnAction(a -> {
            databaseHelper.closeConnection();
            Platform.exit();
        });
    	
        layout.getChildren().addAll(welcomeLabel, continueButton, quitButton);
        Scene welcomeScene = new Scene(layout, 800, 400);
    	
        primaryStage.setScene(welcomeScene);
        primaryStage.setTitle("Welcome Page");
    }
}
