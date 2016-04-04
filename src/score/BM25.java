package score;

import java.util.HashMap;
import java.util.Map;

public class BM25 {

    private static final double R = 0;
    private static final double ri = 0;
    private static final double k1 = 1.2;
    private static final double k2 = 100;
    private static final double b = 0.75;

    public static double score(String query, double dl, double avdl, double N,
                               Map<String, Integer> n, Map<String, Integer> f) {
        double score = 0.0;
        double K = computeK(dl, avdl);
        Map<String, Integer> qf = getQueryTermFrequency(query);

        for (String term : query.split(" ")) {

            double ni = n.get(term);
            double fi = f.get(term);
            double qfi = qf.get(term);

            score += Math.log(((ri + 0.5) / (R - ri + 0.5)) / ((ni - ri + 0.5) / (N - ni - R + ri + 0.5))) *
                    (((k1 + 1) * fi) / (K + fi)) * (((k2 + 1) * qfi) / (k2 + qfi));

        }

        return score;
    }

    private static double computeK(double dl, double avdl) {
        return k1 * ((1 - b) + (b * (dl/avdl)));
    }

    private static Map<String, Integer> getQueryTermFrequency(String query) {
        Map<String, Integer> termFrequency = new HashMap<>();

        for (String term : query.split(" ")) {
            if (termFrequency.get(term) == null) {
                termFrequency.put(term, 1);
            }
            else {
                termFrequency.put(term, termFrequency.get(term) + 1);
            }
        }
        return termFrequency;
    }
}
