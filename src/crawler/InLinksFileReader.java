package crawler;

import java.io.*;

public class InLinksFileReader {

    public static WebGraph readFile(String fileName) {
        WebGraph webGraph = new WebGraph(fileName.replace(".txt", ""));
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
            webGraph.saveGraph();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return webGraph;
    }
}
