package application;
import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;

/**
 * The ViewAdminActionList class represents the interface for displaying the list of available admin actions for
 * the current user and redirecting to those actions.
 * 
 * @author Kylie Kim
 * @version 2.0 4/19/2025
 */
public class ViewAdminActionList {
	
	private DatabaseHelper databaseHelper;
	private User user;
	
	/**
	 * Constructor used to create new instance of ViewAdminActionList
	 * @param databaseHelper object for accessing database
	 * @param user current user operating in system
	 */
	public ViewAdminActionList(DatabaseHelper databaseHelper, User user) {
		this.databaseHelper = databaseHelper;
		this.user = user;
	}
	
	/**
     * Displays the admin action list interface.
     * 
     * @param primaryStage the primary stage where the scene is displayed
     * @param user the current user
     */
    public void show(Stage primaryStage, User user) {
        this.user = user;
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-alignment: center;");

        Label title = new Label("Admin Action Requests");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // New Request Form (Only visible to Instructors)
        VBox requestForm = new VBox(10);
        if (user.getRole().equals("Instructor")) {
            Label formTitle = new Label("Submit New Admin Request");
            TextField descriptionField = new TextField();
            descriptionField.setPromptText("Description");
            ComboBox<String> actionTypeDropdown = new ComboBox<>();
            actionTypeDropdown.getItems().addAll("Request New Role", "Password Reset", "New User Invite", "User Deletion", "Role Modification");
            actionTypeDropdown.setPromptText("Select Action Type");
            TextField priorityField = new TextField();
            priorityField.setPromptText("Priority or Deadline");
            Button submitButton = new Button("Submit Request");
            Label submissionStatus = new Label("");
            submitButton.setOnAction(e -> {
                String desc = descriptionField.getText();
                String action = actionTypeDropdown.getValue();
                String priority = priorityField.getText();
                if (desc.isEmpty() || action == null || priority.isEmpty()) {
                    submissionStatus.setText("All fields are required.");
                    return;
                }
                databaseHelper.submitAdminRequest(user, desc, action, priority);
                submissionStatus.setText("Request submitted successfully.");
            });
            requestForm.getChildren().addAll(formTitle, descriptionField, actionTypeDropdown, priorityField, submitButton, submissionStatus);
        }

        // Open Requests
        Label openLabel = new Label("Open Requests");
        ListView<String> openRequests = new ListView<>();
        ObservableList<String> openList = FXCollections.observableArrayList(databaseHelper.getOpenAdminRequests(user));
        openRequests.setItems(openList);

        // Closed Requests
        Label closedLabel = new Label("Closed Requests");
        ListView<String> closedRequests = new ListView<>();
        ObservableList<String> closedList = FXCollections.observableArrayList(databaseHelper.getClosedAdminRequests(user));
        closedRequests.setItems(closedList);

        // Reopen functionality (Instructor only)
        if (user.getRole().equals("Instructor")) {
            Button reopenButton = new Button("Reopen Selected Request");
            TextField updatedDescription = new TextField();
            updatedDescription.setPromptText("New Description");
            Label reopenStatus = new Label("");
            reopenButton.setOnAction(e -> {
                String selected = closedRequests.getSelectionModel().getSelectedItem();
                String newDesc = updatedDescription.getText();
                if (selected != null && !newDesc.isEmpty()) {
                    databaseHelper.reopenAdminRequest(user, selected, newDesc);
                    reopenStatus.setText("Request reopened successfully.");
                } else {
                    reopenStatus.setText("Select a request and enter a new description.");
                }
            });
            layout.getChildren().addAll(reopenButton, updatedDescription, reopenStatus);
        }

        // Admin functionality to close a request (only for open requests)
        if (user.getRole().equals("Admin")) {
            Button closeButton = new Button("Close Selected Open Request");
            Label closeStatus = new Label("");
            closeButton.setOnAction(e -> {
                String selected = openRequests.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    databaseHelper.closeAdminRequest(user, selected);
                    closeStatus.setText("Request closed successfully.");
                } else {
                    closeStatus.setText("Select an open request to close.");
                }
            });
            layout.getChildren().addAll(closeButton, closeStatus);
        }

        // Navigation
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            if (user.getRole().equals("Instructor")) {
                new InstructorHomePage(databaseHelper, user).show(primaryStage, user);
            } else if (user.getRole().equals("Admin")) {
                new AdminHomePage(databaseHelper, user).show(primaryStage, user);
            } else {
                new StaffHomePage(databaseHelper, user).show(primaryStage, user);
            }
        });

        layout.getChildren().addAll(title, requestForm, openLabel, openRequests, closedLabel, closedRequests, backButton);
        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Admin Action Requests");
    }
	
	/**
     * Redirects user to the confirmLogout scene, continues to logout if confirmed
     * 
     * @param primaryStage  The primary stage where the scene will be displayed
     */
    private void confirmLogout(Stage primaryStage) {
    	Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("Logout Confirmation");
    	alert.setHeaderText("Please confirm if you're logging out.");
    	alert.setContentText("Make sure all changes are saved.");
    	
    	ButtonType saveAndLogout = new ButtonType("Save and Logout");
    	ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    	
    	alert.getButtonTypes().setAll(saveAndLogout, cancel);
    	
    	alert.showAndWait().ifPresent(response -> {
    		if (response == saveAndLogout) {
    			logout(primaryStage);
    		}
    	});
    }
    
    /**
     * Redirects user to back to UserLoginPage.
     * 
     * @param primaryStage The primary stage where the scene will be displayed
     */
	private void logout(Stage primaryStage) {
		UserLoginPage loginPage = new UserLoginPage(databaseHelper);
    	loginPage.show(primaryStage, this.user);
	}
}