package crawler;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Indexer {

    private Map<String, List<Pair<String, Integer>>> indexUniGram;
    private Map<String, List<Pair<String, Integer>>> indexBiGram;
    private Map<String, List<Pair<String, Integer>>> indexTriGram;
    private Map<String, Integer> uniGramVocabularySize;
    private Map<String, Integer> biGramVocabularySize;
    private Map<String, Integer> triGramVocabularySize;

    public Indexer() {
        indexUniGram = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        indexBiGram  = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        indexTriGram = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        uniGramVocabularySize = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        biGramVocabularySize  = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        triGramVocabularySize = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    public void index() {

        // For each document in corpus
        try {
            Files.walk(Paths.get("pages_parsed/")).forEach(filePath -> {
                if (Files.isRegularFile(filePath) && Parser.isTextFile(filePath)) {
                    try {
                        indexDocument(FilenameUtils.removeExtension(filePath.getFileName().toString()),
                                      new String(Files.readAllBytes(filePath)), 1);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            printIndex();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void indexDocument(String docID, String document, int n) {
        Map<String, Integer> docTermMap = new HashMap<>();
        String[] doc = document.split("\\p{javaWhitespace}");
        //String[] doc = document.split("\\s");

        // For each term in document
        for (String term : doc) {

            term.trim();
            // Count number of times it appears on the document

            if (!isPunctuation(term)) {
                // If we haven't encountered that term yet, we add it to the map
                if (!docTermMap.containsKey(term)) {
                    docTermMap.put(term, 1);
                }
                // If we have already encountered that term, we increase its frequency
                else {
                    docTermMap.put(term, docTermMap.get(term) + 1);
                }
            }
        }

        // Now we add the document's terms and frequencies to the indexer
        for (Map.Entry<String, Integer> term : docTermMap.entrySet()) {

            // If term doesn't exist on indexer yet, we add it
            if (!indexUniGram.containsKey(term.getKey())) {
                indexUniGram.put(term.getKey(), new ArrayList<>());
            }

            // If indexer already contains that term, we add the term frequency
            // information for the document to the indexer's inverted list
            // Add <docID, tf> to HashMap entry for that term
            indexUniGram.get(term.getKey()).add(new ImmutablePair<>(docID, term.getValue()));
        }

        // Size of the HashMap will be equal to size of document's vocabulary
        uniGramVocabularySize.put(docID, docTermMap.size());
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

    private void printIndex() {
        for (Map.Entry<String, List<Pair<String, Integer>>> index : indexUniGram.entrySet()) {
            System.out.print(index.getKey() + " -> ");
            System.out.println(getInvertedList(index.getValue()));
        }
    }
}
