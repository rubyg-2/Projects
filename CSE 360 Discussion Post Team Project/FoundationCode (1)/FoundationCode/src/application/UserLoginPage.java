package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Background;
import application.UserNameRecognizer;
import javafx.scene.paint.Color;
/**
 * The UserLoginPage class provides a login interface for users to access their accounts.
 * It validates the user's credentials and navigates to the appropriate page upon successful login.
 */
public class UserLoginPage {
	
    private final DatabaseHelper databaseHelper;

    public UserLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	// Input field for the user's userName, password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        // Label to display error messages
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");


        Button useOTPButton = new Button("Use One-Time-Password");
        useOTPButton.setOnAction(a -> {
        	String userName = userNameField.getText();
        	String errUser = UserNameRecognizer.checkForValidUserName(userName);

        	if (databaseHelper.doesUserExist(userName)) {
        		if (errUser == "") {
        			errorLabel.setText(databaseHelper.generateOneTimePassword(userName));
        			passwordField.setText("");
        			passwordField.setPromptText("Enter One-Time-Password");
        			errorLabel.setText("");
        			useOTPButton.setBackground(new Background(new BackgroundFill(Color.CYAN, null, null)));

        		} else {
        			errorLabel.setText("This username is invalid because:\n" + errUser +
        			"\n" + userName.substring(0,UserNameRecognizer.userNameRecognizerIndexofError) + "\u21E6");
        		}
        	} else {
        		errorLabel.setText("user account doesn't exists");
        	}

        });
        
        Button loginButton = new Button("Login");
        
        loginButton.setOnAction(a -> {
			// Retrieve user inputs
			String userName = userNameField.getText();
			String password = passwordField.getText();
			String errUser = UserNameRecognizer.checkForValidUserName(userName);
			String errPassword = PasswordEvaluator.evaluatePassword(password);
			
			if (errUser == "") {
				errorLabel.setText("");
				
				//If we are using the normal password
				if (passwordField.getPromptText() == "Enter Password") {
					if (errPassword == "") {
						errorLabel.setText("");
						
						try {
							User user = new User(userName, password, "");
							WelcomeLoginPage welcomeLoginPage = new WelcomeLoginPage(databaseHelper);
					
							// Retrieve the user's role from the database using userName
							String role = databaseHelper.getUserRole(userName);

							if (role != null) {
								user.setRole(role);
								if (databaseHelper.login(user)) {
									welcomeLoginPage.show(primaryStage, user);
								} else {
									// Display an error if the login fails
									errorLabel.setText("Error logging in");
								}
							} else {
								// Display an error if the account does not exist
								errorLabel.setText("user account doesn't exists");
							}

						} catch (SQLException e) {
							System.err.println("Database error: " + e.getMessage());
							e.printStackTrace();
						}
					} else { //password is invalid
						errorLabel.setText("This password is invalid because:\n" + errPassword);
					}
				//If we are using a one-time-password
				} else if (passwordField.getPromptText() == "Enter One-Time-Password") {
					try {
						User user = new User(userName, password, "");
						WelcomeLoginPage welcomeLoginPage = new WelcomeLoginPage(databaseHelper);
				
						// Retrieve the user's role from the database using userName
						String role = databaseHelper.getUserRoleOTP(userName);

						if (role != null) {
							user.setRole(role);
							if (databaseHelper.loginOTP(user)) {
								databaseHelper.rmOTP(user);
								welcomeLoginPage.show(primaryStage, user);
							} else {
								// Display an error if the login fails
								errorLabel.setText("Error logging in");
							}
						} else {
							// Display an error if the account does not exist
							errorLabel.setText("user account doesn't exists");
						}
						databaseHelper.rmOTP(user);

					} catch (SQLException e) {
						System.err.println("Database error: " + e.getMessage());
						e.printStackTrace();
					}
				}
				
			} else {	//userName is invalid
				errorLabel.setText("This username is invalid because:\n" + errUser +
							userName.substring(0,UserNameRecognizer.userNameRecognizerIndexofError) + "\u21E6");
			}
			
		});

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, passwordField, loginButton, errorLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("User Login");
        primaryStage.show();
    }
}