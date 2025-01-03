package Code.banana.engine;

import Code.banana.engine.Session;

public class LoginScreen {
    
    public void login(String username, String password) {
        // Assuming login validation is successful
        if (loginIsSuccessful(username, password)) {
            // Set the logged-in user
            Session.setLoggedInUser(username);
            
            // Create LevelsScreen instance to show the welcome message
            LevelsScreen levelsScreen = new LevelsScreen();
            levelsScreen.showWelcomeMessage();
        }
    }
    
    // Simulated login check (you would replace this with actual login logic)
    private boolean loginIsSuccessful(String username, String password) {
        // Simulated login check, assuming the credentials are correct
        return true;
    }
}
