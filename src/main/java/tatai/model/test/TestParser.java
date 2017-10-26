package tatai.model.test;

import com.google.gson.Gson;
import com.sun.media.jfxmedia.logging.Logger;
import util.Files;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class TestParser {

    /**
     * Reads a test with the given name. This name must have come from the above
     * listTests method
     * TODO: Document the format
     */
    public static TestJson      read(String name) throws FileNotFoundException {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(Files.testFile(name)), "UTF-8"))) {

            // Thank you holy Gson
            Gson gson = new Gson();

            return gson.fromJson(reader, TestJson.class);
        } catch(UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Test File is not in correct UTF8 format " + e.getMessage());
        } catch(IOException e) {
            throw new IllegalArgumentException("Test File could not be read: " + e.getMessage());
        }
    }

    /**
     * Writes the given test so that it can be retrievable later
     */
    public static void          save(TestJson testInfo) {
        try(Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Files.testFile(testInfo.name)), StandardCharsets.UTF_8))) {
            // Thank you again holy Gson
            Gson gson = new Gson();

            gson.toJson(testInfo, writer);
        } catch (IOException e) {
            Logger.logMsg(Logger.ERROR, "Unable to save test: " + e.getMessage());
        }
    }
}
