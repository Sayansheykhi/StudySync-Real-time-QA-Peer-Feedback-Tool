package test;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Test;
import org.junit.Before;

import application.Review;
import application.Reviews;
import application.User;
import databasePart1.DatabaseHelper;

/**
 * The ReviewTest class represents an interface for perform JUnit testing on Review.Java
 * 
 * IMPORTANT: uncomment out Line 48 of DatabaseHelper.java before running
 */
public class ReviewsTest {
	
	/**
	 * Declaration of a Reviews test object
	 */
	Reviews testReviews;
	/**
	 *  DatabaseHelper object to call DatabaseHelper functions 
	 */
	DatabaseHelper databaseHelper;
	
	/**
	 *  Test user to use for methods that require an argument of type User
	 */
	User testUser = new User("TestUser", "", new boolean[5], "", "", "");
	
	
	/**
	 * Method initializes all test object and connects to database.
	 */
	@Before
	public void setup() {
		databaseHelper = new DatabaseHelper();
		testReviews = new Reviews(databaseHelper, testUser);
		try {
			databaseHelper.connectToDatabase();	
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Test the addReview() method.
	 */
	@Test
	public void addReviewTest() {
		testReviews.addReview(new Review());
		assertEquals(1, testReviews.getAllReviews(testUser).size());
	}
	
	/**
	 * Test the createNewReviewFromOld() method.
	 */
	@Test
	public void createNewReviewFromOldTest() {
		testReviews.addReview(new Review("Test", 1, "Test"));
		testReviews.createNewReviewFromOld(testReviews.getReviewByID(1));
		assertEquals(1, testReviews.getReviewByID(2).getPrevReviewID());
	}
	
	/**
	 * Test the editReview() method.
	 */
	@Test
	public void editReviewTest() {
		testReviews.addReview(new Review("Test", 1, "Test"));
		String testString = "Test";
		testReviews.getReviewByID(1).setReviewBody("PlaceHolder");
		testReviews.editReview(testReviews.getReviewByID(1), testString);
		System.out.println(testReviews.getReviewByID(1).getReviewBody());
		assertArrayEquals(testString.toCharArray(), testReviews.getReviewByID(1).getReviewBody().toCharArray());
	}
	
	/**
	 * Test the getAllReviews() method.
	 */
	@Test
	public void getAllReviewsTest() {	
		testReviews.addReview(new Review());
		testReviews.addReview(new Review());
		testReviews.addReview(new Review());
		assertEquals(3, testReviews.getAllReviews(testUser).size());
	}
	
	/**
	 * Test the getReviewsByUsername() method.
	 */
	@Test
	public void getReviewsByUsernameTest() {
		String testUser = "testUser";
		testReviews.addReview(new Review(1, "testBody", testUser));
		testReviews.addReview(new Review(2, "testBody", "testUser2"));
		assertEquals(1, testReviews.getReviewsByUsername(testUser).size());
	}

	 
	/**
	 * Test the getReviewByAnswerID() method.
	 */
	@Test
	public void getReviewByAnswerIDTest() {
		testReviews.addReview(new Review("testBody", 1, "testUser"));
		testReviews.addReview(new Review("testBody", 2, "testUser"));
		assertEquals(1, testReviews.getReviewByAnswerID(1).size());
	}
	
	/**
	 * Test the getReviewByIDTest() method.
	 */
	@Test
	public void getReviewByIDTest() {
		testReviews.addReview(new Review());
		assertEquals(1, testReviews.getReviewByID(1).getReviewID());
	}

}