package test;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.RequestNewRole;

/**
 * Unit tests for the pure‐logic helper methods in {@link RequestNewRole}.
 * <p>
 * Uses reflection to invoke the private methods
 * {@code formatRoles(String)} and {@code processRoleRequestDataForTable(String[])}
 * and verifies their behavior under various input scenarios.
 * </p>
 * 
 * @author Sajjad Sheykhi
 * @version 1.0
 */
class RequestNewRoleTest {

    private RequestNewRole view;

    /**
     * Prepare a fresh instance of {@code RequestNewRole} (no real
     * DatabaseHelper needed for these logic‐only tests).
     */
    @BeforeEach
    void setUp() {
        view = new RequestNewRole(null);
    }

    /**
     * Ensures that {@code formatRoles} returns an empty string
     * when no boolean flags are true.
     *
     * @throws Exception if reflection fails
     */
    @Test
    void formatRoles_noTrue() throws Exception {
        Method m = RequestNewRole.class
                .getDeclaredMethod("formatRoles", String.class);
        m.setAccessible(true);

        String input = "[false,false,false,false,false]";
        String out = (String) m.invoke(view, input);
        assertEquals("", out, "no 'true' flags should produce empty string");
    }

    /**
     * Verifies that {@code formatRoles} correctly returns "Student"
     * when only the second flag is true.
     *
     * @throws Exception if reflection fails
     */
    @Test
    void formatRoles_singleTrue() throws Exception {
        Method m = RequestNewRole.class
                .getDeclaredMethod("formatRoles", String.class);
        m.setAccessible(true);

        String input = "[false,true,false,false,false]";
        String out = (String) m.invoke(view, input);
        assertEquals("Student", out);
    }

    /**
     * Checks that {@code formatRoles} concatenates multiple true roles
     * in the correct order, separated by commas.
     *
     * @throws Exception if reflection fails
     */
    @Test
    void formatRoles_multipleTrue() throws Exception {
        Method m = RequestNewRole.class
                .getDeclaredMethod("formatRoles", String.class);
        m.setAccessible(true);

        String input = "[true,false,true,false,true]";
        String out = (String) m.invoke(view, input);
        assertEquals("Admin, Reviewer, Staff", out);
    }

    /**
     * Validates that {@code processRoleRequestDataForTable} transforms
     * a raw row array into the expected display format for a basic case.
     *
     * @throws Exception if reflection fails
     */
    @Test
    void processRoleRequestDataForTable_basic() throws Exception {
        Method m = RequestNewRole.class
                .getDeclaredMethod("processRoleRequestDataForTable", String[].class);
        m.setAccessible(true);

        String[] row = new String[] {
            "alice",
            "Alice Smith",
            "[true,false,true,false,false]",
            "Pending"
        };
        String[] out = (String[]) m.invoke(view, (Object) row);

        assertArrayEquals(
            new String[] {"alice", "Alice Smith", "Admin, Reviewer", "Pending"},
            out
        );
    }

    /**
     * Ensures {@code processRoleRequestDataForTable} handles the case
     * where all roles are requested (all flags true).
     *
     * @throws Exception if reflection fails
     */
    @Test
    void processRoleRequestDataForTable_allTrue() throws Exception {
        Method m = RequestNewRole.class
                .getDeclaredMethod("processRoleRequestDataForTable", String[].class);
        m.setAccessible(true);

        String[] row = new String[] {
            "bob",
            "Bob Jones",
            "[true,true,true,true,true]",
            "Approved"
        };
        String[] out = (String[]) m.invoke(view, (Object) row);

        assertArrayEquals(
            new String[] {"bob", "Bob Jones", "Admin, Student, Reviewer, Instructor, Staff", "Approved"},
            out
        );
    }
}