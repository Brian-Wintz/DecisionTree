
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bkw.dt.Answer;
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
System.out.println("##JSON1:"+json);
            //json=java.net.URLDecoder.decode(json, StandardCharsets.UTF_8.toString());
            Gson gson=new Gson();
            UserCategory uc=gson.fromJson(json, UserCategory.class);
System.out.println("##UC:"+uc);
            String userInput=uc.getUserInput();
            String reply=OpenAI.getCategoryForUserInput(userInput);
            uc.setAIResponse(reply);
            json=gson.toJson(uc);
System.out.println("##JSON2:"+json);
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
        //Question question=dt.getQuestion("1");
        //System.out.println(question.toJSONString());
        String json=convertAnswersToJSON(dt,"1.3.2.3");
        System.out.println("json:"+json);
    }

}