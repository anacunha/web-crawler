package crawler;

import java.io.*;
import java.util.*;

public class InLinksFileReader {

    private Map<String, List<String>> inLinks;
    private Map<String, List<String>> outLinks;

    public InLinksFileReader(String fileName) {
        inLinks = new LinkedHashMap<>();
        outLinks = new LinkedHashMap<>();
        readFile(fileName);
    }

    private void readFile(String fileName) {
        String line;

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                String[] pages = line.split(" ");
                String destination = pages[0];

                // In and Out Links Graph Representations
                for (int i = 1; i < pages.length; i++) {
                    addIncomingLink(destination, pages[i]);
                    addOutgoingLink(destination, pages[i]);
                }
            }
            saveGraph();
            bufferedReader.close();
        }
        catch (Exception e) {
            // Does nothing
        }
    }

    private void addIncomingLink(String destination, String source) {
        if (inLinks.containsKey(destination)) {
            if (!inLinks.get(destination).contains(source))
                inLinks.get(destination).add(source);
        }
        else {
            inLinks.put(destination, new ArrayList<>(Collections.singletonList(source)));
        }
    }

    private void addOutgoingLink(String destination, String source) {
        if (outLinks.containsKey(source)) {
            if (!outLinks.get(source).contains(destination))
                outLinks.get(source).add(destination);
        }
        else {
            outLinks.put(source, new ArrayList<>(Collections.singletonList(destination)));
        }
    }

    private String printGraph() {
        StringBuilder graphString = new StringBuilder();

        for (Map.Entry<String, List<String>> pairs : outLinks.entrySet()) {
            graphString.append(pairs.getKey())
                    .append(" ")
                    .append(Arrays.toString(pairs.getValue().toArray()))
                    .append("\n");
        }

        return graphString.toString();
    }

    private void saveGraph() {
        try {
            FileWriter fileWriter = new FileWriter("output/out-links.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(printGraph());
            bufferedWriter.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
