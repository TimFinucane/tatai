package tatai.model.test;

import com.google.gson.Gson;
import tatai.model.question.QuestionReader;
import util.Files;
import javax.annotation.Nonnull;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TestParser {
    /**
     * Lists the names of all tests available to choose from
     */
    public static List<String>  listTests() {
        ArrayList<String> list = new ArrayList<>();

        File files[] = Files.testFolder().listFiles();

        if(files == null) {
            return list; // Nothing exists, that's ok
        }

        for(File file : files) {
            String name = file.getName();
            name = name.substring(0, name.lastIndexOf('.'));
            list.add(name);
        }

        return list;
    }

    /**
     * Reads a test with the given name. This name must have come from the above
     * listTests method
     * TODO: Document the format
     */
    public static TestJson      read(String name) throws FileNotFoundException {
        Reader reader = new FileReader(Files.testFile(name));

        // Thank you holy Gson
        Gson gson = new Gson();

        return gson.fromJson(reader, TestJson.class);
    }

    /**
     * Makes a test from a testJson
     */
    @Nonnull
    public static Test          make(TestJson testJson) {
        // Create questions
        ArrayList<Test.QuestionInfo> questions = new ArrayList<>();
        for(TestJson.Question question : testJson.questions) {
            questions.add(
                    new Test.QuestionInfo(
                            question.rounds,
                            question.tries,
                            QuestionReader.read(question.question)));
        }

        if(testJson.randomizeQuestions)
            Collections.shuffle(questions, new Random());

        return new Test("", testJson.name, "", questions);
    }

    /**
     * Writes the given test so that it can be retrievable later
     */
    public static void          save(String user, TestJson testInfo) throws IOException {
        File file = Files.testFile(testInfo.name);
        file.createNewFile();

        try(Writer writer = new FileWriter(file)) {
            // Thank you again holy Gson
            Gson gson = new Gson();

            gson.toJson(testInfo, writer);
        }

    }
}
