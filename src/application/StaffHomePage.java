package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The {@code StaffHomePage} class represents the home screen for a logged-in Staff user.
 * <p>
 * It provides a welcome message and a logout button. Additional functionality for staff
 * users can be integrated here in future phases.
 * </p>
 * 
 * @author Kylie Kim
 * @version 1.0
 * @since 2025-03-27
 * 
 * @author Cristina Hooe
 * @version 2.0 4/10/2025
 */
public class StaffHomePage {

	/** Database helper instance for interacting with the database. */
    private final DatabaseHelper databaseHelper;

    /** The currently logged-in user (staff). */
    private User user;

    /**
     * Constructs a {@code StaffHomePage} with the specified {@code DatabaseHelper}.
     * 
     * @param databaseHelper the database helper used for interactions
     * @param user the currently logged-in staff user
     */
    public StaffHomePage(DatabaseHelper databaseHelper, User user) {
        this.databaseHelper = databaseHelper;
        this.user = user;
    }
    
    /**
     * Displays the staff home page with a welcome message and logout option.
     * 
     * @param primaryStage the primary window of the JavaFX application
     * @param user the currently logged-in staff user
     */
    public void show(Stage primaryStage, User user) {
    	this.user = user;
    	VBox layout = new VBox(10);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display Hello student
	    Label staffLabel = new Label("Welcome Staff!");
	    staffLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // Create AdminActionList button
	    Button adminActionListButton = new Button("Admin Action List");
	    adminActionListButton.setOnAction(e -> openAdminActionList(primaryStage));
	    
	    // Create StaffPrivateMessages button
	    Button staffPrivateMessagesButton = new Button("View Private Messages");
	    staffPrivateMessagesButton.setOnAction(e -> openStaffPrivateMessages(primaryStage));
	    
	    // Create StaffStudentInteractionView button
	    Button staffStudentInteractionViewButton = new Button ("View Student and Reviewer Interactions");
	    staffStudentInteractionViewButton.setOnAction(e -> openStaffStudentInteractions(primaryStage));
	    
	    // Create UserAndPostActivityLog Button
	    Button activityLogButton = new Button("User and Post Log");
        activityLogButton.setOnAction(e -> {
        	openActivityLog(primaryStage);
        });
        
	    // Create logout button
	    Button logoutButton = new Button("Logout");
	    logoutButton.setOnAction(a -> confirmLogout(primaryStage));
	    layout.getChildren().addAll(staffLabel, adminActionListButton, staffPrivateMessagesButton, staffStudentInteractionViewButton, activityLogButton, logoutButton);
	    Scene staffScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(staffScene);
	    primaryStage.setTitle("Staff Page");
    }
    
    /**
     * Prompts the user with a confirmation dialog before logging out.
     * 
     * @param primaryStage the primary window of the application
     */
    private void confirmLogout(Stage primaryStage) {
    	Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
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
     * Redirects the user to {@code StaffStudentInteractionView}.
     * 
     * @param primaryStage the primary window of the application
     */
    private void openStaffStudentInteractions(Stage primaryStage) {
    	StaffStudentInteractionView staffStudentInteractions = new StaffStudentInteractionView(databaseHelper);
    	staffStudentInteractions.show(primaryStage, this.user);
    }
    
    /**
     * Redirects the user to {@code StaffPrivateMessages}.
     * 
     * @param primaryStage the primary window to update the scene
     */
    private void openStaffPrivateMessages(Stage primaryStage) {
    	StaffPrivateMessages staffPrivateMessage = new StaffPrivateMessages(databaseHelper);
    	staffPrivateMessage.show(primaryStage, this.user);
    }
    
    /**
     * Logs the user out and redirects them to the {@code ViewAdminActionList}.
     * 
     * @param primaryStage the primary window to update the scene
     */
    private void openAdminActionList(Stage primaryStage) {
    	ViewAdminActionList adminActionView = new ViewAdminActionList(databaseHelper, user);
    	adminActionView.show(primaryStage, this.user);
    }
    
    /**
     * Redirects user to UserAndPostActivityLog
     * 
     * @param primaryStage the primary window to update the scene
     */
    private void openActivityLog(Stage primaryStage) {
    	new UserAndPostActivityLog(databaseHelper, user).show(primaryStage, user);
    }
    
    /**
     * Navigates to the "Request New Role" page for the currently logged-in user.
     * <p>
     * This method instantiates a new {@link RequestNewRole} controller using the
     * shared {@code databaseHelper}, and then displays its UI on the given
     * {@code primaryStage}, passing along the current {@code user}.
     * </p>
     *
     * @param primaryStage the primary JavaFX {@link Stage} on which to display
     *                     the RequestNewRole scene
     */
    private void openRoleRequestPage(Stage primaryStage) {
        RequestNewRole openRoleRequestPage = new RequestNewRole(databaseHelper);
        openRoleRequestPage.show(primaryStage, user);
    }
    
    /**
     * Logs the user out and redirects them to the {@code UserLoginPage}.
     * 
     * @param primaryStage the primary window to update the scene
     */
    private void logout(Stage primaryStage) {
    	UserLoginPage loginPage = new UserLoginPage(databaseHelper);
    	loginPage.show(primaryStage, this.user);
    }
}