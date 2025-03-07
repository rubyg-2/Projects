package application;

import java.sql.SQLException;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.util.Optional;
/**
 * AdminPage class represents the user interface for the admin user.
 * This page displays a simple welcome message for the admin.
 */

public class AdminHomePage {
	/**
     * Displays the admin page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     * @param databaseHelper to help with database operations
     */
    public void show(Stage primaryStage, DatabaseHelper databaseHelper) {
    	VBox layout = new VBox();
    	
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // label to display the welcome message for the admin
	    Label adminLabel = new Label("Hello, Admin!");
	    
	    adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	    Scene adminScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(adminScene);
	    primaryStage.setTitle("Admin Page");
	    
	    //RUBY'S CODE
	    Button logoutButton = new Button("Logout"); //adding logout feature
	    logoutButton.setStyle("-fx-font-size: 14px;  -fx-padding: 10;");
	    
	    logoutButton.setOnAction(a -> {
	    	//redirect admin to the SetupLogin page w/ database helper
	    	SetupLoginSelectionPage setupLoginPage = new SetupLoginSelectionPage(databaseHelper);
	    	setupLoginPage.show(primaryStage);
	    });
	    
	    Label assignRoleLabel = new Label("Assign Role to User");
	    assignRoleLabel.setStyle("-fx-font-size: 14px;  -fx-font-weight: bold;");
	    
	    //input box to get username
	    TextField usernameField = new TextField();
	    usernameField.setPromptText("Enter username: ");
	    usernameField.setMaxWidth(250); //adjust acording to scale of panel
	    
	    //select role(S) opt
	    ListView<CheckBox> roleListView = new ListView<>();
	    CheckBox adminRole = new CheckBox("Admin");
	    CheckBox studentRole = new CheckBox("Student");
	    CheckBox instructorRole = new CheckBox("Instructor");
	    CheckBox staffRole = new CheckBox("Staff");
	    CheckBox reviewerRole = new CheckBox("Reviewer");
	    
	    //add checkbox so readable to user multi select
	    
	    roleListView.getItems().addAll(adminRole, studentRole,instructorRole, staffRole, reviewerRole );
	    roleListView.setPrefHeight(122);//change to 120 adjust
	    
	    ///button to assign roles selected by admin
	    Button assignRoleButton = new Button("Assign");
	    assignRoleButton.setOnAction(e ->{
	    	String username = usernameField.getText();
	    	if(username != null && !username.isEmpty()) {
	    		
	    		try {
	    			//check if user even exist in first place before trying to assign role
	    			if(!databaseHelper.doesUserExist(username)) {
	    				Alert userNotFound = new Alert(Alert.AlertType.ERROR);
	    				userNotFound.setTitle("User does not exist!");
	    				userNotFound.setContentText("The user \"" + username + "\" , cannot be found in database");
	    				userNotFound.showAndWait();
	    				return;
	    			
	    			
	    		}
	    		StringBuilder selectedRoles = new StringBuilder();	    		//GET ROLES ADMIN SELECTED

	    		if(adminRole.isSelected()) selectedRoles.append("admin, ");
	    		if(studentRole.isSelected()) selectedRoles.append("student, ");
	    		if(instructorRole.isSelected()) selectedRoles.append("instructor, ");
	    		if(staffRole.isSelected()) selectedRoles.append("staff, ");
	    		if(reviewerRole.isSelected()) selectedRoles.append("reviewer, ");
	    	
	    	
	    	if(selectedRoles.length() > 0) {
	    		selectedRoles.setLength(selectedRoles.length() - 2);	    		
	    		//when only 1 role is chosen we shorten display message commma remains if more than 1 selected
				databaseHelper.assignUserRole(username, selectedRoles.toString());
		    	//assign role to database
	    	
						//success good
					Alert roleAssignedAlert = new Alert(Alert.AlertType.INFORMATION);
					roleAssignedAlert.setContentText("Roles " + selectedRoles + " assigned to " + username);
					roleAssignedAlert.showAndWait();
				} else{
					Alert noRole = new Alert(Alert.AlertType.WARNING);
					noRole.setContentText("No role selected. Please select role to add");
					noRole.showAndWait();
				}
	    		}catch (SQLException e1) {
	    	
					System.err.println("Error while assigning roles to user: " + username);
			        e1.printStackTrace();  // You can log this to a file or database if needed

			        // Show an error alert to the admin
			        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
			        errorAlert.setContentText("There was an error assigning roles to " + username + ". Please try again.");
			        errorAlert.showAndWait();
					
				}
	    	
	    	}else {
	    		Alert userNotEnteralert = new Alert(Alert.AlertType.WARNING);
	    		userNotEnteralert.setContentText("Enter username.");
	    		userNotEnteralert.showAndWait()	;
	    		}
	    	
	    
	    });
	    
	    //invite code button which will redirect user
	   Button inviteButton = new Button("Invite Code");
	   inviteButton.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
	   inviteButton.setOnAction(e -> {
		   InvitationPage invitationPage = new InvitationPage();
		   invitationPage.show(databaseHelper, primaryStage);
	   });
	    
	    //RUBYS CODE

	   Region spacer = new Region();
	   VBox.setVgrow(spacer, Priority.ALWAYS);
	    
	   
	   //Brandon's Code
	    //Button to see all the users in the system
	    TableView<Rows> tableView = new TableView<>();
	    
	    TableColumn<Rows, String> userNameCol = new TableColumn<>("Username");
        userNameCol.setCellValueFactory(cellData -> cellData.getValue().column1Property());
        
        TableColumn<Rows, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(cellData -> cellData.getValue().column2Property());
        
        TableColumn<Rows, String> oneTimePassword = new TableColumn<>("One Time Password");
        //oneTimePassword.setCellValueFactory(cellData -> cellData.getValue().column3Property());
        
        tableView.getColumns().addAll(userNameCol, roleCol);
        
	    Button seeAllUsers = new Button("See All Users");
	    seeAllUsers.setOnAction(a -> {
	    ObservableList<String> data;
		try {
			data = databaseHelper.displayUsers();
			ObservableList<Rows> tableData = convertToPairs(data);
			tableView.setItems(tableData);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		});
	    
	    //Button to see one time Password
	    /*
	    Button OTP = new Button("See One Time Passwords");
	    OTP.setOnAction(a -> {
    		String data = databaseHelper.getOTP(usernameField.getText()); 
    		ObservableList<String> data1 = new FXCollections.observableArrayList(); 
    		data1.add(data); 
    		ObervableList<Rows> tableData = convertToPairs(new FXCollections.observableArrayList(data));
	    });
	    */
	    
	     
	    
	    //Button to get the function going
	    Button deleteUser = new Button("Delete User");
	    deleteUser.setOnAction(a -> {
	    	Alert alert = new Alert(AlertType.CONFIRMATION);
	    	alert.setTitle("Confirmation");
	    	alert.setHeaderText("Do you wish to continue?");
	    	alert.setContentText("Choose an Option");
	    	
	    	Optional<ButtonType> result = alert.showAndWait();
	    	if(result.isPresent() && result.get() == ButtonType.OK) {
	    		String username = usernameField.getText();
			    try {
			    	
					int deleteUserResult = databaseHelper.deleteUser(username);
					if(deleteUserResult == -1) {
						Alert failed = new Alert(AlertType.ERROR);
						failed.setTitle("Failed");
						failed.setHeaderText("No such user exists");
					}
					else if(deleteUserResult == 1) {
						Alert success = new Alert(AlertType.INFORMATION);
						success.setTitle("Success");
						success.setHeaderText("User successfully deleted");
					}
					else {
						Alert failed2 = new Alert(AlertType.ERROR);
						failed2.setTitle("Failed");
						failed2.setHeaderText("Unknown Error");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    	
	    });
	    layout.getChildren().addAll(adminLabel,usernameField,deleteUser,tableView,seeAllUsers,inviteButton,
	    		assignRoleLabel, roleListView, assignRoleButton, spacer, logoutButton);
    }
    //method that converts the fetched data and turns it into pairs to display
    private ObservableList<Rows> convertToPairs(ObservableList<String> list) {
        List<Rows> pairs = new ArrayList<>();
        for (int i = 0; i < list.size(); i += 2) {
            String first = list.get(i);
            String second = (i + 1 < list.size()) ? list.get(i + 1) : ""; // Handle odd size
            pairs.add(new Rows(first, second));
        }
        return FXCollections.observableArrayList(pairs);
    }
    //Class for the rows displayed for the username and roles
    private static class Rows{
    	private final SimpleStringProperty column1;
        private final SimpleStringProperty column2;
        //private final SimpleStringProperty column3;

        public Rows(String column1, String column2) {
        	this.column1 = new SimpleStringProperty(column1);
        	this.column2 = new SimpleStringProperty(column2);
        }
        public Rows(String column1, String column2, String column3) {
            this.column1 = new SimpleStringProperty(column1);
            this.column2 = new SimpleStringProperty(column2);
            //this.column3 = new SimpleStringProperty(column3);
        }

        public String getColumn1() { return column1.get(); }
        public String getColumn2() { return column2.get(); }
        //public String getColumn3() { return column3.get(); }
        
        public void setColumn1(String value) { column1.set(value); }
        public void setColumn2(String value) { column2.set(value); }
        //public void setColumn3(String value) { column3.set(value); }

        public StringProperty column1Property() { return column1; }
        public StringProperty column2Property() { return column2; }
        //public StringProperty column3Property() { return column3; }
    }
}