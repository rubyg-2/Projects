package databasePart1;

import java.sql.*;
import java.util.UUID;
import java.security.SecureRandom;
import application.User;
import application.PasswordEvaluator;
import javafx.scene.control.Label;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import java.sql.Timestamp;
import java.time.LocalDateTime;


/**
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, and handling invitation codes.
 */
public class DatabaseHelper {

    // JDBC driver name and database URL 
    static final String JDBC_DRIVER = "org.h2.Driver";   
    static final String DB_URL = "jdbc:h2:~/TP2_db";  

    // Database credentials 
    static final String USER = "sa"; 
    static final String PASS = ""; 

    public Connection connection = null;
    private Statement statement = null; 

    public void connectToDatabase() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER); // Load the JDBC driver
            System.out.println("Connecting to database...");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement(); 
            // You can use this command to clear the database and restart from fresh.
            // statement.execute("DROP ALL OBJECTS");

            createTables();  // Create the necessary tables if they don't exist
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        }
    }

    private void createTables() throws SQLException {
        String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "userName VARCHAR(255) UNIQUE, "
                + "password VARCHAR(255), "
                + "role VARCHAR(20))";
        statement.execute(userTable);

        // Create the invitation codes table
        String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
                + "code VARCHAR(10) PRIMARY KEY, "
                + "isUsed BOOLEAN DEFAULT FALSE, "
                + "codeExpireDate TIMESTAMP)";
        statement.execute(invitationCodesTable);

        // Create the one-time-password table
        String otpTable = "CREATE TABLE IF NOT EXISTS oneTimePasswords ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "userName VARCHAR(255), "
                + "otp VARCHAR(255), "
                + "role VARCHAR(20))";
        statement.execute(otpTable);

        // Note: Changed foreign key references from 'users' to 'cse360users'
        String questionTable = "CREATE TABLE IF NOT EXISTS questions ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "userId INT, "
                + "questionText VARCHAR(1000), "
                + "FOREIGN KEY(userId) REFERENCES cse360users(id))";
        statement.execute(questionTable);

        String answerTable = "CREATE TABLE IF NOT EXISTS answers ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "questionId INT, "
                + "userId INT, "
                + "answerText VARCHAR(1000), "
                + "FOREIGN KEY(questionId) REFERENCES questions(id), "
                + "FOREIGN KEY(userId) REFERENCES cse360users(id))";
        statement.execute(answerTable);

        String replyTable = "CREATE TABLE IF NOT EXISTS replies ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "parentType VARCHAR(50), "  
                + "parentId INT, "
                + "userId INT, "
                + "replyText VARCHAR(1000), "
                + "FOREIGN KEY(userId) REFERENCES cse360users(id))";
        statement.execute(replyTable);
    }

    // Check if the database is empty
    public boolean isDatabaseEmpty() throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM cse360users";
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            return resultSet.getInt("count") == 0;
        }
        return true;
    }
    

    // from qadb
    public int addQuestion(int userId, String questionText) throws SQLException {
        String sql = "INSERT INTO questions (userId, questionText) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, questionText);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()){
                return rs.getInt(1);
            }
        }
        return -1;
    }
    public int getUserId(String username, String password) throws SQLException {
        String query = "SELECT id FROM cse360users WHERE userName = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1;
    }


    public int addAnswer(int userId, int questionId, String answerText) throws SQLException {
        String sql = "INSERT INTO answers (questionId, userId, answerText) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, questionId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, answerText);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()){
                return rs.getInt(1);
            }
        }
        return -1;
    }

    public int addReply(int userId, String parentType, int parentId, String replyText) throws SQLException {
        String sql = "INSERT INTO replies (parentType, parentId, userId, replyText) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, parentType);
            pstmt.setInt(2, parentId);
            pstmt.setInt(3, userId);
            pstmt.setString(4, replyText);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()){
                return rs.getInt(1);
            }
        }
        return -1;
    }

    public void deleteQuestion(int questionId) throws SQLException {
        String sql = "UPDATE questions SET questionText = 'Deleted' WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, questionId);
            pstmt.executeUpdate();
        }
    }

    // delete answer
    public void deleteAnswer(int answerId) throws SQLException {
        String sql = "UPDATE answers SET answerText = 'Deleted' WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, answerId);
            pstmt.executeUpdate();
        }
    }

    // edit question
    public boolean editQuestion(int questionId, String newText) throws SQLException {
        String sql = "UPDATE questions SET questionText = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newText);
            pstmt.setInt(2, questionId);
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        }
    }

    public void deleteReply(int replyId) throws SQLException {
        String sql = "UPDATE replies SET replyText = 'Deleted' WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, replyId);
            pstmt.executeUpdate();
        }
    }

    // working on a method to make a question unrelated
    public void markAnswerAsUnrelated(int answerId) throws SQLException {
        String sql = "UPDATE answers SET answerText = 'Unrelated' WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, answerId);
            pstmt.executeUpdate();
        }
    }

    public void markReplyAsUnrelated(int replyId) throws SQLException {
        String sql = "UPDATE replies SET replyText = 'Unrelated' WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, replyId);
            pstmt.executeUpdate();
        }
    }

    public ObservableList<String> getAllQuestions() throws SQLException {
        ObservableList<String> list = FXCollections.observableArrayList();
        // Changed join from "users" to "cse360users"
        String query = "SELECT q.id, q.questionText, u.userName " +
                       "FROM questions q JOIN cse360users u ON q.userId = u.id";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                int id = rs.getInt("id");
                String text = rs.getString("questionText");
                String username = rs.getString("userName");
                list.add("Question " + id + " by " + username + ": " + text);
            }
        }
        return list;
    }

    // all things required for processing answers
    public ObservableList<String> getAnswersForQuestion(int questionId) throws SQLException {
        ObservableList<String> list = FXCollections.observableArrayList();
        // Changed join from "users" to "cse360users"
        String query = "SELECT a.id, a.answerText, u.userName " +
                       "FROM answers a JOIN cse360users u ON a.userId = u.id " +
                       "WHERE a.questionId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, questionId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                int id = rs.getInt("id");
                String text = rs.getString("answerText");
                String username = rs.getString("userName");
                list.add("Answer " + id + " by " + username + ": " + text);
            }
        }
        return list;
    }

    public ObservableList<String> getRepliesForQuestion(int questionId) throws SQLException {
        ObservableList<String> list = FXCollections.observableArrayList();
        // Changed join from "users" to "cse360users"
        String query = "SELECT r.id, r.replyText, u.userName " +
                       "FROM replies r JOIN cse360users u ON r.userId = u.id " +
                       "WHERE r.parentType = 'question' AND r.parentId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, questionId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                int id = rs.getInt("id");
                String text = rs.getString("replyText");
                String username = rs.getString("userName");
                list.add("Reply " + id + " by " + username + ": " + text);
            }
        }
        return list;
    }

    // gets replies for an answer
    public ObservableList<String> getRepliesForAnswer(int answerId) throws SQLException {
        ObservableList<String> list = FXCollections.observableArrayList();
        // Changed join from "users" to "cse360users"
        String query = "SELECT r.id, r.replyText, u.userName " +
                       "FROM replies r JOIN cse360users u ON r.userId = u.id " +
                       "WHERE r.parentType = 'answer' AND r.parentId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, answerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                int id = rs.getInt("id");
                String text = rs.getString("replyText");
                String username = rs.getString("userName");
                list.add("Reply " + id + " by " + username + ": " + text);
            }
        }
        return list;
    }

    // Registers a new user in the database.
    public void register(User user) throws SQLException {
        String insertUser = "INSERT INTO cse360users (userName, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.executeUpdate();
        }
    }

    // Validates a user's login credentials.
    public boolean login(User user) throws SQLException {
        String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ? AND role = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Checks if a user already exists in the database based on their userName.
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

    // Retrieves the role of a user from the database using their userName.
    public String getUserRole(String userName) {
        String query = "SELECT role FROM cse360users WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("role"); // Return the role if user exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // If no user exists or an error occurs
    }

    // RUBY'S CODE - Assign a user role
    public void assignUserRole(String userName, String role) throws SQLException {
        String query = "UPDATE cse360users SET role = ? WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, role);
            pstmt.setString(2, userName);
            pstmt.executeUpdate();
        }
    }

    // Chase's Code - Generate one-time password
    public String generateOneTimePassword(String userName) {
        String query = "INSERT INTO oneTimePasswords (userName, otp, role) VALUES (?, ?, ?)";
        StringBuilder randomPassword = new StringBuilder();

        // Sets of accepted characters
        String upperChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerChar = "abcdefghijklmnopqrstuvwxyz";
        String digit = "1234567890";
        String specialChar = "~`!@#$%^&*()_-+{}[]|:,.?/";

        // All allowed characters
        String allChar = upperChar + lowerChar + digit + specialChar;

        // Secure Random Generator
        SecureRandom random = new SecureRandom();

        // StringBuilder for the password
        StringBuilder password = new StringBuilder();

        // Fill the requirements for the password
        password.append(upperChar.charAt(random.nextInt(upperChar.length())));
        password.append(lowerChar.charAt(random.nextInt(lowerChar.length())));
        password.append(digit.charAt(random.nextInt(digit.length())));
        password.append(specialChar.charAt(random.nextInt(specialChar.length())));

        // Add random characters to get to at least 8 total characters
        int j = random.nextInt(8) + 4;
        for (int i = 0; i < j; i++) {
            password.append(allChar.charAt(random.nextInt(allChar.length())));
        }

        // Shuffle password to ensure random order
        while (password.length() > 0) {
            int index = random.nextInt(password.length());
            randomPassword.append(password.charAt(index));
            password.deleteCharAt(index);
        }

        // Check if the generated password meets requirements
        String checkRnd = PasswordEvaluator.evaluatePassword(randomPassword.toString());
        if (checkRnd.equals("")) {
            // Insert saving to database
            System.out.println(randomPassword);
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, userName);
                pstmt.setString(2, randomPassword.toString());
                pstmt.setString(3, getUserRole(userName));
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "Random password generated, check with admin";
        } else {
            return "Password Generation Failed because generated password\n" + checkRnd;
        }
    }

    // Fetches the user's role while using an OTP
    public String getUserRoleOTP(String userName) {
        String query = "SELECT role FROM oneTimePasswords WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Login checker while using an OTP
    public Boolean loginOTP(User user) throws SQLException {
        String query = "SELECT * FROM oneTimePasswords WHERE userName = ? AND otp = ? AND role = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Removing a One Time Password from database
    public void rmOTP(User user) {
        String query = "DELETE FROM oneTimePasswords WHERE userName = ? AND role = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getRole());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Retrieve OTP
    public String getOTP(String username) {
        String query = "SELECT otp FROM oneTimePasswords WHERE userName = ?";
        String passwords = "";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) {
                return "No one-time-passwords found for this user";
            } else {
                do {
                    if (passwords.length() > 0) {
                        passwords += "\n";
                    }
                    passwords += rs.getString("otp");
                } while (rs.next());
            }
            return passwords;
        } catch (SQLException e) {
            e.printStackTrace();
            return "SQL Error";
        }
    }

    // Generates a new invitation code and inserts it into the database.
    public String generateInvitationCode() {
        String code = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
        
        // Set expiration date to 3 days from now using LocalDateTime.
        LocalDateTime expireTime = LocalDateTime.now().plusDays(3);
        Timestamp expireTimestamp = Timestamp.valueOf(expireTime);
        
        String query = "INSERT INTO InvitationCodes (code, codeExpireDate) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, code);
            pstmt.setTimestamp(2, expireTimestamp);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return code;
    }

    // Validates an invitation code to check if it is unused.
    public boolean validateInvitationCode(String code) {
        String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Optionally, you could check the expiration time here:
                Timestamp expireTimestamp = rs.getTimestamp("codeExpireDate");
                if (expireTimestamp.before(new Timestamp(System.currentTimeMillis()))) {
                    // Code has expired.
                    return false;
                }
                // Mark the code as used.
                markInvitationCodeAsUsed(code);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Marks the invitation code as used in the database.
    private void markInvitationCodeAsUsed(String code) {
        String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, code);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Displays all of the users in the database
    public ObservableList<String> displayUsers() throws SQLException {
        if (isDatabaseEmpty()) {
            Label emptyDatabase = new Label("No users in the Database");
            emptyDatabase.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            return null;
        }
        ObservableList<String> data = FXCollections.observableArrayList();
        // Use correct column name: userName (not username)
        String query = "SELECT userName, role FROM cse360users";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                data.add(rs.getString("userName"));
                data.add(rs.getString("role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } 
        return data;
    }

    // Deletes user, admin call
    public int deleteUser(String username) throws SQLException {
        if (!doesUserExist(username)) {
            return -1;   // return -1 if username doesn't exist
        }
        String query = "DELETE FROM cse360users WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
            return 1;    // return 1 if successful
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -2;    // return -2 if something else happened
    }

    // Closes the database connection and statement.
    public void closeConnection() {
        try { 
            if (statement != null) statement.close(); 
        } catch (SQLException se2) { 
            se2.printStackTrace();
        } 
        try { 
            if (connection != null) connection.close(); 
        } catch (SQLException se) { 
            se.printStackTrace();
        } 
    }
    public int addMessage(int userId, String msgText) throws SQLException {
        String sql = "INSERT INTO messages (userId, msgText) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, msgText);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()){
                return rs.getInt(1);
            }
        }
        return -1;
    }
    //JULIE'S CODE
    public boolean similarQuestionExists(String questionText) throws SQLException {
        String sql = "SELECT COUNT(*) FROM questions WHERE questionText LIKE ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + questionText.trim() + "%");
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}
