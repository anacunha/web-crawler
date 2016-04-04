package indexer;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import parser.Parser;
import main.MapUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Indexer {

    private Map<String, List<Pair<String, Integer>>> index;
    private Map<String, Integer> wordOccurrences;

    public Indexer() {
        index = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        wordOccurrences = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    public void index() {

        // For each document in corpus
        try {
            Files.walk(Paths.get("pages_parsed/")).forEach(filePath -> {
                if (Files.isRegularFile(filePath) && Parser.isTextFile(filePath)) {
                    try {
                        String docID = FilenameUtils.removeExtension(filePath.getFileName().toString());
                        String document = new String(Files.readAllBytes(filePath));
                        indexDocument(docID, document, 1);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            getTermFrequency();
            //printIndex();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void indexDocument(String docID, String document, int n) {
        Map<String, Integer> docTermMap = new HashMap<>();
        String[] doc = document.split("\\p{javaWhitespace}");
        int count = 0;

        // For each term in document
        for (int i = 0; i <= doc.length - n; i++) {

            String term = doc[i];

            // Check if first term of word is punctuation
            if (!isPunctuation(term)) {

                // Create n-grams
                for (int t = 1; t < n; t++) {
                    term = term + " " + doc[i + t];
                }

                term = term.trim();

                // Count number of times it appears on the document
                // If we haven't encountered that term yet, we add it to the map
                if (!docTermMap.containsKey(term)) {
                    docTermMap.put(term, 1);
                }
                // If we have already encountered that term, we increase its frequency
                else {
                    docTermMap.put(term, docTermMap.get(term) + 1);
                }

                count++;
            }
        }

        // Now we add the document's terms and frequencies to the indexer
        for (Map.Entry<String, Integer> term : docTermMap.entrySet()) {
            addTermToIndex(docID, term, n);
        }

        wordOccurrences.put(docID, count);
    }

    private void addTermToIndex(String docID, Map.Entry<String, Integer> term, int n) {
        // If term doesn't exist on indexer yet, we add it
        if (index != null && !index.containsKey(term.getKey())) {
            index.put(term.getKey(), new ArrayList<>());
        }

        // If indexer already contains that term, we add the term frequency
        // information for the document to the indexer's inverted list
        // Add <docID, tf> to HashMap entry for that term
        if (index != null) {
            index.get(term.getKey()).add(new ImmutablePair<>(docID, term.getValue()));
        }

        // Size of the HashMap will be equal to size of document's vocabulary
    }

    private boolean isPunctuation(String s) {
        Pattern p = Pattern.compile("^[\\p{Punct}]+$");
        Matcher m = p.matcher(s);
        return m.find();
    }

    private String getInvertedList(List<Pair<String, Integer>> list) {
        StringBuilder invertedList = new StringBuilder();

        for (Pair<String, Integer> pair : list) {
            invertedList.append("(");
            invertedList.append(pair.getLeft());
            invertedList.append(", ");
            invertedList.append(pair.getRight());
            invertedList.append(") ");
        }

        return invertedList.toString();
    }

    private void getTermFrequency() {
        Map<String, Integer> termFrequency = new LinkedHashMap<>();

        // For each term
        for (Map.Entry<String, List<Pair<String, Integer>>> indexEntry : index.entrySet()) {

            // Total term frequency incrementer
            int tf = 0;

            // Go through inverted list of (DocID, tf)
            for (Pair<String, Integer> post : indexEntry.getValue()) {

                // Sum document term frequency to total term frequency
                tf = tf + post.getValue();
            }

            termFrequency.put(indexEntry.getKey(), tf);
        }

        termFrequency = MapUtil.sortByValueDesc(termFrequency);
        printMap(termFrequency);
    }

    private void printIndex() {
        for (Map.Entry<String, List<Pair<String, Integer>>> indexEntry : index.entrySet()) {
            System.out.print(indexEntry.getKey() + " -> ");
            System.out.println(getInvertedList(indexEntry.getValue()));
        }
    }

    private void printMap(Map<String, Integer> map) {
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }
}
