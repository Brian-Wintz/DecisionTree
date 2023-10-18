
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.bkw.dt.DecisionTree;
import com.bkw.dt.Question;

public class DecisionTreeServlet extends HttpServlet {

    protected String escapeSQL(String value) {
        value.replace("%","\\%");
        value.replace("_","\\_");
        return value;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        Map<String,String[]> parameterMap=request.getParameterMap();
        String key="1";
        String[] vals=parameterMap.get("key");
        if(vals!=null && vals.length>0) {
            key=vals[0];
        }

        DecisionTree dt=new DecisionTree();
        Question question=dt.getQuestion(key);
        String json=question.toJSONString();
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.println(json);
    }

    public static void main(String[] args) {
        DecisionTree dt=new DecisionTree();

        Question question=dt.getQuestion("1");
        System.out.println(question.toJSONString());
    }

}