package test;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import databasePart1.DatabaseHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import application.StaffMessage;
import application.User;

/**
 * The StaffPrivateMessagesTesting class represents an interface for performing JUnit testing on the StaffPrivateMessages.java class.
 * 
 * IMPORTANT: uncomment out Line 48 of DatabaseHelper.java before running
 * 
 * @author Cristina Hooe
 * @author John Gallagher
 * @version 1.0 4/10/2025
 * @version 2.0 4/25/2025
 */
class StaffPrivateMessagesTesting {
	
	/**
	 * DatabaseHelper object to call DatabaseHelper functions that need to be tested and connect to/ disconnect from the database
	 */
	static DatabaseHelper databaseHelper = new DatabaseHelper();
	
	/**
	 * Array of the test users provisioned roles where they are provisioned as Staff
	 */
	static boolean[] staffRole = {false, false, false, true, false};
	
	/**
	 * Array of the test users provisioned roles where they are provisioned as an Instructor
	 */
	static boolean[] instructorRole = {false, false, false, false, true};
	
	/**
	 * Test user Staff to use for methods that require an argument of type User
	 */
	static User testUserStaff;
	
	/**
	 * Second Test user Staff to use for methods that require an argument of type User
	 */
	static User testUserStaff2;
	
	/**
	 * Test user Instructor to use for methods that require an argument of type User
	 */
	static User testUserInstructor;	
	
	/**
	 * Test User object which is instantiated but not registered in the database
	 */
	static User testUserNotRegistered;
	
	/**
	 * StaffMessage object for a message from testUserStaff to testUserInstructor
	 */
	static StaffMessage staffToInstructorMessage;
	
	/**
	 * StaffMessage object for a message from testUserStaff2 to testUserInstructor
	 */
	static StaffMessage staff2ToInstructorMessage;
	
	/**
	 * StaffMessage object for a message from testUserStaff to testUserStaff2
	 */
	static StaffMessage staffToStaff2Message;
	
	/**
	 * StaffMessage object which is never used as an argument to addStaffPrivateMessage()
	 */
	static StaffMessage staffToStaff2MessageNeverStoredInDB;
	
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
		
		// Instantiate staff user
		testUserStaff = new User("testStaffUserName", "Cse360**", staffRole, "testStaffEmail@asu.edu", "testStaffFirstName", "testStaffLastName");
				
		// Add user to the database
		databaseHelper.register(testUserStaff);
		
		// Instantiate instructor user
		testUserInstructor = new User("testInstructorUserName", "Cse360**", instructorRole, "testInstructorEmail@asu.edu", "testInstructorFirstName", "testInstructorLastName");
		
		// Add user to the database
		databaseHelper.register(testUserInstructor);
		
		// Instantiate 2nd staff user
		testUserStaff2 = new User("testSecondStaffUserName", "Cse360**", staffRole, "testSecondStaffEmail@asu.edu", "testSecondStaffFirstName", "testSecondStaffLastName");
		
		// Add user to the database
		databaseHelper.register(testUserStaff2);
		
		// Instantiate test user but don't add to the database
		testUserNotRegistered = new User("testNonRegisteredUserName", "Cse360**", staffRole, "testNonRegisteredEmail@asu.edu", "testNonRegisteredFirstName", "testNonRegisteredLastName");
		
		// Create message object for message from testUserStaff to testUserInstructor
		staffToInstructorMessage = new StaffMessage(testUserStaff.getUserName(), testUserStaff.getFirstName(), testUserStaff.getLastName(), testUserStaff.getEmail(), "Staff", testUserInstructor.getFirstName(), testUserInstructor.getLastName(), testUserInstructor.getUserName(), testUserInstructor.getEmail(), "Instructor", "Test message subject from testUserStaff to testUserInstructor", "Test message body from testUserStaff to testUserInstructor", false, LocalDateTime.now());
		
		// Create message object for message from testUserStaff2 to testUserInstructor
		staff2ToInstructorMessage = new StaffMessage(testUserStaff2.getUserName(), testUserStaff2.getFirstName(), testUserStaff2.getLastName(), testUserStaff2.getEmail(), "Staff", testUserInstructor.getFirstName(), testUserInstructor.getLastName(), testUserInstructor.getUserName(), testUserInstructor.getEmail(), "Instructor", "Test message subject from testUserStaff2 to testUserInstructor", "Test message body from testUserStaff2 to testUserInstructor", false, LocalDateTime.now());
		
