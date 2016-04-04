package main;

import indexer.Indexer;
import score.QueryProcessor;
import score.Scoring;

public class Main {

    public static void main(String[] args) {

        System.out.println("\nTask 1: Calculating BM25 Scores");
        System.out.println("...");

        Indexer indexer = new Indexer();
        indexer.index();

        QueryProcessor qp = new QueryProcessor("queries.txt");
        new Scoring(indexer, qp.getQueries());
        System.out.println("\nTop 100 scores saved to /tables folder");
    }
}
