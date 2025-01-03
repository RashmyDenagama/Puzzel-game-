package Code.banana.engine;

import java.sql.Timestamp;

public class UserScore {
    private String username;
    private int score;          // Represents the user's score
    private int timeTaken;      // Represents the duration or time taken by the user
    private Timestamp timestamp; // Represents when the score was recorded

    // Constructor with all fields
    public UserScore(String username, int score, int timeTaken, Timestamp timestamp) {
        this.username = username;
        this.score = score;
        this.timeTaken = timeTaken;
        this.timestamp = timestamp;
    }

    // Constructor for username and timeTaken (used for leaderboard ranking)
    public UserScore(String username, int timeTaken) {
        this.username = username;
        this.timeTaken = timeTaken;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public int getTimeTaken() {
        return timeTaken;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    // Setters (if values need to be updated)
    public void setUsername(String username) {
        this.username = username;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setTimeTaken(int timeTaken) {
        this.timeTaken = timeTaken;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
