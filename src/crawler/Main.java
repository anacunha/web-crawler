package crawler;

public class Main {

    public static void main(String[] args) {
        System.out.println("Task 1.A: Obtaining directed web graph from crawled pages");
        System.out.println("...\n");
        //Crawler crawler = new Crawler("https://en.wikipedia.org/wiki/Sustainable_energy");
        //WebGraph wg1 = crawler.crawl();
        WebGraph wg1 = InLinksFileReader.readFile("WG1.txt");

        System.out.println("Statistics for WG1\n");
        wg1.getStatistics();
        System.out.println("\n===============================================================================\n");

        System.out.println("Task 1.B: Obtaining directed web graph from WT2g");
        System.out.println("...\n");
        WebGraph wg2 = InLinksFileReader.readFile("WG2.txt");

        System.out.println("Statistics for WG2\n");
        wg2.getStatistics();
        System.out.println("\n===============================================================================\n");

        System.out.println("\nTask 2.B: Running PageRank algorithm on WG1 (d = 0.85)");
        System.out.println("...\n");
        PageRank wg1PR = new PageRank(wg1, 0.85);
        wg1PR.getPageRanks();
        System.out.println("Top 50 WG1 pages by PageRank score (d = 0.85)\n");
        wg1PR.getTop50();

        System.out.println("\n===============================================================================\n");
        System.out.println("Task 2.B: Running PageRank algorithm on WG2 (d = 0.85)");
        System.out.println("...\n");
        PageRank wg2PR = new PageRank(wg2, 0.85);
        wg2PR.getPageRanks();
        System.out.println("Top 50 WG2 pages by PageRank score (d = 0.85)\n");
        wg2PR.getTop50();

        System.out.println("\n===============================================================================\n");
        System.out.println("Task 3.A: Top 50 WG1 pages by in-link count");
        System.out.println("...\n");
        wg1.getTop50InLink();

        System.out.println("\n===============================================================================\n");
        System.out.println("Task 3.A: Top 50 WG2 pages by in-link count");
        System.out.println("...\n");
        wg2.getTop50InLink();

        System.out.println("\n===============================================================================\n");
        System.out.println("Task 3.B: Top 50 WG1 pages by PageRank score (d = 0.95)");
        System.out.println("...\n");
        wg1PR = new PageRank(wg1, 0.95);
        wg1PR.getPageRanks();
        wg1PR.getTop50();

        System.out.println("\n===============================================================================\n");
        System.out.println("Task 3.B: Top 50 WG2 pages by PageRank score (d = 0.95)");
        System.out.println("...\n");
        wg2PR = new PageRank(wg2, 0.95);
        wg2PR.getPageRanks();
        wg2PR.getTop50();
    }
}
