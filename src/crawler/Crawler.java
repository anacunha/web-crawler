package crawler;

import org.apache.commons.lang3.text.WordUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;


public class Crawler {

    private static final int MAX_DEPTH_TO_CRAWL = 5;
    private static final int MAX_PAGES_TO_CRAWL = 1000;
    private Map<String, String> crawledPages;
    private List<String> requestQueue;
    private StringBuilder urlsList;

    public Crawler(String seed) {
        urlsList = new StringBuilder();
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
                //System.out.println("Page Crawled: " + currentPage);

                for(Element link : anchorElements) {

                    // Only add if it's not a redirect
                    if (!link.hasClass("mw-redirect")) {

                        // Get the absolute URL from link
                        String url = link.absUrl("href");

                        // Only follow links with prefix http://en.wikipedia.org/wiki
                        // Do not include administrative links containing :
                        if (Pattern.matches("^https?://en\\.wikipedia\\.org/wiki/[^:]*", url)) {

                            // Remove # from URLs
                            url = url.split("#")[0];
                            requestQueue.add(url);
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

    private String getNextPage() {
        String nextPage = requestQueue.remove(0);

        while(crawledPages.containsKey(nextPage)) {
            nextPage = requestQueue.remove(0);
        }

        return nextPage;
    }

    private void saveURLs() {
        try {
            FileWriter urlsFile = new FileWriter("urls.txt");
            urlsFile.write(urlsList.toString());
            urlsFile.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDocuments() {
        try {
            FileWriter docsFile;
            BufferedWriter docsBuffer;

            for (Map.Entry<String, String> pairs : crawledPages.entrySet()) {
                docsFile = new FileWriter("pages_downloaded/" + getDocID(pairs.getValue()) + ".txt");
                docsBuffer = new BufferedWriter(docsFile);
                docsBuffer.write(pairs.getKey() + "\n");
                docsBuffer.write(pairs.getValue());
                docsBuffer.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getDocID(String content) {
        Document doc = Jsoup.parse(content);
        String docID = doc.select("h1#firstHeading").text().replaceAll("[-.]"," ");
               docID = WordUtils.capitalize(docID).replaceAll("\\s+","");

        return docID;
    }
}
