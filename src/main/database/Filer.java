package main.database;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Filer {

    public static String getPath() throws RuntimeException {
        String envRootDir = System.getProperty("user.dir");
        Path rootDir = Paths.get(".").normalize().toAbsolutePath();

        if (rootDir.startsWith(envRootDir)) {
            return rootDir.toString();
        } else {
            throw new RuntimeException("Root dir not found in user directory");
        }
    }

    public static void deleteDatabaseFile() {
        String databaseFilePath = getPath() + "database.db";
        File file = new File(databaseFilePath);
        if (file.delete()) {
            System.out.println(databaseFilePath + " is deleted");
        } else {
            System.out.println("Could not delete: + " + databaseFilePath);
        }
    }
}
