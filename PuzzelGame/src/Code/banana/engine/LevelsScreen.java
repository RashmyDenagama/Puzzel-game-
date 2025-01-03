package Code.banana.engine;

import Code.banana.engine.Session;
import javax.swing.JOptionPane;

public class LevelsScreen {
    
    public void showWelcomeMessage() {
        String username = Session.getLoggedInUser();

        if (Session.isFirstLogin()) {
            // Show the "Welcome" message for the first login
            JOptionPane.showMessageDialog(null, "Welcome " + username, "Welcome", JOptionPane.INFORMATION_MESSAGE);
            // Set the flag to false to indicate it's no longer the first login
            Session.setFirstLoginFalse();
        } else {
            // Show the "Welcome Back" message for subsequent logins
            JOptionPane.showMessageDialog(null, "Welcome back " + username, "Welcome", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
