package application;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The StaffPrivateMessages class provides an interface for users with the Staff role to send messages to other users with the Staff or Instructor role.
 * Staff can view both received and sent messages, delete received messages from their inbox, and reply to a received message. Staff can choose to either
 * manually enter in the recipient's email and name or choose a user from the displayed list of Staff/Instructor users to pre-populate the message fields.
 * 
 * @author Cristina Hooe
 * @version 1.0 4/8/2025
 */
public class StaffPrivateMessages {
	/**
	 * Declaration of a DatabaseHelper object for database interactions
	 */
    private final DatabaseHelper databaseHelper;

    /**
	 * Declaration of a User object which is set to the user object passed from the previously accessed page via the show() function call
	 */
    private User user;

    /**
	 * Constructor used to create a new instance of StaffPrivateMessages within class StaffHomePage
	 * 
	 * @param databaseHelper object instance passed from previously accessed page
	 */
    public StaffPrivateMessages(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
	
    /**
     * Declaration of a User object which is the user object specific to the user the Staff member is sending a message to
     */
    private User recipientUser;
    
    /**
     * DateTimeFormatter type-variable used to format the timeSent attribute of a message
     */
    private DateTimeFormatter timeMessageSent = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
     
    /**
     * Declaration of a StaffMessage object used as the message object for new messages
     */
    private StaffMessage newMessage;
    
    /**
     * Declaration of a string-type variable used for checking input validation of the messageSubjectTextField
     */
    private String isMessageSubjectValid;
    
    /**
     * Declaration of a string-type variable used for checking input validation of the messageBodyTextArea field
     */
    private String isMessageBodyValid;
    
    /**
     * Declaration of a string-type variable which represents the message subject text input by the current Staff user
     */
    private String messageSubject;
    
    /**
     * Declaration of a string-type variable which represents the message body text input by the current Staff user
     */
	private String messageBody;
	
	/**
	 * Declaration of int-type variable used to display the current count of unread messages
	 */
	private int countUnreadMessages;
	
	/**
	 * Displays the StaffPrivateMessages page including 2 lists, one displaying registered Staff and Instructor users, and another that displays either
	 * messages received by the currently logged in Staff user or messages sent by the currently logged in Staff user depending on which tab is selected.
	 * A user can send a new message, delete an existing message from the inbox, and reply to an existing message. A user can also view the number of currently
	 * unread messages. When sending a new message, a user can either manually enter input into the Recipient Name and Recipient Email fields or the user
	 * can click on an available user which will pre-fill those fields with the attributes of the selected user. 
	 * 
	 * @param primaryStage the primary stage where the scene will be displayed
	 * @param user the registered user whom successfully logged in within the UserLoginPage and just accessed StaffHomePage
	 */
    public void show(Stage primaryStage, User user) {
    	this.user = user;
    	
    	countUnreadMessages = databaseHelper.countUnreadStaffPrivateMessages(user);
    	
    	// Tab Pane for inbox and sent messages tabs
    	TabPane messageTypeTabPane = new TabPane();
    	
    	// Border Pane containing the 3 VBox's and 3 HBox's
    	BorderPane messageBoard = new BorderPane();
    	messageBoard.setPadding(new Insets(5));
    	
    	// VBox containing the ListView staffAndInstructors
    	VBox leftUsersList = new VBox(10);
    	leftUsersList.setPrefWidth(300);
    	leftUsersList.setStyle("-fx-alignment: center; -fx-padding: 10; -fx-border-color: black; -fx-border-width: 1;");
    	
    	// VBox containing the ListView inbox
    	VBox centerInbox = new VBox(10);
    	centerInbox.setPrefWidth(425);
    	centerInbox.setStyle("-fx-alignment: center; -fx-padding: 10; -fx-border-color: black; -fx-border-width: 1;");
    	
    	// Tab to display the inbox List View
    	Tab inboxTab = new Tab("Inbox");
    	inboxTab.setClosable(false);
    	
    	// VBox containing all the text fields and text areas specific to sending a new message or message reply
    	VBox rightSendMessage = new VBox(10);
    	rightSendMessage.setPrefWidth(425);
    	rightSendMessage.setStyle("-fx-alignment: center_left; -fx-padding: 10; -fx-border-color: black; -fx-border-width: 1;");
    	
    	// Tab to display the sentMessages ListView
    	Tab sentMessagesTab = new Tab("Sent Messages");
    	sentMessagesTab.setClosable(false);
    	
    	// HBox to align the delete message, and reply to message buttons
    	HBox centerButtons = new HBox(10);
    	centerButtons.setPrefWidth(425);
    	centerButtons.setStyle("-fx-alignment: center; -fx-padding: 10;");
    	
    	// HBox for the Send Message and Send Message Reply buttons
    	HBox rightButtons = new HBox(10);
    	rightButtons.setPrefWidth(425);
    	rightButtons.setStyle("-fx-alignment: center; -fx-padding: 10;");
    	
    	// HBox for the header in VBox centerInbox
    	HBox topHeaders = new HBox(10);
    	topHeaders.setPadding(new Insets(5, 100, 5, 5));
    	
    	// Spacers to set positions of the two labels at the top of VBox centerInbox
    	Region spacerLeft = new Region();
    	Region spacerRight = new Region();
    	
    	HBox.setHgrow(spacerLeft, Priority.ALWAYS);
    	HBox.setHgrow(spacerRight, Priority.ALWAYS);
    	
    	// Header label for VBox leftUsersList
    	Label availableUsersLabel = new Label("Instructors and Staff");
    	availableUsersLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    	
    	// Header label for VBox centerInbox
    	Label inboxLabel = new Label("Inbox");
    	inboxLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    	
    	// Count of unread messages label for VBox centerInbox
    	Label countUnreadMessagesLabel = new Label("Unread Messages: " + countUnreadMessages);
    	countUnreadMessagesLabel.setStyle("-fx-font-size: 12px;");
    	
    	// Header label for VBox rightSendMessage
    	Label sendNewMessageLabel = new Label("Compose Message");
    	sendNewMessageLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    	
    	// Header label for recipientNameTextField
    	Label recipientNameLabel = new Label("Recipient's Full Name");
    	recipientNameLabel.setStyle("-fx-font-size: 12px;");
    	
    	// TextField to enter in full name of message recipient
    	TextField recipientNameTextField = new TextField();
    	recipientNameTextField.setPromptText("Enter Recipient's Full Name");
    	recipientNameTextField.setMaxWidth(400);
    	
    	// Header label for recipientEmailTextField
    	Label recipientEmailLabel = new Label("Recipient's Email Address");
    	recipientEmailLabel.setStyle("-fx-font-size: 12px;");
    	
    	// TextField to enter in email address of recipient
    	TextField recipientEmailTextField = new TextField();
    	recipientEmailTextField.setPromptText("Enter Recipient's Email Address");
    	recipientEmailTextField.setMaxWidth(400);
    	
    	// Header label for messageSubjectTextField
    	Label messageSubjectLabel = new Label("Subject");
    	messageSubjectLabel.setStyle("-fx-font-size: 12px;");
    	
    	// TextField to enter in message subject
    	TextField messageSubjectTextField = new TextField();
    	messageSubjectTextField.setPromptText("Enter Message Subject");
    	messageSubjectTextField.setMaxWidth(400);
    	
    	// Header label for messageBodyTextArea
    	Label messageBodyLabel = new Label("Message");
    	messageBodyLabel.setStyle("-fx-font-size: 12px;");
    	
    	// TextArea to enter in message body
    	TextArea messageBodyTextArea = new TextArea();
    	messageBodyTextArea.setPromptText("Enter Message");
    	messageBodyTextArea.setMinHeight(150);
    	messageBodyTextArea.setMinWidth(200);
    	
    	// Button to add a new message to the database from the logged in Staff user to either an Instructor or other Staff member
    	Button sendMessageButton = new Button("Send Message");
    	
    	// Button to delete an existing message from the inbox List View and database
    	Button deleteInboxMessageButton =  new Button("Delete Message");
    	
    	// Button to populate all the input fields with the info from the clicked on message to reply
    	Button messageReplyButton = new Button("Reply to Message");
    	
    	// Button to add a new message reply to the database from the logged in Staff user to either an Instructor or other Staff member
    	Button sendMessageReplyButton = new Button("Send Message Reply");
    	sendMessageReplyButton.setVisible(false);
    	
    	// Button to pre-populate compose message fields based on clicked on Instructor/Staff user
    	Button sendMessageToSpecifiedUser = new Button("Send Message to Specified User");
    	
    	// Error label specific to invalid message subject input
    	Label messageSubjectErrorLabel = new Label();
    	messageSubjectErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
    	messageSubjectErrorLabel.setVisible(false);
    	
    	// Error label specific to invalid message body input
    	Label messageBodyErrorLabel = new Label();
    	messageBodyErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
    	messageBodyErrorLabel.setVisible(false);
    	
    	// Error label specific to not having clicked a message in the inbox ListView
    	Label inboxListViewErrorLabel = new Label();
    	inboxListViewErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
    	inboxListViewErrorLabel.setVisible(false);
    	
    	// Error label specific to input email not matching a registered user in the database
    	Label invalidEmailOrRoleError = new Label();
    	invalidEmailOrRoleError.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
    	invalidEmailOrRoleError.setVisible(false);
    	
    	// Error label specific to not selecting a specified user for pre-filled fields
    	Label noUserSelectedErrorLabel = new Label();
    	noUserSelectedErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
    	noUserSelectedErrorLabel.setVisible(false);
    	
    	// Load the list of all registered Staff and Instructor users
    	ListView<User> staffAndInstructors = new ListView<>();
    	ObservableList<User> staffAndInstructorsObservable = FXCollections.observableArrayList();
    	staffAndInstructorsObservable.addAll(databaseHelper.getStaffAndInstructorUsers(user));
    	staffAndInstructors.setItems(staffAndInstructorsObservable);
    	
    	// FOR DEBUGGING
    	ArrayList<User> usersFromDB = databaseHelper.getStaffAndInstructorUsers(user);
    	
    	System.out.println("Fetched Instructor and Staff Users from database:");
    	for (User userList : usersFromDB) {
    	    System.out.println("First Name: " + userList.getFirstName() + 
    	                       ", Last Name: " + userList.getLastName() + 
    	                       ", Email: " + userList.getEmail());
    	}
    	
    	// Cell Factory for Instructor and Staff user list
    	staffAndInstructors.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
    		@Override
    		public ListCell<User> call(ListView<User> param) {
    			return new ListCell<User>() {
    				@Override
    				protected void updateItem(User user, boolean empty) {
    					super.updateItem(user,  empty);
    					if (empty || user == null) {
    						setText(null);
    					}
    					else {
    						String formattedText = String.format("Name: %s\nEmail: %s",
    								user.getFirstName() + " " + user.getLastName(),
    								user.getEmail());
    						setText(formattedText);
    					}
    				}
    			};
    		}
    	});
    	
