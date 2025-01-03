package Code.banana.engine;

public class Session {
    private static String loggedInUser;
    private static boolean isFirstLogin = true; // Flag to track if it's the user's first login

    // Method to get the logged-in user
    public static String getLoggedInUser() {
        return loggedInUser;
    }

    // Method to set the logged-in user
    public static void setLoggedInUser(String username) {
        loggedInUser = username;
    }

    // Method to check if a user is logged in
    public static boolean isUserLoggedIn() {
        return loggedInUser != null && !loggedInUser.isEmpty();
    }

    // Method to log out the user
    public static void logout() {
        loggedInUser = null;
        isFirstLogin = true; // Reset for next login session
    }

    // Method to check if it's the user's first login
    public static boolean isFirstLogin() {
        return isFirstLogin;
    }

    // Set first login flag to false
    public static void setFirstLoginFalse() {
        isFirstLogin = false;
    }
}
