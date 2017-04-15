package MileStone1;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * A very simple search engine. Uses an inverted index over a folder of TXT
 * files.
 */
public class SimpleEngine {

    public List<String> fileWalkThrough(Path currentWorkingPath, PositionalInvertedIndex positionalInvertedIndex, BiwordIndex biwordIndex) throws IOException {
        // the inverted index
        //final PositionalInvertedIndex positionalInvertedIndex = new PositionalInvertedIndex();
        // the list of file names that were processed
        final List<String> fileNames = new ArrayList<String>();

        //final BiwordIndex biwordIndex=new BiwordIndex();
        // This is our standard "walk through all .txt files" code.
        Files.walkFileTree(currentWorkingPath, new SimpleFileVisitor<Path>() {
            int mDocumentID = 0;

            public FileVisitResult preVisitDirectory(Path dir,
                    BasicFileAttributes attrs) {
                // make sure we only process the current working directory
                if (currentWorkingPath.equals(dir)) {
                    return FileVisitResult.CONTINUE;
                }
                return FileVisitResult.SKIP_SUBTREE;
            }

            public FileVisitResult visitFile(Path file,
                    BasicFileAttributes attrs) {
                // only process .txt files
                if (file.toString().endsWith(".json")) {
                    // we have found a .txt file; add its name to the fileName list,
                    // then index the file and increase the document ID counter.
                    fileNames.add(file.getFileName().toString());
                    indexFile(file.toFile(), positionalInvertedIndex, biwordIndex, mDocumentID);
                    mDocumentID++;
                }
                return FileVisitResult.CONTINUE;
            }

            // don't throw exceptions if files are locked/other errors occur
            public FileVisitResult visitFileFailed(Path file,
                    IOException e) {
                return FileVisitResult.CONTINUE;
            }
        });
        return fileNames;
    }

    /**
     * Indexes a file by reading a series of tokens from the file, treating each
     * token as a term, and then adding the given document's ID to the inverted
     * index for the term.
     *
     * @param file a File object for the document to index.
     * @param index the current state of the index for the files that have
     * already been processed.
     * @param positionalInvertedIndex
     * @param biwordIndex
     * @param docID the integer ID of the current document, needed when indexing
     * each term from the document.
     */
    private void indexFile(File file, PositionalInvertedIndex positionalInvertedIndex,
            BiwordIndex biwordIndex, int docID) {
        try {
            // TO-DO: finish this method for indexing a particular file.
            // Construct a SimpleTokenStream for the given File.
            // Read each token from the stream and add it to the index.           
            JSONParser jsonParser = new JSONParser();
            Object object = jsonParser.parse(new FileReader(file));
            JSONObject jsonObject = (JSONObject) object;
            SimpleTokenStream simpleTokenStream = new SimpleTokenStream(jsonObject.get("body").toString());
            //CAll Porter stemmer class before adding index
            PorterStemmer porterStemmer = new PorterStemmer();
            String hypenWord = new String();
            String temp = new String();
            String[] stems = new String[3];
            int position = 1;
            while (simpleTokenStream.hasNextToken()) {
                String term = simpleTokenStream.nextToken();
                if (term != null) {
                    if (term.contains("-")) {
                        stems = removehypen(term);
                        if (stems != null) {
                            for (String word : stems) {
                                if (word == null) {
                                    break;
                                } else {
                                    positionalInvertedIndex.addTerm(PorterStemmer.processToken(word), docID, position);
                                    position++;
                                }
                            }
                            hypenWord = PorterStemmer.processToken(term.replaceAll("-", ""));
                            temp = temp + hypenWord;
                            biwordIndex.addTerms(temp, docID);
                            temp = hypenWord + " ";
                        }
                    } else {
                        String token = PorterStemmer.processToken(term);
                        positionalInvertedIndex.addTerm(token, docID, position);
                        position++;
                        if (position == 2) {
                            temp = token + " ";
                        } else {
                            temp = temp + token;
                            biwordIndex.addTerms(temp, docID);
                            temp = token + " ";
                        }
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SimpleEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SimpleEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(SimpleEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void printResults(PositionalInvertedIndex positionalInvertedIndex, List<String> fileNames) {
        String dictionary[] = positionalInvertedIndex.getDictionary();
        int longestWord = 0;
        for (int i = 0; i < dictionary.length; i++) {
            longestWord = Math.max(dictionary[i].length(), longestWord);
        }
        for (String term : dictionary) {
            System.out.println("");
            System.out.print(term);
            printSpace(longestWord - term.length() + 1);
            System.out.print(": ");
            ArrayList<PositionalPosting> postingList = positionalInvertedIndex.getPostings(term);
            for (int i = 0; i < postingList.size(); i++) {
                int ind = postingList.get(i).getDocId();
                System.out.print(" " + fileNames.get(ind));
            }
        }
    }

    private void printBiwordResults(BiwordIndex bindex, List<String> fileNames) {
        String[] dictionary = bindex.getDictionary();
        int longestword = 0;
        System.err.println(" BIWORD INDEX RESULT :- ");
        for (String term : dictionary) {
            longestword = Math.max(longestword, term.length());
        }
        for (int i = 0; i < dictionary.length; i++) {
            System.out.print(dictionary[i]);
            printSpace(longestword - dictionary[i].length());
            System.out.print(bindex.getList(dictionary[i]));
            System.out.println();
        }
    }

    private void printSpace(int longestWord) {
        for (int i = 0; i < longestWord; i++) {
            System.out.print(" ");
        }
    }

    private String[] removehypen(String word) {
        String[] stem = new String[3];
        word = word.replaceAll("[-]+", "-");
        if (word.matches("(\\w+[-]\\w+){1}")) {
            stem[0] = word.replaceAll("\\W", "");
            stem[1] = word.substring(0, word.indexOf("-"));
            stem[2] = word.substring(word.indexOf("-") + 1, word.length());
        } else if (word.matches("[-]")) {
            return null;
        } else {
            stem[0] = word.replaceAll("[-]", "");
        }
        return stem;
    }
}
