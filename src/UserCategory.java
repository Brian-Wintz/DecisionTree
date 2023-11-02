import com.google.gson.Gson;

/**
 * Copyright (C) 2023 Brian Wintz.
 * <p/>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301, USA.
 */
public class UserCategory {
    private String userInput;
    private String aiResponse;
    private String key;

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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key=key;
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
