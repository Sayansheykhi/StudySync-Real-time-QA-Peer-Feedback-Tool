package test;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import databasePart1.DatabaseHelper;

import org.junit.Before;
import org.junit.Test;

import application.InstructorReviewerRequests.NewRoleRequest;
import application.StudentTrustedReviewersList;
import application.User;

/**
 * The AdminRoleTest class represents an interface for performing JUnit testing on the AdminRoleRequests.java class.
 * 
 * @author Kylie Kim
 * @version 1.0 4/25/2025
 */
public class AdminRoleTest {

	/**
	 * DatabaseHelper object to call DatabaseHelper functions that need to be tested and connect to/ disconnect from the database
	 */
    private DatabaseHelper dbHelper;

    /**
	 * Method connects to the database and initializes necessary general test objects.
	 */
    @Before
    public void setUp() {
        dbHelper = new DatabaseHelper(); // Replace with mock or actual test instance setup
    }

    
    /**
     * Test databaseHelper.submitAdminRequest()
     */
    @Test
    public void testSubmitAdminRequest() {
        User testInstructor = new User("instructorUser", new boolean[]{false, false, false, true, false}, "instructor@test.com", "Instructor", "User");

        try {
            dbHelper.submitAdminRequest(testInstructor, "Need role change", "High", "Open");
        } catch (Exception e) {
            fail("submitAdminRequest() threw an exception: " + e.getMessage());
        }
    }

    /**
     * Test databaseHelper.reopenAdminRequest()
     */
    @Test
    public void testReopenAdminRequest() {
        User testInstructor = new User(
            "instructorUser",
            new boolean[]{false, false, false, true, false},
            "instructor@test.com",
            "Instructor",
            "User"
        );

        try {
            dbHelper.reopenAdminRequest(testInstructor, "Reopening request with updated details", "Medium");
        } catch (Exception e) {
            fail("reopenAdminRequest() threw an exception: " + e.getMessage());
        }
    }

    /**
     * Test databaseHelper.closeAdminRequest()
     */
    @Test
    public void testCloseAdminRequest() {
        User testAdmin = new User(
            "adminUser",
            new boolean[]{true, false, false, false, false},  // Admin role
            "admin@test.com",
            "Admin",
            "User"
        );

        try {
            dbHelper.closeAdminRequest(testAdmin, "someRequestId"); // Use actual request ID if needed
        } catch (Exception e) {
            fail("closeAdminRequest() threw an exception: " + e.getMessage());
        }
    }

    /**
     * Test databaseHelper.getNonReviewerRoleRequests()
     */
    @Test
    public void testGetNonReviewerRoleRequests() {
        List<NewRoleRequest> requests = dbHelper.getNonReviewerRoleRequests();
        assertNotNull(requests);
        for (NewRoleRequest req : requests) {
            assertNotEquals("Reviewer", req.getRequestStatus());
        }
    }
    
    /**
     * Test  extractReviewerUserName
     * @throws Exception if fails
     */
    @Test
    public void testExtractReviewerUsername() throws Exception {
        StudentTrustedReviewersList list = new StudentTrustedReviewersList(dbHelper);
        Method method = StudentTrustedReviewersList.class.getDeclaredMethod("extractReviewerUsername", String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(list, "Username: reviewer123 \nName: John Smith\nWeight: 5");
        assertEquals("reviewer123", result);
    }
}