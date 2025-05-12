package application;

import java.time.LocalDateTime;

import databasePart1.DatabaseHelper;

/**
 * The StaffMessage class represents messages and their attributes sent and received by a user with the Staff role.
 * 
 * @author Cristina Hooe
 * @version 1.0 4/8/2025
 */
public class StaffMessage {
	
	/**
	 * The unique ID of the message.
	 */
	private int messageID;
	
	/**
	 * The email address of the sender of the message.
	 */
	private String senderEmail;
	
	/**
	 * The userName of the sender of the message.
	 */
	private String senderUserName;
	
	/**
	 * The first name of the sender of the message.
	 */
	private String senderFirstName;
	
	/**
	 * The last name of the sender of the message.
	 */
	private String senderLastName;
	
	/**
	 * The role of the sender of the message, either "Instructor" or "Staff."
	 */
	private String senderRole;
	
	/**
	 * The subject text of the message.
	 */
	private String messageSubject;
	
	/**
	 * The body text of the message.
	 */
	private String messageBody;
	
	/**
	 * Boolean for whether the message is read, false if not read, true if read.
	 */
	private boolean isMessageRead;
	
	/**
	 * The email address of the recipient of the message.
	 */
	private String recipientEmail;
	
	/**
	 * The first name of the recipient of the message.
	 */
	private String recipientFirstName;
	
	/**
	 * The last name of the recipient of the message.
	 */
	private String recipientLastName;
	
	/**
	 * The userName of the recipient of the message.
	 */
	private String recipientUserName;
	
	/**
	 * The role of the recipient of the message either "Instructor" or "Staff."
	 */
	private String recipientRole;
	
	/**
	 * Date object containing the time the message was sent.
	 */
	private LocalDateTime timeSent; 
	
	
	private boolean isDeletedInbox;
	
	private boolean isRepliedTo;
	
	
	/**
	 * String used to represent if the input of the message subject field is valid. If valid, an empty string is returned, if not a specific error message.
	 */
	public static String validateMessageSubjectInput = "";
	
	/**
	 * String used to represent if the input of the message body field is valid. If valid, an empty string is returned, if not a specific error message.
	 */
	public static String validateMessageBodyInput = "";
	
	/**
	 * Constructor which creates an empty StaffMessage object.
	 */
	public StaffMessage() {
		this.senderEmail = "";
		this.senderUserName = "";
		this.senderFirstName = "";
		this.senderLastName = "";
		this.messageSubject = "";
		this.messageBody = "";
		this.isMessageRead = false;
		this.recipientEmail = "";
		this.recipientFirstName = "";
		this.recipientLastName = "";
		this.recipientRole = "";
		this.timeSent = LocalDateTime.now();
		this.messageID = -1;
	}
	
	/**
	 * Constructor to build a StaffMessage object prior to pulling the recipient details
	 * 
	 * @param senderUserName the userName specific to the user who sent the message
	 * @param senderFirstName the first name specific to the user who sent the message
	 * @param senderLastName the last name specified to the user who sent the message
	 * @param senderEmail the email specific to the user who sent the message
	 * @param senderRole the role of the user who sent the message, either "Instructor" or "Staff"
	 * @param messageSubject the subject text of the message
	 * @param messageBody the body text of the message
	 */
	public StaffMessage(String senderUserName, String senderFirstName, String senderLastName, String senderEmail, String senderRole, String messageSubject, String messageBody) {
		this.senderUserName = senderUserName;
		this.senderFirstName = senderFirstName;
		this.senderLastName = senderLastName;
		this.senderEmail = senderEmail;
		this.senderRole = senderRole;
		this.messageSubject = messageSubject;
		this.messageBody = messageBody;
	}
	
