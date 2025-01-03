package Code.banana.engine;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Images {
    private BufferedImage[] imageArray;
    private Random random;

    public Images() throws IOException {
        // Load images from the specified directory
        File[] files = new File("C:\\Users\\Rashmi Denagama\\Pictures\\Fruit Images").listFiles(); // Specify the path to your images directory
        if (files != null) {
            imageArray = new BufferedImage[files.length];
            for (int i = 0; i < files.length; i++) {
                imageArray[i] = ImageIO.read(files[i]);
            }
        }
        random = new Random();
    }

    public BufferedImage getRandomImage() {
        if (imageArray != null && imageArray.length > 0) {
            return imageArray[random.nextInt(imageArray.length)];
        }
        return null; // Return null if no images are available
    }

	public BufferedImage getImageByHashCode(int int1) {
		// TODO Auto-generated method stub
		return null;
	}
}
