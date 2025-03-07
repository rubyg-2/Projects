package application;

import databasePart1.DatabaseHelper;
import javafx.collections.ObservableList;
import java.sql.*;
public class backendQA {
    private DatabaseHelper dbHelper;
    private int currentUserId;

    public backendQA() {
        dbHelper = new DatabaseHelper();
        try {
            dbHelper.connectToDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentUserId = -1;
    }

    // Register a new user.
    public boolean register(String username, String password, String role) {
        try {
            User user = new User(username, password, role);
            dbHelper.register(user);
            // Get the user id after registration.
            currentUserId = dbHelper.getUserId(username, password);
            return true;
        } catch (Exception e) {
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    // Login an existing user.
    public boolean login(String username, String password) {
        try {
            // Create a user instance for login.
            // (If your login method requires a role, you might need to adjust this accordingly.)
            User user = new User(username, password, "");
            boolean loggedIn = dbHelper.login(user);
            if (loggedIn) {
                currentUserId = dbHelper.getUserId(username, password);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            return false;
        }
    }

    // Retrieves the user ID for the given credentials.
    // (Ensure that DatabaseHelper includes a matching getUserId(String, String) method.)
    private int getUserId(String username, String password) {
        try {
            return dbHelper.getUserId(username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Add a new question.
    public int addQuestion(String content) {
        try {
            if (currentUserId == -1) {
                System.err.println("No user logged in.");
                return -1;
            }

            // Check if similar question exists
            String checkQuery = "SELECT COUNT(*) FROM questions WHERE questionText LIKE ?";
            try (PreparedStatement checkStmt = dbHelper.connection.prepareStatement(checkQuery)) {
                checkStmt.setString(1, "%" + content.trim() + "%");
                ResultSet rs = checkStmt.executeQuery();
                rs.next();
                int count = rs.getInt(1);

                if (count > 0) {
                    System.out.println("Unable to add question - similar question exists");
                    return -2; // Indicates similar question exists
                }
            }

            // Add the question to the database
            int questionId = dbHelper.addQuestion(currentUserId, content);

            if (questionId != -1) {
                System.out.println("Question #" + questionId + " added successfully");
                return questionId; // Return the new question ID on success
            } else {
                System.out.println("Unable to add question - database error occurred");
                return -3; // Indicates database error
            }
        } catch (SQLException e) {
            System.err.println("Error adding question: " + e.getMessage());
            return -4; // Indicates SQL exception
        }
    }

   public int addAnswer(int questionId, String answerText) {
        try {
            if (currentUserId == -1) {
                System.err.println("No user logged in.");
                return -1; // No user logged in
            }

            // Check if question exists by trying to get answers for it
            try {
                // Verify the question exists
                String checkQuery = "SELECT COUNT(*) FROM questions WHERE id = ?";
                try (PreparedStatement checkStmt = dbHelper.connection.prepareStatement(checkQuery)) {
                    checkStmt.setInt(1, questionId);
                    ResultSet rs = checkStmt.executeQuery();
                    rs.next();
                    int count = rs.getInt(1);

                    if (count == 0) {
                        System.out.println("Question not found");
                        return -2; // Question not found
                    }
                }

                // Use the dbHelper to add the answer
                int answerId = dbHelper.addAnswer(currentUserId, questionId, answerText);

                if (answerId != -1) {
                    System.out.println("Answer added successfully");
                    return answerId; // Return the new answer ID on success
                } else {
                    System.out.println("Unable to add answer - database error occurred");
                    return -3; // Database error
                }
            } catch (SQLException e) {
                System.out.println("Unable to add answer: " + e.getMessage());
                return -4; // SQL exception when adding answer
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return -5; // General exception
        }
    }

    // Add a reply to either a question or answer.
    public int addReply(int parentId, String parentType, String replyText) {
        if (currentUserId == -1) {
            System.err.println("No user logged in.");
            return -1;
        }
        try {
            return dbHelper.addReply(currentUserId, parentType, parentId, replyText);
        } catch (Exception e) {
            System.err.println("Error adding reply: " + e.getMessage());
            return -1;
        }
    }

    // Delete a question.
    public boolean deleteQuestion(int questionId) {
        try {
            dbHelper.deleteQuestion(questionId);
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting question: " + e.getMessage());
            return false;
        }
    }

    // Delete an answer.
    public boolean deleteAnswer(int answerId) {
        try {
            dbHelper.deleteAnswer(answerId);
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting answer: " + e.getMessage());
            return false;
        }
    }

    // Delete a reply.
    public boolean deleteReply(int replyId) {
        try {
            dbHelper.deleteReply(replyId);
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting reply: " + e.getMessage());
            return false;
        }
    }

    // Edit a question.
    public boolean editQuestion(int questionId, String newText) {
        try {
            return dbHelper.editQuestion(questionId, newText);
        } catch (Exception e) {
            System.err.println("Error editing question: " + e.getMessage());
            return false;
        }
    }

    // Retrieve all questions.
    public ObservableList<String> getAllQuestions() {
        try {
            return dbHelper.getAllQuestions();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Retrieve answers for a given question.
    public ObservableList<String> getAnswersForQuestion(int questionId) {
        try {
            return dbHelper.getAnswersForQuestion(questionId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Retrieve replies for a given question.
    public ObservableList<String> getRepliesForQuestion(int questionId) {
        try {
            return dbHelper.getRepliesForQuestion(questionId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Retrieve replies for a given answer.
    public ObservableList<String> getRepliesForAnswer(int answerId) {
        try {
            return dbHelper.getRepliesForAnswer(answerId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

	public void setCurrentUserId(int currentUserId) {
		this.currentUserId = currentUserId;
		
	}
	 public int addMessage(String questionText) {
	        if (currentUserId == -1) {
	            System.err.println("No user logged in.");
	            return -1;
	        }
	        try {
	            return dbHelper.addQuestion(currentUserId, questionText);
	        } catch (Exception e) {
	            System.err.println("Error adding question: " + e.getMessage());
	            return -1;
	        }
	    }
}
