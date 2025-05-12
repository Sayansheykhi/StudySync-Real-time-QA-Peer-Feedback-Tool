package application;

import java.sql.SQLException;
import java.util.List;

import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * The UserAndPostActivityLog class represents an interface for interacting with flagged and hidden posts, and muted users.
 * 
 * @author John Gallagher
 * @version 1.0 4/22/25
 */
public class UserAndPostActivityLog {

	private DatabaseHelper databaseHelper;
	
	private User user;
	
	
	
	 /**
     * The ListView UI component showing the User.
     */
	private ListView<String> mutedUsersList;
	
	/**
     * An observable list of Users that will be displayed in the ListView.
     */
	private ObservableList<String> mutedUsersObservable;
	
	 /**
     * The ListView UI component showing the Question.
     */
	private ListView<Question> flaggedQuestionList;
	
	/**
     * An observable list of Questions that will be displayed in the ListView.
     */
	private ObservableList<Question> flaggedQuestionObservable;
	
	 /**
     * The ListView UI component showing the Question.
     */
	private ListView<Question> hiddenQuestionList;
	
	/**
     * An observable list of Questions that will be displayed in the ListView.
     */
	private ObservableList<Question> hiddenQuestionObservable;
	
	 /**
     * The ListView UI component showing the Answer.
     */
	private ListView<Answer> flaggedAnswerList;
	
	/**
     * An observable list of Answers that will be displayed in the ListView.
     */
	private ObservableList<Answer> flaggedAnswerObservable;
	
	 /**
     * The ListView UI component showing the Answer.
     */
	private ListView<Answer> hiddenAnswerList;
	
	/**
     * An observable list of Answers that will be displayed in the ListView.
     */
	private ObservableList<Answer> hiddenAnswerObservable;
	
	 /**
     * The ListView UI component showing the Review.
     */
	private ListView<Review> flaggedReviewList;
	
	/**
     * An observable list of Reviews that will be displayed in the ListView.
     */
	private ObservableList<Review> flaggedReviewObservable;
	
	 /**
     * The ListView UI component showing the Review.
     */
	private ListView<Review> hiddenReviewList;
	
	/**
     * An observable list of Reviews that will be displayed in the ListView.
     */
	private ObservableList<Review> hiddenReviewObservable;
	
	
	public UserAndPostActivityLog(DatabaseHelper databaseHelper, User user) {
		this.databaseHelper = databaseHelper;
		this.user = user;
	}
	
