package crawler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class WebGraph {

    private Map<String, List<String>> inLinks;
    private Map<String, List<String>> outLinks;
    private List<String> pages;
    private String name;

    public WebGraph(String name) {
        inLinks = new LinkedHashMap<>();
        outLinks = new LinkedHashMap<>();
        pages = new ArrayList<>();
        this.name = name;
    }

    public void addPage(String page) {
        if(!pages.contains(page)) {
            pages.add(page);

            if(!inLinks.containsKey(page))
                inLinks.put(page, new ArrayList<>());

            if(!outLinks.containsKey(page))
                outLinks.put(page, new ArrayList<>());
        }
    }

    public void addIncomingLink(String destination, String source) {
        if (inLinks.containsKey(destination)) {
            if (!inLinks.get(destination).contains(source))
                inLinks.get(destination).add(source);
        }
        else {
            inLinks.put(destination, new ArrayList<>(Collections.singletonList(source)));
        }
    }

    public void addOutgoingLink(String destination, String source) {
        if (outLinks.containsKey(source)) {
            if (!outLinks.get(source).contains(destination))
                outLinks.get(source).add(destination);
        }
        else {
            outLinks.put(source, new ArrayList<>(Collections.singletonList(destination)));
        }
    }

    public void saveGraph() {
        try {
            FileWriter fileWriter = new FileWriter("output/" + name + "_in.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(printGraph(inLinks));
            bufferedWriter.close();

            fileWriter = new FileWriter("output/" + name + "_out.txt");
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(printGraph(outLinks));
            bufferedWriter.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        System.out.println("In links: " + inLinks.size());
        System.out.println("Out links: " + outLinks.size());
        System.out.println("Total Pages: " + pages.size());
    }

    private String printGraph(Map<String, List<String>> graph) {
        StringBuilder graphString = new StringBuilder();

        for (Map.Entry<String, List<String>> entry : graph.entrySet()) {
            graphString.append(entry.getKey())
                    .append(" ")
                    .append(Arrays.toString(entry.getValue().toArray()))
                    .append("\n");
        }

        return graphString.toString().replace("[","").replace(",", "").replace("]","");
    }

    public void cleanInLinks() {
        Iterator<Map.Entry<String, List<String>>> iterator = inLinks.entrySet().iterator();

        // Iterate over all entries in inLinks to remove the ones not in pages
        while (iterator.hasNext()) {
            Map.Entry<String, List<String>> entry = iterator.next();

            // If key is not in pages, remove entire entry
            if(!pages.contains(entry.getKey())) {
                iterator.remove();
            }
            // If key is in pages, iterate over list of incoming
            // links and remove the ones not in pages
            else {
                Iterator<String> listIterator = entry.getValue().iterator();
                while (listIterator.hasNext()) {
                    if(!pages.contains(listIterator.next())) {
                        listIterator.remove();
                    }
                }
            }
        }
    }

    public List<String> getPages() {
        return pages;
    }

    public Map<String, List<String>> getOutLinks() {
        return outLinks;
    }

    public Map<String, List<String>> getInLinks() {
        return inLinks;
    }

    public String getName() {
        return name;
    }
}
