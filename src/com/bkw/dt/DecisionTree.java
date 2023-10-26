package com.bkw.dt;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class DecisionTree {
    private static Map<String,Question> questions;
    private static Map<String,Answer> answers;

    public DecisionTree() {
    }

    public DecisionTree(List<Question> questions) {
        if(questions!=null) return;
        setDecisionTree(questions);
    }

    public void setDecisionTree(List<Question> qstns) {
        DecisionTree.questions=new HashMap<String,Question>();
        DecisionTree.answers=new HashMap<String,Answer>();
        this.addQuestions(qstns);
    }

    public void reset() {
        DecisionTree.questions=null;
        DecisionTree.answers=null;
    }

    public void readFile(String filename) {
        if(DecisionTree.questions!=null) return;

        // Find the specified file on the classpath
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        java.net.URL url=classLoader.getResource(filename);
        String file=(url!=null?url.getFile():null);
        //System.out.println("URL:"+url+":"+file);

        BufferedReader reader;
        List<Question> qstns=new LinkedList<Question>();
        Question question=null;

		try {
			reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();

			while (line != null) {
                if(line.startsWith("Q")) {
                    question=createQuestion(line);
                    qstns.add(question);
                } else {
                    if(line.startsWith("A")) {
                        Answer answer=createAnswer(line,question.getKey(),false);
                        if(question!=null) question.addAnswer(answer);
                    } else {
                        if(line.startsWith("T")) {
                            Answer answer=createAnswer(line,question.getKey(),true);
                            if(question!=null) question.addAnswer(answer);
                        }
                    }

                }
				// read next line
				line = reader.readLine();
			}

			reader.close();
            setDecisionTree(qstns);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public void addQuestions(List<Question> qstns) {
        // First add all the questions to the map, by question key
        for(Question question: qstns) {
            questions.put(question.getKey(),question);
        }

        // Next, resolve the parent answer key for each question
        for(Question question: qstns) {
            List<Answer> answers=question.getAnswers();
            for(Answer answer: answers) {
                DecisionTree.answers.put(answer.getKey(),answer);
                String nextQuestionKey=answer.getNextQuestionKey();
                if(nextQuestionKey!=null) {
                    Question q=questions.get(nextQuestionKey);
                    q.setLastAnswerKey(answer.getKey());
                }
            }
        }

    }

    public Question getQuestion(String key) {
        if(key==null) key="1";
        return questions.get(key);
    }

    public List<Question> getSelectedAnswers(String answerKey) {
        List<Question> selectedAnswers=new LinkedList<Question>();
        while(answerKey!=null) {
            Answer answer=answers.get(answerKey);
            if(answer!=null) {
                // Create a new Question from this answer's question with only the key and text values set
                Question question=questions.get(answer.getLastQuestionKey());
                Question qstn=new Question(question.getKey(),question.getText());
                // Create a new Answer instance from this answer with only the key and text values set
                Answer answr=new Answer(answer.getKey(),answer.getText(),null);
                qstn.addAnswer(answr);
                // Since adding the answer to the question assigns the lastQuestionKey value, need to null it out afterwards
                answr.setLastQuestionKey(null);

                selectedAnswers.add(qstn);
/***
                selectedAnswers.add(question);
                selectedAnswers.add(answer);
***/
                answerKey=question.getLastAnswerKey();
            } else {
                answerKey=null;
            }
        }
        return selectedAnswers;
    }

    @Override
    public String toString() {
        String result="";
        Set<String> keys=questions.keySet();
        for(String key: keys) {
            Question question=questions.get(key);
            result+=(result.length()>0?"\n":"")+question.toString();
        }
        return result;
    }

    public static Question createQuestion(String text) {
        Question question=null;
        int index1=text.indexOf(" ");
        int index2=text.indexOf(" ",index1+1);
        String key=text.substring(index1+1, index2);
        String txt=text.substring(index2+1);
        question=new Question(key,txt);
        return question;
    }

    public static Answer createAnswer(String text,String parentQuestionKey,boolean isTerminal) {
        Answer answer=null;
        int index1=text.indexOf(" ");
        int index2=text.indexOf(" ",index1+1);
        String key=text.substring(index1+1, index2);
        String txt=text.substring(index2+1);
        answer=new Answer(key,txt,(!isTerminal?key:null));
        answer.setLastQuestionKey(parentQuestionKey);
        return answer;
    }

    public static void main(String[] args) {
        DecisionTree dt=new DecisionTree();

/***
        System.out.println(dt.toString());

        Question q=dt.getQuestion("1");
        List<Answer> answers=q.getAnswers();

        System.out.println("SELECTED=B1"+dt.getSelectedAnswers("B1"));
        //System.out.println("First Answer:"+answers.get(0)+":"+dt.getQuestion("1.1"));
        //System.out.println("Second Answer:"+answers.get(1)+":"+dt.getQuestion("1.2"));
        //System.out.println("Third Answer:"+answers.get(2)+":"+dt.getQuestion("1.3"));
***/
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        dt.reset();
        dt.readFile("com/bkw/dt/dt1.txt");
        System.out.println("dt:"+dt);
    }
}
