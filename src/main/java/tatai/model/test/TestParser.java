package tatai.model.test;

import com.google.gson.Gson;
import tatai.model.question.QuestionReader;
import util.Files;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TestParser {
    /**
     * Lists the names of all tests available to choose from
     */
    public static List<String>  listTests() {
        ArrayList<String> list = new ArrayList<>();

        File files[] = Files.testFolder().listFiles();

        if(files == null) {
            return list; // Nothing exists, thats ok
        }

        for(File file : files)
            list.add(file.getName());

        return list;
    }

    /**
     * Reads a test with the given name. This name must have come from the above
     * listTests method
     * TODO: Document the format
     */
    public static Test          read(String name) throws FileNotFoundException {
        Reader reader = new FileReader(Files.testFile(name));

        // Thank you holy Gson
        Gson gson = new Gson();

        TestJson testJson = gson.fromJson(reader, TestJson.class);

        // Create questions
        ArrayList<Test.QuestionInfo> questions = new ArrayList<>();
        for(TestJson.Question question : testJson.questions) {
            questions.add(
                    new Test.QuestionInfo(
                            question.rounds,
                            question.tries,
                            QuestionReader.read(question.question)));
        }

        return new Test(testJson.name, questions);
    }

    /**
     * Writes the given test so that it can be retrievable later
     */
    public static void          makeTest(TestJson testInfo) throws IOException {
        File file = Files.testFile(testInfo.name);
        file.createNewFile();

        try(Writer writer = new FileWriter(file)) {
            // Thank you again holy Gson
            Gson gson = new Gson();

            gson.toJson(testInfo, writer);
        }

    }

    /**
     * Creates some basic tests. TODO: Move this to another area for checking and creating
     */
    public static void          main(String[] args) {
        TestJson basic = new TestJson();
        basic.name = "Easy Test";
        basic.questions = new TestJson.Question[1];
        basic.questions[0] = new TestJson.Question();
        basic.questions[0].rounds = 10;
        basic.questions[0].tries = 2;
        basic.questions[0].question = "(1 to 9)";

        try {
            makeTest(basic);
        } catch(IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