	/**
	 * Constructor used to retrieve all messages sent to Staff users
	 * 
	 * @param senderUserName the userName specific to the user who sent the message
	 * @param senderFirstName the first name specific to the user who sent the message
	 * @param senderLastName the last name specified to the user who sent the message
	 * @param senderEmail the email specific to the user who sent the message
	 * @param senderRole the role of the user who sent the message, either "Instructor" or "Staff"
	 * @param messageSubject the subject text of the message
	 * @param messageBody the body text of the message
	 * @param isMessageRead false if the message has not yet been read, true if the message has been read
	 * @param sentTime the time the message was sent
	 */
	public StaffMessage(String senderUserName, String senderFirstName, String senderLastName, String senderEmail, String senderRole, String messageSubject, String messageBody, boolean isMessageRead, LocalDateTime sentTime) {
		this.senderUserName = senderUserName;
		this.senderFirstName = senderFirstName;
		this.senderLastName = senderLastName;
		this.senderEmail = senderEmail;
		this.senderRole = senderRole;
		this.messageSubject = messageSubject;
		this.messageBody = messageBody;
		this.isMessageRead = isMessageRead;
		this.timeSent = sentTime;
	}
	
	/**
	 * Constructor used to load the inbox ListView
	 * 
	 * @param messageID the unique identifier for the message
	 * @param senderUserName the userName specific to the user who sent the message
	 * @param senderFirstName the first name specific to the user who sent the message
	 * @param senderLastName the last name specified to the user who sent the message
	 * @param senderEmail the email specific to the user who sent the message
	 * @param senderRole the role of the user who sent the message, either "Instructor" or "Staff"
	 * @param messageSubject the subject text of the message
	 * @param messageBody the body text of the message
	 * @param isMessageRead false if the message has not yet been read, true if the message has been read
	 * @param sentTime the time the message was sent
	 */
	public StaffMessage(int messageID, String senderUserName, String senderFirstName, String senderLastName, String senderEmail, String senderRole, String messageSubject, String messageBody, boolean isMessageRead, LocalDateTime sentTime) {
		this.messageID = messageID;
		this.senderUserName = senderUserName;
		this.senderFirstName = senderFirstName;
		this.senderLastName = senderLastName;
		this.senderEmail = senderEmail;
		this.senderRole = senderRole;
		this.messageSubject = messageSubject;
		this.messageBody = messageBody;
		this.isMessageRead = isMessageRead;
		this.timeSent = sentTime;
	}
	
	/**
	 * Constructor used to retrieve all messages sent from a Staff user to other Staff and Instructor users
	 * 
	 * @param timeSent the time the message was sent
	 * @param recipientFirstName the first name specific to the user who received the message
	 * @param recipientLastName the last name specific to the user who received the message
	 * @param recipientEmail the email specific to the user who received the message
	 * @param recipientUserName the userName specific to the user who received the message
	 * @param recipientRole the role of the user who received the message, either "Instructor" or "Staff"
	 * @param messageSubject the subject text of the message
	 * @param messageBody the body text of the message
	 * @param isMessageRead false if the message has not yet been read, true if the message has been read
	 */
	public StaffMessage(LocalDateTime timeSent, String recipientFirstName, String recipientLastName, String recipientEmail, String recipientUserName, String recipientRole, String messageSubject, String messageBody, boolean isMessageRead) {
		this.recipientFirstName = recipientFirstName;
		this.recipientLastName = recipientLastName;
		this.recipientEmail = recipientEmail;
		this.recipientUserName = recipientUserName;
		this.recipientRole = recipientRole;
		this.messageSubject = messageSubject;
		this.messageBody = messageBody;
		this.isMessageRead = isMessageRead;
		this.timeSent = timeSent;
	}
	
