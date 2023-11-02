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
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bkw.dt.DecisionTree;
import com.bkw.dt.Question;
import com.google.gson.Gson;

public class DecisionTreeServlet extends HttpServlet {

        @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        String requestURI=request.getRequestURI();

        // Retrieve a category using AI to pick which category best matches the user input
        if(requestURI.endsWith("usercategory")) {
            // Extract json from posted data and convert to a new TranslatedString instance
            Reader body=request.getReader();
            StringBuilder sb=new StringBuilder();
            char c[]=new char[100];
            try {
                int chars=body.read(c);
                while(chars>0) {
                    sb.append(c,0,chars);
                    chars=body.read(c);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
            String json=sb.toString();
            //json=java.net.URLDecoder.decode(json, StandardCharsets.UTF_8.toString());
            Gson gson=new Gson();
            UserCategory uc=gson.fromJson(json, UserCategory.class);
            String userInput=uc.getUserInput();
            uc=OpenAI.getCategoryForUserInput(userInput);
            json=gson.toJson(uc);
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.println(json);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String requestURI=request.getRequestURI();

        DecisionTree dt=new DecisionTree();
        // Resets the decision tree's internal maps to null to force a subsequent reload of data
        if(requestURI.endsWith("reset")) {
            dt.reset();
            return;
        }

        // Load the decision tree, if not previously loaded or has been reset
        dt.readFile("com/bkw/dt/dt1.txt");

        // Retrieve question for specified question key
        if(requestURI.endsWith("question")) {
            Map<String,String[]> parameterMap=request.getParameterMap();
            String key="1";
            String[] vals=parameterMap.get("key");
            if(vals!=null && vals.length>0) {
                key=vals[0];
            }

            Question question=dt.getQuestion(key);
            String json=question.toJSONString();
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.println(json);
        }

        // Retrieve chain of answers for specified answer key (including specified answer)
        if(requestURI.endsWith("answers")) {
            Map<String,String[]> parameterMap=request.getParameterMap();
            String key="";
            String[] vals=parameterMap.get("key");
            if(vals!=null && vals.length>0) {
                key=vals[0];
            }

            String json=convertAnswersToJSON(dt,key);
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.println(json);
        }

    }

    public static String convertAnswersToJSON(DecisionTree dt,String answerKey) {
            List<Question> answers=dt.getSelectedAnswers(answerKey);
            String json="{\n  \"answers\": [\n";
            boolean isFirst=true;
            for(Question question: answers) {
                json+=(!isFirst?",\n":"");
                json+=question.toJSONString();
                isFirst=false;
            }
            json+="\n]}";
        return json;
    }

    public static void main(String[] args) {
        DecisionTree dt=new DecisionTree();
        dt.readFile("com/bkw/dt/dt1.txt");
        String json=convertAnswersToJSON(dt,"1.3.2.3");
        System.out.println("json:"+json);
    }

}