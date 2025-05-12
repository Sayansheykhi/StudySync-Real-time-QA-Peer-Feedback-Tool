package application;

import databasePart1.DatabaseHelper;
import application.InstructorStudentInteractionView;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.Map;

/**
 * The ComputeReviewersScorecard handles the interface responsible for displaying and calculating the scorecards
 * for reviewers within the forum question and answer system
 * 
 * @author Kylie Kim
 * @version 2.0 4/19/25
 */
public class ComputeReviewersScorecard {
	
	private DatabaseHelper databaseHelper;
	private User user;
	private ComboBox<String> reviewerDropdown;
    private Label scorecardLabel;
    private TextField manualEditField;
    private String preselectedReviewer;
	
	/**
	 * Constructor creates an instance of the ComputeReviewersScorecard interface
	 * 
	 * @param databaseHelper object for handling interacting with database
	 * @param user the current user in the system
	 */
	public ComputeReviewersScorecard(DatabaseHelper databaseHelper, User user) {
		this.databaseHelper =databaseHelper;
		this.user = user;
	}
	
	/**
	 * Constructs an instance of the ComputeReviewersScorecard view with an optional preselected reviewer.
	 * This allows the interface to automatically display the scorecard of the specified reviewer when loaded.
	 *
	 * @param databaseHelper the DatabaseHelper object used for interacting with the database
	 * @param user the current logged-in user (typically a student or instructor)
	 * @param preselectedReviewer the username of the reviewer whose scorecard should be automatically displayed; can be null or empty if none
	 */
	public ComputeReviewersScorecard(DatabaseHelper databaseHelper, User user, String preselectedReviewer) {
	    this.databaseHelper = databaseHelper;
	    this.user = user;
	    this.preselectedReviewer = preselectedReviewer;
	}
	
	 /**
     * Shows the ComputeReviewersScorecard scene
     * @param primaryStage JavaFX stage
     * @param user current user
     */
    public void show(Stage primaryStage, User user) {
        this.user = user;

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-alignment: center;");

        // Title
        Label title = new Label("Reviewer Scorecard Dashboard");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Reviewer Dropdown
        reviewerDropdown = new ComboBox<>();
        reviewerDropdown.setPromptText("Select a Reviewer");
        reviewerDropdown.setItems(getReviewerUsernames());
        reviewerDropdown.setOnAction(e -> computeScorecard());
        if (preselectedReviewer != null && !preselectedReviewer.isEmpty()) {
            reviewerDropdown.setValue(preselectedReviewer);
            computeScorecard();
        }

        // Scorecard Label
        scorecardLabel = new Label("Select a reviewer to view scorecard.");
        scorecardLabel.setStyle("-fx-padding: 10px; -fx-font-size: 14px;");

        // Manual Edit Field
        manualEditField = new TextField();
        manualEditField.setPromptText("Enter manual score...");
        manualEditField.setPrefWidth(200);

        // Save Edited Score
        Button saveButton = new Button("Save Manual Score");
        saveButton.setOnAction(e -> scorecardLabel.setText("Scorecard (edited): " + manualEditField.getText()));

        // Redirect Button to View Interactions
        Button interactionBtn = new Button("View Reviewer Interactions");
        interactionBtn.setOnAction(e -> {
            InstructorStudentInteractionView view = new InstructorStudentInteractionView(databaseHelper, user);
            view.show(primaryStage, user);
        });

        // Revoke Role Button
        Button revokeBtn = new Button("Revoke Reviewer Role");
        revokeBtn.setOnAction(e -> {
            ViewAdminActionList view = new ViewAdminActionList(databaseHelper, user);
            view.show(primaryStage, user);
        });

        // Go Back Button
        Button backButton = new Button("Go Back");
        backButton.setOnAction(e -> returnHomePage(primaryStage));

        VBox box = new VBox(10, title, reviewerDropdown, scorecardLabel, manualEditField, saveButton, interactionBtn, revokeBtn, backButton);
        box.setStyle("-fx-alignment: center;");
        layout.getChildren().add(box);

        Scene computeScene = new Scene(layout, 800, 600);
        primaryStage.setScene(computeScene);
        primaryStage.setTitle("Compute Scorecard");
    }
    
    /**
     * Computes and displays the scorecard for the selected reviewer.
     * (Dummy logic for now; this should be replaced with actual DB calculations.)
     */
    private void computeScorecard() {
        String selectedReviewer = reviewerDropdown.getValue();
        if (selectedReviewer == null) return;

        // Dummy scoring logic - replace with DB logic
        int totalReviews = 12;
        int helpfulReviews = 7;
        double score = (helpfulReviews / (double) totalReviews) * 100;

        scorecardLabel.setText(String.format("Reviewer: %s\nTotal Reviews: %d\nHelpful Reviews: %d\nScorecard: %.2f%%",
                selectedReviewer, totalReviews, helpfulReviews, score));
    }

    /**
     * Retrieves a list of reviewer usernames.
     * (Replace with actual DB query for real implementation.)
     * @return list of usernames
     */
    private ObservableList<String> getReviewerUsernames() {
        // Dummy list - replace with DB retrieval
        return FXCollections.observableArrayList("reviewer1", "reviewer2", "reviewer3");
    }
	
	/**
	 * Redirects to InstructorHomePage
	 * 
	 * @param primaryStage the primary stage the user is in
	 */
	private void returnHomePage(Stage primaryStage) {
		InstructorHomePage instrucPage = new InstructorHomePage(databaseHelper, user);
		instrucPage.show(primaryStage, this.user);
	}
}