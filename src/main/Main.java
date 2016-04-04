package main;

import indexer.Indexer;

public class Main {

    public static void main(String[] args) {

        //new Crawler("https://en.wikipedia.org/wiki/Sustainable_energy");

        System.out.println("\nTask 2: Implementing an inverted indexer and creating inverted indexes");
        System.out.println("...");
        Indexer indexer = new Indexer();
        indexer.index();

    }
}
