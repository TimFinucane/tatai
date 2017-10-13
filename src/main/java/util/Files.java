package util;

import java.io.File;

public class Files {
    public static File testFolder() {
        File file = new File("./tests");

        if(!file.exists())
            file.mkdir();

        return file;
    }
    public static File scoreFolder() {
        File file = new File("./scores");

        if(!file.exists())
            file.mkdir();

        return file;
    }
    /**
     * Gets a test file with the given name
     */
    public static File testFile(String name) {
        return new File(testFolder().getPath() + "/" + name + ".txt");
    }
    public static File scoreFile(String name) {
        return new File(testFolder().getPath() + "/" + name + ".txt");
    }
}
