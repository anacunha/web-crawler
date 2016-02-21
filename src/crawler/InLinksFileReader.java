package crawler;

import java.io.*;
import java.util.*;

public class InLinksFileReader {

    private WebGraph webGraph;

    public InLinksFileReader(String fileName) {
        webGraph = new WebGraph();
        readFile(fileName);
    }

    private void readFile(String fileName) {
        String line;

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                String[] pages = line.split(" ");
                String destination = pages[0];
                webGraph.addPage(destination);

                // In and Out Links Graph Representations
                for (int i = 1; i < pages.length; i++) {
                    webGraph.addIncomingLink(destination, pages[i]);
                    webGraph.addOutgoingLink(destination, pages[i]);
                }
            }
            bufferedReader.close();
            webGraph.saveGraph("output/WG2");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
