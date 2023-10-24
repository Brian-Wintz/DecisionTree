# DecisionTree
The purpose of this project is to implement a decision tree that is exposed through REST APIs for determining the next (or starting) question along with the question's possible answers.  It also allows for processing an answer to provide all the answers that lead up to the specified answer.

A decision tree is made of a tree of questions and answers as illustrated below:

![image](https://github.com/Brian-Wintz/DecisionTree/assets/133924124/2f8832f6-6247-4a05-90a2-e5659dbd8428)

The circles denote the qustions and the lines represent the possible answers for each question.  Note that there are terminal answers which indicate the decision tree has been navigated to a final answer.  The red lines highlight a potential path to one of the terminal answers for which each answer in the path, including the final answer, can be retrieved.

## Implementation
com.bkw.dt.DecisionTree - holds a static HashMap containing the loaded decision tree of questions and answers
com.bkw.dt.Answer- represents an answer option for a given question and has key pointers to the originating/parent question and the associated child question.  If there is not child question then it is a terminating answer which will have a null pointer for the child question
com.bkw.dt.Question - represents a question which which is associated with 2 or more Answer instances, as well as a answer key which points to the answer which resulted in this question (or null, if top level question)

The details for these classes is provided below:

### com.bkw.dt.Answer

#### Attributes
    private String key: A unique identifier for the Answer instance.  This key value is the same as the associated child question's key value using a "N[.N]..." format.  Since a terminal answer does not have a child question, it uses a key that the child question would have if there was one.
    private String text: The text which specifies the answer for this Answer instance.
    private String nextQuestionKey: The key to the child question, or null if this is a terminating answer.
    private String lastQuestionKey: A pointer back to the question which resulted in this answer.

#### Constructors
    public Answer(String key,String text,String nextQuestionKey): Instantiates an Answer with a null source question.  Source question is assigned by calling setLastQuestionKey (see below).

#### Methods
    public String getKey(): Retrieves this Answer instance's key value
    public String getText(): Retrieves this Answer instance's answer text
    public String getNextQuestionKey():  Retrieves this Answer instance's child question key value
    public String getLastQuestionKey(): Retrieves this Answer instance's originating question key value
    public void setLastQuestionKey(String lastQuestionKey): Assigns this Answer instance's originating key value
    public String toJSONString(): Converts this Answer instance into a JSON String snippet
    public String toString(): Generates a String containing this Answer instance's attribute values (not in JSON)

### com.bkw.dt.Question

#### Attributes
    private String key: A unique identifier for the Question instance in "N[.N]..." format.
    private String text: The text for this Question instance.
    private List<Answer> answers: The collection of Answer instances for this Question instance.
    private String lastAnswerKey: The Answer key which resulted in this Question, or null for the top level Question (which won't have an associated originating answer)

#### Constructors
    public Question(String key,String text): Creates a Question instance assigning only the key and text attribute values, answers are added later using the addAnswer method (see below).
    public Question(String key,String text,List<Answer> answers): Creates a Question instance with the provided collection of Answer instances.

#### Methods
    public void addAnswer(Answer answer): Adds an Answer instance to this question.
    public String getKey(): Retrieves the key value for this Question instance.
    public String getText(): Retrieves the question text value for this Question instance.
    public List<Answer> getAnswers():: Retrieves the collection of answers for this Question instance.
    public void setLastAnswerKey(String lastAnswerKey): Assigns the answer key for the Answer which resulted in this question.
    public String getLastAnswerKey(): Retrieves the answer key for the Answer which resulted in this question.
    public String toJSONString(): Generates a JSON String for this Question instance, including its collection of Answer instances.
    public String toString(): Generates a String Containing this Question instance's attribute values and each Answer instance in its collection of answers (not in JSON).

### com.bkw.dt.DecisionTree

####  Attributes
    private static Map<String,Question> questions: A map of Question instances by question key value.  This value is static to allow for sharing of a previously loaded map without incurring the overhead of reloading the data.
    private static Map<String,Answer> answers: A map of Answer instances by answer key value.  Note that each question has its Answer instances, but this map allows for efficient lookup for finding an Answer instance.  This value is static to allow for sharing of a previously loaded map without incurring the overhead of reloading the data.

#### Constructors
    public DecisionTree(): Creates a "dummy" DecisionTree instance which will either use previously loaded values into the maps or have these values loaded the first time using the readFile method (see below).
    public DecisionTree(List<Question> questions): Creates a DecisionTree instance with the provided collection of Question instances, which will have their associated Answer instances specified.

#### Methods
    public void setDecisionTree(List<Question> qstns): Assigns the DecisionTree instance's questions and answers maps
    public void reset(): Resets the static questions and answers map values to null so that they will get reloaded.
    public void readFile(String filename): If the questions map has not already been loaded (is null), then processes loading the questions and answers maps from the specified file (see below for file format details)
    public void addQuestions(List<Question> qstns): Processes the provided collection of questions and adds them to the existing values in these maps.
    public Question getQuestion(String key): Retrieves a Question by its key.
    public List<Answer> getSelectedAnswers(String answerKey): Based on a given answer key, returns a collection of all the answers including the specified answer.  Returns an empty collection if the answer does not exist.
    public String toString(): Generates a String for the questions and answers in this DecisionTree
    public static Question createQuestion(String text): From a question line in a decision tree input file, creates a Question instance (see below for file format details)
    public static Answer createAnswer(String text,String parentQuestionKey,boolean isTerminal): From an answer or terminal answer in a decision tree input file, Creates an Answer instance with the specified parent question key (see below for file format details)

### OpenAI
    public static String chatGPT(String message): Process the provided message as a prompt to ChatGPT 3.5 and returns the resulting reply.
    public static String extractContentFromResponse(String response): Extracts the reply from the response to a ChatGPT 3.5 REST API call.
    public static String getCategoryForUserInput(String input): Calls OpenAI to determine the category which best matches user input.
### UserCategory

####  Attributes
    private String userInput: Used to hold the user's input text
    private String aiResponse: Used to hold OpenAI's selected category based on the user's input
#### Methods
    public void setUserInput(String input): Assigns the user's input
    public String getUserInput(): Retrieves the user's input
    public void setAIResponse(String response): Assigns the returned text for which category was selected by OpenAI for the user's text
    public String getAIResponse(): Retrieves the OpenAI selected category

### DecisionTreeServlet

#### Methods
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException: Processes a GET REST API request which supports the following REST requests:
    question[?key={question key value}]: Returns specified question key data, or base question if key is not specified
    answers?key={answer key value}: Returns all answers within the decision tree which result in the specified answer (including the specified answer)
    reset: Forces a reload of the decision tree, sets the decision tree's internal questions and maps values to null to cause them to be reloaded
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    usercategory: Takes user input as posted JSON and returns the best matching category
    Posted JSON input:
        { "userInput": "My son is feeling sad and suffering from anxiety due to a drug addiction" }
    Resulting JSON output:
        {
        "userInput": "My son is feeling sad and suffering from anxiety due to a drug addiction",
        "aiResponse": "1. Emotional Well-being and Mental Health"
        }
        
## Decision Tree File Format

The general format for a line of text in this file is as follows:

   {Q|A|T} {key} {text}
   
Where {Q|A|T} determines what the line is for:

   Q: A question
   A: An answer
   T: A terminating answer (no child questions)
   
The {text} value represents the text corresponding to either the question or the answer

Note that the lines for all answers must come immediately after the question which they are for.  So the following represents a valid decision tree file format:
```
Q 1 How big is it?
A 1.1 Small
A 1.2 Medium
Q 1.1 Is it a?
T 1.1.1 Atom
T 1.1.2 Quark
Q 1.2 Is it a?
T 1.2.1 Rabit
T 1.2.2 Squirrel
```
Whereas the following is not correct because all the answers do not occur immediately after the question which parents them (question 1.1, with its answers comes before answer 1.2):
```
Q 1 How big is it?
A 1.1 Small
Q 1.1 Is it a?
T 1.1.1 Atom
T 1.1.2 Quark
A 1.2 Medium
Q 1.2 Is it a?
T 1.2.1 Rabit
T 1.2.2 Squirrel
```
A sample dt1.txt decision tree is available in the src/com/bkw/dt directory which is used for testing.

### REST API Testing
These REST APIs have been deployed to a google cloud VM and can be accessed through a web browser by calling the following URLs:

Retrieve a question with its answers (key is not needed if requesting a top level question).  The question key values are of the form "N[.N]..." where each question's answer points to the next level question key:<br><br>
    http://34.82.132.4/decisiontree/question[?key={question key}]

Retrieve the chain of answers for a given answer key, including the specified answer.  Answer key values are also in the "N[.N]..." form.<br><br>
    http://34.82.132.4/decisiontree/answers?key={answer key}

Clear out the decision tree to force a reload of the data from the file (used to deploy changes with a new decision tree file without bringing the system down).<br><br>
    http://34.82.132.4/decisiontree/reset

Make a prompt request to OpenAI.<br><br>
    http://34.82.132.4/decisiontree/openai?message=Say%20hello
