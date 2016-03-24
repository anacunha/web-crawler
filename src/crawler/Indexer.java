package crawler;

import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Indexer {

    private Map<String, List<Pair<String, Integer>>> indexUniGram;
    private Map<String, List<Pair<String, Integer>>> indexBiGram;
    private Map<String, List<Pair<String, Integer>>> indexTriGram;

    public Indexer() {
        indexUniGram = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        indexBiGram  = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        indexTriGram = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    public void index() {

        // For each document in corpus
        try {
            Files.walk(Paths.get("pages_parsed_/")).forEach(filePath -> {
                if (Files.isRegularFile(filePath) && Parser.isTextFile(filePath)) {
                    try {
                        indexDocument(new String(Files.readAllBytes(filePath)));
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        catch(IOException e) {
            e.printStackTrace();
        }

            // For each word in document

                // Count number of times if appears on the document

                // Add <docID, tf> to HashMap entry with that word as key

                // Also store vocabulary size for that document
    }

    public void indexDocument(String document) {
        String[] doc = document.split(" ");

        for (String word : doc) {
            System.out.println(word);
        }
    }
}