	/**
	 * Displays the primary stage.
	 * @param primaryStage the stage for the UI
	 * @param user the current user
	 */
	public void show(Stage primaryStage, User user) {
    	this.user = user;
    	
    	// Create list views
    	mutedUsersList = new ListView<String>();
    	flaggedQuestionList = new ListView<Question>();
    	hiddenQuestionList = new ListView<Question>();
    	flaggedAnswerList = new ListView<Answer>();
    	hiddenAnswerList = new ListView<Answer>();
    	flaggedReviewList = new ListView<Review>();
    	hiddenReviewList = new ListView<Review>();
    	
    	// Establish boxes for layout
    	VBox mainlayout = new VBox(10);
    	HBox innerLayout = new HBox(3);
    	VBox leftBox = new VBox(10);
    	VBox centerBox = new VBox(10);
    	VBox rightBox = new VBox(10);
    	
    	// Align all boxes
	    mainlayout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    leftBox.setStyle("-fx-alignment: center");
	    centerBox.setStyle("-fx-alignment: center");
	    rightBox.setStyle("-fx-alignment: center");
	   
	    // Attempt to get all required lists from database
	    try {
	    	mutedUsersObservable = FXCollections.observableArrayList(databaseHelper.getMutedUsers());
	    	flaggedQuestionObservable = FXCollections.observableArrayList(databaseHelper.getAllQuestionsFlagged(user));
	    	hiddenQuestionObservable = FXCollections.observableArrayList(databaseHelper.getAllQuestionsHidden(user));
	    	flaggedAnswerObservable = FXCollections.observableArrayList(databaseHelper.getAllAnswersFlagged(user));
	    	hiddenAnswerObservable = FXCollections.observableArrayList(databaseHelper.getAllAnswersHidden(user));
	    	flaggedReviewObservable = FXCollections.observableArrayList(databaseHelper.getAllReviewsFlagged(user));
	    	hiddenReviewObservable = FXCollections.observableArrayList(databaseHelper.getAllReviewsHidden(user));
	    }catch(SQLException e){
	    	e.printStackTrace();
	    }
	    
	    
	    // Prepare lists and style
	    mutedUsersList.setPrefHeight(250);
	    mutedUsersList.setPrefWidth(500);
	    mutedUsersList.setEditable(true);
	    mutedUsersList.setItems(mutedUsersObservable);
	    flaggedQuestionList.setPrefHeight(250);
	    flaggedQuestionList.setPrefWidth(500);
	    flaggedQuestionList.setEditable(true);
	    flaggedQuestionList.setItems(flaggedQuestionObservable);
	    hiddenQuestionList.setPrefHeight(250);
	    hiddenQuestionList.setPrefWidth(500);
	    hiddenQuestionList.setEditable(true);
	    hiddenQuestionList.setItems(hiddenQuestionObservable);
	    flaggedAnswerList.setPrefHeight(250);
	    flaggedAnswerList.setPrefWidth(500);
	    flaggedAnswerList.setEditable(true);
	    flaggedAnswerList.setItems(flaggedAnswerObservable);
	    hiddenAnswerList.setPrefHeight(250);
	    hiddenAnswerList.setPrefWidth(500);
	    hiddenAnswerList.setEditable(true);
	    hiddenAnswerList.setItems(hiddenAnswerObservable);
	    flaggedReviewList.setPrefHeight(250);
	    flaggedReviewList.setPrefWidth(500);
	    flaggedReviewList.setEditable(true);
	    flaggedReviewList.setItems(flaggedReviewObservable);
	    hiddenReviewList.setPrefHeight(250);
	    hiddenReviewList.setPrefWidth(500);
	    hiddenReviewList.setEditable(true);
	    hiddenReviewList.setItems(hiddenReviewObservable);
	    
	    // Set selection modes for all lists
	    mutedUsersList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	    flaggedQuestionList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	    hiddenQuestionList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	    flaggedAnswerList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	    hiddenAnswerList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	    flaggedReviewList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	    hiddenReviewList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	    
	    
	    // Create and style all labels
	    Label pageLabel = new Label("User and Post Activity Log");
	    Label leftBoxLabel = new Label("Muted Users");
	    Label centerBoxLabel = new Label("Flagged Posts");
	    Label rightBoxLabel = new Label("Hidden Posts");
	    Label innerQuestionLabel = new Label("Questions");
	    Label innerAnswerLabel = new Label("Answers");
	    Label innerReviewLabel = new Label("Reviews");
	    Label innerQuestionLabel2 = new Label("Questions");
	    Label innerAnswerLabel2 = new Label("Answers");
	    Label innerReviewLabel2 = new Label("Reviews");
	    pageLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    leftBoxLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    centerBoxLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    rightBoxLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    innerQuestionLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
	    innerAnswerLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
	    innerReviewLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
	    innerQuestionLabel2.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
	    innerAnswerLabel2.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
	    innerReviewLabel2.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
	    
	    // Create and style all buttons
	    Button unmuteButton = new Button("Unmute");
	    Button unflagButton = new Button("Unflag");
	    Button unhideButton = new Button("Unhide");
	    Button backButton = new Button("Back");
	    unmuteButton.setStyle("-fx-font-size: 12px; -fx-alignment: center;");
	    unflagButton.setStyle("-fx-font-size: 12px; -fx-alignment: center;");
	    unhideButton.setStyle("-fx-font-size: 12px; -fx-alignment: center;");
	    backButton.setStyle("-fx-font-size: 12px; -fx-alignment: center;");
	    
	    // Button to allow unmuting selected user
	    unmuteButton.setOnAction(e -> {
	    	String selectedItem = mutedUsersList.getSelectionModel().getSelectedItem();
	    	databaseHelper.unmuteUser(selectedItem);
	    	try {
	    		mutedUsersObservable = FXCollections.observableArrayList(databaseHelper.getMutedUsers());
	    		mutedUsersList.setItems(mutedUsersObservable);
	    	}catch(SQLException a) {
	    		a.printStackTrace();
	    	}
	    	});
	    
	    // Button to allow unflagging a question, answer or review.
	    unflagButton.setOnAction(e -> {
	    	Question selectedItem = flaggedQuestionList.getSelectionModel().getSelectedItem();
	    	Answer selectedAnswer = flaggedAnswerList.getSelectionModel().getSelectedItem();
	    	Review selectedReview = flaggedReviewList.getSelectionModel().getSelectedItem();
	    	if(selectedItem != null) {
	    		databaseHelper.clearQuestionFlag(selectedItem.getQuestionID());
	    		flaggedQuestionObservable = FXCollections.observableArrayList(databaseHelper.getAllQuestionsFlagged(user));
	    		flaggedQuestionList.setItems(flaggedQuestionObservable);
	    	}else if(selectedAnswer != null){
	    		databaseHelper.clearAnswerFlag(selectedAnswer.getAnswerID());
	    		flaggedAnswerObservable = FXCollections.observableArrayList(databaseHelper.getAllAnswersFlagged(user));
	    		flaggedAnswerList.setItems(flaggedAnswerObservable);
	    	}else if(selectedReview != null) {
	    		databaseHelper.clearReviewFlag(selectedReview.getReviewID());
			    flaggedReviewObservable = FXCollections.observableArrayList(databaseHelper.getAllReviewsFlagged(user));
			    flaggedReviewList.setItems(flaggedReviewObservable);
	    	}
	    });
	    
	    // Button to allow unhidding a question, answer or review.
	    unhideButton.setOnAction(e -> {
	    	Question selectedItem = hiddenQuestionList.getSelectionModel().getSelectedItem();
	    	Answer selectedAnswer = hiddenAnswerList.getSelectionModel().getSelectedItem();
	    	Review selectedReview = hiddenReviewList.getSelectionModel().getSelectedItem();
	    	if(selectedItem != null) {
	    		databaseHelper.clearHiddenQuestion(selectedItem.getQuestionID());
	    		hiddenQuestionObservable = FXCollections.observableArrayList(databaseHelper.getAllQuestionsHidden(user));
	    		hiddenQuestionList.setItems(hiddenQuestionObservable);
	    	}else if(selectedAnswer != null) {
	    		databaseHelper.clearHiddenAnswer(selectedAnswer.getAnswerID());
	    		hiddenAnswerObservable = FXCollections.observableArrayList(databaseHelper.getAllAnswersHidden(user));
	    		hiddenAnswerList.setItems(hiddenAnswerObservable);
	    	}else if(selectedReview != null) {
		    	databaseHelper.clearHiddenReview(selectedReview.getReviewID());
		    	hiddenReviewObservable = FXCollections.observableArrayList(databaseHelper.getAllReviewsHidden(user));
		    	hiddenReviewList.setItems(hiddenReviewObservable);
	    	}
	    });
	    
	    // back button takes user back to their role page.
	    backButton.setOnAction(a -> {
	    	if(user.getRole()[3]) {
	    		new InstructorHomePage(databaseHelper, user).show(primaryStage, user);
	    	}else if(user.getRole()[4]) {
	    		new StaffHomePage(databaseHelper, user).show(primaryStage, user);
	    	}
	    	
	    });
	    
	    // If Instructor
	    if(user.getRole()[3]) {
	    	leftBox.getChildren().addAll(leftBoxLabel, mutedUsersList, unmuteButton);
	    	centerBox.getChildren().addAll(centerBoxLabel, innerQuestionLabel, flaggedQuestionList, innerAnswerLabel, flaggedAnswerList, innerReviewLabel, flaggedReviewList, unflagButton);
	    	rightBox.getChildren().addAll(rightBoxLabel, innerQuestionLabel2, hiddenQuestionList, innerAnswerLabel2, hiddenAnswerList, innerReviewLabel2, hiddenReviewList, unhideButton);
	   // If staff
	    }else {
	    	leftBox.getChildren().addAll(leftBoxLabel, mutedUsersList);
	    	centerBox.getChildren().addAll(centerBoxLabel, innerQuestionLabel, flaggedQuestionList, innerAnswerLabel, flaggedAnswerList, innerReviewLabel, flaggedReviewList);
	    	rightBox.getChildren().addAll(rightBoxLabel, innerQuestionLabel2, hiddenQuestionList, innerAnswerLabel2, hiddenAnswerList, innerReviewLabel2, hiddenReviewList);
	    }
	    
	    innerLayout.getChildren().addAll(leftBox, centerBox, rightBox);
	    mainlayout.getChildren().addAll(pageLabel, innerLayout, backButton);
	    Scene logScene = new Scene(mainlayout, 1200, 600);

	    // Set the scene to primary stage
	    primaryStage.setScene(logScene);
	    primaryStage.setTitle("User and Post Activity Log Page");
	    primaryStage.show();
    	
    }
    
}
