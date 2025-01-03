package Code.banana.engine;

import java.awt.image.BufferedImage;

/**
 * Main class where the games are coming from.
 *
 */
public class GameEngine {
    String thePlayer = null;
    int counter = 0;
    int score = 0;
    GameServer theGames = new GameServer();
    Game current = null;

    /**
     * Each player has their own game engine.
     * 
     * @param player
     */
    public GameEngine(String player) {
        thePlayer = player;
    }

    /**
     * Retrieves a game. This basic version only has two games that alternate.
     */
    public BufferedImage nextGame() {
        current = theGames.getRandomGame();
        return current.getImage();
    }

    /**
     * Checks if the parameter i is a solution to the game. If so, score is
     * increased by one.
     * 
     * @param i The player's answer to check.
     * @return true if the answer is correct, false otherwise.
     */
    public boolean checkAnswer(int i) {
        if (current != null && i == current.getSolution()) {
            score++;
            return true;
        }
        return false;
    }

    /**
     * Submits the player's answer and checks if it's correct, updating the score if so.
     * 
     * @param number The player's answer.
     */
    public void submitAnswer(int number) {
        if (checkAnswer(number)) {
            System.out.println("Correct answer! Score: " + score);
        } else {
            System.out.println("Incorrect answer. Try again.");
        }
    }

    /**
     * Retrieves the current score.
     * 
     * @return the player's score.
     */
    public int getScore() {
        return score;
    }

}
