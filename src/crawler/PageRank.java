package crawler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PageRank {

    private static final double D = 0.85;   /* Dumping Factor */
    private HashMap<String, Double> pageRanks;
    private List<String> sinkPages;
    private WebGraph webGraph;
    private StringBuilder perplexities;
    private double previousPerplexity;
    private int convergeCount;

    public PageRank(WebGraph webGraph) {
        this.webGraph = webGraph;
        processSinkPages();
    }

    public HashMap<String, Double> getPageRanks() {

        final int N = webGraph.getPages().size();
        HashMap<String, Double> newPageRanks = new HashMap<>();
        previousPerplexity = Integer.MAX_VALUE;
        perplexities = new StringBuilder();
        pageRanks = new HashMap<>();
        convergeCount = 0;

        // For each page p in P (set of all pages)
        for (String p : webGraph.getPages()) {
            // PR(p) = 1 / N
            pageRanks.put(p, (1.0 / N));  /* Initial Value */
        }

        // While PageRank has not converged
        while (!hasConverged()) {

            // sinkPR = 0
            double sinkPageRank = 0;

            // For each page s in S
            for (String sinkPage : sinkPages) {  /* Calculate total sink PR */
                // sinkPR += PR(s)
                sinkPageRank += pageRanks.get(sinkPage);
            }

            // For each page p in P
            for (String page : webGraph.getPages()) {

                // newPR(p) = (1 - d) / N
                double newPageRank = (1 - D) / N;

                // newPR(p) += d * sinkPR / N;
                newPageRank += (D * sinkPageRank / N);

                // For each page q in M(q),
                // where M(q) is the set of pages that link to q
                for (String q : webGraph.getInLinks().get(page)) {

                    // newPR(p) += (d * PR(q) / L(q)),
                    // where L(q) is the number of out-links from page q
                    newPageRank += (D * pageRanks.get(q) / webGraph.getOutLinks().get(q).size());
                }
                newPageRanks.put(page, newPageRank);
            }

            // For each page p in P
            for (String page : webGraph.getPages()) {
                // PR(p) = newPR(p)
                pageRanks.put(page, newPageRanks.get(page));
            }
        }
        savePerplexities();
        return pageRanks;
    }

    private void processSinkPages() {
        sinkPages = new ArrayList<>();

        // S is the set of sink pages
        // (pages that have no out links)
        for (String page : webGraph.getOutLinks().keySet()) {
            if (webGraph.getOutLinks().get(page).isEmpty()) {
                sinkPages.add(page);
            }
        }
    }

    private boolean hasConverged() {
        // PageRank can be considered as converged if the change in
        // perplexity is less than 1 for at least four consecutive iterations
        double perplexity = getPerplexity();
        perplexities.append(perplexity).append("\n");

        if (Math.abs(perplexity - previousPerplexity) < 1) {
            convergeCount++;
            previousPerplexity = perplexity;
            return convergeCount >= 4;
        }
        else {
            convergeCount = 0;
            previousPerplexity = perplexity;
            return false;
        }
    }

    private double getEntropy() {
        double entropy = 0;

        for (String page : pageRanks.keySet()) {

            // PR(p) * log_2 PR(p)
            entropy += pageRanks.get(page) * (Math.log(pageRanks.get(page)) / Math.log(2));
        }
        return entropy * (-1);
    }

    private double getPerplexity() {
        return Math.pow(2, getEntropy());
    }

    private void savePerplexities() {
        try {
            FileWriter fileWriter = new FileWriter("output/" + webGraph.getName() + "_perplexities.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(perplexities.toString());
            bufferedWriter.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

}
