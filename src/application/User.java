package application;

import java.util.ArrayList;
import application.Question;
import databasePart1.DatabaseHelper;
import application.Answer;

/**
 * The {@code User} class represents a user in the system, holding personal information, 
 * credentials, roles, and associations with submitted questions and answers.
 * <p>
 * It also provides access to user-submitted content via database interactions.
 * </p>
 * 
 * @author Kylie Kim
 * @version 3.0
 * @since 2025-03-27
 */
public class User {
	
	/** The user's unique username. */
    private String userName;

    /** The user's password. */
    private String password;

    /** The user's roles represented as a boolean array. */
    private boolean[] role;

    /** The user's email address. */
    private String email;

    /** The user's first name. */
    private String firstName;

    /** The user's last name. */
    private String lastName;
    
    /** Whether the user is muted and is thus unable to submit any new posts and has all their content hidden*/
    private boolean isMuted;

    /** List of questions submitted by the user. */
    private ArrayList<Question> submittedQuestions = new ArrayList<>();

    /** List of answers submitted by the user. */
    private ArrayList<Answer> submittedAnswers = new ArrayList<>();
  
    /**
     * Constructs a new {@code User} with the specified credentials and personal details.
     * 
     * @param userName  the unique username
     * @param password  the user's password
     * @param role      an array indicating user roles
     * @param email     the user's email
     * @param firstName the user's first name
     * @param lastName  the user's last name
     */
    public User(String userName, String password, boolean[] role, String email, String firstName, String lastName) {
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    /**
     * Constructs a new {@code User} with the specified credentials and personal details. Excludes password. 
     * 
     * @param userName  the unique username
     * @param role      an array indicating user roles
     * @param email     the user's email
     * @param firstName the user's first name
     * @param lastName  the user's last name
     */
    public User(String userName, boolean[] role, String email, String firstName, String lastName) {
    	this.userName = userName;
        this.role = role;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    /**
     * Constructs a new {@code User} with the specified credentials and personal details. Excludes password, but includes isMuted.
     * 
     * @param userName  the unique username
     * @param role      an array indicating user roles
     * @param email     the user's email
     * @param firstName the user's first name
     * @param lastName  the user's last name
     * @param isMuted	the user's isMuted status
     */
    public User(String userName, boolean[] role, String email, String firstName, String lastName, boolean isMuted) {
    	this.userName = userName;
        this.role = role;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isMuted = isMuted;
    }
    
    /**
     * Updates the user's role array.
     * 
     * @param role a new boolean array representing roles
     */
    public void setRole(boolean[] role) {
    	this.role=role;
    }
    
    /**
     * Sets a new password for the user.
     * 
     * @param password the new password string
     */
    public void setPassword(String password) {
    	this.password = password;
    }
    
    /**
     * Prints the user's information to the console (for debugging).
     */
    public void printUser() {
    	System.out.println(this.userName + " " + this.firstName + " " + this.lastName + " " + this.email + " " + this.role);
    }
    
    /**
     * Adds a new question to the list of submitted questions.
     *
     * @param question The Question object to add.
     */
    public void addSubmittedQuestion(Question question) {
        if (question != null) {
            submittedQuestions.add(question);
        }
    }
    
    /**
     * Adds a new answer to the list of submitted answers.
     *
     * @param answer The Answer object to add.
     */
    public void addSubmittedAnswer(Answer answer) {
        if (answer != null) {
            submittedAnswers.add(answer);
        }
    }
    
    /**
     * Returns the list of questions submitted by this user.
     * 
     * @return List of submitted questions.
     */
    public ArrayList<Question> getSubmittedQuestions() {
        return new Questions(new DatabaseHelper(), this).getQuestionsByUserName(this.getUserName());
    }

    /**
     * Returns the list of answers submitted for this user's questions.
     * @return List of submitted answers.
     */
    public ArrayList<Answer> getSubmittedAnswers() {
        return new Answers(new DatabaseHelper(), this).getAnswersForUserQuestions(this.getUserName());
    }

    /**
     * Retrieve User username.
     *  
     * @return the user's username
     */
    public String getUserName() { return userName; }

    /**
     * 	Retrieve User password.
     * 
     *  @return the user's password
     */
    public String getPassword() { return password; }

    /** 
     * Retrieve User roles.
     * 
     * @return the user's role array
     */
    public boolean[] getRole() { return role; }

    /**
     * Retrieve User email address.
     * 
     *  @return the user's email address 
     */
    public String getEmail() { return email; }

    /** 
     * Retrieve User first name.
     * 
     * @return the user's first name
     */
    public String getFirstName() { return firstName; }

    /**
     * Retrieve User last name.
     * 
     * @return the user's last name 
     */
    public String getLastName() { return lastName; }
    
    /**
     * Retrieve whether isMuted status of the user.
     * 
     * @return true if the user is muted, false if not
     */
    public boolean getIsMuted() { return isMuted;}
    
    /**
     * Sets the isMuted status of the user.
     * 
     * @param isMuted new isMuted status
     */
    public void setIsMuted(boolean isMuted) { this.isMuted = isMuted;}
    
    @Override
    public String toString() {
    	return "Username: " + this.getUserName() + ", Name: " + this.getFirstName() + " " + this.getLastName() +
    			", Email: " + this.getUserName();
    }
    
}