package crawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;


public class Crawler {

    private static final int MAX_PAGES_TO_CRAWL = 1000;
    private Map<String, String> crawledPages;
    private List<String> requestQueue;
    private WebGraph webGraph;
    private StringBuilder urlsList;

    public Crawler(String seed) {
        urlsList = new StringBuilder();
        crawledPages = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        requestQueue = new LinkedList<>();
        requestQueue.add(seed);
        webGraph = new WebGraph("WG1");
    }

    public WebGraph crawl() {
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
                webGraph.addPage(getDocID(currentPage));

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
                        webGraph.addIncomingLink(getDocID(url),
                                                 getDocID(currentPage));
                        // Add outgoing link to graph
                        webGraph.addOutgoingLink(getDocID(url),
                                                 getDocID(currentPage));
                    }
                }
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }

        // Clear incoming links not in pages
        webGraph.cleanInLinks();
        // Save Graph
        webGraph.saveGraph();
        // Save URLs
        saveURLs();
        // Save Documents
        saveDocuments();

        return webGraph;
    }

    private String getNextPage() {
        String nextPage = requestQueue.remove(0);

        while(crawledPages.containsKey(nextPage)) {
            nextPage = requestQueue.remove(0);
        }

        return nextPage;
    }

    private void saveURLs() {
        try {
            FileWriter fileWriter = new FileWriter("output/WG1_urls.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(urlsList.toString());
            bufferedWriter.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDocuments() {
        try {
            FileWriter fileWriter = new FileWriter("output/WG1_docs.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for (Map.Entry<String, String> entry : crawledPages.entrySet()) {
                bufferedWriter.write(entry.getKey() + "\n");
                bufferedWriter.write(entry.getValue() + "\n\n");
            }
            bufferedWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getDocID(String url) {
        return url.replace("https://en.wikipedia.org/wiki/", "").toLowerCase();
    }
}
