package crawler;

public class Main {

    public static void main(String[] args) {

        //new Crawler("https://en.wikipedia.org/wiki/Sustainable_energy");

        System.out.println("Task 1: Generating the corpus from raw Wikipedia articles previously downloaded");
        System.out.println("...");
        Parser.parse();
        System.out.println("\nParsed pages saved to /pages_parsed folder");

        System.out.println("\nTask 2: Implementing an inverted indexer and creating inverted indexes");
        System.out.println("...");
        Indexer indexer = new Indexer();
        indexer.index();

        System.out.println("\nTask 3: Generating corpus statistics:");
        System.out.println("...");
        System.out.println("\nTables saved to /tables folder\n");
        indexer.statistics();
    }
}