    	// ListView for both sent and received messages
    	ListView<StaffMessage> inbox = new ListView<>();
    	
    	// Observable List for inbox
    	ObservableList<StaffMessage> inboxObservable = FXCollections.observableArrayList();
    	inboxObservable.addAll(databaseHelper.getAllReceivedStaffPrivateMessagesWithoutHidden(user.getUserName()));
    	
    	// FOR DEBUGGING
    	ArrayList<StaffMessage> receivedMessages = databaseHelper.getAllReceivedStaffPrivateMessages(user.getUserName());
    	
    	System.out.println("Fetched received messages from database:");
    	for (StaffMessage message : receivedMessages) {
    	    System.out.println("MessageID: " + message.getMessageID() +
    	    				   ", First Name: " + message.getSenderFirstName() + 
    	                       ", Last Name: " + message.getSenderLastName() + 
    	                       ", Email: " + message.getSenderEmail() +
    	                       ", Subject: " + message.getMessageBody() +
    	                       ", Body: " + message.getMessageBody());
    	}
    	
    	// Observable List for sent messages
    	ObservableList<StaffMessage> sentMessageObservable = FXCollections.observableArrayList();
    	sentMessageObservable.addAll(databaseHelper.getAllPrivateMessagesSentFromStaff(user.getUserName()));
    	
