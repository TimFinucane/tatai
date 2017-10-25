package tatai.model;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * Holds the helper function for recognizing words
 */
public class Recognizer {
    private static String TEMP = ".tmp/";

    private static final String HTK = System.getProperty("user.home") + "/Documents/HTK/MaoriNumbers/";
    private static final String[] HMMS = { HTK + "HMMs/hmm15/macros", HTK + "HMMs/hmm15/hmmdefs" };
    private static final String CONFIG = HTK + "user/configLR";
    private static final String NETWORK = HTK + "user/wordNetworkNum";
    private static final String OUTPUT = TEMP + "output.mlf";
    private static final String DICTIONARY = HTK + "user/dictionaryD";
    private static final String TIED_LIST = HTK + "user/tiedList";

    private static final String COMMAND_NAME = "HVite";
    private static final String COMMAND_OPTIONS = "-o SWT";

    private static final String LOG_FILE = TEMP + "recognition.log";
    /**
     * Produces a string output of the words recognized in the
     * file given by the filename input, and gives this result to
     * onComplete
     */
    public static void recognize(String filename, Consumer<String> onComplete) {
        String command = buildCommand(filename);

        ProcessBuilder pb = new ProcessBuilder( "bash", "-c", command );

        // Forget output for now
        pb.redirectError(new File(LOG_FILE));
        pb.redirectOutput(new File(LOG_FILE));

        Process process;
        try {
            process = pb.start();
        } catch(IOException e) {
            throw new RuntimeException("Recognition command failed: " + e.getMessage());
        }

        // Wait until process is complete and give output to onComplete
        new Thread(() -> {
            try {
                process.waitFor();
            } catch(InterruptedException ignored) {
                // Do nothing
            }

            String recognizedSequence = processOutput(OUTPUT);
            Platform.runLater(() -> onComplete.accept(recognizedSequence));
        }).run();
    }

    /**
     * Creates the command string
     */
    private static String buildCommand(String filename) {
        StringBuilder command = new StringBuilder(COMMAND_NAME);
        for(String hmm : HMMS) {
            command.append(" -H ");
            command.append(hmm);
        }
        command.append(" -C ");
        command.append(CONFIG);

        command.append("  -w ");
        command.append(NETWORK);

        command.append(" ");
        command.append(COMMAND_OPTIONS);

        command.append(" -i ");
        command.append(OUTPUT);

        command.append("  ");
        command.append(DICTIONARY);
        command.append(" ");
        command.append(TIED_LIST);
        command.append(" ");

        command.append(filename);

        return command.toString();
    }

    /**
     * Reads the file and outputs a series of words (space seperated) as a result
     */
    private static String processOutput(String outputFile) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(outputFile));

            // File always contains at least one line
            reader.readLine();

            // If the file contains info, there will be a second useless line (the input file description)
            if(reader.readLine() == null)
                return "";

            StringBuilder output = new StringBuilder();
            String line = reader.readLine();
            // Go through, remove occurrences of 'sil' or '.'
            while(line != null) {
                if(!line.equals("sil") && !line.equals(".")) {
                    // Occurrences of aa should be replaced with \u0101
                    output.append(line.replace("aa", "\u0101"));
                    output.append(" ");
                }

                line = reader.readLine();
            }

            output.deleteCharAt(output.length() - 1);
            return output.toString();

        } catch(IOException e) {
            throw new RuntimeException("Failed to read recognition file: " + e.getMessage());
        }
    }
}
