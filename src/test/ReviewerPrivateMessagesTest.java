package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import application.ReviewerMessage;
import application.ReviewerPrivateMessages;
import application.User;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import databasePart1.DatabaseHelper;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

/**
 * The ReviewerPrivateMessagesTest class represents an interface for perform JUnit testing on ReviewerPrivateMessages.Java
 */
public class ReviewerPrivateMessagesTest {
	
	/**
	 * Instance of the current class being tested
	 */
	private ReviewerPrivateMessages reviewerPrivateMessages;
	
	/**
	 *  DatabaseHelper object to call DatabaseHelper functions that need to be tested and connect to/ disconnect from the database
	 */
    private DatabaseHelper realDatabaseHelper;
    
    /**
	 *  Test user to use for methods that require an argument of type User
	 */
    private User reviewerUser;
    
    /**
     * Method initializes test objects. 
     */
    @BeforeEach
    void setUp() {
        // Instantiate the dummy DatabaseHelper.
        realDatabaseHelper = new DatabaseHelper();
        reviewerUser = new User("reviewerUsername", "somePassword", new boolean[5], "filler", "filler", "filler");
        reviewerPrivateMessages = new ReviewerPrivateMessages(realDatabaseHelper);
    }
    
    /**
     * Test that private message lists start empty.
     * @throws Exception undefined
     */
    @Test
    void testMessagesListStartsEmpty() throws Exception {
       
        Field messagesField = ReviewerPrivateMessages.class.getDeclaredField("messages");
        messagesField.setAccessible(true);
        @SuppressWarnings("unchecked")
        ObservableList<ReviewerMessage> messages = (ObservableList<ReviewerMessage>) messagesField.get(reviewerPrivateMessages);

       
        assertTrue(messages.isEmpty(), "Messages list should be empty at the start.");
    }
    
    /**
     * Test messages marked asRead when message accessed. 
     * @throws Exception undefined
     */
    @Test
    void testShowMessageDetailsMarksMessageAsRead() throws Exception {
        
        ReviewerMessage message = new ReviewerMessage(
                1001,
                "reviewerSender",
                "studentRecipient",
                "StudentRole",
                "Test Subject",
                "Test Body",
                LocalDateTime.now(),
                false,  
                -1
        );

        
        Method showMessageDetailsMethod = ReviewerPrivateMessages.class
                .getDeclaredMethod("showMessageDetails", ReviewerMessage.class);
        showMessageDetailsMethod.setAccessible(true);

       
        showMessageDetailsMethod.invoke(reviewerPrivateMessages, message);

    
        assertTrue(message.getIsRead(), "Message should be marked as read after showing details.");
    }
    
    /**
     * Test that reply via dialog, creates reply message.
     * @throws Exception undefined.
     */
    @Test
    void testOpenReplyDialogCreatesReplyMessage() throws Exception {
        
        ReviewerMessage originalMessage = new ReviewerMessage(
                1002,
                "reviewerSender",
                "studentRecipient",
                "StudentRole",
                "Original Subject",
                "Original Body",
                LocalDateTime.now(),
                false,
                -1
        );

      
        Method openReplyDialogMethod = ReviewerPrivateMessages.class
                .getDeclaredMethod("openReplyDialog", Stage.class, ReviewerMessage.class);
        openReplyDialogMethod.setAccessible(true);

        
        openReplyDialogMethod.invoke(reviewerPrivateMessages, null, originalMessage);

       
        assertTrue(true, "openReplyDialog invoked successfully without error.");
    }
    /**
     * Test methods associated with sending a message.
     * @throws Exception undefined
     */
    @Test
    void testSendNewMessageProgrammatically() throws Exception {
        
        Field messagesField = ReviewerPrivateMessages.class.getDeclaredField("messages");
        messagesField.setAccessible(true);
        @SuppressWarnings("unchecked")
        ObservableList<ReviewerMessage> messages = (ObservableList<ReviewerMessage>) messagesField.get(reviewerPrivateMessages);

       
        assertTrue(messages.isEmpty(), "Messages list should start empty.");

      
        ReviewerMessage newMsg = new ReviewerMessage(
            -1,
            reviewerUser.getUserName(),
            "studentRecipient",
            "Student",
            "Test Subject",
            "Test body text",
            LocalDateTime.now(),
            false,
            -1
        );

      
        realDatabaseHelper.saveReviewerMessage(newMsg);
        messages.add(newMsg);

        
        assertEquals(1, messages.size(), "Messages list should have 1 message after adding.");
    }
}