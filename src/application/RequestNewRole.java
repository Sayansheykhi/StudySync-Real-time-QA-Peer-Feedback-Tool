package application;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import databasePart1.DatabaseHelper;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The RequestNewRole class provides an interface for users to request new Role(s)
 * and displays a table of any role requests submitted by the currently
 * logged in user with their status.
 *
 * @author Cristina Hooe
 * @version 1.0 3/29/2025
 * 
 * @author Sajjad Sheykhi
 * @version 4/23/2025
 * 
 */
public class RequestNewRole {
    private final DatabaseHelper databaseHelper;
    private User user;

    public RequestNewRole(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage, User user) {
        boolean[] roles = new boolean[5];
        this.user = user;
        int userID = databaseHelper.getUserID(user);

        
        // Layout setup
        VBox leftLayout = new VBox(5);
        leftLayout.setAlignment(Pos.CENTER);
        leftLayout.setSpacing(10);
        leftLayout.setPrefWidth(500);

        VBox rightLayout = new VBox(5);
        rightLayout.setPrefWidth(500);
        rightLayout.setAlignment(Pos.CENTER);
        rightLayout.setSpacing(20);

        HBox fullLayout = new HBox(20);
        HBox checkBoxLayout = new HBox(10);
        checkBoxLayout.setAlignment(Pos.CENTER);

        Label roleLabel = new Label("Request Additional Role(s)");
        roleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label existingRoleRequests = new Label("Submitted Role Requests");
        existingRoleRequests.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button returnButton = new Button("Return to Student Homepage");
        returnButton.setTranslateX(-130);
        returnButton.setTranslateY(90);

        Button submitRoleRequestButton = new Button("Submit New Role(s) Request");
        submitRoleRequestButton.setDisable(true);

        Label availableRolesLabel = new Label("Available User Roles:");
        availableRolesLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label errLabel = new Label("No Role yet selected for provisioning, please select one or more Roles.");
        errLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: grey;");
        errLabel.setVisible(true);

        Label errLabelRequestExists = new Label("Role request already exists for one or more selected roles.");
        errLabelRequestExists.setStyle("-fx-font-size: 12px; -fx-text-fill: red;");
        errLabelRequestExists.setVisible(false);

        CheckBox adminCB = new CheckBox("Admin");
        CheckBox studentCB = new CheckBox("Student");
        CheckBox reviewerCB = new CheckBox("Reviewer");
        CheckBox instructorCB = new CheckBox("Instructor");
        CheckBox staffCB = new CheckBox("Staff");

        // initially invisible
        adminCB.setVisible(false);
        studentCB.setVisible(false);
        reviewerCB.setVisible(false);
        instructorCB.setVisible(false);
        staffCB.setVisible(false);

        
        
        Alert confirmRoleRequestAlert = new Alert(
        	    AlertType.CONFIRMATION,
        	    "Reviewer role requests go to an Instructor; all others go to an Admin.\nProceed?",
        	    new ButtonType("Submit Request"),
        	    new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE)
        	);
        	confirmRoleRequestAlert.setTitle("Role Request Confirmation");
        	confirmRoleRequestAlert.setHeaderText("Confirm your role-request selections");


        ObservableList<String[]> currRoleRequests = FXCollections.observableArrayList();

        GridPane roleRequestsTable = new GridPane();
        roleRequestsTable.setHgap(0);
        roleRequestsTable.setVgap(0);
        roleRequestsTable.setAlignment(Pos.CENTER);

        // Show roles not already held
        boolean[] userRoles = user.getRole();
        if (!userRoles[0]) adminCB.setVisible(true);
        if (!userRoles[1]) studentCB.setVisible(true);
        if (!userRoles[2]) reviewerCB.setVisible(true);
        if (!userRoles[3]) instructorCB.setVisible(true);
        if (!userRoles[4]) staffCB.setVisible(true);

        ChangeListener<Boolean> listener = (obs, oldVal, newVal) -> {
            boolean anySelected = adminCB.isSelected() || studentCB.isSelected()
                || reviewerCB.isSelected() || instructorCB.isSelected() || staffCB.isSelected();
            submitRoleRequestButton.setDisable(!anySelected);
            errLabel.setVisible(!anySelected);
            errLabelRequestExists.setVisible(false);
        };
        adminCB.selectedProperty().addListener(listener);
        studentCB.selectedProperty().addListener(listener);
        reviewerCB.selectedProperty().addListener(listener);
        instructorCB.selectedProperty().addListener(listener);
        staffCB.selectedProperty().addListener(listener);

