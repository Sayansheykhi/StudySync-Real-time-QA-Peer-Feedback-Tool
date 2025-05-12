package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import application.StaffMessage;

/**
 * The StaffMessageTesting class represents an interface for performing JUnit testing on the StaffMessage.java class.
 * 
 * @author Cristina Hooe
 * @version 1.0 4/10/2025
 */
class StaffMessageTesting {

	/**
	 * Declaration of a Staff Message object to use to call the two functions to be tested in the StaffMessage class
	 */
	StaffMessage newMessage = new StaffMessage();
	
	/**
	 * Test valid values for checkMessageSubjectInput()
	 */
	@Test
	public void NormalTestCheckMessageSubjectInput() {
		assertEquals("", newMessage.checkMessageSubjectInput("This is a valid message subject"), "A valid message subject is expected to result in the return of an empty string");
		assertNotEquals("Error: Message has no subject.", newMessage.checkMessageSubjectInput("This is a valid message subject"), "A valid message subject is expected to result in the return of an empty string, not a specific error message");
	}
	
	/**
	 * Test invalid values for checkMessageSubjectInput()
	 */
	@Test
	public void RobustTestCheckMessageSubjectInput() {
		assertEquals("Error: Message has no subject.", newMessage.checkMessageSubjectInput(""), "An empty message subject is expected to output a specific error");
		assertEquals("Error: Email subject exceeds 255 characters. Use the email body for additional details.", newMessage.checkMessageSubjectInput("A valid message subject cannot exceed 255 characters. If it does exceed 255 characters it will not pass validation and the private message from a staff member to an Instructor or other Staff member will not be successfully sent and instead result in an error message."), "A message subject with over 255 characters is expected to output a specific error");
		assertNotEquals("", newMessage.checkMessageSubjectInput(""), "An empty message subject is expected to output a specific error, not return an empty string");
		assertNotEquals("", newMessage.checkMessageSubjectInput("A valid message subject cannot exceed 255 characters. If it does exceed 255 characters it will not pass validation and the private message from a staff member to an Instructor or other Staff member will not be successfully sent and instead result in an error message."), "A message subject with over 255 characters is expected to output a specific error, not return an empty string");
	}
	
	/**
	 * Test valid values for checkMessageBodyInput()
	 */
	@Test
	public void NormalTestCheckMessageBodyInput() {
		assertEquals("", newMessage.checkMessageBodyInput("This is a valid message body"), "A valid message body is expected to result in the return of an empty string");
		assertNotEquals("Error: Message body has no input.", newMessage.checkMessageBodyInput("This is a valid message body"), "A valid message body is expected to result in the return of an empty string, not a specific error message");
	}

	/**
	 * Test invalid values for checkMessageBodyInput()
	 */
	@Test
	public void RobustTestCheckMessageBodyInput() {
		assertEquals("Error: Message body has no input.", newMessage.checkMessageBodyInput(""), "An empty message body is expected to output a specific error");
		assertNotEquals("", newMessage.checkMessageBodyInput(""), "An empty message body is expected to output a specific error, not return an empty string");
	}
}