    	// FOR DEBUGGING
    	ArrayList<StaffMessage> sentMessages = databaseHelper.getAllPrivateMessagesSentFromStaff(user.getUserName());
    	
    	System.out.println("Fetched sent messages from database:");
    	for (StaffMessage message : sentMessages) {
    	    System.out.println("MessageID: " + message.getMessageID() +
    	    				   ", First Name: " + message.getRecipientFirstName() + 
    	                       ", Last Name: " + message.getRecipientLastName() + 
    	                       ", Email: " + message.getRecipientEmail() +
    	                       ", Subject: " + message.getMessageBody() +
    	                       ", Body: " + message.getMessageBody());
    	}
    	
    	// Listener to load the ListView specific to which tab is selected, inbox or sent messages
    	messageTypeTabPane.getSelectionModel().selectedItemProperty().addListener((observableList, oldTab, newTab) -> {
    		if (newTab == inboxTab) {
    			inbox.setItems(inboxObservable);
    			
    			// Cell Factory for received messages
    	    	inbox.setCellFactory(new Callback<ListView<StaffMessage>, ListCell<StaffMessage>>() {
    	    		@Override
    	    		public ListCell<StaffMessage> call(ListView<StaffMessage> param) {
    	    			return new ListCell<StaffMessage>() {
    	    				@Override
    	    				protected void updateItem(StaffMessage message, boolean empty) {
    	    					super.updateItem(message,  empty);
    	    					if (empty || message == null) {
    	    						setText(null);
    	    					}
    	    					else {
    	    						String formattedTime = message.getTimeSent().format(timeMessageSent);
    	    						String formattedText;
    	    						if(message.isRepliedTo()) {	
    	    							formattedText = String.format("From: %s %s\nDate: %s\nMessage Subject: %s\nMessage Body: %s",
        	    								message.getSenderFirstName() + " " + message.getSenderLastName() + " <" + message.getSenderEmail() + ">",
        	    								(char)0x21A9,
        	    								formattedTime,
        	    								message.getMessageSubject(),
        	    								message.getMessageBody());
    	    						}
    	    						else {
    	    							formattedText = String.format("From: %s \nDate: %s\nMessage Subject: %s\nMessage Body: %s",
        	    								message.getSenderFirstName() + " " + message.getSenderLastName() + " <" + message.getSenderEmail() + ">",
        	    								formattedTime,
        	    								message.getMessageSubject(),
        	    								message.getMessageBody());
    	    						}
    	    						
    	    						setText(formattedText);					
    	    					}
    	    				}
    	    			};
    	    		}
    	    	});
    		}
    		else if (newTab == sentMessagesTab) {
    			inbox.setItems(sentMessageObservable);
    			
    			// Cell Factory for sent messages
    			inbox.setCellFactory(new Callback<ListView<StaffMessage>, ListCell<StaffMessage>>() {
    	    		@Override
    	    		public ListCell<StaffMessage> call(ListView<StaffMessage> param) {
    	    			return new ListCell<StaffMessage>() {
    	    				@Override
    	    				protected void updateItem(StaffMessage message, boolean empty) {
    	    					super.updateItem(message,  empty);
    	    					if (empty || message == null) {
    	    						setText(null);
    	    					}
    	    					else {
    	    						
    	    						String formattedTime = message.getTimeSent().format(timeMessageSent);
    	    						String formattedText;
    	    						if(message.isRepliedTo()) {	
    	    							formattedText = String.format("To: %s %c\nDate: %s\nMessage Subject: %s\nMessage Body: %s",
        	    								message.getRecipientFirstName() + " " + message.getRecipientLastName() + " <" + message.getRecipientEmail() + ">",
        	    								(char)0x21A9,
        	    								formattedTime,
        	    								message.getMessageSubject(),
        	    								message.getMessageBody());
    	    						}
    	    						else {
    	    							formattedText = String.format("To: %s\nDate: %s\nMessage Subject: %s\nMessage Body: %s",
        	    								message.getRecipientFirstName() + " " + message.getRecipientLastName() + " <" + message.getRecipientEmail() + ">",
        	    								formattedTime,
        	    								message.getMessageSubject(),
        	    								message.getMessageBody());
    	    						}
    	    						
    	    						setText(formattedText);
    	    					}
    	    				}
    	    			};
    	    		}
    	    	});
    		}
    	});
    	
