package MileStone1;

import MileStone2.DiskPositionalIndex;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Abhishek
 */
public class NearQuery {

    //searchNearQuery method returns list of docIds if term found within K positions.
    public ArrayList<Integer> searchNearQuery(String[] term1, int k, DiskPositionalIndex index) {
        QueryHandler queryHandler = new QueryHandler();
        ArrayList<Integer> result = new ArrayList<>();
        int i = 0, j = 0;
        term1[0] = queryHandler.processQueryLiteral(term1[0]);
        term1[1] = queryHandler.processQueryLiteral(term1[1]);
        List<PositionalPosting> ps1 = new ArrayList<PositionalPosting>();
        List<PositionalPosting> ps2 = new ArrayList<PositionalPosting>();
        ps1 = index.GetPositionalPostings(term1[0]);
        ps2 = index.GetPositionalPostings(term1[1]);
        PositionalPosting l1;
        PositionalPosting l2;
        ArrayList<Integer> positions1;
        ArrayList<Integer> positions2;
        boolean flag = false;
        if (ps1 != null && ps2 != null) {
            while (i < ps1.size() && j < ps2.size()) {
                l1 = ps1.get(i);
                l2 = ps2.get(j);
                if (l1.getDocId() == l2.getDocId()) {
                    positions1 = l1.getPositions();
                    positions2 = l2.getPositions();
                    flag = checkPosition(positions1, positions2, k);
                    i = i + 1;
                    j = j + 1;
                    if (flag) {
                        result.add(l1.getDocId());
                    }
                } else if (l1.getDocId() < l2.getDocId()) {
                    i = i + 1;
                } else {
                    j = j + 1;
                }
            }
        }
        return result;
    }

    //parseNearQuery method return array list of terms For Ex:- 
    // user enters :-  in the NEAR/2 park national
    // output of this method will be :- [in, the NEAR/2 park, national]
    public ArrayList<String> parseNearQuery(String searchQuery) {
        String[] terms = searchQuery.split(" ");
        ArrayList<String> result = new ArrayList<String>();
        int i = 0, k = 0;
        String no;
        while (i < terms.length) {
            if (terms[i].matches("near/[0-9]")) {
                result.remove(terms[i - 1]);
                result.add(terms[i - 1] + " " + terms[i] + " " + terms[i + 1]);
                no = String.valueOf(terms[i].charAt(terms[i].indexOf("/") + 1));
                k = Integer.parseInt(no);
                i = i + 2;
            } else {
                result.add(terms[i]);
                i++;
            }
        }
        return result;
    }

    //checkPosition method return boolean 
    // true if term found within K position.
    private boolean checkPosition(ArrayList<Integer> positions1, ArrayList<Integer> positions2, int k) {
        int i = 0, j = 0;
        while (i < positions1.size() && j < positions2.size()) {
            if (positions1.get(i) < positions2.get(j)) {
                if (positions2.get(j) - positions1.get(i) < k) {
                    return true;
                }
                i = i + 1;
            } else {
                j = j + 1;
            }
        }
        return false;
    }
}