		// Create message object for message from testUserStaff to testUserStaff2
		staffToStaff2Message = new StaffMessage(testUserStaff.getUserName(), testUserStaff.getFirstName(), testUserStaff.getLastName(), testUserStaff.getEmail(), "Staff", testUserStaff2.getFirstName(), testUserStaff2.getLastName(), testUserStaff2.getUserName(), testUserStaff2.getEmail(), "Staff", "Test message subject from testUserStaff to testUserStaff2", "Test message body from testUserStaff to testUserStaff2", false, LocalDateTime.now());
		
		// Create message object which is never used to add a new message to the database, so messageID = -1
		staffToStaff2MessageNeverStoredInDB = new StaffMessage(testUserStaff.getUserName(), testUserStaff.getFirstName(), testUserStaff.getLastName(), testUserStaff.getEmail(), "Staff", testUserStaff2.getFirstName(), testUserStaff2.getLastName(), testUserStaff2.getUserName(), testUserStaff2.getEmail(), "Staff", "Test message subject from testUserStaff to testUserStaff2", "Test message body from testUserStaff to testUserStaff2", false, LocalDateTime.now());
	}
	
	/**
	 * Deletes the test objects from the database and closes the database connection.
	 */
	@AfterAll
	static void tearDownAfterClass() {
		
		// Delete the 3 staff messages created during NormalTestAddStaffPrivateMessage() and RobustTestAddStaffPrivateMessage()
		databaseHelper.deleteStaffPrivateMessage(1);
		databaseHelper.deleteStaffPrivateMessage(2);
		databaseHelper.deleteStaffPrivateMessage(3);
		
		// Delete instructor user from the database
		databaseHelper.deleteUser(testUserInstructor);
		
		// Delete 2nd staff user from the database
		databaseHelper.deleteUser(testUserStaff2);
		
		// Delete staff user from the database
		databaseHelper.deleteUser(testUserStaff);
		
		// Close the database connection
		databaseHelper.closeConnection();
	}

	
	/**
	 * Test valid values for databaseHelper.getUserInfoByName().
	 */
	@Test
	public void NormalTestGetUserInfoByEmail() {
		assertNotNull(databaseHelper.getUserInfoByName("testStaffFirstName testStaffLastName"), "User object should not be null for first name + last name associated with a registered user in the database");
	}
	
	/**
	 * Test invalid values for databaseHelper.getUserInfoByName().
	 */
	@Test
	public void RobustTestGetUserInfoByEmail() {
		assertNull(databaseHelper.getUserInfoByName("Cristina Hooe"), "User object should be null for first name + last name not associated with any user registered in the database");
	}
	
	
	/**
	 * Test valid values for databaseHelper.getStaffAndInstructorUsers().
	 */
	@Test
	public void NormalTestGetStaffAndInstructorUsers() {
		assertEquals(2, databaseHelper.getStaffAndInstructorUsers(testUserStaff).size(), "ArrayList should be of size 2 for one Staff and one Instructor user when the third Staff user is passed as an argument");
		assertNotNull(databaseHelper.getStaffAndInstructorUsers(testUserStaff), "ArrayList returned should never be null");
	}
	
	/**
	 * Test invalid values for databaseHelper.getStaffAndInstructorUsers().
	 */
	@Test
	public void RobustTestGetStaffAndInstructorUsers() {
		assertNotEquals(3, databaseHelper.getStaffAndInstructorUsers(testUserStaff).size(), "ArrayList should not be returning the User passed as an argument, so the size should not be 3");
		assertNotNull(databaseHelper.getStaffAndInstructorUsers(testUserNotRegistered), "ArrayList returned should never be null, even when an invalid User is passed as an argument");
	}
	
	
	/**
	 * Test valid values for databaseHelper.addStaffPrivateMessage().
	 */
	@Test
	public void NormalTestAddStaffPrivateMessage() {
		assertEquals(1, databaseHelper.addStaffPrivateMessage(staffToInstructorMessage), "messageID for first staff message sent should be 1");
		assertEquals(2, databaseHelper.addStaffPrivateMessage(staff2ToInstructorMessage), "messageID for second staff message sent should be 2");
	}
	
	/**
	 * Test invalid values for databaseHelper.addStaffPrivateMessage().
	 */
	@Test
	public void RobustTestAddStaffPrivateMessage() {
		assertNotEquals(-1, databaseHelper.addStaffPrivateMessage(staffToStaff2Message), "messageID should not be -1 for valid StaffMessage object added to the database");
	}
	
	
	/**
	 * Test valid values for databaseHelper.getAllReceivedStaffPrivateMessages().
	 */
	@Test
	public void NormalTestGetAllReceivedStaffPrivateMessages() {
		// Retrieve all messages sent to testUserStaff (0)
		assertEquals(0, databaseHelper.getAllReceivedStaffPrivateMessages(testUserStaff.getUserName()).size(), "ArrayList size should be 0 for Staff user testUserStaff who sent 2 messages, and recieved 0 messages");
		
		// Retrieve all messages sent to testUserStaff2 (1)
		assertEquals(1, databaseHelper.getAllReceivedStaffPrivateMessages(testUserStaff2.getUserName()).size(), "ArrayList shize should be 1 for Staff user testUserStaff2 who sent 1 message, and recieved 1 message");
		
	}
	
	/**
	 * Test invalid values for databaseHelper.getAllReceivedStaffPrivateMessages().
	 */
	@Test
	public void RobustTestGetAllReceivedStaffPrivateMessages() {
		assertEquals(0, databaseHelper.getAllReceivedStaffPrivateMessages(testUserInstructor.getUserName()).size(), "ArrayList size returned when user sent as argument is an instructor user, and not a staff user, should be 0");
		assertNotNull(databaseHelper.getAllReceivedStaffPrivateMessages(testUserInstructor.getUserName()), "ArrayList returned should not be null even when argument passed is instructor user");
	}
	
	
	/**
	 * Test valid values for databaseHelper.getAllPrivateMessagesSentFromStaff().
	 */
	@Test
	public void NormalTestGetAllPrivateMessagesSentFromStaff() {
		// Retrieve all messages sent by testUserStaff (2)
				
		assertEquals(2, databaseHelper.getAllPrivateMessagesSentFromStaff(testUserStaff.getUserName()).size(), "ArrayList size returned when current Staff user is testUserStaff should be 2");

		// Retrieve all messages sent by TestUserStaff2 (1)
		assertEquals(1, databaseHelper.getAllPrivateMessagesSentFromStaff(testUserStaff2.getUserName()).size(), "ArrayList size returned when current Staff user is testUserStaff2 should be 1");
	}
	
	/**
	 * Test invalid values for databaseHelper.getAllPrivateMessagesSentFromStaff().
	 */
	@Test
	public void RobustTestGetAllPrivateMessagesSentFromStaff() {
		// Attempt to retrieve all messages sent by testUserInstructor, should return ArrayList of size 0
		assertEquals(0, databaseHelper.getAllPrivateMessagesSentFromStaff(testUserInstructor.getUserName()).size(), "ArrayList size returned when user sent as argument is an instructor user, and not a staff user, should be 0");
		assertNotNull(databaseHelper.getAllPrivateMessagesSentFromStaff(testUserInstructor.getUserName()), "ArrayList returned should not be null even when argument passed is instructor user");
	}
	
	/**
	 * Test valid values for databaseHelper.getMessagesSentToStaffByUser().
	 */
	@Test
	public void NormalTestGetMessagesSentToStaffByUser() {
		assertEquals(1, databaseHelper.getMessagesSentToStaffByUser(testUserStaff, testUserStaff2).size(), "ArrayList size returned when current Staff User is testUserStaff2 and requested are messages sent from testUserStaff to testUserStaff2 should be 1");
		assertEquals(0, databaseHelper.getMessagesSentToStaffByUser(testUserStaff2, testUserStaff).size(), "ArrayList size returned when current Staff User is testUserStaff and requested are messages sent from testUserStaff2 to testUserStaff should be 0");
		assertNotNull(databaseHelper.getMessagesSentToStaffByUser(testUserStaff2, testUserStaff).size(), "ArrayList returned should not be null even when there are no messages in the database sent from testUserStaff2 to testUserStaff ");
	}
	
	/**
	 * Test invalid values for databaseHelper.getMessagesSentToStaffByUser().
	 */
	@Test
	public void RobustTestGetMessagesSentToStaffByUser() {
		assertEquals(0, databaseHelper.getMessagesSentToStaffByUser(testUserInstructor, testUserStaff2).size(), "ArrayList size returned when current Staff User is testUserStaff2 and requested are messages sent from testUserInstructor to testUserStaff2 should be 0");
		assertEquals(0, databaseHelper.getMessagesSentToStaffByUser(testUserInstructor, testUserStaff).size(), "ArrayList size returned when current Staff User is testUserStaff and requested are messages sent from testUserInstructor to testUserStaff should be 0");
	}
	
	
	/**
	 * Test valid values for databaseHelper.getMessagesSentByStaffToUser().
	 */
	@Test
	public void NormalTestGetMessagesSentByStaffToUser() {
		assertEquals(1, databaseHelper.getMessagesSentByStaffToUser(testUserInstructor, testUserStaff).size(), "ArrayList size returned should be 1 when recipient is testUserInstructor and current staff user is testUserStaff");
		assertEquals(1, databaseHelper.getMessagesSentByStaffToUser(testUserInstructor, testUserStaff2).size(), "ArrayList size returned should be 1 when recipient is testUserInstructor and current staff user is testUserStaff2");
	}
	
	/**
	 * Test invalid values for databaseHelper.getMessagesSentByStaffToUser().
	 */
	@Test
	public void RobustTestGetMessagesSentByStaffToUser() {
		assertEquals(0, databaseHelper.getMessagesSentByStaffToUser(testUserStaff, testUserStaff).size(), "ArrayList size returned should be 0 when both the current Staff user and recipient passed as arguments are the same user");
		assertNotNull(databaseHelper.getMessagesSentByStaffToUser(testUserStaff, testUserStaff), "ArrayList returned should not be null even when the specified recipient and current staff user are both the same user");
	}
	
	
	/**
	 * Test valid values for databaseHelper.countUnreadStaffPrivateMessages().
	 */
	@Test
	public void NormalTestCountUnreadStaffPrivateMessages() {
		assertEquals(0, databaseHelper.countUnreadStaffPrivateMessages(testUserStaff), "Count of unread private messages should be 0 for staff user testUserStaff who received 0 messages");
		assertEquals(1, databaseHelper.countUnreadStaffPrivateMessages(testUserStaff2), "Count of unread private messages should be 1 for staff user testUserStaff2 who received 1 message");
	}
	
	/**
	 * Test invalid values for databaseHelper.countUnreadStaffPrivateMessages()
	 */
	@Test
	public void RobustTestCountUnreadStaffPrivateMessages() {
		assertEquals(0, databaseHelper.countUnreadStaffPrivateMessages(testUserNotRegistered), "Count of unread private messages should be 0 even for user who has not been registered in the database");
		assertNotEquals(-1, databaseHelper.countUnreadStaffPrivateMessages(testUserStaff2), "Count of unread private messages should never be a negative number");
	}
	
	/**
	 * Test valid values for databaseHelper.markStaffMessageAsRead()
	 */
	@Test
	public void NormalTestMarkStaffMessageAsRead() {
		// Mark staffToStaff2Message as read
		databaseHelper.markStaffMessageAsRead(staffToStaff2Message);
		
		// Use databaseHelper.countUnreadStaffPrivateMessages() to check count is now 0
		assertEquals(0, databaseHelper.countUnreadStaffPrivateMessages(testUserStaff2), "Count of unread private messages should be 0 now for staff user testUserStaff2 whose only received message was just marked read");
	}

	/**
	 * Test invalid values for databaseHelper.markStaffMessageAsRead()
	 */
	@Test
	public void RobustTestMarkStaffMessageAsRead() {
		// Attempt to mark message from testUserStaff to testUserStaff2 as read, but object was never used to add a message to the database
		databaseHelper.markStaffMessageAsRead(staffToStaff2MessageNeverStoredInDB);
		assertEquals(0, databaseHelper.countUnreadStaffPrivateMessages(testUserStaff2), "Count of unread private messages should still be 0 for staff user testUserStaff2");
	}
	
	/**
	 * Test for databaseHelper.markStaffMessageReplied()
	 */
	@Test
	public void markStaffMessageRepliedToTest() {
		databaseHelper.markStaffMessageReplied(staffToStaff2Message.getMessageID());
		assertTrue(staffToStaff2Message.isRepliedTo());
	}
	
	/**
	 * Test for databaseHelper.getAllReceivedStaffPrivateMessages()
	 */
	@Test
	public void getAllStaffMessages() {
		assertEquals(1, databaseHelper.getAllReceivedStaffPrivateMessages(testUserStaff2.getUserName()).size());
	}
	
	/**
	 * Test for databaseHelper.deleteStaffPrivateMessage()
	 */
	@Test
	public void deleteStaffPrivateMessageTest() {
		databaseHelper.deleteStaffPrivateMessage(staffToStaff2Message.getMessageID());
		assertEquals(0, databaseHelper.getAllReceivedStaffPrivateMessagesWithoutHidden(testUserStaff2.getUserName()));
	}
	
	/**
	 * Test for databaseHelper.getAllPrivateMessagesSentFromStaff()
	 */
	@Test
	public void getAllPriveMessageSentFromStaffTest() {
		assertEquals(2, databaseHelper.getAllPrivateMessagesSentFromStaff(testUserStaff.getUserName()));
	}
	
	/**
	 * Test for databaseHelper.getMessagesSentByStaffToUser
	 */
	@Test
	public void getAllMessageSentoStaffByUser() {
		assertEquals(1, databaseHelper.getMessagesSentByStaffToUser(testUserStaff2, testUserStaff));
	}	
}