package crawler;

import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Parser {

    public static void parse() {
        try {
            createParsedDocsDir();
            Files.walk(Paths.get("pages_downloaded/")).forEach(filePath -> {
                if (Files.isRegularFile(filePath) && isTextFile(filePath)) {
                    try {
                        parse(filePath.getFileName().toString(), new String(Files.readAllBytes(filePath)));
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

    private static void parse(String fileName, String document) {
        StringBuilder parsedDoc = new StringBuilder();

        // Remove first line from document (contains the URL)
        Document doc = Jsoup.parse(removeFirstLine(document));

        // Get title
        parsedDoc.append(doc.select("h1#firstHeading").text()).append("\n");

        // Remove table of contents
        doc.getElementsByClass("toc").remove();

        // Remove images
        doc.select("img").remove();

        // Remove edit links
        doc.getElementsByClass("mw-editsection").remove();

        // Remove tables that are not of 'wikitable' class
        for (Element table : doc.select("table")) {
            if (!table.hasClass("wikitable")) {
                table.remove();
            }
        }

        // Remove links to references
        doc.getElementsByClass("reference").remove();

        // Remove thumbnails
        doc.getElementsByClass("thumb").remove();

        // Remove 'See Also'
        Element seeAlso = doc.getElementById("See_also");
        if (seeAlso != null)
            removeAllAfter(seeAlso);

        // Remove 'References'
        Element references = doc.getElementById("References");
        if (references != null)
            removeAllAfter(references);

        // Remove 'External Links'
        Element externalLinks = doc.getElementById("External_links");
        if (externalLinks != null)
            removeAllAfter(externalLinks);

        // Remove 'Further Reading'
        Element furtherReading = doc.getElementById("Further_reading");
        if (furtherReading != null)
            removeAllAfter(furtherReading);

        // Get Plain Textual Content
        parsedDoc.append(doc.select("div#mw-content-text").text());

        String parsedDocStr = parsedDoc.toString().toLowerCase();

        // Remove punctuation from text, but preserve hyphens and punctuation within digits
        parsedDocStr = parsedDocStr.replaceAll("[()]","");
        parsedDocStr = parsedDocStr.replaceAll("([^\\d])(?![\\-%])\\p{P}+", "$1");
        parsedDocStr = parsedDocStr.replaceAll("([\\d])(?![\\-%])\\p{P}([^\\d])", "$1$2");
        parsedDocStr = parsedDocStr.replaceAll("\\s+", " ");

        // System.out.println(parsedDocStr);

        // Save document file
        saveDocumentFile(fileName, parsedDocStr);
    }

    private static void saveDocumentFile(String fileName, String document) {
        try {
            FileWriter docFile;
            BufferedWriter docBuffer;

            docFile = new FileWriter("pages_parsed/" + fileName);
            docBuffer = new BufferedWriter(docFile);
            docBuffer.write(document);
            docBuffer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void removeAllAfter(Element element) {
        Element e = element.parent();
        while (e != null) {
            Element sibling = e.nextElementSibling();
            e.remove();
            e = sibling;
        }
    }

    public static boolean isTextFile(Path filePath) {
        return FilenameUtils.getExtension(filePath.getFileName().toString()).equals("txt");
    }

    private static String removeFirstLine(String string) {
        return string.substring(string.indexOf("\n") + 1);
    }

    private static void createParsedDocsDir() {
        File dir = new File("pages_parsed/");
        if (!dir.exists()) {
            try {
                dir.mkdir();
            }
            catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }
}
