package databasePart1;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import application.Answer;
import application.InstructorMessage;
import application.InstructorReviewerRequests;
import application.Question;
import application.Review;
import application.ReviewerMessage;
import application.ReviewerPrivateMessages;
import application.StaffMessage;
import application.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;


/**
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, storage of data 
 * including users, questions, question replies, answers, reviews, and private messages, 
 * and retrieval of this data in various forms.
 * 
 * @author John Gallagher
 * @author Cristina Hooe
 * @author Kylie Kim
 * @author Sajjad Sheykhi
 * 
 * @version 1.0 4/3/2025
 * @version 2.0 4/10/2025
 * @version 3.0 4/20/2025
 */
public class DatabaseHelper {
	 
	/**
	 * JDBC driver name
	 */
	static final String JDBC_DRIVER = "org.h2.Driver";   
	
	/**
	 * Database URL
	 */
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	
	/**
	 * Database UserName credentials
	 */
	static final String USER = "sa"; 
	
	/**
	 * Database Password credentials
	 */
	static final String PASS = ""; 
	
	/**
	 * Fixed expiration duration
	 */
	private final int expirationTimer = 15 * 60;
	
	/**
	 * Instantiation of the connection to the database as null 
	 */
	private Connection connection = null;
	
	/**
	 * PreparedStatement pstmt
	 */
	private Statement statement = null; 
	
	/**
	 * Default constructor
	 */
	public DatabaseHelper() {
		// empty constructor
	}
	
	/**
	 * Establishes a connection to the database using the specified URL, UserName, and Password.
	 * 
	 * @throws SQLException if a database access error if the JDBC Driver is not found
	 */
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			//statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	/**
	 * Creates the database tables if they do not already exist
	 * 
	 * @throws SQLException if table creation fails
	 */
	private void createTables() throws SQLException {
		// Create the user information table.
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "firstName VARCHAR(255), "
				+ "lastName VARCHAR (255), "
				+ "email VARCHAR(255), "
				+ "role VARCHAR(255), "
				+ "isMuted BOOLEAN DEFAULT FALSE "
				+ ")";
		statement.execute(userTable);
		
		// Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	    		+ "email VARCHAR(255), "
	            + "code VARCHAR(10) PRIMARY KEY, "
	    		+ "role VARCHAR(255), "
	            + "isUsed BOOLEAN DEFAULT FALSE, "
	            + "deadline DATE)";
	    statement.execute(invitationCodesTable);
	    
	    // Create the password reset codes table.
	    String passwordResetsTable = "CREATE TABLE IF NOT EXISTS passwordResets ("
	    		+ "userName VARCHAR(255), "
	    		+ "resetCode VARCHAR(255) UNIQUE, "
	    		+ "expiration INT)";
	    statement.execute(passwordResetsTable);
	    
	    // Create the questions table
		String questionTable = "CREATE TABLE IF NOT EXISTS questions ("
				+ "questionID INT AUTO_INCREMENT PRIMARY KEY, "
				+ "studentUserName VARCHAR(255), "
				+ "studentFirstName VARCHAR(255), "
				+ "studentLastName VARCHAR(255), "
				+ "questionTitle VARCHAR(255), "
				+ "questionBody TEXT, "
				+ "isResolved BOOLEAN DEFAULT FALSE, "
				+ "creationTime DATETIME, "
				+ "oldQuestionID INT, "
				+ "isFlagged BOOLEAN DEFAULT FALSE, "
				+ "reasonIsFlagged VARCHAR(255), "
				+ "isHidden BOOLEAN DEFAULT FALSE "
				+ ");";
		statement.execute(questionTable);
		
		// Create the answers table
		String answerTable = "CREATE TABLE IF NOT EXISTS answers ("
				+ "answerID INT AUTO_INCREMENT PRIMARY KEY, "
				+ "studentUserName VARCHAR(255), "
				+ "studentFirstName VARCHAR(255), "
				+ "studentLastName VARCHAR(255), "
				+ "questionID INT, "
				+ "answerText TEXT, "
				+ "isAnswerUnread BOOLEAN DEFAULT TRUE, "
				+ "isResolved BOOLEAN, "
				+ "creationTime DATETIME, "
				+ "isFlagged BOOLEAN DEFAULT FALSE, "
				+ "reasonIsFlagged VARCHAR(255), "
				+ "isHidden BOOLEAN DEFAULT FALSE "
				+ ");";
		statement.execute(answerTable);	
		
		// Create the questionReplies table
		String questionReplyTable = "CREATE TABLE IF NOT EXISTS questionReplies ("
				+ "replyID INT AUTO_INCREMENT PRIMARY KEY, "
				+ "questionID INT, "
				+ "studentUserName VARCHAR(255), "
				+ "studentFirstName VARCHAR(255), "
				+ "studentLastName VARCHAR(255), "
				+ "questionReplyText TEXT, "
				+ "creationTime DATETIME, "
				+ "replyingTo TEXT, "
				+ "isFlagged BOOLEAN DEFAULT FALSE, "
				+ "reasonIsFlagged VARCHAR(255), "
				+ "isHidden BOOLEAN DEFAULT FALSE "
				+ ");";
		statement.execute(questionReplyTable);
		
		// Create the newRoleRequests table
		String newRoleRequestTable = "CREATE TABLE IF NOT EXISTS newRoleRequests ("
				+ "roleRequestID INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userID INT, "
				+ "userName VARCHAR(255), "
				+ "userFirstName VARCHAR (255), "
				+ "userLastName VARCHAR (255), "
				+ "isRequestApproved BOOLEAN DEFAULT FALSE, "
				+ "requestStatus VARCHAR (255), "
				+ "role VARCHAR(255))";
				
		statement.execute(newRoleRequestTable);
		
		// Create the reviews table
		String reviewTable = "Create TABLE IF NOT EXISTS reviews ("
				+ "reviewID INT AUTO_INCREMENT PRIMARY KEY, "
				+ "questionID INT, "
				+ "answerID INT, "
				+ "prevReviewID INT, "
				+ "reviewerUserName VARCHAR(255), "
				+ "reviewerFirstName VARCHAR(255), "
				+ "reviewerLastName VARCHAR(255), "
				+ "reviewBody TEXT, "
				+ "isFlagged BOOLEAN DEFAULT FALSE, "
				+ "reasonIsFlagged VARCHAR(255), "
				+ "isHidden BOOLEAN DEFAULT FALSE "
				+ ");";
		statement.execute(reviewTable);
		
		// Create the Student private messages table
		String studentMessagesTable = "CREATE TABLE IF NOT EXISTS PrivateMessages ("
				+ "messageID INT AUTO_INCREMENT PRIMARY KEY, "
				+ "questionID INT, "
				+ "reviewID INT, "
				+ "sender_user_name VARCHAR(255), "
				+ "receiver_user_name VARCHAR(255), "
				+ "subject VARCHAR(255), "
				+ "message_body TEXT, "
				+ "is_read BOOLEAN DEFAULT FALSE, "
				+ "timestamp DATETIME, "
				+ "isFlagged BOOLEAN DEFAULT FALSE, "
				+ "reasonIsFlagged VARCHAR(255)"
				+ ")";
		statement.execute(studentMessagesTable);
		
		// Create the reviewerMessages table
	    String reviewerMessagesTable = "CREATE TABLE IF NOT EXISTS reviewerMessages ("
	            + "messageID INT AUTO_INCREMENT PRIMARY KEY, "
	            + "sender VARCHAR(255), "
	            + "recipient VARCHAR(255), "
	            + "recipientRole VARCHAR(255), "  
	            + "subject VARCHAR(255), "
	            + "body TEXT, "
	            + "sentTime DATETIME, "
	            + "isRead BOOLEAN DEFAULT FALSE, "
	            + "reviewID INT, "
	            + "isFlagged BOOLEAN DEFAULT FALSE, "
				+ "reasonIsFlagged VARCHAR(255)"
	            + ")";
	    statement.execute(reviewerMessagesTable);
	    
	    // Create the trustedReviewers table
	    String trustedReviewersTable = "Create TABLE IF NOT EXISTS trustedReviewers ("
				+ "studentUserName VARCHAR(255), "
				+ "reviewerUserName VARCHAR(255), "
				+ "weight INT"
				+ ");";
		statement.execute(trustedReviewersTable);
		
		// Create the staffMessages table
		String staffMessagesTable = "CREATE TABLE IF NOT EXISTS staffMessages ("
				+ "messageID INT AUTO_INCREMENT PRIMARY KEY, "
				+ "senderEmail VARCHAR(255), "
				+ "senderUserName VARCHAR(255), "
				+ "senderFirstName VARCHAR(255), "
				+ "senderLastName VARCHAR(255), "
				+ "senderRole VARCHAR(255), "
				+ "messageSubject VARCHAR(255), "
				+ "messageBody TEXT, "
				+ "isMessageRead BOOLEAN DEFAULT FALSE, "
				+ "recipientEmail VARCHAR(255), "
				+ "recipientUserName VARCHAR(255), "
				+ "recipientFirstName VARCHAR(255), "
				+ "recipientLastName VARCHAR(255), "
				+ "recipientRole VARCHAR(255), "
				+ "timeSent DATETIME"
				+ ")";
		statement.execute(staffMessagesTable);
		
		String instructorMessagesTable = "CREATE TABLE IF NOT EXISTS instructorMessages ("
				+ "messageID INT AUTO_INCREMENT PRIMARY KEY, "
				+ "senderEmail VARCHAR(255), "
				+ "senderUserName VARCHAR(255), "
				+ "senderFirstName VARCHAR(255), "
				+ "senderLastName VARCHAR(255), "
				+ "senderRole VARCHAR(255), "
				+ "messageSubject VARCHAR(255), "
				+ "messageBody TEXT, "
				+ "isMessageRead BOOLEAN DEFAULT FALSE, "
				+ "isDeletedInbox BOOLEAN DEFAULT FALSE, "
				+ "recipientEmail VARCHAR(255), "
				+ "recipientUserName VARCHAR(255), "
				+ "recipientFirstName VARCHAR(255), "
				+ "recipientLastName VARCHAR(255), "
				+ "recipientRole VARCHAR(255), "
				+ "timeSent DATETIME"
				+ ");";
		statement.execute(instructorMessagesTable);
	}

