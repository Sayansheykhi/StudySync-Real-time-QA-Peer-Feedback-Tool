package test;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import application.Answer;
import application.Question;
import application.Review;
import application.User;
import databasePart1.DatabaseHelper;

/**
 * The StudentMessageFromQuestionsAnswersTP4Testing class represents an interface for performing JUnit testing on the StudentMessageFromQuestionsAnswers.java class for functions created during Team Project Phase 4.
 *
 * IMPORTANT: uncomment out Line 97 of DatabaseHelper.java before running
 * 
 * @author Cristina Hooe
 * @version 1.0 4/25/2025
 */
class StudentMessageFromQuestionsAnswersTP4Testing {
	
	/**
	 * DatabaseHelper object to call DatabaseHelper functions that need to be tested and connect to/ disconnect from the database
	 */
	static DatabaseHelper databaseHelper = new DatabaseHelper();
	
	/**
	 * Array of the test users provisioned roles where they are provisioned as a Student
	 */
	static boolean[] studentRole = {false, true, false, false, false};
	
	/**
	 * Array of the test users provisioned roles where they are provisioned as a Reviewer
	 */
	static boolean[] reviewerRole = {false, false, true, false, false};
	
	/**
	 * Test user Student to use for methods that require an argument of type User
	 */
	static User testUserStudent;
	
	/**
	 * Second Test user Student to use for methods that require an argument of type User
	 */
	static User testUserStudent2;
	
	/**
	 * Test user Reviewer to use for methods that require an argument of type User
	 */
	static User testUserReviewer;
	
	/**
	 * Question object specific to a question which is added to the database
	 */
	static Question newQuestion;
	
	/**
	 * questionID generated by the database when adding the test question
	 */
	static int questionID;
	
	/**
	 * Answer object specific to an answer which is added to the database
	 */
	static Answer newAnswer;
	
	/**
	 * answerID generated by the database when adding the test answer
	 */
	static int answerID;
	
	/**
	 * Review object specific to a review which is added to the database
	 */
	static Review newReview;
	
	/**
	 * reviewID generated by the database when adding the test review
	 */
	static int reviewID;
	
	

	/**
	 * Method connects to the database and initializes necessary general test objects.
	 * 
	 * @throws Exception SQLException "Failed to connect to database"
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		// Create a database connection
		try {
			databaseHelper.connectToDatabase();
		} catch (SQLException e) {
			System.out.println("Failed to connect to database");
		}
		
		// Instantiate student user
		testUserStudent = new User("testStudentUserName", studentRole, "testStudentEmail@asu.edu", "testStudentFirstName", "testStudentLastName", false);
		
		// Add user to the database
		databaseHelper.register(testUserStudent);
		
		// Instantiate 2nd student user
		testUserStudent2 = new User("testSecondStudentUserName", studentRole, "testSecondStudentEmail@asu.edu", "testSecondStudentFirstName", "testSecondStudentLastName", false);
		
		// Add user to the database
		databaseHelper.register(testUserStudent2);
		
		// Instantiate Reviewer user
		testUserReviewer = new User("testReviewerUserName", reviewerRole, "testReviewerEmail@asu.edu", "testReviewerFirstName", "testReviewerLastName", false);
		
		// Add user to the database
		databaseHelper.register(testUserReviewer);
		
		// Instantiate Question object
		newQuestion = new Question(-1, testUserStudent.getUserName(), testUserStudent.getFirstName(), testUserStudent.getLastName(), "Test Question Title", "Test Question Body", false, LocalDateTime.now(), false, "", false);
		
		// Add a test Question to the database
		questionID = databaseHelper.addQuestion(newQuestion.getQuestionTitle() , newQuestion.getQuestionBody(), newQuestion, testUserStudent);
		newQuestion.setQuestionID(questionID);
		
		// Instantiate Answer object
		newAnswer = new Answer(-1, questionID, testUserStudent2.getUserName(), testUserStudent2.getFirstName(), testUserStudent2.getLastName(), "Test Answer Text", true, false, LocalDateTime.now(), false, "", false);
				
		// Add a test Answer to the database
		answerID = databaseHelper.addAnswers(newAnswer.getAnswerText(), newAnswer, testUserStudent2, questionID);
		newAnswer.setAnswerID(answerID);
		
		// Instantiate Review object
		newReview = new Review(questionID, answerID, -1, -1, "Test Review Body", testUserReviewer.getUserName(), testUserReviewer.getFirstName(), testUserReviewer.getLastName(), false, "", false);
		
		// Add a test Review to the database
		reviewID = databaseHelper.addReview(testUserReviewer.getUserName(), testUserReviewer.getFirstName(), testUserReviewer.getLastName(), newReview.getReviewBody(), questionID, answerID, -1);
		newReview.setReviewID(reviewID);
	}

	/**
	 * Deletes the test objects from the database and closes the database connection.
	 */
	@AfterAll
	static void tearDownAfterClass() {
		
		// Delete 1st private message
		databaseHelper.deletePrivateMessage("1");
		
		// Delete 2nd private message
		databaseHelper.deletePrivateMessage("2");
		
		// Delete 3rd private message
		databaseHelper.deletePrivateMessage("3");
		
		// Delete 4th private message
		databaseHelper.deletePrivateMessage("4");
		
		// Delete Review
		databaseHelper.deleteReview(reviewID);
		
		// Delete Answer
		databaseHelper.deleteAnswer(answerID);
		
		// Delete Question
		databaseHelper.deleteQuestion(questionID);
		
		// Delete reviewer user from the database
		databaseHelper.deleteUser(testUserReviewer);
		
		// Delete 2nd student user from the database
		databaseHelper.deleteUser(testUserStudent2);
		
		// Delete student user from the database
		databaseHelper.deleteUser(testUserStudent);
		
		// Close the database connection
		databaseHelper.closeConnection();	
	}
	
