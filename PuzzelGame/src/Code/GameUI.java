package Code;

import javax.swing.*;

import Code.banana.engine.Images;
import Code.database.DataBaseManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

public class GameUI extends JFrame {
    private int gridSize;
    private ArrayList<BufferedImage> images;
    private ArrayList<JButton> buttons;
    private String username;
    private DataBaseManager dbManager;
    private int score;
    private int hintsLeft;
    private JLabel hintLabel, timerLabel, scoreLabel;
    private Timer timer;
    private int timeLeft;
    private JButton firstClicked = null;
    private JButton secondClicked = null;
    private Images imageLoader;
    private int matchedPairs = 0;
    private final String logoPath = "C:\\Users\\Rashmi Denagama\\Downloads\\Logo.png";

    private final int HINTS_EASY = 6;
    private final int HINTS_INTERMEDIATE = 4;
    private final int HINTS_ADVANCED = 3;

    private final int TIME_EASY = 120;
    private final int TIME_INTERMEDIATE = 90;
    private final int TIME_ADVANCED = 60;

    public GameUI(DataBaseManager dbManager, String username, int gridSize, String difficulty) throws Exception {
        this.dbManager = dbManager;
        this.username = username;
        this.gridSize = gridSize;
        this.imageLoader = new Images();
        this.buttons = new ArrayList<>();
        this.score = 0;

        switch (difficulty) {
            case "easy":
                this.hintsLeft = HINTS_EASY;
                this.timeLeft = TIME_EASY;
                break;
            case "intermediate":
                this.hintsLeft = HINTS_INTERMEDIATE;
                this.timeLeft = TIME_INTERMEDIATE;
                break;
            case "advanced":
                this.hintsLeft = HINTS_ADVANCED;
                this.timeLeft = TIME_ADVANCED;
                break;
            default:
                throw new IllegalArgumentException("Invalid difficulty level: " + difficulty);
        }

        createUI();
        loadImages();
        startTimer();

        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void loadImages() {
        ArrayList<BufferedImage> imageList = new ArrayList<>();
        for (int i = 0; i < gridSize * gridSize / 2; i++) {
            BufferedImage img = imageLoader.getRandomImage();
            if (img != null) {
                imageList.add(img);
                imageList.add(img);
            } else {
                System.out.println("Image not loaded for index: " + i);
            }
        }
        Collections.shuffle(imageList);

        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setActionCommand(String.valueOf(imageList.get(i).hashCode()));
            buttons.get(i).putClientProperty("image", imageList.get(i));
        }
    }

    private void createUI() {
        setTitle("Picture Matching Game");
        setSize(800, 800);
        setLayout(new BorderLayout());

        // Top panel for hints, score, and timer
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        hintLabel = new JLabel("Hints: " + hintsLeft);
        scoreLabel = new JLabel("Score: " + score);
        timerLabel = new JLabel("Time left: " + timeLeft + "s");

        Font largeFont = new Font("Arial", Font.BOLD, 18);
        hintLabel.setFont(largeFont);
        scoreLabel.setFont(largeFont);
        timerLabel.setFont(largeFont);

        topPanel.add(hintLabel);
        topPanel.add(scoreLabel);
        topPanel.add(timerLabel);
        add(topPanel, BorderLayout.NORTH);

        // Create grid panel for buttons
        JPanel gridPanel = new JPanel(new GridLayout(gridSize, gridSize, 10, 10));
        for (int i = 0; i < gridSize * gridSize; i++) {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(100, 100));
            button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleButtonClick(button);
                }
            });
            buttons.add(button);
            gridPanel.add(button);
        }
        add(gridPanel, BorderLayout.CENTER);

        setLogoOnButtons();

        JPanel bottomPanel = new JPanel();
        JButton hintButton = new JButton("Use Hint");
        hintButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                useHint();
            }
        });

        setHintButtonColor(hintButton);
        bottomPanel.add(hintButton);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void setHintButtonColor(JButton hintButton) {
        hintButton.setBackground(Color.YELLOW);
        hintButton.setOpaque(true);
        hintButton.setBorderPainted(false);
    }

    private void setLogoOnButtons() {
        ImageIcon logoIcon = new ImageIcon(logoPath);
        for (JButton button : buttons) {
            button.setIcon(logoIcon);
            button.setText("");
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(resultingImage, 0, 0, null);
        g2d.dispose();
        return outputImage;
    }

    private void startTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                timerLabel.setText("Time left: " + timeLeft + "s");
                if (timeLeft <= 0) {
                    timer.stop();
                    endGame();
                }
            }
        });
        timer.start();
    }

    private void handleButtonClick(JButton clickedButton) {
        if (firstClicked == null) {
            firstClicked = clickedButton;
            BufferedImage image = (BufferedImage) clickedButton.getClientProperty("image");
            revealImage(clickedButton, image);
        } else if (secondClicked == null && clickedButton != firstClicked) {
            secondClicked = clickedButton;
            BufferedImage image = (BufferedImage) clickedButton.getClientProperty("image");
            revealImage(clickedButton, image);

            if (firstClicked.getActionCommand().equals(secondClicked.getActionCommand())) {
                firstClicked.setEnabled(false);
                secondClicked.setEnabled(false);
                score += 10;
                scoreLabel.setText("Score: " + score);
                matchedPairs++;

                if (matchedPairs == (gridSize * gridSize) / 2) {
                    endGame();
                }

                firstClicked = null;
                secondClicked = null;
            } else {
                Timer hideTimer = new Timer(1000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        hideImage(firstClicked);
                        hideImage(secondClicked);
                        firstClicked = null;
                        secondClicked = null;
                    }
                });
                hideTimer.setRepeats(false);
                hideTimer.start();
            }
        }
    }

    private void revealImage(JButton button, BufferedImage image) {
        button.setIcon(new ImageIcon(resizeImage(image, 100, 100)));
    }

    private void hideImage(JButton button) {
        button.setIcon(new ImageIcon(logoPath));
    }

    private void useHint() {
        if (hintsLeft > 0) {
            hintsLeft--;
            hintLabel.setText("Hints: " + hintsLeft);
            
            ArrayList<JButton> unmatchedButtons = new ArrayList<>();
            for (JButton button : buttons) {
                if (button.isEnabled() && !button.getActionCommand().equals(firstClicked != null ? firstClicked.getActionCommand() : "")) {
                    unmatchedButtons.add(button);
                }
            }

            for (JButton button : unmatchedButtons) {
                BufferedImage actualImage = (BufferedImage) button.getClientProperty("image");
                revealImage(button, actualImage);
            }

            Timer hideTimer = new Timer(2000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for (JButton button : unmatchedButtons) {
                        hideImage(button);
                    }
                }
            });  
            hideTimer.setRepeats(false);
            hideTimer.start();
        } else {
            JOptionPane.showMessageDialog(null, "No hints left!");
        }
    }

    private void endGame() {
        timer.stop();
        long endTime = System.currentTimeMillis();
        long startTime = 0;  // You can use an actual start time if you track it
        int duration = (int) ((endTime - startTime) / 1000);  // Calculate duration in seconds

        // Save the score to the database
        saveScoreToDatabase();

        // Show the game over message
        JOptionPane.showMessageDialog(this, "Game Over! Your score is: " + score);
        
        // Transition to the next game (e.g., EquationGame)
        new EquationGame(username).setVisible(true);

        // Close the current game UI after transitioning to the next game
        this.dispose();
    }

    private void saveScoreToDatabase() {
        try {
            dbManager.saveScore(username, score, timeLeft);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving score.");
        }
    }
}