	/**
	 * Check if the database is empty.
	 *
	 * @return true or false as to whether the database is empty
	 * @throws SQLException if the query does not execute
	 */
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}
	
	/**
	 * Get the number of users in database.
	 * 
	 * @return the number of users in the database and may return 0 if no users exist in the database
	 * @throws SQLException if the query does not execute
	 */
	public int countDataBase() throws SQLException{
		String query = "SELECT COUNT(*) FROM cse360users";
		try(ResultSet resultSet = statement.executeQuery(query)){
			resultSet.next();
			int count = resultSet.getInt(1);
			return count;
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * Get the count of users in the database whom hold the "Admin" role for use in the AdminUserModifications class.
	 * 
	 * @return the count of Admin users in the database
	 */
	public int countAdminDataBase(){
		ArrayList<User> userList = getUserList();
		int count = 0;
		for(User u : userList) {
			if(u.getRole()[0]) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Registers a new user in the database.
	 * 
	 * @param user the specific user whom is defined by user input for userName, password, firstName, lastName, email and role(s)
	 * @throws SQLException if query cannot execute
	 */
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO cse360users (userName, password, firstName, lastName, email, role, isMuted) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getFirstName());
			pstmt.setString(4, user.getLastName());
			pstmt.setString(5, user.getEmail());
			pstmt.setString(6, Arrays.toString(user.getRole())); 
			pstmt.setBoolean(7, false);
			pstmt.executeUpdate();
		}
	}

	/**
	 * Validates a user's login credentials.
	 * 
	 * @param user the specific user who is attempting to login
	 * @return true or false if the input credentials match an existing user in the database
	 *
	 * @throws SQLException if query cannot execute
	 */
	public boolean login(User user) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ? AND role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, Arrays.toString(user.getRole()));
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	
	/**
	 * Returns a User object if a user exists for the specified userName and password for use in the UserLoginPage class.
	 * 
	 * @param userName the userName of the user
	 * @param password the password of the user
	 * @return a User object associated with the specified userName and password
	 */
	public User getUser(String userName, String password) {
		String getUser = "SELECT * FROM cse360users WHERE userName = ? AND password = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(getUser)) {
			pstmt.setString(1, userName);
			pstmt.setString(2, password);
			
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				return new User(
					rs.getString("userName"),
					rs.getString("password"),
					new boolean[5],
					rs.getString("email"),
					rs.getString("firstName"),
					rs.getString("lastName")
				);
			}
		}
		catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	/**
	 * Checks if a user already exists in the database based on their userName.
	 * 
	 * @param userName the userName of the user
	 * @return true or false as to whether a user already exists in the database
	 */
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}
	
	/**
	 * Retrieve all users information from database excluding password.
	 * 
	 * @return an ArrayList of all users in the database including all their attributes except for password
	 */
	public ArrayList<User> getUserList(){
		ArrayList<User>  userList = new ArrayList<>();
		String query = "SELECT userName || ',' || firstName || ',' || lastName ||  " 
				+ "',' || email || ',' || role FROM cse360users";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				String[] userInfoString = rs.getString(1).split(",");
				boolean[] roles = stringToBoolArray(userInfoString, 4);
				userList.add(new User(userInfoString[0], "", roles, userInfoString[3], userInfoString[1], userInfoString[2]));
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return userList;
	}
	
	/**
	 * Retrieves the role of a user from the database using their UserName.
	 * The order of roles as is stored in the database is Admin[0], Student[1], Reviewer[2], Instructor[3], Staff[4].
	 * 
	 * @param userName the userName of the user
	 * @return a boolean array containing true for roles the user currently holds, and false for those the user does not
	 */
	public boolean[] getUserRole(String userName) {
	    String query = "SELECT role FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	        	String[] userInfoString = rs.getString(1).split(",");
	        	boolean[] roles = stringToBoolArray(userInfoString, 0);
	            return roles; // Return the role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	/**
	 *  Used pull the roles assigned by an Admin user during the invitation code process. If multiple users are in the system with the same email address, this
	 *  method does not work as designed. Use getInvitedUserRoleEmailAndCode() instead.
	 * 
	 * @param email the email address associated with an invitation code and role assignment created by an admin user
	 * @return a boolean array containing true for roles the user currently holds, and false for those the user does not 
	 */
	public boolean[] getInvitedUserRole(String email) {
	    String query = "SELECT role FROM InvitationCodes WHERE email = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, email);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	        	String[] userInfoString = rs.getString(1).split(",");
	        	boolean[] roles = stringToBoolArray(userInfoString, 0);
	            return roles; // Return the role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	/**
	 *  Used in the SetupAccountPage class to pull the roles assigned by an Admin user during the invitation code process which are then used to officially
	 *  register the user in the database.
	 * 
	 * @param email the email address associated with an invitation code and role assignment created by an admin user
	 * @param code the user input invitation code
	 * @return a boolean array containing true for roles the user currently holds, and false for those the user does not 
	 */
	public boolean[] getInvitedUserRoleEmailAndCode(String email, String code) {
	    String query = "SELECT role FROM InvitationCodes WHERE email = ? AND code  = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, email);
	        pstmt.setString(2, code);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	        	String[] userInfoString = rs.getString(1).split(",");
	        	boolean[] roles = stringToBoolArray(userInfoString, 0);
	            return roles; // Return the role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	/**
	 * Delete a user from the database.
	 * 
	 * @param user the user object that contains the userName for the user to delete
	 */
	public void deleteUser(User user) {
		String query = "DELETE FROM cse360users WHERE userName = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setString(1, user.getUserName());
			pstmt.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Generates a new invitation code and inserts it into the database.
	 * 
	 * @param email the email address associated with the user being invited
	 * @param roles the roles chosen by the Admin user to assign to the invited user
	 * @param deadline the deadline chosen by the Admin user
	 * @return a random 4-digit invitation code as a string
	 */
	public String generateInvitationCode(String email, boolean[] roles, LocalDate deadline) {
	    String code = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
	    String query = "INSERT INTO InvitationCodes (email, code, role, deadline) VALUES (?, ?, ?, ?)";
	    System.out.println(code);
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, email);
	        pstmt.setString(2, code);
	        pstmt.setString(3,	Arrays.toString(roles));
	        pstmt.setDate(4, java.sql.Date.valueOf(deadline));
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return code;
	}
	
	/**
	 * Calls markInvitationCodeAsUsed() to mark an invitation code as used and calls checkExpiration() to check if the code is expired.
	 * 
	 * @param code the invitation code being checked
	 * @return true or false as to whether the invitation code has already expired
	 */
	public boolean validateInvitationCode(String code) {
	    String query = "SELECT COUNT(*) FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            // Mark the code as used
	            markInvitationCodeAsUsed(code);
	            if(checkExpiration(code)) {
	            	return true;
	            }else {
	            	return false;
	            }
	            
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	/**
	 * Marks an invitation code as used in the database.
	 * 
	 * @param code the code to mark used in the database
	 */
	private void markInvitationCodeAsUsed(String code) {
	    String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Checks if an invitation code is expired based on the deadline set and the current date/time.
	 * 
	 * @param code the code to check the expiration date for
	 * @return true or false as to whether the invitation code is expired
	 */
	public boolean checkExpiration(String code) {
		String query = "SELECT deadline FROM InvitationCodes WHERE code = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1,  code);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				System.out.print(rs.getDate(1));
				System.out.println(Date.valueOf(LocalDate.now()));
				if(rs.getDate(1).after(Date.valueOf(LocalDate.now()))){
					return true;
				}else {
					return false;
				}
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	 /**
	  * Closes the database connection and statement.
	  */
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}

	/**
	 * Update user's password in userTable.
	 * 
	 * @param userName the users userName
	 * @param newPass the users newly created password to update in the database
	 * @param oneTimePass the one-time-password given to the user during their password reset request
	 */
	public void setUserPassword(String userName, String newPass, String oneTimePass) {
		String query = "UPDATE cse360users SET password = ? WHERE userName = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setString(1, newPass);
			pstmt.setString(2, userName);
			pstmt.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates a password reset request in the passResets table.
	 * 
	 * @param userName the users userName
	 * @param oneTimePass the one-time-password to be given to the user to be entered before being prompted to enter a new password
	 */
	public void createRequest(String userName, String oneTimePass) {
		int expiration = (int) System.currentTimeMillis() / 1000; //Stores creation time in seconds.
		String query = "INSERT INTO passwordResets (userName, resetCode, expiration) VALUES(?, ?, ?)"; 
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setString(1, userName);
			pstmt.setString(2, oneTimePass);
			pstmt.setInt(3, expiration);
			pstmt.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	};
	
	/**
	 * Removes a request from the passResets table.
	 * 
	 * @param userName the users userName
	 */
	public void deleteRequest(String userName) {
		String query = "DELETE FROM passwordResets WHERE userName = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setString(1, userName);
			pstmt.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	};
	
	/**
	 * Retrieves all password reset requests from the passwordResets table in the database.
	 * 
	 * @return an Array List containing all the password Reset request information including userName, one-time-password, and expiration date
	 */
	public ArrayList<String> getRequests() {
		ArrayList<String> requests = new ArrayList<String>();
		String query = "SELECT userName || ',' || resetCode || ',' || expiration FROM passwordResets";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				requests.add(rs.getString(1));
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return requests;
	};
	
	/**
	 * Confirms if a password reset request exists in the database for the specified userName.
	 * 
	 * @param userName the users userName
	 * @return true or false based on whether a password reset request exists for the specified userName
	 */
	public boolean doesRequestExist(String userName) {
		String query = "SELECT * FROM passwordResets WHERE userName = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();
			return rs.next() ? true : false;
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	};
	
	/**
	 * Retrieves all user attributes based on the specified userName.
	 * 
	 * @param userName the users userName
	 * @return a User object that includes the users' firstName, lastName, email, and role based on the specified userName
	 */
	public User getUserInfo(String userName) {
		String query = "SELECT firstName || ',' || lastName || " 
				+ "',' || email || ',' || role FROM cse360users WHERE userName = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()){
				String[] userString = rs.getString(1).split(",");
				for(String s: userString) {
				System.out.println(s);
				}
				return new User(userName, "", stringToBoolArray(userString, 3), userString[2], userString[0], userString[1]);
			}	
		}catch(SQLException e) {
			e.printStackTrace();
			System.out.println("Nope don't work boy");
		}
		return null;
	}
	
	/**
	 * Assigns the specified roles to the specified user.
	 * 
	 * @param user the user object for the current user
	 * @param roles the roles to assign to the current user
	 */
	public void setUserRoles(User user, boolean[] roles) {
		String query = "UPDATE cse360users SET role = ? WHERE userName = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setString(1, Arrays.toString(roles));
			pstmt.setString(2,  user.getUserName());
			pstmt.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Helper function to convert a string array containing user information to a boolean array of that information.
	 * 
	 * @param userInfo the string array containing specified user information retrieved from the database
	 * @param index the specific index in which to start the loop-through of the argument string array userInfo
	 * @return a boolean array which is created from the string array that stores the user roles in the database
	 */
	public boolean[] stringToBoolArray (String[] userInfo, int index) {
		boolean[] roles = new boolean[5];
		int j = 0;
		for(int i = index; i < userInfo.length; i++) {
			if(userInfo[i].contains("true")) {
				roles[j] = true;
				j++;
			}else {
				roles[j] = false;
				j++;
			}
		}
		return roles;
	}
	
	/**
	 * Adds a question to the database.
	 * 
	 * @param questionTitle the question title input the user
	 * @param questionBody the contents of the question input by the user
	 * @param question the question object containing all the attributes of the question to add to the database
	 * @param student a user object containing all the attributes of the user submitting the question
	 * @return the database generated questionID
	 * @throws SQLException if the query cannot execute
	 */
	public int addQuestion(String questionTitle, String questionBody, Question question, User student) throws SQLException {
		String insertQuestion = "INSERT INTO questions (studentUserName, studentFirstName, studentLastName, questionTitle, questionBody, isResolved, creationTime, oldQuestionID, isFlagged, reasonIsFlagged, isHidden) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
		int questionIDGenerated = -1;
		try (PreparedStatement pstmt = connection.prepareStatement(insertQuestion, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, student.getUserName());
			pstmt.setString(2, student.getFirstName());
			pstmt.setString(3, student.getLastName());
			pstmt.setString(4, questionTitle);
			pstmt.setString(5, questionBody);
			pstmt.setBoolean(6, false);
			
			LocalDateTime creationTime = question.getCreationTime();
			pstmt.setTimestamp(7, Timestamp.valueOf(creationTime));
			
			pstmt.setInt(8, -1);
			pstmt.setBoolean(9, false);
			pstmt.setString(10, "");
			pstmt.setBoolean(11,  false);
			
			pstmt.executeUpdate();
			
			try (ResultSet rs = pstmt.getGeneratedKeys()) { // retrieve primary key "questionID" generated by INSERT above
				if (rs.next()) {
					questionIDGenerated = rs.getInt(1);
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return questionIDGenerated;
	}
	
	/**
	 * Retrieves the userName of the student who submitted a question based on the specified questionID.
	 * 
	 * @param questionID the questionID of the specified question
	 * @return the userName of the student who submitted the question
	 */
	public String getUserFromQuestionID(int questionID) {
		String studentUserName = "";
		String getUser = "SELECT studentUserName FROM questions WHERE questionID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(getUser)) {
			pstmt.setInt(1, questionID);
			
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				studentUserName = rs.getString("studentUserName");
			}
		}
		catch (SQLException e) {
	        e.printStackTrace();
	    }
		return studentUserName;
	}
	
	
	/**
	 * Retrieves all questions from the database.
	 * 
	 * @param user a User object representing the current user
	 * @return an ArrayList containing all of the questions stored in the database
	 */
	public ArrayList<Question> getAllQuestions(User user) { 
		ArrayList<Question> allQuestions = new ArrayList<>();
		String sqlQuery = "SELECT questionID, studentUserName, studentFirstName, studentLastName, questionTitle, questionBody, isResolved, creationTime, isFlagged, reasonIsFlagged FROM questions WHERE isHidden = FALSE";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int questionID = rs.getInt("questionID");
				String studentUserName = rs.getString("studentUserName");
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				String questionTitle = rs.getString("questionTitle");
				String questionBody = rs.getString("questionBody");
				boolean isResolved = rs.getBoolean("isResolved");
				Timestamp creationTime = rs.getTimestamp("creationTime");
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reasonIsFlagged = rs.getString("reasonIsFlagged");
				
				Question questionObject = new Question(questionID, studentUserName, studentFirstName, studentLastName, questionTitle, questionBody, isResolved, creationTime.toLocalDateTime(), isFlagged, reasonIsFlagged);
				
				allQuestions.add(questionObject);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return allQuestions;
	}
	
	/**
	 * Retrieves all question replies from the database.
	 * 
	 * @return an ArrayList containing all of the question replies from the database.
	 */
	public ArrayList<Question> getAllReplies() {
		ArrayList<Question> allReplies = new ArrayList<>();
		String sqlQuery = "SELECT replyID, questionID, studentUserName, studentFirstName, studentLastName, questionReplyText, replyingTo, isFlagged, reasonIsFlagged FROM questionReplies WHERE isHidden = FALSE";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int replyID = rs.getInt("replyID");
				int questionID = rs.getInt("questionID");
				String studentUserName = rs.getString("studentUserName");
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				String questionReplyText = rs.getString("questionReplyText");
				String questionReplyingto = rs.getString("replyingto");
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reasonIsFlagged = rs.getString("reasonIsFlagged");
				
				Question replyObject = new Question(replyID, questionID, studentUserName, studentFirstName, studentLastName, questionReplyText, questionReplyingto, isFlagged, reasonIsFlagged);
				allReplies.add(replyObject);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return allReplies;	
	}
	

	
	/**
	 * Retrieves all the questions from the database not marked resolved.
	 * 
	 * @return an ArrayList containing all of the questions marked FALSE for the attribute isResolved
	 */
	public ArrayList<Question> getUnresolvedQuestions() { 
		ArrayList<Question> unresolvedQuestions = new ArrayList<>();
		String sqlQuery = "SELECT questionID, studentUserName, studentFirstName, studentLastName, questionTitle, questionBody, isResolved, creationTime, isFlagged, reasonIsFlagged FROM questions WHERE isResolved = false AND isHidden = FALSE";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int questionID = rs.getInt("questionID");
				String studentUserName = rs.getString("studentUserName");
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				String questionTitle = rs.getString("questionTitle");
				String questionBody = rs.getString("questionBody");
				boolean isResolved = rs.getBoolean("isResolved");
				Timestamp creationTime = rs.getTimestamp("creationTime");
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reasonIsFlagged = rs.getString("reasonIsFlagged");
				
				Question questionObject = new Question(questionID, studentUserName, studentFirstName, studentLastName, questionTitle, questionBody, isResolved, creationTime.toLocalDateTime(), isFlagged, reasonIsFlagged);
				
				unresolvedQuestions.add(questionObject);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return unresolvedQuestions;
	}
	
	/**
	 * Retrieves all questions from the database which have received at least one answer.
	 * 
	 * @return an ArrayList containing all of the questions which have at least one potential answer
	 */
	public ArrayList<Question> getAnsweredQuestions() {
		ArrayList<Question> getAnsweredQuestions = new ArrayList<>();
		String sqlQuery = "SELECT q.questionID, q.studentUserName, q.studentFirstName, q.studentLastName, q.questionTitle, q.questionBody, q.isResolved, q.creationTime, q.isFlagged, q.reasonIsFlagged FROM questions q WHERE q.questionID IN (SELECT a.questionID FROM answers a) AND q.isHidden = FALSE";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int questionID = rs.getInt("questionID");
				String studentUserName = rs.getString("studentUserName");
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				String questionTitle = rs.getString("questionTitle");
				String questionBody = rs.getString("questionBody");
				boolean isResolved = rs.getBoolean("isResolved");
				Timestamp creationTime = rs.getTimestamp("creationTime");
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reasonIsFlagged = rs.getString("reasonIsFlagged");
				
				Question questionObject = new Question(questionID, studentUserName, studentFirstName, studentLastName, questionTitle, questionBody, isResolved, creationTime.toLocalDateTime(), isFlagged, reasonIsFlagged);
				
				getAnsweredQuestions.add(questionObject);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return getAnsweredQuestions;
	}
	
	/**
	 * Retrieves all replies associated with the questions that have at least one potential answer for filtering in the StudentQuestionsAnswers class.
	 * 
	 * @param answeredQuestions an Array List that contains all questions with at least one potential answer.
	 * @return an Array List that has added all question replies associated with the questions in the answeredQuestions Array List.
	 */
	public ArrayList<Question> addQuestionRepliesToAnsweredQuestions(ArrayList<Question> answeredQuestions) {
		String sqlQuery = "SELECT r.replyID, r.studentFirstName, r.studentLastName, r.questionReplyText, r.replyingTo FROM questionReplies r WHERE r.questionID IN (SELECT a.questionID FROM answers a) AND r.isHidden = FALSE";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int replyID = rs.getInt("replyID");
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				String questionReplyText = rs.getString("questionReplyText");
				String replyingTo = rs.getString("replyingTo");
				
				Question questionObject = new Question(replyID, studentFirstName, studentLastName, questionReplyText, replyingTo);
				
				answeredQuestions.add(questionObject);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return answeredQuestions;
	}
	
	/**
	 * Retrieves all questions from the database created within the last 48 hours.
	 * 
	 * @return an ArrayList of all questions created within the last 48 hours
	 */
	public ArrayList<Question> getRecentQuestions() { 
		ArrayList<Question> recentQuestions = new ArrayList<>();
		// This can be changed to 24 hours, or 168 for a week..
		LocalDateTime last48Hours = LocalDateTime.now().minusHours(48);
		String sqlQuery = "SELECT questionID, studentUserName, studentFirstName, studentLastName, questionTitle, questionBody, isResolved, creationTime FROM questions WHERE creationTime >= ? AND isHidden = FALSE";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			pstmt.setTimestamp(1, Timestamp.valueOf(last48Hours));
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int questionID = rs.getInt("questionID");
				String studentUserName = rs.getString("studentUserName");
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				String questionTitle = rs.getString("questionTitle");
				String questionBody = rs.getString("questionBody");
				boolean isResolved = rs.getBoolean("isResolved");
				Timestamp creationTime = rs.getTimestamp("creationTime");
				
				Question questionObject = new Question(questionID, studentUserName, studentFirstName, studentLastName, questionTitle, questionBody, isResolved, creationTime.toLocalDateTime());
				
				recentQuestions.add(questionObject);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return recentQuestions;
	}
	
	/**
	 * Marks a question as deleted in the database by setting all user identifiable information to "Deleted".
	 * 
	 * @param questionID the questionID associated with the question to mark as deleted.
	 */
	public void markQuestionDeleted(int questionID){
		String sqlUpdate = "UPDATE questions SET studentUserName = 'Deleted Student User Name', studentFirstName = 'Deleted Student First Name', studentLastName = 'Deleted Student Last Name', questionTitle = 'Deleted Question Title', questionBody = 'Deleted Question Body' WHERE questionID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlUpdate)) {
			pstmt.setInt(1, questionID);
			pstmt.executeUpdate();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Deletes a question entirely from the database.
	 * 
	 * @param questionID the questionID associated with the question to delete from the database
	 */
	public void deleteQuestion(int questionID) {
		String sqlDelete = "DELETE FROM questions where questionID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlDelete)) {
			pstmt.setInt(1, questionID);
			pstmt.executeUpdate();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Deletes all questions replies based on a specified questionID. Used in conjunction with the deleteQuestion() method.
	 * 
	 * @param questionID the questionID tied to all the question replies to delete
	 */
	public void deleteRepliesForQuestion(int questionID) {
		String sqlDelete = "DELETE FROM questionReplies where questionID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlDelete)) {
			pstmt.setInt(1, questionID);
			pstmt.executeUpdate();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieves all the question attributes for a specified questionID and returns a Question object.
	 * 
	 * @param questionID the questionID for the question whose attribute values are being requested
	 * @return a Question object containing all the attributes for the specified questionID
	 */
	public Question getQuestionByID(int questionID) {
		Question question = null;
		String sqlQuery = "SELECT studentUserName, studentFirstName, studentLastName, questionTitle, questionBody, isResolved, creationTime FROM questions WHERE questionID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			pstmt.setInt(1, questionID);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				String studentUserName = rs.getString("studentUserName");
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				String questionTitle = rs.getString("questionTitle");
				String questionBody = rs.getString("questionBody");
				Boolean isResolved = rs.getBoolean("isResolved");
				Timestamp creationTime = rs.getTimestamp("creationTime");
					
				question = new Question(questionID, studentUserName, studentFirstName, studentLastName, questionTitle, questionBody, isResolved, creationTime.toLocalDateTime());
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return question;
	}
	
	/**
	 * Adds a new question to the database which is tied to a specified already existing question by the questionID of this specified question.
	 * 
	 * @param newQuestionTitle the new question title text
	 * @param newQuestionBody the new question body text
	 * @param newQuestion the Question object representing the new question being created from the old question
	 * @param student the User object representing the current student user
	 * @param oldQuestionID the questionID of the question being "cloned"
	 * @return the questionID generated by the database for the new question
	 */
	public int createNewQuestionfromOld(String newQuestionTitle, String newQuestionBody, Question newQuestion, User student, int oldQuestionID) {
		int questionIDGenerated = -1;
		String insertQuestion = "INSERT INTO questions (studentUserName, studentFirstName, studentLastName, questionTitle, questionBody, isResolved, creationTime, oldQuestionID) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ";
		try (PreparedStatement pstmt = connection.prepareStatement(insertQuestion, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, newQuestion.getStudentUserName());
			pstmt.setString(2, newQuestion.getStudentFirstName());
			pstmt.setString(3, newQuestion.getStudentLastName());
			pstmt.setString(4, newQuestionTitle);
			pstmt.setString(5, newQuestionBody);
			pstmt.setBoolean(6, false);
			pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
			pstmt.setInt(8, oldQuestionID);
			pstmt.executeUpdate();
			
			try (ResultSet rs = pstmt.getGeneratedKeys()) { // retrieve primary key "questionID" generated by INSERT above
				if (rs.next()) {
					questionIDGenerated = rs.getInt(1);
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return questionIDGenerated;
	}
	
	/**
	 * Edits a question that already exists in the database.
	 * 
	 * @param modifiedQuestionTitle the user edited question title text
	 * @param modifiedQuestionBody the user edited question body text
	 * @param questionID the questionID specific to the question being modified
	 */
	public void editQuestion(String modifiedQuestionTitle, String modifiedQuestionBody, int questionID) {
		String sqlUpdate = "UPDATE questions SET questionTitle = ?, questionBody = ? WHERE questionID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlUpdate)) {
			pstmt.setString(1, modifiedQuestionTitle);
			pstmt.setString(2, modifiedQuestionBody);
			pstmt.setInt(3, questionID);
			pstmt.executeUpdate();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Marks a question as resolved in the database and updates the specified question object's isResolved attribute to true.
	 * 
	 * @param questionID the questionID specific to the question to mark resolved
	 * @param question the question object specific to the question to mark resolved
	 * @return true or false as to whether any rows were updated in the database which if true indicate that the isResolved attribute was set to true for some row
	 */
	public boolean markQuestionResolved(int questionID, Question question) {
	    String sqlUpdate = "UPDATE questions SET isResolved = TRUE WHERE questionID = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(sqlUpdate)) {
	        pstmt.setInt(1, questionID);
	        int rowsAffected = pstmt.executeUpdate();
	        connection.commit(); 
	        if (rowsAffected > 0) {
				question.setIsResolved(true);
			}
	        return rowsAffected > 0;
	    }
	    catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	/**
	 * Adds an answer to the database tied to a specific question.
	 * 
	 * @param answerText the contents of the Answer input by the user
	 * @param answer the Answer object representing the answer to be added to the database
	 * @param student the User object representing the current student 
	 * @param questionID the questionID for the question that is being answered
	 * @return the answerID generated by the database
	 */
	public int addAnswers(String answerText, Answer answer, User student, int questionID) {
		String insertAnswer = "INSERT INTO answers (studentUserName, studentFirstName, studentLastName, questionID, answerText, isAnswerUnread, isResolved, creationTime, isFlagged, reasonIsFlagged, isHidden) VALUES (?, ?, ?, ?, ?, TRUE, FALSE, ?, ?, ?, ?) ";
		int answerIDGenerated = -1;
		try (PreparedStatement pstmt = connection.prepareStatement(insertAnswer, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, student.getUserName());
			pstmt.setString(2, student.getFirstName());
			pstmt.setString(3, student.getLastName());
			pstmt.setInt(4,questionID);
			pstmt.setString(5, answerText);
			//pstmt.setBoolean(6, answer.getIsAnswerUnread());
			//pstmt.setBoolean(7, answer.getIsResolved());
			
			LocalDateTime creationTime = answer.getCreationTime();
			pstmt.setTimestamp(6, Timestamp.valueOf(creationTime));
			pstmt.setBoolean(7, false);
			pstmt.setString(8, "");
			pstmt.setBoolean(9, false);
			pstmt.executeUpdate();
			
			try(ResultSet rs = pstmt.getGeneratedKeys()) {
				if (rs.next()) {
					answerIDGenerated = rs.getInt(1);
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return answerIDGenerated;
	}
	
	/**
	 * Retrieves all the answers from the database.
	 * 
	 * @param user the User object representing the current user
	 * @return an ArrayList of all the answers existing in the database
	 */
	public ArrayList<Answer> getAllAnswers(User user) { 
		ArrayList<Answer> allAnswers = new ArrayList<>();
		String sqlQuery = "SELECT answerID, questionID, studentUserName, studentFirstName, studentLastName, answerText, isAnswerUnread, isResolved, creationTime, isFlagged, reasonIsFlagged FROM answers WHERE isHidden = FALSE";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int answerID = rs.getInt("answerID");
				int questionID = rs.getInt("questionID");
				String studentUserName= rs.getString("studentUserName");
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				String answerText= rs.getString("answerText");
				boolean isAnswerUnread = rs.getBoolean("isResolved");
				boolean isResolved = rs.getBoolean("isResolved");
				Timestamp creationTime = rs.getTimestamp("creationTime");
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reasonIsFlagged = rs.getString("reasonIsFlagged");
				
				Answer answerObject = new Answer(answerID, questionID, studentUserName, studentFirstName, studentLastName, answerText, isAnswerUnread, isResolved, creationTime.toLocalDateTime(), isFlagged, reasonIsFlagged);
				
				allAnswers.add(answerObject);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return allAnswers;
	}
	
	/**
	 * Retrieve only answers from the database for the specified questionID.
	 * 
	 * @param questionID the questionID for the question to retrieve answers for
	 * @return an ArrayList containing only the answers for the specified questionID
	 */
	public ArrayList<Answer> getAnswersByQuestionID(int questionID) {
		ArrayList<Answer> answersForQuestionID = new ArrayList<>();
		String sqlQuery = "SELECT answerID, questionID, studentUserName, studentFirstName, studentLastName, answerText, isResolved, isAnswerUnread, creationTime, isFlagged, reasonIsFlagged FROM answers WHERE questionID = ? AND isHidden = FALSE";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			pstmt.setInt(1, questionID);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int answerID = rs.getInt("answerID");
				int questID = rs.getInt("questionID");
				String studentUserName = rs.getString("studentUserName");
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				String answerText = rs.getString("answerText");
				boolean isResolved = rs.getBoolean("isResolved");
				boolean isAnswerUnread = rs.getBoolean("isAnswerUnread");
				Timestamp creationTime = rs.getTimestamp("creationTime");
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reasonIsFlagged = rs.getString("reasonIsFlagged");
					
				Answer answer = new Answer(answerID, questID, studentUserName, studentFirstName, studentLastName, answerText, isAnswerUnread, isResolved, creationTime.toLocalDateTime(), isFlagged, reasonIsFlagged);
				answersForQuestionID.add(answer);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return answersForQuestionID;
	}
	
	/**
	 * Retrieve the questionID from the database tied to the specified answerID.
	 * 
	 * @param answerID the answerID for the specified answer 
	 * @return the questionID associated with the specified answerID
	 */
	public int getQuestionIDForAnswer(int answerID) {
		int questionID = -1;
		String sqlQuery = "SELECT questionID FROM answers WHERE answerID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			pstmt.setInt(1, answerID);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				questionID = rs.getInt("questionID");
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return questionID;
	}
	
	/**
	 * Retrieves all answers from the database whose isAnswerUnread attribute in the database is set to true.
	 * 
	 * @return an ArrayList of all answers whose isAnswerUnread attribute in the database is set to true
	 */
	public ArrayList<Answer> getUnreadAnswers() { 
		ArrayList<Answer> unreadAnswers = new ArrayList<>();
		String sqlQuery = "SELECT studentFirstName, studentLastName, answerText FROM answers WHERE isAnswerUnread = true";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			ResultSet rs = pstmt.executeQuery(sqlQuery);
			while(rs.next()) {
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				String answerText = rs.getString("answerText");
				
				Answer answerObject = new Answer(studentFirstName, studentLastName, answerText) ;
				
				unreadAnswers.add(answerObject);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return unreadAnswers;
	}
	
	/**
	 * Retrieves all answers from the database that have not been marked as resolved by a user. 
	 * 
	 * @return an ArrayList of all answers from the database whose isResolved attribute is set to false
	 */
	public ArrayList<Answer> getAnswersUnresolvedQuestions() {
		ArrayList<Answer> answersForUnresolvedQuestions = new ArrayList<>();
		String sqlQuery = "SELECT studentFirstName, studentLastName, answerText FROM answers WHERE isResolved = false";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			ResultSet rs = pstmt.executeQuery(sqlQuery);
			while(rs.next()) {
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				String answerText = rs.getString("answerText");
				
				Answer answerObject = new Answer(studentFirstName, studentLastName, answerText) ;
				
				answersForUnresolvedQuestions.add(answerObject);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return answersForUnresolvedQuestions;
	}
	
	
	/**
	 * Retrieves all answers from the database that have been marked as resolved by a user.
	 * 
	 * @return an ArrayList of all answers from the database whose isResolved attribute is set to true
	 */
	public ArrayList<Answer> getResolvedAnswers() {
		ArrayList<Answer> resolvedAnswers = new ArrayList<>();
		String sqlQuery = "SELECT answerID, questionID, studentUserName, studentFirstName, studentLastName, answerText, isAnswerUnread, isResolved, creationTime, isFlagged, reasonIsFlagged FROM answers WHERE isResolved = TRUE AND isHidden = FALSE";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int answerID = rs.getInt("answerID");
				int questionID = rs.getInt("questionID");
				String studentUserName= rs.getString("studentUserName");
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				String answerText= rs.getString("answerText");
				boolean isAnswerUnread = rs.getBoolean("isResolved");
				boolean isResolved = rs.getBoolean("isResolved");
				Timestamp creationTime = rs.getTimestamp("creationTime");
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reasonIsFlagged = rs.getString("reasonIsFlagged");
				
				Answer answerObject = new Answer(answerID, questionID, studentUserName, studentFirstName, studentLastName, answerText, isAnswerUnread, isResolved, creationTime.toLocalDateTime(), isFlagged, reasonIsFlagged);
				
				resolvedAnswers.add(answerObject);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return resolvedAnswers;
	}
	
	/**
	 * Retrieves all answers from the database that have not been marked as resolved by a user. 
	 * 
	 * @return an ArrayList of all answers from the database whose isResolved attribute is set to false
	 */
	public ArrayList<Answer> getUnresolvedAnswers() {
		ArrayList<Answer> unresolvedAnswers = new ArrayList<>();
		String sqlQuery = "SELECT answerID, questionID, studentUserName, studentFirstName, studentLastName, answerText, isAnswerUnread, isResolved, creationTime, isFlagged, reasonIsFlagged FROM answers WHERE isResolved = FALSE AND isHidden = FALSE";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int answerID = rs.getInt("answerID");
				int questionID = rs.getInt("questionID");
				String studentUserName= rs.getString("studentUserName");
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				String answerText= rs.getString("answerText");
				boolean isAnswerUnread = rs.getBoolean("isResolved");
				boolean isResolved = rs.getBoolean("isResolved");
				Timestamp creationTime = rs.getTimestamp("creationTime");
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reasonIsFlagged = rs.getString("reasonIsFlagged");
				
				Answer answerObject = new Answer(answerID, questionID, studentUserName, studentFirstName, studentLastName, answerText, isAnswerUnread, isResolved, creationTime.toLocalDateTime(), isFlagged, reasonIsFlagged) ;
				
				unresolvedAnswers.add(answerObject);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return unresolvedAnswers;
	}
	
	/**
	 * Retrieve and return an Answer object containing all attributes specific to a specified answerID.
	 * 
	 * @param answerID the answerID specific to the answer to be retrieved from the database
	 * @return an Answer object containing all attributes associated with the specified answerID
	 */
	public Answer getAnswerByID(int answerID) {
		Answer answer = null;
		String sqlQuery = "SELECT questionID, studentUserName, studentFirstName, studentLastName, answerText, isAnswerUnread, isResolved, creationTime FROM answers WHERE answerID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			pstmt.setInt(1, answerID);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				int questionID = rs.getInt("questionID");
				String studentUserName= rs.getString("studentUserName");
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				String answerText = rs.getString("answerText");
				boolean isAnswerUnread = rs.getBoolean("isResolved");
				boolean isResolved = rs.getBoolean("isResolved");
				Timestamp creationTime = rs.getTimestamp("creationTime");

				answer = new Answer(answerID, questionID, studentUserName, studentFirstName, studentLastName, answerText, isAnswerUnread, isResolved, creationTime.toLocalDateTime());
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return answer;
	}
	
	/**
	 * Delete an answer entirely from the database.
	 * 
	 * @param answerID the answerID associated with the answer to delete
	 */
	public void deleteAnswer(int answerID) {
		String sqlDelete = "DELETE FROM answers where answerID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlDelete)) {
			pstmt.setInt(1, answerID);
			pstmt.executeUpdate();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Edit an answer that already exists in the database.
	 * 
	 * @param modifiedAnswer the answer text contents modified by the user
	 * @param answerID the answerID for the answer to delete
	 */
	public void editAnswer(String modifiedAnswer, int answerID) {
		String sqlUpdate = "UPDATE answers SET answerText = ? WHERE answerID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlUpdate)) {
			pstmt.setString(1, modifiedAnswer);
			pstmt.setInt(2, answerID);
			pstmt.executeUpdate();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Marks an answer as resolved in the database and updates the specified answer object's isResolved attribute to true.
	 * 
	 * @param answerID the answerID specific to the answer to mark resolved
	 * @param answer the answer object specific to the answer to mark resolved
	 * @return true or false as to whether any rows were updated in the database which if true indicate that the isResolved attribute was set to true for some row
	 */
	public boolean markAnswerResolved(int answerID, Answer answer) {
		String sqlUpdate = "UPDATE answers SET isResolved = TRUE WHERE answerID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlUpdate)) {
			pstmt.setInt(1, answerID);
			int rowsAffected = pstmt.executeUpdate();
			connection.commit(); 
			if (rowsAffected > 0) {
				answer.setIsResolved(true);
			}
			return rowsAffected > 0;
			
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	

	/**
	 * Adds a question reply to the database.
	 * 
	 * @param replyText the content of the reply as entered by the user
	 * @param parentQuestionID the questionID of the question being replied to
	 * @param questionReply the Question object containing the reply attributes
	 * @param student the User object associated with the current user
	 * @param replyingTo the question title text of the question being replied to
	 * @return the replydID generated by the database
	 */
	public int addReply(String replyText, int parentQuestionID, Question questionReply, User student, String replyingTo) {
		String insertQuestionReply = "INSERT INTO questionReplies (questionID, studentUserName, studentFirstName, studentLastName, questionReplyText, creationTime, replyingTo, isFlagged, reasonIsFlagged, isHidden) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
		int replyIDGenerated = -1;
		try (PreparedStatement pstmt = connection.prepareStatement(insertQuestionReply, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setInt(1, parentQuestionID);
			pstmt.setString(2, student.getUserName());
			pstmt.setString(3, student.getFirstName());
			pstmt.setString(4, student.getLastName());
			pstmt.setString(5, replyText);
			
			LocalDateTime creationTime = questionReply.getCreationTime();
			pstmt.setTimestamp(6, Timestamp.valueOf(creationTime));
			pstmt.setString(7, replyingTo);
			pstmt.setBoolean(8, false);
			pstmt.setString(9, "");
			pstmt.setBoolean(10, false);
			
			pstmt.executeUpdate();
			
			try (ResultSet rs = pstmt.getGeneratedKeys()) { // retrieve primary key "replyID" generated by INSERT above
				if (rs.next()) {
					replyIDGenerated = rs.getInt(1);
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return replyIDGenerated;
	}

	/**
	 * Retrieves all questions from the database that have received no potential answers.
	 * 
	 * @return an ArrayList of all questions from the database that have no tied answers
	 */
	public ArrayList<Question> getUnansweredQuestions() {
		ArrayList<Question> unansweredQuestions = new ArrayList<>();
		String sqlQuery = "SELECT q.questionID, q.studentUserName, q.studentFirstName, q.studentLastName, q.questionTitle, q.questionBody, q.isResolved, q.creationTime, q.isFlagged, q.reasonIsFlagged " +
				"FROM questions q LEFT JOIN answers a ON q.questionID = a.questionID " +
				"WHERE a.answerID IS NULL AND q.isHidden = FALSE";

		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int questionID = rs.getInt("questionID");
				String studentUserName = rs.getString("studentUserName");
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				String questionTitle = rs.getString("questionTitle");
				String questionBody = rs.getString("questionBody");
				boolean isResolved = rs.getBoolean("isResolved");
				LocalDateTime creationTime = rs.getTimestamp("creationTime").toLocalDateTime();
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reasonIsFlagged = rs.getString("reasonIsFlagged");

				Question questionObject = new Question(questionID, studentUserName, studentFirstName, studentLastName, questionTitle, questionBody, isResolved, creationTime, isFlagged, reasonIsFlagged);
				questionObject.setQuestionID(questionID);
				unansweredQuestions.add(questionObject);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return unansweredQuestions;
	}

	/**
	 * Retrieves all questions from the database that have been submitted by the specified userName.
	 * 
	 * @param userName the user's userName
	 * @return an ArrayList containing all questions from the database which were submitted by the specified userName
	 */
	public ArrayList<Question> getQuestionsByUserName(String userName) {
		ArrayList<Question> userQuestions = new ArrayList<>();
		String sqlQuery = "SELECT questionID, studentFirstName, studentLastName, questionTitle, questionBody, isResolved, creationTime " +
				"FROM questions WHERE studentUserName = ? AND isHidden = FALSE";

		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int questionID = rs.getInt("questionID");
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				String questionTitle = rs.getString("questionTitle");
				String questionBody = rs.getString("questionBody");
				boolean isResolved = rs.getBoolean("isResolved");
				LocalDateTime creationTime = rs.getTimestamp("creationTime").toLocalDateTime();

				Question questionObject = new Question(questionID, userName, studentFirstName, studentLastName, questionTitle, questionBody, isResolved, creationTime);
				questionObject.setQuestionID(questionID);
				userQuestions.add(questionObject);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userQuestions;
	}

	/**
	 * Retrieves the count of all answers for a specified questionID who are marked as having been unread.
	 * 
	 * @param questionID the questionID of the question 
	 * @return the number of answers associated with the specified answerID whose attribute isAnswerUnread in the database is set to true
	 */
	public int countUnreadPotentialAnswers(int questionID) {
		int unreadCount = 0;
		String sqlQuery = "SELECT COUNT(*) AS unreadCount FROM answers WHERE questionID = ? AND isAnswerUnread = TRUE";

		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			pstmt.setInt(1, questionID);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				unreadCount = rs.getInt("unreadCount");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return unreadCount;
	}

	/**
	 * Retrieves all answers from the database that have been marked as resolved.
	 * 
	 * @return an ArrayList of all answers whose isResolved attribute in the database is marked as true
	 */
	public ArrayList<Answer> getResolvedAnswersWithQuestions() {
		ArrayList<Answer> resolvedAnswers = new ArrayList<>();
		String sqlQuery = "SELECT a.answerID, a.studentFirstName, a.studentLastName, a.answerText, a.questionID, q.questionTitle, a.isFlagged, a.isReasonFlagged " +
				"FROM answers a JOIN questions q ON a.questionID = q.questionID " +
				"WHERE a.isResolved = TRUE AND a.isHidden = FALSE";

		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int answerID = rs.getInt("answerID");
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				String answerText = rs.getString("answerText");
				int questionID = rs.getInt("questionID");
				String questionTitle = rs.getString("questionTitle");
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reasonIsFlagged = rs.getString("reasonIsFlagged");

				Answer answerObject = new Answer(studentFirstName, studentLastName, answerText);
				answerObject.setAnswerID(answerID);
				answerObject.setQuestionID(questionID);
				answerObject.setIsFlagged(isFlagged);
				answerObject.setReasonIsFlagged(reasonIsFlagged);
				
				Question questionObject = new Question(studentFirstName, studentLastName, questionTitle);
				
				String questionTitleForAnswer = questionObject.getQuestionTitle();
				answerObject.setQuestionTitleForAnswer(questionTitleForAnswer);
				resolvedAnswers.add(answerObject);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resolvedAnswers;
	}
	
	/**
	 * Create newRoleRequest in table newRoleRequests in the database.
	 * 
	 * @param user the User object specific to the current user
	 * @param roles a boolean array whose indexes containing "true" represent the roles being requested by the user to be provisioned
	 * @param userID the userID specific to the user requesting new role(s)
	 * @return the roleRequestID generated by the database
	 */
	public int createNewRoleRequest(User user, boolean[] roles, int userID) {
		int roleRequestIDGenerated = -1;
		String requestStatus = "Pending";
		String query = "INSERT INTO newRoleRequests (userID, userName, userFirstName, userLastName, isRequestApproved, requestStatus, role) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try(PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
			pstmt.setInt(1, userID);
			pstmt.setString(2, user.getUserName());
			pstmt.setString(3, user.getFirstName());
			pstmt.setString(4, user.getLastName());
			pstmt.setBoolean(5, false);
			pstmt.setString(6, requestStatus);
			pstmt.setString(7, Arrays.toString(roles));
			pstmt.executeUpdate();
			
			try (ResultSet rs = pstmt.getGeneratedKeys()) {
				if (rs.next()) {
					roleRequestIDGenerated = rs.getInt(1);
				}
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return roleRequestIDGenerated;
	}
	
	/**
	 * Get the status of a role request by requestID.
	 * 
	 * @param roleRequestID the roleRequestID for the specified roleRequest
	 * @return the status of the role request which should either be "Pending", "Approved", or "Denied"
	 */
	public String getNewRoleRequestStatus(int roleRequestID) {
		String roleRequestStatus = "";
		String query = " SELECT requestStatus FROM newRoleRequests WHERE roleRequestID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, roleRequestID);
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				roleRequestStatus = rs.getString("requestStatus");
			}
		}
		catch (SQLException e) {
	        e.printStackTrace();
	    }
		return roleRequestStatus;
	}
	
	/**
	 * Get all role requests for the specified userID that exist in the database.
	 * This will need to be revised in TP4 to use an ArrayList if we enable role requests for roles beyond a Reviewer as this can only return the details for
	 * a single role request.
	 * 
	 * @param userID the userID of the specific user
	 * @return a string array containing all the role request information for the specified userID. If no role request exists, an empty string array is returned
	 */
	public String[] getAllRoleRequestsByUserID(int userID) {
		String[] roleRequests = null;
		String query = "SELECT userName, userFirstName, userLastName, role, requestStatus FROM newRoleRequests WHERE userID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, userID);
			
			try (ResultSet rs = pstmt.executeQuery()) {
				if (!rs.next()) {
					roleRequests = new String[0];
				}
				else {
					roleRequests = new String[4];
					String userName = rs.getString("userName");
					String userFirstName = rs.getString("userFirstName");
					String userLastName = rs.getString("UserLastName");
					String name = userFirstName + " " + userLastName;
					String roles = rs.getString("role");
					String requestStatus = rs.getString("requestStatus");
					
					roleRequests[0] = userName;
					roleRequests[1] = name;
					roleRequests[2] = roles;
					roleRequests[3] = requestStatus;
				}
			}
		}
		catch (SQLException e) {
	        e.printStackTrace();
	    }
		return roleRequests;
	}
	
	/**
	 * Check if a role request already exists in the database.
	 * 
	 * @param userID the userID associated with the current user
	 * @param newRolesRequestRoles a boolean array whose indexes containing "true" represent the roles being requested by the user to be provisioned
	 * @return true or false as to whether a role request exists. False will be returned if no request exists for the specified userID, the user is requesting
	 * a different role to be provisioned that has not been requested in an existing role request, or if the user was denied provisioning for the requested role
	 * in an existing request. True will be returned if there is an existing request for the specified role(s) and the status is either "Pending" or "Approved"
	 */
	public boolean doesRoleRequestExist(int userID, boolean[] newRolesRequestRoles) {
		boolean doesRequestExist = false;
		int i = 0;
		int indexMatching = -1;
		String query = "SELECT role, requestStatus FROM newRoleRequests WHERE userID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, userID);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (!rs.next()) {
					doesRequestExist = false;
				}
				else {
					// roles from database as a String[]
					String[] requestRolesAsStringArray = rs.getString(1).split(",");
					// roles from database as a boolean []
					boolean [] requestRolesAsBooleanArray = stringToBoolArray(requestRolesAsStringArray, 0);
					String requestStatus = rs.getString("requestStatus");
					
					// check if role(s) the user is requesting match the request already in the database
					while (i < newRolesRequestRoles.length) {
						if (newRolesRequestRoles[i] == true && requestRolesAsBooleanArray[i] == true) {
							indexMatching = i;
							break;
						}
						else {
							i++;
						}
					}
					// user is not attempting to make another request for a role they have already requested
					if (indexMatching == -1) {
						doesRequestExist = false;
					}
					// user already has a request for at least one role in the current attempted request
					else {
						// allow user to submit another request
						if (requestStatus.equals("Denied")) {
							doesRequestExist = false;
						}
						// user cannot submit another request
						else if (requestStatus.equals("Pending") || requestStatus.equals("Approved")) {
							doesRequestExist = true;
						}
					}
				}
			}
		}
		catch (SQLException e) {
	        e.printStackTrace();
	    }
		return doesRequestExist;
	}
	
	/**
	 * Delete a Role Request from the database based on the specified userName.
	 * This will need to be revised in TP4 if we enable roles beyond Reviewer as a user could then have multiple requests, each for a different role and this
	 * would need to be updated to indicate which specific role requests to delete.
	 * 
	 * @param userName the userName associated with the role requests to delete.
	 */
	public void deleteRoleRequest(String userName) {
		String query = "DELETE FROM newRoleRequests WHERE userName = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setString(1, userName);
			pstmt.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	};
	
	
	/**
	 * Get a user's userID from the database by specified User object.
	 * 
	 * @param user the User object for the current user
	 * @return the userID associated with the specified User object
	 */
	public int getUserID(User user) {
		int userID = -1;
		String userName = user.getUserName();
		String query = "SELECT id FROM cse360users WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				userID = rs.getInt("id");
			}
		}
		catch (SQLException e) {
	        e.printStackTrace();
	    }
		return userID;
	}
	
	/**
	 * Add a review to the reviews table in the database.
	 * 
	 * @param userName the reviewers userName
	 * @param firstName the reviewers firstName
	 * @param lastName the reviewers lastName
	 * @param reviewBody the text contents of the review
	 * @param questionID the questionID lied to the answer that the review is for
	 * @param answerID the answerID for the answer that the review is for
	 * @param prevID defaults to -1 if this is not a review created from a previous review or is the reviewID of the "parent" review
	 * @return the reviewID generated by the database
	 */
	public int addReview(String userName, String firstName, String lastName, String reviewBody, int questionID, int answerID, int prevID) {
		String insertReview = "INSERT INTO reviews (reviewerUserName, reviewerFirstName, reviewerLastName, reviewBody, questionID, answerID, prevReviewID, isFlagged, reasonIsFlagged, isHidden) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
		int reviewIDGenerated = -1;
		try (PreparedStatement pstmt = connection.prepareStatement(insertReview, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, userName);
			pstmt.setString(2, firstName);
			pstmt.setString(3, lastName);
			pstmt.setString(4, reviewBody);
			pstmt.setInt(5, questionID);
			pstmt.setInt(6, answerID);
			pstmt.setInt(7, prevID);
			pstmt.setBoolean(8, false);
			pstmt.setString(9, "");
			pstmt.setBoolean(10, false);
			
			pstmt.executeUpdate();
			
			try (ResultSet rs = pstmt.getGeneratedKeys()) { // retrieve primary key "questionID" generated by INSERT above
				if (rs.next()) {
					reviewIDGenerated = rs.getInt(1);
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return reviewIDGenerated;
	}
	
	/**
	 * Edit a review that already exists in the database.
	 * 
	 * @param newBody the modified review text
	 * @param reviewID the reviewID of the review to modify
	 */
	public void editReview(String newBody, int reviewID) {
		String editReview = "UPDATE reviews SET reviewBody = ? WHERE reviewID = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(editReview)){
			pstmt.setString(1, newBody);
			pstmt.setInt(2, reviewID);
			pstmt.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieves all reviews from the database.
	 * 
	 * @param user the User object for the current user.
	 * @return an ArrayList containing all reviews existing in the database.
	 */
	public ArrayList<Review> getAllReviews(User user) { 
		ArrayList<Review> allReviews = new ArrayList<>();
		String sqlQuery = "SELECT questionID, answerID, prevReviewID, reviewID, reviewBody, reviewerUserName, reviewerFirstName, reviewerLastName, isFlagged, reasonIsFlagged FROM reviews WHERE isHidden = FALSE";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int questionID = rs.getInt("questionID");
				int answerID = rs.getInt("answerID");
				int prevID = rs.getInt("prevReviewID");
				int reviewID = rs.getInt("reviewID");
				String reviewBody = rs.getString("reviewBody");
				String reviewerUserName = rs.getString("reviewerUserName");
				String reviewerFirstName = rs.getString("reviewerFirstName");
				String reviewerLastName = rs.getString("reviewerLastName");
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reasonIsFlagged = rs.getString("reasonIsFlagged");
				
				Review reviewObject = new Review(questionID, answerID, prevID, reviewID, reviewBody, reviewerUserName, reviewerFirstName, reviewerLastName, isFlagged, reasonIsFlagged);
				
				allReviews.add(reviewObject);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return allReviews;
	}
	
	/**
	 * Confirms if a specified review was created by a Trusted Reviewer of the specified user.
	 * 
	 * @param review the Review object of the review being parsed by the CellFactory
	 * @param user the User object for the current user
	 * @return true or false based on whether the specified review was submitted by a Trusted Reviewer of the specified user
	 */
	public boolean checkIfReviewCreatedByTrustedReviewer(Review review, User user) {
		boolean reviewCreatedByTrustedReviewer = false;
		String reviewerUserName = review.getReviewerUserName();
		String sqlQuery = "SELECT t.reviewerUserName FROM trustedReviewers t INNER JOIN reviews r ON r.reviewerUserName = t.reviewerUserName WHERE t.studentUserName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			pstmt.setString(1,user.getUserName());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				if (rs.getString("reviewerUserName").equals(reviewerUserName)) {
					reviewCreatedByTrustedReviewer = true;
					break;
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return reviewCreatedByTrustedReviewer;		
	}
	
	/**
	 * Retrieves all reviews from the database where the reviewer is on the specified users Trusted Reviewers List.
	 * 
	 * @param user the User object for the current user
	 * @return an ArrayList containing only the reviews submitted by reviewers who have been added to the specified users Trusted Reviewers List
	 */
	public ArrayList<Review> getOnlyReviewsForTrustedReviewers(User user) {
		ArrayList<Review> ReviewsByTrustedReviewer = new ArrayList<>();
		
		String sqlQuery = "SELECT r.questionID, r.answerID, r.prevReviewID, r.reviewID, r.reviewBody, r.reviewerUserName, r.reviewerFirstName, r.reviewerLastName FROM reviews r " +
				"INNER JOIN trustedReviewers t ON r.reviewerUserName = t.reviewerUserName WHERE t.studentUserName = ? AND r.isHidden = FALSE";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			pstmt.setString(1,user.getUserName());
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int questionID = rs.getInt("questionID");
				int answerID = rs.getInt("answerID");
				int prevID = rs.getInt("prevReviewID");
				int reviewID = rs.getInt("reviewID");
				String reviewBody = rs.getString("reviewBody");
				String reviewerUserName = rs.getString("reviewerUserName");
				String reviewerFirstName = rs.getString("reviewerFirstName");
				String reviewerLastName = rs.getString("reviewerLastName");
				
				Review reviewObject = new Review(questionID, answerID, prevID, reviewID, reviewBody, reviewerUserName, reviewerFirstName, reviewerLastName);
				
				ReviewsByTrustedReviewer.add(reviewObject);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return ReviewsByTrustedReviewer;		
	}
	
	/**
	 * Retrieves all answers from the database that have been reviewed by a specified user's Trusted Reviewers.
	 * 
	 * @param user the User object for the current user
	 * @return an ArrayList containing all the answers which have reviews submitted by the specified users Trusted Reviewers
	 */
	public ArrayList<Answer> getOnlyAnswersReviewedByTrustedReviewers(User user) {
		ArrayList<Answer> AnswersReviewedByTrustedReviewer = new ArrayList<>();
		String sqlQuery = "SELECT DISTINCT a.answerID, a.studentUserName, a.studentFirstName, a.studentLastName, a.questionID, a.answerText, a.isAnswerUnread, a.isResolved, a.creationTime " +
				"FROM answers a " +
				"INNER JOIN reviews r ON a.answerID = r.answerID " +
				"INNER JOIN trustedReviewers t ON r.reviewerUserName = t.reviewerUserName " +
				"WHERE t.studentUserName = ? AND a.isHidden = FALSE";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			pstmt.setString(1,user.getUserName());
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int answerID = rs.getInt("answerID");
				String studentUserName = rs.getString("studentUserName");
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				int questionID = rs.getInt("questionID");
				String answerText = rs.getString("answerText");
				boolean isAnswerUnread = rs.getBoolean("isAnswerUnread");
				boolean isResolved = rs.getBoolean("isResolved");
				Timestamp creationTime = rs.getTimestamp("creationTime");
				
				Answer answerObject = new Answer(answerID, questionID, studentUserName, studentFirstName, studentLastName, answerText, isAnswerUnread, isResolved, creationTime.toLocalDateTime());
				
				AnswersReviewedByTrustedReviewer.add(answerObject);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return AnswersReviewedByTrustedReviewer;
	}
	

	/**
	 * Retrieve all reviews from the database submitted by the specified reviewer userName.
	 * 
	 * @param userName the userName of the current reviewer user
	 * @return an ArrayList of all reviews submitted by the specified reviewer userName
	 */
	public ArrayList<Review> getReviewsByUsername(String userName) {
		ArrayList<Review> reviews = new ArrayList<>();
		String sqlQuery = "SELECT questionID, answerID, prevReviewID, reviewID, reviewBody, reviewerUserName, reviewerFirstName, reviewerLastName FROM reviews WHERE reviewerUserName = ? AND isHidden = FALSE";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int questionID = rs.getInt("questionID");
				int answerID = rs.getInt("answerID");
				int prevID = rs.getInt("prevReviewID");
				int reviewID = rs.getInt("reviewID");
				String reviewBody = rs.getString("reviewBody");
				String reviewerUserName = rs.getString("reviewerUserName");
				String reviewerFirstName = rs.getString("reviewerFirstName");
				String reviewerLastName = rs.getString("reviewerLastName");
				
				Review reviewObject = new Review(questionID, answerID, prevID, reviewID, reviewBody, reviewerUserName, reviewerFirstName, reviewerLastName);
				
				reviews.add(reviewObject);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return reviews;
	}
	
	/**
	 * Retrieves all reviews from the database that were created for the specified answerID.
	 * 
	 * @param answerID the answerID for a specific answer
	 * @return an ArrayList containing all reviews tied to a specified answerID
	 */
	public ArrayList<Review> getReviewByAnswerID(int answerID){
		ArrayList<Review> reviews = new ArrayList<>();
		String sqlQuery = "SELECT questionID, answerID, prevReviewID, reviewID, reviewBody, reviewerUserName, reviewerFirstName, reviewerLastName, isFlagged, reasonIsFlagged FROM reviews WHERE answerID = ? AND isHidden = FALSE";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			pstmt.setInt(1, answerID);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int questionID = rs.getInt("questionID");
				int prevID = rs.getInt("prevReviewID");
				int reviewID = rs.getInt("reviewID");
				String reviewBody = rs.getString("reviewBody");
				String reviewerUserName = rs.getString("reviewerUserName");
				String reviewerFirstName = rs.getString("reviewerFirstName");
				String reviewerLastName = rs.getString("reviewerLastName");
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reasonIsFlagged = rs.getString("reasonIsFlagged");
				
				Review reviewObject = new Review(questionID, answerID, prevID, reviewID, reviewBody, reviewerUserName, reviewerFirstName, reviewerLastName, isFlagged, reasonIsFlagged);
				
				reviews.add(reviewObject);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return reviews;
	}
	
	/**
	 * Retrieves and returns a Review object from the database for a specified reviewID.
	 * 
	 * @param ID the reviewID associated with the review
	 * @return a Review object containing all attributes of the specified reviewID
	 */
	public Review getReviewByID(int ID){
		Review review = null;
		String sqlQuery = "SELECT questionID, answerID, prevReviewID, reviewID, reviewBody, reviewerUserName, reviewerFirstName, reviewerLastName FROM reviews WHERE reviewID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			pstmt.setInt(1, ID);
			ResultSet rs = pstmt.executeQuery();
			int questionID = rs.getInt("questionID");
			int answerID = rs.getInt("answerID");
			int prevID = rs.getInt("prevReviewID");
			int reviewID = rs.getInt("reviewID");
			String reviewBody = rs.getString("reviewBody");
			String reviewerUserName = rs.getString("reviewerUserName");
			String reviewerFirstName = rs.getString("reviewerFirstName");
			String reviewerLastName = rs.getString("reviewerLastName");
				
			review = new Review(questionID, answerID, prevID, reviewID, reviewBody, reviewerUserName, reviewerFirstName, reviewerLastName);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
			return review;
	}
	
	/**
	 * Deletes a review from the database.
	 * 
	 * @param reviewID the specified reviewID to delete
	 */
	public void deleteReview(int reviewID) {
		String sqlDelete = "DELETE FROM reviews WHERE reviewID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlDelete)) {
			pstmt.setInt(1, reviewID);
			pstmt.executeUpdate();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves all the answerIDs for answers tied to a specific questionID.
	 * 
	 * @param questionID the questionID for a specific question
	 * @return the an ArrayList for all answers associated with the specified questionID
	 */
	public ArrayList<Integer> getAnswerIDsForQuestion(int questionID) {
		ArrayList<Integer> answerIDs = new ArrayList<>();
		String sqlQuery = "SELECT answerID FROM answers WHERE questionID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			pstmt.setInt(1, questionID);
			try (ResultSet rs = pstmt.executeQuery()) {
				while(rs.next()) {
					answerIDs.add(rs.getInt("answerID"));
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return answerIDs;
	}
	
	/**
	 * Marks a Reviewer private message as read.
	 * 
	 * @param messageID the messageID associated with a specific private message
	 * @param message the ReviewerMessage object for the specified message
	 */
	public void markReviewerMessageAsRead(int messageID, ReviewerMessage message) {
	    String query = "UPDATE reviewerMessages SET isRead = TRUE WHERE messageID = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, messageID);
	        int rowsAffected = pstmt.executeUpdate();
	        connection.commit();
	        if (rowsAffected > 0) {
	        	message.setRead(true);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	/**
	 * Adds a reviewer private message to the database.
	 * 
	 * @param message the ReviewerMessage object containing all the attributes of the message
	 * @return the messageID for the reviewer private message generated by the database
	 */
	public int saveReviewerMessage(ReviewerMessage message) {
	    String sql = "INSERT INTO reviewerMessages (sender, recipient, recipientRole, subject, body, sentTime, isRead, reviewID, isFlagged, reasonIsFlagged) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	    int messageIDGenerated = -1;

	    try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	        pstmt.setString(1, message.getSender());
	        pstmt.setString(2, message.getRecipient());
	        pstmt.setString(3, message.getRecipientRole());
	        pstmt.setString(4, message.getSubject());
	        pstmt.setString(5, message.getBody());
	        pstmt.setTimestamp(6, Timestamp.valueOf(message.getSentTime()));
	        pstmt.setBoolean(7, false);
	        pstmt.setInt(8, message.getReviewID());
	        pstmt.setBoolean(9, false);
	        pstmt.setString(10, "");
	        
	        pstmt.executeUpdate();

	        try (ResultSet rs = pstmt.getGeneratedKeys()) {
	            if (rs.next()) {
	                messageIDGenerated = rs.getInt(1);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return messageIDGenerated;
	}

	
	/**
	 * Deletes a reviewer private message from the database.
	 * 
	 * @param messageID the messageID for a specific reviewer private message
	 */
	public void deleteReviewerMessage(int messageID) {
		String sqlDelete = "DELETE FROM reviewerMessages WHERE messageID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlDelete)) {
			pstmt.setInt(1, messageID);
			pstmt.executeUpdate();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieves all the reviewer private messages associated with a specified reviewID.
	 * 
	 * @param reviewID the reviewID for a specific review
	 * @return an ObservableList containing all the reviewer private messages associated with the specified reviewID
	 */
	public ObservableList<ReviewerMessage> getReviewerMessagesForReview(int reviewID) {
	    ObservableList<ReviewerMessage> messageList = FXCollections.observableArrayList();
	    String query = "SELECT messageID, sender, recipient, recipientRole, subject, body, sentTime, isRead FROM reviewerMessages WHERE reviewID = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, reviewID);
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            int messageID = rs.getInt("messageID");
	            String sender = rs.getString("sender");
	            String recipient = rs.getString("recipient");
	            String recipientRole = rs.getString("recipientRole");
	            String subject = rs.getString("subject");
	            String body = rs.getString("body");
	            Timestamp sentTimeStamp = rs.getTimestamp("sentTime");
	            LocalDateTime sentTime = sentTimeStamp.toLocalDateTime();
	            boolean isRead = rs.getBoolean("isRead");
	            ReviewerMessage msg = new ReviewerMessage(messageID, sender, recipient, recipientRole, subject, body, sentTime, isRead, reviewID);
	            messageList.add(msg);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return messageList;
	}

	
	/**
	 * Retrieves all the reviewer private messages for a specified reviewer userName.
	 * 
	 * @param recipient the userName of the specified reviewer
	 * @return an ObservableList containing all the reviewer private messages sent to the specified recipient userName
	 */
	public ObservableList<ReviewerMessage> getReviewerMessagesForUser(String recipient) {
	    ObservableList<ReviewerMessage> messageList = FXCollections.observableArrayList();
	    String query = "SELECT messageID, sender, recipient, recipientRole, subject, body, sentTime, isRead FROM reviewerMessages WHERE recipient = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, recipient);
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            int messageID = rs.getInt("messageID");
	            String sender = rs.getString("sender");
	            String rec = rs.getString("recipient");
	            String recipientRole = rs.getString("recipientRole");  
	            String subject = rs.getString("subject");
	            String body = rs.getString("body");
	            Timestamp sentTimeStamp = rs.getTimestamp("sentTime");
	            LocalDateTime sentTime = sentTimeStamp.toLocalDateTime();
	            boolean isRead = rs.getBoolean("isRead");

	            ReviewerMessage msg = 
	                new ReviewerMessage(
	                    messageID,
	                    sender,
	                    rec,
	                    recipientRole,
	                    subject,
	                    body,
	                    sentTime,
	                    isRead, 
	                    -1
	                );
	            messageList.add(msg);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return messageList;
	}
	

	/**
	 * Retrieves the count of all unread reviewer private messages from the database for a specified review and reviewer userName.
	 * 
	 * @param recipient the reviewer userName who received the private messages from sender Student
	 * @param reviewID the reviewID for a specific review
	 * @return the count of all reviewer private messages for a specified reviewID, a specified reviewer userName(recipient), and where the database 
	 * attribute isRead is false
	 */
	public int countUnreadReviewerPrivateMessages(String recipient, int reviewID) {
		int unreadCount = 0;
		String sqlQuery = "SELECT COUNT(*) AS unreadCount FROM reviewerMessages WHERE recipient = ? AND reviewID = ? AND isRead = FALSE";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			pstmt.setString(1, recipient);
			pstmt.setInt(2, reviewID);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				unreadCount = rs.getInt("unreadCount");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return unreadCount;
	}
	
	/**
	 * Retrieves all answers linked to questions submitted by the given user.
	 *
	 * @param userName The username of the student who submitted the questions.
	 * @return A list of answers related to the user's questions.
	 */
	public ArrayList<Answer> getAnswersForUserQuestions(String userName) {
	    ArrayList<Answer> answers = new ArrayList<>();
	    String sql = "SELECT a.* FROM Answers a " +
                "JOIN Questions q ON a.questionID = q.questionID " +
                "WHERE q.studentUserName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            Answer answer = new Answer();
	            answer.setAnswerID(rs.getInt("answerID"));
	            answer.setStudentUserName(rs.getString("studentUserName"));
	            answer.setStudentFirstName(rs.getString("studentFirstName"));
	            answer.setStudentLastName(rs.getString("studentLastName"));
	            answer.setQuestionID(rs.getInt("questionID"));
	            answer.setAnswerText(rs.getString("answerText"));
	            answer.setTimestamp(rs.getTimestamp("creationTime").toLocalDateTime());
	            // Add other fields from your Answer model as needed

	            answers.add(answer);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return answers;
	}
	
	/**
	 * Retrieves questions submitted by a specific user.
	 * @param userName The username of the student.
	 * @return A list of submitted questions.
	 */
	public ArrayList<Question> getSubmittedQuestionsByUser(String userName) {
	    ArrayList<Question> questions = new ArrayList<>();
	    String sql = "SELECT * FROM Questions WHERE studentUserName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)){
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            Question question = new Question();
	            question.setStudentUserName(rs.getString("studentUserName"));
	            question.setStudentFirstName(rs.getString("studentFirstName"));
	            question.setStudentLastName(rs.getString("studentLastName"));
	            question.setQuestionTitle(rs.getString("questionTitle"));
	            question.setQuestionBody(rs.getString("questionBody"));
	            question.setQuestionID(rs.getInt("questionID"));
	            questions.add(question);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return questions;
	}
	
	/**
	 * Retrieves reviews associated with potential answers to questions submitted by a specific user.
	 * @param userName The username of the student.
	 * @return A list of submitted reviews.
	 */
	public ArrayList<Review> getReviewsForUserAnswers(String userName) {
		ArrayList<Review> reviews = new ArrayList<>();
		String sql = "SELECT * FROM Reviews WHERE questionID IN (SELECT questionID FROM questions WHERE studentUserName = ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)){
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            Review review = new Review();
	            review.setReviewBody(rs.getString("reviewBody"));
	            review.setAnswerID(rs.getInt("answerID"));
	            review.setReviewerUserName(rs.getString("reviewerUserName"));
	            review.setQuestionID(rs.getInt("questionID"));
	            review.setReviewerFirstName(rs.getString("reviewerFirstName"));
	            review.setReviewerLastName(rs.getString("reviewerLastName"));
	            review.setReviewID(rs.getInt("reviewID"));
	            reviews.add(review);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return reviews;
	}
	
	/**
	 * Retrieves the userName of the Reviewer who submitted a review for the specified answerID.
	 * @param answerID The answerID associated with the review we are messaging the reviewer about.
	 * @return the userName of the reviewer.
	 */
	public String getReviewerUserNameByAnswerID(int answerID) {
		String reviewerUserName = "";
		String sqlQuery = "SELECT reviewerUserName FROM reviews WHERE answerID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			pstmt.setInt(1, answerID);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				reviewerUserName = rs.getString("reviewerUserName");
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return reviewerUserName;
	}
	
	
	/**
	 * Retrieves all private messages received by a specific student user.
	 *
	 * @param studentUserName The username of the student.
	 * @return A list of message strings formatted with recipient, subject, date, and message contents.
	 */
	public ArrayList<String> getPrivateMessagesForStudent(String studentUserName) {
	    ArrayList<String> messages = new ArrayList<>();
	    DateTimeFormatter timeMessageSent = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
	    
	    // Retrieve messages sent from another Student to this Student
	    String query1 = "SELECT p.sender_user_name, p.receiver_user_name, p.subject, p.message_body, p.timestamp FROM PrivateMessages p WHERE p.receiver_user_name = ?";
	    
	    // Retrieve messages sent from Reviewers to this Student 
		String query2 = "SELECT r.sender, r.recipient, r.subject, r.body, r.sentTime FROM reviewerMessages r WHERE r.recipient = ?";
	    
		try {
			try (PreparedStatement pstmt1 = connection.prepareStatement(query1)) {
				pstmt1.setString(1, studentUserName);
				ResultSet rs = pstmt1.executeQuery();
		        while (rs.next()) {
		        	String senderUserName = rs.getString("sender_user_name");
		        	String receiverUserName = rs.getString("receiver_user_name");
		        	String messageSubject = rs.getString("subject");
		        	String messageBody = rs.getString("message_body");
		        	Timestamp timeSent = rs.getTimestamp("timestamp");
		        	String formattedTime = timeSent.toLocalDateTime().format(timeMessageSent);
		        	
		        	messages.add("To [Student]: " + receiverUserName + "\n" + "From [Student]: " + senderUserName + "\n" + "Date: " + formattedTime + "\n" + "Message Subject: " + messageSubject + "\n" + "Message Body: " + messageBody);
		        }
			}
			
			try (PreparedStatement pstmt2 = connection.prepareStatement(query2)) {
				pstmt2.setString(1, studentUserName);
				ResultSet rs2 = pstmt2.executeQuery();
				while (rs2.next()) {
					String reviewer2UserName = rs2.getString("sender");
					String student2UserName = rs2.getString("recipient");
					String messageSubject2 = rs2.getString("subject");
					String messageBody2 = rs2.getString("body");
					Timestamp timeSent2 = rs2.getTimestamp("sentTime");
					String formattedTime2 = timeSent2.toLocalDateTime().format(timeMessageSent);
					
					messages.add("To [Student]: " + student2UserName + "\n" + "From [Reviewer]: " + reviewer2UserName + "\n" + "Date: " + formattedTime2 + "\n" + "Message Subject: " + messageSubject2 + "\n" + "Message Body: " + messageBody2);
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	    return messages;
	}
	
	/**
	 * Sends a private message from a student to a reviewer.
	 *
	 * @param sender   The sender's username.
	 * @param receiver The recipient's username.
	 * @param subject          The message subject.
	 * @param body      The content of the message.
	 * @param replyToMessageID       The unique message ID (for replies).
	 * @return True if the message was sent successfully, false otherwise.
	 */
	public boolean sendPrivateMessage(String sender, String receiver, String subject, String body, String replyToMessageID) {
	    String sql = "INSERT INTO PrivateMessages (sender_user_name, receiver_user_name, subject, message_body, is_read, timestamp, isFlagged, reasonIsFlagged, questionID, reviewID) VALUES (?, ?, ?, ?, FALSE, ?, ?, ?, ?, ?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, sender);
	        pstmt.setString(2, receiver);
	        pstmt.setString(3, subject);
	        pstmt.setString(4, body);
	        pstmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
	        pstmt.setBoolean(6, false);
	        pstmt.setString(7, "");
	        pstmt.setInt(8, -1);
	        pstmt.setInt(9, -1);

	        pstmt.executeUpdate();
	        return true;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	/**
	 * Deletes a private message by its message ID.
	 *
	 * @param messageID The ID of the message to delete.
	 */
	public void deletePrivateMessage(String messageID) {
	    String sql = "DELETE FROM PrivateMessages WHERE messageID = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, messageID);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Adds a trusted reviewer to the trustedReviewers table in the database.
	 * 
	 * @param user the User object for the current user
	 * @param weight the weight assigned by the current user as an integer value
	 * @param reviewerUserName the userName of the reviewer being added as a trusted reviewer
	 */
	public void addTrustedReviewer(User user, int weight, String reviewerUserName) {
		String sqlQuery = "INSERT INTO trustedReviewers (studentUserName, reviewerUserName, weight) VALUES(?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2,  reviewerUserName);
			pstmt.setInt(3, weight);
			
			pstmt.executeUpdate();
		} catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Delete a trusted reviewer from the database.
	 * 
	 * @param user the User object for the current user
	 * @param reviewerUserName the userName of the reviewer being removed as a trusted reviewer
	 */
	public void removeTrustedReviewer(User user, String reviewerUserName) {
		String sqlQuery = "DELETE FROM trustedReviewers WHERE studentUserName = ? AND reviewerUserName = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, reviewerUserName);
			
			pstmt.executeUpdate();		
		} catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Assigns a weight to a trusted reviewer in the trustedReviewers table in the database.
	 * 
	 * @param user the User object for the current user
	 * @param weight the weight to assign as an integer value
	 * @param reviewerUserName the userName of the reviewer being assigned a weight
	 */
	public void assignTrustedReviewerWeight(User user, int weight, String reviewerUserName) {
		String sqlQuery = "UPDATE trustedReviewers SET weight = ? WHERE studentUserName = ? AND reviewerUserName = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			pstmt.setInt(1, weight);
			pstmt.setString(2, user.getUserName());
			pstmt.setString(3, reviewerUserName);
			
			pstmt.executeUpdate();
		} catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Retrieves all trusted reviewers from the database for a specified student userName.
	 * 
	 * @param user the User object for the current user
	 * @return an ArrayList of all trusted reviewers for a specified student userName
	 */
	public ArrayList<String> getTrustedReviewers(User user) {
		ArrayList<String> reviewers = new ArrayList<String>();		
		String sqlQuery = "SELECT reviewerUserName, weight FROM trustedReviewers WHERE studentUserName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			pstmt.setString(1, user.getUserName());
			
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()) {
				String reviewerUserName = rs.getString("reviewerUserName");
				int weight = rs.getInt("weight");
				
				String reviewer = reviewerUserName + " " + weight;
				
				reviewers.add(reviewer);
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return reviewers;
	}
	
	/**
	 * Checks if the specified reviewer userName exists in the trustedReviewers table in the database.
	 * 
	 * @param user the User object for the current user
	 * @param reviewerUserName the userName for a specific reviewer
	 * @return true or false based on whether the revieverUserName exists in the trustedReviewers table
	 */
	public boolean doesReviewerExist(User user, String reviewerUserName) {
		String query = "SELECT COUNT(*) FROM trustedReviewers WHERE reviewerUserName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, reviewerUserName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}
	
	/**
	 *  Retrieve all reviewer role requests (pending or otherwise).
	 *  Adjust the WHERE clause if you only want pending requests.
	 *  
	 * @return an ObservableList of all the role requests for an instructor role
	 */ 
	public ObservableList<InstructorReviewerRequests.NewRoleRequest> getReviewerRequests() {
	  ObservableList<InstructorReviewerRequests.NewRoleRequest> requestsList = FXCollections.observableArrayList();
	
	  String query = "SELECT roleRequestID, userName, requestStatus "
	               + "FROM newRoleRequests";
	  // If you only want to display pending requests, you can do:
	  // "FROM newRoleRequests WHERE requestStatus = 'Pending'";
	
	  try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	      ResultSet rs = pstmt.executeQuery();
	      while (rs.next()) {
	          int roleRequestID = rs.getInt("roleRequestID");
	          String userName    = rs.getString("userName");
	          String requestStatus = rs.getString("requestStatus");
	
	          // Instantiate your NewRoleRequest model object
	          InstructorReviewerRequests.NewRoleRequest newRequest =
	                  new InstructorReviewerRequests.NewRoleRequest(
	                          roleRequestID,
	                          userName,
	                          requestStatus
	                  );
	          requestsList.add(newRequest);
	      }
	  } catch (SQLException e) {
	      e.printStackTrace();
	  }
	  return requestsList;
	}
	
	/**
	 * Approve the role request (set isRequestApproved = TRUE, requestStatus = 'Approved').
	 * 
	 * @param roleRequestID the roleRequestID associated with a specific role request
	 */
	public void approveRoleRequest(int roleRequestID) {
	  String query = "UPDATE newRoleRequests "
	               + "SET isRequestApproved = TRUE, requestStatus = 'Approved' "
	               + "WHERE roleRequestID = ?";
	  try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	      pstmt.setInt(1, roleRequestID);
	      pstmt.executeUpdate();
	  } catch (SQLException e) {
	      e.printStackTrace();
	  }
	}
	
	/**
	 * Deny the role request (set isRequestApproved = FALSE, requestStatus = 'Denied').
	 * 
	 * @param roleRequestID the roleRequestID associated with a specific role request
	 */
	public void denyRoleRequest(int roleRequestID) {
	  String query = "UPDATE newRoleRequests "
	               + "SET isRequestApproved = FALSE, requestStatus = 'Denied' "
	               + "WHERE roleRequestID = ?";
	  try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	      pstmt.setInt(1, roleRequestID);
	      pstmt.executeUpdate();
	  } catch (SQLException e) {
	      e.printStackTrace();
	  }
	}
	
	/**
	 * Retrieve the userName, roles, and email specific to a specified users first and last name which is passed as an argument as a single string.
	 * 
	 * @param fullName the fullName of the user
	 * @return a User object containing the attributes associated with the specified name 
	 */
	public User getUserInfoByName(String fullName) {
		User recipientUser = null;
		String[] fullNameParts = fullName.split(" ");
		String firstName = fullNameParts[0];
		if (fullNameParts.length == 1) {
			recipientUser = null;
			return recipientUser;
		}
		String lastName = fullNameParts[1];
		String query = "SELECT userName, role, email FROM cse360users WHERE firstName = ? AND lastName = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, firstName);
			pstmt.setString(2, lastName);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				String userName = rs.getString("userName");
				boolean[] role = getUserRole(userName);
				String email = rs.getString("email");
				recipientUser = new User(userName, role, email, firstName, lastName);
			}
		}
		catch (SQLException e) {
		     e.printStackTrace();
		}
		return recipientUser;
	}
	
	/**
	 * Retrieves all registered users who have been provisioned a Staff or Instructor role excluding the current Staff user
	 * 
	 * @param user the User object specific to the current user
	 * @return an ArrayList containing all the registered users who have been provisioned Staff and Instructor roles
	 */
	public ArrayList<User> getStaffAndInstructorUsers(User user) {
		String currUserName = user.getUserName();
		String userRole = "";
		ArrayList<User> staffAndInstructorUsers = new ArrayList<>();
		String query = "SELECT userName, firstName, lastName, email FROM cse360users";
		try(PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				userRole = "";
				String userName = rs.getString("userName");
				String firstName = rs.getString("firstName");
				String lastName = rs.getString("lastName");
				String email = rs.getString("email");
				boolean[] roles = getUserRole(userName);
				
				if (roles[3] == true) {
					userRole = "Staff";
				}
				else if (roles[4] == true) {
					userRole = "Instructor";
				}
				
				// Only users with Instructor and Staff roles and excluding the currently logged in Staff user
				if (userRole.equals("Staff") || userRole.equals("Instructor")) {
					if (!currUserName.equals(userName)) {
						User instructorOrStaff = new User(userName, roles, email, firstName, lastName);
						staffAndInstructorUsers.add(instructorOrStaff);
					}
				}
			}
		}
		catch (SQLException e) {
	      e.printStackTrace();
		}
		return staffAndInstructorUsers;
	}
	
	/**
	 * Adds a message being sent from the current Staff user to another user who holds either the Staff or Instructor role to the staffMessages table 
	 * in the database.
	 * 
	 * @param message the StaffMessage object containing the message details to insert into the database
	 * @return the messageID generated for the newly added message
	 */
	public int addStaffPrivateMessage(StaffMessage message) {
		String query = "INSERT INTO staffMessages (senderEmail, senderUserName, senderFirstName, senderLastName, senderRole, messageSubject, messageBody, isMessageRead, recipientEmail, recipientUserName, recipientFirstName, recipientLastName, recipientRole, timeSent) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		int staffPrivateMessageIDGenerated = -1;
		try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, message.getSenderEmail());
			pstmt.setString(2, message.getSenderUserName());
			pstmt.setString(3, message.getSenderFirstName());
			pstmt.setString(4, message.getSenderLastName());
			pstmt.setString(5, message.getSenderRole());
			pstmt.setString(6, message.getMessageSubject());
			pstmt.setString(7, message.getMessageBody());
			pstmt.setBoolean(8, message.getIsMessageRead());
			pstmt.setString(9, message.getRecipientEmail());
			pstmt.setString(10, message.getRecipientUserName());
			pstmt.setString(11, message.getRecipientFirstName());
			pstmt.setString(12, message.getRecipientLastName());
			pstmt.setString(13, message.getRecipientRole());
			pstmt.setTimestamp(14, Timestamp.valueOf(message.getTimeSent()));
			pstmt.executeUpdate();
			try (ResultSet rs = pstmt.getGeneratedKeys()) { // retrieve primary key "messageID" generated by INSERT above
				if (rs.next()) {
					staffPrivateMessageIDGenerated = rs.getInt(1);
					message.setMessageID(staffPrivateMessageIDGenerated);
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return staffPrivateMessageIDGenerated;		
	}
	
	/**
	 * Deletes a message received by the current Staff member from another user who holds the Staff or Instructor role from the database.
	 * 
	 * @param staffPrivateMessageID the messageID to be deleted
	 */
	public void deleteStaffPrivateMessage(int staffPrivateMessageID) {
		String query = "DELETE FROM staffMessages WHERE messageID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, staffPrivateMessageID);
			pstmt.executeUpdate();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieves all the messages sent to the current Staff user.
	 * 
	 * @param userName the userName of the current Staff user
	 * @return an ArrayList containing all the messages where the recipientUserName is the userName of the current Staff user
	 */
	public ArrayList<StaffMessage> getAllReceivedStaffPrivateMessages(String userName) {
		ArrayList<StaffMessage> allStaffPrivateMessages = new ArrayList<>();
		String query = "SELECT messageID, senderFirstName, senderLastName, senderEmail, senderUserName, senderRole, messageSubject, messageBody, isMessageRead, timeSent FROM staffMessages WHERE recipientUserName = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setString(1, userName);
		        ResultSet rs = pstmt.executeQuery();
		        while (rs.next()) {
		        	int messageID = rs.getInt("messageID");
		        	String senderFirstName = rs.getString("senderFirstName");
		        	String senderLastName = rs.getString("senderLastName");
		        	String senderEmail = rs.getString("senderEmail");
		        	String senderUserName  = rs.getString("senderUserName");
		        	String senderRole = rs.getString("senderRole");
		        	String messageSubject = rs.getString("messageSubject");
		        	String messageBody = rs.getString("messageBody");
		        	boolean isMessageRead = rs.getBoolean("isMessageRead");
		        	Timestamp sentTime = rs.getTimestamp("timeSent");
		        	
		        	StaffMessage staffMessagesObj = new StaffMessage(messageID, senderUserName, senderFirstName, senderLastName, senderEmail, senderRole, messageSubject, messageBody, isMessageRead, sentTime.toLocalDateTime());
		        	allStaffPrivateMessages.add(staffMessagesObj);
		        }
		    } 
			catch (SQLException e) {
		        e.printStackTrace();
		    }
		return allStaffPrivateMessages;
	}
	
	/**
	 * Retrieves all the messages sent by the current Staff user.
	 *
	 * @param userName the userName of the current Staff user
	 * @return an ArrayList containing all of the messages where the senderUserName is the userName of the current Staff user
	 */
	public ArrayList<StaffMessage> getAllPrivateMessagesSentFromStaff(String userName) {
		ArrayList<StaffMessage> allMessagesSentStaff = new ArrayList<>();
		String query = "SELECT messageID, recipientFirstName, recipientLastName, recipientEmail, recipientUserName, recipientRole, messageSubject, messageBody, isMessageRead, timeSent FROM staffMessages where senderUserName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        while(rs.next()) {
	        	int messageID = rs.getInt("messageID");
	        	String recipientFirstName = rs.getString("recipientFirstName");
	        	String recipientLastName = rs.getString("recipientLastName");
	        	String recipientEmail = rs.getString("recipientEmail");
	        	String recipientUserName = rs.getString("recipientUserName");
	        	String recipientRole = rs.getString("recipientRole");
	        	String messageSubject = rs.getString("messageSubject");
	        	String messageBody = rs.getString("messageBody");
	        	boolean isMessageRead = rs.getBoolean("isMessageRead");
	        	Timestamp timeSent = rs.getTimestamp("timeSent");

	        	StaffMessage staffMessagesObj = new StaffMessage(messageID, timeSent.toLocalDateTime(), recipientFirstName, recipientLastName, recipientEmail, recipientUserName, recipientRole, messageSubject, messageBody, isMessageRead);
	        	allMessagesSentStaff.add(staffMessagesObj);
	        }
		}
		catch (SQLException e) {
	        e.printStackTrace();
	    }
		return allMessagesSentStaff;
	}
	
	/**
	 * Retrieves all messages sent to the current Staff user by a specified user.
	 * 
	 * @param sender the userName of the user who sent the messages to currStaffUser
	 * @param currStaffUser the current StaffUser who received messages from sender
	 * @return an ArrayList containing all of the messages sent to the current Staff user by the specified users' userName.
	 */
	public ArrayList<StaffMessage> getMessagesSentToStaffByUser(User sender, User currStaffUser) {
		ArrayList<StaffMessage> messagesReceivedBySpecifiedUser = new ArrayList<>();
		String query = "SELECT messageID, senderFirstName, senderLastName, senderEmail, senderUserName, senderRole, messageSubject, messageBody, isMessageRead, timeSent FROM staffMessages WHERE senderUserName = ? AND recipientUserName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, sender.getUserName());
	        pstmt.setString(2, currStaffUser.getUserName());
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	        	int messageID = rs.getInt("messageID");
	        	String senderFirstName = rs.getString("senderFirstName");
	        	String senderLastName = rs.getString("senderLastName");
	        	String senderEmail = rs.getString("senderEmail");
	        	String senderUserName  = rs.getString("senderUserName");
	        	String senderRole = rs.getString("senderRole");
	        	String messageSubject = rs.getString("messageSubject");
	        	String messageBody = rs.getString("messageBody");
	        	boolean isMessageRead = rs.getBoolean("isMessageRead");
	        	Timestamp sentTime = rs.getTimestamp("timeSent");
	        	
	        	StaffMessage staffMessagesObj = new StaffMessage(messageID, senderUserName, senderFirstName, senderLastName, senderEmail, senderRole, messageSubject, messageBody, isMessageRead, sentTime.toLocalDateTime());
	        	messagesReceivedBySpecifiedUser.add(staffMessagesObj);
	        }
	    } 
		catch (SQLException e) {
	        e.printStackTrace();
	    }
		return messagesReceivedBySpecifiedUser;
	}
	
	/**
	 * Retrieves all the messages sent by the current Staff user to a specified user.
	 * 
	 * @param recipient the userName of the user who received messages from currStaffUser
	 * @param currStaffUser the current StaffUser who sent messages to sender
	 * @return an ArrayList containing all of the messages sent by the current Staff user to the specified user
	 */
	public ArrayList<StaffMessage> getMessagesSentByStaffToUser(User recipient, User currStaffUser) {
		ArrayList<StaffMessage> messagesSentByStaffToSpecifiedUser = new ArrayList<>();
		String query = "SELECT messageID, recipientFirstName, recipientLastName, recipientEmail, recipientUserName, recipientRole, messageSubject, messageBody, isMessageRead, timeSent FROM staffMessages where recipientUserName = ? AND senderUserName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, recipient.getUserName());
			pstmt.setString(2, currStaffUser.getUserName());
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	        	int messageID = rs.getInt("messageID");
	        	Timestamp timeSent = rs.getTimestamp("timeSent");
	        	String recipientFirstName = rs.getString("recipientFirstName");
	        	String recipientLastName = rs.getString("recipientLastName");
	        	String recipientEmail = rs.getString("recipientEmail");
	        	String recipientUserName = rs.getString("recipientUserName");
	        	String recipientRole = rs.getString("recipientRole");
	        	String messageSubject = rs.getString("messageSubject");
	        	String messageBody = rs.getString("messageBody");
	        	boolean isMessageRead = rs.getBoolean("isMessageRead");
	        	
	        	StaffMessage staffMessagesObj = new StaffMessage(messageID, timeSent.toLocalDateTime(), recipientFirstName, recipientLastName, recipientEmail, recipientUserName, recipientRole, messageSubject, messageBody, isMessageRead);
	        	messagesSentByStaffToSpecifiedUser.add(staffMessagesObj);
	        }
		}
		catch (SQLException e) {
	        e.printStackTrace();
	    }
		return messagesSentByStaffToSpecifiedUser;
	}
	
	/**
	 * Retrieves the count of all messages received by the current Staff user which are marked as having been unread.
	 * 
	 * @param user the User object for the current Staff user
	 * @return the count of unread messages received by the current Staff user
	 */
	public int countUnreadStaffPrivateMessages(User user) {
		int unreadCount = 0;
		String userName = user.getUserName();
		String query = "SELECT COUNT(*) AS unreadCount FROM staffMessages WHERE recipientUserName = ? AND isMessageRead = FALSE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                unreadCount = rs.getInt("unreadCount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return unreadCount;
	}
	
	/**
	 * Retrieves all the private feedback messages sent between two Student users.
	 * 
	 * @return an ArrayList containing all of the messages sent between two Student users
	 */
	public ArrayList<String> getAllMessagesBetweenStudents() {
		ArrayList<String> allStudentToStudentMessages = new ArrayList<>();
		DateTimeFormatter timeMessageSent = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
		String query = "SELECT p.sender_user_name, p.receiver_user_name, p.subject, p.message_body, p.timestamp FROM PrivateMessages p WHERE p.receiver_user_name IN (SELECT c.userName FROM cse360users c WHERE p.receiver_user_name = c.userName AND role LIKE '[true, true%' OR role LIKE '[false, true%')";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	        	String senderUserName = rs.getString("sender_user_name");
	        	String receiverUserName = rs.getString("receiver_user_name");
	        	String messageSubject = rs.getString("subject");
	        	String messageBody = rs.getString("message_body");
	        	Timestamp timeSent = rs.getTimestamp("timestamp");
	        	String formattedTime = timeSent.toLocalDateTime().format(timeMessageSent);
	        	
	        	allStudentToStudentMessages.add("To [Student]: " + receiverUserName + "\n" + "From [Student]: " + senderUserName + "\n" + "Date: " + formattedTime + "\n" + "Message Subject: " + messageSubject + "\n" + "Message Body: " + messageBody);
	        }
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return allStudentToStudentMessages;
	}
	
	/**
	 * Retrieves all the private feedback messages sent between a Student user and a Reviewer user.
	 * 
	 * @return an ArrayList containing all of the messages sent between a Student user and a Reviewer user
	 */
	public ArrayList<String> getAllMessagesBetweenReviewersAndStudents() {
		ArrayList<String> allReviewerToStudentMessages = new ArrayList<>();
		DateTimeFormatter timeMessageSent = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
		
		// Retrieve messages sent from Students to Reviewers
		String query1 = "SELECT p.sender_user_name, p.receiver_user_name, p.subject, p.message_body, p.timestamp FROM PrivateMessages p WHERE p.receiver_user_name IN (SELECT c.userName FROM cse360users c WHERE p.receiver_user_name = c.userName AND role LIKE '[false, false, true%' OR role LIKE '[false, true, true%' OR role LIKE '[true, true, true%')";
		
		// Retrieve messages sent from Reviewers to Students
		String query2 = "SELECT r.sender, r.recipient, r.subject, r.body, r.sentTime FROM reviewerMessages r WHERE r.recipient IN (SELECT c.userName FROM cse360users c WHERE r.recipient = c.userName AND role LIKE '[true, true%' OR role LIKE '[false, true%')";
		
		try {
			try (PreparedStatement pstmt1 = connection.prepareStatement(query1)) {
				ResultSet rs1 = pstmt1.executeQuery();
				while (rs1.next()) {
					String student1UserName = rs1.getString("sender_user_name");
		        	String reviewer1UserName = rs1.getString("receiver_user_name");
		        	String messageSubject1 = rs1.getString("subject");
		        	String messageBody1 = rs1.getString("message_body");
		        	Timestamp timeSent1 = rs1.getTimestamp("timestamp");
		        	String formattedTime1 = timeSent1.toLocalDateTime().format(timeMessageSent);
		        	
		        	allReviewerToStudentMessages.add("To [Reviewer]: " + reviewer1UserName + "\n" + "From [Student]: " + student1UserName + "\n" +  "Date: " + formattedTime1 + "\n" + "Message Subject: " + messageSubject1 + "\n" + "Message Body: " + messageBody1);
				}
			}
			
			try (PreparedStatement pstmt2 = connection.prepareStatement(query2)) {
				ResultSet rs2 = pstmt2.executeQuery();
				while (rs2.next()) {
					String reviewer2UserName = rs2.getString("sender");
					String student2UserName = rs2.getString("recipient");
					String messageSubject2 = rs2.getString("subject");
					String messageBody2 = rs2.getString("body");
					Timestamp timeSent2 = rs2.getTimestamp("sentTime");
					String formattedTime2 = timeSent2.toLocalDateTime().format(timeMessageSent);
					
					allReviewerToStudentMessages.add("To [Student]: " + student2UserName + "\n" + "From [Reviewer]: " + reviewer2UserName + "\n" + "Date: " + formattedTime2 + "\n" + "Message Subject: " + messageSubject2 + "\n" + "Message Body: " + messageBody2);
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return allReviewerToStudentMessages;
	}
	
	/**
	 * Marks a Staff private message as read.
	 * 
	 * @param message message object to mark isMessageRead attribute as true
	 */
	public void markStaffMessageAsRead(StaffMessage message) {
		String query = "UPDATE staffMessages SET isMessageRead = TRUE WHERE messageID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, message.getMessageID());
			int rowsAffected = pstmt.executeUpdate();
			connection.commit();
			if (rowsAffected > 0) {
				message.setIsMessageRead(true);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}	
	
	/**
	 * Marks a Question as flagged by a Staff user.
	 * 
	 * @param question question object to mark isFlagged attribute as true
	 */
	public void markQuestionFlagged(Question question) {
		String query = "UPDATE questions SET isFlagged = TRUE, reasonIsFlagged = ? where questionID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, question.getReasonIsFlagged());
			pstmt.setInt(2, question.getQuestionID());
			int rowsAffected = pstmt.executeUpdate();
			connection.commit();
			if (rowsAffected > 0) {
				question.setIsFlagged(true);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Marks a Question Reply as flagged by a Staff user.
	 * 
	 * @param reply reply object to mark isFlagged attribute as true
	 */
	public void markQuestionReplyFlagged(Question reply) {
		String query = "UPDATE questionReplies SET isFlagged = TRUE, reasonIsFlagged = ? where replyID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, reply.getReasonIsFlagged());
			pstmt.setInt(2, reply.getReplyID());
			int rowsAffected = pstmt.executeUpdate();
			connection.commit();
			if (rowsAffected > 0) {
				reply.setIsFlagged(true);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Marks an Answer as flagged by a Staff user.
	 * 
	 * @param answer answer object to mark isFlagged attribute as true
	 */
	public void markAnswerFlagged(Answer answer) {
		String query = "UPDATE answers SET isFlagged = TRUE, reasonIsFlagged = ? where answerID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, answer.getReasonIsFlagged());
			pstmt.setInt(2, answer.getAnswerID());
			int rowsAffected = pstmt.executeUpdate();
			connection.commit();
			if (rowsAffected > 0) {
				answer.setIsFlagged(true);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Marks a Review as flagged by a Staff user.
	 * 
	 * @param review review object to mark isFlagged attribute as true
	 */
	public void markReviewFlagged(Review review) {
		String query = "UPDATE reviews SET isFlagged = TRUE, reasonIsFlagged = ? where reviewID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, review.getReasonIsFlagged());
			pstmt.setInt(2, review.getReviewID());
			int rowsAffected = pstmt.executeUpdate();
			connection.commit();
			if (rowsAffected > 0) {
				review.setIsFlagged(true);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieves all registered users who have been provisioned a Student or Reviewer role.
	 * 
	 * @return an ArrayList containing all the registered users who have been provisioned Student and Reviewer roles
	 */
	public ArrayList<User> getStudentAndReviewerUsers() {
		String userRole = "";
		ArrayList<User> studentAndReviewerUsers = new ArrayList<>();
		String query = "SELECT userName, firstName, lastName, email, isMuted FROM cse360users";
		try(PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				userRole = "";
				String userName = rs.getString("userName");
				String firstName = rs.getString("firstName");
				String lastName = rs.getString("lastName");
				String email = rs.getString("email");
				boolean isMuted = rs.getBoolean("isMuted");
				boolean[] roles = getUserRole(userName);
				
				if (roles[1] == true) {
					userRole = "Student";
				}
				else if (roles[2] == true) {
					userRole = "Reviewer";
				}
				
				// Only users with Instructor and Staff roles and excluding the currently logged in Staff user
				if (userRole.equals("Student") || userRole.equals("Reviewer")) {
					User studentOrReviewer = new User(userName, roles, email, firstName, lastName, isMuted);
					studentAndReviewerUsers.add(studentOrReviewer);
				}
			}
		}
		catch (SQLException e) {
	      e.printStackTrace();
		}
		return studentAndReviewerUsers;
	}
	
	/**
	 * Sets the isMuted attribute of a user to true disabling them from posting and hides all their previously posted content.
	 * 
	 * @param user the User object of the user to be muted
	 */
	public void muteUser(User user) {
		String query = "UPDATE cse360users SET isMuted = TRUE WHERE userName = ? AND firstName = ? AND lastName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, user.getUserName());
	        pstmt.setString(2, user.getFirstName());
	        pstmt.setString(3, user.getLastName());
	        int rowsAffected = pstmt.executeUpdate();
	        connection.commit(); 
	        if (rowsAffected > 0) {
				user.setIsMuted(true);
			}
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }		
	}
	
	/**
	 * Retrieves the isMuted attribute value for the selected user.
	 * 
	 * @param user the User object for the user whose isMuted attribute is being checked
	 * @return true if the user is muted, false if not
	 */
	public boolean checkIfUserMuted(User user) {
		Boolean isMutedStatus = false;
		String query = "SELECT isMuted FROM cse360users WHERE userName = ? AND firstName = ? AND lastName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
	        pstmt.setString(2, user.getFirstName());
	        pstmt.setString(3, user.getLastName());
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	        	isMutedStatus = rs.getBoolean("isMuted");
	        }
			
		} catch (SQLException e) {
	        e.printStackTrace();
	    }	
		return isMutedStatus;
	}
	
	
	/**
	 * Sets the isHidden attribute of a question to true which will remove the question from ListViews that display submitted questions.
	 * 
	 * @param question the Question object of the question to be hidden
	 */
	public void hideQuestion(Question question) {
		String query = "UPDATE questions SET isHidden = TRUE WHERE questionID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, question.getQuestionID());
	        int rowsAffected = pstmt.executeUpdate();
	        connection.commit(); 
	        if (rowsAffected > 0) {
				question.setIsHidden(true);
			}
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }		
	}
	
	/**
	 * Sets the isHidden attribute of a question reply to true which will remove the question reply from ListViews that display submitted question replies.
	 * 
	 * @param questionReply the Question object of the question reply to be hidden
	 */
	public void hideQuestionReply(Question questionReply) {
		String query = "UPDATE questionReplies SET isHidden = TRUE WHERE replyID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, questionReply.getReplyID());
	        int rowsAffected = pstmt.executeUpdate();
	        connection.commit(); 
	        if (rowsAffected > 0) {
				questionReply.setIsHidden(true);
			}
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Sets the isHidden attribute of an answer to true which will remove the answer from ListViews that display submitted answers.
	 * 
	 * @param answer the Answer object of the answer to be hidden
	 */
	public void hideAnswer(Answer answer) {
		String query = "UPDATE answers SET isHidden = TRUE WHERE answerID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, answer.getAnswerID());
	        int rowsAffected = pstmt.executeUpdate();
	        connection.commit(); 
	        if (rowsAffected > 0) {
				answer.setIsHidden(true);
			}
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Sets the isHidden attribute of a review to true which will remove the review from ListViews that display submitted reviews.
	 * 
	 * @param review the Review object of the review to be hidden
	 */
	public void hideReview(Review review) {
		String query = "UPDATE reviews SET isHidden = TRUE WHERE reviewID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, review.getReviewID());
	        int rowsAffected = pstmt.executeUpdate();
	        connection.commit(); 
	        if (rowsAffected > 0) {
				review.setIsHidden(true);
			}
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Sets the isHidden attribute of all questions which were submitted by the specified user.
	 * 
	 * @param user the User object for the muted user who has submitted the questions to be hidden
	 * @return true if questions updated to hidden, false if not
	 */
	public boolean hideAllQuestionsForMutedUser(User user) {
		boolean updateStatus = false;
		String query = "UPDATE questions SET isHidden = TRUE WHERE studentUserName = ? AND studentFirstName = ? AND studentLastName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
	        pstmt.setString(2, user.getFirstName());
	        pstmt.setString(3, user.getLastName());
	        int rowsAffected =  pstmt.executeUpdate();
	        if (rowsAffected > 0) {
	        	updateStatus = true;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return updateStatus;
	}
	
	/**
	 * Sets the isHidden attribute of all question replies which were submitted by the specified user.
	 * 
	 * @param user the User object for the muted user who has submitted the question replies to be hidden
	 * @return true if question replies updated to hidden, false if not
	 */
	public boolean hideAllQuestionRepliesForMutedUser(User user) {
		boolean updateStatus = false;
		String query = "UPDATE questionReplies SET isHidden = TRUE WHERE studentUserName = ? AND studentFirstName = ? AND studentLastName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
	        pstmt.setString(2, user.getFirstName());
	        pstmt.setString(3, user.getLastName());
	        int rowsAffected = pstmt.executeUpdate();
	        if (rowsAffected > 0) {
	        	updateStatus = true;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return updateStatus;
	}
	
	/**
	 * Sets the isHidden attribute of all answers which were submitted by the specified user.
	 * 
	 * @param user the User object for the muted user who has submitted the answers to be hidden
	 * @return true if answers updated to hidden, false if not
	 */
	public boolean hideAllAnswersForMutedUser(User user) {
		boolean updateStatus = false;
		String query = "UPDATE answers SET isHidden = TRUE WHERE studentUserName = ? AND studentFirstName = ? AND studentLastName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
	        pstmt.setString(2, user.getFirstName());
	        pstmt.setString(3, user.getLastName());
	        int rowsAffected = pstmt.executeUpdate();
	        if (rowsAffected > 0) {
	        	updateStatus = true;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return updateStatus;
	}
	
	/**
	 * Sets the isHidden attribute of all reviews which were submitted by the specified user.
	 * 
	 * @param user the User object for the muted user who has submitted the reviews to be hidden
	 * @return true if reviews updated to hidden, false if not
	 */
	public boolean hideAllReviewsForMutedUser(User user) {
		boolean updateStatus = false;
		String query = "UPDATE reviews SET isHidden = TRUE WHERE reviewerUserName = ? AND reviewerFirstName = ? AND reviewerLastName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
	        pstmt.setString(2, user.getFirstName());
	        pstmt.setString(3, user.getLastName());
	        int rowsAffected = pstmt.executeUpdate();
	        if (rowsAffected > 0) {
	        	updateStatus = true;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return updateStatus;
	}
	
	/**
	 * Mark a private message sent from a Student to another Student or Student to Reviewer as flagged.
	 * 
	 * @param reasonIsFlagged the user defined reason the message is flagged
	 * @param receiverUserName the recipient of the private message
	 * @param senderUserName the userName of the Student who sent the private message
	 * @param timeSent the original time the message was sent
	 * @param messageSubject the user defined subject of the message
	 * @param messageBody the user defined message body
	 */
	public void markStudentPrivateMessageFlagged(String reasonIsFlagged, String receiverUserName, String senderUserName, LocalDateTime timeSent, String messageSubject, String messageBody) {
		String query = "UPDATE PrivateMessages SET isFlagged = TRUE, reasonIsFlagged = ? WHERE sender_user_name = ? AND receiver_user_name = ? AND subject = ? AND message_body = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, reasonIsFlagged);
			pstmt.setString(2, senderUserName);
			pstmt.setString(3, receiverUserName);
			pstmt.setString(4, messageSubject);
			pstmt.setString(5, messageBody);
			//pstmt.setTimestamp(6, Timestamp.valueOf(timeSent));
			pstmt.executeUpdate();
	        connection.commit(); 
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Mark a private message sent from Reviewer to a Student as flagged.
	 * 
	 * @param reasonIsFlagged the user defined reason the message is flagged
	 * @param receiverUserName the recipient of the private message
	 * @param senderUserName the userName of the Reviewer who sent the private message
	 * @param timeSent the original time the message was sent
	 * @param messageSubject the user defined subject of the message
	 * @param messageBody the user defined message body
	 */
	public void markReviewerPrivateMessageFlagged(String reasonIsFlagged, String receiverUserName, String senderUserName, LocalDateTime timeSent, String messageSubject, String messageBody) {
		String query = "UPDATE reviewerMessages SET isFlagged = TRUE, reasonIsFlagged = ? WHERE sender = ? AND recipient = ? AND subject = ? AND body = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, reasonIsFlagged);
			pstmt.setString(2, senderUserName);
			pstmt.setString(3, receiverUserName);
			pstmt.setString(4, messageSubject);
			pstmt.setString(5, messageBody);
			//pstmt.setTimestamp(6, Timestamp.valueOf(timeSent));
			pstmt.executeUpdate();
			connection.commit();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks if a private message sent from a Student to another Student or Student to Reviewer has been flagged by a Staff member.
	 * 
	 * @param receiverUserName the recipient of the private message
	 * @param senderUserName the userName of the Student who sent the private message
	 * @param timeSent the original time the message was sent
	 * @param messageSubject the user defined subject of the message
	 * @param messageBody the user defined message body
	 * @return true if the private message is flagged, false if not
	 */
	public boolean checkIfStudentPrivateMessageFlagged(String receiverUserName, String senderUserName, LocalDateTime timeSent, String messageSubject, String messageBody) {
		Boolean isFlaggedStatus = false;
		String query = "SELECT isFlagged from PrivateMessages WHERE sender_user_name = ? AND receiver_user_name = ? AND subject = ? AND message_body = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, senderUserName);
			pstmt.setString(2, receiverUserName);
			pstmt.setString(3, messageSubject);
			pstmt.setString(4, messageBody);
			//pstmt.setTimestamp(5, Timestamp.valueOf(timeSent));
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	        	isFlaggedStatus = rs.getBoolean("isFlagged");
	        }
			
		} catch (SQLException e) {
	        e.printStackTrace();
	    }	
		return isFlaggedStatus;
	}
	
	/**
	 * Checks if a private message sent from Reviewer to a Student has been flagged by a Staff member.
	 * 
	 * @param receiverUserName the recipient of the private message
	 * @param senderUserName the userName of the Reviewer who sent the private message
	 * @param timeSent the original time the message was sent
	 * @param messageSubject the user defined subject of the message
	 * @param messageBody the user defined message body
	 * @return true if the private message is flagged, false if not
	 */
	public boolean checkIfReviewerPrivateMessageFlagged(String receiverUserName, String senderUserName, LocalDateTime timeSent, String messageSubject, String messageBody) {
		Boolean isFlaggedStatus = false;
		String query = "SELECT isFlagged from reviewerMessages WHERE sender = ? AND recipient = ? AND subject = ? AND body = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, senderUserName);
			pstmt.setString(2, receiverUserName);
			pstmt.setString(3, messageSubject);
			pstmt.setString(4, messageBody);
			//pstmt.setTimestamp(5, Timestamp.valueOf(timeSent));
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	        	isFlaggedStatus = rs.getBoolean("isFlagged");
	        }
			
		} catch (SQLException e) {
	        e.printStackTrace();
	    }	
		return isFlaggedStatus;
	}
	
	/**
	 * Returns the Staff user reason for flagging a private message from a Student to another Student or Student to Reviewer.
	 * 
	 * @param receiverUserName the recipient of the private message
	 * @param senderUserName the userName of the Student who sent the private message
	 * @param timeSent the original time the message was sent
	 * @param messageSubject the user defined subject of the message
	 * @param messageBody the user defined message body
	 * @return the staff user defined reason a private message was flagged
	 */
	public String getReasonStudentPrivateMessageFlagged(String receiverUserName, String senderUserName, LocalDateTime timeSent, String messageSubject, String messageBody) {
		String reasonIsFlagged = "";
		String query = "SELECT reasonIsFlagged from PrivateMessages WHERE sender_user_name = ? AND receiver_user_name = ? AND subject = ? AND message_body = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, senderUserName);
			pstmt.setString(2, receiverUserName);
			pstmt.setString(3, messageSubject);
			pstmt.setString(4, messageBody);
			//pstmt.setTimestamp(5, Timestamp.valueOf(timeSent));
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	        	reasonIsFlagged = rs.getString("reasonIsFlagged");
	        }
			
		} catch (SQLException e) {
	        e.printStackTrace();
	    }	
		return reasonIsFlagged;		
	}
	
	/**
	 * Returns the Staff user reason for flagging a private message from a Reviewer to a Student
	 * 
	 * @param receiverUserName the recipient of the private message
	 * @param senderUserName the userName of the Reviewer who sent the private message
	 * @param timeSent the original time the message was sent
	 * @param messageSubject the user defined subject of the message
	 * @param messageBody the user defined message body
	 * @return the staff user defined reason a private message was flagged
	 */
	public String getReasonReviewerPrivateMessageFlagged(String receiverUserName, String senderUserName, LocalDateTime timeSent, String messageSubject, String messageBody) {
		String reasonIsFlagged = "";
		String query = "SELECT reasonIsFlagged from reviewerMessages WHERE sender = ? AND recipient = ? AND subject = ? AND body = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, senderUserName);
			pstmt.setString(2, receiverUserName);
			pstmt.setString(3, messageSubject);
			pstmt.setString(4, messageBody);
			//pstmt.setTimestamp(5, Timestamp.valueOf(timeSent));
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	        	reasonIsFlagged = rs.getString("reasonIsFlagged");
	        }
			
		} catch (SQLException e) {
	        e.printStackTrace();
	    }	
		return reasonIsFlagged;
	}
	
	/**
	 * Retrieves all questions from the database, specifically for Instructor and Staff Student Interaction View pages.
	 * 
	 * @param user a User object representing the current user
	 * @return an ArrayList containing all of the questions stored in the database
	 */
	public ArrayList<Question> getAllQuestionsEvenHidden(User user) { 
		ArrayList<Question> allQuestions = new ArrayList<>();
		String sqlQuery = "SELECT questionID, studentUserName, studentFirstName, studentLastName, questionTitle, questionBody, isResolved, creationTime, isFlagged, reasonIsFlagged, isHidden FROM questions";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int questionID = rs.getInt("questionID");
				String studentUserName = rs.getString("studentUserName");
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				String questionTitle = rs.getString("questionTitle");
				String questionBody = rs.getString("questionBody");
				boolean isResolved = rs.getBoolean("isResolved");
				Timestamp creationTime = rs.getTimestamp("creationTime");
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reasonIsFlagged = rs.getString("reasonIsFlagged");
				boolean isHidden = rs.getBoolean("isHidden");
				
				Question questionObject = new Question(questionID, studentUserName, studentFirstName, studentLastName, questionTitle, questionBody, isResolved, creationTime.toLocalDateTime(), isFlagged, reasonIsFlagged, isHidden);
				
				allQuestions.add(questionObject);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return allQuestions;
	}
	
	/**
	 * Retrieves all flagged questions from the database, specifically for Instructor and Staff Student Interaction View pages.
	 * 
	 * @param user a User object representing the current user
	 * @return an ArrayList containing all of the questions stored in the database
	 */
	public ArrayList<Question> getAllQuestionsFlagged(User user) { 
		ArrayList<Question> allQuestions = new ArrayList<>();
		String sqlQuery = "SELECT questionID, studentUserName, studentFirstName, studentLastName, questionTitle, questionBody, isResolved, creationTime, isFlagged, reasonIsFlagged, isHidden FROM questions WHERE isFlagged = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int questionID = rs.getInt("questionID");
				String studentUserName = rs.getString("studentUserName");
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				String questionTitle = rs.getString("questionTitle");
				String questionBody = rs.getString("questionBody");
				boolean isResolved = rs.getBoolean("isResolved");
				Timestamp creationTime = rs.getTimestamp("creationTime");
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reasonIsFlagged = rs.getString("reasonIsFlagged");
				boolean isHidden = rs.getBoolean("isHidden");
				
				Question questionObject = new Question(questionID, studentUserName, studentFirstName, studentLastName, questionTitle, questionBody, isResolved, creationTime.toLocalDateTime(), isFlagged, reasonIsFlagged, isHidden);
				
				allQuestions.add(questionObject);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return allQuestions;
	}
	
	/**
	 * Retrieves all hidden questions from the database, specifically for Instructor and Staff Student Interaction View pages.
	 * 
	 * @param user a User object representing the current user
	 * @return an ArrayList containing all of the questions stored in the database
	 */
	public ArrayList<Question> getAllQuestionsHidden(User user) { 
		ArrayList<Question> allQuestions = new ArrayList<>();
		String sqlQuery = "SELECT questionID, studentUserName, studentFirstName, studentLastName, questionTitle, questionBody, isResolved, creationTime, isFlagged, reasonIsFlagged, isHidden FROM questions WHERE isHidden = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int questionID = rs.getInt("questionID");
				String studentUserName = rs.getString("studentUserName");
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				String questionTitle = rs.getString("questionTitle");
				String questionBody = rs.getString("questionBody");
				boolean isResolved = rs.getBoolean("isResolved");
				Timestamp creationTime = rs.getTimestamp("creationTime");
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reasonIsFlagged = rs.getString("reasonIsFlagged");
				boolean isHidden = rs.getBoolean("isHidden");
				
				Question questionObject = new Question(questionID, studentUserName, studentFirstName, studentLastName, questionTitle, questionBody, isResolved, creationTime.toLocalDateTime(), isFlagged, reasonIsFlagged, isHidden);
				
				allQuestions.add(questionObject);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return allQuestions;
	}

	/**
	 * Retrieves all question replies from the database, specifically for Instructor and Staff Student Interaction View pages.
	 * 
	 * @return an ArrayList containing all of the question replies from the database.
	 */
	public ArrayList<Question> getAllRepliesEvenHidden() {
		ArrayList<Question> allReplies = new ArrayList<>();
		String sqlQuery = "SELECT replyID, questionID, studentUserName, studentFirstName, studentLastName, questionReplyText, replyingTo, isFlagged, reasonIsFlagged, isHidden FROM questionReplies";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int replyID = rs.getInt("replyID");
				int questionID = rs.getInt("questionID");
				String studentUserName = rs.getString("studentUserName");
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				String questionReplyText = rs.getString("questionReplyText");
				String questionReplyingto = rs.getString("replyingto");
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reasonIsFlagged = rs.getString("reasonIsFlagged");
				boolean isHidden = rs.getBoolean("isHidden");
				
				Question replyObject = new Question(replyID, questionID, studentUserName, studentFirstName, studentLastName, questionReplyText, questionReplyingto, isFlagged, reasonIsFlagged, isHidden);
				allReplies.add(replyObject);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return allReplies;	
	}

	/**
	 * Retrieves all questions from the database which have received at least one answer, specifically for Instructor and Staff Student Interaction View pages.
	 * 
	 * @return an ArrayList containing all of the questions which have at least one potential answer
	 */
	public ArrayList<Question> getAnsweredQuestionsEvenHidden() {
		ArrayList<Question> getAnsweredQuestions = new ArrayList<>();
		String sqlQuery = "SELECT q.questionID, q.studentUserName, q.studentFirstName, q.studentLastName, q.questionTitle, q.questionBody, q.isResolved, q.creationTime, q.isFlagged, q.reasonIsFlagged, q.isHidden FROM questions q WHERE q.questionID IN (SELECT a.questionID FROM answers a)";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int questionID = rs.getInt("questionID");
				String studentUserName = rs.getString("studentUserName");
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				String questionTitle = rs.getString("questionTitle");
				String questionBody = rs.getString("questionBody");
				boolean isResolved = rs.getBoolean("isResolved");
				Timestamp creationTime = rs.getTimestamp("creationTime");
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reasonIsFlagged = rs.getString("reasonIsFlagged");
				boolean isHidden = rs.getBoolean("isHidden");
				
				Question questionObject = new Question(questionID, studentUserName, studentFirstName, studentLastName, questionTitle, questionBody, isResolved, creationTime.toLocalDateTime(), isFlagged, reasonIsFlagged, isHidden);
				
				getAnsweredQuestions.add(questionObject);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return getAnsweredQuestions;
	}

	/**
	 * Retrieves all questions from the database that have received no potential answers, specifically for Instructor and Staff Student Interaction View pages.
	 * 
	 * @return an ArrayList of all questions from the database that have no tied answers
	 */
	public ArrayList<Question> getUnansweredQuestionsEvenHidden() {
		ArrayList<Question> unansweredQuestions = new ArrayList<>();
		String sqlQuery = "SELECT q.questionID, q.studentUserName, q.studentFirstName, q.studentLastName, q.questionTitle, q.questionBody, q.isResolved, q.creationTime, q.isFlagged, q.reasonIsFlagged, q.isHidden " +
				"FROM questions q LEFT JOIN answers a ON q.questionID = a.questionID " +
				"WHERE a.answerID IS NULL";

		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int questionID = rs.getInt("questionID");
				String studentUserName = rs.getString("studentUserName");
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				String questionTitle = rs.getString("questionTitle");
				String questionBody = rs.getString("questionBody");
				boolean isResolved = rs.getBoolean("isResolved");
				LocalDateTime creationTime = rs.getTimestamp("creationTime").toLocalDateTime();
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reasonIsFlagged = rs.getString("reasonIsFlagged");
				boolean isHidden = rs.getBoolean("isHidden");

				Question questionObject = new Question(questionID, studentUserName, studentFirstName, studentLastName, questionTitle, questionBody, isResolved, creationTime, isFlagged, reasonIsFlagged, isHidden);
				questionObject.setQuestionID(questionID);
				unansweredQuestions.add(questionObject);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return unansweredQuestions;
	}

	/**
	 * Retrieves all the questions from the database not marked resolved, specifically for Instructor and Staff Student Interaction View pages.
	 * 
	 * @return an ArrayList containing all of the questions marked FALSE for the attribute isResolved
	 */
	public ArrayList<Question> getUnresolvedQuestionsEvenHidden() { 
		ArrayList<Question> unresolvedQuestions = new ArrayList<>();
		String sqlQuery = "SELECT questionID, studentUserName, studentFirstName, studentLastName, questionTitle, questionBody, isResolved, creationTime, isFlagged, reasonIsFlagged, isHidden FROM questions WHERE isResolved = false";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int questionID = rs.getInt("questionID");
				String studentUserName = rs.getString("studentUserName");
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				String questionTitle = rs.getString("questionTitle");
				String questionBody = rs.getString("questionBody");
				boolean isResolved = rs.getBoolean("isResolved");
				Timestamp creationTime = rs.getTimestamp("creationTime");
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reasonIsFlagged = rs.getString("reasonIsFlagged");
				boolean isHidden = rs.getBoolean("isHidden");
				
				Question questionObject = new Question(questionID, studentUserName, studentFirstName, studentLastName, questionTitle, questionBody, isResolved, creationTime.toLocalDateTime(), isFlagged, reasonIsFlagged, isHidden);
				
				unresolvedQuestions.add(questionObject);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return unresolvedQuestions;
	}

	/**
	 * Retrieves all the answers from the database, specifically for Instructor and Staff Student Interaction View pages.
	 * 
	 * @param user the User object representing the current user
	 * @return an ArrayList of all the answers existing in the database
	 */
	public ArrayList<Answer> getAllAnswersEvenHidden(User user) { 
		ArrayList<Answer> allAnswers = new ArrayList<>();
		String sqlQuery = "SELECT answerID, questionID, studentUserName, studentFirstName, studentLastName, answerText, isAnswerUnread, isResolved, creationTime, isFlagged, reasonIsFlagged, isHidden FROM answers";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int answerID = rs.getInt("answerID");
				int questionID = rs.getInt("questionID");
				String studentUserName= rs.getString("studentUserName");
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				String answerText= rs.getString("answerText");
				boolean isAnswerUnread = rs.getBoolean("isResolved");
				boolean isResolved = rs.getBoolean("isResolved");
				Timestamp creationTime = rs.getTimestamp("creationTime");
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reasonIsFlagged = rs.getString("reasonIsFlagged");
				boolean isHidden = rs.getBoolean("isHidden");
				
				Answer answerObject = new Answer(answerID, questionID, studentUserName, studentFirstName, studentLastName, answerText, isAnswerUnread, isResolved, creationTime.toLocalDateTime(), isFlagged, reasonIsFlagged, isHidden);
				
				allAnswers.add(answerObject);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return allAnswers;
	}
	
	/**
	 * Retrieves all the flagged answers from the database, specifically for Instructor and Staff Student Interaction View pages.
	 * 
	 * @param user the User object representing the current user
	 * @return an ArrayList of all the answers existing in the database
	 */
	public ArrayList<Answer> getAllAnswersFlagged(User user) { 
		ArrayList<Answer> allAnswers = new ArrayList<>();
		String sqlQuery = "SELECT answerID, questionID, studentUserName, studentFirstName, studentLastName, answerText, isAnswerUnread, isResolved, creationTime, isFlagged, reasonIsFlagged, isHidden FROM answers WHERE isFlagged = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int answerID = rs.getInt("answerID");
				int questionID = rs.getInt("questionID");
				String studentUserName= rs.getString("studentUserName");
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				String answerText= rs.getString("answerText");
				boolean isAnswerUnread = rs.getBoolean("isResolved");
				boolean isResolved = rs.getBoolean("isResolved");
				Timestamp creationTime = rs.getTimestamp("creationTime");
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reasonIsFlagged = rs.getString("reasonIsFlagged");
				boolean isHidden = rs.getBoolean("isHidden");
				
				Answer answerObject = new Answer(answerID, questionID, studentUserName, studentFirstName, studentLastName, answerText, isAnswerUnread, isResolved, creationTime.toLocalDateTime(), isFlagged, reasonIsFlagged, isHidden);
				
				allAnswers.add(answerObject);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return allAnswers;
	}
	
	/**
	 * Retrieves all the hidden answers from the database, specifically for Instructor and Staff Student Interaction View pages.
	 * 
	 * @param user the User object representing the current user
	 * @return an ArrayList of all the answers existing in the database
	 */
	public ArrayList<Answer> getAllAnswersHidden(User user) { 
		ArrayList<Answer> allAnswers = new ArrayList<>();
		String sqlQuery = "SELECT answerID, questionID, studentUserName, studentFirstName, studentLastName, answerText, isAnswerUnread, isResolved, creationTime, isFlagged, reasonIsFlagged, isHidden FROM answers WHERE isHidden = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int answerID = rs.getInt("answerID");
				int questionID = rs.getInt("questionID");
				String studentUserName= rs.getString("studentUserName");
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				String answerText= rs.getString("answerText");
				boolean isAnswerUnread = rs.getBoolean("isResolved");
				boolean isResolved = rs.getBoolean("isResolved");
				Timestamp creationTime = rs.getTimestamp("creationTime");
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reasonIsFlagged = rs.getString("reasonIsFlagged");
				boolean isHidden = rs.getBoolean("isHidden");
				
				Answer answerObject = new Answer(answerID, questionID, studentUserName, studentFirstName, studentLastName, answerText, isAnswerUnread, isResolved, creationTime.toLocalDateTime(), isFlagged, reasonIsFlagged, isHidden);
				
				allAnswers.add(answerObject);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return allAnswers;
	}

	/**
	 * Retrieve only answers from the database for the specified questionID, specifically for Instructor and Staff Student Interaction View pages.
	 * 
	 * @param questionID the questionID for the question to retrieve answers for
	 * @return an ArrayList containing only the answers for the specified questionID
	 */
	public ArrayList<Answer> getAnswersByQuestionIDEvenHidden(int questionID) {
		ArrayList<Answer> answersForQuestionID = new ArrayList<>();
		String sqlQuery = "SELECT answerID, questionID, studentUserName, studentFirstName, studentLastName, answerText, isResolved, isAnswerUnread, creationTime, , isFlagged, reasonIsFlagged, isHidden FROM answers WHERE questionID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			pstmt.setInt(1, questionID);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int answerID = rs.getInt("answerID");
				int questID = rs.getInt("questionID");
				String studentUserName = rs.getString("studentUserName");
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				String answerText = rs.getString("answerText");
				boolean isResolved = rs.getBoolean("isResolved");
				boolean isAnswerUnread = rs.getBoolean("isAnswerUnread");
				Timestamp creationTime = rs.getTimestamp("creationTime");
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reasonIsFlagged = rs.getString("reasonIsFlagged");
				boolean isHidden = rs.getBoolean("isHidden");
					
				Answer answer = new Answer(answerID, questID, studentUserName, studentFirstName, studentLastName, answerText, isAnswerUnread, isResolved, creationTime.toLocalDateTime(), isFlagged, reasonIsFlagged, isHidden);
				answersForQuestionID.add(answer);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return answersForQuestionID;
	}
	
	/**
	 * Retrieves all answers from the database that have been marked as resolved by a user.
	 * 
	 * @return an ArrayList of all answers from the database whose isResolved attribute is set to true
	 */
	public ArrayList<Answer> getResolvedAnswersEvenHidden() {
		ArrayList<Answer> resolvedAnswers = new ArrayList<>();
		String sqlQuery = "SELECT answerID, questionID, studentUserName, studentFirstName, studentLastName, answerText, isAnswerUnread, isResolved, creationTime, isFlagged, reasonIsFlagged, isHidden FROM answers WHERE isResolved = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int answerID = rs.getInt("answerID");
				int questionID = rs.getInt("questionID");
				String studentUserName= rs.getString("studentUserName");
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				String answerText= rs.getString("answerText");
				boolean isAnswerUnread = rs.getBoolean("isResolved");
				boolean isResolved = rs.getBoolean("isResolved");
				Timestamp creationTime = rs.getTimestamp("creationTime");
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reasonIsFlagged = rs.getString("reasonIsFlagged");
				boolean isHidden = rs.getBoolean("isHidden");
				
				Answer answerObject = new Answer(answerID, questionID, studentUserName, studentFirstName, studentLastName, answerText, isAnswerUnread, isResolved, creationTime.toLocalDateTime(), isFlagged, reasonIsFlagged, isHidden);
				
				resolvedAnswers.add(answerObject);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return resolvedAnswers;
	}

	/**
	 * Retrieves all answers from the database that have not been marked as resolved by a user, specifically for Instructor and Staff Student Interaction View pages.
	 * 
	 * @return an ArrayList of all answers from the database whose isResolved attribute is set to false
	 */
	public ArrayList<Answer> getUnresolvedAnswersEvenHidden() {
		ArrayList<Answer> unresolvedAnswers = new ArrayList<>();
		String sqlQuery = "SELECT answerID, questionID, studentUserName, studentFirstName, studentLastName, answerText, isAnswerUnread, isResolved, creationTime, isFlagged, reasonIsFlagged, isHidden FROM answers WHERE isResolved = FALSE";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int answerID = rs.getInt("answerID");
				int questionID = rs.getInt("questionID");
				String studentUserName= rs.getString("studentUserName");
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				String answerText= rs.getString("answerText");
				boolean isAnswerUnread = rs.getBoolean("isResolved");
				boolean isResolved = rs.getBoolean("isResolved");
				Timestamp creationTime = rs.getTimestamp("creationTime");
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reasonIsFlagged = rs.getString("reasonIsFlagged");
				boolean isHidden = rs.getBoolean("isHidden");
				
				Answer answerObject = new Answer(answerID, questionID, studentUserName, studentFirstName, studentLastName, answerText, isAnswerUnread, isResolved, creationTime.toLocalDateTime(), isFlagged, reasonIsFlagged, isHidden) ;
				
				unresolvedAnswers.add(answerObject);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return unresolvedAnswers;
	}

	/**
	 * Retrieves all answers from the database that have been marked as resolved, specifically for Instructor and Staff Student Interaction View pages.
	 * 
	 * @return an ArrayList of all answers whose isResolved attribute in the database is marked as true
	 */
	public ArrayList<Answer> getResolvedAnswersWithQuestionsEvenHidden() {
		ArrayList<Answer> resolvedAnswers = new ArrayList<>();
		String sqlQuery = "SELECT a.answerID, a.studentFirstName, a.studentLastName, a.answerText, a.questionID, q.questionTitle, a.isFlagged, a.isReasonFlagged, a.isHidden " +
				"FROM answers a JOIN questions q ON a.questionID = q.questionID " +
				"WHERE a.isResolved = TRUE";

		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int answerID = rs.getInt("answerID");
				String studentFirstName = rs.getString("studentFirstName");
				String studentLastName = rs.getString("studentLastName");
				String answerText = rs.getString("answerText");
				int questionID = rs.getInt("questionID");
				String questionTitle = rs.getString("questionTitle");
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reasonIsFlagged = rs.getString("reasonIsFlagged");
				boolean isHidden = rs.getBoolean("isHidden");

				Answer answerObject = new Answer(studentFirstName, studentLastName, answerText);
				answerObject.setAnswerID(answerID);
				answerObject.setQuestionID(questionID);
				answerObject.setIsFlagged(isFlagged);
				answerObject.setReasonIsFlagged(reasonIsFlagged);
				answerObject.setIsHidden(isHidden);
				
				Question questionObject = new Question(studentFirstName, studentLastName, questionTitle);
				
				String questionTitleForAnswer = questionObject.getQuestionTitle();
				answerObject.setQuestionTitleForAnswer(questionTitleForAnswer);
				resolvedAnswers.add(answerObject);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resolvedAnswers;
	}

	/**
	 * Retrieves all reviews from the database, specifically for Instructor and Staff Student Interaction View pages.
	 * 
	 * @param user the User object for the current user.
	 * @return an ArrayList containing all reviews existing in the database.
	 */
	public ArrayList<Review> getAllReviewsEvenHidden(User user) { 
		ArrayList<Review> allReviews = new ArrayList<>();
		String sqlQuery = "SELECT questionID, answerID, prevReviewID, reviewID, reviewBody, reviewerUserName, reviewerFirstName, reviewerLastName, isFlagged, reasonIsFlagged, isHidden FROM reviews";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int questionID = rs.getInt("questionID");
				int answerID = rs.getInt("answerID");
				int prevID = rs.getInt("prevReviewID");
				int reviewID = rs.getInt("reviewID");
				String reviewBody = rs.getString("reviewBody");
				String reviewerUserName = rs.getString("reviewerUserName");
				String reviewerFirstName = rs.getString("reviewerFirstName");
				String reviewerLastName = rs.getString("reviewerLastName");
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reasonIsFlagged = rs.getString("reasonIsFlagged");
				boolean isHidden = rs.getBoolean("isHidden");
				
				Review reviewObject = new Review(questionID, answerID, prevID, reviewID, reviewBody, reviewerUserName, reviewerFirstName, reviewerLastName, isFlagged, reasonIsFlagged, isHidden);
				
				allReviews.add(reviewObject);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return allReviews;
	}
	
	/**
	 * Retrieves all flagged reviews from the database, specifically for Instructor and Staff Student Interaction View pages.
	 * 
	 * @param user the User object for the current user.
	 * @return an ArrayList containing all reviews existing in the database.
	 */
	public ArrayList<Review> getAllReviewsFlagged(User user) { 
		ArrayList<Review> allReviews = new ArrayList<>();
		String sqlQuery = "SELECT questionID, answerID, prevReviewID, reviewID, reviewBody, reviewerUserName, reviewerFirstName, reviewerLastName, isFlagged, reasonIsFlagged, isHidden FROM reviews WHERE isFlagged = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int questionID = rs.getInt("questionID");
				int answerID = rs.getInt("answerID");
				int prevID = rs.getInt("prevReviewID");
				int reviewID = rs.getInt("reviewID");
				String reviewBody = rs.getString("reviewBody");
				String reviewerUserName = rs.getString("reviewerUserName");
				String reviewerFirstName = rs.getString("reviewerFirstName");
				String reviewerLastName = rs.getString("reviewerLastName");
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reasonIsFlagged = rs.getString("reasonIsFlagged");
				boolean isHidden = rs.getBoolean("isHidden");
				
				Review reviewObject = new Review(questionID, answerID, prevID, reviewID, reviewBody, reviewerUserName, reviewerFirstName, reviewerLastName, isFlagged, reasonIsFlagged, isHidden);
				
				allReviews.add(reviewObject);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return allReviews;
	}
	
	/**
	 * Retrieves all hidden reviews from the database, specifically for Instructor and Staff Student Interaction View pages.
	 * 
	 * @param user the User object for the current user.
	 * @return an ArrayList containing all reviews existing in the database.
	 */
	public ArrayList<Review> getAllReviewsHidden(User user) { 
		ArrayList<Review> allReviews = new ArrayList<>();
		String sqlQuery = "SELECT questionID, answerID, prevReviewID, reviewID, reviewBody, reviewerUserName, reviewerFirstName, reviewerLastName, isFlagged, reasonIsFlagged, isHidden FROM reviews WHERE isHidden = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int questionID = rs.getInt("questionID");
				int answerID = rs.getInt("answerID");
				int prevID = rs.getInt("prevReviewID");
				int reviewID = rs.getInt("reviewID");
				String reviewBody = rs.getString("reviewBody");
				String reviewerUserName = rs.getString("reviewerUserName");
				String reviewerFirstName = rs.getString("reviewerFirstName");
				String reviewerLastName = rs.getString("reviewerLastName");
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reasonIsFlagged = rs.getString("reasonIsFlagged");
				boolean isHidden = rs.getBoolean("isHidden");
				
				Review reviewObject = new Review(questionID, answerID, prevID, reviewID, reviewBody, reviewerUserName, reviewerFirstName, reviewerLastName, isFlagged, reasonIsFlagged, isHidden);
				
				allReviews.add(reviewObject);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return allReviews;
	}

	/**
	 * Retrieves all reviews from the database that were created for the specified answerID, specifically for Instructor and Staff Student Interaction View pages.
	 * 
	 * @param answerID the answerID for a specific answer
	 * @return an ArrayList containing all reviews tied to a specified answerID
	 */
	public ArrayList<Review> getReviewByAnswerIDEvenHidden(int answerID){
		ArrayList<Review> reviews = new ArrayList<>();
		String sqlQuery = "SELECT questionID, answerID, prevReviewID, reviewID, reviewBody, reviewerUserName, reviewerFirstName, reviewerLastName, isFlagged, reasonIsFlagged, isHidden FROM reviews WHERE answerID = ? ";
		try (PreparedStatement pstmt = connection.prepareStatement(sqlQuery)) {
			pstmt.setInt(1, answerID);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int questionID = rs.getInt("questionID");
				int prevID = rs.getInt("prevReviewID");
				int reviewID = rs.getInt("reviewID");
				String reviewBody = rs.getString("reviewBody");
				String reviewerUserName = rs.getString("reviewerUserName");
				String reviewerFirstName = rs.getString("reviewerFirstName");
				String reviewerLastName = rs.getString("reviewerLastName");
				boolean isFlagged = rs.getBoolean("isFlagged");
				String reasonIsFlagged = rs.getString("reasonIsFlagged");
				boolean isHidden = rs.getBoolean("isHidden");
				
				Review reviewObject = new Review(questionID, answerID, prevID, reviewID, reviewBody, reviewerUserName, reviewerFirstName, reviewerLastName, isFlagged, reasonIsFlagged, isHidden);
				
				reviews.add(reviewObject);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return reviews;
	}
	
	/**
	 * Marks an answer as read by setting isAnswerUnread attribute to FALSE.
	 * 
	 * @param answer answer object to mark isAnswerUnread attribute as false
	 */
	public void markAnswerAsRead(Answer answer) {
		String query = "UPDATE answers SET isAnswerUnread = FALSE WHERE answerID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, answer.getAnswerID());
			int rowsAffected = pstmt.executeUpdate();
			connection.commit();
			if (rowsAffected > 0) {
				answer.setIsAnswerUnread(false);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieves the count of unread private messages for a specific student and a specific questionID.
	 * 
	 * @param studentUserName the student's username.
	 * @param questionID the unique identifier of the question to retrieve all unread private messages for.
	 * @return the number of unread messages for the specified questionID.
	 */
	public int getUnreadPrivateMessageCountByQuestion(String studentUserName, int questionID) {
		int count = -1;
		String sql = "SELECT COUNT(*) AS unread_count FROM PrivateMessages WHERE receiver_user_name = ? AND is_read = FALSE AND questionID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, studentUserName);
	        pstmt.setInt(2, questionID);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            count = rs.getInt("unread_count");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return count;
	}
	
	/**
	 * Sends a private message from a student to a reviewer about a review.
	 *
	 * @param senderUserName   The username of the student sending the message.
	 * @param receiverUserName The username of the reviewer receiving the message.
	 * @param reviewID       The ID of the review being discussed.
	 * @param messageBody      The content of the message.
	 * @return True if the message was sent successfully; false otherwise.
	 */
	public boolean sendPrivateMessageToReviewer(String senderUserName, String receiverUserName, int reviewID, String subject, String messageBody) {
	    try {
	        String sql = "INSERT INTO PrivateMessages (sender_user_name, receiver_user_name, reviewID, message_body, subject, is_read, timestamp, isFlagged, reasonIsFlagged, questionID) VALUES (?, ?, ?, ?, ?, FALSE, ?, FALSE, ?, ?)";
	        PreparedStatement pstmt = connection.prepareStatement(sql);
	        pstmt.setString(1, senderUserName);
	        pstmt.setString(2, receiverUserName);
	        pstmt.setInt(3, reviewID);
	        pstmt.setString(4, messageBody);
	        pstmt.setString(5, subject);
	        pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
	        pstmt.setString(7, "");
	        pstmt.setInt(8, -1);
	        int rows = pstmt.executeUpdate();
	        pstmt.close();
	        return rows > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	/**
	 * Sends a private message from a student to another student about a question.
	 *
	 * @param senderUserName   The username of the student sending the message.
	 * @param receiverUserName The username of the student receiving the message.
	 * @param questionID       The ID of the question being discussed.
	 * @param messageBody      The content of the message.
	 * @return True if the message was sent successfully; false otherwise.
	 */
	public boolean sendPrivateMessageToStudent(String senderUserName, String receiverUserName, int questionID, String subject, String messageBody) {
	    try {
	        String sql = "INSERT INTO PrivateMessages (sender_user_name, receiver_user_name, questionID, message_body, subject, is_read, timestamp, isFlagged, reasonIsFlagged, reviewID) VALUES (?, ?, ?, ?, ?, FALSE, ?, FALSE, ?, ?)";
	        PreparedStatement pstmt = connection.prepareStatement(sql);
	        pstmt.setString(1, senderUserName);
	        pstmt.setString(2, receiverUserName);
	        pstmt.setInt(3, questionID);
	        pstmt.setString(4, messageBody);
	        pstmt.setString(5, subject);
	        pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
	        pstmt.setString(7, "");
	        pstmt.setInt(8, -1);
	        int rows = pstmt.executeUpdate();
	        pstmt.close();
	        return rows > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	/**
	 * Marks a Student to Student private message as read.
	 * 
	 * @param receiverUserName the username of the Student recipient of the message
	 * @param senderUserName the username of the Student who sent the message
	 * @param messageSubject the message subject of the message to mark as read
	 * @param messageBody the message body of the message to mark as read
	 */
	public void markStudentToStudentMessageAsRead(String receiverUserName, String senderUserName, String messageSubject, String messageBody) {
		String query = "UPDATE PrivateMessages SET is_read = TRUE WHERE sender_user_name = ? AND receiver_user_name = ? AND subject = ? AND message_body = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, senderUserName);
			pstmt.setString(2, receiverUserName);
			pstmt.setString(3, messageSubject);
			pstmt.setString(4, messageBody);
			pstmt.executeUpdate();
	        connection.commit(); 
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Marks a Reviewer to Student private message as read.
	 * 
	 * @param receiverUserName the username of the Student recipient of the message
	 * @param senderUserName the username of the reviewer who sent the message
	 * @param messageSubject the message subject of the message to mark as read
	 * @param messageBody the message body of the message to mark as read
	 */
	public void markReviewerToStudentMessageAsRead(String receiverUserName, String senderUserName, String messageSubject, String messageBody) {
		String query = "UPDATE reviewerMessages SET isRead = TRUE WHERE sender = ? AND recipient = ? AND subject = ? AND body = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, senderUserName);
			pstmt.setString(2, receiverUserName);
			pstmt.setString(3, messageSubject);
			pstmt.setString(4, messageBody);
			pstmt.executeUpdate();
			connection.commit();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieves the count of unread private messages for a specific student.
	 *
	 * @param studentUserName The student's username.
	 * @return The number of unread messages.
	 */
	public int getUnreadPrivateMessageCount(String studentUserName) {
	    int count = 0;
	    // Fetch from PrivateMessages table
	    String query1 = "SELECT COUNT(*) AS unread_count FROM PrivateMessages WHERE receiver_user_name = ? AND is_read = FALSE";
	    
	    // Fetch from reviewerMessages table
	    String query2 =  "SELECT COUNT(*) AS unreadCount FROM reviewerMessages WHERE recipient = ? AND isRead = FALSE";
	    
	    try {
	    	// First query
	    	try (PreparedStatement pstmt1 = connection.prepareStatement(query1)) {
	            pstmt1.setString(1, studentUserName);
	            ResultSet rs = pstmt1.executeQuery();
	            if (rs.next()) {
	                count = rs.getInt("unread_count");
	            }
	    	}
	    	// Second query
            try (PreparedStatement pstmt2 = connection.prepareStatement(query2)) {
        		pstmt2.setString(1, studentUserName);
        		ResultSet rs = pstmt2.executeQuery();
        		if (rs.next()) {
        			count += rs.getInt("unreadCount");
        		}
            }
	    }
	    catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return count;
	}
	
	/**
	 * Retrieves all private messages sent by a specific student user.
	 *
	 * @param studentUserName The username of the student.
	 * @return A list of message strings formatted with recipient, subject, date, and message contents.
	 */
	public ArrayList<String> getPrivateMessagesSentByStudent(String studentUserName) {
	    ArrayList<String> messages = new ArrayList<>();
	    DateTimeFormatter timeMessageSent = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
	    
	    // Retrieve messages sent from another Student to this Student
	    String query1 = "SELECT p.sender_user_name, p.receiver_user_name, p.subject, p.message_body, p.timestamp FROM PrivateMessages p WHERE p.sender_user_name = ?";
	    
	    // Retrieve messages sent from Reviewers to this Student 
		String query2 = "SELECT r.sender, r.recipient, r.subject, r.body, r.sentTime FROM reviewerMessages r WHERE r.sender = ?";
	    
		try {
			try (PreparedStatement pstmt1 = connection.prepareStatement(query1)) {
				pstmt1.setString(1, studentUserName);
				ResultSet rs = pstmt1.executeQuery();
		        while (rs.next()) {
		        	String senderUserName = rs.getString("sender_user_name");
		        	String receiverUserName = rs.getString("receiver_user_name");
		        	String messageSubject = rs.getString("subject");
		        	String messageBody = rs.getString("message_body");
		        	Timestamp timeSent = rs.getTimestamp("timestamp");
		        	String formattedTime = timeSent.toLocalDateTime().format(timeMessageSent);
		        	
		        	messages.add("To [Student]: " + receiverUserName + "\n" + "From [Student]: " + senderUserName + "\n" + "Date: " + formattedTime + "\n" + "Message Subject: " + messageSubject + "\n" + "Message Body: " + messageBody);
		        }
			}
			
			try (PreparedStatement pstmt2 = connection.prepareStatement(query2)) {
				pstmt2.setString(1, studentUserName);
				ResultSet rs2 = pstmt2.executeQuery();
				while (rs2.next()) {
					String reviewer2UserName = rs2.getString("sender");
					String student2UserName = rs2.getString("recipient");
					String messageSubject2 = rs2.getString("subject");
					String messageBody2 = rs2.getString("body");
					Timestamp timeSent2 = rs2.getTimestamp("sentTime");
					String formattedTime2 = timeSent2.toLocalDateTime().format(timeMessageSent);
					
					messages.add("To [Student]: " + student2UserName + "\n" + "From [Reviewer]: " + reviewer2UserName + "\n" + "Date: " + formattedTime2 + "\n" + "Message Subject: " + messageSubject2 + "\n" + "Message Body: " + messageBody2);
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	    return messages;
	}
	
	/**
     * Creates the flags table if it does not already exist.
     *
     * @throws SQLException If an error occurs while creating the table.
     */
    private void createFlagTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS flags (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "itemType TEXT, " +
                     "itemId INTEGER, " +
                     "reason TEXT, " +
                     "flaggedBy TEXT)";
        connection.createStatement().execute(sql);
    }
    
  
    
    /**
     * Deletes a question from the database as a staff member.
     * This is used for moderation purposes and bypasses normal user permissions.
     *
     * @param questionId The ID of the question to delete.
     * @return true if the deletion was successful, false otherwise.
     */
    public boolean staffDeleteQuestion(int questionId) {
        String query = "DELETE FROM Questions WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, questionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes an answer from the database as a staff member.
     * This is used for moderation purposes and bypasses normal user permissions.
     *
     * @param answerId The ID of the answer to delete.
     * @return true if the deletion was successful, false otherwise.
     */
    public boolean staffDeleteAnswer(int answerId) {
        String query = "DELETE FROM Answers WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, answerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a reply from the database as a staff member.
     * This is used for moderation purposes and bypasses normal user permissions.
     *
     * @param replyId The ID of the reply to delete.
     * @return true if the deletion was successful, false otherwise.
     */
    public boolean staffDeleteReply(int replyId) {
        String query = "DELETE FROM Replies WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, replyId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a review from the database as a staff member.
     * This is used for moderation purposes and bypasses normal user permissions.
     *
     * @param reviewId The ID of the review to delete.
     * @return true if the deletion was successful, false otherwise.
     */
    public boolean staffDeleteReview(int reviewId) {
        String query = "DELETE FROM Reviews WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, reviewId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Hides a question by setting its visibility to false.
     */
    public boolean staffHideQuestion(int questionId) {
        String sql = "UPDATE Questions SET visible = false WHERE question_id = ?";
        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, questionId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Establishes and returns a connection to the SQLite database.
     *
     * @return Connection object to the database
     * @throws SQLException if a database access error occurs
     */
    public Connection connect() throws SQLException {
        // Replace this path with your actual DB file path if needed
        String url = "jdbc:sqlite:your-database-file.db";
        return DriverManager.getConnection(url);
    }

    /**
     * Hides an answer by setting its visibility to false.
     */
    public boolean staffHideAnswer(int answerId) {
        String sql = "UPDATE Answers SET visible = false WHERE answer_id = ?";
        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, answerId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Hides a reply by setting its visibility to false.
     */
    public boolean staffHideReply(int replyId) {
        String sql = "UPDATE Replies SET visible = false WHERE reply_id = ?";
        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, replyId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Hides a review by setting its visibility to false.
     */
    public boolean staffHideReview(int reviewId) {
        String sql = "UPDATE Reviews SET visible = false WHERE review_id = ?";
        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reviewId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Submits a clarification to a question by a staff member.
     *
     * @param questionId the ID of the question
     * @param clarificationText the clarification message
     * @return true if submission was successful, false otherwise
     */
    public boolean staffSubmitClarification(int questionId, String clarificationText) {
        String sql = "INSERT INTO Clarifications (question_id, clarification_text, submitted_by_staff, timestamp) VALUES (?, ?, true, CURRENT_TIMESTAMP)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, questionId);
            pstmt.setString(2, clarificationText);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Allows a staff member to provide an answer to a question.
     * @param questionId The ID of the question to answer.
     * @param staffId The ID of the staff member submitting the answer.
     * @param content The content of the staff-provided answer.
     * @return true if the answer was successfully added, false otherwise.
     */
    public boolean staffSubmitAnswer(int questionId, int staffId, String content) {
        String sql = "INSERT INTO Answers (question_id, user_id, content, visible) VALUES (?, ?, ?, true)";
        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, questionId);
            pstmt.setInt(2, staffId);
            pstmt.setString(3, content);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Sends a private message from one staff member to another.
     */
    public boolean staffSendPrivateMessage(String sender, String recipient, String messageBody) {
        String sql = "INSERT INTO PrivateMessages (sender, recipient, message_body, timestamp) VALUES (?, ?, ?, datetime('now'))";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sender);
            pstmt.setString(2, recipient);
            pstmt.setString(3, messageBody);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

  
    
    /**
     * Retrieves all questions in the system.
     * @return List of all questions
     */
    public List<Question> staffGetAllQuestions() {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM Questions";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                questions.add(new Question(
                    rs.getInt("question_id"),
                    rs.getString("username"),
                    rs.getString("title"),
                    rs.getString("content"),
                    rs.getString("timestamp")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }



    
    /**
     * Mutes a user by setting their muted status to true.
     *
     * @param username The username of the student to mute.
     * @return true if the operation was successful, false otherwise.
     */
    public boolean staffMuteUser(String username) {
        String sql = "UPDATE Users SET is_muted = true WHERE username = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Submits a new admin request to the database.
     *
     * @param user the instructor submitting the request
     * @param description the request description
     * @param actionType the action being requested
     * @param priority the priority or deadline for the request
     */
    public void submitAdminRequest(User user, String description, String actionType, String priority) {
        // TODO: Insert into admin_requests table (status = 'open')
        // Sample SQL: INSERT INTO admin_requests (instructor, description, action_type, priority, status) VALUES (?, ?, ?, ?, 'open');
    }

    /**
     * Reopens a closed admin request by creating a new linked request with updated description.
     *
     * @param user the instructor reopening the request
     * @param originalRequest reference to the original request (could be ID or description)
     * @param newDescription the new description for the reopened request
     */
    public void reopenAdminRequest(User user, String originalRequest, String newDescription) {
        // TODO: Lookup closed request ID, insert a new one with reference to old ID and status = 'open'
        // Sample SQL: INSERT INTO admin_requests (instructor, description, status, linked_to) VALUES (?, ?, 'open', ?);
    }

    /**
     * Closes an open admin request in the database.
     *
     * @param user the admin closing the request
     * @param request the selected request (could be ID or description)
     */
    public void closeAdminRequest(User user, String request) {
        // TODO: Update request status to 'closed'
        // Sample SQL: UPDATE admin_requests SET status = 'closed' WHERE id = ?;
    }

    /**
     * Retrieves a list of open admin requests visible to the given user.
     *
     * @param user the user (role-specific filtering could apply)
     * @return list of open request strings
     */
    public List<String> getOpenAdminRequests(User user) {
        // TODO: Query open requests
        return new ArrayList<>();
    }

    /*
     * Retrieves a list of closed admin requests visible to the given user.
     *
     * @param user the user (role-specific filtering could apply)
     * @return list of closed request strings
     */
    public List<String> getClosedAdminRequests(User user) {
        // TODO: Query closed requests
        return new ArrayList<>();
    }
	
	  /**
     * Retrieves a list of non-reviewer role requests (Admin, Student, Instructor, Staff).
     *
     * @return a list of NewRoleRequest objects excluding reviewer requests
     */
	 public List<InstructorReviewerRequests.NewRoleRequest> getNonReviewerRoleRequests() {
	        List<InstructorReviewerRequests.NewRoleRequest> requests = new ArrayList<>();
	        String query = "SELECT id, username, requested_role FROM role_requests " +
	                       "WHERE requested_role IN ('Admin', 'Student', 'Instructor', 'Staff') AND status = 'pending'";
	        try (Connection conn = getConnection();
	             PreparedStatement stmt = conn.prepareStatement(query);
	             ResultSet rs = stmt.executeQuery()) {

	            while (rs.next()) {
	                int id = rs.getInt("id");
	                String username = rs.getString("username");
	                String role = rs.getString("requested_role");
	                requests.add(new InstructorReviewerRequests.NewRoleRequest(id, username, role));
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return requests;
	    }
	 
	 /**
	     * Establishes and returns a connection to the database.
	     * 
	     * @return a Connection object
	     * @throws SQLException if a database access error occurs
	     */
	    private Connection getConnection() throws SQLException {
	        String url = "jdbc:sqlite:forum_database.db"; // Update this with your actual DB path or credentials
	        return DriverManager.getConnection(url);
	    }
	    
	    /**
		 * Counts the number of unread messages sent to the given user
		 * 
		 * @param user	User object storing the selected recipient's information
		 * @return	int count of the number of unread messages sent to the given user
		 */
		public int countUnreadInstructorPrivateMessages(User user) {
			int unreadCount = 0;
			String userName = user.getUserName();
			String query = "SELECT COUNT(*) AS unreadCount FROM instructorMessages WHERE recipientUserName = ? AND isMessageRead = FALSE";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	            pstmt.setString(1, userName);
	            ResultSet rs = pstmt.executeQuery();
	            if (rs.next()) {
	                unreadCount = rs.getInt("unreadCount");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return unreadCount;
		}
		
		public int addInstructorPrivateMessage(InstructorMessage message) {
			String query = "INSERT INTO instructorMessages (senderEmail, senderUserName, senderFirstName, senderLastName, senderRole, messageSubject, messageBody, isMessageRead, recipientEmail, recipientUserName, recipientFirstName, recipientLastName, recipientRole, timeSent) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			int instructorPrivateMessageIDGenerated = -1;
			try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
				pstmt.setString(1, message.sender().getEmail());
				pstmt.setString(2, message.sender().getUserName());
				pstmt.setString(3, message.sender().getFirstName());
				pstmt.setString(4, message.sender().getLastName());
				pstmt.setString(5, message.senderRoleString());
				pstmt.setString(6, message.subjectText());
				pstmt.setString(7, message.messageText());
				pstmt.setBoolean(8, message.isMessageRead());
				pstmt.setString(9, message.recipient().getEmail());
				pstmt.setString(10, message.recipient().getUserName());
				pstmt.setString(11, message.recipient().getFirstName());
				pstmt.setString(12, message.recipient().getLastName());
				pstmt.setString(13, message.recipientRoleString());
				pstmt.setTimestamp(14, Timestamp.valueOf(message.timeSent()));
				pstmt.executeUpdate();
				try (ResultSet rs = pstmt.getGeneratedKeys()) { // retrieve primary key "messageID" generated by INSERT above
					if (rs.next()) {
						instructorPrivateMessageIDGenerated = rs.getInt(1);
						message.setMessageID(instructorPrivateMessageIDGenerated);
					}
				}
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
			return instructorPrivateMessageIDGenerated;		
		}
		
		/**
		 * Marks the given message as read in the database
		 * 
		 * @param message	Object representing the given message
		 */
		public void markInstructorMessageAsRead(InstructorMessage message) {
			String query = "UPDATE instructorMessages SET isMessageRead = TRUE WHERE messageID = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setInt(1, message.messageID());
				int rowsAffected = pstmt.executeUpdate();
				connection.commit();
				if (rowsAffected > 0) {
					message.markRead();
				}
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Retrieves all messages sent to a given user from the database
		 * 
		 * @param userName	userName of the recipient of the messages
		 * @return	ArrayList of InstructorMessages sent to the given user
		 */
		public ArrayList<InstructorMessage> getAllReceivedInstructorPrivateMessages(String userName) {
			ArrayList<InstructorMessage> allInstructorPrivateMessages = new ArrayList<>();
			String query = "SELECT messageID, senderFirstName, senderLastName, senderEmail, senderUserName, senderRole, messageSubject, messageBody, isMessageRead, isDeletedInbox, timeSent FROM instructorMessages WHERE recipientUserName = ?";
				try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			        pstmt.setString(1, userName);
			        ResultSet rs = pstmt.executeQuery();
			        while (rs.next()) {
			        	int messageID = rs.getInt("messageID");
			        	String senderFirstName = rs.getString("senderFirstName");
			        	String senderLastName = rs.getString("senderLastName");
			        	String senderEmail = rs.getString("senderEmail");
			        	String senderUserName  = rs.getString("senderUserName");
			        	String senderRole = rs.getString("senderRole");
			        	String messageSubject = rs.getString("messageSubject");
			        	String messageBody = rs.getString("messageBody");
			        	boolean isMessageRead = rs.getBoolean("isMessageRead");
			        	boolean isDeletedInbox = rs.getBoolean("isDeletedInbox");
			        	Timestamp sentTime = rs.getTimestamp("timeSent");
			        	
			        	InstructorMessage instructorMessagesObj = new InstructorMessage(messageID, senderUserName, senderFirstName, senderLastName, senderEmail, senderRole, messageSubject, messageBody, isMessageRead, sentTime.toLocalDateTime(), isDeletedInbox);
			        	allInstructorPrivateMessages.add(instructorMessagesObj);
			        }
			    } 
				catch (SQLException e) {
			        e.printStackTrace();
			    }
			return allInstructorPrivateMessages;
		}
		
		/**
		 * Retrieves all messages sent to a given user that they have not deleted from their inbox from the database
		 * 
		 * @param userName	userName of the recipient of the messages
		 * @return	ArrayList of InstructorMessages sent to the given user that have not been deleted from their inbox
		 */
		public ArrayList<InstructorMessage> getAllReceivedInstructorPrivateMessagesWithoutHidden(String userName) {
			ArrayList<InstructorMessage> allInstructorPrivateMessages = new ArrayList<>();
			String query = "SELECT messageID, senderFirstName, senderLastName, senderEmail, senderUserName, senderRole, messageSubject, messageBody, isMessageRead, isDeletedInbox, timeSent FROM instructorMessages WHERE recipientUserName = ? AND isDeletedInbox = false";
				try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			        pstmt.setString(1, userName);
			        ResultSet rs = pstmt.executeQuery();
			        while (rs.next()) {
			        	int messageID = rs.getInt("messageID");
			        	String senderFirstName = rs.getString("senderFirstName");
			        	String senderLastName = rs.getString("senderLastName");
			        	String senderEmail = rs.getString("senderEmail");
			        	String senderUserName  = rs.getString("senderUserName");
			        	String senderRole = rs.getString("senderRole");
			        	String messageSubject = rs.getString("messageSubject");
			        	String messageBody = rs.getString("messageBody");
			        	boolean isMessageRead = rs.getBoolean("isMessageRead");
			        	boolean isDeletedInbox = rs.getBoolean("isDeletedInbox");
			        	Timestamp sentTime = rs.getTimestamp("timeSent");
			        	
			        	InstructorMessage instructorMessagesObj = new InstructorMessage(messageID, senderUserName, senderFirstName, senderLastName, senderEmail, senderRole, messageSubject, messageBody, isMessageRead, sentTime.toLocalDateTime(), isDeletedInbox);
			        	allInstructorPrivateMessages.add(instructorMessagesObj);
			        }
			    } 
				catch (SQLException e) {
			        e.printStackTrace();
			    }
			return allInstructorPrivateMessages;
		}
		
		/**
		 * Retrieves all messages sent by a given instructor
		 * 
		 * @param userName	userName of the sender of the messages
		 * @return	ArrayList of InstructorMessages sent by the given user
		 */
		public ArrayList<InstructorMessage> getAllPrivateMessagesSentFromInstructor(String userName) {
			ArrayList<InstructorMessage> allMessagesSentInstructor = new ArrayList<>();
			String query = "SELECT messageID, recipientFirstName, recipientLastName, recipientEmail, recipientUserName, recipientRole, messageSubject, messageBody, isMessageRead, timeSent FROM instructorMessages where senderUserName = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, userName);
		        ResultSet rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	int messageID = rs.getInt("messageID");
		        	String recipientFirstName = rs.getString("recipientFirstName");
		        	String recipientLastName = rs.getString("recipientLastName");
		        	String recipientEmail = rs.getString("recipientEmail");
		        	String recipientUserName = rs.getString("recipientUserName");
		        	String recipientRole = rs.getString("recipientRole");
		        	String messageSubject = rs.getString("messageSubject");
		        	String messageBody = rs.getString("messageBody");
		        	boolean isMessageRead = rs.getBoolean("isMessageRead");
		        	Timestamp timeSent = rs.getTimestamp("timeSent");

		        	InstructorMessage instructorMessagesObj = new InstructorMessage(messageID, timeSent.toLocalDateTime(), recipientFirstName, recipientLastName, recipientEmail, recipientUserName, recipientRole, messageSubject, messageBody, isMessageRead);
		        	allMessagesSentInstructor.add(instructorMessagesObj);
		        }
			}
			catch (SQLException e) {
		        e.printStackTrace();
		    }
			return allMessagesSentInstructor;
		}
		
		/**
		 * Marks the given instructor message as deleted in the recipient's inbox, meaning it will not show up there
		 * 
		 * @param messageID	ID of the message to be marked deleted
		 */
		public void deleteInstructorPrivateMessageFromInbox(int messageID) {
			String query = "UPDATE instructorMessages SET isDeletedInbox = TRUE WHERE messageID = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setInt(1, messageID);
				pstmt.executeUpdate();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Deletes the given message from the database
		 * 
		 * @param messageID	ID of the message to be deleted
		 */
		public void deleteInstructorPrivateMessage(int messageID) {
			String query = "DELETE FROM instructorMessages WHERE messageID = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setInt(1, messageID);
				pstmt.executeUpdate();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Retrieves all the messages sent to the current Staff user except those delete by them.
		 * 
		 * @param userName the userName of the current Staff user
		 * @return an ArrayList containing all the messages where the recipientUserName is the userName of the current Staff user
		 */
		public ArrayList<StaffMessage> getAllReceivedStaffPrivateMessagesWithoutHidden(String userName) { 
			ArrayList<StaffMessage> allStaffPrivateMessages = new ArrayList<>();
			String query = "SELECT messageID, senderFirstName, senderLastName, senderEmail, senderUserName, senderRole, messageSubject, messageBody, isMessageRead, isDeletedInbox, timeSent, isRepliedTo FROM staffMessages WHERE recipientUserName = ? AND isDeletedInbox = false";
				try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			        pstmt.setString(1, userName);
			        ResultSet rs = pstmt.executeQuery();
			        while (rs.next()) {
			        	int messageID = rs.getInt("messageID");
			        	String senderFirstName = rs.getString("senderFirstName");
			        	String senderLastName = rs.getString("senderLastName");
			        	String senderEmail = rs.getString("senderEmail");
			        	String senderUserName  = rs.getString("senderUserName");
			        	String senderRole = rs.getString("senderRole");
			        	String messageSubject = rs.getString("messageSubject");
			        	String messageBody = rs.getString("messageBody");
			        	boolean isMessageRead = rs.getBoolean("isMessageRead");
			        	boolean isDeletedInbox = rs.getBoolean("isDeletedInbox");
			        	Timestamp sentTime = rs.getTimestamp("timeSent");
			        	boolean isRepliedTo = rs.getBoolean("isRepliedTo");
			        	
			        	StaffMessage staffMessagesObj = new StaffMessage(messageID, senderUserName, senderFirstName, senderLastName, senderEmail, senderRole, messageSubject, messageBody, isMessageRead, sentTime.toLocalDateTime(), isDeletedInbox, isRepliedTo);
			        	allStaffPrivateMessages.add(staffMessagesObj);
			        }
			    } 
				catch (SQLException e) {
			        e.printStackTrace();
			    }
			return allStaffPrivateMessages;
		}
		
		/**
		 * Marks a message of the given ID as replied to by another message
		 * 
		 * @param staffPrivateMessageID	The messageID to be marked
		 */
		public void markStaffMessageReplied(int staffPrivateMessageID) {
			String query = "UPDATE staffMessages SET isRepliedTo = TRUE WHERE messageID = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setInt(1, staffPrivateMessageID);
				pstmt.executeUpdate();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Hides a message received by the current Staff member from another user who holds the Staff or Instructor role from their inbox.
		 * 
		 * @param staffPrivateMessageID the messageID to be hidden
		 */
		public void deleteStaffPrivateMessageFromInbox(int staffPrivateMessageID) {
			String query = "UPDATE staffMessages SET isDeletedInbox = TRUE WHERE messageID = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setInt(1, staffPrivateMessageID);
				pstmt.executeUpdate();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		  /**
	     * Retrieves a list of all usernames that are currently muted.
	     *
	     * @return a List of usernames for users whose isMuted flag is TRUE
	     * @throws SQLException if a database access error occurs
	     */
		
		public ArrayList<String> getMutedUsers() throws SQLException {
			  ArrayList<String> users = new ArrayList<String>();
			  String sql = "SELECT userName FROM cse360users WHERE isMuted = TRUE";
			  try (PreparedStatement p = connection.prepareStatement(sql);
			       ResultSet rs = p.executeQuery()) {
			    while (rs.next()) {
			      users.add(rs.getString("userName"));
			    }
			  }catch(SQLException e) {
				  e.printStackTrace();
			  }
			  return users;
			}
		
		/**
	     * Retrieves a list of muted usernames filtered by a specific user role.
	     *
	     * @param role the role to filter muted users by (e.g., "student", "instructor", "reviewer")
	     * @return a List of usernames for users whose isMuted flag is TRUE and whose role matches the given value
	     * @throws SQLException if a database access error occurs
	     */
		
		public List<String> getMutedUsersByRole(String role) throws SQLException {
		    List<String> users = new ArrayList<>();
		    String sql = "SELECT userName FROM cse360users WHERE isMuted = TRUE AND role = ?";
		    try (PreparedStatement p = connection.prepareStatement(sql)) {
		        p.setString(1, role);
		        try (ResultSet rs = p.executeQuery()) {
		            while (rs.next()) {
		                users.add(rs.getString("userName"));
		            }
		        }
		    }
		    return users;
		}
		
		
		 /**
	     * Unmutes a user so they can post again by:
	     * 1) Setting their isMuted flag to FALSE in the users table.
	     * 2) Un-hiding any content (questions, replies, answers, reviews) that was hidden while they were muted.
	     *
	     * @param userName the username of the user to unmute
	     */
		public void unmuteUser(String userName) {
			  // 1) flip the bit
			  try ( PreparedStatement p = connection.prepareStatement(
			         "UPDATE cse360users SET isMuted = FALSE WHERE userName = ?"
			       ) ) {
			    p.setString(1, userName);
			    p.executeUpdate();
			  } catch(SQLException e) {
			    e.printStackTrace();
			  }
			  // 2) now un-hide everything we hid when we muted them
			  clearHiddenQuestionsForUser(userName);
			  clearHiddenRepliesForUser(userName);
			  clearHiddenAnswersForUser(userName);
			  clearHiddenReviewsForUser(userName);
			}
		
		/**
	     * Clears the flagged status and reason for a specific question.
	     *
	     * @param questionID the ID of the question whose flag should be cleared
	     * @return true if flag was cleared, false if not
	     */
		
		public boolean clearQuestionFlag(int questionID) {
			boolean wasFlagCleared = false;
			String sql = "UPDATE questions SET isFlagged = FALSE, reasonIsFlagged = '' WHERE questionID = ?";
			try (PreparedStatement p = connection.prepareStatement(sql)) {
				p.setInt(1, questionID);
				int rowsAffected = p.executeUpdate();
				if (rowsAffected > 0) {
					wasFlagCleared = true;
				}
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
			return wasFlagCleared;
			}
		
		/**
	     * Un-hides a question by setting its isHidden flag to FALSE.
	     *
	     * @param questionID the ID of the question to unhide
	     * @return true if question is now unhidden, false if not
	     */
		public boolean clearHiddenQuestion(int questionID) {
			boolean isNowUnHidden = false;
		    String sql = "UPDATE questions SET isHidden = ? WHERE questionID = ?";
		    try (PreparedStatement p = connection.prepareStatement(sql)) {
		        p.setBoolean(1, false);
		        p.setInt(2, questionID);
		        int rowsAffected = p.executeUpdate();
		        if (rowsAffected > 0) {
		        	isNowUnHidden = true;
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		    return isNowUnHidden;
		}
		
		/**
	     * Un-hides an answer by setting its isHidden flag to FALSE.
	     *
	     * @param answerID the ID of the answer to unhide
	     * @return true if answer is now unhidden, false if not
	     */
		public boolean clearHiddenAnswer(int answerID) {
			boolean isNowUnHidden = false;
		    String sql = "UPDATE answers SET isHidden = ? WHERE answerID = ?";
		    try (PreparedStatement p = connection.prepareStatement(sql)) {
		        p.setBoolean(1, false);
		        p.setInt(2, answerID);
		        int rowsAffected = p.executeUpdate();
		        if (rowsAffected > 0) {
		        	isNowUnHidden = true;
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		    return isNowUnHidden;
		}
		
		/**
	     * Un-hides a review by setting its isHidden flag to FALSE.
	     *
	     * @param reviewID the ID of the review to unhide
	     * @return true if review was unhidden, false if not
	     */
		
		public boolean clearHiddenReview(int reviewID) {
			boolean isNowUnHidden = false;
			String sql = "UPDATE reviews SET isHidden = ? WHERE reviewID = ?";
			try (PreparedStatement p = connection.prepareStatement(sql)) {
		        p.setBoolean(1, false);
		        p.setInt(2, reviewID);
		        int rowsAffected = p.executeUpdate();
		        if (rowsAffected > 0) {
		        	isNowUnHidden = true;
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
			return isNowUnHidden;
		}
		
		/**
	     * Clears the flagged status and reason for a specific question reply.
	     *
	     * @param replyID the ID of the reply whose flag should be cleared
	     */
		public void clearQuestionReplyFlag(int replyID) {
			String sql ="UPDATE questionReplies SET isFlagged = FALSE, reasonIsFlagged = '' WHERE replyID = ?";
			try (PreparedStatement p = connection.prepareStatement(sql)){
				p.setInt(1, replyID);
				p.executeUpdate();
			} catch(SQLException e) {
				e.printStackTrace();
			}}
		
		
		/**
	     * Clears the flagged status and reason for a specific answer.
	     *
	     * @param answerID the ID of the answer whose flag should be cleared
	     * @return true if flag was cleared. false if not
	     */
		public boolean clearAnswerFlag(int answerID) {
			boolean wasFlagCleared = false;
			String sql = "UPDATE answers SET isFlagged = FALSE, reasonIsFlagged = '' WHERE answerID = ?";
			try(PreparedStatement p = connection.prepareStatement(sql)){
				p.setInt(1, answerID);
				int rowsAffected = p.executeUpdate();
				if (rowsAffected > 0) {
					wasFlagCleared = true;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return wasFlagCleared;
		}
		
		
		/**
	     * Clears the flagged status and reason for a specific review.
	     *
	     * @param reviewID the ID of the review whose flag should be cleared
	     * @return true if flag was cleared, false if not
	     */
		public boolean clearReviewFlag(int reviewID) {
			boolean wasFlagCleared = false;
			String sql = "UPDATE reviews SET isFlagged = FALSE, reasonIsFlagged = '' WHERE reviewID = ?";
			try(PreparedStatement p = connection.prepareStatement(sql)){
				p.setInt(1, reviewID);
				int rowsAffected = p.executeUpdate();
				if (rowsAffected > 0) {
					wasFlagCleared = true;
				}
			} catch (SQLException e){
				e.printStackTrace();
			}
			return wasFlagCleared;
		}
		
		
		/**
	     * Un-hides all questions posted by the given user.
	     *
	     * @param userName the username whose questions should be unhidden
	     * @return true if questions were unhidden, false if not
	     */
		public boolean clearHiddenQuestionsForUser(String userName) {
			boolean isNowUnHidden = false;
			  String sql = "UPDATE questions SET isHidden = FALSE WHERE studentUserName = ?";
			  try (PreparedStatement p = connection.prepareStatement(sql)) {
			    p.setString(1, userName);
			    int rowsAffected = p.executeUpdate();
		        if (rowsAffected > 0) {
		        	isNowUnHidden = true;
		        }
			  } catch (SQLException e) {
			    e.printStackTrace();
			  }
			  return isNowUnHidden;
			}
		
		/**
	     * Un-hides all replies posted by the given user.
	     *
	     * @param userName the username whose question replies should be unhidden
	     * @return true if question replies were unhidden, false if not
	     */
		public boolean clearHiddenRepliesForUser(String userName) {
		  boolean isNowUnHidden = false;
		  String sql = "UPDATE questionReplies SET isHidden = FALSE WHERE studentUserName = ?";
		  try (PreparedStatement p = connection.prepareStatement(sql)) {
		    p.setString(1, userName);
		    int rowsAffected = p.executeUpdate();
	        if (rowsAffected > 0) {
	        	isNowUnHidden = true;
	        }
		  } catch (SQLException e) {
		    e.printStackTrace();
		  }
		  return isNowUnHidden;
		}
		
		
		/**
	     * Un-hides all answers posted by the given user.
	     *
	     * @param userName the username whose answers should be unhidden
	     * @return true if answers were unhidden, false if not
	     */
		public boolean clearHiddenAnswersForUser(String userName) {
			boolean isNowUnHidden = false;
		  String sql = "UPDATE answers SET isHidden = FALSE WHERE studentUserName = ?";
		  try (PreparedStatement p = connection.prepareStatement(sql)) {
		    p.setString(1, userName);
		    int rowsAffected = p.executeUpdate();
	        if (rowsAffected > 0) {
	        	isNowUnHidden = true;
	        }
		  } catch (SQLException e) {
		    e.printStackTrace();
		  }
		  return isNowUnHidden;
		}
		
		/**
	     * Un-hides all reviews made by the given reviewer.
	     *
	     * @param userName the username of the reviewer whose reviews should be unhidden
	     * @return true if reviews were unhidden, false if not
	     */
		public boolean clearHiddenReviewsForUser(String userName) {
		  boolean isNowUnHidden = false;
		  String sql = "UPDATE reviews SET isHidden = FALSE WHERE reviewerUserName = ?";
		  try (PreparedStatement p = connection.prepareStatement(sql)) {
		    p.setString(1, userName);
		    int rowsAffected = p.executeUpdate();
	        if (rowsAffected > 0) {
	        	isNowUnHidden = true;
	        }
		  } catch (SQLException e) {
		    e.printStackTrace();
		  }
		  return isNowUnHidden;
		}
		
		/**
	     * Clears the flagged status and reason for a specific private message in the reviewer inbox.
	     *
	     * @param senderUserName the sender userName
	     * @param receiverUserName the recipient userName
	     * @param timeSent time original message sent
	     * @param messageSubject the subject of the message
	     * @param messageBody the message body of the message
	     */
		public void clearReviewerPrivateMessageFlag(String senderUserName,
                	String receiverUserName,
                	LocalDateTime timeSent,
                	String messageSubject,
                	String messageBody) {
					String sql =
							"UPDATE reviewerMessages " +
							"SET isFlagged = FALSE, reasonIsFlagged = NULL " +
							"WHERE sender    = ? " +
							"  AND recipient = ? " +
							"  AND subject   = ? " +
							"  AND body      = ?";
				try (PreparedStatement p = connection.prepareStatement(sql)) {
						p.setString(1, senderUserName);
						p.setString(2, receiverUserName);
						p.setString(3, messageSubject);
						p.setString(4, messageBody);
						p.executeUpdate();
						connection.commit();
						} catch (SQLException e) {
					e.printStackTrace();
			  }
		}
		
		/**
		 * Clears (unflags) a Student→Student private message.
		 *
		 * @param to the username of the recipient
		 * @param from   the username of the sender
		 * @param timestamp         the timestamp when the message was sent
		 * @param subject   the subject of the message
		 * @param body      the body of the message
		 * 
		 */
		public void clearStudentPrivateMessageFlag(
			    String to,
			    String from,
			    LocalDateTime timestamp,
			    String subject,
			    String body
			) {
			    String sql =
			      "UPDATE PrivateMessages " +
			      "SET isFlagged = FALSE, reasonIsFlagged = NULL " +
			      "WHERE sender_user_name    = ? " +
			      "  AND receiver_user_name  = ? " +
			      "  AND timestamp = ? " +
			      "  AND subject   = ? " +
			      "  AND message_body      = ?";
			    try (PreparedStatement p = connection.prepareStatement(sql)) {
			      p.setString(1, from);                // the actual sender
			      p.setString(2, to);                  // the actual receiver
			      p.setTimestamp(3, Timestamp.valueOf(timestamp));
			      p.setString(4, subject);
			      p.setString(5, body);
			      p.executeUpdate();
			    } catch (SQLException e) {
			      e.printStackTrace();
			    }
			}
		
		 /**
	     * Retrieves all flagged student↔student private messages from the database
	     * and returns them as a list of formatted strings suitable for display in the UI.
	     * <p>
	     * Each entry in the returned list is formatted as five lines:
	     * <pre>
	     * To [Student]: &lt;recipient username&gt;
	     * From [Student]: &lt;sender username&gt;
	     * Date: &lt;formatted timestamp (MMM dd, yyyy hh:mm a)&gt;
	     * Message Subject: &lt;subject&gt;
	     * Message Body: &lt;body&gt;
	     * </pre>
	     *
	     * @return a {@code List<String>} where each element is one flagged
	     *         student↔student private message formatted as described above
	     * @throws SQLException if a database access error occurs while fetching the messages
	     */
		public List<String> getFlaggedStudentPrivateMessages() throws SQLException {
		    List<String> rows = new ArrayList<>();

		    // Note: use "PrivateMessages" (your table name) and the exact column names
		    String sql = """
		        SELECT
		            receiver_user_name AS toUser,
		            sender_user_name   AS fromUser,
		            timestamp,
		            subject,
		            message_body       AS body
		          FROM PrivateMessages
		         WHERE isFlagged = TRUE
		      ORDER BY timestamp DESC
		    """;

		    // reuse your existing formatter or define one here
		    DateTimeFormatter msgFmt =
		        DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");

		    try (PreparedStatement p = connection.prepareStatement(sql);
		         ResultSet rs = p.executeQuery()) {

		        while (rs.next()) {
		            String to      = rs.getString("toUser");
		            String from    = rs.getString("fromUser");
		            LocalDateTime ts = rs.getTimestamp("timestamp")
		                                 .toLocalDateTime();
		            String dateStr = ts.format(msgFmt);
		            String subj    = rs.getString("subject");
		            String body    = rs.getString("body");

		            // build the same 5-line format your UI expects:
		            String formatted =
		                "To [Student]: "     + to      + "\n" +
		                "From [Student]: "   + from    + "\n" +
		                "Date: "             + dateStr + "\n" +
		                "Message Subject: "  + subj    + "\n" +
		                "Message Body: "     + body;

		            rows.add(formatted);
		        }
		    }

		    return rows;
		}


		/**
	     * Retrieves all flagged reviewer↔student private messages from the database
	     * and returns them as a list of formatted strings suitable for display in the UI.
	     * <p>
	     * Each entry in the returned list is formatted as five lines:
	     * <pre>
	     * To [role]: &lt;recipient username&gt;
	     * From [Student]: &lt;sender username&gt;
	     * Date: &lt;formatted timestamp (MMM dd, yyyy hh:mm a)&gt;
	     * Message Subject: &lt;subject&gt;
	     * Message Body: &lt;body&gt;
	     * </pre>
	     *
	     * @return a {@code List<String>} where each element is one flagged reviewer↔student
	     *         message formatted as described above
	     * @throws SQLException if a database access error occurs while fetching the messages
	     */
		public List<String> getFlaggedReviewerPrivateMessages() throws SQLException {
		    List<String> rows = new ArrayList<>();

		    // Alias columns so your ResultSet.getXXX names line up with your parser:
		    String sql = """
		        SELECT
		            recipientRole AS toRole,
		            sender        AS fromUser,
		            recipient     AS toUser,
		            sentTime      AS timestamp,
		            subject,
		            body
		          FROM reviewerMessages
		         WHERE isFlagged = TRUE
		      ORDER BY sentTime DESC
		    """;

		    DateTimeFormatter msgFmt =
		        DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");

		    try (PreparedStatement p = connection.prepareStatement(sql);
		         ResultSet rs = p.executeQuery()) {

		        while (rs.next()) {
		            String toRole   = rs.getString("toRole");
		            String fromUser = rs.getString("fromUser");
		            String toUser   = rs.getString("toUser");
		            LocalDateTime ts = rs.getTimestamp("timestamp")
		                                 .toLocalDateTime();
		            String dateStr  = ts.format(msgFmt);
		            String subj     = rs.getString("subject");
		            String body     = rs.getString("body");

		            String formatted =
		                "To ["   + toRole   + "]: " + toUser   + "\n" +
		                "From [Student]: "    + fromUser + "\n" +
		                "Date: "              + dateStr  + "\n" +
		                "Message Subject: "   + subj     + "\n" +
		                "Message Body: "      + body;

		            rows.add(formatted);
		        }
		    }

		    return rows;
		}
		
		
		/**
	     * Retrieves all student↔student private messages from the database,
	     * formatted exactly as shown in the UI so that each raw message string
	     * can be mapped back to its database ID for deletion or other operations.
	     *
	     * <p>Each entry in the returned map has:
	     * <ul>
	     *   <li>Key: the multi-line raw string:
	     *       <pre>
	     *       To [Student]: &lt;receiver_user_name&gt;
	     *       From [Student]: &lt;sender_user_name&gt;
	     *       Date: &lt;formatted timestamp&gt;
	     *       Message Subject: &lt;subject&gt;
	     *       Message Body: &lt;body&gt;
	     *       </pre>
	     *   </li>
	     *   <li>Value: the corresponding {@code messageID} from the
	     *       {@code PrivateMessages} table.
	     *   </li>
	     * </ul>
	     *
	     * @return a {@code Map<String,Integer>} mapping each formatted message string
	     *         to its {@code messageID} in the {@code PrivateMessages} table
	     * @throws SQLException if an error occurs while querying the database
	     */
		public Map<String,Integer> getAllStudentMessagesWithIds() throws SQLException {
		    Map<String,Integer> map = new LinkedHashMap<>();
		    String sql = """
		        SELECT messageID, receiver_user_name, sender_user_name,
		               timestamp, subject, message_body
		          FROM PrivateMessages
		      ORDER BY timestamp DESC
		    """;
		    try (PreparedStatement p = connection.prepareStatement(sql);
		         ResultSet rs = p.executeQuery()) {
		        while (rs.next()) {
		            int id        = rs.getInt("messageID");
		            String to     = rs.getString("receiver_user_name");
		            String from   = rs.getString("sender_user_name");
		            LocalDateTime ts = rs.getTimestamp("timestamp").toLocalDateTime();
		            String date   = ts.format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a"));
		            String subj   = rs.getString("subject");
		            String body   = rs.getString("message_body");

		            String raw = String.join("\n",
		                "To [Student]: "    + to,
		                "From [Student]: "  + from,
		                "Date: "            + date,
		                "Message Subject: " + subj,
		                "Message Body: "    + body
		            );

		            map.put(raw, id);
		        }
		    }
		    return map;
		}

		
	    /**
	     * Retrieves all flagged student↔student private messages from the database.
	     * Each entry maps the exact raw‐format string (as shown in the UI) to its
	     * {@code messageID}, preserving insertion order (newest first).
	     *
	     * @return a {@code Map<String,Integer>} where each key is the formatted
	     *         message (To/From/Date/Subject/Body) and the value is its
	     *         {@code messageID} in the {@code PrivateMessages} table
	     * @throws SQLException if a database access error occurs while querying
	     */
		
		public Map<String,Integer> getFlaggedStudentMessagesWithIds() throws SQLException {
		    Map<String,Integer> map = new LinkedHashMap<>();
		    String sql = """
		        SELECT messageID, receiver_user_name, sender_user_name,
		               timestamp, subject, message_body
		          FROM PrivateMessages
		         WHERE isFlagged = TRUE
		      ORDER BY timestamp DESC
		    """;
		    // …same loop, building `raw` and `map.put(raw,id)`…
		    return map;
		}

		 /**
	     * Retrieves all reviewer↔student private messages from the database.
	     * Each entry maps the exact raw‐format string (as shown in the UI) to its
	     * {@code messageID}, preserving insertion order (newest first).
	     *
	     * @return a {@code Map<String,Integer>} where each key is the formatted
	     *         message (To/From/Date/Subject/Body) and the value is its
	     *         {@code messageID} in the {@code reviewerMessages} table
	     * @throws SQLException if a database access error occurs while querying
	     */
		public Map<String,Integer> getAllReviewerMessagesWithIds() throws SQLException {
		    Map<String,Integer> map = new LinkedHashMap<>();
		    String sql = """
		        SELECT messageID,
		               sender        AS fromUser,
		               recipient     AS toUser,
		               recipientRole AS toRole,
		               sentTime,
		               subject,
		               body
		          FROM reviewerMessages
		      ORDER BY sentTime DESC
		    """;
		    try (PreparedStatement p = connection.prepareStatement(sql);
		         ResultSet rs = p.executeQuery()) {
		        while (rs.next()) {
		            int id              = rs.getInt("messageID");
		            String fromUser     = rs.getString("fromUser");
		            String toUser       = rs.getString("toUser");
		            String toRole       = rs.getString("toRole");
		            LocalDateTime ts    = rs.getTimestamp("sentTime")
		                                     .toLocalDateTime();
		            String date         = ts.format(
		                DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a")
		            );
		            String subj         = rs.getString("subject");
		            String body         = rs.getString("body");

		            // build the same raw string your cell-factory expects
		            String raw = String.join("\n",
		                "To ["   + toRole + "]: "           + toUser,
		                "From [" + (toRole.equals("Reviewer") ? "Student" : "Reviewer") +
		                          "]: "         + fromUser,
		                "Date: "                          + date,
		                "Message Subject: "               + subj,
		                "Message Body: "                  + body
		            );
		            map.put(raw, id);
		        }
		    }
		    return map;
		}

		/**
	     * Retrieves only the flagged reviewer↔student private messages from the database.
	     * Each entry maps the exact raw‐format string (as shown in the UI) to its
	     * {@code messageID}, preserving insertion order (newest first).
	     *
	     * @return a {@code Map<String,Integer>} where each key is the formatted
	     *         flagged message (To/From/Date/Subject/Body) and the value is its
	     *         {@code messageID} in the {@code reviewerMessages} table
	     * @throws SQLException if a database access error occurs while querying
	     */
		public Map<String,Integer> getFlaggedReviewerMessagesWithIds() throws SQLException {
		    Map<String,Integer> map = new LinkedHashMap<>();
		    String sql = """
		        SELECT messageID,
		               sender        AS fromUser,
		               recipient     AS toUser,
		               recipientRole AS toRole,
		               sentTime,
		               subject,
		               body
		          FROM reviewerMessages
		         WHERE isFlagged = TRUE
		      ORDER BY sentTime DESC
		    """;
		    try (PreparedStatement p = connection.prepareStatement(sql);
		         ResultSet rs = p.executeQuery()) {
		        while (rs.next()) {
		            int id           = rs.getInt("messageID");
		            String fromUser  = rs.getString("fromUser");
		            String toUser    = rs.getString("toUser");
		            String toRole    = rs.getString("toRole");
		            LocalDateTime ts = rs.getTimestamp("sentTime").toLocalDateTime();
		            String date      = ts.format(
		                DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a")
		            );
		            String subj      = rs.getString("subject");
		            String body      = rs.getString("body");

		            String raw = String.join("\n",
		                "To ["   + toRole + "]: " + toUser,
		                "From [" + (toRole.equals("Reviewer") ? "Student" : "Reviewer") +
		                          "]: " + fromUser,
		                "Date: "            + date,
		                "Message Subject: " + subj,
		                "Message Body: "    + body
		            );
		            map.put(raw, id);
		        }
		    }
		    return map;
		}
		
		/**
		 * Retrieves ALL role‐requests for a given user.
		 * 
		 * @param userID the userID of the specific user
		 * @return a List of String-arrays, each array = { userName, fullName, roleFlags, requestStatus }
		 */
			public List<String[]> getRoleRequestsForUser(int userID) {
		    List<String[]> rows = new ArrayList<>();
		    String sql =
		      "SELECT userName, "
		    + "       userFirstName || ' ' || userLastName AS fullName, "
		    + "       role, requestStatus "
		    + "  FROM newRoleRequests "
		    + " WHERE userID = ?";
		    try (PreparedStatement p = connection.prepareStatement(sql)) {
		        p.setInt(1, userID);
		        try (ResultSet rs = p.executeQuery()) {
		            while (rs.next()) {
		                rows.add(new String[]{
		                  rs.getString("userName"),
		                  rs.getString("fullName"),
		                  rs.getString("role"),         // still the “[false,false,true,…]”
		                  rs.getString("requestStatus")
		                });
		            }
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		    return rows;
		}
}