package crawler;

import java.util.Map;

public class Main {

    public static void main(String[] args) {
        System.out.println("Task 1.A: Obtaining directed web graph from Crawler");
        System.out.println("...\n");
        Crawler crawler = new Crawler("https://en.wikipedia.org/wiki/Sustainable_energy");
        WebGraph wg1 = crawler.crawl();
        PageRank wg1PR = new PageRank(wg1);
        wg1PR.getPageRanks();

        System.out.println("Task 1.B: Obtaining directed web graph from WT2g");
        System.out.println("...\n");
        WebGraph wg2 = InLinksFileReader.readFile("wt2g_inlinks.txt");

        WebGraph sample = InLinksFileReader.readFile("sample.txt");
        PageRank pageRank = new PageRank(sample);
        for(Map.Entry<String, Double> entry : pageRank.getPageRanks().entrySet()) {
            System.out.println("Page Rank (" + entry.getKey() + "): " + entry.getValue());
        }


    }
}
