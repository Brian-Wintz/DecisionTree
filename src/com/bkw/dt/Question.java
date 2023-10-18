package com.bkw.dt;

import java.util.List;
import java.util.LinkedList;

public class Question {
    private String text;
    private String key;
    private List<Answer> answers;
    private String lastAnswerKey;

    public Question(String key,String text) {
        this.key=key;
        this.text=text;
        answers=new LinkedList<Answer>();
        this.lastAnswerKey=null;
    }

    public Question(String key,String text,List<Answer> answers) {
        this.key=key;
        this.text=text;
        for(Answer answer: answers) {
            answer.setLastQuestionKey(key);
        }
        this.answers=answers;
        this.lastAnswerKey=null;
    }

    public void addAnswer(Answer answer) {
        if(answers==null) answers=new LinkedList<Answer>();
        answer.setLastQuestionKey(this.key);
        answers.add(answer);
    }

    public String getKey() {
        return key;
    }

    public String getText() {
        return text;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setLastAnswerKey(String lastAnswerKey) {
        this.lastAnswerKey=lastAnswerKey;
    }

    public String getLastAnswerKey() {
        return lastAnswerKey;
    }

    public String toJSONString() {
        String result="{\n  \"Question\": {";
        result+="\n    \"Key\": \""+this.key+"\"";
	    result+=",\n    \"Text\": \""+this.text+"\"";
        result+=",\n    \"Answers\": [\n";
        boolean isFirst=true;
        for(Answer answer: answers) {
            result+=(!isFirst?",\n":"");
            result+=answer.toJSONString();
            isFirst=false;
        }
	    result+="\n]}}";
	    return result;
    }

    @Override
    public String toString() {
        String result=key+":"+text;
        for(Answer answer: answers) {
            result+="\n"+answer.toString();
        }
        return result;
    }
}