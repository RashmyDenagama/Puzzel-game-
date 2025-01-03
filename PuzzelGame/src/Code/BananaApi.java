package Code;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BananaApi {
    private static final String API_URL = "https://api.banana.com/v1/random-images";  // Replace with actual API

    public static String[] fetchImages(int numImages) throws Exception {
        URL url = new URL(API_URL + "?count=" + numImages);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        // Assuming the API returns a JSON array of image URLs
        // For simplicity, we're converting to a string array here
        return content.toString().replace("[", "").replace("]", "").replace("\"", "").split(",");
    }
}
