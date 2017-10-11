package util;

import java.io.File;

public class Files {
    public static File TEST_FOLDER = new File("./tests");

    /**
     * Gets a test file with the given name
     */
    public static File testFile(String name) {
        return new File(TEST_FOLDER.getPath() + "/" + name + ".txt");
    }
}
