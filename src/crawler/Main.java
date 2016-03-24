package crawler;

public class Main {

    public static void main(String[] args) {

        //new Crawler("https://en.wikipedia.org/wiki/Sustainable_energy");

        System.out.println("Task 1: Generating the corpus from raw Wikipedia articles previously downloaded");
        System.out.println("...");
        Parser.parse();

    }
}
