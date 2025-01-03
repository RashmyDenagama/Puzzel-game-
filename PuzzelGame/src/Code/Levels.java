package Code;

import javax.swing.*;

import Code.banana.engine.UserFeedback;
import Code.database.DataBaseManager;
import java.awt.*;
import java.io.File;

public class Levels extends JFrame {
    private DataBaseManager dbManager;
    private String username;

    public Levels(DataBaseManager dbManager2, String username2) {
        this.dbManager = dbManager2;
        this.username = username2;
        createUI();
    }

    private void createUI() {
        // Check if the user is logged in
        if (username == null || username.isEmpty()) {
            UserFeedback.showError("No user is logged in. Redirecting to Login...");
            EventQueue.invokeLater(() -> {
                new Login(dbManager);  // Redirect to the login page
                dispose();  // Close the current window
            });
            return;
        }

        setTitle("Select Difficulty Level");
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Create a panel for content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(new Color(255, 255, 255));

        setContentPane(contentPanel);

        // Panel for "User Profile" and "Logout" buttons
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        topPanel.setOpaque(false);

        JButton viewProfileButton = new JButton("View Profile");
        viewProfileButton.setFont(new Font("Arial", Font.PLAIN, 16));
        viewProfileButton.setBackground(new Color(70, 130, 180));
        viewProfileButton.setForeground(Color.WHITE);
        viewProfileButton.addActionListener(e -> showProfile());

        topPanel.add(viewProfileButton);

        contentPanel.add(topPanel, BorderLayout.NORTH);

        JLabel titleLabel = new JLabel("Select Your Level", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(new Color(0, 102, 204));
        contentPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JButton beginnerButton = new JButton("Beginner");
        beginnerButton.setBackground(new Color(144, 238, 144));
        beginnerButton.setFont(new Font("Arial", Font.PLAIN, 18));
        beginnerButton.addActionListener(e -> openGameUI(4, "easy"));
        beginnerButton.setFocusPainted(false);
        buttonPanel.add(beginnerButton);

        JButton intermediateButton = new JButton("Intermediate");
        intermediateButton.setBackground(new Color(255, 228, 181));
        intermediateButton.setFont(new Font("Arial", Font.PLAIN, 18));
        intermediateButton.addActionListener(e -> openGameUI(6, "intermediate"));
        intermediateButton.setFocusPainted(false);
        buttonPanel.add(intermediateButton);

        JButton advancedButton = new JButton("Advanced");
        advancedButton.setBackground(new Color(255, 160, 122));
        advancedButton.setFont(new Font("Arial", Font.PLAIN, 18));
        advancedButton.addActionListener(e -> openGameUI(8, "advanced"));
        advancedButton.setFocusPainted(false);
        buttonPanel.add(advancedButton);

        contentPanel.add(buttonPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void openGameUI(int gridSize, String difficulty) {
        System.out.println("Starting game with difficulty: " + difficulty);
        SwingUtilities.invokeLater(() -> {
            try {
                new GameUI(dbManager, username, gridSize, difficulty);
                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private void showProfile() {
        // Fetch user profile and score from the database
        String userProfileInfo = "Username: " + username;
        String score = fetchUserScore();

        // Create profile panel
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BorderLayout());
        profilePanel.setBackground(new Color(255, 255, 255));

        JLabel usernameLabel = new JLabel(userProfileInfo, JLabel.CENTER);
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 18));

        JLabel scoreLabel = new JLabel("Score: " + score, JLabel.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 18));

        // Profile picture handling
        JLabel profilePic = new JLabel();
        ImageIcon profileIcon = loadProfilePicture();
        profilePic.setIcon(profileIcon);
        profilePic.setHorizontalAlignment(JLabel.CENTER);

        JPanel profileDetailsPanel = new JPanel();
        profileDetailsPanel.setLayout(new BoxLayout(profileDetailsPanel, BoxLayout.Y_AXIS));
        profileDetailsPanel.setBackground(Color.WHITE);
        profileDetailsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        profileDetailsPanel.add(profilePic);
        profileDetailsPanel.add(Box.createVerticalStrut(10));
        profileDetailsPanel.add(usernameLabel);
        profileDetailsPanel.add(scoreLabel);

        JPanel centeredProfilePanel = new JPanel();
        centeredProfilePanel.setLayout(new BorderLayout());
        centeredProfilePanel.setBackground(Color.WHITE);
        centeredProfilePanel.add(profileDetailsPanel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonsPanel.setBackground(Color.WHITE);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBackground(new Color(70, 130, 180));
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(e -> {
            dispose();
            new Levels(dbManager, username);
        });

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 16));
        logoutButton.setBackground(new Color(255, 69, 0));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.addActionListener(e -> {
            username = null;
            EventQueue.invokeLater(() -> {
                new Login(dbManager);
                dispose();
            });
        });

        buttonsPanel.add(backButton);
        buttonsPanel.add(logoutButton);

        profilePanel.add(centeredProfilePanel, BorderLayout.CENTER);
        profilePanel.add(buttonsPanel, BorderLayout.SOUTH);

        getContentPane().removeAll();
        getContentPane().add(profilePanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private ImageIcon loadProfilePicture() {
        // Check if the user has a profile picture stored
        File profilePictureFile = new File("path_to_user_profile_picture_directory/" + username + ".png");
        if (profilePictureFile.exists()) {
            return new ImageIcon(profilePictureFile.getAbsolutePath());
        } else {
            // Return a default image if no profile picture is found
            return new ImageIcon("path_to_default_profile_picture.png");
        }
    }

    private String fetchUserScore() {
        // Fetch the user score from the database using dbManager
        int score = dbManager.getUserScore(username);
        return String.valueOf(score);
    }

    public static void main(String[] args) {
        DataBaseManager dbManager = new DataBaseManager();
        String username = "user1"; // Replace with actual user login logic
        new Levels(dbManager, username);
    }
}