    	// Listener to modify the inbox ListView to display only messages received/sent to the clicked on user in the staffAndInstructors ListView based on current tab
    	staffAndInstructors.getSelectionModel().selectedItemProperty().addListener((observableList, oldUser, newUser) -> {
    		if (newUser != null) {
    			if (messageTypeTabPane.getSelectionModel().getSelectedItem() == inboxTab) {
    				ObservableList<StaffMessage> receivedMessagesFilteredByUser = FXCollections.observableArrayList();
    				receivedMessagesFilteredByUser.addAll(databaseHelper.getMessagesSentToStaffByUser(newUser, user));
    				for(StaffMessage message : receivedMessagesFilteredByUser) {
    					if(message.isDeletedInbox()) {
    						receivedMessagesFilteredByUser.remove(message);
    					}
    				}
    				inbox.setItems(receivedMessagesFilteredByUser);
    			}
    			else if (messageTypeTabPane.getSelectionModel().getSelectedItem() == sentMessagesTab) {
    				ObservableList<StaffMessage> sentMessagesFilteredByUser = FXCollections.observableArrayList();
    				sentMessagesFilteredByUser.addAll(databaseHelper.getMessagesSentByStaffToUser(newUser, user));
    				inbox.setItems(sentMessagesFilteredByUser);
    			}
    		}
    		// Defaults back to display all messages when tab is switched
    	});
    	
    	// Listener that sets messages which are clicked on in the inbox tab of the inbox List View to true for isMessageRead
    	inbox.getSelectionModel().selectedItemProperty().addListener((observableList, oldMessage, newMessage) -> {
    		if (newMessage != null) {
    			if (messageTypeTabPane.getSelectionModel().getSelectedItem() == inboxTab) {
    				StaffMessage clickedMessage = new StaffMessage();
    				clickedMessage = inbox.getSelectionModel().getSelectedItem();
    				databaseHelper.markStaffMessageAsRead(clickedMessage);
    				clickedMessage.markReplied();
    				countUnreadMessages = databaseHelper.countUnreadStaffPrivateMessages(user);
    				countUnreadMessagesLabel.setText("Unread Messages: " + countUnreadMessages);
    			}
    		}
    	});
    
