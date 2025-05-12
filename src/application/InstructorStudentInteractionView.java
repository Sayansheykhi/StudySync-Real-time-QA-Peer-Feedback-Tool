package application;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;

/**
 * Provides a unified dashboard for instructors to view and manage all
 * interactions with students and reviewers, including:
 * <ul>
 *   <li>Questions &amp; replies</li>
 *   <li>Answers</li>
 *   <li>Reviews</li>
 *   <li>Student↔Student private messages</li>
 *   <li>Reviewer↔Student private messages</li>
 *   <li>Hidden posts and muted users</li>
 * </ul>
 * 
 * @author Sajjad Sheykhi
 * @version 1.0
 */
public class InstructorStudentInteractionView {

    private final DatabaseHelper databaseHelper;
    private User user;
	protected String confirmDialog;

	/**
     * Constructs the instructor interaction view.
     *
     * @param databaseHelper  the {@link DatabaseHelper} instance for data access
     * @param user            the {@link User} representing the instructor
     */
    public InstructorStudentInteractionView(DatabaseHelper databaseHelper, User user) {
        this.databaseHelper = databaseHelper;
        this.user = user;
    }
    
    private static final DateTimeFormatter timeMessageSent =
    	    DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");

    /**
     * Builds and displays the JavaFX scene containing tabs for each type of
     * interaction (questions, answers, reviews, private messages, etc.). This
     * method sets up all ListViews, cell factories (for flagging, editing,
     * deleting), and footer controls.
     *
     * @param primaryStage  the primary {@link Stage} in which to show the UI
     * @param user          the {@link User} context (instructor) for data filtering
     */
    public void show(Stage primaryStage, User user){
    	DateTimeFormatter msgFmt = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
        this.user = user;


        // DAO & ListViews
        final Questions qHelper = new Questions(databaseHelper, user);
        final ListView<Question> questionsList = new ListView<>();
        final ListView<Answer> answersList = new ListView<>();
        final ListView<Review> reviewsList = new ListView<>();
        final ListView<String> ssList = new ListView<>();
        final ListView<String> rsList = new ListView<>();
        final ListView<String> mutedList = new ListView<>();
        final ListView<ReviewerMessage> messageListView = new ListView<>();
        
        ListView<String> privateMessages = new ListView<>();
        

        

        // 1) Build filter dropdown
        ComboBox<String> filterBy = new ComboBox<>(
            FXCollections.observableArrayList("All", "Student", "Reviewer")
        );
        filterBy.getSelectionModel().select("All");  // default

        // 2) Extract the populate logic into a method
        Runnable populate = () -> {
        	
        		
        	try {
        	    // --- Student↔Student ---
        	    Map<String,Integer> ssMap =
        	        databaseHelper.getAllStudentMessagesWithIds();
        	    ssList.setItems(FXCollections.observableArrayList(ssMap.keySet()));
        	    ssList.getProperties().put("idMap", ssMap);
        	    ssList.getProperties().put(
        	      "flaggedSet",
        	      new HashSet<>(databaseHelper.getFlaggedStudentMessagesWithIds().keySet())
        	    );

        	    // --- Reviewer↔Student ---
        	    Map<String,Integer> rsMap =
        	        databaseHelper.getAllReviewerMessagesWithIds();
        	    rsList.setItems(FXCollections.observableArrayList(rsMap.keySet()));
        	    rsList.getProperties().put("idMap", rsMap);
        	    rsList.getProperties().put(
        	      "flaggedSet",
        	      new HashSet<>(databaseHelper.getFlaggedReviewerMessagesWithIds().keySet())
        	    );

        	  } catch (SQLException ex) {
        	    ex.printStackTrace();
        	  }
        	
        	
        	
        		String mode = filterBy.getSelectionModel().getSelectedItem();
        		
        	    ObservableList<Question> qItems   = FXCollections.observableArrayList();
        	    ObservableList<Answer> aItems     = FXCollections.observableArrayList();
        	    ObservableList<Review> rItems     = FXCollections.observableArrayList();
        	    ObservableList<String> ssItems    = FXCollections.observableArrayList();
        	   
        	    ObservableList<String> mutedUsers = FXCollections.observableArrayList();
        	    ObservableList<String> rsItems = FXCollections.observableArrayList();
        	    
        	    // don’t create an empty msgs here:
        	    // ObservableList<ReviewerMessage> msgs = FXCollections.observableArrayList();

        	    if ("All".equals(mode) || "Student".equals(mode)) {
        	        qItems.addAll(qHelper.getAllReplies());
        	        qItems.addAll(databaseHelper.getAllQuestionsEvenHidden(user));
        	        aItems.addAll(databaseHelper.getAllAnswersEvenHidden(user));
        	        ssItems.addAll(databaseHelper.getAllMessagesBetweenStudents());
        	        rsItems.addAll(databaseHelper.getAllMessagesBetweenReviewersAndStudents());
        	        rItems.addAll(databaseHelper.getAllReviewsEvenHidden(user));
        	        
        	    }

        	    if ("All".equals(mode) || "Reviewer".equals(mode)) {
        	    	rsItems.addAll(databaseHelper.getAllMessagesBetweenReviewersAndStudents());
        	    	rItems.addAll(databaseHelper.getAllReviewsEvenHidden(user));
        	    	
        	    }


        	    try {
        	        if ("All".equals(mode)) {
        	            mutedUsers.addAll(databaseHelper.getMutedUsers());
        	        } else {
        	            mutedUsers.addAll(databaseHelper.getMutedUsersByRole(mode));
        	        }
        	    } catch (SQLException ex) {
        	        ex.printStackTrace();
        	    }

        	    reviewsList.setItems(rItems);
        	    questionsList.setItems(qItems);
        	    answersList.setItems(aItems);
        	    reviewsList.setItems(rItems);
        	    mutedList.setItems(mutedUsers);
        	    

        	};

    
        
        
        // 3) Hook the listener to call that method
        filterBy.getSelectionModel().selectedItemProperty().addListener((obs, old, nw) -> populate.run());

        // 4) Call it once now to populate “All” on startup
        populate.run();

        /**
         * Configures the cell factory for the Questions & Replies {@link ListView}.
         * <p>
         * Each cell will render a {@link Question} by displaying:
         * <ul>
         *   <li>The author’s name, question subject, and body text.</li>
         *   <li>A flag icon and italicized reason label if the question is flagged.</li>
         *   <li>An “Unflag” button that clears the flag via {@link DatabaseHelper#clearQuestionFlag(int)}.</li>
         *   <li>An “Unhide” button that clears the hidden state via {@link DatabaseHelper#clearHiddenQuestion(int)}.</li>
         *   <li>A “Delete” button that confirms and removes the question via {@link DatabaseHelper#deleteQuestion(int)}.</li>
         *   <li>An “Edit” button that shows a dialog to update the question title/body and applies changes via {@link DatabaseHelper#editQuestion(String, String, int)}.</li>
         *   <li>A “Private Message” button that opens the instructor-to-student messaging dialog.</li>
         * </ul>
         * After any action (unflag, unhide, delete, edit), the shared {@code populate} Runnable
         * is invoked to refresh the list contents.
         */
        
        questionsList.setCellFactory(lv -> new ListCell<Question>() {
            private final Label lbl       = new Label();
            
            private final Label reasonLbl = new Label();
            private final Label flagIcon  = new Label("🚩");
            private final Button unflagBtn = new Button("Unflag");
            private final Button deleteBtn = new Button("Delete");
            private final Button editBtn = new Button("Edit");
            private final Region spacer = new Region();
            private final Region spacer2   = new Region();
            private final VBox cellBox = new VBox(4);
            private final HBox box  = new HBox(8,lbl ,deleteBtn , editBtn);
            private final Button instructorPrivateMessagesView = new Button("Private Message");
            private final Button unhideBtn = new Button("Unhide");
            {
            	
                lbl.setWrapText(true);
                reasonLbl.setWrapText(true);
                flagIcon.setTooltip(new Tooltip());
                reasonLbl.setStyle("-fx-font-style: italic; -fx-font-size: 11px;");
                flagIcon.setStyle("-fx-font-size: 16px;");
                unflagBtn.setStyle("-fx-right-20px;");
                HBox.setHgrow(spacer, Priority.ALWAYS);
                HBox.setHgrow(spacer2, Priority.ALWAYS);
                
                unflagBtn.setOnAction(e -> {
                    Question q = getItem();
                    if (q != null) {
                        databaseHelper.clearQuestionFlag(q.getQuestionID());
                        populate.run();   // re‑populate the list
                    }
                });
                unhideBtn.setOnAction(e ->{
                	Question q = getItem();
                	if (q != null) {
                		databaseHelper.clearHiddenQuestion(q.getQuestionID());
                	    populate.run();
                	}
                });
                deleteBtn.setOnAction(e -> {
                	  Question q = getItem();
                	  if (q != null
                	      && InstructorStudentInteractionView.this.confirmDialog(
                	           "Are you sure you want to delete this question?"))
                	  {
                	    databaseHelper.deleteQuestion(q.getQuestionID());
                	    populate.run();
                	  }
                	});

                	editBtn.setOnAction(e -> {
                	  Question q = getItem();
                	  if (q == null) return;

                	  // 1) build and show the dialog
                	  Dialog<Pair<String,String>> dlg = new Dialog<>();
                	  dlg.setTitle("Edit Question");
                	  dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                	  TextField titleField = new TextField(q.getQuestionTitle());
                	  TextArea bodyArea      = new TextArea(q.getQuestionBody());
                	  bodyArea.setWrapText(true);

                	  VBox content = new VBox(8,
                	    new Label("Title"), titleField,
                	    new Label("Body"),  bodyArea
                	  );
                	  content.setPadding(new Insets(10));
                	  dlg.getDialogPane().setContent(content);

                	  dlg.setResultConverter(bt -> {
                	    if (bt == ButtonType.OK)
                	      return new Pair<>(titleField.getText(), bodyArea.getText());
                	    return null;
                	  });

                	  // 2) actually show it and wait
                	  Optional<Pair<String,String>> result = dlg.showAndWait();

                	  // 3) if user clicked OK, apply the edit and refresh
                	  result.ifPresent(pair -> {
                	    databaseHelper.editQuestion(
                	      pair.getKey(),       // new title
                	      pair.getValue(),     // new body
                	      q.getQuestionID()
                	    );
                	    populate.run();
                	  });
                	});
         instructorPrivateMessagesView.setOnAction( e-> {
                	new InstructorPrivateMessages(databaseHelper).show(primaryStage, user);
                	});
                
            }

            @Override
            protected void updateItem(Question q, boolean empty) {
                super.updateItem(q, empty);
                if (empty || q == null) {
                    setGraphic(null);
                } else {
                    lbl.setText(String.format("%s %s%s%s", "Author: " +q.getStudentFirstName(), q.getStudentLastName() + "\nSubject: ", q.getQuestionTitle() + "\nQuestion: ", q.getQuestionBody()));
                    lbl.setWrapText(true);
                    box.getChildren().setAll(lbl);
                    
                    // 2) flagged?
                    if (q.getIsFlagged()) {
                      reasonLbl.setText("Reason: [" + q.getReasonIsFlagged() + "]");
                      box.getChildren().addAll(flagIcon, reasonLbl);
                    }

                    // 3) now spacer (pushes following buttons to the right)
                    box.getChildren().add(spacer);

                    // 4) unflag (only if flagged)
                    if (q.getIsFlagged()) {
                      box.getChildren().add(unflagBtn);
                     // box.getChildren().add(privateMsgBtn);
                    }

                    // 5) unhide (only if hidden)
                    if (q.getIsHidden()) {
                      lbl.setStyle("-fx-text-fill: gray;");
                      box.getChildren().add(unhideBtn);
                    }
                    
                   
                    else {
                    	lbl.setStyle("-fx-text-fill: black;");
                        box.setOpacity(1.0);
                    }
                    
                    setGraphic(box);
                }
            }
        });
        
        /**
         * Configures the cell factory for the Answers {@link ListView}.
         * <p>
         * Each cell will render an {@link Answer} by displaying:
         * <ul>
         *   <li>The author’s full name and answer text.</li>
         *   <li>A flag icon and italicized reason label if the answer is flagged.</li>
         *   <li>An “Unflag” button that clears the flag via {@link DatabaseHelper#clearAnswerFlag(int)}.</li>
         *   <li>An “Unhide” button that clears the hidden state via {@link DatabaseHelper#clearHiddenAnswer(int)}.</li>
         *   <li>A “Private Message” button that opens the instructor-to-student messaging dialog.</li>
         * </ul>
         * After any action (unflag or unhide), the shared {@code populate} {@link Runnable} is invoked
         * to refresh the list contents.
         */
        
        answersList.setCellFactory(lv -> new ListCell<Answer>() {
            private final Label lbl       = new Label();
            private final Label reasonLbl = new Label();
            private final HBox box        = new HBox(5);
            private final Label flagIcon  = new Label("🚩");
            private final Region spacer   = new Region();
            private final Button unflagBtn= new Button("Unflag");
            private final Button unhideBtn= new Button("Unhide");
            private final Button pmBtn    = new Button("Private Message");

            {
              lbl.setWrapText(true);
              reasonLbl.setWrapText(true);
              flagIcon.setStyle("-fx-font-size:16px;");
              HBox.setHgrow(spacer, Priority.ALWAYS);

              unflagBtn.setOnAction(e -> {
                Answer a = getItem();
                if (a != null) {
                  databaseHelper.clearAnswerFlag(a.getAnswerID());
                  populate.run();
                }
              });
              unhideBtn.setOnAction(e -> {
                Answer a = getItem();
                if (a != null) {
                  databaseHelper.clearHiddenAnswer(a.getAnswerID());
                  populate.run();
                }
              });
              pmBtn.setOnAction(e -> {
                new InstructorPrivateMessages(databaseHelper)
                  .show(primaryStage, user);
              });
            }

            @Override
            protected void updateItem(Answer a, boolean empty) {
              super.updateItem(a, empty);
              if (empty || a == null) {
                setGraphic(null);
                return;
              }

              // 1) Clear last frame’s content
              box.getChildren().clear();

              // 2) Author + text
              lbl.setText("Author: " + a.getStudentFirstName() + " " +
                          a.getStudentLastName() + "\nAnswer: " + a.getAnswerText());
              lbl.setStyle(a.getIsHidden()
                ? "-fx-text-fill: gray;"
                : "-fx-text-fill: black;");
              box.getChildren().add(lbl);

              // 3) If it’s flagged, show flag + reason + Unflag + PM
              if (a.getIsFlagged()) {
                reasonLbl.setText("Reason: [" + a.getReasonIsFlagged() + "]");
                box.getChildren().addAll(flagIcon, reasonLbl);
              }

              // 4) spacer always to push “Unhide” to the far right
              box.getChildren().add(spacer);
              
              if(a.getIsFlagged()) {
            	 box.getChildren().addAll(unflagBtn, pmBtn);
              }

              // 5) If it’s hidden, show Unhide
              if (a.getIsHidden()) {
                box.getChildren().add(unhideBtn);
              }

              setGraphic(box);
            }
        });



        
        /**
         * Configures the cell factory for the Reviews {@link ListView}.
         * <p>
         * Each cell will render a {@link Review} by displaying:
         * <ul>
         *   <li>The reviewer’s full name and review text.</li>
         *   <li>A flag icon and italicized reason label if the review is flagged.</li>
         *   <li>An “Unflag” button that clears the flag via
         *       {@link DatabaseHelper#clearReviewFlag(int)}.</li>
         *   <li>An “Unhide” button that clears the hidden state via
         *       {@link DatabaseHelper#clearHiddenReview(int)}.</li>
         *   <li>A “Private Message” button that opens the instructor-to-student messaging dialog.</li>
         * </ul>
         * After any action (unflag or unhide), the shared {@code populate} {@link Runnable} is invoked
         * to refresh the list contents.
         */
        
        reviewsList.setCellFactory(lv -> new ListCell<Review>() {
            private final Label lbl = new Label();
            private final Label reasonLbl = new Label();
            private final HBox box = new HBox(5);
            private final Label flagIcon = new Label("🚩");
            private final Region spacer = new Region();
            private final Button instructorPrivateMessageView = new Button("Private Message");
            private final Button unflagBtn = new Button("Unflag");
            private final Button unhideBtn = new Button("Unhide");

            {
                // single initializer block is fine
                lbl.setWrapText(true);
                reasonLbl.setWrapText(true);
                flagIcon.setStyle("-fx-font-size:16px;");
                HBox.setHgrow(spacer, Priority.ALWAYS);

                instructorPrivateMessageView.setOnAction(e -> {
                    new InstructorPrivateMessages(databaseHelper)
                        .show(primaryStage, user);
                });

                unflagBtn.setOnAction(e -> {
                    Review r = getItem();
                    if (r != null) {
                        databaseHelper.clearReviewFlag(r.getReviewID());
                        populate.run();
                    }
                });

                unhideBtn.setOnAction(e -> {
                    Review r = getItem();
                    if (r != null) {
                        databaseHelper.clearHiddenReview(r.getReviewID());
                        populate.run();
                    }
                });
            }

            @Override
            protected void updateItem(Review r, boolean empty) {
                super.updateItem(r, empty);
                if (empty || r == null) {
                    setGraphic(null);
                    return;
                }
                box.getChildren().clear();

                lbl.setText(r.getReviewerFirstName() + " " +
                            r.getReviewerLastName() + "\n" +
                            "Answer: " + r.getReviewBody());
                box.getChildren().setAll(lbl);

               
                
                if (r.getIsFlagged()) {
                    reasonLbl.setText("Reason: [" + r.getReasonIsFlagged() + "]");
                    box.getChildren().addAll(flagIcon, reasonLbl);
                  }

                  box.getChildren().add(spacer);
                  
                  if(r.getIsFlagged()) {
                	 box.getChildren().addAll(unflagBtn);
                  }

                if (r.getIsHidden()) {
                    // greyed‐out hidden state
                    lbl.setStyle("-fx-text-fill: gray;");
                    box.setOpacity(1);
                    box.getChildren().add(unhideBtn);
                } else {
                    // normal state
                    lbl.setStyle("-fx-text-fill: black;");
                    box.setOpacity(1.0);
                }

                setGraphic(box);
            }
        });
        
        
        
        

        /**
         * Configures the cell factory for the "Muted Users" {@link ListView}.
         * <p>
         * Each cell consists of:
         * <ul>
         *   <li>A speaker-muted icon and the username.</li>
         *   <li>An "Unmute" button that:
         *     <ul>
         *       <li>Calls {@link DatabaseHelper#unmuteUser(String)} to unmute the user.</li>
         *       <li>Clears all hidden content for that user via
         *           {@link DatabaseHelper#clearHiddenQuestionsForUser(String)},
         *           {@link DatabaseHelper#clearHiddenRepliesForUser(String)},
         *           {@link DatabaseHelper#clearHiddenAnswersForUser(String)}, and
         *           {@link DatabaseHelper#clearHiddenReviewsForUser(String)}.</li>
         *       <li>Invokes the shared {@code populate} {@link Runnable} to refresh the view.</li>
         *     </ul>
         *   </li>
         *   <li>A "Private Message" button that opens the messaging dialog via
         *       {@link InstructorPrivateMessages#show(Stage, User)}.</li>
         * </ul>
         */

        mutedList.setCellFactory(lv -> new ListCell<String>() {
            private final Label lbl         = new Label();
            private final HBox box          = new HBox(5);
            private final Label muteIcon    = new Label("🔇");
            private final Button unmuteBtn  = new Button("Unmute");
            private final Region spacer     = new Region();
            private final Button pmBtn      = new Button("Private Message");

            {
                // styling & layout
                lbl.setWrapText(true);
                muteIcon.setStyle("-fx-font-size: 16px;");
                HBox.setHgrow(spacer, Priority.ALWAYS);

                // unmute action
                unmuteBtn.setOnAction(e -> {
                	  String name = getItem();
                	  if (name != null) {
                	    databaseHelper.unmuteUser(name);
                	    // now un-hide their content
                	    databaseHelper.clearHiddenQuestionsForUser(name);
                	    databaseHelper.clearHiddenRepliesForUser(name);
                	    databaseHelper.clearHiddenAnswersForUser(name);
                	    databaseHelper.clearHiddenReviewsForUser(name);
                	    populate.run();
                	  }
                	});
                

                // private‑message action
                pmBtn.setOnAction(e ->
                    new InstructorPrivateMessages(databaseHelper)
                        .show(primaryStage, user)
                );
            }

            @Override
            protected void updateItem(String username, boolean empty) {
                super.updateItem(username, empty);
                if (empty || username == null) {
                    setGraphic(null);
                } else {
                    lbl.setText(username);
                    box.getChildren().setAll(
                        muteIcon,
                        lbl,
                        spacer,
                        unmuteBtn,
                        pmBtn
                    );
                    setGraphic(box);
                }
            }
        });
        
     // ------------------------------------
     // CellFactory for Student↔Student Messages
     
        ssList.setCellFactory(lv -> new ListCell<String>() {
            private final Label content   = new Label();
            private final Label flagIcon  = new Label("🚩");
            private final Label reasonLbl = new Label();
            private final Button unflag   = new Button("Unflag");
            private final HBox box        = new HBox(8, content);

            {
                content.setWrapText(true);
                flagIcon.setStyle("-fx-font-size:16px;");
                reasonLbl.setStyle("-fx-font-style:italic; -fx-font-size:11px;");
                HBox.setHgrow(content, Priority.ALWAYS);

                unflag.setOnAction(e -> {
                    String raw = getItem();
                    if (raw == null) return;

                    // parse to the same pieces...
                    String[] lines = raw.split("\n");
                    String to   = lines[0].substring("To [Student]: ".length());
                    String from = lines[1].substring("From [Student]: ".length());
                    LocalDateTime ts = LocalDateTime.parse(
                        lines[2].substring("Date: ".length()), timeMessageSent
                    );
                    String subj = lines[3].substring("Message Subject: ".length());
                    String body = lines[4].substring("Message Body: ".length());

                    // clear the flag
                    databaseHelper.clearStudentPrivateMessageFlag(to, from, ts, subj, body);

                    // reload everything (including the flaggedSet)
                    populate.run();
                });
            }

            @Override
            protected void updateItem(String raw, boolean empty) {
                super.updateItem(raw, empty);
                if (empty || raw == null) {
                    setGraphic(null);
                    return;
                }

                content.setText(raw);
                box.getChildren().setAll(content);

                // pull the flaggedSet out of the ListView’s properties
                @SuppressWarnings("unchecked")
                Set<String> flaggedSet = (Set<String>) ssList.getProperties()
                                                             .get("flaggedSet");

                if (flaggedSet != null && flaggedSet.contains(raw)) {
                    // only hit the DB now to get the reason
                    String[] lines = raw.split("\n");
                    String to   = lines[0].substring("To [Student]: ".length());
                    String from = lines[1].substring("From [Student]: ".length());
                    LocalDateTime ts = LocalDateTime.parse(
                        lines[2].substring("Date: ".length()), timeMessageSent
                    );
                    String subj = lines[3].substring("Message Subject: ".length());
                    String body = lines[4].substring("Message Body: ".length());

                    String reason = databaseHelper.getReasonStudentPrivateMessageFlagged(
                        to, from, ts, subj, body
                    );
                    reasonLbl.setText("Reason: " + reason);
                    box.getChildren().addAll(flagIcon, reasonLbl, unflag);
                }

                setGraphic(box);
            }
        });

        
        /**
         * Configures the cell factory for the “Reviewer↔Student Messages” {@link ListView} ({@code rsList}).
         * <p>
         * Each cell displays:
         * <ul>
         *   <li>The full raw message text in a wrapping {@link Label}.</li>
         *   <li>If the message is flagged (determined by the {@code flaggedSet} in the ListView’s properties):
         *     <ul>
         *       <li>A 🚩 icon ({@code flagIcon}).</li>
         *       <li>The reason text in an italic {@code reasonLbl}.</li>
         *       <li>An “Unflag” {@link Button} which, when pressed:
         *         <ul>
         *           <li>Parses the message’s recipient, sender, timestamp, subject, and body.</li>
         *           <li>Calls {@link DatabaseHelper#clearReviewerPrivateMessageFlag(String,String,LocalDateTime,String,String)}
         *               to clear the flag in the database.</li>
         *           <li>Invokes the shared {@code populate} {@link Runnable} to refresh both lists and flagged sets.</li>
         *         </ul>
         *       </li>
         *     </ul>
         *   </li>
         * </ul>
         */
     
        rsList.setCellFactory(lv -> new ListCell<String>() {
            private final Label content   = new Label();
            private final Label flagIcon  = new Label("🚩");
            private final Label reasonLbl = new Label();
            private final Button unflag   = new Button("Unflag");
            private final HBox box        = new HBox(8, content);

            {
                content.setWrapText(true);
                flagIcon.setStyle("-fx-font-size:16px;");
                reasonLbl.setStyle("-fx-font-style:italic; -fx-font-size:11px;");
                HBox.setHgrow(content, Priority.ALWAYS);

                unflag.setOnAction(e -> {
                    String raw = getItem();
                    if (raw == null) return;

                    String[] lines = raw.split("\n");
                    String receiver = lines[0]
                        .substring(lines[0].indexOf("]:") + 2).trim();
                    String sender   = lines[1]
                        .substring(lines[1].indexOf("]:") + 2).trim();
                    LocalDateTime ts = LocalDateTime.parse(
                        lines[2].substring("Date: ".length()).trim(),
                        timeMessageSent
                    );
                    String subj = lines[3]
                        .substring("Message Subject: ".length()).trim();
                    String body = lines[4]
                        .substring("Message Body: ".length()).trim();

                    // clear the flag in DB
                    databaseHelper.clearReviewerPrivateMessageFlag(
                        receiver, sender, ts, subj, body
                    );
                    // reload both lists & flagged sets
                    populate.run();
                });
            }

            @Override
            protected void updateItem(String raw, boolean empty) {
                super.updateItem(raw, empty);
                if (empty || raw == null) {
                    setGraphic(null);
                    return;
                }

                content.setText(raw);
                box.getChildren().setAll(content);

                @SuppressWarnings("unchecked")
                Set<String> flaggedSet =
                    (Set<String>) rsList.getProperties().get("flaggedSet");

                if (flaggedSet != null && flaggedSet.contains(raw)) {
                    // only one more DB call to get the reason
                    String[] lines = raw.split("\n");
                    String receiver = lines[0]
                        .substring(lines[0].indexOf("]:") + 2).trim();
                    String sender   = lines[1]
                        .substring(lines[1].indexOf("]:") + 2).trim();
                    LocalDateTime ts = LocalDateTime.parse(
                        lines[2].substring("Date: ".length()).trim(),
                        timeMessageSent
                    );
                    String subj = lines[3]
                        .substring("Message Subject: ".length()).trim();
                    String body = lines[4]
                        .substring("Message Body: ".length()).trim();

                    String reason = databaseHelper
                        .getReasonReviewerPrivateMessageFlagged(
                            receiver, sender, ts, subj, body
                        );
                    reasonLbl.setText("Reason: " + reason);
                    box.getChildren().addAll(flagIcon, reasonLbl, unflag);
                }

                setGraphic(box);
            }
        });
    
     			
        
        /**
         * Configures the cell factory for the “Muted Users” {@link ListView} ({@code mutedList}).
         * <p>
         * Each cell displays:
         * <ul>
         *   <li>A “🔇” icon indicating the user is muted.</li>
         *   <li>The muted username in a wrapping {@link Label}.</li>
         *   <li>An “Unmute” {@link Button} which, when pressed:
         *     <ul>
         *       <li>Calls {@link DatabaseHelper#unmuteUser(String)}.</li>
         *       <li>Clears all hidden content for that user
         *           ({@code clearHiddenQuestionsForUser}, {@code clearHiddenRepliesForUser}, etc.).</li>
         *       <li>Invokes the shared {@code populate} {@link Runnable} to refresh the dashboard.</li>
         *     </ul>
         *   </li>
         *   <li>A “Private Message” {@link Button} which opens a private messaging dialog
         *       ({@link InstructorPrivateMessages}).</li>
         * </ul>
         */
  
     
       
        mutedList.setCellFactory(lv -> new ListCell<String>() {
            private final Label lbl = new Label();
            private final HBox row = new HBox(10);
            private final Label muteIcon = new Label("🔇");
            private final Region spacer = new Region();
            private final Button unMuteBtn = new Button("Unmute");
            private final Button instructorPrivateMessagesView = new Button("Private Message");

            {
                lbl.setWrapText(true);
                muteIcon.setStyle("-fx-font-size:16px;");
                HBox.setHgrow(spacer, Priority.ALWAYS);

                unMuteBtn.setOnAction(e -> {
                    String name = getItem();
                    if (name != null) {
                        databaseHelper.unmuteUser(name);
                        populate.run();   // now reloads the mutedList (and everything else)
                    }
                });

                instructorPrivateMessagesView.setOnAction(e -> {
                    new InstructorPrivateMessages(databaseHelper)
                        .show(primaryStage, user);
                });
            }

            @Override
            protected void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty || name == null) {
                    setGraphic(null);
                } else {
                    lbl.setText(name);
                    row.getChildren().setAll(
                        muteIcon,
                        lbl,
                        spacer,
                        unMuteBtn,
                        instructorPrivateMessagesView
                    );
                    row.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(row);
                }
            }
        });
        
        

        /**
         * Initializes the dashboard’s main TabPane with six non-closable tabs, each displaying
         * a different ListView:
         * <ul>
         *   <li><b>Questions & Replies</b> – shows all questions and replies.</li>
         *   <li><b>Answers</b> – shows all answers.</li>
         *   <li><b>Reviews</b> – shows all reviews.</li>
         *   <li><b>Student↔Student Messages</b> – shows private messages between students.</li>
         *   <li><b>Reviewer↔Student Messages</b> – shows messages between reviewers and students.</li>
         *   <li><b>Muted Users</b> – shows the list of muted users.</li>
         * </ul>
         * Each tab’s content is wrapped in a VBox with 10px spacing and 10px padding.
         */
        
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().addAll(
            new Tab("Questions & Replies",     new VBox(10, questionsList)),
            new Tab("Answers",                 new VBox(10, answersList)),
            new Tab("Reviews",                 new VBox(10, reviewsList)),
            new Tab("Student↔Student Messages", new VBox(10, ssList)),
            new Tab("Reviewer↔Student Messages", new VBox(10, rsList)),
            new Tab("Muted Users",             new VBox(10, mutedList))
        );
        tabs.getTabs().forEach(tab -> ((VBox)tab.getContent()).setPadding(new Insets(10)));

        /**
         * Creates the footer HBox containing:
         * <ul>
         *   <li><b>Admin Actions</b> – opens the admin actions list.</li>
         *   <li><b>Back to Home</b> – returns to the instructor home page.</li>
         *   <li><b>Delete</b> – deletes the currently selected item based on the active tab:
         *     <ul>
         *       <li>Questions &gt; deletes the selected question.</li>
         *       <li>Student↔Student &gt; deletes the selected private message by ID.</li>
         *       <li>Reviewer↔Student &gt; deletes the selected reviewer message by ID.</li>
         *       <li>Answers &gt; deletes the selected answer.</li>
         *       <li>Reviews &gt; deletes the selected review.</li>
         *     </ul>
         *   </li>
         *   <li><b>Edit</b> – opens editing dialogs appropriate to the active tab’s item.</li>
         *   <li><b>Private Messages</b> – opens the private messaging dialog.</li>
         * </ul>
         */
        Button adminBtn = new Button("Admin Actions");
        adminBtn.setOnAction(e -> new ViewAdminActionList(databaseHelper, user).show(primaryStage, user));
        Button backBtn = new Button("Back to Home");
        backBtn.setOnAction(e -> new InstructorHomePage(databaseHelper, user).show(primaryStage, user));
        Button deleteSelectedBtn = new Button("Delete");
        deleteSelectedBtn.setOnAction(e -> {
        	Tab tab = tabs.getSelectionModel().getSelectedItem();
        	
        	//Questions
        	if(tab.getText().equals("Questions & Replies")) {
        		Question q = questionsList.getSelectionModel().getSelectedItem();
        		if( q != null && confirmDialog("Are you sure you want to delete this question?")) {
        			databaseHelper.deleteQuestion(q.getQuestionID());
        			populate.run();
        		}
        	}
        	
        	//Student↔Student Messages
        	if (tab.getText().equals("Student↔Student Messages")) {
        	    String raw = ssList.getSelectionModel().getSelectedItem();
        	    if (raw != null && confirmDialog("Delete this private message?")) {
        	        @SuppressWarnings("unchecked")
        	        Map<String,Integer> idMap =
        	            (Map<String,Integer>) ssList.getProperties().get("idMap");
        	        Integer messageID = idMap.get(raw);
        	        if (messageID != null) {
        	            databaseHelper.deletePrivateMessage(String.valueOf(messageID));
        	            populate.run();
        	        }
        	    }
        	}
        	
        	//Reviewer↔Student Messages
        	if (tab.getText().equals("Reviewer↔Student Messages")) {
        	    String raw = rsList.getSelectionModel().getSelectedItem();
        	    if (raw != null && confirmDialog("Delete this reviewer message?")) {
        	        @SuppressWarnings("unchecked")
        	        Map<String,Integer> idMap =
        	            (Map<String,Integer>) rsList.getProperties().get("idMap");
        	        Integer msgId = idMap.get(raw);
        	        if (msgId != null) {
        	            databaseHelper.deleteReviewerMessage(msgId);
        	            populate.run();
        	        }
        	    }
        	}
        	
        	
        	//answers
        	if(tab.getText().equals("Answers")) {
        		Answer a = answersList.getSelectionModel().getSelectedItem();
        		if(a != null && confirmDialog("Are you sure you want to delete this Answer?")) {
        			databaseHelper.deleteAnswer(a.getAnswerID());
        			populate.run();
        		}
        	}
        	
        	//Review
        	
        	if(tab.getText().equals("Reviews")) {
        		Review r = reviewsList.getSelectionModel().getSelectedItem();
        		if( r != null && confirmDialog("Are you sure you want to delete this review?")) {
        			databaseHelper.deleteReview(r.getReviewID());
        			populate.run();
        		}
        	}
        });
        
        
        /**
         * Creates the “Edit” button in the footer and wires its action to open
         * an edit dialog appropriate to the currently active tab.
         * <p>
         * When clicked:
         * <ul>
         *   <li>If the “Questions &amp; Replies” tab is active and a Question is
         *       selected, pops up a dialog pre-filled with its title and body.
         *       On OK, saves changes via {@code databaseHelper.editQuestion(...)}
         *       and repopulates the view.</li>
         *   <!-- You would add similar handling here for Answers, Reviews, etc. -->
         * </ul>
         */
        Button editSelectedBtn   = new Button("Edit");
        
        editSelectedBtn.setOnAction(e->{
        	Tab tab = tabs.getSelectionModel().getSelectedItem();
        	
        	//Question 
        	if(tab.getText().equals("Questions & Replies")) {
        		Question q = questionsList.getSelectionModel().getSelectedItem();
        		if(q == null)return;
        		
        		// pop up exactly the same edit‐dialog you wrote in the cell‑factory
        	    Dialog<Pair<String,String>> dlg = new Dialog<>();
        	    dlg.setTitle("Edit Question");
        	    dlg.initOwner(primaryStage);
        	    dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        	    TextField titleField = new TextField(q.getQuestionTitle());
        	    TextArea bodyArea = new TextArea(q.getQuestionBody());
        	    bodyArea.setWrapText(true);
        	    dlg.getDialogPane().setContent(new VBox(8,
        	    		new Label("Title"),titleField,
        	    		new Label("Body"), bodyArea
        	    		));
        	    dlg.setResultConverter(bt -> bt == ButtonType.OK
        	    		? new Pair<>(titleField.getText(), bodyArea.getText())
        	    		:null
        	    );
        		
        	    dlg.showAndWait().ifPresent(pair -> {
        	    	databaseHelper.editQuestion(pair.getKey(), pair.getValue(), q.getQuestionID());
        	    	populate.run();
        	    });
        	}
        	
        	 if(tab.getText().equals("Reviews")) {
        		 Review r = reviewsList.getSelectionModel().getSelectedItem();
        		 if(r == null) return;
        		 Dialog<String> dlg = new Dialog<>();
        		 dlg.setTitle("Edit Review");
        		 dlg.initOwner(primaryStage);
        		 dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        		 // 2) Build your TextArea
        		 TextArea bodyArea = new TextArea(r.getReviewBody());
        		 bodyArea.setWrapText(true);
        		 bodyArea.setPrefRowCount(8);      // make it big!
        		 bodyArea.setPrefColumnCount(40);

        		 dlg.getDialogPane().setContent(new VBox(10,
        		     new Label("Review:"),
        		     bodyArea
        		 ));

        		 // 3) Convert the result to a String
        		 dlg.setResultConverter(btn -> btn == ButtonType.OK
        		     ? bodyArea.getText()
        		     : null
        		 );

        		 // 4) Now newBody is a String and matches editReview
        		 dlg.showAndWait().ifPresent(newBody -> {
        		     databaseHelper.editReview(newBody, r.getReviewID());
        		     populate.run();
        		 });
	
        	}
        	
        	if (tab.getText().equals("Answers")) {
        	    Answer a = answersList.getSelectionModel().getSelectedItem();
        	    if (a == null) return;

        	    // 1) Create a TextInputDialog, pre‑filled with the old answer
        	    TextInputDialog dlg = new TextInputDialog(a.getAnswerText());
        	    dlg.setTitle("Edit Answer");
        	    //dlg.setHeaderText(null);          // no extra header
        	    dlg.setContentText("Answer:");    // single prompt
        	    
        	 // grab the underlying TextField
        	    TextField editor = dlg.getEditor();
        	   
        	    editor.setPrefColumnCount(50);       // more columns
        	    editor.setPrefWidth(400);            // or pixels

        	    // if you need the whole dialog pane wider too:
        	    dlg.getDialogPane().setPrefWidth(450);

        	    // if you want the dialog to grow vertically too:
        	    dlg.getDialogPane().setPrefHeight(Region.USE_COMPUTED_SIZE);
        	    dlg.getDialogPane().setPrefWidth(500);
        	    // 2) Show and wait for the user to click OK
        	    dlg.showAndWait()
        	       .ifPresent(newBody -> {
        	         // 3) Apply the change and refresh
        	         databaseHelper.editAnswer(newBody, a.getAnswerID());
        	         populate.run();
        	       });
        	}
        	
        });
        
        /**
         * Creates the “Private Messages” button and the footer bar containing all
         * action buttons (Admin, Delete, Edit, Private Messages, Back).  Aligns
         * them centrally with spacing and padding, then builds the main layout:
         * <ul>
         *   <li>Top: filter dropdown</li>
         *   <li>Center: TabPane with all interaction views</li>
         *   <li>Bottom: footer HBox with action buttons</li>
         * </ul>
         * Finally, sets the Scene on the primaryStage (1200×700) and displays it.
         */
        Button pmButton = new Button ("Private Messages");
        pmButton.setOnAction(e->{
        	new InstructorPrivateMessages(databaseHelper).show(primaryStage, user);
        });
        
        HBox footer = new HBox(10,adminBtn,deleteSelectedBtn, editSelectedBtn,  pmButton ,backBtn);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10));

        
        BorderPane root = new BorderPane();
        root.setTop(filterBy);
        BorderPane.setAlignment(filterBy, Pos.CENTER);
        root.setCenter(tabs);
        root.setBottom(footer);

        primaryStage.setScene(new Scene(root, 1200, 700));
        primaryStage.setTitle("Instructor – Student Interaction");
        primaryStage.show();
    }

    private void showPopup(String comment) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText("Flag reason");
        a.setContentText(comment);
        a.showAndWait();
    }

    private boolean confirmDialog(String text) {
        Alert c = new Alert(Alert.AlertType.CONFIRMATION, text, ButtonType.YES, ButtonType.NO);
        c.setHeaderText(null);
        return c.showAndWait().filter(bt -> bt == ButtonType.YES).isPresent();
    }
}