	/**
	 * Constructor used to load the sentMessages ListView
	 * 
	 * @param messageID the unique identifier for the message
	 * @param timeSent the time the message was sent
	 * @param recipientFirstName the first name specific to the user who received the message
	 * @param recipientLastName the last name specific to the user who received the message
	 * @param recipientEmail the email specific to the user who received the message
	 * @param recipientUserName the userName specific to the user who received the message
	 * @param recipientRole the role of the user who received the message, either "Instructor" or "Staff"
	 * @param messageSubject the subject text of the message
	 * @param messageBody the body text of the message
	 * @param isMessageRead false if the message has not yet been read, true if the message has been read
	 */
	public StaffMessage(int messageID, LocalDateTime timeSent, String recipientFirstName, String recipientLastName, String recipientEmail, String recipientUserName, String recipientRole, String messageSubject, String messageBody, boolean isMessageRead) {
		this.messageID = messageID;
		this.recipientFirstName = recipientFirstName;
		this.recipientLastName = recipientLastName;
		this.recipientEmail = recipientEmail;
		this.recipientUserName = recipientUserName;
		this.recipientRole = recipientRole;
		this.messageSubject = messageSubject;
		this.messageBody = messageBody;
		this.isMessageRead = isMessageRead;
		this.timeSent = timeSent;
	}
	

	/**
	 * Constructor to send a new message from a Staff user to other Staff and Instructor users
	 * 
	 * @param senderUserName the userName specific to the user who sent the message
	 * @param senderFirstName the first name specific to the user who sent the message
	 * @param senderLastName the last name specified to the user who sent the message
	 * @param senderEmail the email specific to the user who sent the message
	 * @param senderRole the role of the user who sent the message, either "Instructor" or "Staff"
	 * @param recipientFirstName the first name specific to the user who received the message
	 * @param recipientLastName the last name specific to the user who received the message
	 * @param recipientEmail the email specific to the user who received the message
	 * @param recipientUserName the userName specific to the user who received the message
	 * @param recipientRole the role of the user who received the message, either "Instructor" or "Staff"
	 * @param messageSubject the subject text of the message
	 * @param messageBody the body text of the message
	 * @param isMessageRead false if the message has not yet been read, true if the message has been read
	 * @param sentTime the time the message was sent
	 */
	public StaffMessage(String senderUserName, String senderFirstName, String senderLastName, String senderEmail, String senderRole, String recipientFirstName, String recipientLastName, String recipientUserName, String recipientEmail, String recipientRole, String messageSubject, String messageBody, boolean isMessageRead, LocalDateTime sentTime) {
		this.senderUserName = senderUserName;
		this.senderFirstName = senderFirstName;
		this.senderLastName = senderLastName;
		this.senderEmail = senderEmail;
		this.senderRole = senderRole;
		this.messageSubject = messageSubject;
		this.messageBody = messageBody;
		this.isMessageRead = isMessageRead;
		this.recipientFirstName = recipientFirstName;
		this.recipientLastName = recipientLastName;
		this.recipientUserName = recipientUserName;
		this.recipientEmail = recipientEmail;
		this.recipientRole = recipientRole;
		this.timeSent = sentTime;
		this.messageID = -1;
	}
	
	
	/**
	 * Constructor used to retrieve all messages sent to Staff users
	 * 
	 * @param senderUserName the userName specific to the user who sent the message
	 * @param senderFirstName the first name specific to the user who sent the message
	 * @param senderLastName the last name specified to the user who sent the message
	 * @param senderEmail the email specific to the user who sent the message
	 * @param senderRole the role of the user who sent the message, either "Instructor" or "Staff"
	 * @param messageSubject the subject text of the message
	 * @param messageBody the body text of the message
	 * @param isMessageRead false if the message has not yet been read, true if the message has been read
	 * @param sentTime the time the message was sent
	 */
	public StaffMessage(String senderUserName, String senderFirstName, String senderLastName, String senderEmail, String senderRole, String messageSubject, String messageBody, boolean isMessageRead, LocalDateTime sentTime, boolean isRepliedTo) {
		this.senderUserName = senderUserName;
		this.senderFirstName = senderFirstName;
		this.senderLastName = senderLastName;
		this.senderEmail = senderEmail;
		this.senderRole = senderRole;
		this.messageSubject = messageSubject;
		this.messageBody = messageBody;
		this.isMessageRead = isMessageRead;
		this.timeSent = sentTime;
		this.isRepliedTo = isRepliedTo;
	}
	