    	// Send new message to specified user clicked on in the staffAndInstructors ListView
    	sendMessageToSpecifiedUser.setOnAction(u -> {
    		messageSubjectErrorLabel.setVisible(false);
    		messageBodyErrorLabel.setVisible(false);
			invalidEmailOrRoleError.setVisible(false);
			noUserSelectedErrorLabel.setVisible(false);
    		
    		// Check if a user was clicked on from the staffAndInstructors ListView
    		User recipientSelected = staffAndInstructors.getSelectionModel().getSelectedItem();
    		
    		if (recipientSelected == null) {
    			noUserSelectedErrorLabel.setVisible(true);
    			noUserSelectedErrorLabel.setText("No user selected to message.");
    		}
    		else {
    			noUserSelectedErrorLabel.setVisible(false);
    			
    			// Pre-populate the user specific input fields
    			recipientNameTextField.setText(recipientSelected.getFirstName() + " " + recipientSelected.getLastName());
    			recipientEmailTextField.setText(recipientSelected.getEmail());
    			
    			sendMessageButton.setOnAction(s -> {
    				messageSubject = messageSubjectTextField.getText();
    				messageBody = messageBodyTextArea.getText();
    				
    				StaffMessage preNewMessage = new StaffMessage(user.getUserName(), user.getFirstName(), user.getLastName(), user.getEmail(), "Staff", messageSubject, messageBody);
    				
    				// Check if message subject input is valid
    				isMessageSubjectValid = preNewMessage.checkMessageSubjectInput(messageSubject);
    				
    				// Check if message body input is valid
    				isMessageBodyValid = preNewMessage.checkMessageBodyInput(messageBody);
    				
    				// Both message subject and message body input are valid
    				if (isMessageSubjectValid.equals("") && isMessageBodyValid.equals("")) {
    					String recipientEmail = "";
    					String recipientFirstName = "";
    					String recipientLastName = "";
    					String recipientUserName = "";
    					boolean[] recipientRoles = {false, false, false, false, false};
    					String recipientRole = "";
    					
    					messageSubjectErrorLabel.setVisible(false);
    					messageBodyErrorLabel.setVisible(false);

    					invalidEmailOrRoleError.setVisible(false);
    					recipientFirstName = recipientSelected.getFirstName();
    					recipientLastName = recipientSelected.getLastName();
    					recipientEmail = recipientSelected.getEmail();
    					recipientUserName = recipientSelected.getUserName();
    					recipientRoles = recipientSelected.getRole();
    					if (recipientRoles[3] == true) {
    						recipientRole = "Instructor";
    					}
    					else if (recipientRoles[4] == true) {
    						recipientRole = "Staff";
    					}
    					newMessage = new StaffMessage(user.getUserName(), user.getFirstName(), user.getLastName(), user.getEmail(), "Staff", recipientFirstName, recipientLastName, recipientUserName, recipientEmail, recipientRole, messageSubject, messageBody, false, LocalDateTime.now());
    					int messageID = databaseHelper.addStaffPrivateMessage(newMessage);
    					
    					newMessage.setMessageID(messageID);

    					sentMessageObservable.add(newMessage);

    					recipientNameTextField.clear();
    					recipientEmailTextField.clear();
    					messageSubjectTextField.clear();
    					messageBodyTextArea.clear();
    				}
    				// Either message subject or message body or both inputs are invalid
    				else {
    					if (!isMessageSubjectValid.equals("") && !isMessageBodyValid.equals("")) {
    						messageSubjectErrorLabel.setText(isMessageSubjectValid);
    						messageSubjectErrorLabel.setVisible(true);
    						messageBodyErrorLabel.setText(isMessageBodyValid);
    						messageBodyErrorLabel.setVisible(true);
    					}
    					if (!isMessageBodyValid.equals("") && isMessageSubjectValid.equals("")) {
    						messageSubjectErrorLabel.setVisible(false);
    						messageBodyErrorLabel.setText(isMessageBodyValid);
    						messageBodyErrorLabel.setVisible(true);
    					}
    					if (!isMessageSubjectValid.equals("") && isMessageBodyValid.equals("")) {
    						messageBodyErrorLabel.setVisible(false);
    						messageSubjectErrorLabel.setText(isMessageSubjectValid);
    						messageSubjectErrorLabel.setVisible(true);
    					}
    				}
    			});
    		}
    	});

