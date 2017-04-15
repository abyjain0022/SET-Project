package MileStone1;

import MileStone2.DiskBiwordIndex;
import MileStone2.DiskPositionalIndex;
import java.util.ArrayList;

/**
 *
 * @author Abhishek
 */
public class PhraseQuery {

    PorterStemmer porterStemmer = new PorterStemmer();

    //This method handles phrase query
    public ArrayList<Integer> phraseQuery(String phrase, DiskPositionalIndex diskPositionalIndex, DiskBiwordIndex diskBiwordIndex) {
        QueryHandler queryHandler = new QueryHandler();
        ArrayList<Integer> result = new ArrayList<Integer>();
        ArrayList<ArrayList<PositionalPosting>> initialPosting = new ArrayList<ArrayList<PositionalPosting>>();
        String[] tokens = phrase.replaceAll("\"", "").trim().split(" ");
        //Phrase query.
        //Flow enters "If" if phrase contains more than 2 words than it will use positional inverted index to find result.
        if (tokens.length > 2) {
            for (int i = 0; i < tokens.length; i++) {
                initialPosting.add(diskPositionalIndex.GetPositionalPostings(queryHandler.processQueryLiteral(tokens[i])));
            }
            result.addAll(searchPhraseQuery(initialPosting));
        }
        //Biword query.
        //Flow enters "If" if phrase contains 2 words than it will use Biword index to find result.
        if (tokens.length == 2) {
            StringBuilder phraseAfterStem = new StringBuilder();
            for (int i = 0; i < tokens.length; i++) {
                phraseAfterStem.append(queryHandler.processQueryLiteral(tokens[i]));
                phraseAfterStem.append(" ");
            }
            if (diskBiwordIndex.GetPostings(phraseAfterStem.toString().trim()) != null) {
                result.addAll(diskBiwordIndex.GetPostings(phraseAfterStem.toString().trim()));
            }
        }
        return result;
    }

    //Process search for phrase query.
    private ArrayList<Integer> searchPhraseQuery(ArrayList<ArrayList<PositionalPosting>> initialPosting) {
        QueryHandler queryHandler = new QueryHandler();
        ArrayList<Integer> result = new ArrayList<>();
        ArrayList<Integer> commonResult = new ArrayList<Integer>();
        ArrayList<PositionalPosting> mergePosting;
        ArrayList<PositionalPosting> nextPosting;
        mergePosting = initialPosting.remove(0);
        while (initialPosting.size() > 0) {
            nextPosting = initialPosting.remove(0);
            mergePosting = mergePostingList(mergePosting, nextPosting);

        }
        for (PositionalPosting pp : mergePosting) {
            commonResult.add(pp.getDocId());
        }
        if (result.isEmpty()) {
            result.addAll(commonResult);
        } else {
            result = queryHandler.and(result, commonResult);
        }

        return result;
    }

    //This method returns merged posting list with updated positions (the greater one) 
    private ArrayList<PositionalPosting> mergePostingList(ArrayList<PositionalPosting> posting1, ArrayList<PositionalPosting> posting2) {
        ArrayList<PositionalPosting> posting = new ArrayList<>();
        PositionalPosting p1, p2, pResult;
        int i = 0;
        int j = 0;
        if (posting1.isEmpty() || posting2.isEmpty()) {
            return posting;
        }
        while (posting1.size() > i || posting2.size() > j) {
            p1 = posting1.get(i);
            p2 = posting2.get(j);
            //Comparing DocID of two posting lists
            if (p1.getDocId() == p2.getDocId()) {
                pResult = mergePositions(p1, p2);
                if (pResult != null) {
                    posting.add(pResult);
                }
                i = i + 1;
                j = j + 1;
            } else if (p1.getDocId() < p2.getDocId()) // If DocID of posting list 1 is smaller than 2.
            {
                i = i + 1;
            } else if (p1.getDocId() > p2.getDocId()) // If DocID of posting list 2 is smaller than 1.
            {
                j = j + 1;
            }
            if (i == posting1.size()) // Out of index
            {
                break;
            }
            if (j == posting2.size()) // Out of index
            {
                break;
            }
        }
        return posting;
    }

    //This method will merge positions in postingList
    private PositionalPosting mergePositions(PositionalPosting p1, PositionalPosting p2) {
        ArrayList<Integer> result = new ArrayList<>();
        ArrayList<Integer> prePosition = p1.getPositions();
        ArrayList<Integer> postPosition = p2.getPositions();
        Integer[] postArray = new Integer[postPosition.size()];
        postArray = postPosition.toArray(postArray);
        int i = 0;
        //Checking positions of two different posting list having same doc id with difference of only 1.
        for (int pre : prePosition) {
            while (pre > postArray[i] && i < postArray.length - 1) {
                i = i + 1;
            }
            if (pre + 1 == postArray[i]) {
                result.add(postArray[i]);
            }
        }
        if (result.isEmpty()) {
            return null;
        }
        return new PositionalPosting(p1.getDocId(), result);
    }
}
