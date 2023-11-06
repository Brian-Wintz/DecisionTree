# DecisionTree
The purpose of this project is to implement a decision tree that is exposed through REST APIs for determining the next (or starting) question along with the question's possible answers.  It also allows for processing an answer to provide all the questions and answers that lead up to the specified answer.

A decision tree is made of a tree of questions and answers as illustrated below:

![image](https://github.com/Brian-Wintz/DecisionTree/assets/133924124/2f8832f6-6247-4a05-90a2-e5659dbd8428)

The circles denote the qustions and the lines represent the possible answers for each question.  Note that there are terminal answers which indicate the decision tree has been navigated to a final answer.  The red lines highlight a potential path to one of the terminal answers for which each answer and question in the path, including the final answer, can be retrieved.

## Tomcat Setup
Before installing tomcat you need to have a compatible version of Java installed.  For my testing I used the free OpenJDK 21.0.1 version which you can download as a gzip'ed tar file here: https://download.java.net/java/GA/jdk21.0.1/415e3f918a1f4062a0074a2794853d0d/12/GPL/openjdk-21.0.1_linux-x64_bin.tar.gz.  Once this downloaded file has been uploaded to the linux VM it can be extracted by executing "tar -xzf openjdk-21.0.1_linux-x64_bin.tar.gz" which will install the tomcat files into the current directory.  I created a java directory under my home directory and extracted the files there which created a jdk-21.0.1 sub-directory.  In order to use this java install you'll need to create a new JAVA_HOME and update the existing PATH environment variables as shown below:

    export JAVA_HOME=~/java/jdk-21.0.1
    export JDK_HOME=~/java/jdk-21.0.1
    export PATH=$PATH:$JAVA_HOME/bin

These can be added to the .profile file within the user's home directory (~/.profile) so that they get set automatically when the user logs onto the VM.  However, note that if you switch to another user, such as using the root user in order to start/stop tomcat, then these environment variables will need to be set.

Tomcat 8.5.95 (version used for testing) can be dowloaded as a gzip'ed tar file here: https://dlcdn.apache.org/tomcat/tomcat-8/v8.5.95/bin/apache-tomcat-8.5.95.tar.gz.  Once the downloaded file has been uploaded to the linux VM it can be extracted by executing tar -xzf apache-tomcat-8.5.95.tar.gz, which will install the tomcat files into the current directory.

In order to have tomcat run on the normal HTTP and HTTPS ports it is necessary to first modify the conf/server.xml to use the standard ports 80 (HTTP) and 443 (HTTPS).  By default, tomcat comes configured to use port 8080 for HTTP and 8443 for HTTPS.  The modifications to conf.server.xml are **highlighted** below:

    <Connector port="**80**" protocol="HTTP/1.1"
               connectionTimeout="20000"
               redirectPort="**443**"
               maxParameterCount="1000"
               />'
Note that in order to run tomcat on these ports it is necessary to run them as the root user by first running "sudo bash" to start a bash shell running as root.  In doing this you may loose environment variables, such as JAVA_HOME, which will need to be set for this root shell.  To leave the shell, enter "exit" (without quotes), which will place you back in the user's login shell.  Within this shell you can start tomcat using the bin/startup.sh script or stop tomcat by running the bin/shutdown.sh script.

### Files Copied into Tomcat

### Dependent gson jar file
Copy the gson*.jar file from the lib github project to the {tomcat_home}/lib directory.  This makes this jar available to all web applications running in this tomcat instance

### Compiled *.class files
Copy the *.class files from the github target directory to the {tomcat_home}/webapps/{webapp_name}/WEB-INF/classes directory.  For this example I use "myapp" as the name for my {webapp_name}.  If you have not previously done so, you will need to create the {webapp_name}/WEB-INF/classes sub-directories by executing mkdir -p {webapp_name}/WEB-INF/classes (using the name of your web application in place of {webapp_name}).
Copy the *.class files and dt1.txt file from the github target/com/qad/dt directory to the {tomcat_home}/webapps/{webapps_name}/WEB-INF/classes/com/qad/dt directory. Of you have not previously done so, you will need to create the com/bkw/dt sub-directories by executing mkdir -p com/bkw/dt from the {webapp_name}/WEB-INF/classes directory.

## Implementation
* com.bkw.dt.DecisionTree - holds a static HashMap containing the loaded decision tree of questions and answers
* com.bkw.dt.Answer- represents an answer option for a given question and has key pointers to the originating/parent question and the associated child question.  If there is not child question then it is a terminating answer which will have a null pointer for the child question
* com.bkw.dt.Question - represents a question which which is associated with 2 or more Answer instances, as well as a answer key which points to the answer which resulted in this question (or null, if top level question)

The details for these classes are provided below:

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
    public static UserCategory getCategoryForUserInput(String input): Calls OpenAI to determine the category which best matches user input.

### UserCategory

####  Attributes
    private String userInput: Used to hold the user's input text
    private String aiResponse: Used to hold OpenAI's selected category based on the user's input
    private String key: The key for the resulting category
    
#### Methods
    public void setUserInput(String input): Assigns the user's input
    public String getUserInput(): Retrieves the user's input
    public void setAIResponse(String response): Assigns the returned text for which category was selected by OpenAI for the user's text
    public String getAIResponse(): Retrieves the OpenAI selected category
    public void setKey(String key): Assigns the category's key
    public String getKey(): Retrieves the category's key

### DecisionTreeServlet

#### Methods
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException: Processes a GET REST API request which supports the following REST requests:
    question[?key={question key value}]: Returns specified question key data, or base question if key is not specified
    answers?key={answer key value}: Returns all answers within the decision tree which result in the specified answer (including the specified answer)
    reset: Forces a reload of the decision tree, sets the decision tree's internal questions and maps values to null to cause them to be reloaded
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    usercategory: Takes user input as posted JSON and returns the best matching category
    Posted JSON input:
        { "userInput": "Larger than a skyscraper" }
    Resulting JSON output:
        {
        "userInput": "Larger than a skyscraper",
        "aiResponse": "1.3 Large"
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
A 1.1 Small: Size of item is less than or equal to  5 centimeters in height, width or depth.
A 1.2 Medium: Size of item is greater than 5 centimeters in height, width or depth and less than or equal to 20 meters in height, width or depth.
A 1.3 Large: Size of item is greater than 20 meters in height, width or depth.Q 1.1 Is it a?
Q 1.1 Is it a?
T 1.1.1 Atom
T 1.1.2 Quark
Q 1.2 Is it a?
T 1.2.1 Rabbit
T 1.2.2 Squirrel
Q 1.3 Is it larger than a house?
A 1.3.1 Yes
A 1.3.2 No
Q 1.3.1 Is it a?
T 1.3.1.1 Whale
T 1.3.1.2 Dinosaur
Q 1.3.2 Is it a?
T 1.3.2.1 Truck
T 1.3.2.2 House
T 1.3.2.3 Elephant
```
Note that the initial question's answers have additional detail which is passed to OpenAI with the user's free form text to determine which answer best represents their input.

The following is not correct because all the answers do not occur immediately after the question which parents them (question 1.1, with its answers comes before answer 1.2):
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
These REST APIs have been deployed to a google cloud VM under a myapp web application and can be accessed through a web browser by calling the following URLs:

Retrieve a question with its answers (key is not needed if requesting a top level question).  The question key values are of the form "N[.N]..." where each question's answer points to the next level question key:<br><br>
    http://34.82.132.4/myapp/decisiontree/question[?key={question key}]

    http://34.82.132.4/myapp/decisiontree/question?key=1.3

    {
      "question": {"key": "1.3", "text": "Is it larger than a house?", "answers": [
          {"key": "1.3.1", "text": "Yes", "nextQuestionKey": "1.3.1", "lastQuestionKey": "1.3"},
          {"key": "1.3.2", "text": "No", "nextQuestionKey": "1.3.2", "lastQuestionKey": "1.3"}
    ]}}
Retrieve the chain of answers for a given answer key, including the specified answer.  Answer key values are also in the "N[.N]..." form.<br><br>
    http://34.82.132.4/myapp/decisiontree/answers?key={answer key}

    [http://34.82.132.4/myapp/decisiontree/answers?key=1.3.1.2](http://34.82.132.4/myapp/decisiontree/answers?key=1.3.1.2)

    {
      "answers": [
    {
      "question": {"key": "1.3.1", "text": "Is it a?", "answers": [
          {"key": "1.3.1.2", "text": "Dinosaur"}
    ]}},
    {
      "question": {"key": "1.3", "text": "Is it larger than a house?", "answers": [
          {"key": "1.3.1", "text": "Yes"}
    ]}},
    {
      "question": {"key": "1", "text": "How big is it?", "answers": [
          {"key": "1.3", "text": "Large"}
    ]}}
    ]}

Clear out the decision tree to force a reload of the data from the file (used to deploy changes with a new decision tree file without bringing the system down).<br><br>
    http://34.82.132.4/decisiontree/reset

Request OpenAI to determine which category best matches user's free form text input (POST).<br><br>
    [http://34.82.132.4/decisiontree/usercategory](http://34.82.132.4/myapp/decisiontree/usercategory)
    Posted data:
        { "userInput": "Larger than a skyscraper" }
    Returned data:
        {
            "userInput": "Larger than a skyscraper",
            "aiResponse": "Large",
            "key": "1.3"
        }
The configuration of the servlet which exposes these REST APIs through tomcat is configured in the web.xml file contained in the web application's WEB-INF/web.xml file (.../myapp/WEB-INF/web.xml) as shown below:

    <servlet>
        <servlet-name>DecisionTree</servlet-name>
        <servlet-class>DecisionTreeServlet</servlet-class>
    </servlet>
    
    <servlet-mapping>
        <servlet-name>DecisionTree</servlet-name>
        <url-pattern>/decisiontree/*</url-pattern>
    </servlet-mapping>

The url-pattern entry determines what path, when appended to the web application name (myapp/decisiontree), will be processed through the servlet class specified by the servlet-class entry (DecisionTree).
