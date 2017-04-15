package MileStone1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
/**
 *
 * @author Abhishek
 */
//This class is used to create positional inverted index
public class PositionalInvertedIndex {

    private HashMap<String, ArrayList<PositionalPosting>> positionalInvertedIndex;

    // Constructor used to initialize positionalInvertedIndex
    public PositionalInvertedIndex() {
        positionalInvertedIndex = new HashMap<String, ArrayList<PositionalPosting>>();
    }

    // This method is used to add term in Index which is mapped with posting list.
    public void addTerm(String token, int docID, int position) {
        ArrayList<PositionalPosting> postingList = new ArrayList<PositionalPosting>();
        ArrayList<Integer> positions = new ArrayList<Integer>();
        PositionalPosting positionalPosting;
        if (!(positionalInvertedIndex.containsKey(token))) {
            positions.add(position);
            positionalPosting = new PositionalPosting(docID, positions);
            postingList.add(positionalPosting);
            positionalInvertedIndex.put(token, postingList);
        } else {
            postingList = positionalInvertedIndex.get(token);
            positionalPosting = postingList.get(postingList.size() - 1);
            if (positionalPosting.getDocId() == docID) {
                positions = positionalPosting.getPositions();
                positions.add(position);
                positionalPosting.setPositions(positions);
                postingList.remove(postingList.size() - 1);
                postingList.add(positionalPosting);
                positionalInvertedIndex.put(token, postingList);
            } else {
                positions.add(position);
                positionalPosting = new PositionalPosting(docID, positions);
                postingList.add(positionalPosting);
                positionalInvertedIndex.put(token, postingList);
            }
        }
    }

    //This method returns list of posting for a term present in index.
    public ArrayList<PositionalPosting> getPostings(String token) {
        ArrayList<PositionalPosting> postingList = new ArrayList<PositionalPosting>();
        if (positionalInvertedIndex.containsKey(token)) {
            postingList = positionalInvertedIndex.get(token);
        }
        return postingList;
    }

    //THis method will return dictionary
    public String[] getDictionary() {
        String dictionary[];
        Set<String> keys;
        keys = positionalInvertedIndex.keySet();
        dictionary = keys.toArray(new String[keys.size()]);
        //Arrays.so;
        Arrays.sort(dictionary, new Comparator<String>() { 
            @Override 
            public int compare(String char1, String char2) 
            { 
                return char1.compareTo(char2);
            }
        });
        return dictionary;
    }

    //This method will return term count
    public int getTermCount() {
        // TO-DO: return the number of terms in the index.
        int termCount;
        termCount = positionalInvertedIndex.keySet().size();
        return termCount;
    }
}
