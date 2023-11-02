package com.bkw.dt;

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
import java.util.List;
import java.util.LinkedList;

public class Question {
    private String key;
    private String text;
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
        String result="{\n  \"question\": {";
        result+="\"key\": \""+this.key+"\"";
        result+=", \"text\": \""+this.text+"\"";
        result+=", \"answers\": [\n";
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