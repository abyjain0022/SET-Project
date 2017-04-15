package MileStone1;

import java.util.*;

//This class creates BIWORD index  
public class BiwordIndex {

    HashMap<String, ArrayList<Integer>> bIndex;

    public BiwordIndex() {
        bIndex = new HashMap<String, ArrayList<Integer>>();
    }

    // addTerms method will add terms mapping to docIDs
    public void addTerms(String term, int docID) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        int id;
        if (bIndex.containsKey(term)) {
            list = bIndex.get(term);
            id = list.get(list.size() - 1);
            if (!(id == docID)) {
                list.add(docID);
                bIndex.put(term, list);
            }
        } else {
            list.add(docID);
            bIndex.put(term, list);
        }
    }

    // getDictionary method is used to retrive dictionary 
    public String[] getDictionary() {
        String[] terms = new String[bIndex.size()];
        int i = 0;
        for (String key : bIndex.keySet()) {
            terms[i] = key;
            i++;
        }
        Arrays.sort(terms);
        return terms;
    }

    // getList method return list of docIDs containing string
    public ArrayList<Integer> getList(String term) {
        bIndex.get(term);
        return bIndex.get(term);
    }
}
