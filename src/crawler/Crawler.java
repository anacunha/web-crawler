package crawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;


public class Crawler {

    private static final int MAX_DEPTH_TO_CRAWL = 5;
    private static final int MAX_PAGES_TO_CRAWL = 1000;
    private Map<String, String> crawledPages;
    private Map<String, List<String>> graph;
    private List<String> requestQueue;
    private StringBuilder urlsList;

    public Crawler(String seed) {
        urlsList = new StringBuilder();
        graph = new LinkedHashMap<>();
        crawledPages = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        requestQueue = new LinkedList<>();
        requestQueue.add(seed);
        crawl();
    }

    private void crawl() {
        // Stop once you've crawled 1000 unique URLs
        while(crawledPages.size() < MAX_PAGES_TO_CRAWL) {

            String currentPage = getNextPage();

            try {
                Connection connection = Jsoup.connect(currentPage);
                Document document = connection.get();
                Elements anchorElements = document.select("a[href]");

                // Add current page to tree of crawled pages
                crawledPages.put(currentPage, document.html());
                urlsList.append(currentPage).append("\n");

                for(Element link : anchorElements) {
                    // Get the absolute URL from link
                    String url = link.absUrl("href");

                    // Only follow links with prefix http://en.wikipedia.org/wiki
                    // Do not include administrative links containing :
                    if(Pattern.matches("^https?://en\\.wikipedia\\.org/wiki/[^:]*", url)) {

                        // Remove # from URLs
                        url = url.split("#")[0];
                        requestQueue.add(url);

                        // Add incoming link to graph
                        addIncomingLink(url.toLowerCase(), currentPage.toLowerCase());
                    }
                }
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }

        // Save Graph
        saveGraph();
        // Save URLs
        saveURLs();
        // Save Documents
        saveDocuments();
    }

    public String getNextPage() {
        String nextPage = requestQueue.remove(0);

        while(crawledPages.containsKey(nextPage)) {
            nextPage = requestQueue.remove(0);
        }

        return nextPage;
    }

    private void saveURLs() {
        try {
            FileWriter urlsFile = new FileWriter("urls_task1.txt");
            urlsFile.write(urlsList.toString());
            urlsFile.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDocuments() {
        FileWriter docsFile;
        BufferedWriter docsBuffer;

        try {
            docsFile = new FileWriter("docs_task1.txt");
            docsBuffer = new BufferedWriter(docsFile);

            Iterator<Map.Entry<String, String>> iterator = crawledPages.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, String> pairs = iterator.next();
                docsBuffer.write(pairs.getKey() + "\n");
                docsBuffer.write(pairs.getValue() + "\n\n");
            }
            docsBuffer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveGraph() {
        try {
            FileWriter urlsFile = new FileWriter("WG1.txt");
            urlsFile.write(printGraph());
            urlsFile.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    private String getDocID(String url) {
        return url.replace("https://en.wikipedia.org/wiki/", "");
    }

    private void addIncomingLink(String page, String link) {
        String pageID = getDocID(page);
        String linkID = getDocID(link);

        if(graph.containsKey(pageID) && !graph.get(pageID).contains(linkID)) {
            graph.get(pageID).add(linkID);
        }
        else {
            graph.put(pageID, new ArrayList<>(Collections.singletonList(linkID)));
        }
    }

    private String printGraph() {
        StringBuilder graphString = new StringBuilder();

        for (Map.Entry<String, List<String>> pairs : graph.entrySet()) {
            graphString.append(pairs.getKey())
                       .append(" ")
                       .append(Arrays.toString(pairs.getValue().toArray()))
                       .append("\n");
        }

        return graphString.toString().replace("[","").replace(",", "").replace("]","");
    }

}
