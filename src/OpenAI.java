import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class OpenAI {
    public static void main(String[] args) {
        // Print the response from the chatGPT method to the console.
        System.out.println(chatGPT("hello, how are you?"));
    }

    // This method sends a message to the GPT-3.5 Turbo model and retrieves a response.
    public static String chatGPT(String message) {
        String url = "https://api.openai.com/v1/chat/completions";
        String apiKey = "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"; // Replace with your OpenAI API key
        String model = "gpt-3.5-turbo"; // The model version for chatGPT

        try {
            // Create an HTTP POST request to the OpenAI API.
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + apiKey);
            con.setRequestProperty("Content-Type", "application/json");

            // Build the request body in JSON format.
            String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + message + "\"}]}";
            con.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            writer.write(body);
            writer.flush();
            writer.close();

            // Get the response from the OpenAI API.
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Extract and return the response from the API.
            return extractContentFromResponse(response.toString());
        } catch (IOException e) {
            // Handle exceptions, e.g., if there is an issue with the HTTP request.
            throw new RuntimeException(e);
        }
    }

    // This method extracts the content part of the response from the OpenAI API.
    public static String extractContentFromResponse(String response) {
        int startMarker = response.indexOf("content") + 11; // Find where the content starts.
        int endMarker = response.indexOf("\"", startMarker); // Find where the content ends.
        return response.substring(startMarker, endMarker); // Return the extracted response.
    }
}
