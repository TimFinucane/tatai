package tatai.model.test;

import com.sun.media.jfxmedia.logging.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Scores {
    private static final String SCORES_FILE = "scores.txt";

    /**
     * Gets all recorded scores
     */
    public static Map<String, ArrayList<Integer>>   retrieve() {
        HashMap<String, ArrayList<Integer>> scores = new HashMap<>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(SCORES_FILE));

            for(String line = reader.readLine(); line != null; line = reader.readLine()) {
                String items[] = line.split(",");

                scores.get(items[0]).add(Integer.parseInt(items[1]));
            }
        } catch (FileNotFoundException e) {
            // Do nothing. If the file doesn't exist the intended behaviour is that
            //  no scores have been recorded
        } catch (IOException e) {
            Logger.logMsg(Logger.ERROR, "Exception closing the reader: " + e.getMessage());
        } finally {
            // I really do not like having to do this. Why would you do this java.
            try {
                if( reader != null ) {
                    reader.close();
                }
            } catch(IOException e) {
                Logger.logMsg(Logger.ERROR, "Exception closing the reader: " + e.getMessage());
            }
        }
        return scores;
    }

    /**
     * Saves a score
     */
    public static void                              save(String testName, Integer score) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(SCORES_FILE, true));

            writer.write(testName + "," + String.valueOf(score));
            writer.newLine();

            writer.close();
        } catch(IOException e) {
            Logger.logMsg(Logger.ERROR, "Cannot store scores: " + e.getMessage());
        }
    }

    /**
     * Clears all scores
     */
    public static void	                            clear() {
        new File(SCORES_FILE).delete();
    }
}
