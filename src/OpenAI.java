import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class OpenAI {
    public static String encode(String text) {
        char[] chars=text.toCharArray();
        char[] encchars=new char[chars.length];
        for(int i=0; i<chars.length; ++i) encchars[i]=(char)((int)chars[chars.length-1-i]+1);
        String encoded=new String(encchars);
        return encoded;
    }

    public static String decode(String text) {
        char[] chars=text.toCharArray();
        char[] decchars=new char[chars.length];
        for(int i=0; i<chars.length; ++i) decchars[i]=(char)((int)chars[chars.length-1-i]-1);
        String decoded=new String(decchars);
        return decoded;

    }

    public static UserCategory getCategoryForUserInput(String input) {
        String template="Choose a category most relevant to {input} from the following categories {categories} tell me the category only with no other text.";
        String categories=
            "1.1 Small: Size of item is less than or equal to  5 centimeters in height, width or depth. "+
            "1.2 Medium: Size of item is greater than 5 centimeters in height, width or depth and less than or equal to 20 meters in height, width or depth. "+
            "1.3 Large: Size of item is greater than 20 meters in height, width or depth.";
        String prompt=template.replace("{input}",input);
        prompt=prompt.replace("{categories}",categories);

        String reply=chatGPT(prompt);
        String key=reply;
        String category=reply;
        if(reply.length()>3) {
            int index=reply.indexOf(" ");
            key=reply.substring(0,index);
            category=reply.substring(index+1);
        }
        UserCategory uc=new UserCategory();
        uc.setUserInput(input);
        uc.setAIResponse(category);
        uc.setKey(key);

        return uc;

    }

    public static void main(String[] args) {
/***
        String txt="{OpenAI Key}";
        String encoded=encode(txt);
        String decoded=decode(encoded);
        System.out.println("txt="+txt+" encoded="+encoded+" decoded="+decoded);
***/
/***
        String reply="1. Conflict Resolution and Problem Solving";
        String key=getCategoryKey(reply);
        System.out.println("reply:"+reply+" key:"+key);
***/
        String text="Larger than a skyscraper";
        UserCategory category=getCategoryForUserInput(text);
        System.out.println("response:"+category);
    }

    // This method sends a message to the GPT-3.5 Turbo model and retrieves a response.
    public static String chatGPT(String message) {
        String url = "https://api.openai.com/v1/chat/completions";
        String apiKey = decode("izHsE73wKs4PEBGnncFmKGlcmC4UJHypnf8O:WD9zgnkxc1M.lt"); // OpenAI API key
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
