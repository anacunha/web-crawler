package crawler;

public class Main {

    public static void main(String[] args) {
        System.out.println("Task 1: Crawling the documents");
        System.out.println("...");
        new Crawler("https://en.wikipedia.org/wiki/Sustainable_energy");

        System.out.println("\nTask 2: Focused Crawling BFS");
        System.out.println("...");
        new FocusedCrawler("https://en.wikipedia.org/wiki/Sustainable_energy", "solar");

        System.out.println("\nTask 2: Focused Crawling DFS");
        System.out.println("...");
        new FocusedCrawlerDFS("https://en.wikipedia.org/wiki/Sustainable_energy", "solar");
    }
}
