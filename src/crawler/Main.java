package crawler;

public class Main {

    public static void main(String[] args) {
        System.out.println("Task 1.A: Obtaining directed web graph from Crawler");
        System.out.println("...\n");
        //new Crawler("https://en.wikipedia.org/wiki/Sustainable_energy");

        System.out.println("Task 1.B: Obtaining directed web graph from WT2g");
        System.out.println("...\n");
        new InLinksFileReader("input/wt2g_inlinks.txt");
    }
}
