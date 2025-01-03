package Code.banana.engine;


	import java.awt.image.BufferedImage;

	/**
	 * A Game is an image (BufferedImage) and an integer. 
	 * The integer is the solution of the game that is described in the image.
	 * 
	 */
	public class Game {
	    private BufferedImage image; 	
	    private int solution;
		
	    /**
	     * Image of the game and the solution to the game.
	     * @param image The game image.
	     * @param solution The solution to the game.
	     */
	    public Game(BufferedImage image, int solution) {
	        this.image = image;
	        this.solution = solution;
	    }
		
	    /**
	     * Returns the image of the game.
	     * @return The game image.
	     */
	    public BufferedImage getImage() {
	        return image;
	    }

	    /**
	     * @return The solution of the game.
	     */
	    public int getSolution() {
	        return solution;
	    }
	}



