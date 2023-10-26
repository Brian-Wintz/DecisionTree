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

    public static String getCategoryForUserInput(String input) {
        String template="Choose a category most relevant to {input} from the following categories {categories} tell me the category only with no other text.";
/***
        String categories=
            "1.1 Small: Size of item is less than or equal to  5 centimeters in height, width or depth. "+
            "1.2 Medium: Size of item is greater than 5 centimeters in height, width or depth and less than or eqal to 20 meters in height, width or depth. "+
            "1.3 Large: Size of item is greater than 20 meters in height, width or depth.";
***/
/***/
            String categories=
            "1.1 Emotional Well-being and Mental Health: Stories that address topics like anxiety, depression, self-esteem, and coping with emotions can help children understand and manage their feelings. These stories often feature relatable characters and scenarios that offer strategies for emotional well-being. "+
            "1.2 Diversity and Inclusion: Stories that celebrate diversity, promote inclusivity, and address issues like racism, discrimination, and bullying can help children learn about empathy, respect, and the value of differences. "+
            "1.3 Resilience and Perseverance: Stories that showcase characters facing adversity, overcoming challenges, and never giving up can inspire children to be resilient and tenacious in their own lives. "+
            "1.4 Friendship and Social Skills: Stories that focus on building and maintaining healthy friendships, resolving conflicts, and improving social skills can be beneficial for children who struggle with peer relationships. "+
            "1.5 Grief and Loss: Stories that deal with themes of grief, loss, and coping with the death of a loved one can provide comfort and guidance for children facing these difficult situations. "+
            "1.6 Education and Learning: Educational stories that explain complex concepts or introduce new subjects in an engaging and understandable way can help children with their studies. "+
            "1.7 Health and Well-being: Stories about healthy eating, exercise, hygiene, and self-care can promote good habits and awareness of personal well-being. "+
            "1.8 Safety and Awareness: Stories that address safety topics like stranger danger, fire safety, and personal boundaries can help children stay safe and informed. "+
            "1.9 Environmental and Conservation Awareness: Stories that teach children about the environment, conservation, and the importance of protecting nature can inspire a sense of responsibility and stewardship. "+
            "1.10 Special Needs and Inclusivity: Stories that feature characters with disabilities or special needs can promote understanding and inclusivity among children, teaching them to appreciate differences and offer support. "+
            "1.11 Career Exploration and Aspirations: Stories that introduce various careers and explore the importance of ambition and hard work can help children think about their future goals. "+
            "1.12 Conflict Resolution and Problem Solving: Stories that depict characters resolving conflicts and finding creative solutions to problems can help children develop critical thinking and conflict resolution skills. "+
            "1.13 Parental Separation and Divorce: Stories that address the challenges children may face when their parents separate or divorce can provide emotional support and guidance. "+
            "1.14 Character-Building and Values: Stories that emphasize important values like honesty, kindness, responsibility, and respect can help instill positive character traits in children. "+
            "1.15 Imaginative Adventures: Create imaginative stories that transport your child to exciting and magical worlds. These stories can be great for encouraging creativity and expanding your child's imagination. "+
            "1.16 Personalized Family Stories: Create a book that tells a story about your own family's adventures or experiences. This could include vacations, special celebrations, or even everyday moments. Personalized stories can be a fantastic way to preserve family memories and share them with your child.";
/***/
        String prompt=template.replace("{input}",input);
        prompt=prompt.replace("{categories}",categories);

        String reply=chatGPT(prompt);
        return reply;

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
        String text="My child is suffering from addiction to Meth";
        String category=getCategoryForUserInput(text);
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
