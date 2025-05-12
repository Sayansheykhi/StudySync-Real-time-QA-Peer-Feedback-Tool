package application;

import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * GUI page that allows students to view, send, delete, and reply to private messages
 * exchanged with Reviewers. Also displays unread message count and supports search/sort.
 *
 * <p>Features include:</p>
 * <ul>
 *     <li>Viewing and loading message history</li>
 *     <li>Sending new messages or replying to existing ones</li>
 *     <li>Deleting sent messages</li>
 *     <li>Searching and sorting messages</li>
 *     <li>Displaying the count of unread messages</li>
 *     <li>Opening messages for a full view on double-click</li>
 * </ul>
 *
 * <p>Messages are pulled from the database using {@link DatabaseHelper}.</p>
 * 
 * @author Kylie Kim
 * @author Cristina Hooe
 * @version 3.0
 * @version 4.0
 * @since 2025-03-27
 */
public class StudentPrivateMessages {

    private User student;
    private DatabaseHelper dbHelper;
    private ListView<String> messageListView;
    
    private ObservableList<String> inboxObservable;
    private ObservableList<String> sentMessageObservable;
    
    private TextArea messageInput;
    private TextField recipientField, subjectField, searchField;
    private Label unreadCountLabel;
    private TabPane messageTypeTabPane;
    private Tab inboxTab;
    private Tab sentMessagesTab;

    private String replyToMessageID = null;

