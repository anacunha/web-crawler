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

public class FocusedCrawlerDFS {

    private static final int MAX_DEPTH_TO_CRAWL = 5;
    private static final int MAX_PAGES_TO_CRAWL = 1000;
    private Map<String, String> crawledPages;
    private Map<String, Integer> depthPages;
    private LinkedList<String> requestQueue;
    private String keyword;
    private StringBuilder urlsList;

    public FocusedCrawlerDFS(String seed, String keyword) {
        this.keyword = keyword;
        urlsList = new StringBuilder();
        crawledPages = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        depthPages = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        depthPages.put(seed, 1);
        requestQueue = new LinkedList<>();
        requestQueue.add(seed);
        crawl();
    }

    public void crawl() {
        int count = 0;

        // Stop once you've crawled 1000 unique URLs
        while(count < MAX_PAGES_TO_CRAWL) {

            String currentPage = getNextPage();
            int currentDepth = depthPages.get(currentPage);

            try {
                Connection connection = Jsoup.connect(currentPage);
                Document document = connection.get();
                Elements anchorElements = document.select("a[href]");

                // If page hasn't been crawled yet
                if(!hasBeewnCrawled(currentPage) && currentDepth <= MAX_DEPTH_TO_CRAWL) {

                    // Search keyword on page's text or check if current page
                    // had already been added to tree (meaning that either its
                    // URL or the anchor text pointing to it had the keyword)
                    if (document.text().toLowerCase().contains(keyword) || crawledPages.containsKey(currentPage)) {

                        // Add current page and document to tree of crawled pages
                        crawledPages.put(currentPage, document.html());
                        urlsList.append(currentPage).append("\n");
                        count++;
                        //System.out.println("Page Crawled: " + currentPage + " (" + currentDepth + ")");

                        if(currentDepth < MAX_DEPTH_TO_CRAWL) {
                            for (Element link : anchorElements) {
                                // Get the absolute URL from link
                                String url = link.absUrl("href");

                                // Only follow links with prefix http://en.wikipedia.org/wiki
                                // Do not include administrative links containing :
                                if (Pattern.matches("^https?://en\\.wikipedia\\.org/wiki/[^:]*", url)) {

                                    // Remove # from URLs
                                    url = url.split("#")[0];
                                    requestQueue.addFirst(url);
                                    depthPages.put(url, currentDepth + 1);

                                    // Search for keyword on link's URL or,
                                    // Search for keyword on link's anchor text
                                    if (url.toLowerCase().contains(keyword) || link.text().contains(keyword))
                                        if (!hasBeewnCrawled(url))
                                            addToBeCrawled(url);
                                }
                            }
                        }
                    }
                }
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }

        // Save URLs
        saveURLs();
        // Save Documents
        saveDocuments();
    }

    private boolean hasBeewnCrawled(String url) {
        if(crawledPages.containsKey(url)) {
            return !crawledPages.get(url).isEmpty();
        }
        return false;
    }

    private void addToBeCrawled(String url) {
        if(!crawledPages.containsKey(url))
            crawledPages.put(url, "");
    }

    private String getNextPage() {
        String nextPage = requestQueue.remove(0);

        while(crawledPages.containsKey(nextPage) && !crawledPages.get(nextPage).isEmpty()) {
            nextPage = requestQueue.remove(0);
        }

        return nextPage;
    }

    private void saveURLs() {
        try {
            FileWriter urlsFile = new FileWriter("urls_task2_dfs.txt");
            urlsFile.write(urlsList.toString());
            urlsFile.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void saveDocuments() {
        FileWriter docsFile;
        BufferedWriter docsBuffer;

        try {
            docsFile = new FileWriter("docs_task2_dfs.txt");
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
}
