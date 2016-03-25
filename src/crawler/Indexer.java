package crawler;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.FileWriter;
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
    private Map<String, Integer> uniGramWordOccurrences;
    private Map<String, Integer> biGramWordOccurrences;
    private Map<String, Integer> triGramWordOccurrences;

    public Indexer() {
        indexUniGram = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        indexBiGram  = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        indexTriGram = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        uniGramWordOccurrences = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        biGramWordOccurrences  = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        triGramWordOccurrences = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
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
                        indexDocument(docID, document, 3);
                        indexDocument(docID, document, 2);
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
        Map<String, Integer> docTermMap = new HashMap<>();
        String[] doc = document.split("\\p{javaWhitespace}");
        int wordOccurrences = 0;

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

                wordOccurrences++;
            }
        }

        // Now we add the document's terms and frequencies to the indexer
        for (Map.Entry<String, Integer> term : docTermMap.entrySet()) {
            addTermToIndex(docID, term, n);
        }

        storeWordOccurrences(docID, wordOccurrences, n);
    }

    private void addTermToIndex(String docID, Map.Entry<String, Integer> term, int n) {
        Map<String, List<Pair<String, Integer>>> index = getIndex(n);

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

    private void storeWordOccurrences(String docID, int count, int n) {
        Map<String, Integer> wordOccurrences;

        switch (n) {
            case 1: wordOccurrences = uniGramWordOccurrences;
                    break;
            case 2: wordOccurrences = biGramWordOccurrences;
                    break;
            case 3: wordOccurrences = triGramWordOccurrences;
                    break;
            default: return;
        }

        wordOccurrences.put(docID, count);
    }

    private void getTotalWordOccurrences(Map<String, Integer> wordOccurrences, int n) {

        int totalWordOccurrences = 0;

        if (wordOccurrences != null) {
            // Table in csv format
            // StringBuilder table = new StringBuilder("docID,number of tokens\n");

            // For each document
            for (Map.Entry<String, Integer> entry : wordOccurrences.entrySet()) {

                // Get number of tokens
                totalWordOccurrences = totalWordOccurrences + entry.getValue();
                // table.append('"').append(entry.getKey()).append('"').append(',').append(entry.getValue()).append('\n');
            }

            // Save in file
            // saveTable("number_of_tokens_" + n + "_gram.csv", table.toString());
            System.out.println("Total Word Occurrences for " + n + "-gram corpus: " + totalWordOccurrences);
        }
    }

    private Map<String, List<Pair<String, Integer>>> getIndex(int n) {
        if (n == 1) {
            return indexUniGram;
        } else if (n == 2) {
            return indexBiGram;
        } else if (n == 3) {
            return indexTriGram;
        } else {
            return null;
        }
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

    private void printIndex(int n) {
        Map<String, List<Pair<String, Integer>>> index = getIndex(n);

        for (Map.Entry<String, List<Pair<String, Integer>>> indexEntry : index.entrySet()) {
            System.out.print(indexEntry.getKey() + " -> ");
            System.out.println(getInvertedList(indexEntry.getValue()));
        }
    }

    private void getTermFrequencyTable(int n) {
        Map<String, List<Pair<String, Integer>>> index = getIndex(n);

        if (index != null) {
            // Table in csv format
            StringBuilder table = new StringBuilder("term,tf\n");

            // For each term
            for (Map.Entry<String, List<Pair<String, Integer>>> indexEntry : index.entrySet()) {

                // Total term frequency incrementer
                int tf = 0;

                // Go through inverted list of (DocID, tf)
                for (Pair<String, Integer> post : indexEntry.getValue()) {

                    // Sum document term frequency to total term frequency
                    tf = tf + post.getValue();
                }

                table.append('"').append(indexEntry.getKey()).append('"').append(',').append(tf).append('\n');
            }

            // Save in file
            saveTable("tables/term_frequency_" + n + "_gram.csv", table.toString());
        }
    }

    private void getDocumentFrequencyTable(int n) {
        Map<String, List<Pair<String, Integer>>> index = getIndex(n);

        if (index != null) {
            // Table in csv format
            StringBuilder table = new StringBuilder("term,docID,df\n");

            // For each term
            for (Map.Entry<String, List<Pair<String, Integer>>> indexEntry : index.entrySet()) {

                // Total term frequency incrementer
                StringBuilder docs = new StringBuilder("{");

                // Go through inverted list of (DocID, tf)
                for (Pair<String, Integer> post : indexEntry.getValue()) {

                    docs.append(post.getKey()).append(", ");
                }

                docs.delete(docs.length() - 2, docs.length());
                docs.append("}");

                // term
                table.append('"').append(indexEntry.getKey()).append('"').append(',');
                // docID
                table.append('"').append(docs.toString()).append('"').append(',');
                // df
                table.append(indexEntry.getValue().size()).append('\n');
            }

            // Save in file
            saveTable("tables/document_frequency_" + n + "_gram.csv", table.toString());
        }
    }

    private void saveTable(String fileName, String document) {
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

    private void createTablesDir() {
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

    public void statistics() {
        createTablesDir();
        getTermFrequencyTable(1);
        getTermFrequencyTable(2);
        getTermFrequencyTable(3);
        getDocumentFrequencyTable(1);
        getDocumentFrequencyTable(2);
        getDocumentFrequencyTable(3);
        //getTotalWordOccurrences(uniGramWordOccurrences, 1);
        //getTotalWordOccurrences(biGramWordOccurrences, 2);
        //getTotalWordOccurrences(triGramWordOccurrences, 3);
    }
}
