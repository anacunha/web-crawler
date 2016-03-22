package crawler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class WebGraph {

    private Map<String, List<String>> inLinks;
    private Map<String, List<String>> outLinks;
    private Map<String, Integer> inLinkCount;
    private List<String> pages;
    private String name;

    public WebGraph(String name) {
        inLinkCount = new LinkedHashMap<>();
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
            FileWriter fileWriter = new FileWriter("output/" + name + ".txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(printGraph(inLinks));
            bufferedWriter.close();

//            fileWriter = new FileWriter("output/" + name + "_out.txt");
//            bufferedWriter = new BufferedWriter(fileWriter);
//            bufferedWriter.write(printGraph(outLinks));
//            bufferedWriter.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
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

    public List<String> getSinkPages() {
        // Sink Pages are pages with no out-links
        ArrayList<String> sinkPages = new ArrayList<>();

        for (String page : outLinks.keySet()) {
            if (outLinks.get(page).isEmpty()) {
                sinkPages.add(page);
            }
        }
        return sinkPages;
    }

    public List<String> getSourcePages() {
        // Source Pages are pages with no in-links
        ArrayList<String> sourcePages = new ArrayList<>();

        for (String page : inLinks.keySet()) {
            if (inLinks.get(page).isEmpty()) {
                sourcePages.add(page);
            }
        }
        return sourcePages;
    }

    public HashMap<Integer, Integer> getInLinksDistribution() {
        HashMap<Integer, Integer> dist = new HashMap<>();

        // Number of in-link count vs. Number of pages
        for (String page : inLinks.keySet()) {

            int numInLinks = inLinks.get(page).size();

            if (dist.containsKey(numInLinks)) {
                // Increase number of pages by 1
                dist.put(numInLinks, dist.get(numInLinks) + 1);
            } else {
                dist.put(numInLinks, 1);
            }
        }
        return dist;
    }

    private void saveLinksDistribution() {
        try {
            FileWriter fileWriter = new FileWriter("output/" + name + ".csv");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("Number of Pages,In-link Count");

            for (Map.Entry<Integer, Integer> entry : getInLinksDistribution().entrySet()) {
                bufferedWriter.write("\n");
                bufferedWriter.write(entry.getValue().toString());
                bufferedWriter.write(",");
                bufferedWriter.write(entry.getKey().toString());
            }
            bufferedWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getStatistics() {
        double totalPages = pages.size();
        System.out.println("Total Pages: " + (int) totalPages);

        double sourcePages = getSourcePages().size();
        System.out.println("Source Pages: " + (int) sourcePages);

        double sinkPages = getSinkPages().size();
        System.out.println("Sinks Pages: " + (int) sinkPages + "\n");

        System.out.println("Proportion of Source Pages: " + (100 * sourcePages / totalPages) + "%");
        System.out.println("Proportion of Sink Pages: " + (100 * sinkPages / totalPages) + "%\n");

        saveLinksDistribution();
    }

    private void getInLinkCount() {
        for (Map.Entry<String, List<String>> entry : inLinks.entrySet()) {
            inLinkCount.put(entry.getKey(), entry.getValue().size());
        }
    }

    private void sortInLinkCount() {
        // Convert Map to List
        List<Map.Entry<String, Integer>> list = new LinkedList<>(inLinkCount.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, (o1, o2) -> (o1.getValue()).compareTo(o2.getValue()));

        // Reverse to get descending order
        Collections.reverse(list);

        // Convert sorted map back to a Map
        inLinkCount = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            inLinkCount.put(entry.getKey(), entry.getValue());
        }
    }

    public List<String> getTop50InLink() {
        getInLinkCount();
        sortInLinkCount();

        ArrayList<String> list = new ArrayList<>();
        int count = 1;

        for (Map.Entry<String, Integer> e : inLinkCount.entrySet()) {
            System.out.println(count + ". " + e.getKey() + " (" + e.getValue() +")");
            list.add(e.getKey());
            count++;

            if(count > 50)
                break;
        }

        return list;
    }
}
