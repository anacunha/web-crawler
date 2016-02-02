package crawler;

public class Main {

    public static void main(String[] args) {
        Crawler crawler = new Crawler("https://en.wikipedia.org/wiki/Sustainable_energy");
        crawler.crawl();
    }
}
