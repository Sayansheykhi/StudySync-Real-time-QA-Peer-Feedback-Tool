package test;


import application.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

/**
 * The ReviewTest class represents an interface for perform JUnit testing on Review.Java
 */
public class ReviewTest {
	
	/**
	 * Declaration of a Review test object
	 */
	Review testReview;
	
	/**
	 * Initiates test Review object before each test.
	 */
	@Before
	public void setup() {
		testReview = new Review();
	}
	
	/**
	 * Test the setReviewID() method
	 */
	@Test
	public void setReviewIDTest() {
		int testID = 34;
		testReview.setReviewID(testID);
		assertEquals(testID, testReview.getReviewID());
	}
	
	/**
	 * Test the getReviewID() method
	 */
	@Test
	public void getReviewIDTest() {
		int testID = 77;
		testReview.setReviewID(testID);
		assertEquals(testID, testReview.getReviewID());
	}
	
	/**
	 * Test the setAnswerID() method
	 */
	@Test
	public void setAnswerIDTest() {
		int testID = 62;
		testReview.setAnswerID(testID);
		assertEquals(testID, testReview.getAnswerID());
	}
	
	/**
	 * Test the getAnswerID() method
	 */
	@Test
	public void getAnswerIDTest() {
		int testID = 98;
		testReview.setAnswerID(testID);
		assertEquals(testID, testReview.getAnswerID());
	}
	
	
	/**
	 * Test the setQuestionID() method
	 */
	@Test
	public void setQuestionIDTest() {
		int testID = 2367;
		testReview.setQuestionID(testID);
		assertEquals(testID, testReview.getQuestionID());
	}
	
	/**
	 * Test the getQuestionID() method
	 */
	@Test
	public void getQuestionIDTest() {
		int testID = 79;
		testReview.setQuestionID(testID);
		assertEquals(testID, testReview.getQuestionID());
	}
	
	/**
	 * Test the setPrevReviewID() method
	 */
	@Test
	public void setPrevReviewIDTest() {
		int testID = 404;
		testReview.setPrevReviewID(testID);
		assertEquals(testID, testReview.getPrevReviewID());
	}
	
	/**
	 * Test the getPrevReviewID() method
	 */
	@Test
	public void getPrevReviewIDTest() {
		int testID = 60602;
		testReview.setPrevReviewID(testID);
		assertEquals(testID, testReview.getPrevReviewID());
	}
	
	/**
	 * Test the setReviewerFirstName() method
	 */
	@Test
	public void setReviewerFirstNameTest() {
		String testName = "TestFirst";
		testReview.setReviewerFirstName(testName);
		assertEquals(testName, testReview.getReviewerFirstName());
	}
	
	/**
	 * Test the getReviewerFirstName() method
	 */
	@Test
	public void getReviewerFirstNameTest() {
		String testName = "TestFirst";
		testReview.setReviewerFirstName(testName);
		assertEquals(testName, testReview.getReviewerFirstName());
	}
	
	/**
	 * Test the setReviewerLastName() method
	 */
	@Test
	public void setReviewerLastNameTest() {
		String testName = "TestLast";
		testReview.setReviewerLastName(testName);
		assertEquals(testName, testReview.getReviewerLastName());
	}
	
	/**
	 * Test the getReviewerLastName() method
	 */
	@Test
	public void getReviewerLastNameTest() {
		String testName = "TestLast";
		testReview.setReviewerLastName(testName);
		assertEquals(testName, testReview.getReviewerLastName());
	}
	
	/**
	 * Test the setReviewerUserName() method
	 */
	@Test
	public void setReviewerUserNameTest() {
		String testName = "TestUsername";
		testReview.setReviewerUserName(testName);
		assertEquals(testName, testReview.getReviewerUserName());
	}
	
	/**
	 * Test the getReviewerUserName() method
	 */
	@Test
	public void getReviewerUserNameTest() {
		String testName = "TestUsername";
		testReview.setReviewerUserName(testName);
		assertEquals(testName, testReview.getReviewerUserName());
	}
	
	/**
	 * Test the empty review body error of checkReviewBodyInput()
	 */
	@Test
	public void checkReviewBodyInputTest1() {
		String testInput = "";
		assertEquals("Error: Review Body is empty!", testReview.checkReviewBodyInput(testInput));
	}

	/**
	 * Test the invalid character review body error of checkReviewBodyInput()
	 */
	@Test
	public void checkReviewBodyInputTest2() {
		String regexCheckNonControlChar = "^[\\x20-\\x7E\\t\\n\\r]+$";
		assertEquals("Error: Review Body contains non-printable or control characters.", testReview.checkReviewBodyInput(regexCheckNonControlChar));
	}	
}