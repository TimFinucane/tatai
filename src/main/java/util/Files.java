package util;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Files {
    /**
     * Lists the names of all tests available to choose from
     */
    public static List<String> listTests() {
        return list(testFolder());
    }

    /**
     * Lists available users
     */
    public static List<String> listUsers() {
        return list(userFolder());
    }

    /**
     * Gets a test file with the given name
     */
    public static File testFile(String name) {
        return new File(testFolder().getPath() + "/" + name);
    }

    /**
     * Gets a user file with the given name
     */
    public static File userFile(String name) {
        return new File(userFolder().getPath() + "/" + name);
    }

    private static List<String> list(File folder) {
        File files[] = folder.listFiles();

        if(files == null)
            return Collections.emptyList(); // Nothing exists, that's ok

        return Arrays.stream(files).map(File::getName).collect(Collectors.toList());
    }

    private static File testFolder() {
        File file = new File("./tests");

        if(!file.exists())
            file.mkdir();

        return file;
    }
    private static File userFolder() {
        File file = new File("./users");

        if(!file.exists())
            file.mkdir();

        return file;
    }
}
