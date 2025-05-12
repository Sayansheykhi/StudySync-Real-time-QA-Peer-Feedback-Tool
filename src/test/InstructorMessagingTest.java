package test;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import application.StaffMessage;
import application.InstructorMessage;
import application.User;
import databasePart1.DatabaseHelper;

class InstructorMessagingTest {
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
	static InstructorMessage staffToInstructorMessage;
	
	/**
	 * StaffMessage object for a message from testUserStaff2 to testUserInstructor
	 */
	static InstructorMessage InstructorToStaffMessage;
	
	@BeforeAll
	static void setup() throws Exception{
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
		
		// Create message object for message from testUserStaff to testUserInstructor
		staffToInstructorMessage = new InstructorMessage(LocalDateTime.now(),  testUserStaff.getFirstName(), testUserStaff.getLastName(), testUserStaff.getEmail(), testUserStaff.getUserName(), "Staff", "Test message subject from testUserStaff to testUserInstructor", "Test message body from testUserStaff to testUserInstructor", false);
		
		// Create message object for message from testUserStaff2 to testUserInstructor
		InstructorToStaffMessage = new InstructorMessage(LocalDateTime.now(), testUserInstructor.getFirstName(), testUserInstructor.getLastName(), testUserInstructor.getEmail(), testUserInstructor.getUserName(), "Instructor", "Test message subject from testUserStaff2 to testUserInstructor", "Test message body from testUserStaff2 to testUserInstructor", false);
				
	}
	/**
	 * This function test the senderRoleString method.
	 */
	@Test
	void getSenderRoleStringTest() {
		assertEquals("Staff", staffToInstructorMessage.senderRoleString());
	}
	
	/**
	 * This function test the recipientRoleString method.
	 */
	@Test
	void getRecipientRoleStringTest() {
		assertEquals("Instructor", staffToInstructorMessage.recipientRoleString());
	}
	
	/**
	 * This function test the checkMessageSubjectInput method.
	 */
	@Test
	void checkMessageSubjectInputTest() {
		assertEquals("", InstructorMessage.checkMessageBodyInput("word"));
	}
	
	/**
	 * This function test the addPrivateMessage method.
	 */
	@Test
	void addPrivateMessageTest() {
		databaseHelper.addInstructorPrivateMessage(new InstructorMessage(testUserInstructor, "String", "String"));
		assertEquals(2, databaseHelper.getAllPrivateMessagesSentFromInstructor(null));
	}
	
	/**
	 * This function test the countUnreadInstructorPrivateMessages method.
	 */
	@Test
	void countUnreadInstructorPrivateMessagesTest() {
		assertEquals(1, databaseHelper.countUnreadInstructorPrivateMessages(testUserInstructor));
	}
	
	/**
	 * This function test the markInstructorMessageAsRead method.
	 */
	@Test
	void markInstructorMessageAsReadTest() {
		databaseHelper.markInstructorMessageAsRead(staffToInstructorMessage);
		assertEquals(0, databaseHelper.countUnreadInstructorPrivateMessages(testUserInstructor));
	}
	
	/**
	 * This function test the getAllPrivateMessagesSentFromInstructor method.
	 */
	@Test
	void getAllPrivateMessagesSentFromInstructorTest() {
		assertEquals(1, databaseHelper.getAllPrivateMessagesSentFromInstructor(testUserInstructor.getUserName()));
	}
	
	/**
	 * This function test the deleteInstructorPrivateMessageFromInbox method.
	 */
	@Test
	void deleteInstructorPrivateMessageFromInboxTest() {
		databaseHelper.deleteInstructorPrivateMessageFromInbox(staffToInstructorMessage.messageID());
		assertEquals(0, databaseHelper.getAllReceivedInstructorPrivateMessages(testUserInstructor.getUserName()));
	}
	
}