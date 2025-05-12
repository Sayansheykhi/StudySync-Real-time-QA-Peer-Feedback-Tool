package application;

import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * The ReviewerProfile class represents an interface for Reviewer users to view there scorecards,
 * private messages and all reviews the have made in a single place.
 * 
 * @author John Gallagher
 * @version 1.0 4/20/25
 */
public class ReviewerProfile {
	
	private DatabaseHelper databaseHelper;
	
	private User user;
	
	private Reviews userReviews;
	
	ListView<Boolean> emptyView;
    /**
     * The ListView UI component showing the Review.
     */
	private ListView<Review> submittedReviewList;
	
	/**
     * An observable list of Reviews that will be displayed in the ListView.
     */
	private ObservableList<Review> userReviewObservable;
	
	/**
     * An observable list of messages that will be displayed in the ListView.
     */
    private ObservableList<ReviewerMessage> messages;
    /**
     * The ListView UI component showing the messages.
     */
    private ListView<ReviewerMessage> messageListView;
	
	/**
	 * Initializes the page, taking the databaseHelper object from the previous page
	 * 
	 * @param databaseHelper 		Object that handles all database interactions
	 */
	public ReviewerProfile(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}
	
	/**
	 * Builds and displays the page
	 * 
	 * @param primaryStage			The application window
	 * @param user					The logged in user's information
	 */
	public void show(Stage primaryStage, User user) {
		this.user = user;
		
		VBox layout = new VBox(10);
		HBox layout2 = new HBox(10);
		
		userReviews = new Reviews(databaseHelper, user);
		submittedReviewList = new ListView<Review>();
		userReviewObservable = FXCollections.observableArrayList(userReviews.getReviewsByUsername(user.getUserName()));
		
		messages = databaseHelper.getReviewerMessagesForUser(user.getUserName());
		messageListView = new ListView<ReviewerMessage>();
		
		emptyView = new ListView<Boolean>();
		
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    layout2.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // prepare lists for display
	    submittedReviewList.setPrefHeight(250);
	    submittedReviewList.setPrefWidth(500);
	    submittedReviewList.setEditable(true);
	    submittedReviewList.setItems(userReviewObservable);
	    
	    messageListView.setPrefHeight(250);
	    messageListView.setPrefWidth(500);
	    messageListView.setEditable(true);
	    messageListView.setItems(messages);
	    
	    emptyView.setPrefHeight(250);
	    emptyView.setPrefWidth(500);
	    emptyView.setEditable(true);
	    
	    
	    
	    // Label to display Hello user
	    Label reviewerLabel = new Label("Reviewer Profile");
	    reviewerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    Button userReviewsButton = new Button("Submitted Reviews");
	    Button privateMessageButton = new Button("Private Messages");
	    Button viewScoreCardsButton = new Button("View Scorecards");
	    Button backButton = new Button("Back");
	    
	    
	    
	    userReviewsButton.setOnAction(a -> {
	    	layout.getChildren().remove(1);
	    	layout.getChildren().add(1, submittedReviewList);
;	    });
	    
	    privateMessageButton.setOnAction(a -> {
	    	layout.getChildren().remove(1);
	    	layout.getChildren().add(1, messageListView);
	    });
	    /*
	    viewScoreCardsButton.setOnAction(a -> {
	    	layout.getChildren().remove(1);
	    	//add scorecard
	    	layout.getChildren().add(1, emptyView);
	    });
	    */
	    
	    backButton.setOnAction(a -> {
	    	new ReviewerHomePage(databaseHelper).show(primaryStage, user);
	    });
	    
	    layout2.getChildren().addAll(userReviewsButton, privateMessageButton, backButton);
	    layout.getChildren().addAll(reviewerLabel, layout2);
	    Scene scene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(scene);
	    primaryStage.setTitle("Reviewer Profile");
	}
	
}