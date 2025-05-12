package application;

import java.time.LocalDateTime;


/**
 * 	Represents messages sent by Instructors to staff, other instructors, students, and reviewers.
 */
public class InstructorMessage {
	private int messageID;
	
	private String subjectText;
	private String messageText;
	
	private User sender;
	private User recipient;
	
	private boolean isDeletedInbox;
	private boolean isMessageRead;
	private LocalDateTime timeSent; 
	
	/**
	 * Constructor used to create a message without recipient for checking the text inputs
	 * 
	 * @param sender	User object of the sender that contains their information
	 * @param subject	subject text of the message
	 * @param body		body text of the message
	 */
	public InstructorMessage(User sender, String subject, String body) {
		this.sender = sender;
		this.subjectText = subject;
		this.messageText = body;
	}
	
	/**
	 * Constructor used to create message object for adding to the database
	 * 
	 * @param sender		User object of the sender
	 * @param recipient		User object of the recipient
	 * @param subject		subject text of the message
	 * @param body			body text of the message
	 * @param timeSent		LocalDateTime that the message was sent
	 */
	public InstructorMessage(User sender, User recipient, String subject, String body, LocalDateTime timeSent) {
		this.sender = sender;
		this.recipient = recipient;
		this.subjectText = subject;
		this.messageText = body;
		this.isDeletedInbox = false;
		this.isMessageRead = false;
		this.timeSent = timeSent;
	}
	
	/**
	 * Constructor used when retrieving messages from database by sender
	 * 
	 * @param messageID			ID of message
	 * @param senderUserName	userName of sender
	 * @param senderFirstName	first name of sender
	 * @param senderLastName	last name of sender
	 * @param senderEmail		email of sender
	 * @param senderRole		role(s) of sender as a string
	 * @param subject			subject text of message
	 * @param body				body text of message
	 * @param isRead			flag denoting whether the recipient has read the message
	 * @param timeSent			the time of creation for the message
	 * @param isDeletedInbox	flag denoting whether the recipient has deleted the message from their inbox
	 */
	public InstructorMessage(int messageID, String senderUserName, String senderFirstName, String senderLastName, String senderEmail, String senderRole, String subject, String body, boolean isRead, LocalDateTime timeSent, boolean isDeletedInbox) {
		boolean[] roles = new boolean[5];
		roles[1] = senderRole.contains("Student");
		roles[2] = senderRole.contains("Reviewer");
		roles[3] = senderRole.contains("Instructor");
		roles[4] = senderRole.contains("Staff");
		
		this.messageID = messageID;
		this.sender = new User(senderUserName, roles, senderEmail, senderFirstName, senderLastName);
		this.subjectText = subject;
		this.messageText = body;
		this.isMessageRead = isRead;
		this.isDeletedInbox = isDeletedInbox;
		this.timeSent = timeSent;
	}
	
	/**
	 * Constructor used when retrieving messages from database by recipient
	 * 
	 * @param messageID				ID of message
	 * @param timeSent				time of creation for the message
	 * @param recipientFirstName	first name of recipient
	 * @param recipientLastName		last name of recipient
	 * @param recipientEmail		email of recipient
	 * @param recipientUserName		userName of recipient
	 * @param recipientRole			role(s) of recipient as string
	 * @param subject				subject text of message
	 * @param body					body text of message
	 * @param isRead				flag denoting whether the recipient has read the message
	 */
	public InstructorMessage(int messageID, LocalDateTime timeSent, String recipientFirstName, String recipientLastName, String recipientEmail, String recipientUserName, String recipientRole, String subject, String body, boolean isRead) {
		boolean[] roles = new boolean[5];
		roles[1] = recipientRole.contains("Student");
		roles[2] = recipientRole.contains("Reviewer");
		roles[3] = recipientRole.contains("Instructor");
		roles[4] = recipientRole.contains("Staff");
		
		this.messageID = messageID;
		this.timeSent = timeSent;
		this.recipient = new User(recipientUserName, roles, recipientEmail, recipientFirstName, recipientLastName);
		this.subjectText = subject;
		this.messageText = body;
		this.isMessageRead = isRead;
	}
	