    	// Send new message via manual input
    	sendMessageButton.setOnAction(s -> {
    		noUserSelectedErrorLabel.setVisible(false);
    		messageSubject = messageSubjectTextField.getText();
    		messageBody = messageBodyTextArea.getText();
    		
    		StaffMessage preNewMessage = new StaffMessage(user.getUserName(), user.getFirstName(), user.getLastName(), user.getEmail(), "Staff", messageSubject, messageBody);
    		
    		// Check if message subject input is valid
    		isMessageSubjectValid = preNewMessage.checkMessageSubjectInput(messageSubject);
    		
    		// Check if message body input is valid
			isMessageBodyValid = preNewMessage.checkMessageBodyInput(messageBody);
			
			String recipientFullName = "";
			String recipientEmail = "";
			
			recipientFullName = recipientNameTextField.getText();
			recipientEmail = recipientEmailTextField.getText();
			recipientUser = databaseHelper.getUserInfoByName(recipientFullName);
			
			// Recipient name was not input
			if (recipientFullName.equals("") || recipientFullName.isEmpty()) {
				messageBodyErrorLabel.setVisible(false);
				invalidEmailOrRoleError.setVisible(false);
				messageSubjectErrorLabel.setVisible(true);
				messageSubjectErrorLabel.setText("Error: Recipient name has no input.");
			}
			// Recipient email was not input
			else if (recipientEmail.equals("")) {
				messageSubjectErrorLabel.setVisible(false);
				invalidEmailOrRoleError.setVisible(false);
				messageBodyErrorLabel.setVisible(true);
				messageBodyErrorLabel.setText("Error: Recipient email has no input.");
			}
			// No user found in the database with the input full name
			else if (recipientUser == null) {
				messageSubjectErrorLabel.setVisible(false);
				messageBodyErrorLabel.setVisible(false);
				invalidEmailOrRoleError.setVisible(true);
				invalidEmailOrRoleError.setText("Error: No user exists matching the name input, unable to send message.");
			}
			// Input email does not match the email registered to the input recipient's full name in the database
			else if (!recipientEmail.equals(recipientUser.getEmail())) {
				messageSubjectErrorLabel.setVisible(false);
				messageBodyErrorLabel.setVisible(false);
				invalidEmailOrRoleError.setVisible(true);
				invalidEmailOrRoleError.setText("Error: Email entered does not match email registered to the input recipient name.");
			}
			// Both message subject and message body input are valid
			else if (isMessageSubjectValid.equals("") && isMessageBodyValid.equals("")) {
				String recipientFirstName = "";
    			String recipientLastName = "";
    			String recipientUserName = "";
    			boolean[] recipientRoles = {false, false, false, false, false};
    			String recipientRole = "";
				
				messageSubjectErrorLabel.setVisible(false);
	    		messageBodyErrorLabel.setVisible(false);
				invalidEmailOrRoleError.setVisible(false);
				
    			recipientFirstName = recipientUser.getFirstName();
    			recipientLastName = recipientUser.getLastName();
    			recipientUserName = recipientUser.getUserName();
    			recipientRoles = recipientUser.getRole();
    			
    			if (recipientRoles[3] == true || recipientRoles[4] == true) {
    				if (recipientRoles[3] == true) {
	    				recipientRole = "Instructor";
	    			}
	    			else if (recipientRoles[4] == true) {
	    				recipientRole = "Staff";
	    			}
    				newMessage = new StaffMessage(user.getUserName(), user.getFirstName(), user.getLastName(), user.getEmail(), "Staff", recipientFirstName, recipientLastName, recipientUserName, recipientEmail, recipientRole, messageSubject, messageBody, false, LocalDateTime.now());
	    			int messageID = databaseHelper.addStaffPrivateMessage(newMessage);
	    			
	    			newMessage.setMessageID(messageID);
	    			
	    			sentMessageObservable.add(newMessage);
	    			
	    			recipientNameTextField.clear();
	    			recipientEmailTextField.clear();
	    			messageSubjectTextField.clear();
	    			messageBodyTextArea.clear();
    			}
    			else {
    				invalidEmailOrRoleError.setVisible(true);
    				invalidEmailOrRoleError.setText("Recipient is not an Instructor or other Staff member, cannot send message.");
    			}	
			}
			// Either message subject or message body or both inputs are invalid
			else {
				if (!isMessageSubjectValid.equals("") && !isMessageBodyValid.equals("")) {
					messageSubjectErrorLabel.setText(isMessageSubjectValid);
					messageSubjectErrorLabel.setVisible(true);
					messageBodyErrorLabel.setText(isMessageBodyValid);
					messageBodyErrorLabel.setVisible(true);
				}
				if (!isMessageBodyValid.equals("") && isMessageSubjectValid.equals("")) {
					messageSubjectErrorLabel.setVisible(false);
					messageBodyErrorLabel.setText(isMessageBodyValid);
					messageBodyErrorLabel.setVisible(true);
				}
				if (!isMessageSubjectValid.equals("") && isMessageBodyValid.equals("")) {
					messageBodyErrorLabel.setVisible(false);
					messageSubjectErrorLabel.setText(isMessageSubjectValid);
					messageSubjectErrorLabel.setVisible(true);
				}
			}	
    	});
    	
