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

public class InstructorPrivateMessages {
	private final DatabaseHelper databaseHelper;
	
	private DateTimeFormatter timeMessageSent = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
	
	private int countUnreadMessages;
	
    private InstructorMessage newMessage;

	
	public InstructorPrivateMessages(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}
	
	public void show(Stage primaryStage, User user) {
    	countUnreadMessages = databaseHelper.countUnreadInstructorPrivateMessages(user);
    	
    	// Tab Pane for inbox and sent messages tabs
    	TabPane messageTypeTabPane = new TabPane();
    	
    	// Border Pane containing the 2 VBox's and 3 HBox's
    	BorderPane messageBoard = new BorderPane();
    	messageBoard.setPadding(new Insets(5));
    	
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
    	
    	// Button to add a new message to the database from the logged in Instructor user to either an Instructor, Staff, Student, or Reviewer
    	Button sendMessageButton = new Button("Send Message");
    	
    	// Button to delete an existing message from the inbox List View and database
    	Button deleteInboxMessageButton =  new Button("Delete Message");
    	
    	// Button to populate all the input fields with the info from the clicked on message to reply
    	Button messageReplyButton = new Button("Reply to Message");
    	
    	// Button to add a new message reply to the database from the logged in Instructor user to either an Instructor, Staff, Student, or Reviewer
    	Button sendMessageReplyButton = new Button("Send Message Reply");
    	sendMessageReplyButton.setVisible(false);
    	
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
    	
    	// ListView for both sent and received messages
    	ListView<InstructorMessage> inbox = new ListView<>();
    	
    	// Observable List for inbox
    	ObservableList<InstructorMessage> inboxObservable = FXCollections.observableArrayList();
    	inboxObservable.addAll(databaseHelper.getAllReceivedInstructorPrivateMessagesWithoutHidden(user.getUserName()));
    	
    	// FOR DEBUGGING
    	ArrayList<InstructorMessage> receivedMessages = databaseHelper.getAllReceivedInstructorPrivateMessages(user.getUserName());
    	
    	System.out.println("Fetched received messages from database:");
    	for (InstructorMessage message : receivedMessages) {
    	    System.out.println("MessageID: " + message.messageID() +
    	    				   ", First Name: " + message.sender().getFirstName() + 
    	                       ", Last Name: " + message.sender().getLastName() + 
    	                       ", Email: " + message.sender().getEmail() +
    	                       ", Subject: " + message.subjectText() +
    	                       ", Body: " + message.messageText());
    	}
    	
    	// Observable List for sent messages
    	ObservableList<InstructorMessage> sentMessageObservable = FXCollections.observableArrayList();
    	sentMessageObservable.addAll(databaseHelper.getAllPrivateMessagesSentFromInstructor(user.getUserName()));
    	
    	// FOR DEBUGGING
    	ArrayList<InstructorMessage> sentMessages = databaseHelper.getAllPrivateMessagesSentFromInstructor(user.getUserName());
    	
    	System.out.println("Fetched sent messages from database:");
    	for (InstructorMessage message : sentMessages) {
    	    System.out.println("MessageID: " + message.messageID() +
    	    				   ", First Name: " + message.recipient().getFirstName() + 
    	                       ", Last Name: " + message.recipient().getLastName() + 
    	                       ", Email: " + message.recipient().getEmail() +
    	                       ", Subject: " + message.subjectText() +
    	                       ", Body: " + message.messageText());
    	}
    	
    	// Listener to load the ListView specific to which tab is selected, inbox or sent messages
    	messageTypeTabPane.getSelectionModel().selectedItemProperty().addListener((observableList, oldTab, newTab) -> {
    		if (newTab == inboxTab) {
    			inbox.setItems(inboxObservable);
    			
    			// Cell Factory for received messages
    	    	inbox.setCellFactory(new Callback<ListView<InstructorMessage>, ListCell<InstructorMessage>>() {
    	    		@Override
    	    		public ListCell<InstructorMessage> call(ListView<InstructorMessage> param) {
    	    			return new ListCell<InstructorMessage>() {
    	    				@Override
    	    				protected void updateItem(InstructorMessage message, boolean empty) {
    	    					super.updateItem(message,  empty);
    	    					if (empty || message == null) {
    	    						setText(null);
    	    					}
    	    					else {
    	    						
    	    						String formattedTime = message.timeSent().format(timeMessageSent);
    	    						String formattedText;
	    							formattedText = String.format("From: %s \nDate: %s\nMessage Subject: %s\nMessage Body: %s",
    	    								message.sender().getFirstName() + " " + message.sender().getLastName() + " <" + message.sender().getEmail() + ">",
    	    								formattedTime,
    	    								message.subjectText(),
    	    								message.messageText());
    	    						
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
    			inbox.setCellFactory(new Callback<ListView<InstructorMessage>, ListCell<InstructorMessage>>() {
    	    		@Override
    	    		public ListCell<InstructorMessage> call(ListView<InstructorMessage> param) {
    	    			return new ListCell<InstructorMessage>() {
    	    				@Override
    	    				protected void updateItem(InstructorMessage message, boolean empty) {
    	    					super.updateItem(message,  empty);
    	    					if (empty || message == null) {
    	    						setText(null);
    	    					}
    	    					else {
    	    						
    	    						String formattedTime = message.timeSent().format(timeMessageSent);
    	    						String formattedText;
    	    						formattedText = String.format("To: %s\nDate: %s\nMessage Subject: %s\nMessage Body: %s",
    	    								message.recipient().getFirstName() + " " + message.recipient().getLastName() + " <" + message.recipient().getEmail() + ">",
    	    								formattedTime,
    	    								message.subjectText(),
    	    								message.messageText());
    	    						
    	    						setText(formattedText);
    	    					}
    	    				}
    	    			};
    	    		}
    	    	});
    		}
    	});
    	
    	// Listener that sets messages which are clicked on in the inbox tab of the inbox List View to true for isMessageRead
    	inbox.getSelectionModel().selectedItemProperty().addListener((observableList, oldMessage, newMessage) -> {
    		if (newMessage != null) {
    			if (messageTypeTabPane.getSelectionModel().getSelectedItem() == inboxTab) {
    				InstructorMessage clickedMessage = inbox.getSelectionModel().getSelectedItem();
    				databaseHelper.markInstructorMessageAsRead(clickedMessage);
    				countUnreadMessages = databaseHelper.countUnreadInstructorPrivateMessages(user);
    				countUnreadMessagesLabel.setText("Unread Messages: " + countUnreadMessages);
    			}
    		}
    	});
    

    	// Send new message via manual input
    	sendMessageButton.setOnAction(s -> {
    		String messageSubject = messageSubjectTextField.getText();
    		String messageBody = messageBodyTextArea.getText();
    		
    		InstructorMessage preNewMessage = new InstructorMessage(user, messageSubject, messageBody);
    		
    		// Check if message subject input is valid
    		String isMessageSubjectValid = preNewMessage.checkMessageSubjectInput(messageSubject);
    		
    		// Check if message body input is valid
			String isMessageBodyValid = preNewMessage.checkMessageBodyInput(messageBody);
			
			String recipientFullName = "";
			String recipientEmail = "";
			
			recipientFullName = recipientNameTextField.getText();
			recipientEmail = recipientEmailTextField.getText();
			User recipientUser = databaseHelper.getUserInfoByName(recipientFullName);
			
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
    			
    			if (recipientRoles[1] || recipientRoles[2] || recipientRoles[3] || recipientRoles[4] ) {
    				for(int i = 1; i < 5; i++) {
    					if(recipientRoles[i]) {
    						if(recipientRole != "") {
    							recipientRole += ", ";
    						}
    						switch(i) {
    							case 1:
    								recipientRole += "Student";
    								break;
    							case 2:
    								recipientRole += "Reviewer";
    								break;
    							case 3:
    								recipientRole += "Instructor";
    								break;
    							case 4:
    								recipientRole += "Staff";
    								break;
    							default:
    								break;
    						}
    					}
    				}
    				User recipient = new User(recipientUserName, recipientRoles, recipientEmail, recipientFirstName, recipientLastName);
    				
    				newMessage = new InstructorMessage(user, recipient, messageSubject, messageBody, LocalDateTime.now());
	    			int messageID = databaseHelper.addInstructorPrivateMessage(newMessage);
	    			
	    			newMessage.setMessageID(messageID);
	    			
	    			sentMessageObservable.add(newMessage);
	    			
	    			recipientNameTextField.clear();
	    			recipientEmailTextField.clear();
	    			messageSubjectTextField.clear();
	    			messageBodyTextArea.clear();
    			}
    			else {
    				invalidEmailOrRoleError.setVisible(true);
    				invalidEmailOrRoleError.setText("Recipient is not an Instructor, Staff, Student, or Reviewer, cannot send message.");
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
    		InstructorMessage messageToDelete = inbox.getSelectionModel().getSelectedItem();
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
    			int messageIDToDelete = messageToDelete.messageID();
    			databaseHelper.deleteInstructorPrivateMessageFromInbox(messageIDToDelete);
    			inboxObservable.remove(messageToDelete);
    		}
    	});
    	
    	// Reply to an existing message in the inbox
    	messageReplyButton.setOnAction(r -> {
    		InstructorMessage messageToReply = inbox.getSelectionModel().getSelectedItem();
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
    			boolean[] recipientRole;
    			int recipientMessageID;

    			
    			String prevMessageDetails = "\nOn " + messageToReply.timeSent().format(timeMessageSent) + " " + messageToReply.sender().getFirstName() + " " + messageToReply.sender().getLastName() + "<" + messageToReply.sender().getEmail() + "> wrote: \n";
    			
    			recipientNameTextField.setText(messageToReply.sender().getFirstName() + " " + messageToReply.sender().getLastName());
    			recipientEmailTextField.setText(messageToReply.sender().getEmail());
    			messageSubjectTextField.setText("Re: " + messageToReply.subjectText());
    			messageBodyTextArea.setText(prevMessageDetails + messageToReply.messageText() + "\n");
    			
    			recipientFirstName = messageToReply.sender().getFirstName();
    			recipientLastName = messageToReply.sender().getLastName();
    			recipientUserName = messageToReply.sender().getUserName();
    			recipientRole = messageToReply.sender().getRole();
    			recipientEmail = messageToReply.sender().getEmail();
    			recipientMessageID = messageToReply.messageID();
    			
    			sendMessageReplyButton.setVisible(true);
    			sendMessageReplyButton.setOnAction(s -> {
	    			String messageSubject = messageSubjectTextField.getText();
	        		String messageBody = messageBodyTextArea.getText();
	        		
	        		InstructorMessage preNewMessage = new InstructorMessage(user, messageSubject, messageBody);
	        		
	        		String isMessageSubjectValid = preNewMessage.checkMessageSubjectInput(messageSubject);
	    			String isMessageBodyValid = preNewMessage.checkMessageBodyInput(messageBody);
	    			
	    			// Both message subject and message body input are valid
	    			if (isMessageSubjectValid.equals("") && isMessageBodyValid.equals("")) {
	    				messageSubjectErrorLabel.setVisible(false);
	    	    		messageBodyErrorLabel.setVisible(false);
	    	    		
	    	    		User recipient = new User(recipientUserName, recipientRole, recipientEmail, recipientFirstName, recipientLastName);
	    	    		newMessage = new InstructorMessage(user, recipient, messageSubject, messageBody, LocalDateTime.now());
	    	    		
	    	    		int newMessageID = databaseHelper.addInstructorPrivateMessage(newMessage);
	    	    		
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
	    
	    // Button to return to the Instructor HomePage
    	Button returnButton = new Button("Return to Instructor Homepage");
    	// Return to instructorHomePage if button clicked
    	returnButton.setOnAction(r -> {
    		InstructorHomePage instructorHomePage = new InstructorHomePage(databaseHelper, user);
    		instructorHomePage.show(primaryStage, user);
    	    
    	});
    	
    	topHeaders.getChildren().addAll(countUnreadMessagesLabel, spacerLeft, inboxLabel, spacerRight);
    	centerButtons.getChildren().addAll(deleteInboxMessageButton, messageReplyButton, returnButton);
    	centerInbox.getChildren().addAll(messageTypeTabPane, topHeaders, inbox, centerButtons, inboxListViewErrorLabel);
    	rightButtons.getChildren().addAll(sendMessageButton);
    	rightSendMessage.getChildren().addAll(sendNewMessageLabel,recipientNameLabel, recipientNameTextField, recipientEmailLabel, recipientEmailTextField, messageSubjectLabel, messageSubjectTextField, messageBodyLabel, messageBodyTextArea, rightButtons, invalidEmailOrRoleError, messageSubjectErrorLabel, messageBodyErrorLabel);
    	messageBoard.setCenter(centerInbox);
    	messageBoard.setRight(rightSendMessage);
    	
    	messageTypeTabPane.getTabs().addAll(inboxTab, sentMessagesTab);
		
		
		Scene instructorPrivateMessages = new Scene(messageBoard, 1500, 600);
		
		// Set the scene to primary stage
	    primaryStage.setScene(instructorPrivateMessages);
	    primaryStage.setTitle("Instructor Private Messages");
	    primaryStage.show();
	}
}