package com.bkw.dt;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class DecisionTree {
    public Map<String,Question> dt;
    public Map<String,Answer> answers;

    public DecisionTree() {
        List<Question> questions=new LinkedList<Question>();
        Question q=new Question("1","How big is it?");
        q.addAnswer(new Answer("A1","Small","1.1"));
        q.addAnswer(new Answer("A2","Medium","1.2"));
        q.addAnswer(new Answer("A3","Large","1.3"));
        questions.add(q);

        q=new Question("1.3","Is it a?");
        q.addAnswer(new Answer("B1","Whale"));
        q.addAnswer(new Answer("B2","Elephant"));
        questions.add(q);

        q=new Question("1.2","Is it a?");
        q.addAnswer(new Answer("C1","Rabbit"));
        q.addAnswer(new Answer("C2","Squirrel"));
        questions.add(q);

        q=new Question("1.1","Is it a?");
        q.addAnswer(new Answer("C1","Atom"));
        q.addAnswer(new Answer("C2","Quark"));
        questions.add(q);

        dt=new HashMap<String,Question>();
        this.answers=new HashMap<String,Answer>();
        this.addQuestions(questions);

    }
    public DecisionTree(List<Question> questions) {
        dt=new HashMap<String,Question>();
        this.answers=new HashMap<String,Answer>();
        this.addQuestions(questions);
    }

    public void addQuestions(List<Question> questions) {
        // First add all the questions to the map, by question key
        for(Question question: questions) {
            dt.put(question.getKey(),question);
        }

        // Next, resolve the parent answer key for each question
        for(Question question: questions) {
            List<Answer> answers=question.getAnswers();
            for(Answer answer: answers) {
                this.answers.put(answer.getKey(),answer);
                String nextQuestionKey=answer.getNextQuestionKey();
                if(nextQuestionKey!=null) {
                    Question q=dt.get(nextQuestionKey);
                    q.setLastAnswerKey(answer.getKey());
                }
            }
        }

    }
    public Question getQuestion(String key) {
        if(key==null) key="1";
        return dt.get(key);
    }

    public List<Answer> getSelectedAnswers(String answerKey) {
        List<Answer> selectedAnswers=new LinkedList<Answer>();
        while(answerKey!=null) {
            Answer answer=answers.get(answerKey);
            if(answer!=null) {
                selectedAnswers.add(answer);
                Question question=dt.get(answer.getLastQuestionKey());
                answerKey=question.getLastAnswerKey();
            }
        }
        return selectedAnswers;
    }

    @Override
    public String toString() {
        String result="";
        Set<String> keys=dt.keySet();
        for(String key: keys) {
            Question question=dt.get(key);
            result+=(result.length()>0?"\n":"")+question.toString();
        }
        return result;
    }

    public static void main(String[] args) {
        DecisionTree dt=new DecisionTree();

        System.out.println(dt.toString());

        Question q=dt.getQuestion("1");
        List<Answer> answers=q.getAnswers();

        System.out.println("SELECTED=B1"+dt.getSelectedAnswers("B1"));
        //System.out.println("First Answer:"+answers.get(0)+":"+dt.getQuestion("1.1"));
        //System.out.println("Second Answer:"+answers.get(1)+":"+dt.getQuestion("1.2"));
        //System.out.println("Third Answer:"+answers.get(2)+":"+dt.getQuestion("1.3"));

        
    }
}
