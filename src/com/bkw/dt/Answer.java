package com.bkw.dt;

public class Answer {
    private String key;
    private String text;
    private String nextQuestionKey;
    private String lastQuestionKey;

    public Answer(String key,String text,String nextQuestionKey) {
        this.key=key;
        this.text=text;
        this.nextQuestionKey=nextQuestionKey;
        this.lastQuestionKey=null;
    }

    public String getKey() {
        return key;
    }

    public String getText() {
        return text;
    }

    public String getNextQuestionKey() {
        return nextQuestionKey;
    }

    public String getLastQuestionKey() {
        return lastQuestionKey;
    }

    public void setLastQuestionKey(String lastQuestionKey) {
        this.lastQuestionKey=lastQuestionKey;
    }

    public String toJSONString() {
        String result="      {";
        result+="\n        \"key\": \""+this.key+"\"";
        result+=",\n        \"text\": \""+this.text+"\"";
        if(this.nextQuestionKey!=null)
            result+=",\n        \"nextQuestionKey\": \""+this.nextQuestionKey+"\"";
        if(this.lastQuestionKey!=null)
            result+=",\n        \"lastQuestionKey\": \""+this.lastQuestionKey+"\"";
        result+="\n      }";
        return result;
    }

    @Override
    public String toString() {
        return key+":"+text+":"+(nextQuestionKey==null?"<null>":nextQuestionKey+":"+(lastQuestionKey==null?"<null>":lastQuestionKey));
    }
}
