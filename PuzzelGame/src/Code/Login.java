package Code;

import javax.imageio.ImageIO;
import javax.swing.*;
import Code.database.DataBaseManager;
import Code.banana.engine.Session;
import Code.banana.engine.UserFeedback;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;
    private DataBaseManager dbManager;

    public Login(DataBaseManager dbManager) {
        this.dbManager = dbManager;
        createUI();
    }

    private void createUI() {
        setTitle("Login");
        setLayout(new BorderLayout());
        setResizable(false);

        // Create the main panel with a pastel yellow background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(new Color(255, 255, 204)); // Light pastel yellow background
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(255, 255, 204)); // Ensuring background color is pastel yellow

        // Create the form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));  // Adding padding for a more spacious look

        // Add profile image
        JLabel profileImage = new JLabel(createCircularImageIcon("C:\\Users\\Rashmi Denagama\\Downloads\\BANANA LOGO.jpg", 100));
        profileImage.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(profileImage);
        formPanel.add(Box.createVerticalStrut(20));

        // Add username field
        JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        usernamePanel.setOpaque(false);
        JLabel usernameLabel = new JLabel("Username: ");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameLabel.setForeground(Color.BLACK);
        usernamePanel.add(usernameLabel);
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        usernameField.setPreferredSize(new Dimension(250, 35)); // Set preferred size for better alignment
        usernamePanel.add(usernameField);
        formPanel.add(usernamePanel);
        formPanel.add(Box.createVerticalStrut(10));

        // Add password field
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passwordPanel.setOpaque(false);
        JLabel passwordLabel = new JLabel("Password: ");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordLabel.setForeground(Color.BLACK);
        passwordPanel.add(passwordLabel);
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        passwordField.setPreferredSize(new Dimension(250, 35)); // Set preferred size for better alignment
        passwordPanel.add(passwordField);
        formPanel.add(passwordPanel);
        formPanel.add(Box.createVerticalStrut(20));

        // Button panel with login and register buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);

        loginButton = createButton("Login");
        loginButton.addActionListener(e -> handleLogin());

        registerButton = createButton("Register");
        registerButton.addActionListener(e -> handleRegistration());

        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(registerButton);
        formPanel.add(buttonPanel);

        // Add the form panel to the main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);

        setSize(400, 500);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(100, 149, 237)); // Light blue buttons to complement yellow background
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(150, 40));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(135, 206, 250)); // Lighter blue on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 149, 237)); // Default blue color
            }
        });
        return button;
    }

    private void handleLogin() {
        String username = usernameField.getText();
        char[] password = passwordField.getPassword();

        // Validate input fields
        if (username.isEmpty() || password.length == 0) {
            UserFeedback.showWarning("Username and Password must not be empty.");
            return;
        }

        try {
            if (dbManager.login(username, new String(password))) {
                UserFeedback.showInfo("Login successful");
                Session.setLoggedInUser(username);
                EventQueue.invokeLater(() -> {
                    new Levels(dbManager, username);
                    dispose();
                });
            } else {
                UserFeedback.showError("Invalid username or password. Please try again.");
            }
        } catch (SQLException ex) {
            UserFeedback.showError("Database error: " + ex.getMessage());
        } catch (Exception ex) {
            UserFeedback.showError("An unexpected error occurred: " + ex.getMessage());
        }
    }

    private void handleRegistration() {
        String username = usernameField.getText();
        char[] password = passwordField.getPassword();

        // Validate input fields
        if (username.isEmpty() || password.length == 0) {
            UserFeedback.showWarning("Username and Password must not be empty.");
            return;
        }

        try {
            if (dbManager.register(username, new String(password))) {
                UserFeedback.showInfo("Registration successful");
                usernameField.setText("");
                passwordField.setText("");
            } else {
                UserFeedback.showError("Registration failed");
            }
        } catch (SQLException ex) {
            UserFeedback.showError("Database error: " + ex.getMessage());
        } catch (Exception ex) {
            UserFeedback.showError("An unexpected error occurred: " + ex.getMessage());
        }
    }

    private ImageIcon createCircularImageIcon(String path, int diameter) {
        try {
            BufferedImage image = ImageIO.read(new File(path));
            BufferedImage croppedImage = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2 = croppedImage.createGraphics();
            g2.setClip(new Ellipse2D.Float(0, 0, diameter, diameter));
            g2.drawImage(image, 0, 0, diameter, diameter, null);
            g2.dispose();

            return new ImageIcon(croppedImage);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } 
    }

    public static void main(String[] args) {
        DataBaseManager dbManager = new DataBaseManager();
        new Login(dbManager);
    }
}
