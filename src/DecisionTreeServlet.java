
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bkw.dt.Answer;
import com.bkw.dt.DecisionTree;
import com.bkw.dt.Question;

public class DecisionTreeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String requestURI=request.getRequestURI();

        if(requestURI.endsWith("openai")) {
            Map<String,String[]> parameterMap=request.getParameterMap();
            String message=null;
            String[] vals=parameterMap.get("message");
            if(vals!=null && vals.length>0) {
                message=vals[0];
            }
            if(message!=null) {
                String reply=OpenAI.chatGPT(message);
                String json="{ \"request\": \""+message+"\", \"reply\": \""+reply+"\" }";
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.println(json);
            }

            return;
        }

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

            List<Answer> answers=dt.getSelectedAnswers(key);
            String json="{\n  \"answers\": [\n";
            boolean isFirst=true;
            for(Answer answer: answers) {
                json+=(!isFirst?",\n":"");
                json+=answer.toJSONString();
                isFirst=false;
            }
            json+="\n]}";
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.println(json);
        }

    }

    public static void main(String[] args) {
        DecisionTree dt=new DecisionTree();

        Question question=dt.getQuestion("1");
        System.out.println(question.toJSONString());
    }

}