package indexer;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import parser.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Indexer {

    private Map<String, Map<String, Integer>> docTermFrequency;
    private Map<String, List<Pair<String, Integer>>> index;
    private Map<String, Double> dl;
    private double corpusSize;

    public Indexer() {
        index = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        docTermFrequency = new LinkedHashMap<>();
        dl = new LinkedHashMap<>();
    }

    public void index() {
        corpusSize = 0;

        // For each document in corpus
        try {
            Files.walk(Paths.get("pages_parsed/")).forEach(filePath -> {
                if (Files.isRegularFile(filePath) && Parser.isTextFile(filePath)) {
                    try {
                        String docID = FilenameUtils.removeExtension(filePath.getFileName().toString());
                        String document = new String(Files.readAllBytes(filePath));
                        indexDocument(docID, document, 1);
                        corpusSize++;
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
    }

    private void indexDocument(String docID, String document, int n) {
        Map<String, Integer> termFrequency = new HashMap<>();
        String[] doc = document.split("\\p{javaWhitespace}");
        double wordCount = 0;

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
                if (!termFrequency.containsKey(term)) {
                    termFrequency.put(term, 1);
                }
                // If we have already encountered that term, we increase its frequency
                else {
                    termFrequency.put(term, termFrequency.get(term) + 1);
                }

                wordCount++;
            }
        }

        // Now we add the document's terms and frequencies to the indexer
        for (Map.Entry<String, Integer> term : termFrequency.entrySet()) {
            addTermToIndex(docID, term);
        }

        docTermFrequency.put(docID, termFrequency);
        dl.put(docID, wordCount);
    }

    private void addTermToIndex(String docID, Map.Entry<String, Integer> term) {
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

    public Map<String, Integer> getDocFrequency() {
        Map<String, Integer> docFrequency = new LinkedHashMap<>();

        // For each term
        for (Map.Entry<String, List<Pair<String, Integer>>> indexEntry : index.entrySet()) {
            docFrequency.put(indexEntry.getKey(), indexEntry.getValue().size());
        }
        return docFrequency;
    }

    public Map<String, Map<String, Integer>> getDocTermFrequency() {
        return docTermFrequency;
    }

    public Map<String, Double> getDocLengths() {
        return dl;
    }

    public double getCorpusSize() {
        return corpusSize;
    }

    public Map<String, List<Pair<String, Integer>>> getIndex() {
        return index;
    }
}
