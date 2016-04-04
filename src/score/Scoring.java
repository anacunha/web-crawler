package score;

import indexer.Indexer;
import main.MapUtil;
import main.TableUtil;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Scoring {

    private Indexer indexer;

    public Scoring(Indexer indexer, Map<String, String> queries) {
        this.indexer = indexer;
        double avdl = getAverageDocLength();
        double N = indexer.getCorpusSize();
        Map<String, Double> dl = indexer.getDocLengths();
        Map<String, Integer> n = indexer.getDocFrequency();
        Map<String, List<Pair<String, Integer>>> index = indexer.getIndex();
        Map<String, Map<String, Integer>> f = indexer.getDocTermFrequency();

        // For every query q
        for (Map.Entry<String,String> q : queries.entrySet()) {

            String query = q.getValue();
            Map<String, Integer> qf = getQueryTermFrequency(query);
            Map<String, Double> scoreList = new LinkedHashMap<>();

            // For every term t in query q
            for (String t : q.getValue().split(" ")) {

                // For every document d containing t
                for (Pair<String, Integer> d : index.get(t)) {

                    // Calculate BM25 for that document
                    String docID = d.getLeft();
                    double score = BM25.score(query, dl.get(docID), avdl, N, n, f.get(docID), qf);
                    scoreList.put(docID, score);
                }
            }

            saveScoreList(q.getKey(), scoreList);
        }
    }

    private void saveScoreList(String queryID, Map<String, Double> scoreList) {

        // Sort by score
        scoreList = MapUtil.sortByValueDesc(scoreList);

        int rank = 1;

        // Table in csv format
        StringBuilder table = new StringBuilder("query_id,Q0,doc_id,rank,BM25_score,system_name\n");

        for (Map.Entry<String, Double> entry : scoreList.entrySet()) {
            // query_id
            table.append('"').append(queryID).append("\",");
            // Q0
            table.append("\"Q0\",");
            // doc_id
            table.append('"').append(entry.getKey()).append("\",");
            // rank
            table.append('"').append(rank).append("\",");
            // BM25_score
            table.append('"').append(entry.getValue()).append("\",");
            // system_name
            table.append("\"Search Engine Task 1\",\n");

            if (rank == 100)
                break;
            else
                rank++;
        }

        TableUtil.saveTable("tables/" + queryID + "_search_engine_task_1.csv", table.toString());
        printScoreList(queryID, scoreList);
    }

    private void printScoreList(String queryID, Map<String, Double> scoreList) {

        int rank = 1;

        System.out.format("\n%-10s%-4s%-58s%-6s%-13s%-20s",
                "Query ID ", "Q0 ", "Doc ID", "Rank ", "BM25 Score ", "System Name");

        for (Map.Entry<String, Double> entry : scoreList.entrySet()) {
            System.out.format("\n%-10s%-4s%-58s%-6s%-13.8f%-20s",
                    queryID, "Q0 ", entry.getKey(), rank, entry.getValue(), "Search Engine Task 1");

            if (rank == 100) {
                System.out.println();
                break;
            }
            else
                rank++;
        }
    }

    private double getAverageDocLength() {
        Map<String, Double> dl = indexer.getDocLengths();
        double total = 0.0;

        for (Map.Entry<String, Double> doc : dl.entrySet())
            total = total + doc.getValue();

        return (total / dl.size());
    }

    private static Map<String, Integer> getQueryTermFrequency(String query) {
        Map<String, Integer> termFrequency = new HashMap<>();

        for (String term : query.split(" ")) {
            if (!termFrequency.containsKey(term)) {
                termFrequency.put(term, 1);
            }
            else {
                termFrequency.put(term, termFrequency.get(term) + 1);
            }
        }
        return termFrequency;
    }
}
