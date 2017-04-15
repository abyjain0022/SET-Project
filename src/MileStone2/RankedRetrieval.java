package MileStone2;

import MileStone1.*;
import java.util.*;
import java.util.Map.Entry;
import javafxapplication1.*;

/**
 *
 * @author darsh
 */
public class RankedRetrieval {

    private static String mPath;

    public RankedRetrieval(String path) {
        mPath = path;
    }

    public PriorityQueue<Entry<Integer, Double>> calculateScore(String query, int formulaMode) {
        DiskPositionalIndex index = new DiskPositionalIndex(mPath);
        QueryHandler queryHandler = new QueryHandler();
        PriorityQueue<Entry<Integer, Double>> pq = null;
        HashMap<Integer, Double> accumalator = new HashMap<>();
        HashMap<Integer, Double> wdtValues = new HashMap<>();
        JavaFXApplication1.fileNames = index.getFileNames();
        String[] terms = query.split(" ");
        int N = JavaFXApplication1.fileNames.size();
        if (!terms.equals(null)) {
            for (String token : terms) {
                String term = queryHandler.processQueryLiteral(token);
                if (term.contains("-")) {
                    String[] stems = queryHandler.removehypen(term);
                    for (String stem : stems) {
                        index.readValues(stem, formulaMode, accumalator, N);
                    }
                } else {
                    index.readValues(term, formulaMode, accumalator, N);
                }
            }
            if (!accumalator.isEmpty() && formulaMode != 3) {
                index.normalize(accumalator, formulaMode);
            }
            if (!accumalator.isEmpty()) {
                pq = new PriorityQueue<Map.Entry<Integer, Double>>(
                        accumalator.size(), new Comparator<Entry<Integer, Double>>() {
                    @Override
                    public int compare(Entry<Integer, Double> arg0,
                            Entry<Integer, Double> arg1) {

                        return arg1.getValue().compareTo(arg0.getValue());
                    }
                });

                pq.addAll(accumalator.entrySet());
            }
        }

        return pq;
    }
}
