package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.Answer;
import application.Question;
import application.StudentQuestionsAnswers;
import application.User;
import databasePart1.DatabaseHelper;
import java.util.ArrayList;

/**
 * The StudentMessageFromQuestionsAnswersTest class represents an interface for 
 * perform JUnit testing on StudentMessageFromQuestionsAnswers.Java
 */
public class StudentMessageFromQuestionsAnswersTest {
	
	/**
	 * Declaration of DatabaseHelper object for interacting directly with database.
	 */
    private DatabaseHelper dbHelper;
    
    /**
     * Declaration of a User object for testing.
     */
    private User testUser;
    
    /**
     * Declaration of the StudentQuestionAnswers test stage.
     */
    private StudentQuestionsAnswers qaPage;
    
    /**
     * Method initializes test objects.
     */
    @BeforeEach
    public void setUp() {
        dbHelper = new DatabaseHelper();
        testUser = new User("testUser", "pass", new boolean[]{false, true, false, false, false}, "test@example.com", "Test", "User");
        qaPage = new StudentQuestionsAnswers(dbHelper);
    }
    
    /**
     * Test valid execution of User.getSubmittedQuestions and User.getSubmittedAnswers
     */
    @Test
    public void testLoadSubmittedQuestionsAnswers() {
        ArrayList<Question> questions = testUser.getSubmittedQuestions();
        ArrayList<Answer> answers = testUser.getSubmittedAnswers();

        // Expecting no questions or answers yet for a fresh test user
        assertNotNull(questions);
        assertNotNull(answers);
        assertTrue(questions.isEmpty(), "Expected no submitted questions");
        assertTrue(answers.isEmpty(), "Expected no submitted answers");
    }
}