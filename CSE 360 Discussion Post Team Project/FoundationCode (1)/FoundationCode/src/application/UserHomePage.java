package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// Import your User class and other pages
import application.User;
import application.qaGUI; 
import application.SetupLoginSelectionPage;

public class UserHomePage {

    /**
     * Displays the user's home page.
     * 
     * @param primaryStage   the main stage of the application
     * @param databaseHelper the database helper instance
     * @param user           the current logged-in user
     */
    public void show(Stage primaryStage, DatabaseHelper databaseHelper, User user) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        // Label to display greeting for the user
        Label userLabel = new Label("Hello, " + user.getUserName() + "!");
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Button to navigate to the Q&A page
        Button qaButton = new Button("Go to Q&A Page");
        qaButton.setOnAction(a -> {
            try {
                // Get the user id using the database helper.
                int userId = databaseHelper.getUserId(user.getUserName(), user.getPassword());
                if(userId == -1) {
                    System.err.println("Could not retrieve user id.");
                } else {
                    // Create the qaGUI instance with the logged-in user's id
                    qaGUI qaPage = new qaGUI(databaseHelper, userId);
                    qaPage.show(primaryStage, databaseHelper);
                }
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });
        
        // Logout button to return to the login page
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
        logoutButton.setOnAction(a -> {
            new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
        
        // Add all nodes to the layout
        layout.getChildren().addAll(userLabel, qaButton, logoutButton);
        
        Scene userScene = new Scene(layout, 800, 400);
        primaryStage.setScene(userScene);
        primaryStage.setTitle("User Page");
    }
}
