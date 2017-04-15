package MileStone1;

import MileStone2.DiskBiwordIndex;
import MileStone2.DiskPositionalIndex;
import java.util.ArrayList;
import java.util.Collections;
import javafxapplication1.JavaFXApplication1;

/**
 *
 * @author Abhishek
 */
//This class handles user queries.
public class QueryHandler {

    PhraseQuery phraseQuery = new PhraseQuery();
    //SimpleEngine simpleEngine = new SimpleEngine();
    NearQuery nearQuery = new NearQuery();
    PorterStemmer porterStemmer = new PorterStemmer();
    ArrayList<String> andQueryLiteral;
    //ArrayList<Integer> postingList;
    ArrayList<Integer> andResult;
    ArrayList<Integer> orResult;
    ArrayList<Integer> searchResult;
    DiskPositionalIndex diskPositionalIndex;
    DiskBiwordIndex diskBiwordIndex;

    //This method return list of docIds based on their input query.
    public ArrayList<Integer> searchQuery(String searchQuery, String path) {
        // postingList = new ArrayList<Integer>();        
        searchResult = new ArrayList<>();
        diskPositionalIndex = new DiskPositionalIndex(path);
        diskBiwordIndex = new DiskBiwordIndex(path);
        JavaFXApplication1.fileNames = diskPositionalIndex.getFileNames();

        //It will go inside "IF", If query contains OR otherwise flow will go inside else.
        if (searchQuery.contains("+")) {
            orResult = new ArrayList<Integer>();
            String[] queryLiterals = searchQuery.split("\\+");
            for (int i = 0; i < queryLiterals.length; i++) {
                andQueryLiteral = new ArrayList<String>();
                andResult = new ArrayList<Integer>();
                String queryLiteral = queryLiterals[i].trim();
                andQueryLiteral = parseQuery(queryLiteral);
                for (int k = 0; k < andQueryLiteral.size(); k++) {
                    processQuery(andQueryLiteral.get(k), diskPositionalIndex, diskBiwordIndex, k, andQueryLiteral.size());
                }
                orResult = or(orResult, andResult);
            }
            searchResult.addAll(orResult);
        } else {
            andQueryLiteral = new ArrayList<String>();
            andResult = new ArrayList<Integer>();
            andQueryLiteral = parseQuery(searchQuery.trim());
            for (int k = 0; k < andQueryLiteral.size(); k++) {
                processQuery(andQueryLiteral.get(k), diskPositionalIndex, diskBiwordIndex, k, andQueryLiteral.size());
            }
            searchResult.addAll(andResult);
        }
        Collections.sort(searchResult);
        return searchResult;
    }

    // THis method returns ArrayList<String> of all terms inside a single query literal based on below conditions	  
    public ArrayList<String> parseQuery(String queryLiteral) {
        andQueryLiteral = new ArrayList<String>();
        if (!(queryLiteral.contains(" "))) {
            //No space
            andQueryLiteral.add(queryLiteral);
        } else if ((queryLiteral.contains(" ")) && !(queryLiteral.contains("\"")) && !(queryLiteral.contains("near/"))) {
            //Queries containing spaces
            //Does not contains any phrase,near queries
            String[] a = queryLiteral.split(" ");
            for (int j = 0; j < a.length; j++) {
                andQueryLiteral.add(a[j]);
            }
        } else if (queryLiteral.contains("\"") && queryLiteral.indexOf("\"") == 0
                && queryLiteral.lastIndexOf("\"") != queryLiteral.length() - 1
                && !(queryLiteral.contains("near/"))) {
            //Queries containing phrase and space.
            //Phrase in the start of query.
            //Does not contain near query
            andQueryLiteral.add(queryLiteral.substring(queryLiteral.indexOf("\""), queryLiteral.lastIndexOf("\"")));
            String[] tok = (queryLiteral.substring(queryLiteral.lastIndexOf("\"") + 1, queryLiteral.length())).trim().split(" ");
            for (int i = 0; i < tok.length; i++) {
                andQueryLiteral.add(tok[i]);
            }
        } else if (queryLiteral.contains("\"") && queryLiteral.indexOf("\"") != 0 && !(queryLiteral.contains("near/"))) {
            //Queries containing phrase and space.
            //Phrase in the end of query.
            //Does not contain near query            
            String[] tok = (queryLiteral.substring(0, queryLiteral.indexOf("\"") - 1)).trim().split(" ");
            for (int i = 0; i < tok.length; i++) {
                andQueryLiteral.add(tok[i]);
            }
            andQueryLiteral.add(queryLiteral.substring(queryLiteral.indexOf("\""), queryLiteral.length()));
        } else if (queryLiteral.contains("\"") && queryLiteral.indexOf("\"") == 0
                && queryLiteral.lastIndexOf("\"") == queryLiteral.length() - 1
                && !(queryLiteral.contains("near/"))) {
            //Only phrase query.    
            andQueryLiteral.add(queryLiteral.substring(queryLiteral.indexOf("\""), queryLiteral.lastIndexOf("\"")));
        } else if (queryLiteral.contains("near/")) {
            //Only near query.
            ArrayList<String> b = nearQuery.parseNearQuery(queryLiteral);
            for (String terms : b) {
                andQueryLiteral.add(terms);
            }
        }
        return andQueryLiteral;
    }

