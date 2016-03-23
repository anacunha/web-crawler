package crawler;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Parser {

    public static void parse() {
        try {
            Files.walk(Paths.get("pages/")).forEach(filePath -> {
                if (Files.isRegularFile(filePath) && isTextFile(filePath)) {
                    try {
                        //System.out.println(filePath.getFileName());
                        parse(new String(Files.readAllBytes(filePath)));
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static void parse(String document) {
        StringBuilder parsedDoc = new StringBuilder();

        // Remove first line from document (contains the URL)
        Document doc = Jsoup.parse(removeFirstLine(document));

        // Title
        parsedDoc.append(doc.select("h1#firstHeading").text()).append("\n");

        // Remove table of contents
        doc.getElementsByClass("toc").remove();

        // Remove edit links
        doc.getElementsByClass("mw-editsection").remove();

        // Remove tables
        // Selecionar o contrario?
        // Tables que não são da classe wikitable ???
        doc.select("table").remove();

        // Remove references
        doc.getElementsByClass("reference").remove();

        // Remove thumbnails
        doc.getElementsByClass("thumb").remove();

        // Plain Textual Content
        parsedDoc.append(doc.select("div#mw-content-text").text());



        System.out.println(parsedDoc.toString());
    }

    private static boolean isTextFile(Path filePath) {
        return FilenameUtils.getExtension(filePath.getFileName().toString()).equals("txt");
    }

    private static String removeFirstLine(String string) {
        return string.substring(string.indexOf("\n") + 1);
    }
}
