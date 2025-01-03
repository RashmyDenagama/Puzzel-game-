package Code;

import javax.swing.*;
import Code.banana.engine.UserScore;
import Code.database.DataBaseManager;
import java.awt.*;
import java.util.List;

public class Leaderboard extends JFrame {
    private static final long serialVersionUID = 1L;
    private DataBaseManager dbManager;
    private JPanel leaderboardPanel;
    private JButton backButton;
    private String currentUser;

    // Constructor
    public Leaderboard(DataBaseManager dbManager, String currentUser) {
        this.dbManager = dbManager;
        this.currentUser = currentUser;

        setTitle("Leaderboard");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initializeUI();
        loadLeaderboard();
    }

    // Initialize UI components
    private void initializeUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 248, 255)); // Light background color

        // Leaderboard panel
        leaderboardPanel = new JPanel();
        leaderboardPanel.setLayout(new BoxLayout(leaderboardPanel, BoxLayout.Y_AXIS));
        leaderboardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(new JScrollPane(leaderboardPanel), BorderLayout.CENTER);

        // Header label
        JLabel headerLabel = new JLabel("Leaderboard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 26));
        headerLabel.setForeground(new Color(72, 61, 139)); // Dark Slate Blue color
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        add(headerLabel, BorderLayout.NORTH);

        // Back button
        backButton = createBackButton();
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.add(backButton);
        add(footerPanel, BorderLayout.SOUTH);
    }

    // Create the "Back to Levels" button
    private JButton createBackButton() {
        JButton button = new JButton("Back to Levels");
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(255, 223, 0)); // Bright yellow background
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.addActionListener(e -> {
            dispose(); // Close current window
            new Levels(dbManager, currentUser).setVisible(true);
        });
        return button;
    }

    // Load leaderboard data and display it
    private void loadLeaderboard() {
        leaderboardPanel.removeAll(); // Clear previous entries if refreshing

        // Add header row
        JPanel headerRow = new JPanel(new GridLayout(1, 3, 10, 10));
        headerRow.add(createLabel("Rank", Font.BOLD));
        headerRow.add(createLabel("Username", Font.BOLD));
        headerRow.add(createLabel("Time Taken (s)", Font.BOLD));
        headerRow.setBackground(new Color(135, 206, 250)); // Light sky blue for header
        leaderboardPanel.add(headerRow);

        // Retrieve scores from the database
        List<UserScore> scores = dbManager.getTopScoresByTime(); // Ensure this method exists in DataBaseManager
        int rank = 1;

        // Populate leaderboard rows
        for (UserScore score : scores) {
            JPanel row = new JPanel(new GridLayout(1, 3, 10, 10));

            // Highlight current user's row
            if (score.getUsername().equals(currentUser)) {
                row.setBackground(new Color(255, 223, 186)); // Peach color for highlighting
            } else {
                row.setBackground(rank % 2 == 0 ? new Color(224, 255, 255) : new Color(173, 216, 230)); // Alternating row colors
            }

            row.add(createLabel(String.valueOf(rank++), Font.PLAIN));
            row.add(createLabel(score.getUsername(), Font.PLAIN));
            row.add(createLabel(String.valueOf(score.getTimeTaken()), Font.PLAIN));
            leaderboardPanel.add(row);
        }

        leaderboardPanel.revalidate(); // Refresh panel to display new content
        leaderboardPanel.repaint();
    }

    // Create a label for leaderboard row with custom font style
    private JLabel createLabel(String text, int fontStyle) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", fontStyle, 18));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    // Display leaderboard frame
    public void showLeaderboard() {
        setVisible(true);
    }

    // Main method to test the Leaderboard class
    public static void main(String[] args) {
        DataBaseManager dbManager = new DataBaseManager(); // Replace with actual implementation
        String currentUser = "TestUser"; // Replace with actual username
        SwingUtilities.invokeLater(() -> new Leaderboard(dbManager, currentUser).setVisible(true));
    }

	public void addScore(String playerName, int score) {
		// TODO Auto-generated method stub
		
	}
}
