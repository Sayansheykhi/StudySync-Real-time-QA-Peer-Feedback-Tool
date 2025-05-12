package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import databasePart1.DatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This page allows a student to view all questions, answers,and reviews and
 * send private messages to reviewers or other students.
 * 
 * This class has been enhanced in TP3 to support:
 * - Private messaging per question or review
 * - Clarification requests
 * - Display all questions, answers, and reviews
 * 
 * @author Kylie Kim
 * @author Cristina Hooe
 * @version 3.0
 * @version 4.0
 * @since 2025-03-27
 */
public class StudentMessageFromQuestionsAnswers {

    private DatabaseHelper dbHelper;
    private User user;

    private ListView<Question> submittedQuestionsList;
    private ListView<Answer> submittedAnswerList;
    private ListView<Review> submittedReviewsList;

    private TextArea replyTextField;
    private TextField subjectField;
    private Button saveReplyButton;
    private Label replyStatusLabel;
    
    /**
     * Constructs the StudentQuestionsAnswers page with a shared DatabaseHelper.
     *
     * @param databaseHelper the shared DatabaseHelper instance
     */
    public StudentMessageFromQuestionsAnswers(DatabaseHelper databaseHelper) {
        this.dbHelper = databaseHelper;
    }

    /**
     * Displays the Student Questions and Answers page for the given user.
     * 
     * @param primaryStage The stage to display the scene.
     * @param user The logged-in student user.
     */
    public void show(Stage primaryStage, User user) {
        this.user = user;

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label pageTitle = new Label("Send Private Feedback Message to another Student or Reviewer");
        pageTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        submittedQuestionsList = new ListView<>();
        submittedAnswerList = new ListView<>();
        submittedReviewsList = new ListView<>();

        loadSubmittedQuestions();
        loadSubmittedAnswers();
        loadSubmittedReviews();

        submittedQuestionsList.setPrefHeight(150);
        submittedAnswerList.setPrefHeight(150);
        submittedReviewsList.setPrefHeight(150);

        // Reply section for private messages or clarifications
        Label replyLabel = new Label("Compose Message: ");
        replyTextField = new TextArea();
        replyTextField.setPromptText("Type your message here...");
        replyTextField.setWrapText(true);
        replyTextField.setPrefHeight(200);
        
        subjectField = new TextField();
        subjectField.setPromptText("Type your message subject here...");
        

        saveReplyButton = new Button("Send Message");
        saveReplyButton.setOnAction(e -> sendPrivateMessage());

        replyStatusLabel = new Label();
        
        Button backButton = new Button("Back to Home");
        backButton.setOnAction(e -> {
            StudentHomePage homePage = new StudentHomePage(dbHelper);
            homePage.show(primaryStage, user);
        });

        root.getChildren().addAll(
            pageTitle,
            new Label("Submitted Questions:"),
            submittedQuestionsList,
            new Label("Potential Answers:"),
            submittedAnswerList,
            new Label("Reviews for Potential Answers:"),
            submittedReviewsList,
            replyLabel,
            subjectField,
            replyTextField,
            saveReplyButton,
            backButton,
            replyStatusLabel
        );

        Scene scene = new Scene(root, 600, 1000);
        primaryStage.setTitle("Student Message Dispatch");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Loads the student's submitted questions from the database.
     */
    private void loadSubmittedQuestions() {
        ArrayList<Question> questions = dbHelper.getAllQuestions(user);
        ObservableList<Question> observableQuestions = FXCollections.observableArrayList(questions);
        submittedQuestionsList.setItems(observableQuestions);
    }

    /**
     * Loads answers linked to the student's questions from the database.
     */
    private void loadSubmittedAnswers() {
        ArrayList<Answer> answers = dbHelper.getAllAnswers(user);
        ObservableList<Answer> observableAnswers = FXCollections.observableArrayList(answers);
        submittedAnswerList.setItems(observableAnswers);
    }
    
    /**
     * Loads reviews linked to potential answers to the student's questions from the database.
     */
    private void loadSubmittedReviews() {
    	ArrayList<Review> reviews = dbHelper.getAllReviews(user);
    	ObservableList<Review> observableReviews = FXCollections.observableArrayList(reviews);
        submittedReviewsList.setItems(observableReviews);
    }

    /**
     * Sends a private message or clarification request to the selected reviewer.
     * This message is stored in the database.
     */
    private void sendPrivateMessage() {
        Review selectedReview = submittedReviewsList.getSelectionModel().getSelectedItem();
        Question selectedQuestion = submittedQuestionsList.getSelectionModel().getSelectedItem();
        String subject = subjectField.getText().trim();
        String message = replyTextField.getText().trim();
        boolean success = false;

        if (selectedReview == null && selectedQuestion == null) {
        	replyStatusLabel.setStyle("-fx-text-fill: red;");
            replyStatusLabel.setText("Please select a Question or Review to message.");
            return;
        }

        // Message another student about a question
        else if (selectedReview == null && selectedQuestion!= null) {
        	if (message.isEmpty()) {
        		replyStatusLabel.setStyle("-fx-text-fill: red;");
                replyStatusLabel.setText("Message cannot be empty.");
                return;
            }
        	else {
        		String recipientStudentUserName = selectedQuestion.getStudentUserName();
        		System.out.println("recipientStudentUserName is: " + recipientStudentUserName);
        		success = dbHelper.sendPrivateMessageToStudent(user.getUserName(), recipientStudentUserName, selectedQuestion.getQuestionID(), subject, message);
        		submittedQuestionsList.refresh();
        	}
        	
        }
        // Message a reviewer about a review
        else if (selectedQuestion == null && selectedReview != null) {
        	if (message.isEmpty()) {
        		replyStatusLabel.setStyle("-fx-text-fill: red;");
                replyStatusLabel.setText("Message cannot be empty.");
                return;
            }
        	else {
        		String reviewerUserName = selectedReview.getReviewerUserName();
        		System.out.println("reviewerUserName is: " + reviewerUserName);
        		success = dbHelper.sendPrivateMessageToReviewer(user.getUserName(), reviewerUserName, selectedReview.getReviewID(), subject, message);

        	}
        }

        if (success) {
        	replyStatusLabel.setStyle("-fx-text-fill: green;");
            replyStatusLabel.setText("Message sent successfully!");
            subjectField.clear();
            replyTextField.clear();
            
        } else {
        	replyStatusLabel.setStyle("-fx-text-fill: red;");
            replyStatusLabel.setText("Failed to send message.");
        }
    }
}