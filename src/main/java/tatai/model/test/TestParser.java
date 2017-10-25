package tatai.model.test;

import com.google.gson.Gson;
import com.sun.media.jfxmedia.logging.Logger;
import util.Files;

import java.io.*;

public class TestParser {

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
     * Writes the given test so that it can be retrievable later
     */
    public static void          save(TestJson testInfo) {
        try(Writer writer = new FileWriter(Files.testFile(testInfo.name))) {
            // Thank you again holy Gson
            Gson gson = new Gson();

            gson.toJson(testInfo, writer);
        } catch (IOException e) {
            Logger.logMsg(Logger.ERROR, "Unable to save test: " + e.getMessage());
        }
    }
}