	/**
	 * Constructor used to load the inbox ListView
	 * 
	 * @param messageID the unique identifier for the message
	 * @param senderUserName the userName specific to the user who sent the message
	 * @param senderFirstName the first name specific to the user who sent the message
	 * @param senderLastName the last name specified to the user who sent the message
	 * @param senderEmail the email specific to the user who sent the message
	 * @param senderRole the role of the user who sent the message, either "Instructor" or "Staff"
	 * @param messageSubject the subject text of the message
	 * @param messageBody the body text of the message
	 * @param isMessageRead false if the message has not yet been read, true if the message has been read
	 * @param sentTime the time the message was sent
	 * @param isDeletedInbox whether or not the message has been hidden in the reciever's inbox
	 */
	public StaffMessage(int messageID, String senderUserName, String senderFirstName, String senderLastName, String senderEmail, String senderRole, String messageSubject, String messageBody, boolean isMessageRead, LocalDateTime sentTime, boolean isDeletedInbox, boolean isRepliedTo) {
		this.messageID = messageID;
		this.senderUserName = senderUserName;
		this.senderFirstName = senderFirstName;
		this.senderLastName = senderLastName;
		this.senderEmail = senderEmail;
		this.senderRole = senderRole;
		this.messageSubject = messageSubject;
		this.messageBody = messageBody;
		this.isMessageRead = isMessageRead;
		this.timeSent = sentTime;
		this.isDeletedInbox = isDeletedInbox;
		this.isRepliedTo = isRepliedTo;
	}
	
	/**
	 * Constructor used to retrieve all messages sent from a Staff user to other Staff and Instructor users
	 * 
	 * @param timeSent the time the message was sent
	 * @param recipientFirstName the first name specific to the user who received the message
	 * @param recipientLastName the last name specific to the user who received the message
	 * @param recipientEmail the email specific to the user who received the message
	 * @param recipientUserName the userName specific to the user who received the message
	 * @param recipientRole the role of the user who received the message, either "Instructor" or "Staff"
	 * @param messageSubject the subject text of the message
	 * @param messageBody the body text of the message
	 * @param isMessageRead false if the message has not yet been read, true if the message has been read
	 */
	public StaffMessage(LocalDateTime timeSent, String recipientFirstName, String recipientLastName, String recipientEmail, String recipientUserName, String recipientRole, String messageSubject, String messageBody, boolean isMessageRead, boolean isRepliedTo) {
		this.recipientFirstName = recipientFirstName;
		this.recipientLastName = recipientLastName;
		this.recipientEmail = recipientEmail;
		this.recipientUserName = recipientUserName;
		this.recipientRole = recipientRole;
		this.messageSubject = messageSubject;
		this.messageBody = messageBody;
		this.isMessageRead = isMessageRead;
		this.timeSent = timeSent;
		this.isRepliedTo = isRepliedTo;
	}
	
	/**
	 * Constructor used to load the sentMessages ListView
	 * 
	 * @param messageID the unique identifier for the message
	 * @param timeSent the time the message was sent
	 * @param recipientFirstName the first name specific to the user who received the message
	 * @param recipientLastName the last name specific to the user who received the message
	 * @param recipientEmail the email specific to the user who received the message
	 * @param recipientUserName the userName specific to the user who received the message
	 * @param recipientRole the role of the user who received the message, either "Instructor" or "Staff"
	 * @param messageSubject the subject text of the message
	 * @param messageBody the body text of the message
	 * @param isMessageRead false if the message has not yet been read, true if the message has been read
	 */
	public StaffMessage(int messageID, LocalDateTime timeSent, String recipientFirstName, String recipientLastName, String recipientEmail, String recipientUserName, String recipientRole, String messageSubject, String messageBody, boolean isMessageRead, boolean isRepliedTo) {
		this.messageID = messageID;
		this.recipientFirstName = recipientFirstName;
		this.recipientLastName = recipientLastName;
		this.recipientEmail = recipientEmail;
		this.recipientUserName = recipientUserName;
		this.recipientRole = recipientRole;
		this.messageSubject = messageSubject;
		this.messageBody = messageBody;
		this.isMessageRead = isMessageRead;
		this.timeSent = timeSent;
		this.isRepliedTo = isRepliedTo;
	}
	
	


	
	/**
	 * Getter for obtaining messageID.
	 * 
	 * @return messageID as type int
	 */
	public int getMessageID() {
		return messageID;
	}
	
