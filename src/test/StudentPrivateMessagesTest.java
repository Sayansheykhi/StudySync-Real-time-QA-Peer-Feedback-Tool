package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.User;
import databasePart1.DatabaseHelper;

import java.util.ArrayList;
/**
 * The StudentPrivateMessagesTest class represents an interface for performing
 * JUnit testing on StudentPrivateMessages.java
 */
public class StudentPrivateMessagesTest {
	/**
	 *  DatabaseHelper object to call DatabaseHelper functions that need to be tested and connect to/ disconnect from the database
	 */
    private DatabaseHelper dbHelper;
    
    /**
	 *  Test user to use for methods that require an argument of type User
	 */
    private User testUser;
    
    /**
     * Method initiates test objects.
     */
    @BeforeEach
    public void setUp() {
        dbHelper = new DatabaseHelper();
        testUser = new User("testStudent", "pass", new boolean[]{false, true, false, false, false}, "test@asu.edu", "Test", "Student");
    }
    
    /**
     * Test the save and load private message functions.
     */
    @Test
    public void testSendAndLoadPrivateMessages() {
        String reviewer = "testReviewer";
        String subject = "JUnit Subject";
        String body = "JUnit Test Message Body";

        boolean sendSuccess = dbHelper.sendPrivateMessage(
                testUser.getUserName(), reviewer, subject, body, null
        );

        assertTrue(sendSuccess, "Message should be sent successfully");

        ArrayList<String> messages = dbHelper.getPrivateMessagesForStudent(testUser.getUserName());
        assertNotNull(messages, "Messages list should not be null");
        assertFalse(messages.isEmpty(), "Expected at least one message");

        int unread = dbHelper.getUnreadPrivateMessageCount(testUser.getUserName());
        assertTrue(unread >= 1, "Expected at least one unread message");
    }
}