        submitRoleRequestButton.setOnAction(e -> {
            Arrays.fill(roles, false);
            if (adminCB.isSelected())      roles[0] = true;
            if (studentCB.isSelected())    roles[1] = true;
            if (reviewerCB.isSelected())   roles[2] = true;
            if (instructorCB.isSelected()) roles[3] = true;
            if (staffCB.isSelected())      roles[4] = true;

            Optional<ButtonType> rs = confirmRoleRequestAlert.showAndWait();
            if (rs.isPresent() && rs.get().getText().equals("Submit Request")) {
                databaseHelper.createNewRoleRequest(user, roles, userID);
                refreshTable(roleRequestsTable, currRoleRequests, userID);
            }
        });

        returnButton.setOnAction(e -> {
            new StudentHomePage(databaseHelper).show(primaryStage, this.user);
        });

        checkBoxLayout.getChildren().addAll(adminCB, studentCB, reviewerCB, instructorCB, staffCB);
        leftLayout.getChildren().addAll(roleLabel, availableRolesLabel,
            checkBoxLayout, errLabelRequestExists,
            submitRoleRequestButton, errLabel, returnButton);
        rightLayout.getChildren().addAll(existingRoleRequests, roleRequestsTable);
        fullLayout.getChildren().addAll(leftLayout, rightLayout);

        // initial populate
        refreshTable(roleRequestsTable, currRoleRequests, userID);
        

        primaryStage.setScene(new Scene(fullLayout, 1000, 450));
        primaryStage.setTitle("Request New Role");
    }

    /**
     * Clears then repopulates the GridPane with *all* requests for this user.
     */
    private void refreshTable(GridPane table,
            ObservableList<String[]> data,
            int userID)
{
    		// 1) clear out old nodes
    		table.getChildren().clear();

    		// 2) re-draw your four headers
    		setupTableColumnHeaders(table);

// 3) grab *all* rows
data.clear();
List<String[]> raw = databaseHelper.getRoleRequestsForUser(userID);

if (raw.isEmpty()) {
noRoleRequestsTableDisplay(table);
} else {
// convert each “[false,…]” into a comma-separated list of names
for (String[] row : raw) {
row[2] = formatRoles(row[2]);
}
data.addAll(raw);
existingRoleRequestsTableDisplay(table, data);
}
}

//helper to convert the literal “[false,true,…]” into “Reviewer, Staff” etc.
private String formatRoles(String booleanList) {
// strip brackets, split on commas
String[] bits = booleanList
.substring(1, booleanList.length() - 1)
.split(",\\s*");
StringBuilder out = new StringBuilder();
String[] names = {"Admin","Student","Reviewer","Instructor","Staff"};
for (int i = 0; i < bits.length; i++) {
if (bits[i].equals("true")) {
if (out.length() > 0) out.append(", ");
out.append(names[i]);
}
}
return out.toString();
}
    private void setupTableColumnHeaders(GridPane table) {
        String[] headers = {"userName","Name","Role(s) Requested","Status"};
        for (int i = 0; i < headers.length; i++) {
            Label h = new Label(headers[i]);
            h.setMinWidth(100);
            h.setStyle("-fx-font-weight:bold; -fx-border-color:black; -fx-padding:5;");
            h.setMaxWidth(Double.MAX_VALUE);
            GridPane.setHgrow(h, Priority.ALWAYS);
            table.add(h, i, 0);
        }
    }

    private void noRoleRequestsTableDisplay(GridPane table) {
        Label none = new Label("No existing role requests");
        none.setStyle("-fx-border-color:black; -fx-padding:5; -fx-text-fill:grey;");
        GridPane.setColumnSpan(none, 4);
        none.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(none, Priority.ALWAYS);
        table.add(none, 0, 1);
    }

    private String[] processRoleRequestDataForTable(String[] req) {
        String[] bools = req[2].substring(1, req[2].length()-1).split(",\\s*");
        StringBuilder sb = new StringBuilder();
        String[] names = {"Admin","Student","Reviewer","Instructor","Staff"};
        for (int i = 0; i < bools.length; i++) {
            if ("true".equals(bools[i])) {
                if (sb.length()>0) sb.append(", ");
                sb.append(names[i]);
            }
        }
        return new String[]{req[0], req[1], sb.toString(), req[3]};
    }

    private void existingRoleRequestsTableDisplay(GridPane table, ObservableList<String[]> data) {
        for (int r=0; r<data.size(); r++) {
            String[] row = data.get(r);
            for (int c=0; c<row.length; c++) {
                Label cell = new Label(row[c]);
                cell.setStyle("-fx-border-color:black; -fx-padding:5;");
                GridPane.setHgrow(cell, Priority.ALWAYS);
                table.add(cell, c, r+1);
            }
        }
    }
}