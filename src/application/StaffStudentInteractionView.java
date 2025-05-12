package application;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * The StaffStudentInteractionView class provides an interface for users with the Staff role to assess student interactions which includes 
 * viewing submitted questions, submitted question replies, submitted potential answers, submitted reviews and the private feedback 
 * messages sent from Student-To-Student and Reviewer-To-Student or visa versa. Users can hide or flag selected questions, question replies,
 * answers, or reviews or mute users.
 * 
 * @author Cristina Hooe
 * @version 1.0 4/9/2025
 * @version 2.0 4/19/2025
 */
public class StaffStudentInteractionView {
	/**
	 * Declaration of a DatabaseHelper object for database interactions
	 */
    private final DatabaseHelper databaseHelper;

    /**
	 * Declaration of a User object which is set to the user object passed from the previously accessed page via the show() function call
	 */
    private User user;

    /**
	 * Constructor used to create a new instance of StaffPrivateMessages within class StaffHomePage
	 * 
	 * @param databaseHelper object instance passed from previously accessed page
	 */
    public StaffStudentInteractionView(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    /**
     * Label used in both handleReply() method and the submittedQuestionsList CellFactory
     */
    private Label questionErrorLabel;
    
    /**
     * Label used in handleReply() to store question reply input validation results 
     */
    private Label questionReplyInvalidInput;
    
    /**
     * TextArea for a questionReply used within handleReply() method
     */
    private TextArea replyTextField;
    
    /**
	 * Declaration of a Questions object which is used to populate and filter the questions List View
	 */
    private Questions initialQuestionsList;
    
    /**
	 * Declaration of an ObservableList used to update the contents of the submittedQuestionsList ListView
	 */
    private ObservableList<Question> allQuestionsObservable;

    /**
     * Declaration of a Tab object used for the privateMessages List View Cell Factories
     */
    private Tab currTab;
    
    /**
     * Declaration of a Tab object representing the privateMessages List View tab displaying private messages sent between two Students
     */
    private Tab studentStudentTab;
    
    /**
     * Declaration of a Tab object representing the privateMessages List View tab displaying private messages sent a Student and a Reviewer
     */
    private Tab reviewersStudentTab;
    
    /**
     * Declaration of a label to be used when no message is selected to be flagged.
     */
    private Label questionListViewErrorLabel;
    
    /**
     * Declaration of a label to be used when no answer is selected to be flagged.
     */
    private Label answerListViewErrorLabel;
    
    /**
     * Declaration of a label for input related errors specific to answers.
     */
    private Label answerTextInvalidInput;
    
    /**
     * Declaration of a label for non-input related errors specific to answers.
     */
    private Label answerErrorLabel;
    
    /**
     * Declaration of a label for errors related to specifically to review List Views
     */
    private Label reviewListViewErrorLabel;
    
    /**
     * Declaration of a label to be used when no private message is selected to flag.
     */
    private Label privateMessagesListViewErrorLabel;
    
    /**
     * Declaration of a label for Review filtering.
     */
    private Label reviewFiltersLabel;
    
    /**
     * Displays the StaffStudentInteractionView including 4 lists, one displaying submitted questions and question replies, a second displaying 
     * potential answers, a third displaying submitted reviews for potential answers and a fourth list that displays either private feedback 
     * messages sent between two Students or private feedback messages sent between a Student and Reviewer depending on which tab is 
     * selected. A Staff member can mute a user which hides all of their posted content and prevents them from posting new content,
     * can flag a concerning selected question, question reply, answer, or review for evaluation by an Instructor or can hide a
     * concerning selected question, question reply, answer, or review which removes the selection from display within the ListViews
     * in ReviewerReviewsList.java and StudentQuestionAnswers.java.
     * 
     * @param primaryStage the primary stage where the scene will be displayed
	 * @param user the registered user whom successfully logged in within the UserLoginPage and just accessed StaffHomePage
     */
    public void show(Stage primaryStage, User user) {
    	this.user = user;
	    
	    // Tab Pane for private feedback messages
    	TabPane studentMessagesTabPane = new TabPane();
    	
    	// Border Pane containing the 3 VBox's and 3 HBox's
    	BorderPane studentInteractionsBorderPane = new BorderPane();
    	studentInteractionsBorderPane.setMinWidth(1775);
    	studentInteractionsBorderPane.setMinHeight(700);
    	studentInteractionsBorderPane.setPadding(new Insets(5, 5, 5, 5));
    	studentInteractionsBorderPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE );
    	
    	// Header for top of entire page
    	HBox topHeader = new HBox(10);
    	topHeader.setStyle("-fx-alignment: center;");
    	topHeader.setPadding(new Insets(10));
    	
    	// Header for bottom of the page
    	HBox bottomHeader = new HBox(10);
    	bottomHeader.setStyle("-fx-alignment: bottom-left;");
    	bottomHeader.setPadding(new Insets(10));
    	
    	// Label for top of entire page
    	Label pageLabel = new Label("Staff Student and Reviewer Interaction View");
    	pageLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
	    
    	// Tab to display the List View of messages sent from Reviewer-to-Student and Student-to-Reviewer
    	reviewersStudentTab = new Tab("Private Feedback Messages between Reviewers and Students");
    	reviewersStudentTab.setClosable(false);
    	
    	// Tab to display the List View of messages sent from Student-toStudent
    	studentStudentTab = new Tab("Private Feedback Messages between Students");
    	studentStudentTab.setClosable(false);
	    
    	// VBox containing list of Questions
    	VBox questionList = new VBox(10);
    	questionList.setPrefWidth(425);
    	questionList.setStyle("-fx-alignment: center; -fx-padding: 10; -fx-border-color: black; -fx-border-width: 1;");

    	// Label for title above Questions List View
    	Label submittedQuestionsLabel = new Label("Submitted Questions");
    	submittedQuestionsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

    	// Field to search by Questions Keyword
    	TextField questionsKeywordField = new TextField();
    	questionsKeywordField.setPromptText("Search by Keyword");
    	
    	// Button to activate question filtering
    	Button questionsFilterButton = new Button("Filter");
    	
    	// Hidden label used to align questionList ListView with answerList ListView
    	Label questionsAlignment = new Label();
    	questionsAlignment.setStyle("-fx-font-size: 20px;");
    	questionsAlignment.setVisible(false);
    	
    	// Button to flag a concerning message
    	Button questionFlagButton = new Button ("Flag selected Question / Question Reply");
    	
    	// Label to be used when no message is selected to flag
    	questionListViewErrorLabel = new Label();
    	questionListViewErrorLabel.setVisible(false);
    	questionListViewErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
    	
    	// Label for non-input related errors specific to questions
        questionErrorLabel = new Label();
        questionErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
        questionErrorLabel.setVisible(false);
        
        // Label for invalid input related errors specific to question replies
        questionReplyInvalidInput = new Label();
        questionReplyInvalidInput.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
        questionReplyInvalidInput.setVisible(false);
        
        // TextArea for submitting a new question reply
        replyTextField = new TextArea();
        replyTextField.setPromptText("Enter your question reply");
        replyTextField.setVisible(false);
        
        // Button to hide a concerning question or question reply
        Button hideSelectedQuestion = new Button("Hide selected Question or Question Reply");
    	
    	// VBox containing list of answers
    	VBox answerList = new VBox(10);
    	answerList.setPrefWidth(425);
    	answerList.setStyle("-fx-alignment: center; -fx-padding: 10; -fx-border-color: black; -fx-border-width: 1;");

    	// Label for title above Answers List View
    	Label submittedAnswersLabel = new Label("Potential Answers");
    	submittedAnswersLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

    	// Field to Search by Answers Keyword
    	TextField answersKeywordField = new TextField();
    	answersKeywordField.setPromptText("Search by Keyword");
    	
    	// Button to activate answer filtering
    	Button answersFilterButton = new Button("Filter");
    	
		// Button to flag a concerning answer
    	Button answerFlagButton = new Button ("Flag selected Answer");
    	
    	// Label to be used when no answer is selected to flag
    	answerListViewErrorLabel = new Label();
    	answerListViewErrorLabel.setVisible(false);
    	answerListViewErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
    	
    	// Button to submit a new answer
    	Button submitAnswer = new Button ("Submit new Answer");
    	
    	// Button to hide a concerning answer
        Button hideSelectedAnswer = new Button("Hide selected Answer");
    	
    	// Label for input related errors specific to answers
		answerTextInvalidInput = new Label();
		answerTextInvalidInput.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
		answerTextInvalidInput.setVisible(false);
		
		// Label for non-input related errors specific to answers
        answerErrorLabel = new Label();
        answerErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
        answerErrorLabel.setVisible(false);
        
        // TextArea for submitting a new answer
        TextArea answerTextField = new TextArea();
		answerTextField.setPromptText("Enter your answer");
		answerTextField.setVisible(false);
    	
    	// VBox containing list of reviews
    	VBox reviewList = new VBox(10);
    	reviewList.setPrefWidth(425);
    	reviewList.setStyle("-fx-alignment: center; -fx-padding: 10; -fx-border-color: black; -fx-border-width: 1;");
    	
    	// Label for title above Reviews List View
    	Label submittedReviewsLabel = new Label("Submitted Reviews for Potential Answers");
    	submittedReviewsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    	
    	// Field to Search by Reviews Keyword
    	TextField reviewsKeywordField = new TextField();
    	reviewsKeywordField.setPromptText("Search by Keyword");
    	
    	// Button to activate review filtering
    	Button reviewsFilterButton = new Button("Filter");
    	
    	// Hidden label used to align reviewList ListView with answerList ListView
    	Label reviewsAlignment = new Label();
    	reviewsAlignment.setStyle("-fx-font-size: 20px;");
    	reviewsAlignment.setVisible(false);
    	
    	// Label for Review filtering
    	reviewFiltersLabel = new Label();
    	reviewFiltersLabel.setStyle("-fx-text-fill: grey; -fx-font-size: 12px;");
    	reviewFiltersLabel.setVisible(false);
    	
    	// Label for errors related to specifically to review List Views
    	reviewListViewErrorLabel = new Label();
    	reviewListViewErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
    	reviewListViewErrorLabel.setVisible(false);
    	
    	// Button to flag a concerning review
    	Button reviewFlagButton = new Button ("Flag selected Review");
    	
    	// Button to hide a concerning answer
        Button hideSelectedReview = new Button("Hide selected Review");
    	
    	// HBox containing center BorderPane VBox's answerList and reviewList
    	HBox centerBorderPaneVBoxes = new HBox(0, answerList, reviewList);
    	centerBorderPaneVBoxes.setPrefWidth(850);
    	
    	// VBox containing the private feedback messages
    	VBox messagesList = new VBox(10);
    	messagesList.setPrefWidth(500);
    	messagesList.setStyle("-fx-alignment: top-center; -fx-padding: 10; -fx-border-color: black; -fx-border-width: 1;");
    	
    	// Header label for VBox messagesList
    	Label messagesListLabel = new Label("Student Private Feedback Messages");
    	messagesListLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    	
    	// Hidden label used to align messagesList ListView with answerList ListView
    	Label messagesAlignment = new Label();
    	messagesAlignment.setStyle("-fx-font-size: 16px;");
    	messagesAlignment.setVisible(false);
    	
    	// Field to Search by Messages Keyword
        TextField messagesKeywordField = new TextField();
        messagesKeywordField.setPromptText("Search by Keyword");
        
        // Button to flag a concerning private message
    	Button privateMessageFlagButton = new Button ("Flag selected Private Message");
    	
    	// Label to be used when no private message is selected to flag
    	privateMessagesListViewErrorLabel = new Label();
    	privateMessagesListViewErrorLabel.setVisible(false);
    	privateMessagesListViewErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
    	
    	// Label for errors related to studentsAndReviewers ListView
    	Label userErrorLabel = new Label();
    	userErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
    	userErrorLabel.setVisible(false);
    	
    	// QUESTIONS DISPLAY AND FILTERS
    	// Load Question List upon opening
    	ListView<Question> submittedQuestionsList = new ListView<>();
    	initialQuestionsList = new Questions(databaseHelper, user);
    	allQuestionsObservable = FXCollections.observableArrayList();

    	// Load all the questions first
    	allQuestionsObservable.addAll(databaseHelper.getAllQuestionsEvenHidden(user));

    	// Then grab all the replies
    	ObservableList<Question> replies = FXCollections.observableArrayList(databaseHelper.getAllRepliesEvenHidden());

    	// Then iterate to connect each reply with its parent question
    	for (Question reply : replies) {
    	    boolean parentFound = false;
    	    for (int i = 0; i < allQuestionsObservable.size(); i++) {
    	        Question parentQuestion = allQuestionsObservable.get(i);

    	        if (parentQuestion.getQuestionID() == reply.getQuestionID()) {
    	            allQuestionsObservable.add(i + 1, reply);
    	            parentFound = true;
    	            break;
    	        }
    	    }
    	}
    	
    	// DEBUGGING
    	ArrayList<Question> questionsFromDB = databaseHelper.getAllQuestionsEvenHidden(user);
    	System.out.println("Fetched questions from database:");
    	for (Question question : questionsFromDB) {
    	    System.out.println("Question ID: " + question.getQuestionID() + 
    	                       ", Question Title: " + question.getQuestionTitle() + 
    	                       ", Is Resolved: " + question.getIsResolved() + 
    	                       ", Question Body: " + question.getQuestionBody() +
    	                       ", IsFlagged: " + question.getIsFlagged());
    	}
    	
    	// Apply the submittedQuestionsList to the Filtered List
    	FilteredList<Question> filteredQuestionsSearch = new FilteredList<>(allQuestionsObservable, q -> true);
    	submittedQuestionsList.setItems(filteredQuestionsSearch);
    	submittedQuestionsList.setPrefWidth(425);

    	// List of question filtering options
    	ObservableList<String> questionsFilterOptions = FXCollections.observableArrayList();
    	questionsFilterOptions.add("None");
    	questionsFilterOptions.add("Answered");
    	questionsFilterOptions.add("Unanswered");
    	questionsFilterOptions.add("Unresolved");

    	// Question filtering selection box
    	ChoiceBox<String> questionsFilterChoice = new ChoiceBox<String>(questionsFilterOptions);

    	questionsFilterButton.setOnAction(a -> {	
    		//First filters by selection
    		String filter = questionsFilterChoice.getValue();
    		ArrayList<Question> filteredQuestions = new ArrayList<Question>();	
    		
    		switch(filter) {
    			// No Filter
    			case "None":
    				filteredQuestions.addAll(databaseHelper.getAllQuestionsEvenHidden(user));
    				break;
    			
    			// Answered questions
    			case "Answered":
    				filteredQuestions.addAll(databaseHelper.getAnsweredQuestionsEvenHidden());
    				break;
    			
    			// Questions without answers
    			case "Unanswered":
    				filteredQuestions.addAll(databaseHelper.getUnansweredQuestionsEvenHidden());
    				break;
    			
    			// Unresolved questions
    			case "Unresolved":
    				filteredQuestions.addAll(databaseHelper.getUnresolvedQuestionsEvenHidden());
    				break;
    			
    			default:
    				filteredQuestions.addAll(databaseHelper.getAllQuestionsEvenHidden(user));
    				break;
    		}
    		// Combine replies with their parents to maintain the hierarchy when filtering (replies indented underneath their parent questions)
    		ArrayList<Question> filteredQuestionsAndReplies = new ArrayList<>();
    		
    		for (Question q : filteredQuestions) {
    			filteredQuestionsAndReplies.add(q);
    			if (filter != "Answered") {
    				for (Question r: databaseHelper.getAllRepliesEvenHidden()) {
    					if (r.getQuestionID() == q.getQuestionID()) {
    						filteredQuestionsAndReplies.add(r);
    					}
    				}
    			}
    		}
    		// Enable keyword search to further filter results displayed by selected filter
    		String keyword = questionsKeywordField.getText();
    		ArrayList<Question> filteredQuestionsKey = new ArrayList<Question>();

    		 if (keyword.length() > 0) {
    			int questionID = -1;
    				boolean keywordFound = false;
    				String whereKeywordFound = "";
    				Set<Integer> addedReplies = new HashSet<>(); // used to keep track of replyID() and questionID() already added to filtered view
    				// If a question is found to have the keyword, add its corresponding replies to the filtered view
    			for (Question q : filteredQuestionsAndReplies) {
    				if ((q.getQuestionTitle() != null && q.getQuestionTitle().toLowerCase().contains(keyword.toLowerCase())) ||
    					(q.getQuestionBody() != null && q.getQuestionBody().toLowerCase().contains(keyword.toLowerCase())) ||
    					(q.getStudentFirstName() != null && q.getStudentFirstName().toLowerCase().contains(keyword.toLowerCase())) ||
    					(q.getStudentLastName() != null && q.getStudentLastName().toLowerCase().contains(keyword.toLowerCase()))) {
    					keywordFound = true;
    					whereKeywordFound = "question";
    					questionID = q.getQuestionID();
    					filteredQuestionsKey.add(q);
    				}
    				if (keywordFound && whereKeywordFound.equals("question")) {
    					for (Question r: databaseHelper.getAllRepliesEvenHidden()) {
    	    				if (r.getQuestionID() == questionID && !addedReplies.contains(r.getReplyID())) {
    	    					filteredQuestionsKey.add(r);
    	    					addedReplies.add(r.getReplyID());
    	    					break;
    	    				}
    	    			}
    				}
    			}
    			// If a reply is found to have the keyword, add the corresponding question first and then the reply to the filtered view
    			for (Question r : initialQuestionsList.getAllReplies()) {
    				if ((r.getQuestionReply() != null && r.getQuestionReply().toLowerCase().contains(keyword.toLowerCase())) ||
    						(r.getStudentFirstName() != null && r.getStudentFirstName().toLowerCase().contains(keyword.toLowerCase())) ||
    	    				(r.getStudentLastName() != null && r.getStudentLastName().toLowerCase().contains(keyword.toLowerCase()))) {
    						keywordFound = true;
    						whereKeywordFound = "reply";
    						questionID = r.getQuestionID();

    						if (!addedReplies.contains(r.getReplyID())) {
    							for (Question q : filteredQuestionsAndReplies) {
    	    						if (q.getQuestionID() == questionID && !addedReplies.contains(q.getQuestionID())) {
    	    							filteredQuestionsKey.add(q);
    	    							addedReplies.add(q.getQuestionID());
    	    							break;
    	    						}
    	    					}
    							filteredQuestionsKey.add(r);
    							addedReplies.add(r.getReplyID());
    						}
    				}	
    			}
    		 }
    		 else {
    			 filteredQuestionsKey = filteredQuestionsAndReplies;
    		 }
    		// Displays filtered list
    		ObservableList<Question> filteredQuestionsObservable = FXCollections.observableArrayList(filteredQuestionsKey);
    		submittedQuestionsList.setItems(filteredQuestionsObservable); 
    	});

    	// Enable only Question keyword searching to filter list (no filter selected)
    	questionsKeywordField.textProperty().addListener((observable, oldValue, newValue) -> {
    		filteredQuestionsSearch.setPredicate(question -> {
    			if (newValue == null || newValue.isEmpty()) {
    				return true;
    			}
    			String convertToLowerCase = newValue.toLowerCase();
    			boolean checkQuestionsForKeyword = 
    				   (question.getQuestionTitle() != null && question.getQuestionTitle().toLowerCase().contains(convertToLowerCase)) ||
    				   (question.getQuestionBody() != null && question.getQuestionBody().toLowerCase().contains(convertToLowerCase)) ||
    				   (question.getStudentFirstName() != null && question.getStudentFirstName().toLowerCase().contains(convertToLowerCase)) ||
    				   (question.getStudentLastName() != null && question.getStudentLastName().toLowerCase().contains(convertToLowerCase));
    			
    			boolean checkRepliesForKeyword = 
    					   (question.getQuestionReply() != null && question.getQuestionReply().toLowerCase().contains(convertToLowerCase)) ||
    					   (question.getStudentFirstName() != null && question.getStudentFirstName().toLowerCase().contains(convertToLowerCase)) ||
    					   (question.getStudentLastName() != null && question.getStudentLastName().toLowerCase().contains(convertToLowerCase));
    				
    			if (checkQuestionsForKeyword) {
    				return true;
    			}
    			
    			if (checkRepliesForKeyword) {
    				return true;
    			}
    			return false;

    		});
    		submittedQuestionsList.setItems(filteredQuestionsSearch);
    	});

    	// CellFactory to display questions in the ListView
    	submittedQuestionsList.setCellFactory(new Callback<ListView<Question>, ListCell<Question>>() {
    	    @Override
    	    public ListCell<Question> call(ListView<Question> param) {
    	        return new ListCell<Question>() {
    	            private Label questionContent;
    	            private Button questionReply;
    	            private HBox content;
    	            {
    	            	questionReply = new Button("Request question clarification");
    	            	questionContent = new Label();
    	            	content = new HBox(10, questionContent, questionReply);
    	            	content.setAlignment(Pos.CENTER_LEFT);
    	            	 // Handle reply button click
    	            	 questionReply.setOnAction(a -> {
    	            	     Question questionToReply = getItem();
    	            	     if (questionToReply == null) {
    	            	         questionErrorLabel.setText("No Question was selected for requesting clarification");
    	            	         questionErrorLabel.setVisible(true);
    	            	     } 
    	            	     else {
    	            	     	handleReply(questionToReply);
    	            	     }
    	            	 });
    	            }
    	            @Override
    	            protected void updateItem(Question question, boolean empty) {
    	                super.updateItem(question, empty);
    	                if (empty || question == null) {
    	                    setGraphic(null);
    	                    setText(null);
    	                    return;
    	                } 
    	                else {
    	                    displayQuestionDetails(question);
    	                }
    	            }
    	            private void displayQuestionDetails(Question question) {
    	                String formattedText;
    	                // the question is a reply so indent it
    	                if (question.getReplyID() != -1) {
    	                	content.getChildren().removeIf(node -> node instanceof Label && ((Label) node).getText().equals("✔️"));
    	                	content.getChildren().removeIf(node -> node instanceof Label && ((Label) node).getText().equals("🚩"));
    	                    formattedText = String.format("Author: %s\n%s\nReplyText: %s", question.getStudentFirstName() + " " + question.getStudentLastName(), question.getReplyingTo(), question.getQuestionReply());
    	                    questionContent.setStyle("-fx-padding: 0 0 0 40px;");
    	                    questionContent.setText(formattedText);
    	                    questionReply.setVisible(false);
    	                }
    	                // the question is not a reply but is also marked deleted so do not indent, display with attributes set to "Deleted"
    	                else if (question.getReplyID() == -1 && question.getStudentFirstName().equals("Deleted Student First Name")) {
    	                	formattedText = String.format("Author: %s\nSubject: %s\nUnread Answers: %d\nQuestion: %s", 
    	                			question.getStudentFirstName() + " " + question.getStudentLastName(),
    	                            question.getQuestionTitle(), 
    	                            initialQuestionsList.countUnreadPotentialAnswers(question.getQuestionID()),
    	                            question.getQuestionBody());
    	                			questionContent.setStyle("-fx-padding: 0 0 0 0;");
    	    	                	questionContent.setText(formattedText);
    	    	                	questionReply.setVisible(false);
    	                }
    	                // the question is not a reply so do not indent, just display the question
    	                else if (question.getReplyID() == -1 && !question.getIsResolved()) {
    	                	if (!question.getIsResolved()) {
    	                		content.getChildren().removeIf(node -> node instanceof Label && ((Label) node).getText().equals("✔️"));
    	                	}
    	                	content.getChildren().removeIf(node -> node instanceof Label && ((Label) node).getText().equals("🚩"));
    	                	formattedText = String.format("Author: %s\nSubject: %s\nUnread Answers: %d\nQuestion: %s", 
    	                			question.getStudentFirstName() + " " + question.getStudentLastName(),
    	                            question.getQuestionTitle(), 
    	                            initialQuestionsList.countUnreadPotentialAnswers(question.getQuestionID()),question.getQuestionBody());
    		                    	questionContent.setText(formattedText);
    		                    	questionContent.setStyle("-fx-padding: 0 0 0 0;");
    		                    	questionReply.setVisible(true);
    	                }
    	                // the question is not a reply, but it has been marked resolved so display a checkmark
    	                else if (question.getIsResolved() && question.getReplyID() == -1) {
    	                	formattedText = String.format("Author: %s\nSubject: %s\nUnread Answers: %d\nQuestion: %s",
    		                	question.getStudentFirstName() + " " + question.getStudentLastName(),
    	                        question.getQuestionTitle(), 
    	                        initialQuestionsList.countUnreadPotentialAnswers(question.getQuestionID()),question.getQuestionBody());
    	                	questionContent.setText(formattedText);
    	                	questionContent.setStyle("-fx-padding: 0 0 0 0;");
    	                	questionReply.setVisible(true);
    	                	Label checkmarkLabel = new Label("✔️");
    	                	checkmarkLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: green;");
    	                	content.getChildren().removeIf(node -> node instanceof Label && ((Label) node).getText().equals("✔️"));
    	                	content.getChildren().removeIf(node -> node instanceof Label && ((Label) node).getText().equals("🚩"));
    	                	content.getChildren().add(0, checkmarkLabel);
    	                }
    	                if (question.getIsFlagged()) {
    	                	Label flagLabel = new Label("🚩");
    	                	flagLabel.setStyle("-fx-padding: 5;");
	    	                content.getChildren().removeIf(node -> node instanceof Label && ((Label) node).getText().equals("🚩"));
	    	                Tooltip tooltip = new Tooltip(question.getReasonIsFlagged());
	    	            	tooltip.setStyle("-fx-font-size: 12px;");
	    	            	Tooltip.install(flagLabel, tooltip);
	    	                content.getChildren().add(flagLabel);
    	                }
    	                if (question.getIsHidden() && question.getReplyID() != -1) {
    	                	questionContent.setStyle("-fx-text-fill: grey; -fx-padding: 0 0 0 40px;");
    	                }
    	                else if (question.getIsHidden() && question.getReplyID() == -1) {
    	                	questionContent.setStyle("-fx-text-fill: grey;");
    	                }
    	                setGraphic(content);
    	            }
    	        };
    	    }
    	});
    	
    	// If "Flag selected Question / Question Reply" button is clicked
    	questionFlagButton.setOnAction(q -> {
    		clearAllErrors();
    		Question questionToFlag = submittedQuestionsList.getSelectionModel().getSelectedItem();
    		if (questionToFlag == null) {
    			questionListViewErrorLabel.setVisible(true);
    			questionListViewErrorLabel.setText("No Question or Question Reply selected to flag.");
    		}
    		else {
    			questionListViewErrorLabel.setVisible(false);
    			TextInputDialog flagReasonDialog = new TextInputDialog(questionToFlag.getReasonIsFlagged());
    			if (questionToFlag.getReplyID() == -1) {
    				flagReasonDialog.setTitle("Flag Question");
    			}
    			else if (questionToFlag.getReplyID() != -1 ) {
    				flagReasonDialog.setTitle("Flag Question Reply");
    			}
    			flagReasonDialog.setHeaderText(null);
    			flagReasonDialog.setContentText("Enter reason for flag:");
    			flagReasonDialog.showAndWait().ifPresent(reasonIsFlagged -> {
    				if (reasonIsFlagged.trim().isEmpty()) {
    					Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    					errorAlert.setTitle("Invalid Input");
    					errorAlert.setHeaderText(null);
    					errorAlert.setContentText("Reason for flag cannot be empty.");
    					errorAlert.showAndWait();
    				}
    				else {
	    				int index = allQuestionsObservable.indexOf(questionToFlag);
	        			if (questionToFlag.getReplyID() == -1) {
	        				Question questionFlagObj = new Question(questionToFlag.getStudentFirstName(), questionToFlag.getStudentLastName(), questionToFlag.getQuestionTitle(), questionToFlag.getQuestionBody());
	        				questionFlagObj.setQuestionID(questionToFlag.getQuestionID());
	        				questionFlagObj.setReasonIsFlagged(reasonIsFlagged);
	        				databaseHelper.markQuestionFlagged(questionFlagObj);
	        			}
	        			else {
	        				Question questionReplyToFlagObj  = new Question(questionToFlag.getReplyID(), questionToFlag.getStudentFirstName(), questionToFlag.getStudentLastName(), questionToFlag.getQuestionReply(), questionToFlag.getReplyingTo());
	        				questionReplyToFlagObj.setReplyID(questionToFlag.getReplyID());
	        				questionReplyToFlagObj.setReasonIsFlagged(reasonIsFlagged);
	        				databaseHelper.markQuestionReplyFlagged(questionReplyToFlagObj);
	        			}
	        			questionToFlag.setIsFlagged(true);
	        			questionToFlag.setReasonIsFlagged(reasonIsFlagged);
	        			allQuestionsObservable.set(index, questionToFlag);
	        			submittedQuestionsList.refresh();
    				}
    			});
    		}
    	});
    	
    	// Hide selected Question
    	hideSelectedQuestion.setOnAction(q -> {
    		clearAllErrors();
    		Question questionToHide = submittedQuestionsList.getSelectionModel().getSelectedItem();
    		if (questionToHide == null) {
    			questionListViewErrorLabel.setVisible(true);
    			questionListViewErrorLabel.setText("No Question or Question Reply selected to hide.");
    		}
    		else {
    			questionListViewErrorLabel.setVisible(false);
    			int index = allQuestionsObservable.indexOf(questionToHide);
    			if (questionToHide.getReplyID() == -1) {
    				databaseHelper.hideQuestion(questionToHide);
    			}
    			else {
    				ObservableList<Question> items = allQuestionsObservable;
    				int parentQuestionIndex = items.indexOf(questionToHide);
    				databaseHelper.hideQuestionReply(questionToHide);
    				if (parentQuestionIndex != -1) {
    	                items.set(parentQuestionIndex + 1, questionToHide);
    	            }
    			}
    			questionToHide.setIsHidden(true);
    			allQuestionsObservable.set(index, questionToHide);
    			submittedQuestionsList.refresh();
    		}
    	});

    	// ANSWERS DISPLAY AND FILTERS
    	// Load AnswerList upon opening
    	ListView<Answer> submittedAnswersList = new ListView<>();
    	Answers initialAnswersList;
    	initialAnswersList = new Answers(databaseHelper, user);
    	ObservableList<Answer> allAnswersObservable = FXCollections.observableArrayList();
    	allAnswersObservable.addAll(databaseHelper.getAllAnswersEvenHidden(user));

    	// DEBUGGING
    	ArrayList<Answer> answersFromDB = databaseHelper.getAllAnswersEvenHidden(user);
    	
    	System.out.println("Fetched answers from database:");
    	for (Answer answer : answersFromDB) {
    	    System.out.println("Answer ID: " + answer.getAnswerID() + 
    	                       ", Question ID: " + answer.getQuestionID() + 
    	                       ", Is Resolved: " + answer.getIsResolved() + 
    	                       ", Answer Text: " + answer.getAnswerText() +
    	                       ", IsFlagged: " + answer.getIsFlagged());
    	}
    	
    	// Apply the submittedAnswersList to the Filtered List
    	FilteredList<Answer> filteredAnswersSearch = new FilteredList<>(allAnswersObservable, a -> true);
    	submittedAnswersList.setItems(filteredAnswersSearch);
    	submittedAnswersList.setPrefWidth(425);

    	//List of answer filtering options
    	ObservableList<String> answersFilterOptions = FXCollections.observableArrayList();
    	answersFilterOptions.add("Not Resolving");
    	answersFilterOptions.add("Resolving");
    	answersFilterOptions.add("All Answers");

    	//Answer filtering selection box
    	ChoiceBox<String> answersFilterChoice = new ChoiceBox<String>(answersFilterOptions);

    	// Filters all Answers by selected Question
    	answersFilterButton.setOnAction(a -> {														
    		ArrayList<Answer> filteredAnswers = new ArrayList<Answer>();	

    		//First filters by selection
    		String filter = answersFilterChoice.getValue();
    		
    		// Returns answers to selected question
    		if(submittedQuestionsList.getSelectionModel().getSelectedItem() != null) {
    			ArrayList<Answer> selectedAnswers = databaseHelper.getAnswersByQuestionIDEvenHidden(submittedQuestionsList.getSelectionModel().getSelectedItem().getQuestionID());
    			switch(filter) {
    			// Answers not marked Resolved for the selected Question
    			case "Not Resolving":
    				for(int i = 0; i < selectedAnswers.size(); i++) {
    					if(!selectedAnswers.get(i).getIsResolved()) {
    						filteredAnswers.add(selectedAnswers.get(i));
    					}
    				}
    				break;
    			// All Answers marked Resolved for the selected Question
    			case "Resolving":
    				for(int i = 0; i < selectedAnswers.size(); i++) {
    					if(selectedAnswers.get(i).getIsResolved()) {
    						filteredAnswers.add(selectedAnswers.get(i));
    					}
    				}
    				break;
    			// All Answers for the selected Question
    			case "All Answers":
    				filteredAnswers = selectedAnswers;
    				break;
    				
    			default:
    				filteredAnswers = selectedAnswers; 
    				break;
    		}
    		}
    		//Returns all Answers with no Question selected
    		else {
    			switch(filter) {
    				// All Answers not marked Resolved
    				case "Not Resolving":
    					filteredAnswers = databaseHelper.getUnresolvedAnswersEvenHidden();
    					break;
    				// All Answers marked Resolved
    				case "Resolving":
    					filteredAnswers = databaseHelper.getResolvedAnswersEvenHidden();
    					break;
    				// All Answers Resolved and Unresolved
    				case "All Answers":
    					filteredAnswers = databaseHelper.getAllAnswersEvenHidden(user);
    					break;
    				// All Answers Resolved and Unresolved
    				default:
    					filteredAnswers = databaseHelper.getAllAnswersEvenHidden(user);
    					break;
    			}
    		}
    		// Enable keyword search to further filter results displayed by selected filter
    		String keyword = answersKeywordField.getText();
    		ArrayList<Answer> filteredAnswersKey = new ArrayList<Answer>();
    		
    		 if(keyword.length() > 0) {
    			 for (Answer answer : filteredAnswers) {
    				if (answer.getAnswerText().toLowerCase().contains(keyword.toLowerCase()) ||
    					answer.getStudentFirstName().toLowerCase().contains(keyword.toLowerCase()) ||
    	    			answer.getStudentLastName().toLowerCase().contains(keyword.toLowerCase())) {
    					filteredAnswersKey.add(answer);
    				}
    			 }
    		 }
    		 else {
    			 filteredAnswersKey = filteredAnswers;
    		 }
    		 //Displays filtered list
    		 ObservableList<Answer> filteredAnswersObservable = FXCollections.observableArrayList(filteredAnswersKey);
    		 submittedAnswersList.setItems(filteredAnswersObservable);
    	});

    	// Enable only Answer keyword searching to filter list (no filter selected)
    	answersKeywordField.textProperty().addListener((observable, oldValue, newValue) -> {
    		filteredAnswersSearch.setPredicate(answer -> {
    			if (newValue == null || newValue.isEmpty()) {
    				return true;
    			}
    			String convertToLowerCase = newValue.toLowerCase();
    			return answer.getAnswerText().toLowerCase().contains(convertToLowerCase) ||
    					answer.getStudentFirstName().toLowerCase().contains(convertToLowerCase) ||
    					answer.getStudentLastName().toLowerCase().contains(convertToLowerCase);
    		});
    		submittedAnswersList.setItems(filteredAnswersSearch);
    	});

    	// CellFactory to display formatted Answers in the ListView
    	submittedAnswersList.setCellFactory(new Callback<ListView<Answer>, ListCell<Answer>>() {
    	    @Override
    	    public ListCell<Answer> call(ListView<Answer> param) {
    	        return new ListCell<Answer>() {
    	        	private Label answerContent;
    	        	private HBox content;
    	        	{
    	        		answerContent = new Label();
    	        		content = new HBox(10, answerContent);
    	        		content.setAlignment(Pos.CENTER_LEFT);
    	        	}
    	            @Override
    	            protected void updateItem(Answer answer, boolean empty) {
    	                super.updateItem(answer, empty);
    	                if (empty || answer == null) {
    	                    setText(null);
    	                    setGraphic(null);
    	                } else {
    	                	String formattedText = String.format("Author: %s\nAnswer: %s", 
    	                			answer.getStudentFirstName() + " " + answer.getStudentLastName(),
    	                            answer.getAnswerText());
    	                			answerContent.setText(formattedText);
    	                			answerContent.setStyle("-fx-padding: 0 0 0 0;");
    	                		if (answer.getIsResolved()) {
    	                			Label checkmarkLabel = new Label("✔️");
    	                    		checkmarkLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: green;");
    	                    		content.getChildren().removeIf(node -> node instanceof Label && ((Label) node).getText().equals("✔️"));
    	                    		content.getChildren().removeIf(node -> node instanceof Label && ((Label) node).getText().equals("🚩"));
    	                    		content.getChildren().add(0, checkmarkLabel);
    	                		}
    	                		if (answer.getIsFlagged()) {
    	                			Label flagLabel = new Label("🚩");
    	                			flagLabel.setStyle("-fx-padding: 5;");
    	                			content.getChildren().removeIf(node -> node instanceof Label && ((Label) node).getText().equals("🚩"));
    	                			Tooltip tooltip = new Tooltip(answer.getReasonIsFlagged());
    	                			tooltip.setStyle("-fx-font-size: 12px;");
    	                			Tooltip.install(flagLabel, tooltip);
    	                        	content.getChildren().add(flagLabel);
    	                		}
    	                		if (answer.getIsHidden()) {
    	                			answerContent.setStyle("-fx-text-fill: grey;");
    	                		}
    	                		setGraphic(content);
    	                }
    	            }
    	        };
    	    }
    	});
    	
    	// If "Flag selected Answer" is clicked
    	answerFlagButton.setOnAction(a -> {
    		clearAllErrors();
    		Answer answerToFlag = submittedAnswersList.getSelectionModel().getSelectedItem();
    		if (answerToFlag == null) {
    			answerListViewErrorLabel.setVisible(true);
    			answerListViewErrorLabel.setText("No Answer selected to flag.");
    		}
    		else {
    			answerListViewErrorLabel.setVisible(false);
    			TextInputDialog flagReasonDialog = new TextInputDialog(answerToFlag.getReasonIsFlagged());
    			flagReasonDialog.setTitle("Flag Answer");
    			flagReasonDialog.setHeaderText(null);
    			flagReasonDialog.setContentText("Enter reason for flag:");
    			
    			flagReasonDialog.showAndWait().ifPresent(reasonIsFlagged -> {
    				if (reasonIsFlagged.trim().isEmpty()) {
    					Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    					errorAlert.setTitle("Invalid Input");
    					errorAlert.setHeaderText(null);
    					errorAlert.setContentText("Reason for flag cannot be empty.");
    					errorAlert.showAndWait();
    				}
    				else {
	    				int index = allAnswersObservable.indexOf(answerToFlag);
	        			Answer answerFlagObj = new Answer(answerToFlag.getStudentFirstName(), answerToFlag.getStudentLastName(), answerToFlag.getAnswerText());
	        			answerFlagObj.setAnswerID(answerToFlag.getAnswerID());
	        			answerFlagObj.setReasonIsFlagged(reasonIsFlagged);
	        			databaseHelper.markAnswerFlagged(answerFlagObj);
	        			answerToFlag.setIsFlagged(true);
	        			answerToFlag.setReasonIsFlagged(reasonIsFlagged);
	        			allAnswersObservable.set(index, answerToFlag);
	        			submittedAnswersList.refresh();
    				}
    			});
    		}
    	});
    	
    	// Submit a new answer
    	submitAnswer.setOnAction(a -> {
    		clearAllErrors();
    		// set the clicked on question within the submittedQuestionsList to questionToAnswer
    		Question questionToAnswer = submittedQuestionsList.getSelectionModel().getSelectedItem();
    		
    		// user has not clicked on a question to associate the answer with, do not add answer to database
    		if (questionToAnswer == null) {
				answerErrorLabel.setText("No Question was selected to answer.");
				answerErrorLabel.setVisible(true);
    		}
    		else {
    			answerErrorLabel.setVisible(false);
    			answerTextField.setVisible(true);
    			
    			Stage submitNewAnswerPopUpStage = new Stage();
        		submitNewAnswerPopUpStage.setTitle("Submit New Answer");
        		
        		Button saveNewAnswer = new Button("Save Answer");
        		
        		saveNewAnswer.setOnAction(s -> {
        			String answerText = answerTextField.getText();
    		    	Answer newAnswer = new Answer(user.getFirstName(), user.getLastName(), answerText, user);
    		    	String isAnswerValid = newAnswer.checkAnswerInput(answerText);
    		    	
    		    	// Check validity of answerText via call to below functions in Answer.java
    		    	// answerText has invalid input, so set the label to the returned error message from checkAnswerInput()
    		    	if (!isAnswerValid.equals("")) {
    		    		answerTextInvalidInput.setVisible(true);
    		    		answerTextInvalidInput.setText(isAnswerValid);
    		    	}
    		    	// input is valid, add the answer to the database
    		    	else if (isAnswerValid.equals("")) {
    		    		answerTextInvalidInput.setVisible(false);
    		        	int answerID = initialAnswersList.addAnswer(answerText, newAnswer, user, questionToAnswer.getQuestionID());
    		        	
    		        	newAnswer.setAnswerID(answerID);
    		        	int questionID = questionToAnswer.getQuestionID();
    		        	newAnswer.setQuestionID(questionID);
    		        	newAnswer.setStudentUserName(user.getUserName());
    		        	newAnswer.setStudentFirstName(user.getFirstName());
    		        	newAnswer.setStudentLastName(user.getLastName());
    		        	newAnswer.setAnswerText(answerText);
    		        	newAnswer.setIsAnswerUnread(true);
    		        	newAnswer.setIsResolved(false);
    		        	
    		        	allAnswersObservable.add(newAnswer);
    		        	answerTextField.clear();
    		        	submitNewAnswerPopUpStage.close();
    		    	}
        		});	
    		VBox submitNewAnswerVBox = new VBox(10);
    		submitNewAnswerVBox.setPadding(new Insets(20));
    		submitNewAnswerVBox.getChildren().addAll(answerTextField, saveNewAnswer, answerTextInvalidInput);
    		
    		Scene submitNewAnswerPopUpScene = new Scene(submitNewAnswerVBox, 600, 300);
    		submitNewAnswerPopUpStage.setScene(submitNewAnswerPopUpScene);
    		submitNewAnswerPopUpStage.initModality(Modality.APPLICATION_MODAL);
    		submitNewAnswerPopUpStage.showAndWait();
    		}
    	});
    	
    	// Hide selected Answer
    	hideSelectedAnswer.setOnAction(a -> {
    		clearAllErrors();
    		Answer answerToHide = submittedAnswersList.getSelectionModel().getSelectedItem();
    		if (answerToHide == null) {
    			answerListViewErrorLabel.setVisible(true);
    			answerListViewErrorLabel.setText("No Answer selected to hide.");
    		}
    		else {
    			answerListViewErrorLabel.setVisible(false);
    			int index = allAnswersObservable.indexOf(answerToHide);
    			databaseHelper.hideAnswer(answerToHide);
    			answerToHide.setIsHidden(true);
    			allAnswersObservable.set(index, answerToHide);
    			submittedAnswersList.refresh();
    		}
    	});

    	// REVIEWS DISPLAY AND FILTERS
    	// Load ReviewList upon opening
    	ListView<Review> submittedReviewsList = new ListView<>();
    	ObservableList<Review> allReviewsObservable;
    	Reviews initialReviewsList = new Reviews(databaseHelper, user);
    	allReviewsObservable = FXCollections.observableArrayList();
    	allReviewsObservable.addAll(databaseHelper.getAllReviewsEvenHidden(user));

    	// DEBUGGING
    	ArrayList<Review> reviewsFromDB = databaseHelper.getAllReviewsEvenHidden(user);

    	System.out.println("Fetched reviews from database:");
    	for (Review review : reviewsFromDB) {
    	    System.out.println("Review ID: " + review.getReviewID() + 
    	    				   ", Answer ID: " + review.getAnswerID() +
    	                       ", Question ID: " + review.getQuestionID() + 
    	                       ", Reviewer userName: " + review.getReviewerUserName() + 
    	                       ", Reviewer First Name: " + review.getReviewerFirstName() + 
    	                       ", Reviewer Last Name: " + review.getReviewerLastName() + 
    	                       ", Previous Review ID: " + review.getPrevReviewID() + 
    	                       ", Review Text: " + review.getReviewBody());
    	}

    	// Apply the submittedReviewsList to the FilteredList
    	FilteredList<Review> filteredReviewsSearch = new FilteredList<>(allReviewsObservable, a -> true);
    	submittedReviewsList.setItems(filteredReviewsSearch);
    	submittedReviewsList.setPrefWidth(425);

    	// List of review filtering options
    	ObservableList<String> reviewsFilterOptions = FXCollections.observableArrayList();
    	reviewsFilterOptions.add("None");
    	reviewsFilterOptions.add("Reviews for selected Answer");
    	reviewsFilterOptions.add("Reviews for Answer tied to selected Question");

    	// Review filtering selection box
    	ChoiceBox<String> reviewsFilterChoice = new ChoiceBox<String>(reviewsFilterOptions);

    	reviewsFilterButton.setOnAction(a -> {
    		reviewFiltersLabel.setVisible(false);
    		ArrayList<Review> filteredReviews = new ArrayList<Review>();
    		
    		// First filters by selection
    		String filter = reviewsFilterChoice.getValue();

    		ArrayList<Review> selectedReviewsByAnswer = new ArrayList<Review>();
    		ArrayList<Review> selectedReviewsByQuestion = new ArrayList<Review>();
    		
    		// questionID associated with the question selected from the ListView
    		int questionIDSelected;
    					
    		// ArrayList of all the answerIDs associated with a specific questionID
    		ArrayList<Integer> answerIDsForQuestion;
    		
    		// Returns reviews to selected question or answer
    		if (submittedAnswersList.getSelectionModel().getSelectedItem() != null || submittedQuestionsList.getSelectionModel().getSelectedItem() != null) {
    			
    			// Answer selected from submittedAnswersList so pull the corresponding review by the answerID
    			if (submittedAnswersList.getSelectionModel().getSelectedItem() != null) {
    				 selectedReviewsByAnswer = databaseHelper.getReviewByAnswerIDEvenHidden(submittedAnswersList.getSelectionModel().getSelectedItem().getAnswerID());
    			}
    			// Question selected from submittedQuestions list, so pull the answerID associated to the question and then the corresponding review by the answerID
    			else if (submittedQuestionsList.getSelectionModel().getSelectedItem() != null){
    				questionIDSelected = submittedQuestionsList.getSelectionModel().getSelectedItem().getQuestionID();
    				// Get all the answerIDs tied to the selected question
    				answerIDsForQuestion = databaseHelper.getAnswerIDsForQuestion(questionIDSelected);
    				// Loop through the returned Integer ArrayList and store each review in the selectedReviewByQuestion ArrayList for filtering
    				for (int i = 0; i < answerIDsForQuestion.size(); i++) {
    					Integer answerIDForQuestion = answerIDsForQuestion.get(i);
    					selectedReviewsByQuestion.addAll(databaseHelper.getReviewByAnswerIDEvenHidden(answerIDForQuestion));
    				}
    			}
    			switch(filter) {
    			case "None":
    				reviewFiltersLabel.setVisible(false);
    				filteredReviews = databaseHelper.getAllReviewsEvenHidden(user);
    				break;
    			// Answer selected, display any reviews associated with answer
    			case "Reviews for selected Answer":
    				if (submittedAnswersList.getSelectionModel().getSelectedItem() == null) {
    					reviewFiltersLabel.setVisible(true);
    					reviewFiltersLabel.setText("No answer selected, filter will display no results");
    					break;
    				}
    				else {
    					reviewFiltersLabel.setVisible(false);
    				}
    				filteredReviews = selectedReviewsByAnswer;
    				break;
    			// Question selected, display any reviews associated with answer tied to question
    			case "Reviews for Answer tied to selected Question":
    				if (submittedQuestionsList.getSelectionModel().getSelectedItem() == null) {
    					reviewFiltersLabel.setVisible(true);
    					reviewFiltersLabel.setText("No question selected, filter will display no results");
    					break;
    				}
    				else {
    					reviewFiltersLabel.setVisible(false);
    				}
    				questionIDSelected = submittedQuestionsList.getSelectionModel().getSelectedItem().getQuestionID();
    				answerIDsForQuestion = databaseHelper.getAnswerIDsForQuestion(questionIDSelected);
    				for (int i = 0; i < answerIDsForQuestion.size(); i++) {
    					Integer answerIDForQuestion = answerIDsForQuestion.get(i);
    					selectedReviewsByQuestion.addAll(databaseHelper.getReviewByAnswerIDEvenHidden(answerIDForQuestion));
    				}
    				filteredReviews = selectedReviewsByQuestion;
    				break;
    			// Display all reviews
    			default:
    				reviewFiltersLabel.setVisible(false);
    				filteredReviews = databaseHelper.getAllReviewsEvenHidden(user);
    				break;
    			}	
    		}	
    		else {
    			switch(filter) {
    			// No question or answer chosen, display all reviews
    			case "None":
    				reviewFiltersLabel.setVisible(false);
    				filteredReviews = databaseHelper.getAllReviewsEvenHidden(user);
    				break;
    			// No question or answer chosen, display all reviews
    			case "Reviews for selected Answer":
    				reviewFiltersLabel.setVisible(true);
    				reviewFiltersLabel.setText("With no answer specified, filter displays all reviews");
    				filteredReviews = databaseHelper.getAllReviewsEvenHidden(user);
    				break;
    			// No question or answer chosen, display all reviews
    			case "Reviews for Answer tied to selected Question":
    				reviewFiltersLabel.setVisible(true);
    				reviewFiltersLabel.setText("With no question specified, filter displays all reviews");
    				filteredReviews = databaseHelper.getAllReviewsEvenHidden(user);
    				break;
    			default:
    				reviewFiltersLabel.setVisible(false);
    				filteredReviews = databaseHelper.getAllReviewsEvenHidden(user);
    				break;
    			}	
    		}
    		
    		// Enable keyword search to further filter results displayed by selected filter
    		String keyword = reviewsKeywordField.getText();
    		ArrayList<Review> filteredReviewsKey = new ArrayList<Review>();
    	    
    	    if(keyword.length() > 0) {
    	             for (Review review : filteredReviews) {
    	                    if (review.getReviewBody().toLowerCase().contains(keyword.toLowerCase()) ||
    	                            review.getReviewerFirstName().toLowerCase().contains(keyword.toLowerCase()) ||
    	                    review.getReviewerLastName().toLowerCase().contains(keyword.toLowerCase())) {
    	                    	filteredReviewsKey.add(review);
    	                    }
    	             }
    	     }
    	     else {
    	    	 filteredReviewsKey = filteredReviews;
    	     }
    	     //Displays filtered list
    	     ObservableList<Review> filteredReviewsObservable = FXCollections.observableArrayList(filteredReviewsKey);
    	     submittedReviewsList.setItems(filteredReviewsObservable);
    	});

    	// Enable only Review keyword searching to filter list (no filter selected)
    	reviewsKeywordField.textProperty().addListener((observable, oldValue, newValue) -> {
    		filteredReviewsSearch.setPredicate(review -> {
    	                if (newValue == null || newValue.isEmpty()) {
    	                        return true;
    	                }
    	                String convertToLowerCase = newValue.toLowerCase();
    	                return review.getReviewBody().toLowerCase().contains(convertToLowerCase) ||
    	                                review.getReviewerFirstName().toLowerCase().contains(convertToLowerCase) ||
    	                                review.getReviewerLastName().toLowerCase().contains(convertToLowerCase);
    	        });
    	});
    	submittedReviewsList.setItems(filteredReviewsSearch);

    	// CellFactory to display formatted Reviews in the ListView
    	submittedReviewsList.setCellFactory(new Callback<ListView<Review>, ListCell<Review>>() {
    	    @Override
    	    public ListCell<Review> call(ListView<Review> param) {
    	        return new ListCell<Review>() {
    	            
    	            private HBox content;
    	            private Label reviewContent;
    	            {
    	                reviewContent = new Label();
    	                content = new HBox(10, reviewContent);
    	                content.setAlignment(Pos.CENTER_LEFT);
    	            }
    	            @Override
    	            protected void updateItem(Review review, boolean empty) {
    	                super.updateItem(review, empty);
    	                if (empty || review == null) {
    	                    setGraphic(null);
    	                    setText(null);
    	                } 
    	                else {
    	                    // Display review details
    	                	String formattedText = String.format("Author: %s\nReview: %s", 
    	                            review.getReviewerFirstName() + " " + review.getReviewerLastName(),
    	                            review.getReviewBody());
    	                	reviewContent.setText(formattedText);
    	           
    	                	// if the current Review is a review which just not have any clones, just display with no indent
    	                	if (review.getPrevReviewID() == -1) {
    	                		reviewContent.setStyle("-fx-padding: 0 0 0 0px;");
    	                		content.getChildren().removeIf(node -> node instanceof Label && ((Label) node).getText().equals("🚩"));
    	                	}
    	                	// if the current Review is a Review cloned from another Review indent it
    	                	else if (review.getPrevReviewID() != -1) {
    	                		reviewContent.setStyle("-fx-padding: 0 0 0 20px;");
    	                		content.getChildren().removeIf(node -> node instanceof Label && ((Label) node).getText().equals("🚩"));
    	                	}
    	                	if (review.getIsFlagged()) {
    	                		Label flagLabel = new Label("🚩");
    	                		flagLabel.setStyle("-fx-padding: 5;");
	                			content.getChildren().removeIf(node -> node instanceof Label && ((Label) node).getText().equals("🚩"));
	                			Tooltip tooltip = new Tooltip(review.getReasonIsFlagged());
	                			tooltip.setStyle("-fx-font-size: 12px;");
	                			Tooltip.install(flagLabel, tooltip);
	                        	content.getChildren().add(flagLabel);
    	                	}
    	                	if (review.getIsHidden()) {
    	                		reviewContent.setStyle("-fx-text-fill: grey;");
    	                	}
	                        setGraphic(content);
    	                }
    	            }
    	        };
    	    }
    	});
    	
    	// If "Flag selected Review is clicked"
    	reviewFlagButton.setOnAction(r -> {
    		clearAllErrors();
    		Review reviewToFlag = submittedReviewsList.getSelectionModel().getSelectedItem();
    		if (reviewToFlag == null) {
    			reviewListViewErrorLabel.setVisible(true);
    			reviewListViewErrorLabel.setText("No Review selected to flag.");
    		}
    		else {
    			reviewListViewErrorLabel.setVisible(false);
    			TextInputDialog flagReasonDialog = new TextInputDialog(reviewToFlag.getReasonIsFlagged());
    			flagReasonDialog.setTitle("Flag Review");
    			flagReasonDialog.setHeaderText(null);
    			flagReasonDialog.setContentText("Enter reason for flag:");
    			
    			flagReasonDialog.showAndWait().ifPresent(reasonIsFlagged -> {
    				if (reasonIsFlagged.trim().isEmpty()) {
    					Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    					errorAlert.setTitle("Invalid Input");
    					errorAlert.setHeaderText(null);
    					errorAlert.setContentText("Reason for flag cannot be empty.");
    					errorAlert.showAndWait();
    				}
    				else {
	    				int index = allReviewsObservable.indexOf(reviewToFlag);
	        			Review reviewFlagObj = new Review(reviewToFlag.getQuestionID(), reviewToFlag.getAnswerID(), reviewToFlag.getReviewBody(), reviewToFlag.getReviewerUserName(), reviewToFlag.getReviewerFirstName(), reviewToFlag.getReviewerLastName());
	        			reviewFlagObj.setReviewID(reviewToFlag.getReviewID());
	        			reviewFlagObj.setReasonIsFlagged(reasonIsFlagged);
	        			databaseHelper.markReviewFlagged(reviewFlagObj);
	        			reviewToFlag.setIsFlagged(true);
	        			reviewToFlag.setReasonIsFlagged(reasonIsFlagged);
	        			allReviewsObservable.set(index, reviewToFlag);
	        			submittedReviewsList.refresh();
    				}
    			});
    		}
    	});
    	
    	// Hide selected Review
    	hideSelectedReview.setOnAction(r -> {
    		clearAllErrors();
    		Review reviewToHide = submittedReviewsList.getSelectionModel().getSelectedItem();
    		if (reviewToHide == null) {
    			reviewListViewErrorLabel.setVisible(true);
    			reviewListViewErrorLabel.setText("No Review selected to hide.");
    		}
    		else {
    			reviewListViewErrorLabel.setVisible(false);
    			int index = allReviewsObservable.indexOf(reviewToHide);
    			databaseHelper.hideReview(reviewToHide);
    			reviewToHide.setIsHidden(true);
    			allReviewsObservable.set(index, reviewToHide);
    			submittedReviewsList.refresh();
    		}
    	});
    		
    	// PRIVATE MESSAGES DISPLAYS AND LISTENERS
    	// ListView for both messages between Students and other Students and Reviewers and Students
    	ListView<String> privateMessages = new ListView<>();

    	// Observable List for messages between Students and other Students
    	ObservableList<String> studentStudentMessageObservable = FXCollections.observableArrayList();
    	studentStudentMessageObservable.addAll(databaseHelper.getAllMessagesBetweenStudents());
    	
    	// Filtered list for messages between Students and other Students
    	FilteredList<String> filteredStudentStudentMessages = new FilteredList<>(studentStudentMessageObservable, s -> true);
    	
    	// FOR DEBUGGING
    	System.out.println("Student to Student");
    	ArrayList<String> messagesBetweenStudents = new ArrayList<>();
    	messagesBetweenStudents = databaseHelper.getAllMessagesBetweenStudents();
    	for(String message : messagesBetweenStudents) {
    		System.out.println(message);
    	}
    	
    	// Observable List for messages between Reviewers and Students
    	ObservableList<String> reviewerStudentMessageObservable = FXCollections.observableArrayList();
    	reviewerStudentMessageObservable.addAll(databaseHelper.getAllMessagesBetweenReviewersAndStudents());
    	
    	// Filtered List for messages between Reviewers and Students
    	FilteredList<String> filteredReviewerStudentMessages = new FilteredList<>(reviewerStudentMessageObservable, r -> true);
    	
    	// FOR DEBUGGING
    	System.out.println("Student to Reviewer and Reviewer to Student");
    	ArrayList<String> messagesBetweenReviewerStudents = new ArrayList<>();
    	messagesBetweenReviewerStudents = databaseHelper.getAllMessagesBetweenReviewersAndStudents();
    	for (String message : messagesBetweenReviewerStudents) {
    		System.out.println(message);
    	}
    	
    	// Enable listener to filter by user input keyword text
    	messagesKeywordField.textProperty().addListener((observableList, oldValue, newValue) -> {
    		String messageKeyword = newValue.toLowerCase();
    		filteredStudentStudentMessages.setPredicate(message -> message.toLowerCase().contains(messageKeyword));
    		filteredReviewerStudentMessages.setPredicate(message -> message.toLowerCase().contains(messageKeyword));
    	});
    	
    	// Display messages based on which tab is clicked
    	studentMessagesTabPane.getSelectionModel().selectedItemProperty().addListener((observableList, oldTab, newTab) -> {
    		if (newTab == studentStudentTab) {
    			privateMessages.setItems(filteredStudentStudentMessages);
    			
    			// Cell Factory for messages between two Students
    			privateMessages.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
    				@Override
    	    		public ListCell<String> call(ListView<String> param) {
    					return new ListCell<String>() {
    	    				@Override
    	    				protected void updateItem(String message, boolean empty) {
    	    					super.updateItem(message,  empty);
    	    					if (empty || message == null) {
    	    						setText(null);
    	    						setGraphic(null);
    	    					}
    	    					else {
    	    						String reasonIsFlagged = checkIfMessageIsFlagged(message, studentStudentTab);
    	    						Label messageContent = new Label(message);
    	    						HBox content = new HBox(5, messageContent);
        							content.setAlignment(Pos.CENTER_LEFT);
    	    						// Flagged
    	    						if (!reasonIsFlagged.equals("")) {
    	    							Label flagLabel = new Label("🚩");
    	    							flagLabel.setStyle("-fx-padding: 5;");
    	    							content.getChildren().removeIf(node -> node instanceof Label && ((Label) node).getText().equals("🚩"));
    	    							Tooltip tooltip = new Tooltip(reasonIsFlagged);
    	    							tooltip.setStyle("-fx-font-size: 12px;");
    	    							Tooltip.install(flagLabel, tooltip);
    	    							content.getChildren().add(flagLabel);
    	    						}
    	    						// Not flagged
    	    						else if (reasonIsFlagged.equals("")) {
    	    							content.getChildren().removeIf(node -> node instanceof Label && ((Label) node).getText().equals("🚩"));
    	    						}
    	    						setText(null);
	    							setGraphic(content);
    	    					}
    	    				}
    					};
    				}
    			});
    		}
    		else if (newTab == reviewersStudentTab) {
    			privateMessages.setItems(filteredReviewerStudentMessages);
    			
    			// Cell factory for messages between a Reviewer and a Student
    			privateMessages.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
    				@Override
    	    		public ListCell<String> call(ListView<String> param) {
    					return new ListCell<String>() {
    	    				@Override
    	    				protected void updateItem(String message, boolean empty) {
    	    					super.updateItem(message,  empty);
    	    					if (empty || message == null) {
    	    						setText(null);
    	    						setGraphic(null);
    	    					}
    	    					else {
    	    						String reasonIsFlagged = checkIfMessageIsFlagged(message, reviewersStudentTab);
    	    						Label messageContent = new Label(message);
    	    						HBox content = new HBox(5, messageContent);
        							content.setAlignment(Pos.CENTER_LEFT);
    	    						// Flagged
    	    						if (!reasonIsFlagged.equals("")) {
    	    							Label flagLabel = new Label("🚩");
    	    							flagLabel.setStyle("-fx-padding: 5;");
    	    							content.getChildren().removeIf(node -> node instanceof Label && ((Label) node).getText().equals("🚩"));
    	    							Tooltip tooltip = new Tooltip(reasonIsFlagged);
    	    							tooltip.setStyle("-fx-font-size: 12px;");
    	    							Tooltip.install(flagLabel, tooltip);
    	    							content.getChildren().add(flagLabel);
    	    						}
    	    						// Not flagged
    	    						else if (reasonIsFlagged.equals("")) {
    	    							content.getChildren().removeIf(node -> node instanceof Label && ((Label) node).getText().equals("🚩"));
    	    						}
    	    						setText(null);
	    							setGraphic(content);
    	    					}
    	    				}
    					};
    				}
    			});	
    		}
    	});
    	
    	// If "Flag selected Private Message" is clicked
    	privateMessageFlagButton.setOnAction(p -> {
    		clearAllErrors();
    		String messageToFlag = privateMessages.getSelectionModel().getSelectedItem();
    		Tab currentTab = studentMessagesTabPane.getSelectionModel().getSelectedItem();
    		String[] splitFormattedMessage;
    		DateTimeFormatter timeMessageSent = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
    		
    		if (messageToFlag == null) {
    			privateMessagesListViewErrorLabel.setVisible(true);
    			privateMessagesListViewErrorLabel.setText("No Private Message selected to flag.");
    		}
    		else {
    			privateMessagesListViewErrorLabel.setVisible(false);
    			TextInputDialog flagReasonDialog = new TextInputDialog();
    			flagReasonDialog.setHeaderText(null);
    			flagReasonDialog.setContentText("Enter reason for flag:");
    			
    			splitFormattedMessage = messageToFlag.split("\n");
    			
    			flagReasonDialog.showAndWait().ifPresent(reasonIsFlagged -> {
    				if (reasonIsFlagged.trim().isEmpty()) {
    					Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    					errorAlert.setTitle("Invalid Input");
    					errorAlert.setHeaderText(null);
    					errorAlert.setContentText("Reason for flag cannot be empty.");
    					errorAlert.showAndWait();
    				}
    				else {
    					if (currentTab == studentStudentTab) {
    	    				int index = studentStudentMessageObservable.indexOf(messageToFlag);
    	    				
    	    				String to = splitFormattedMessage[0].replace("To [Student]: ", "").trim();
    	    				String from = splitFormattedMessage[1].replace("From [Student]: ", "").trim();
    	    				String formattedDate = splitFormattedMessage[2].replace("Date: ", "").trim();
    	    				String messageSubject = splitFormattedMessage[3].replace("Message Subject: ", "").trim();
    	    				String messageBody = splitFormattedMessage[4].replace("Message Body: ", "").trim();
    	    				
    	    				LocalDateTime dateLocalDateTime = LocalDateTime.parse(formattedDate, timeMessageSent);
    	    				
    	    				// Flag message between two students
    	    				databaseHelper.markStudentPrivateMessageFlagged(reasonIsFlagged, to, from, dateLocalDateTime, messageSubject, messageBody);
    	    				studentStudentMessageObservable.set(index, messageToFlag);
    	    			}
    	    			else if (currentTab == reviewersStudentTab) {
    	    				int index = reviewerStudentMessageObservable.indexOf(messageToFlag);
    	    				String toRole = "";
    	    				String fromRole = "";
    	    				
    	    				// Determine if its a message from Reviewer to Student or Student to Reviewer to call the correct database function
    	    				for (String formattedMessage : splitFormattedMessage) {
    	    					if (formattedMessage.startsWith("To [")) {
    	    						toRole = formattedMessage.substring(formattedMessage.indexOf('[') + 1, formattedMessage.indexOf(']'));
    	    					}
    	    					else if (formattedMessage.startsWith("From [")) {
    	    						fromRole = formattedMessage.substring(formattedMessage.indexOf('[') + 1, formattedMessage.indexOf(']'));
    	    					}
    	    				}
    	    				// If From: Student, To Reviewer
    	    				if (toRole.equals("Reviewer") && fromRole.equals("Student")) {
    	    					String to = splitFormattedMessage[0].replace("To [Reviewer]: ", "").trim();
    		    				String from = splitFormattedMessage[1].replace("From [Student]: ", "").trim();
    		    				String formattedDate = splitFormattedMessage[2].replace("Date: ", "").trim();
    		    				String messageSubject = splitFormattedMessage[3].replace("Message Subject: ", "").trim();
    		    				String messageBody = splitFormattedMessage[4].replace("Message Body: ", "").trim();
    		    				
    		    				LocalDateTime dateLocalDateTime = LocalDateTime.parse(formattedDate, timeMessageSent);
    		    				
    		    				// Flag message from a Student to a Reviewer
    		    				databaseHelper.markStudentPrivateMessageFlagged(reasonIsFlagged, to, from, dateLocalDateTime, messageSubject, messageBody);
    	    				}
    	    				// If From: Reviewer, To Student
    	    				else if(toRole.equals("Student") && fromRole.equals("Reviewer")) {
    	    					String to = splitFormattedMessage[0].replace("To [Student]: ", "").trim();
    		    				String from = splitFormattedMessage[1].replace("From [Reviewer]: ", "").trim();
    		    				String formattedDate = splitFormattedMessage[2].replace("Date: ", "").trim();
    		    				String messageSubject = splitFormattedMessage[3].replace("Message Subject: ", "").trim();
    		    				String messageBody = splitFormattedMessage[4].replace("Message Body: ", "").trim();
    		    				
    		    				LocalDateTime dateLocalDateTime = LocalDateTime.parse(formattedDate, timeMessageSent);
    		    				
    		    				// Flag message from Reviewer to a Student
    		    				databaseHelper.markReviewerPrivateMessageFlagged(reasonIsFlagged, to, from, dateLocalDateTime, messageSubject, messageBody);
    	    				}
    	    				reviewerStudentMessageObservable.set(index, messageToFlag);
    	    			}
    	    			privateMessages.refresh();
    				}
    			});
    		}
    	});

    	// Button to return to the Staff HomePage
    	Button returnButton = new Button("Return to Staff Homepage");
    	// return to staffHomePage if button clicked
    	returnButton.setOnAction(r -> {
    		StaffHomePage staffHomePage = new StaffHomePage(databaseHelper, user);
    		staffHomePage.show(primaryStage, this.user);
    	});
    	
    	// Button to open the Student and Reviewer Users pop-up window
    	Button muteUser = new Button("Mute User");
    	
    	// Mute user
    	muteUser.setOnAction(m -> {
    		clearAllErrors();
    		Stage muteUserPopUpStage = new Stage();
    		muteUserPopUpStage.setTitle("Mute User With Student or Reviewer Role");
    		
    		// Load the list of all Student and Reviewer users
    		ListView<User> studentsAndReviewers = new ListView<>();
    		ObservableList<User> studentsAndReviewersObservable = FXCollections.observableArrayList();
    		studentsAndReviewersObservable.addAll(databaseHelper.getStudentAndReviewerUsers());
    		studentsAndReviewers.setItems(studentsAndReviewersObservable);
    		
    		// FOR DEBUGGING
    		ArrayList<User> usersFromDB = databaseHelper.getStudentAndReviewerUsers();

    		System.out.println("Fetched Student and Reviewer Users from database:");
    		for (User userList : usersFromDB) {
    		    System.out.println("First Name: " + userList.getFirstName() + 
    		                       ", Last Name: " + userList.getLastName() + 
    		                       ", Email: " + userList.getEmail());
    		}

    		// Cell Factory for Instructor and Staff user list
    		studentsAndReviewers.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
    			@Override
    			public ListCell<User> call(ListView<User> param) {
    				return new ListCell<User>() {
    					@Override
    					protected void updateItem(User user, boolean empty) {
    						super.updateItem(user,  empty);
    						if (empty || user == null) {
    							setText(null);
    						}
    						else {
    							String formattedText = String.format("UserName: %s\nName: %s\nEmail: %s\nIs User Muted?: %b",
    									user.getUserName(),
    									user.getFirstName() + " " + user.getLastName(),
    									user.getEmail(),
    									user.getIsMuted());
    							setText(formattedText);
    						}
    					}
    				};
    			}
    		});
    		
    		Button muteSelectedUser = new Button("Mute Selected User");
    		muteSelectedUser.setOnAction(s-> {
    			User selectedUser = studentsAndReviewers.getSelectionModel().getSelectedItem();
    			if (selectedUser == null) {
    				userErrorLabel.setVisible(true);
    				userErrorLabel.setText("No user selected to mute.");
    			}
    			else {
    				userErrorLabel.setVisible(false);
    				// Confirmation pop-up
    				Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    				alert.setTitle("Mute User Confirmation");
    				alert.setHeaderText("Please confirm you want to mute the selected user.");
    				alert.setContentText("Once complete, this action can only be undone by an Instructor.");

    				ButtonType muteConfirmation = new ButtonType("Confirm");
    				ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

    				alert.getButtonTypes().setAll(muteConfirmation, cancel);

    				alert.showAndWait().ifPresent(response -> {
    				    if (response == muteConfirmation) {
    				    	// Mute user
    	    				databaseHelper.muteUser(selectedUser);
    	    				selectedUser.setIsMuted(true);
    	    				studentsAndReviewers.refresh();
    	    				
    	    				// Hide all user content
    	    				databaseHelper.hideAllQuestionsForMutedUser(selectedUser);
    	    				databaseHelper.hideAllQuestionRepliesForMutedUser(selectedUser);
    	    				databaseHelper.hideAllAnswersForMutedUser(selectedUser);
    	    				databaseHelper.hideAllReviewsForMutedUser(selectedUser);
    	    				
    	    				// Mark all the questions submitted by muted user from submittedQuestions ListView in grey
    	    				for (Question question : allQuestionsObservable) {
    	    					if (question.getStudentUserName().equals(selectedUser.getUserName()) && question.getStudentFirstName().equals(selectedUser.getFirstName()) && question.getStudentLastName().equals(selectedUser.getLastName())) {
    	    						ObservableList<Question> items = allQuestionsObservable;
	    				            int parentQuestionIndex = items.indexOf(question);
    	    						if (question.getReplyID() == -1) {
        	    						allQuestionsObservable.set(allQuestionsObservable.indexOf(question), question);
    	    						}
    	    						else if (question.getReplyID() != 1) {
    	    				            if (parentQuestionIndex != -1) {
    	    				                items.set(parentQuestionIndex + 1, question);
    	    				            }
    	    						}
    	    						question.setIsHidden(true);
    	    						submittedQuestionsList.refresh();
    	    					}
    	    				}
		
    	    				// Mark all the answers submitted by muted user from submittedAnswers ListView in grey
    	    				for (Answer answer : allAnswersObservable) {
    	    					if (answer.getStudentUserName().equals(selectedUser.getUserName()) && answer.getStudentFirstName().equals(selectedUser.getFirstName()) && answer.getStudentLastName().equals(selectedUser.getLastName())) {
    	    						answer.setIsHidden(true);
    	    						allAnswersObservable.set(allAnswersObservable.indexOf(answer), answer);
    	    						submittedAnswersList.refresh();
    	    					}
    	    				}

    	    				// Mark all the questions submitted by muted user from submittedReviews ListView in grey
    	    				for (Review review : allReviewsObservable) {
    	    					if (review.getReviewerUserName().equals(selectedUser.getUserName()) && review.getReviewerFirstName().equals(selectedUser.getFirstName()) && review.getReviewerLastName().equals(selectedUser.getLastName())) {
    	    						review.setIsHidden(true);
    	    						allReviewsObservable.set(allReviewsObservable.indexOf(review), review);
    	    						submittedReviewsList.refresh();
    	    					}
    	    				}
    				    }
    				});
    				muteUserPopUpStage.close();
    			}
    		});
    		
    		VBox usersList = new VBox(10);
    		usersList.setPrefWidth(300);
    		usersList.setStyle("-fx-alignment: center; -fx-padding: 10; -fx-border-color: black; -fx-border-width: 1;");
    		usersList.setPadding(new Insets(20));
    		usersList.getChildren().addAll(studentsAndReviewers, muteSelectedUser, userErrorLabel);
    		
    		Scene muteUserPopUpScene = new Scene(usersList, 1000, 400);
    		muteUserPopUpStage.setScene(muteUserPopUpScene);
    		muteUserPopUpStage.initModality(Modality.APPLICATION_MODAL);
    		muteUserPopUpStage.showAndWait();
    	});
    	
    	// Add all the elements to the VBox's
    	questionList.getChildren().addAll(submittedQuestionsLabel, questionsKeywordField, questionsFilterChoice, questionsFilterButton, submittedQuestionsList, questionFlagButton, hideSelectedQuestion, questionErrorLabel, questionListViewErrorLabel, questionsAlignment);
    	answerList.getChildren().addAll(submittedAnswersLabel, answersKeywordField, answersFilterChoice, answersFilterButton, submittedAnswersList, submitAnswer, answerFlagButton, hideSelectedAnswer, answerListViewErrorLabel, answerErrorLabel);
    	messagesList.getChildren().addAll(messagesListLabel, messagesKeywordField, studentMessagesTabPane, privateMessages, privateMessageFlagButton, privateMessagesListViewErrorLabel);
    	reviewList.getChildren().addAll(submittedReviewsLabel, reviewsKeywordField, reviewsFilterChoice, reviewsFilterButton, submittedReviewsList, reviewFlagButton, hideSelectedReview, reviewFiltersLabel, reviewListViewErrorLabel, reviewsAlignment);
    	
    	// Add all the elements to the Top Header HBox
    	topHeader.getChildren().addAll(pageLabel);
    	
    	// Add all the elements to the Bottom Header HBox
    	bottomHeader.getChildren().addAll(returnButton, muteUser);
    	
    	// Add all the VBox's to the Border Pane in the appropriate positions
    	studentInteractionsBorderPane.setLeft(questionList);
    	studentInteractionsBorderPane.setCenter(centerBorderPaneVBoxes);
    	studentInteractionsBorderPane.setRight(messagesList);
    	studentInteractionsBorderPane.setTop(topHeader);
    	studentInteractionsBorderPane.setBottom(bottomHeader);
    	
    	// Add the tabs to the Tab Pane
    	studentMessagesTabPane.getTabs().addAll(studentStudentTab, reviewersStudentTab);
    	
    	// Set the scene using the Border Pane
	    Scene staffStudentInteractionView = new Scene(studentInteractionsBorderPane, 1775, 700);

	    // Set the scene to primary stage
	    primaryStage.setScene(staffStudentInteractionView);
	    primaryStage.setTitle("Student and Reviewer Interactions");
    }
    
    /**
    * The handleReply method validates text input by a user as a reply to a question once the "saveReply" button is clicked If the input is valid, 
    * the reply is added to the database and the questions ListView is updated to display the reply underneath its' parent question indented.
    * 
    * @param questionToReply the question object clicked on within the ListView to reply to
    */
    private void handleReply(Question questionToReply) {
    	clearAllErrors();
    	questionErrorLabel.setVisible(false);
    	replyTextField.setVisible(true);
    	
    	Stage submitNewQuestionReplyPopUpStage = new Stage();
		submitNewQuestionReplyPopUpStage.setTitle("Submit New Question Reply");
		
		Button saveNewQuestionReply = new Button("Save Question Reply");
		
		String replyingTo = "Replying to: " + questionToReply.getQuestionTitle();
		
		saveNewQuestionReply.setOnAction(s -> {
			String replyText = replyTextField.getText();
	        Question newQuestionReply = new Question(replyText, replyingTo, user);
	        String isQuestionReplyValid = newQuestionReply.checkQuestionReplyInput(replyText);
	        
	        // Validate the reply input
	        if (!isQuestionReplyValid.equals("")) {
	            questionReplyInvalidInput.setVisible(false);
	            questionReplyInvalidInput.setText(isQuestionReplyValid);
	            questionReplyInvalidInput.setVisible(true);
	        } 
	        else if (isQuestionReplyValid.equals("")){
	            questionReplyInvalidInput.setVisible(false);
	            int replyID = initialQuestionsList.addReply(replyText, questionToReply.getQuestionID(), newQuestionReply, user, replyingTo);
	            newQuestionReply.setReplyID(replyID);
	            newQuestionReply.setQuestionReply(replyText);
	            newQuestionReply.setStudentFirstName(user.getFirstName());
	            newQuestionReply.setStudentLastName(user.getLastName());
	            newQuestionReply.setStudentUserName(user.getUserName());
	            newQuestionReply.setReplyingTo(replyingTo);
	            newQuestionReply.setQuestionID(questionToReply.getQuestionID());

	            // Add the reply to the ListView, indented under the original question
	            ObservableList<Question> items = allQuestionsObservable;
	            int parentQuestionIndex = items.indexOf(questionToReply);
	            if (parentQuestionIndex != -1) {
	                items.add(parentQuestionIndex + 1, newQuestionReply);
	            }
	            replyTextField.clear();
	            submitNewQuestionReplyPopUpStage.close();
	        }
		});
		VBox submitNewQuestionReplyVBox = new VBox(10);
		submitNewQuestionReplyVBox.setPadding(new Insets(20));
		submitNewQuestionReplyVBox.getChildren().addAll(replyTextField, saveNewQuestionReply, questionReplyInvalidInput);
		
		Scene submitNewQuestionreplyPopUpScene = new Scene(submitNewQuestionReplyVBox, 600, 300);
		submitNewQuestionReplyPopUpStage.setScene(submitNewQuestionreplyPopUpScene);
		submitNewQuestionReplyPopUpStage.initModality(Modality.APPLICATION_MODAL);
		submitNewQuestionReplyPopUpStage.showAndWait();
    }
    
    /**
     * The checkIfMessageIsFlagged method is used within the privateMessages Cell Factory to determine whether a particular message
     * has true or false for its isFlagged attribute in the database and returns the reasonIsFlagged as String. If the message is not 
     * flagged, an empty string is returned. 
     * 
     * @param message the message whose flag status is being checked
     * @param currTab the current tab within the studentMessagesTabPane
     * @return a String with the user input reason for the flag of the specified private message or an empty string if not flagged
     */
    private String checkIfMessageIsFlagged(String message, Tab currTab) {
    	boolean isFlaggedStatus = false;
    	String reasonIsFlagged = "";
    	String[] splitFormattedMessage = message.split("\n");
    	DateTimeFormatter timeMessageSent = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
    	
    	if (currTab == studentStudentTab) {
    		String to = splitFormattedMessage[0].replace("To [Student]: ", "").trim();
			String from = splitFormattedMessage[1].replace("From [Student]: ", "").trim();
			String formattedDate = splitFormattedMessage[2].replace("Date: ", "").trim();
			String messageSubject = splitFormattedMessage[3].replace("Message Subject: ", "").trim();
			String messageBody = splitFormattedMessage[4].replace("Message Body: ", "").trim();
			
			LocalDateTime dateLocalDateTime = LocalDateTime.parse(formattedDate, timeMessageSent);
			isFlaggedStatus = databaseHelper.checkIfStudentPrivateMessageFlagged(to, from, dateLocalDateTime, messageSubject, messageBody);
			
			if (isFlaggedStatus) {
				reasonIsFlagged = databaseHelper.getReasonStudentPrivateMessageFlagged(to, from, dateLocalDateTime, messageSubject, messageBody);
			}
			else if (!isFlaggedStatus) {
				reasonIsFlagged = "";
			}
    	}
    	else if (currTab == reviewersStudentTab) {
    		String toRole = "";
			String fromRole = "";
			
			// Determine if its a message from Reviewer to Student or Student to Reviewer to call the correct database function
			for (String formattedMessage : splitFormattedMessage) {
				if (formattedMessage.startsWith("To [")) {
					toRole = formattedMessage.substring(formattedMessage.indexOf('[') + 1, formattedMessage.indexOf(']'));
				}
				else if (formattedMessage.trim().startsWith("From [")) {
					fromRole = formattedMessage.substring(formattedMessage.indexOf('[') + 1, formattedMessage.indexOf(']'));
				}
			}
			// If From: Student, To Reviewer
			if (toRole.equals("Reviewer") && fromRole.equals("Student")) {
				String to = splitFormattedMessage[0].replace("To [Reviewer]: ", "").trim();
				String from = splitFormattedMessage[1].replace("From [Student]: ", "").trim();
				String formattedDate = splitFormattedMessage[2].replace("Date: ", "").trim();
				String messageSubject = splitFormattedMessage[3].replace("Message Subject: ", "").trim();
				String messageBody = splitFormattedMessage[4].replace("Message Body: ", "").trim();
				
				LocalDateTime dateLocalDateTime = LocalDateTime.parse(formattedDate, timeMessageSent);
				isFlaggedStatus = databaseHelper.checkIfStudentPrivateMessageFlagged(to, from, dateLocalDateTime, messageSubject, messageBody);
				
				if (isFlaggedStatus) {
					reasonIsFlagged = databaseHelper.getReasonStudentPrivateMessageFlagged(to, from, dateLocalDateTime, messageSubject, messageBody);
				}
				else if (!isFlaggedStatus) {
					reasonIsFlagged = "";
				}
			}
			// If From: Reviewer, To Student
			else if(toRole.equals("Student") && fromRole.equals("Reviewer")) {
				String to = splitFormattedMessage[0].replace("To [Student]: ", "").trim();
				String from = splitFormattedMessage[1].replace("From [Reviewer]: ", "").trim();
				String formattedDate = splitFormattedMessage[2].replace("Date: ", "").trim();
				String messageSubject = splitFormattedMessage[3].replace("Message Subject: ", "").trim();
				String messageBody = splitFormattedMessage[4].replace("Message Body: ", "").trim();
				
				LocalDateTime dateLocalDateTime = LocalDateTime.parse(formattedDate, timeMessageSent);
				isFlaggedStatus = databaseHelper.checkIfReviewerPrivateMessageFlagged(to, from, dateLocalDateTime, messageSubject, messageBody);
				
				if (isFlaggedStatus) {
					reasonIsFlagged = databaseHelper.getReasonReviewerPrivateMessageFlagged(to, from, dateLocalDateTime, messageSubject, messageBody);
				}
				else if (!isFlaggedStatus) {
					reasonIsFlagged = "";
				}
			}
    	}
    	return reasonIsFlagged;
    }
    
    /**
     * Method sets all error labels to not visible.
     */
    private void clearAllErrors() {
    	questionListViewErrorLabel.setVisible(false);
    	questionErrorLabel.setVisible(false);
    	questionReplyInvalidInput.setVisible(false);
    	answerListViewErrorLabel.setVisible(false);
    	answerTextInvalidInput.setVisible(false);
    	answerErrorLabel.setVisible(false);
    	reviewListViewErrorLabel.setVisible(false);
    	privateMessagesListViewErrorLabel.setVisible(false);
    	reviewFiltersLabel.setVisible(false);
    }
}