	/**
	 * Test valid values for databaseHelper.sendPrivateMessageToStudent().
	 */
	@Test
	public void NormalTestSendPrivateMessageToStudent() {
		assertTrue(databaseHelper.sendPrivateMessageToStudent(testUserStudent.getUserName(), testUserStudent2.getUserName(), questionID, "Test Message Subject for Private Message from testUserStudent to testUserStudent2", "Test message body"),"Should return true for successful send of a Student to Student Private message");
		assertTrue(databaseHelper.sendPrivateMessageToStudent(testUserStudent2.getUserName(), testUserStudent.getUserName(), questionID, "Test Message Subject for Private Message from testUserStudent2 to testUserStudent", "Test message body"),"Should return true for successful send of a Student to Student Private message");
	}
	
	/**
	 * Test valid values for databaseHelper.sendPrivateMessageToReviewer().
	 */
	@Test
	public void NormalTestSendPrivateMessageToReviewer() {
		assertTrue(databaseHelper.sendPrivateMessageToReviewer(testUserStudent.getUserName(), testUserReviewer.getUserName(), reviewID, "Test Message Subject for Private Message from testUserStudent to testUserReviewer", "Test message body"),"Should return true for successful send of a Student to Reviewer Private message");
		assertTrue(databaseHelper.sendPrivateMessageToReviewer(testUserStudent2.getUserName(), testUserReviewer.getUserName(), reviewID, "Test Message Subject for Private Message from testUserStudent2 to testUserReviewer", "Test message body"),"Should return true for successful send of a Student to Reviewer Private message");
	}
	
	/**
	 * Test valid values for databaseHelper.getUnreadPrivateMessageCountByQuestion().
	 */
	@Test
	public void NormalTestGetUnreadPrivateMessageCountByQuestion() {
		assertEquals(1, databaseHelper.getUnreadPrivateMessageCountByQuestion(testUserStudent2.getUserName(), questionID), "Unread private message count should be 1 for message sent from testUserStudent to testUserStudent2 about questionID 1");
		assertEquals(1, databaseHelper.getUnreadPrivateMessageCountByQuestion(testUserStudent.getUserName(), questionID), "Unread private message count should be 1 for message sent from testUserStudent2 to testUserStudent about questionID 1");
	}
}