	public InstructorMessage(LocalDateTime timeSent, String recipientFirstName, String recipientLastName, String recipientEmail, String recipientUserName, String recipientRole, String subject, String body, boolean isRead) {
		boolean[] roles = new boolean[5];
		roles[1] = recipientRole.contains("Student");
		roles[2] = recipientRole.contains("Reviewer");
		roles[3] = recipientRole.contains("Instructor");
		roles[4] = recipientRole.contains("Staff");
		
		this.messageID = messageID;
		this.timeSent = timeSent;
		this.recipient = new User(recipientUserName, roles, recipientEmail, recipientFirstName, recipientLastName);
		this.subjectText = subject;
		this.messageText = body;
		this.isMessageRead = isRead;
	}
	
	//Getters
	public int messageID() { return messageID; }
	public String subjectText() { return subjectText; }
	public String messageText() { return messageText; }
	public User sender() { return sender; }
	public User recipient() { return recipient; }
	public boolean isDeletedInbox() { return isDeletedInbox; }
	public boolean isMessageRead() { return isMessageRead; }
	public LocalDateTime timeSent() { return timeSent; }
	
	
	/**
	 * Marks the messages as deleted in the receiver's inbox, meaning it will not show up there
	 */
	public void deleteInbox() {
		this.isDeletedInbox = true;
	}
	
	/**
	 * Marks the message as having been read by the recipient
	 */
	public void markRead() {
		this.isMessageRead = true;
	}
	
	/**
	 * Updates the subject text to the given text
	 * 
	 * @param newSubject The text that will replace the old subject text
	 */
	public void changeSubjectText(String newSubject) {
		this.subjectText = newSubject;
	}
	
	/**
	 * Updates the message text to the given text
	 * 
	 * @param newMessage The text that will replace the old message text
	 */
	public void changeMessageText(String newMessage) {
		this.messageText = newMessage;
	}
	
	/**
	 * Updates the messageID to the given number
	 * 
	 * @param messageID	The number of the new messageID
	 */
	public void setMessageID(int messageID) {
		this.messageID = messageID;
	}
	
	public String senderRoleString() {
		String senderRole = "";
		for(int i = 1; i < 5; i++) {
			if(sender.getRole()[i]) {
				if(senderRole != "") {
					senderRole += ", ";
				}
				switch(i) {
					case 1:
						senderRole += "Student";
					case 2:
						senderRole += "Reviewer";
					case 3:
						senderRole += "Instructor";
					case 4:
						senderRole += "Staff";
				}
			}
		}
		return senderRole;
	}
	
	public String recipientRoleString() {
		String recipientRole = "";
		for(int i = 1; i < 5; i++) {
			if(recipient.getRole()[i]) {
				if(recipientRole != "") {
					recipientRole += ", ";
				}
				switch(i) {
					case 1:
						recipientRole += "Student";
					case 2:
						recipientRole += "Reviewer";
					case 3:
						recipientRole += "Instructor";
					case 4:
						recipientRole += "Staff";
				}
			}
		}
		return recipientRole;
	}
	
	/**
	 * Method which checks the validity of message subject input.
	 * 
	 * @param messageSubject the input message subject text
	 * @return empty string if valid or specific error message
	 */
	public String checkMessageSubjectInput(String messageSubject) {
		String validateMessageSubjectInput;
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
	public static String checkMessageBodyInput(String messageBody) {
		String validateMessageBodyInput;
		if (messageBody.equals("") || messageBody == null) {
			validateMessageBodyInput = "Error: Message body has no input.";
			return validateMessageBodyInput;
		}
		
		if (messageBody.length() > 5000000) {
			validateMessageBodyInput = "Error: Email body exceeds 5,000,000 characters.";
			return validateMessageBodyInput;
		}
		
		else {
			validateMessageBodyInput = "";
		}
		return validateMessageBodyInput;
	}
	
	
	
}