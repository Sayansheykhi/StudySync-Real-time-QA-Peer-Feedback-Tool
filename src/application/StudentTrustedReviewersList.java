package application;

import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.collections.transformation.SortedList;
import javafx.collections.transformation.FilteredList;
import java.util.Comparator;
import application.ComputeReviewersScorecard;
import javafx.collections.ObservableList;

/**
 * Provides and creates a page for students to create and maintain a list of trusted reviewers.
 * Students can see list of, sort, filter, add, remove, and change the weight of reviewers, and return to the student home page.
 * Now includes the ability to view Reviewers' Scorecards.
 * 
 * @author Kylie Kim
 * @version 2.0 4/19/25
 */
public class StudentTrustedReviewersList {
    
	private final DatabaseHelper databaseHelper;
    private User studentUser;
    private ObservableList<String> trustedReviewers;
    private ListView<String> listView;
	
	/**
	 * Initializes the page, taking the databaseHelper object from the previous page
	 * 
	 * @param databaseHelper			Object that handles all database interactions
	 */
    public StudentTrustedReviewersList(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    /**
     * Constructor to initialize StudentTrustedReviewersList
     * 
     * @param db   Reference to the database helper
     * @param user The currently logged in student
     */
    public StudentTrustedReviewersList(DatabaseHelper db, User user) {
        this.databaseHelper = db;
        this.studentUser = user;
        this.trustedReviewers = FXCollections.observableArrayList();
    }
    
    /**
     * Builds and displays the page
     * 
     * @param primaryStage				The application window
     * @param user						The logged in user's information
     */
    public void show(Stage primaryStage, User user) {
    	this.studentUser = user;

        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        Label header = new Label("Trusted Reviewers");

        // Setup search and sort
        HBox sortBox = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Search");
        ChoiceBox<String> sortChoices = new ChoiceBox<>();
        sortChoices.getItems().addAll("Weight", "Username", "Name");
        Button ascendingDescendingToggle = new Button("^");
        Button sortButton = new Button("Sort");

        // Load reviewers
        ArrayList<String> reviewers = getReviewers();
        ArrayList<String> formattedList = userReviewerList(reviewers);
        ObservableList<String> trustedReviewers = FXCollections.observableArrayList(formattedList);

        FilteredList<String> trustedReviewersFiltered = new FilteredList<>(trustedReviewers);
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            trustedReviewersFiltered.setPredicate(item -> newValue == null || newValue.isEmpty() || item.contains(newValue));
        });
        SortedList<String> trustedReviewersSorted = new SortedList<>(trustedReviewersFiltered);
        ListView<String> trustedReviewersList = new ListView<>(trustedReviewersSorted);

        sortButton.setOnAction(e -> applySort(trustedReviewersSorted, sortChoices, ascendingDescendingToggle));
        ascendingDescendingToggle.setOnAction(e -> {
            ascendingDescendingToggle.setText(ascendingDescendingToggle.getText().equals("^") ? "v" : "^");
        });

        // Add reviewer UI
        HBox addReviewerBox = new HBox(10);
        TextField addReviewerField = new TextField();
        addReviewerField.setPromptText("Enter Reviewer Username");
        Button addReviewerButton = new Button("Add Reviewer");
        Slider weightSlider = createWeightSlider();
        Label addReviewerLabel = new Label("");

        // Modify reviewer weight UI
        HBox changeWeightBox = new HBox(10);
        Button changeWeightButton = new Button("Change Selected Reviewer's Weight");
        Label changeWeightLabel = new Label("");

        // Remove reviewer UI
        HBox removeReviewerBox = new HBox(10);
        Button removeReviewerButton = new Button("Remove Selected Reviewer");
        Label removeReviewerLabel = new Label("");

        // NEW: View Scorecard UI
        HBox scorecardBox = new HBox(10);
        Button viewScorecardButton = new Button("View Scorecard of Selected Reviewer");
        Label scorecardMessageLabel = new Label("");