	/**
	 * Setter for setting messageID.
	 * 
	 * @param messageID new messageID
	 */
	public void setMessageID(int messageID) {
		this.messageID = messageID;
	}
	
	/**
	 * Getter for obtaining sender email address.
	 * 
	 * @return the senderEmail as type String
	 */
	public String getSenderEmail() {
		return senderEmail;
	}
	
	/**
	 * Setter for setting sender email address.
	 * 
	 * @param senderEmail new senderEmail
	 */
	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}
	
	/**
	 * Getter for obtaining sender userName.
	 * 
	 * @return the senderUserName as an int
	 */
	public String getSenderUserName() {
		return senderUserName;
	}
	
	/**
	 * Setter for setting sender UserName.
	 * 
	 * @param senderUserName new senderUserName
	 */
	public void setSenderUserName(String senderUserName) {
		this.senderUserName = senderUserName;
	}
	
	/**
	 * Getter for obtaining sender first name.
	 * 
	 * @return the senderFirstName as a String
	 */
	public String getSenderFirstName() {
		return senderFirstName;
	}
	
	/**
	 * Setter for setting sender first name.
	 * 
	 * @param senderFirstName new senderFirstName
	 */
	public void setSenderFirstName(String senderFirstName) {
		this.senderFirstName = senderFirstName;
	}
	
	/**
	 * Getter for obtaining sender last name.
	 * 
	 * @return the senderLastName as a String
	 */
	public String getSenderLastName() {
		return senderLastName;
	}
	
	/**
	 * Setter for setting sender last name.
	 * 
	 * @param senderLastName new senderLastName
	 */
	public void setSenderLastName(String senderLastName) {
		this.senderLastName = senderLastName;
	}
	
	/**
	 * Getter for obtaining the sender role, either "Instructor" or "Staff."
	 * 
	 * @return senderRole as a String
	 */
	public String getSenderRole() {
		return senderRole;
	}
	
	/**
	 * Setter for setting sender role.
	 * 
	 * @param senderRole new senderRole
	 */
	public void setSenderRole(String senderRole) {
		this.senderRole = senderRole;
	}
	
	/**
	 * Getter for obtaining the message subject text.
	 * 
	 * @return the messageSubject as a String
	 */
	public String getMessageSubject() {
		return messageSubject;
	}
	
	/**
	 * Setter for setting the message subject text.
	 * 
	 * @param messageSubject new messageSubject
	 */
	public void setMessageSubject(String messageSubject) {
		this.messageSubject = messageSubject;
	}
	
	/**
	 * Getter for obtaining the message body text.
	 * 
	 * @return the messageBody as a String
	 */
	public String getMessageBody() {
		return messageBody;
	}
	
	/**
	 * Setter for setting the message body text.
	 * 
	 * @param messageBody new messageBody
	 */
	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}
	
	/**
	 * Getter for obtaining whether a message is read or unread.
	 * 
	 * @return true if read, false if unread.
	 */
	public boolean getIsMessageRead() {
		return isMessageRead;
	}
	
	/**
	 * Setter for setting whether a message is read or unread.
	 * 
	 * @param isMessageRead new isMessageUnread
	 */
	public void setIsMessageRead(boolean isMessageRead) {
		this.isMessageRead = isMessageRead;
	}
	
	/**
	 * Getter for obtaining recipient's email.
	 * 
	 * @return the recipientEmail as a String
	 */
	public String getRecipientEmail() {
		return recipientEmail;
	}
	
	/**
	 * Setter for setting the recipient's email.
	 * 
	 * @param recipientEmail new recipientEmail
	 */
	public void setRecipientEmail(String recipientEmail) {
		this.recipientEmail = recipientEmail;
	}
	
	/**
	 * Getter for obtaining the recipient's first name.
	 * 
	 * @return the recipientFirstName as a String
	 */
	public String getRecipientFirstName() {
		return recipientFirstName;
	}
	
	/**
	 * Setters for setting the recipient's first name.
	 * 
	 * @param recipientFirstName new recipientFirstName
	 */
	public void setRecipientFirstName(String recipientFirstName) {
		this.recipientFirstName = recipientFirstName;
	}
	
	/**
	 * Getter for obtaining the recipient's last name.
	 * 
	 * @return the recipientLastName as a String
	 */
	public String getRecipientLastName() {
		return recipientLastName;
	}
	
	/**
	 * Setter for setting the recipient's last name
	 * 
	 * @param recipientLastName new recipientLastName
	 */
	public void setRecipientLastName(String recipientLastName) {
		this.recipientLastName = recipientLastName;
	}
	
	/**
	 * Getter for obtaining the recipient's userName
	 * 
	 * @return the recipientUserName as a String
	 */
	public String getRecipientUserName() {
		return recipientUserName;
	}
	
	/**
	 * Setter for setting the recipient's userName.
	 * 
	 * @param recipientUserName new recipientUserName
	 */
	public void setRecipientUserName(String recipientUserName) {
		this.recipientUserName = recipientUserName;
	}
	
	/**
	 * Getter for obtaining the recipient's role, either "Instructor" or "Staff."
	 * 
	 * @return the recipientRole as a String
	 */
	public String getRecipientRole() {
		return recipientRole;
	}
	
	/**
	 * Setter for setting the recipient's role.
	 * 
	 * @param recipientRole new recipientRole
	 */
	public void setRecipientRole(String recipientRole) {
		this.recipientRole = recipientRole;
	}
	
	/**
	 * Getter for obtaining the time message was sent.
	 * 
	 * @return the timeSent as type LocalDateTime
	 */
	public LocalDateTime getTimeSent() {
		return timeSent;
	}
	
	/**
	 * Setter for setting the time message was sent.
	 * 
	 * @param timeSent new timeSent
	 */
	public void setTimeSent(LocalDateTime timeSent) {
		this.timeSent = timeSent;
	}
	
	/**
	 * Method which checks the validity of message subject input.
	 * 
	 * @param messageSubject the input message subject text
	 * @return empty string if valid or specific error message
	 */
	public String checkMessageSubjectInput(String messageSubject) {
		if (messageSubject.equals("") || messageSubject == null) {
			validateMessageSubjectInput = "Error: Message has no subject.";
			return validateMessageSubjectInput;
		}
		
		if (messageSubject.length() > 255) {
			validateMessageSubjectInput = "Error: Email subject exceeds 255 characters. Use the email body for additional details.";
			return validateMessageSubjectInput;
		}
		
		else {
			validateMessageSubjectInput = "";
		}
		return validateMessageSubjectInput;
	}
	
	/**
	 * Method which checks the validity of message body input.
	 * 
	 * @param messageBody the input message body text
	 * @return empty string if valid or specific error message
	 */
	public String checkMessageBodyInput(String messageBody) {
		if (messageBody.equals("") || messageBody == null) {
			validateMessageBodyInput = "Error: Message body has no input.";
			return validateMessageBodyInput;
		}
		
		if (messageSubject.length() > 5000000) {
			validateMessageBodyInput = "Error: Email body exceeds 5,000,000 characters.";
			return validateMessageBodyInput;
		}
		
		else {
			validateMessageBodyInput = "";
		}
		return validateMessageBodyInput;
	}
	
	public void deleteFromInbox() {
		this.isDeletedInbox = true;
	}
	
	public boolean isDeletedInbox() {
		return isDeletedInbox;
	}
	
	public void markReplied() {
		this.isRepliedTo = true;
	}
	
	public boolean isRepliedTo() {
		return isRepliedTo;
	}
}