    	// Delete message from inbox
    	deleteInboxMessageButton.setOnAction(d -> {
    		StaffMessage messageToDelete = new StaffMessage();
    		messageToDelete = inbox.getSelectionModel().getSelectedItem();
    		Tab currentTab = messageTypeTabPane.getSelectionModel().getSelectedItem();
    		
    		// User has not clicked on a message to delete
    		if (messageToDelete == null) {
    			inboxListViewErrorLabel.setText("No message was selected for deletion.");
    			inboxListViewErrorLabel.setVisible(true);
    		}
    		else if (currentTab == sentMessagesTab) {
    			inboxListViewErrorLabel.setVisible(false);
    			inboxListViewErrorLabel.setText("Cannot delete a sent message, choose a message from the inbox.");
    			inboxListViewErrorLabel.setVisible(true);
    		}
    		// Since the only messaged displayed are those received by the currently logged in user, delete the selected message from the inbox
    		else if (messageToDelete != null && currentTab == inboxTab){
    			inboxListViewErrorLabel.setVisible(false);
    			int messageIDToDelete = messageToDelete.getMessageID();
    			databaseHelper.deleteStaffPrivateMessageFromInbox(messageIDToDelete);
    			inboxObservable.remove(messageToDelete);
    		}
    	});
    	