        viewScorecardButton.setOnAction(e -> {
            String selected = trustedReviewersList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                String username = extractReviewerUsername(selected);
                ComputeReviewersScorecard scorecardView = new ComputeReviewersScorecard(databaseHelper, studentUser, username);
                scorecardView.show(primaryStage, studentUser);
            } else {
                scorecardMessageLabel.setStyle("-fx-text-fill: red;");
                scorecardMessageLabel.setText("Please select a reviewer to view their scorecard.");
            }
        });

        // Button actions
        addReviewerButton.setOnAction(e -> handleAddReviewer(addReviewerField, weightSlider, trustedReviewers, addReviewerLabel));
        changeWeightButton.setOnAction(e -> handleChangeWeight(trustedReviewersList, weightSlider, trustedReviewers, changeWeightLabel));
        removeReviewerButton.setOnAction(e -> handleRemoveReviewer(trustedReviewersList, trustedReviewers, removeReviewerLabel));

        // Navigation
        Button backButton = new Button("Back");
        backButton.setOnAction(a -> back(primaryStage));

        sortBox.getChildren().addAll(searchField, sortChoices, ascendingDescendingToggle, sortButton);
        addReviewerBox.getChildren().addAll(addReviewerField, addReviewerButton, addReviewerLabel);
        changeWeightBox.getChildren().addAll(changeWeightButton, changeWeightLabel);
        removeReviewerBox.getChildren().addAll(removeReviewerButton, removeReviewerLabel);
        scorecardBox.getChildren().addAll(viewScorecardButton, scorecardMessageLabel);

        layout.getChildren().addAll(header, sortBox, trustedReviewersList, addReviewerBox, weightSlider, changeWeightBox, removeReviewerBox, scorecardBox, backButton);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Trusted Reviewers");
        primaryStage.show();
    }
  
    /**
     * Extracts reviewer username from formatted reviewer info string.
     *
     * @param info Formatted string
     * @return Username
     */
    private String extractReviewerUsername(String info) {
        Scanner scanner = new Scanner(info);
        scanner.next(); // skip "Username:"
        String username = scanner.next();
        scanner.close();
        return username;
    }
    
    /**
     * Builds a slider widget for reviewer weight.
     *
     * @return Slider with range 0–10
     */
    private Slider createWeightSlider() {
        Slider slider = new Slider(0, 10, 0);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setSnapToTicks(true);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        return slider;
    }
    
    /**
     * Applies a selected sort method to the reviewer list.
     *
     * @param sortedList Sorted observable list
     * @param sortChoices Sort choice dropdown
     * @param toggle Ascending/descending toggle
     */
    private void applySort(SortedList<String> sortedList, ChoiceBox<String> sortChoices, Button toggle) {
        String selection = sortChoices.getValue();
        boolean ascending = toggle.getText().equals("^");
        if (selection == null) return;
        sortedList.setComparator(new Comparator<String>() {
            public int compare(String o1, String o2) {
                Scanner s1 = new Scanner(o1), s2 = new Scanner(o2);
                String key1 = "", key2 = "";
                switch (selection) {
                    case "Weight":
                        for (int i = 0; i < 6; i++) s1.next(); key1 = s1.next();
                        for (int i = 0; i < 6; i++) s2.next(); key2 = s2.next();
                        break;
                    case "Username":
                        s1.next(); key1 = s1.next(); s2.next(); key2 = s2.next(); break;
                    case "Name":
                        for (int i = 0; i < 3; i++) s1.next(); key1 = s1.nextLine().trim();
                        for (int i = 0; i < 3; i++) s2.next(); key2 = s2.nextLine().trim(); break;
                }
                s1.close(); s2.close();
                return ascending ? key1.compareTo(key2) : key2.compareTo(key1);
            }
        });
    }
    
    /**
     * Handles logic for adding a reviewer to the student's trusted reviewer list,
     * performs validation on existence and role, and updates the UI list accordingly.
     *
     * @param input The TextField containing the username of the reviewer to be added.
     * @param weightSlider The Slider used to select the weight to assign to the reviewer.
     * @param list The ObservableList representing the current trusted reviewers shown in the UI.
     * @param label The Label used to display success or error messages to the user.
     */
    private void handleAddReviewer(TextField input, Slider weightSlider, ObservableList<String> list, Label label) {
        String username = input.getText();
        if (!databaseHelper.doesUserExist(username) || !databaseHelper.getUserRole(username)[2]) {
            label.setStyle("-fx-text-fill: red;");
            label.setText("Reviewer does not exist or is invalid.");
            return;
        }
        if (databaseHelper.doesReviewerExist(studentUser, username)) {
            label.setStyle("-fx-text-fill: red;");
            label.setText("Reviewer already exists.");
            return;
        }
        int weight = (int) weightSlider.getValue();
        databaseHelper.addTrustedReviewer(studentUser, weight, username);
        User user = databaseHelper.getUserInfo(username);
        list.add(formatReviewerInfo(user, weight));
        label.setStyle("-fx-text-fill: black;");
        label.setText("Reviewer added successfully.");
    }

    /**
     * Handles logic for changing the weight of a selected reviewer and updates the UI.
     *
     * @param list The ListView component where the reviewer is selected.
     * @param slider The Slider used to determine the new weight.
     * @param trustedReviewers The ObservableList representing the trusted reviewers displayed in the UI.
     * @param label The Label used to display success or error messages to the user.
     */
    private void handleChangeWeight(ListView<String> list, Slider slider, ObservableList<String> trustedReviewers, Label label) {
        String selected = list.getSelectionModel().getSelectedItem();
        if (selected == null) {
            label.setStyle("-fx-text-fill: red;");
            label.setText("No reviewer selected.");
            return;
        }
        String username = extractReviewerUsername(selected);
        int newWeight = (int) slider.getValue();
        databaseHelper.assignTrustedReviewerWeight(studentUser, newWeight, username);
        User reviewer = databaseHelper.getUserInfo(username);
        trustedReviewers.set(list.getSelectionModel().getSelectedIndex(), formatReviewerInfo(reviewer, newWeight));
        label.setStyle("-fx-text-fill: black;");
        label.setText("Weight updated.");
    }
    
    /**
     * Handles logic for removing a selected reviewer from the student's trusted reviewer list,
     * and updates the UI accordingly.
     *
     * @param list The ListView from which a reviewer is selected for removal.
     * @param trustedReviewers The ObservableList of trusted reviewers displayed in the UI.
     * @param label The Label used to display success or error messages to the user.
     */
    private void handleRemoveReviewer(ListView<String> list, ObservableList<String> trustedReviewers, Label label) {
        String selected = list.getSelectionModel().getSelectedItem();
        if (selected == null) {
            label.setStyle("-fx-text-fill: red;");
            label.setText("No reviewer selected.");
            return;
        }
        String username = extractReviewerUsername(selected);
        databaseHelper.removeTrustedReviewer(studentUser, username);
        trustedReviewers.remove(list.getSelectionModel().getSelectedIndex());
        label.setStyle("-fx-text-fill: black;");
        label.setText("Reviewer removed.");
    }
    
    /**
     * Gets a list of all a user's trusted reviewers and their assigned weights
     * 
     * @return							A list of reviewer usernames and the corresponding weights as a list of strings formatted: "userName weight"
     */
    private ArrayList<String> getReviewers() {
    	ArrayList<String> reviewers = databaseHelper.getTrustedReviewers(studentUser);
    	return reviewers;
    }
    
    /**
     * Isolates a reviewer's username from the string retrieved from the database
     * 
     * @param data						The string from the database
     * @return							The reviewer's username
     */
    private String getReviewerUserName(String data) {
    	return data.substring(0, data.indexOf(' '));
    }
    
    /**
     * Isolates a reviewer's weight from the string retrieved from the database
     * 
     * @param data						The string from the database
     * @return							The reivewer's weight value
     */
    private int getReviewerWeight(String data) {
    	return Integer.parseInt(data.substring(data.indexOf(' ') + 1));
    }
    
    /**
     * Creates list of usernames, first and last names, and weight values from list of usernames and weights from database
     * 
     * @param list						The list of reviewer username and corresponding weights in the form of strings from the database
     * @return							List of strings containing reviewer usernames, first and last names, and weights formatted: "Username: userName \nName: firstName lastName\nWeight: weight"
     */
    private ArrayList<String> userReviewerList(ArrayList<String> list) {
    	ArrayList<String> formattedList = new ArrayList<String>();
    	for(int i = 0; i < list.size(); i++) {
    		String userName = getReviewerUserName(list.get(i));
    		User reviewer = databaseHelper.getUserInfo(userName);
    		int weight = getReviewerWeight(list.get(i));
    		
    		String formattedInfo = formatReviewerInfo(reviewer, weight);
    		
    		formattedList.add(formattedInfo);    		
    	}    	
    	return formattedList;
    }
    
    /**
     * Formats given user information and weight value into a string
     * 
     * @param reviewer					User information of given reviewer		
     * @param weight					Weight value
     * @return							String containing the username, first and last names, and weight of reviewer formatted: "Username: userName \nName: firstName lastName\nWeight: weight"
     */
    private String formatReviewerInfo(User reviewer, int weight) {    	
    	return String.format("Username: %s \n"
    			+ "Name: %s\n"
    			+ "Weight: %d", 
    			reviewer.getUserName(), reviewer.getFirstName() + " " + reviewer.getLastName(), weight);
    			
    }
     
    /**
     * Returns user to StudentHomePage
     * 
     * @param primaryStage				The application window
     */
    private void back(Stage primaryStage) {
    	new StudentHomePage(databaseHelper).show(primaryStage, studentUser);
    }
    
}