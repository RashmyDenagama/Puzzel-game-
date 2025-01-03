package Code;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import Code.banana.engine.GameEngine;
import Code.database.DataBaseManager;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class EquationGame extends JFrame {
    private static final long serialVersionUID = -107785653906635L;

    private JLabel questionLabel;
    private JTextArea infoArea;
    private JPanel buttonPanel;
    private JButton[] numberButtons;
    private JButton nextGameButton;
    private JButton leaderboardButton;
    private JLabel timerLabel;

    private GameEngine gameEngine;
    private Leaderboard leaderboard;
    private String playerName;
    private Timer gameTimer;
    private static final int GAME_DURATION = 60 * 1000; // 1 minute in milliseconds
    private int remainingTime;

    public EquationGame(String player) {
        super("Equation Game - Find the Missing Value?");
        this.playerName = player != null ? player : "Player";
        DataBaseManager dbManager = new DataBaseManager(); // Initialize DataBaseManager here
        leaderboard = new Leaderboard(dbManager, player); 
        initializeGameComponents();
        configureUI();
        startGameTimer();
    }

    private void initializeGameComponents() {
        gameEngine = new GameEngine(playerName);
       
        questionLabel = createQuestionLabel();
        infoArea = createInfoArea();
        remainingTime = GAME_DURATION / 1000;
        loadNextGame();
    }

    private void configureUI() {
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        getContentPane().setBackground(new Color(255, 255, 204));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);
        mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        mainPanel.add(createHeaderPanel());
        mainPanel.add(createTimerPanel());
        mainPanel.add(new JScrollPane(infoArea));
        mainPanel.add(new JScrollPane(questionLabel));
        mainPanel.add(createButtonPanel());
        mainPanel.add(createControlPanel());

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(mainPanel);
        add(centerPanel, BorderLayout.CENTER);

        add(createImagePanel("/mon.gif"), BorderLayout.NORTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        JLabel headerLabel = new JLabel("Equation Game");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(headerLabel);
        return headerPanel;
    }

    private JPanel createTimerPanel() {
        JPanel timerPanel = new JPanel();
        timerPanel.setOpaque(false);
        timerLabel = new JLabel("Time: " + remainingTime);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        timerLabel.setOpaque(true);
        timerLabel.setBackground(new Color(255, 255, 204));
        timerLabel.setForeground(Color.BLACK);
        timerLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerPanel.add(timerLabel);
        return timerPanel;
    }

    private JTextArea createInfoArea() {
        JTextArea infoArea = new JTextArea(3, 25);
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Arial", Font.BOLD, 16));
        infoArea.setText("Guess the missing value.");
        return infoArea;
    }

    private JLabel createQuestionLabel() {
        JLabel questionLabel = new JLabel();
        questionLabel.setPreferredSize(new Dimension(500, 300));
        questionLabel.setHorizontalAlignment(JLabel.CENTER);
        questionLabel.setVerticalAlignment(JLabel.CENTER);
        return questionLabel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridLayout(2, 5, 10, 10));
        numberButtons = new JButton[10];
        for (int i = 0; i < 10; i++) {
            numberButtons[i] = createNumberButton(i);
            buttonPanel.add(numberButtons[i]);
        }
        return buttonPanel;
    }

    private JButton createNumberButton(int number) {
        JButton button = new JButton(String.valueOf(number));
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(new Color(200, 200, 200));
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.addActionListener(new AnswerButtonListener(number));
        return button;
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setOpaque(false);

        // Next Game button redirects to Levels class
        nextGameButton = new JButton("Next Game");
        nextGameButton.setFont(new Font("Arial", Font.BOLD, 18));
        nextGameButton.setBackground(new Color(200, 200, 200));
        nextGameButton.setOpaque(true);
        nextGameButton.setBorderPainted(false);
        nextGameButton.addActionListener(e -> openLevels()); // Changed action
        controlPanel.add(nextGameButton);

        // Leaderboard button opens the leaderboard screen
        leaderboardButton = new JButton("Leaderboard");
        leaderboardButton.setFont(new Font("Arial", Font.BOLD, 18));
        leaderboardButton.setBackground(new Color(200, 200, 200));
        leaderboardButton.setOpaque(true);
        leaderboardButton.setBorderPainted(false);
        leaderboardButton.addActionListener(e -> openLeaderboard());
        controlPanel.add(leaderboardButton);

        return controlPanel;
    }

    private void startGameTimer() {
        gameTimer = new Timer(1000, e -> updateTimer());
        gameTimer.start();
    }

    private void updateTimer() {
        remainingTime--;
        timerLabel.setText("Time: " + remainingTime);

        if (remainingTime <= 10) {
            timerLabel.setBackground(remainingTime % 2 == 0 ? Color.YELLOW : Color.BLACK);
            timerLabel.setForeground(remainingTime % 2 == 0 ? Color.BLACK : Color.YELLOW);
        } else {
            timerLabel.setBackground(new Color(255, 255, 204));
            timerLabel.setForeground(Color.BLACK);
        }

        if (remainingTime <= 0) {
            gameTimer.stop();
            JOptionPane.showMessageDialog(this, "Time's up! Game over.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            saveScoreToLeaderboard();
        }
    }

    private void loadNextGame() {
        BufferedImage currentEquationImage = gameEngine.nextGame();
        if (currentEquationImage != null) {
            SwingUtilities.invokeLater(() -> {
                int labelWidth = questionLabel.getWidth();
                int labelHeight = questionLabel.getHeight();
                if (labelWidth > 0 && labelHeight > 0) {
                    Image scaledImage = currentEquationImage.getScaledInstance(labelWidth, labelHeight, Image.SCALE_SMOOTH);
                    questionLabel.setIcon(new ImageIcon(scaledImage));
                    infoArea.setText("Guess the missing value.");
                } else {
                    questionLabel.setText("Game over! Final score: " + gameEngine.getScore());
                    saveScoreToLeaderboard();
                }
            });
        } else {
            questionLabel.setText("Game over! Final score: " + gameEngine.getScore());
            saveScoreToLeaderboard();
        }
    }

    private void saveScoreToLeaderboard() {
        leaderboard.addScore(playerName, gameEngine.getScore());
        JOptionPane.showMessageDialog(this, "Score saved to leaderboard.", "Leaderboard", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openLeaderboard() {
        this.dispose();
        leaderboard.showLeaderboard();
    }

    private void openLevels() {
        this.dispose();
        new Levels(null, playerName).setVisible(true); // Assuming `Levels` is another JFrame that manages levels
    }

    private JPanel createImagePanel(String imagePath) {
        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        imagePanel.setOpaque(false);
        try {
            File file = new File(imagePath);
            if (file.exists()) {
                BufferedImage gifImage = ImageIO.read(file);
                JLabel gifLabel = new JLabel(new ImageIcon(gifImage));
                imagePanel.add(gifLabel);
            } else {
                System.out.println("Image file not found at the path: " + imagePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imagePanel;
    }

    private class AnswerButtonListener implements ActionListener {
        private final int number;

        public AnswerButtonListener(int number) {
            this.number = number;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (gameEngine.checkAnswer(number)) {
                JOptionPane.showMessageDialog(EquationGame.this, "Congratulations! You've won this round.", "Correct Answer", JOptionPane.INFORMATION_MESSAGE);
                openLeaderboard(); // Direct to leaderboard after winning
            } else {
                JOptionPane.showMessageDialog(EquationGame.this, "Incorrect, try again.", "Incorrect Answer", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String playerName = "John Doe"; // Replace with actual player name
            new EquationGame(playerName).setVisible(true);
        });
    }
}