    	// Reply to an existing message in the inbox
    	messageReplyButton.setOnAction(r -> {
    		StaffMessage messageToReply = new StaffMessage();
    		messageToReply = inbox.getSelectionModel().getSelectedItem();
    		Tab currentTab = messageTypeTabPane.getSelectionModel().getSelectedItem();
    		
    		// User has not clicked on a message to reply to
    		if (messageToReply == null) {
    			inboxListViewErrorLabel.setText("No message was selected to reply to.");
    			inboxListViewErrorLabel.setVisible(true);
    		}
    		else if (currentTab == sentMessagesTab) {
    			inboxListViewErrorLabel.setVisible(false);
    			inboxListViewErrorLabel.setText("Cannot reply to a sent message, choose a message from the inbox.");
    			inboxListViewErrorLabel.setVisible(true);
    		}
    		// Pre-populate all the input fields with the selected messages information and update the message in the database
    		else if (messageToReply != null && currentTab == inboxTab){
    			inboxListViewErrorLabel.setVisible(false);
    			
				String recipientEmail;
				String recipientFirstName;
    			String recipientLastName;
    			String recipientUserName;
    			String recipientRole;
    			int recipientMessageID;

    			
    			String prevMessageDetails = "\nOn " + messageToReply.getTimeSent().format(timeMessageSent) + " " + messageToReply.getSenderFirstName() + " " + messageToReply.getSenderLastName() + "<" + messageToReply.getSenderEmail() + "> wrote: \n";
    			
    			recipientNameTextField.setText(messageToReply.getSenderFirstName() + " " + messageToReply.getSenderLastName());
    			recipientEmailTextField.setText(messageToReply.getSenderEmail());
    			messageSubjectTextField.setText("Re: " + messageToReply.getMessageSubject());
    			messageBodyTextArea.setText(prevMessageDetails + messageToReply.getMessageBody() + "\n");
    			
    			recipientFirstName = messageToReply.getSenderFirstName();
    			recipientLastName = messageToReply.getSenderLastName();
    			recipientUserName = messageToReply.getSenderUserName();
    			recipientRole = messageToReply.getSenderRole();
    			recipientEmail = messageToReply.getSenderEmail();
    			recipientMessageID = messageToReply.getMessageID();
    			
    			sendMessageReplyButton.setVisible(true);
    			sendMessageReplyButton.setOnAction(s -> {
	    			messageSubject = messageSubjectTextField.getText();
	        		messageBody = messageBodyTextArea.getText();
	        		
	        		StaffMessage preNewMessage = new StaffMessage(user.getUserName(), user.getFirstName(), user.getLastName(), user.getEmail(), "Staff", messageSubject, messageBody);
	        		
	        		isMessageSubjectValid = preNewMessage.checkMessageSubjectInput(messageSubject);
	    			isMessageBodyValid = preNewMessage.checkMessageBodyInput(messageBody);
	    			
	    			// Both message subject and message body input are valid
	    			if (isMessageSubjectValid.equals("") && isMessageBodyValid.equals("")) {
	    				messageSubjectErrorLabel.setVisible(false);
	    	    		messageBodyErrorLabel.setVisible(false);
	    	    		
	    	    		newMessage = new StaffMessage(user.getUserName(), user.getFirstName(), user.getLastName(), user.getEmail(), "Staff", recipientFirstName, recipientLastName, recipientUserName, recipientEmail, recipientRole, messageSubject, messageBody, false, LocalDateTime.now());
	    	    		
	    	    		// THIS DOESN'T LINK TO THE OLD MESSAGE IN ANY WAY RIGHT NOW
	    	    		int newMessageID = databaseHelper.addStaffPrivateMessage(newMessage);
	    	    		databaseHelper.markStaffMessageReplied(recipientMessageID);
	    	    		
	    	    		newMessage.setMessageID(newMessageID);
	    	    		
	    	    		sentMessageObservable.add(newMessage);
	    	    		
	    	    		recipientNameTextField.clear();
	        			recipientEmailTextField.clear();
	        			messageSubjectTextField.clear();
	        			messageBodyTextArea.clear();
	        			sendMessageReplyButton.setVisible(false);
	    	    		
	    			}
	    			// Either message subject or message body or both inputs are invalid
	    			else {
	    				if (!isMessageSubjectValid.equals("") && !isMessageBodyValid.equals("")) {
    						messageSubjectErrorLabel.setText(isMessageSubjectValid);
    						messageSubjectErrorLabel.setVisible(true);
    						messageBodyErrorLabel.setText(isMessageBodyValid);
    						messageBodyErrorLabel.setVisible(true);
    					}
    					if (!isMessageBodyValid.equals("") && isMessageSubjectValid.equals("")) {
    						messageSubjectErrorLabel.setVisible(false);
    						messageBodyErrorLabel.setText(isMessageBodyValid);
    						messageBodyErrorLabel.setVisible(true);
    					}
    					if (!isMessageSubjectValid.equals("") && isMessageBodyValid.equals("")) {
    						messageBodyErrorLabel.setVisible(false);
    						messageSubjectErrorLabel.setText(isMessageSubjectValid);
    						messageSubjectErrorLabel.setVisible(true);
    					}
	    			}
    			});
    			if (!rightButtons.getChildren().contains(sendMessageReplyButton)) {
    				rightButtons.getChildren().add(sendMessageReplyButton);
    			}
	    	}
    	});
	    
	    // Button to return to the Staff HomePage
    	Button returnButton = new Button("Return to Staff Homepage");
    	// Return to staffHomePage if button clicked
    	returnButton.setOnAction(r -> {
    		StaffHomePage staffHomePage = new StaffHomePage(databaseHelper, user);
    		staffHomePage.show(primaryStage, this.user);
    	    
    	});
    	
    	leftUsersList.getChildren().addAll(availableUsersLabel, staffAndInstructors, sendMessageToSpecifiedUser, returnButton, noUserSelectedErrorLabel);
    	topHeaders.getChildren().addAll(countUnreadMessagesLabel, spacerLeft, inboxLabel, spacerRight);
    	centerButtons.getChildren().addAll(deleteInboxMessageButton, messageReplyButton);
    	centerInbox.getChildren().addAll(messageTypeTabPane, topHeaders, inbox, centerButtons, inboxListViewErrorLabel);
    	rightButtons.getChildren().addAll(sendMessageButton);
    	rightSendMessage.getChildren().addAll(sendNewMessageLabel,recipientNameLabel, recipientNameTextField, recipientEmailLabel, recipientEmailTextField, messageSubjectLabel, messageSubjectTextField, messageBodyLabel, messageBodyTextArea, rightButtons, invalidEmailOrRoleError, messageSubjectErrorLabel, messageBodyErrorLabel);
    	messageBoard.setLeft(leftUsersList);
    	messageBoard.setCenter(centerInbox);
    	messageBoard.setRight(rightSendMessage);
    	
    	messageTypeTabPane.getTabs().addAll(inboxTab, sentMessagesTab);

	    Scene staffPrivateMessages = new Scene(messageBoard, 1600, 600);

	    // Set the scene to primary stage
	    primaryStage.setScene(staffPrivateMessages);
	    primaryStage.setTitle("Staff Private Messages");
	    primaryStage.show();
    }
}