    private void processQuery(String andQueryLiteral, DiskPositionalIndex diskPositionalIndex, DiskBiwordIndex diskBiwordIndex, int k, int size) {
        if (!(andQueryLiteral.contains("\"")) && !(andQueryLiteral.contains("near/")) && !(andQueryLiteral.contains("-"))) {
            //Processing for single word
            //Does not contain BIWORD, PHRASE, NEAR and HYPHEN queries
            ArrayList<Integer> list = new ArrayList<Integer>();
            String token = processQueryLiteral(andQueryLiteral);
            list = diskPositionalIndex.GetPostings(token);
            if (list != null) {
                if ((andResult.isEmpty() && k != size - 1) || (andResult.isEmpty() && size == 1)) {
                    andResult.addAll(list);
                } else {
                    andResult = and(andResult, list);
                }
            }
        } else if ((andQueryLiteral.contains("\"")) && !(andQueryLiteral.contains("near/")) && !(andQueryLiteral.contains("-"))) {
            //Processing only for Phrase query         
            if (andResult.isEmpty()) {
                andResult.addAll(phraseQuery.phraseQuery(andQueryLiteral, diskPositionalIndex, diskBiwordIndex));
            } else {
                andResult = and(andResult, phraseQuery.phraseQuery(andQueryLiteral, diskPositionalIndex, diskBiwordIndex));
            }
        } else if (!(andQueryLiteral.contains("\"")) && (andQueryLiteral.contains("near/")) && !(andQueryLiteral.contains("-"))) {
            // Processing only for NEAR K queries                    
            int nearK = Integer.parseInt(String.valueOf(andQueryLiteral.charAt(andQueryLiteral.indexOf("/") + 1)));
            String[] terms = andQueryLiteral.split("near/" + nearK);
            if (andResult.isEmpty()) {
                andResult.addAll(nearQuery.searchNearQuery(terms, nearK, diskPositionalIndex));
            } else {
                andResult = and(andResult, nearQuery.searchNearQuery(terms, nearK, diskPositionalIndex));
            }
        } else if (andQueryLiteral.contains("-")) {
            //Processing only for HYPHEN query.                      
            //ArrayList<PositionalPosting> postinglistForHyphen = new ArrayList<PositionalPosting>();                     
            String[] termsAfterRemovingHyphens = removehypen(andQueryLiteral);
            for (String terms1 : termsAfterRemovingHyphens) {
                if (terms1 == null) {
                    break;
                } else {
                    ArrayList<Integer> listForHyphen = new ArrayList<Integer>();
                    listForHyphen = diskPositionalIndex.GetPostings(processQueryLiteral(terms1));
                    /* for(PositionalPosting postingList1 : postinglistForHyphen)
                     listForHyphen.add(postingList1.getDocId()); */
                    if (listForHyphen != null) {
                        if (andResult.isEmpty()) {
                            andResult.addAll(listForHyphen);
                        } else {
                            andResult = and(andResult, listForHyphen);
                        }
                    }
                }
            }
        }
    }

    public String processQueryLiteral(String token) {
        token = token.replaceAll("^([\\W]*)|([\\W]*)$|\'", "").toLowerCase().trim();
        token = porterStemmer.processToken(token);
        return token;
    }

    public static String[] removehypen(String word) {
        String[] stem = word.split("-");
        String[] stems= new String[stem.length+1];
        for(int i=0;i<stem.length;i++)
        {
        stems[i]=stem[i].replaceAll("^([\\W]*)|([\\W]*)$|\'", "").toLowerCase().trim();
        }
        stems[stem.length] = word.replaceAll("-", "");
        return stems;
    }

    //This method contains AND logic  
    public ArrayList<Integer> and(ArrayList<Integer> andResult, ArrayList<Integer> list) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        int i = 0, j = 0;
        while (i < andResult.size() && j < list.size()) {
            if (andResult.get(i) < list.get(j)) {
                i++;
            } else if (andResult.get(i).equals(list.get(j))) {
                result.add(andResult.get(i));
                i++;
                j++;
            } else if (andResult.get(i) > list.get(j)) {
                j++;
            }
        }
        return result;
    }

    //This method contains OR logic
    public ArrayList<Integer> or(ArrayList<Integer> orResult, ArrayList<Integer> andResult) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        int i = 0, j = 0, k = 0;
        if (orResult.isEmpty()) {
            result.addAll(andResult);
            return result;
        }
        if (andResult.isEmpty()) {
            result.addAll(orResult);
            return result;
        }
        while (i < orResult.size() || j < andResult.size()) {
            if (orResult.get(i) < andResult.get(j)) {
                result.add(k, orResult.get(i));
                i++;
                k++;
            } else if (orResult.get(i).equals(andResult.get(j))) {
                result.add(k, orResult.get(i));
                i++;
                j++;
                k++;
            } else if (orResult.get(i) > andResult.get(j)) {
                result.add(k, andResult.get(j));
                j++;
                k++;
            }

            if (i == orResult.size()) {
                for (; j < andResult.size(); j++, k++) {
                    result.add(k, andResult.get(j));
                }
            }
            if (j == andResult.size()) {
                for (; i < orResult.size(); i++, k++) {
                    result.add(k, orResult.get(i));
                }
            }
        }
        return result;
    }
}
