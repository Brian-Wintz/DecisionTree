import com.google.gson.Gson;

public class UserCategory {
    private String userInput;
    private String aiResponse;

    public void setUserInput(String input) {
        userInput=input;
    }

    public String getUserInput() {
        return userInput;
    }

    public void setAIResponse(String response) {
        aiResponse=response;
    }

    public String getAIResponse() {
        return aiResponse;
    }

    public UserCategory() {

    }

    public static void main(String[] args) {
        UserCategory uc=new UserCategory();
        uc.setUserInput("testing, \"1,2,3\"");
        uc.setAIResponse("just a test!");

        Gson gson=new Gson();
        String json=gson.toJson(uc);
        System.out.println("json:"+json);

        UserCategory uc2=gson.fromJson(json, UserCategory.class);
        System.out.println("UserCategory:"+uc2.getUserInput()+":"+uc2.getAIResponse());

    }
}
