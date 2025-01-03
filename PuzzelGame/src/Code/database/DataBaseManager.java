package Code.database;

import Code.banana.engine.UserScore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBaseManager {
    private Connection connection;

    // Constructor to establish database connection
    public DataBaseManager() {
        try {
            // Load MySQL driver and establish connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/game_db";
            String user = "root";
            String password = "";
            connection = DriverManager.getConnection(url, user, password);

            if (connection != null) {
                System.out.println("Connection to MySQL has been established.");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Failed to connect to the MySQL database.");
            e.printStackTrace();
            connection = null;
        }
    }

    // Method to hash a password using SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    // Register a new user
    public boolean register(String username, String password) throws SQLException {
        if (connection == null) {
            System.out.println("Database connection is not established.");
            return false;
        }

        if (password.length() < 8) {
            System.out.println("Password must be at least 8 characters long.");
            return false;
        }

        String hashedPassword = hashPassword(password);
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.executeUpdate();
            System.out.println("User registered successfully.");
            return true;
        } catch (SQLException e) {
            System.out.println("Error registering user.");
            e.printStackTrace();
            return false;
        }
    }

    // User login
    public boolean login(String username, String password) throws SQLException {
        if (connection == null) {
            System.out.println("Database connection is not established.");
            return false;
        }

        String hashedPassword = hashPassword(password);
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Login successful.");
                    return true;
                } else {
                    System.out.println("Invalid username or password.");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error during login.");
            e.printStackTrace();
            return false;
        }
    }

    // Save user's score with username and score
    public void saveScore(String username, int score) {
        String query = "SELECT username FROM users WHERE username = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String checkScoreQuery = "SELECT * FROM scores WHERE username = ?";
                try (PreparedStatement checkStmt = connection.prepareStatement(checkScoreQuery)) {
                    checkStmt.setString(1, username);
                    ResultSet scoreRs = checkStmt.executeQuery();

                    if (scoreRs.next()) {
                        String updateQuery = "UPDATE scores SET score = ? WHERE username = ?";
                        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                            updateStmt.setInt(1, score);
                            updateStmt.setString(2, username);
                            updateStmt.executeUpdate();
                            System.out.println("Score updated successfully for " + username);
                        }
                    } else {
                        String insertQuery = "INSERT INTO scores (username, score) VALUES (?, ?)";
                        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                            insertStmt.setString(1, username);
                            insertStmt.setInt(2, score);
                            insertStmt.executeUpdate();
                            System.out.println("Score inserted successfully for " + username);
                        }
                    }
                }
            } else {
                System.out.println("User not found in the database.");
            }
        } catch (SQLException e) {
            System.out.println("Error saving or updating score.");
            e.printStackTrace();
        }
    }

    // Save user's score with time_taken
    public void saveScore(String username, int score, int timeTaken) throws SQLException {
        String query = "INSERT INTO scores (username, score, time_taken, timestamp) VALUES (?, ?, ?, NOW())";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setInt(2, score);
            stmt.setInt(3, timeTaken);
            stmt.executeUpdate();
            System.out.println("Score saved successfully for " + username);
        } catch (SQLException e) {
            System.out.println("Error saving score to the database.");
            e.printStackTrace();
        }
    }

    // Get top 10 players by time taken
    public List<UserScore> getTopScoresByTime() {
        List<UserScore> topScores = new ArrayList<>();
        String query = "SELECT username, time_taken FROM scores ORDER BY time_taken ASC LIMIT 10";

        try (Statement stmt = connection.createStatement(); ResultSet resultSet = stmt.executeQuery(query)) {
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                int timeTaken = resultSet.getInt("time_taken");
                topScores.add(new UserScore(username, timeTaken));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching top scores by time.");
            e.printStackTrace();
        }

        return topScores;
    }

    // Get top 10 high scores
    public List<UserScore> getTopScores() {
        List<UserScore> topScores = new ArrayList<>();
        String query = "SELECT username, score FROM scores ORDER BY score DESC LIMIT 10";

        try (Statement stmt = connection.createStatement(); ResultSet resultSet = stmt.executeQuery(query)) {
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                int score = resultSet.getInt("score");
                topScores.add(new UserScore(username, score));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching top scores.");
            e.printStackTrace();
        }

        return topScores;
    }

    // Get user's score by username
    public int getUserScore(String username) {
        String query = "SELECT score FROM scores WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("score");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching user score.");
            e.printStackTrace();
        }
        return 0;
    }

    // Helper method to return the existing connection
    private Connection getConnection() {
        return connection;
    }
}
