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

/**
 * AdminRoleRequests is a JavaFX page allowing Admin users to manage role requests
 * for Admin, Student, Staff, and Instructor roles.
 * Admins can approve or deny requests and assign roles directly.
 * This page can be accessed from AdminHomePage or ViewAdminActionList.
 *
 * @author Kylie Kim
 * @version 1.0 4/19/25
 */
public class AdminRoleRequests {

    private DatabaseHelper databaseHelper;
    private User adminUser;

    /**
     * Constructs an AdminRoleRequests page instance.
     *
     * @param databaseHelper reference to the database interaction helper
     * @param adminUser the currently logged-in admin user
     */
    public AdminRoleRequests(DatabaseHelper databaseHelper, User adminUser) {
        this.databaseHelper = databaseHelper;
        this.adminUser = adminUser;
    }

    /**
     * Displays the AdminRoleRequests interface for viewing and managing open role requests.
     *
     * @param primaryStage the application window
     * @param fromAdminHome boolean flag indicating whether navigation originated from AdminHomePage
     */
    public void show(Stage primaryStage, boolean fromAdminHome) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label title = new Label("Pending Role Requests");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        ObservableList<InstructorReviewerRequests.NewRoleRequest> roleRequests = FXCollections.observableArrayList(
                databaseHelper.getNonReviewerRoleRequests()
        );

        ListView<InstructorReviewerRequests.NewRoleRequest> listView = new ListView<>(roleRequests);
        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(InstructorReviewerRequests.NewRoleRequest req, boolean empty) {
                super.updateItem(req, empty);
                if (empty || req == null) {
                    setText(null);
                } else {
                    setText("User: " + req.getUserName() + "\nRequested Role: " + req.getRequestStatus());
                }
            }
        });

        Button approveButton = new Button("Approve");
        Button denyButton = new Button("Deny");
        Label actionStatus = new Label("");

        /**
         * Handles approval action by launching AdminUserModifications for the selected user.
         */
        approveButton.setOnAction(e -> {
            InstructorReviewerRequests.NewRoleRequest selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                AdminUserModifications modPage = new AdminUserModifications(databaseHelper, selected.getUserName(), adminUser);
                modPage.show(primaryStage, adminUser);
            } else {
                actionStatus.setText("Please select a request to approve.");
            }
        });

        /**
         * Handles denial of selected role request and refreshes the list.
         */
        denyButton.setOnAction(e -> {
            InstructorReviewerRequests.NewRoleRequest selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                databaseHelper.denyRoleRequest(selected.getRoleRequestID());
                roleRequests.setAll(databaseHelper.getNonReviewerRoleRequests());
                actionStatus.setText("Request denied.");
            } else {
                actionStatus.setText("Please select a request to deny.");
            }
        });

        HBox buttonBox = new HBox(10, approveButton, denyButton);
        buttonBox.setAlignment(Pos.CENTER);

        /**
         * Returns the user to the appropriate previous screen depending on entry point.
         */
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            if (fromAdminHome) {
                new AdminHomePage(databaseHelper, adminUser).show(primaryStage, adminUser);
            } else {
                new ViewAdminActionList(databaseHelper, adminUser).show(primaryStage, adminUser);
            }
        });

        layout.getChildren().addAll(title, listView, buttonBox, actionStatus, backButton);

        Scene scene = new Scene(layout, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Admin Role Requests");
    }
}