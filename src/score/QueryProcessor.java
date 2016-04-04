package score;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class QueryProcessor {

    private Map<String, String> queries;
    private String queryFile;

    public QueryProcessor(String queryFile) {
        this.queryFile = queryFile;
        queries = new HashMap<>();
    }

    public Map<String, String> getQueries() {
        try (Stream<String> stream = Files.lines(Paths.get(queryFile))) {

            stream.forEach(this::processQuery);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return queries;
    }

    private void processQuery(String query) {
        String queryID = query.substring(0, query.indexOf(" "));
        queries.put(queryID, query.substring(query.indexOf(" ")).trim());
    }
}
