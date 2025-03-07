package application;

import databasePart1.DatabaseHelper;
import javafx.collections.ObservableList;

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
    public int addQuestion(String questionText) {
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

    // Add an answer to a question.
    public int addAnswer(int questionId, String answerText) {
        if (currentUserId == -1) {
            System.err.println("No user logged in.");
            return -1;
        }
        try {
            return dbHelper.addAnswer(currentUserId, questionId, answerText);
        } catch (Exception e) {
            System.err.println("Error adding answer: " + e.getMessage());
            return -1;
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
}
