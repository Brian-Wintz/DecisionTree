# DecisionTree
The purpose of this project is to implement a decision tree that is exposed through REST APIs for determining the next (or starting) question along with the question's possible answers.  It also allows for processing an answer to provide all the answers that lead up to the specified answer.

A decision tree is made of a tree of questions and answers as illustrated below:

![image](https://github.com/Brian-Wintz/DecisionTree/assets/133924124/2f8832f6-6247-4a05-90a2-e5659dbd8428)

The circles denote the qustions and the lines represent the possible answers for each question.  Note that there are terminal answers which indicate the decision tree has been navigated to a final answer.  The red lines highlight a potential path to one of the terminal answers for which each answer in the path, including the final answer, can be retrieved.

