package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TableUtil {

    public static void saveTable(String fileName, String document) {
        createTablesDir();

        // Save in file
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.write(document);
            fileWriter.close();
            //System.out.println(table.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createTablesDir() {
        File dir = new File("tables/");
        if (!dir.exists()) {
            try {
                dir.mkdir();
            }
            catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }
}