    /**
     * Constructs the StudentPrivateMessages UI.
     *
     * @param dbHelper a shared {@link DatabaseHelper} instance used for data access
     */
    public StudentPrivateMessages(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    /**
     * Displays the private messages page for the student.
     *
     * @param primaryStage the main JavaFX stage
     * @param student      the logged-in student
     */
    public void show(Stage primaryStage, User student) {
        this.student = student;

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        
        // Tab Pane for inbox and sent messages tabs
        messageTypeTabPane = new TabPane();

        // Tab to display the inbox List View
        inboxTab = new Tab("Inbox");
        inboxTab.setClosable(false);
        
        // Tab to display the sentMessages ListView
        sentMessagesTab = new Tab("Sent Messages");
        sentMessagesTab.setClosable(false);

        unreadCountLabel = new Label("Total Unread Messages: " + getUnreadMessageCount());
        inboxObservable = FXCollections.observableArrayList();
        sentMessageObservable = FXCollections.observableArrayList();
        
        messageListView = new ListView<>();
        messageListView.setPrefHeight(250);

        // Double-click to open message detail view
        messageListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedMessage = messageListView.getSelectionModel().getSelectedItem();
                if (selectedMessage != null) {
                    openMessageDetails(selectedMessage);
                }
            }
        });

        loadMessages();

        recipientField = new TextField();
        recipientField.setPromptText("Recipient Username");

        subjectField = new TextField();
        subjectField.setPromptText("Subject");

        messageInput = new TextArea();
        messageInput.setPromptText("Type your message...");
        messageInput.setWrapText(true);
        messageInput.setPrefHeight(200);

        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessage());

        Button deleteButton = new Button("Delete Selected");
        deleteButton.setOnAction(e -> deleteSelectedMessage());

        Button replyButton = new Button("Reply");
        replyButton.setOnAction(e -> prepareReply());

        searchField = new TextField();
        searchField.setPromptText("Search messages...");
        searchField.textProperty().addListener((obs, oldText, newText) -> searchMessages(newText));

        ComboBox<String> sortCombo = new ComboBox<>();
        sortCombo.getItems().addAll("Sort by Date", "Sort by Recipient", "Sort by Read/Unread");
        sortCombo.setOnAction(e -> sortMessages(sortCombo.getValue()));
        
        // Listener to load the ListView specific to which tab is selected, inbox or sent messages
        messageTypeTabPane.getSelectionModel().selectedItemProperty().addListener((observableList, oldTab, newTab) -> {
        	if (newTab == inboxTab) {
        		messageListView.setItems(inboxObservable);
        	}
        	else if (newTab == sentMessagesTab) {
        		messageListView.setItems(sentMessageObservable);
            }
        });
        
        // Listener to mark a private message as read once clicked on
        messageListView.getSelectionModel().selectedItemProperty().addListener((observableList, oldMessage, newMessage) -> {
        	if (newMessage != null) {
        		int index = -1;
        		String messageToMarkRead = messageListView.getSelectionModel().getSelectedItem();
        		String[] splitFormattedMessage = newMessage.split("\n");
        		
        		String toRole = "";
        		String fromRole = "";
        		
        		// Determine if its a message from Reviewer to this Student or another Student to this Student to call the correct database function
        		for (String formattedMessage : splitFormattedMessage) {
        			if (formattedMessage.startsWith("To [")) {
        				toRole = formattedMessage.substring(formattedMessage.indexOf('[') + 1, formattedMessage.indexOf(']'));
        			}
        			else if (formattedMessage.trim().startsWith("From [")) {
        				fromRole = formattedMessage.substring(formattedMessage.indexOf('[') + 1, formattedMessage.indexOf(']'));
        			}
        		}
        		
        		// Message is from another Student to this Student
        		if (toRole.equals("Student") && fromRole.equals("Student")) {
        			String to = splitFormattedMessage[0].replace("To [Student]: ", "").trim();
            		String from = splitFormattedMessage[1].replace("From [Student]: ", "").trim();
            		//String formattedDate = splitFormattedMessage[2].replace("Date: ", "").trim();
            		String messageSubject = splitFormattedMessage[3].replace("Message Subject: ", "").trim();
            		String messageBody = splitFormattedMessage[4].replace("Message Body: ", "").trim();

            		dbHelper.markStudentToStudentMessageAsRead(to, from, messageSubject, messageBody);
        		}
        		// Message is from Reviewer to this Student
        		else if (toRole.equals("Student") && fromRole.equals("Reviewer")) {
		        	String to = splitFormattedMessage[0].replace("To [Student]: ", "").trim();
					String from = splitFormattedMessage[1].replace("From [Reviewer]: ", "").trim();
					//String formattedDate = splitFormattedMessage[2].replace("Date: ", "").trim();
					String messageSubject = splitFormattedMessage[3].replace("Message Subject: ", "").trim();
					String messageBody = splitFormattedMessage[4].replace("Message Body: ", "").trim();
					
					dbHelper.markReviewerToStudentMessageAsRead(to, from, messageSubject, messageBody);
        		}
        		// Update the total count of unread messages
        		unreadCountLabel.setText("Total Unread Messages: " + getUnreadMessageCount());
        		
        		// Update ListView/Observable List
        		index = inboxObservable.indexOf(messageToMarkRead);
        		inboxObservable.set(index, messageToMarkRead);
        		messageListView.refresh();
        	}
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            StudentHomePage home = new StudentHomePage(dbHelper);
            home.show(primaryStage, student);
        });
        
        messageTypeTabPane.getTabs().addAll(inboxTab, sentMessagesTab);

        root.getChildren().addAll(
                new Label("Private Messages"),
                unreadCountLabel,
                searchField,
                sortCombo,
                messageTypeTabPane,
                messageListView,
                new Label("To:"), recipientField,
                new Label("Subject:"), subjectField,
                messageInput,
                new HBox(10, sendButton, replyButton, deleteButton, backButton)
        );

        Scene scene = new Scene(root, 700, 850);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Student Private Messages");
        primaryStage.show();
    }

    /**
     * Loads all private messages for the current student and updates the view.
     */
    private void loadMessages() {
    	inboxObservable.clear();
    	ArrayList<String> inboxMsgs = dbHelper.getPrivateMessagesForStudent(student.getUserName());
    	inboxObservable.addAll(inboxMsgs);
  
    	sentMessageObservable.clear();
    	ArrayList<String> sentMsgs = dbHelper.getPrivateMessagesSentByStudent(student.getUserName());
    	sentMessageObservable.addAll(sentMsgs);
    }

    /**
     * Sends a new private message or reply, validates input fields,
     * and refreshes the message list if successful.
     */
    private void sendMessage() {
        String recipient = recipientField.getText();
        String subject = subjectField.getText();
        String body = messageInput.getText();

        if (recipient.isEmpty() || subject.isEmpty() || body.isEmpty()) {
            showAlert("Please fill out all message fields.");
            return;
        }

        boolean success = dbHelper.sendPrivateMessage(
                student.getUserName(), recipient, subject, body, replyToMessageID
        );

        if (success) {
            showAlert("Message sent successfully.");
            recipientField.clear();
            subjectField.clear();
            messageInput.clear();
            replyToMessageID = null;
            loadMessages();
            unreadCountLabel.setText("Unread Messages: " + getUnreadMessageCount());
        } else {
            showAlert("Failed to send message.");
        }
    }

    /**
     * Prepares the UI for replying to the selected message.
     * Autofills the recipient and subject fields.
     */
    private void prepareReply() {
        String selected = messageListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        String recipient = extractRecipientFromMessage(selected);
        recipientField.setText(recipient);
        subjectField.setText("RE: " + extractSubjectFromMessage(selected));
        replyToMessageID = extractMessageIDFromMessage(selected);
    }

    /**
     * Deletes the currently selected message.
     */
    private void deleteSelectedMessage() {
        String selected = messageListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        String messageID = extractMessageIDFromMessage(selected);
        dbHelper.deletePrivateMessage(messageID);
        loadMessages();
        unreadCountLabel.setText("Unread Messages: " + getUnreadMessageCount());
    }

    /**
     * Filters messages based on the user's search query (case-insensitive).
     *
     * @param query The search query string
     */
    private void searchMessages(String query) {
    	Tab currentTab = messageTypeTabPane.getSelectionModel().getSelectedItem();
    	
        ArrayList<String> filtered = dbHelper.getPrivateMessagesForStudent(student.getUserName())
                .stream()
                .filter(m -> m.toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toCollection(ArrayList::new));
        
        if (currentTab == inboxTab) {
        	inboxObservable.setAll(filtered);
        }
        else if (currentTab == sentMessagesTab) {
        	sentMessageObservable.setAll(filtered);
        } 
    }

    /**
     * Sorts the message list based on the given criterion.
     *
     * @param criterion The selected sort option
     */
    private void sortMessages(String criterion) {
    	Tab currentTab = messageTypeTabPane.getSelectionModel().getSelectedItem();
        Comparator<String> comparator;

        switch (criterion) {
            case "Sort by Recipient":
                comparator = Comparator.comparing(this::extractRecipientFromMessage);
                break;
            case "Sort by Read/Unread":
                comparator = Comparator.comparing(msg -> msg.contains("[UNREAD]") ? 0 : 1);
                break;
            default: // Sort by Date
                comparator = Comparator.comparing(this::extractDateFromMessage).reversed();
        }
        
        if (currentTab == inboxTab) {
        	FXCollections.sort(inboxObservable, comparator);
        }
        else if (currentTab == sentMessagesTab) {
        	FXCollections.sort(sentMessageObservable, comparator);
        }        
    }

    /**
     * Returns the number of unread messages for the current student.
     *
     * @return unread message count
     */
    private int getUnreadMessageCount() {
        return dbHelper.getUnreadPrivateMessageCount(student.getUserName());
    }

    // ============================
    // Utility parsing methods
    // ============================

    /**
     * Extracts the recipient from the raw message string.
     *
     * @param msg raw message string
     * @return extracted recipient username
     */
    private String extractRecipientFromMessage(String msg) {
    	String[] splitFormattedMessage = msg.split("\n");
    	String toRole = "";
    	String fromRole = "";
    	String from = "";

    	// Determine if its a message from Reviewer to this Student or another Student to this Student
    	for (String formattedMessage : splitFormattedMessage) {
    		if (formattedMessage.startsWith("To [")) {
    			toRole = formattedMessage.substring(formattedMessage.indexOf('[') + 1, formattedMessage.indexOf(']'));
    		}
    		else if (formattedMessage.trim().startsWith("From [")) {
    			fromRole = formattedMessage.substring(formattedMessage.indexOf('[') + 1, formattedMessage.indexOf(']'));
    		}
    	}
    	
    	// Message is from another Student to this Student
    	if (toRole.equals("Student") && fromRole.equals("Student")) {
    		from = splitFormattedMessage[1].replace("From [Student]: ", "").trim();

    	}
    	// Message is from Reviewer to this Student
    	else if (toRole.equals("Student") && fromRole.equals("Reviewer")) {
    		from = splitFormattedMessage[1].replace("From [Reviewer]: ", "").trim();
    	}
        return from;
    }

    /**
     * Extracts the subject from the raw message string.
     *
     * @param msg raw message string
     * @return extracted subject
     */
    private String extractSubjectFromMessage(String msg) {
    	String[] splitFormattedMessage = msg.split("\n");
    	String messageSubject = splitFormattedMessage[3].replace("Message Subject: ", "").trim();
        return messageSubject;
    }

    /**
     * Extracts the message ID from the raw message string.
     *
     * @param msg raw message string
     * @return extracted message ID
     */
    private String extractMessageIDFromMessage(String msg) {
        return "123"; // TODO: implement actual parsing
    }

    /**
     * Extracts the timestamp from the raw message string.
     *
     * @param msg raw message string
     * @return extracted {@link LocalDateTime}
     */
    private LocalDateTime extractDateFromMessage(String msg) {
    	String[] splitFormattedMessage = msg.split("\n");
    	DateTimeFormatter timeMessageSent = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
    	String formattedDate = splitFormattedMessage[2].replace("Date: ", "").trim();
    	LocalDateTime dateLocalDateTime = LocalDateTime.parse(formattedDate, timeMessageSent);
        return dateLocalDateTime;
    }

    /**
     * Opens a dialog window showing the full details of the selected message.
     *
     * @param message the message string to display
     */
    private void openMessageDetails(String message) {
        Stage dialog = new Stage();
        dialog.setTitle("Message Details");

        String[] parts = message.split("\\|");

        String fromPart    = parts.length > 0 ? parts[0].trim() : "";
        String subjectPart = parts.length > 1 ? parts[1].trim() : "";
        String bodyPart    = parts.length > 2 ? parts[2].trim() : "";
        String datePart    = parts.length > 3 ? parts[3].trim() : "";
        String idPart      = parts.length > 4 ? parts[4].trim() : "";

        StringBuilder sb = new StringBuilder();
        sb.append(fromPart).append("\n");
        sb.append("Subject: ").append(subjectPart).append("\n");
        sb.append(datePart).append("\n");
        sb.append(idPart).append("\n\n");
        sb.append("Message:\n").append(bodyPart);

        TextArea textArea = new TextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);

        VBox layout = new VBox(textArea);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 400, 300);
        dialog.setScene(scene);
        dialog.show();
    }

    /**
     * Displays a simple popup alert with the provided message.
     *
     * @param message the alert content to show
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setContentText(message);
        alert.showAndWait();
    }
}