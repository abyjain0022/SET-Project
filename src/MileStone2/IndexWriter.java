package MileStone2;

import MileStone1.BiwordIndex;
import MileStone1.PorterStemmer;
import MileStone1.PositionalInvertedIndex;
import MileStone1.PositionalPosting;
import MileStone1.QueryHandler;
import MileStone1.SimpleEngine;
import MileStone1.SimpleTokenStream;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Writes an inverted indexing of a directory to disk.
 */
public class IndexWriter {

    QueryHandler queryHandler = new QueryHandler();
    private String mFolderPath;
    private static double avgTokens = 0;
    private static int docId = 0;

    /**
     * Constructs an IndexWriter object which is prepared to index the given
     * folder.
     */
    public IndexWriter(String folderPath) {
        mFolderPath = folderPath;
    }

    /**
     * Builds and writes an inverted index to disk. Creates three files:
     * vocab.bin, containing the vocabulary of the corpus; postings.bin,
     * containing the postings list of document IDs; vocabTable.bin, containing
     * a table that maps vocab terms to postings locations
     */
    public void buildIndex() {
        buildIndexForDirectory(mFolderPath);
    }

    /**
     * Builds the normal NaiveInvertedIndex for the folder.
     */
    private static void buildIndexForDirectory(String folder) {
        PositionalInvertedIndex index = new PositionalInvertedIndex();
        BiwordIndex bIndex = new BiwordIndex();

        // Index the directory using a naive index
        indexFiles(folder, index, bIndex);
        // at this point, "index" contains the in-memory inverted index 
        // now we save the index to disk, building three files: the postings index,
        // the vocabulary list, and the vocabulary table.

        // the array of terms
        String[] dictionary = index.getDictionary();
        // an array of positions in the vocabulary file
        long[] vocabPositions = new long[dictionary.length];
        buildVocabFile(folder, dictionary, vocabPositions);
        buildPostingsFile(folder, index, dictionary, vocabPositions);
        String[] biwordDictionary = bIndex.getDictionary();
        long[] biwordVocabPositions = new long[biwordDictionary.length];
        buildBiwordVocabFile(folder, biwordDictionary, biwordVocabPositions);
        buildBiwordPostingsFile(folder, bIndex, biwordDictionary, biwordVocabPositions);
    }

    /**
     * Builds the postings.bin file for the indexed directory, using the given
     * NaiveInvertedIndex of that directory.
     */
    private static void buildPostingsFile(String folder, PositionalInvertedIndex index,
            String[] dictionary, long[] vocabPositions) {
        FileOutputStream postingsFile = null;
        try {
            postingsFile = new FileOutputStream(
                    new File(folder, "postings.bin")
            );

            // simultaneously build the vocabulary table on disk, mapping a term index to a
            // file location in the postings file.
            FileOutputStream vocabTable = new FileOutputStream(
                    new File(folder, "vocabTable.bin")
            );

            // the first thing we must write to the vocabTable file is the number of vocab terms.
            byte[] tSize = ByteBuffer.allocate(4)
                    .putInt(dictionary.length).array();
            vocabTable.write(tSize, 0, tSize.length);
            int vocabI = 0;

            RandomAccessFile docLengthA;
            docLengthA = new RandomAccessFile(new File(folder, "docLengthA.bin"), "r");
            byte[] docLengthAByte = new byte[8];
            docLengthA.read(docLengthAByte, 0, docLengthAByte.length);
            double avgDocLength = ByteBuffer.wrap(docLengthAByte).getDouble();
            RandomAccessFile docWeights;
            docWeights = new RandomAccessFile(new File(folder, "docWeights.bin"), "r");
            for (String s : dictionary) {
                // for each String in dictionary, retrieve its postings.
                ArrayList<PositionalPosting> postings = index.getPostings(s);
                byte[] byte3 = ByteBuffer.allocate(16).array();
                // write the vocab table entry for this term: the byte location of the term in the vocab list file,
                // and the byte location of the postings for the term in the postings file.
                byte[] vPositionBytes = ByteBuffer.allocate(8)
                        .putLong(vocabPositions[vocabI]).array();
                byte[] pPositionBytes = ByteBuffer.allocate(8)
                        .putLong(postingsFile.getChannel().position()).array();
                System.arraycopy(vPositionBytes, 0, byte3, 0, vPositionBytes.length);
                System.arraycopy(pPositionBytes, 0, byte3, 8, pPositionBytes.length);
                vocabTable.write(byte3, 0, byte3.length);
                // write the postings file for this term. first, the document frequency for the term, then
                // the document IDs, encoded as gaps.
                byte[] docFreqBytes = ByteBuffer.allocate(4)
                        .putInt(postings.size()).array();
                postingsFile.write(docFreqBytes, 0, docFreqBytes.length);

                int lastDocId = 0;
                byte[] docIdBytes, wdtbyte, termFreqBytes, byte1, termPositionBytes, byte2;
                for (PositionalPosting p : postings) {
                    docIdBytes = ByteBuffer.allocate(4)
                            .putInt(p.getDocId() - lastDocId).array(); // encode a gap, not a doc ID
                    wdtbyte = addWdtValuesInPostingsFile(folder, p.getPositions().size(), p.getDocId(), docWeights, avgDocLength);
                    termFreqBytes = ByteBuffer.allocate(4).putInt(p.getPositions().size()).array();
                    int lastPosition = 0;
                    byte1 = ByteBuffer.allocate(4 * p.getPositions().size()).array();
                    int pos = 0;
                    for (int termPosition : p.getPositions()) {
                        termPositionBytes = ByteBuffer.allocate(4).putInt(termPosition - lastPosition).array();

                        System.arraycopy(termPositionBytes, 0, byte1, pos, termPositionBytes.length);
                        lastPosition = termPosition;
                        pos = pos + 4;
                    }
                    byte2 = ByteBuffer.allocate(40 + 4 * p.getPositions().size()).putInt(p.getPositions().size()).array();
                    System.arraycopy(docIdBytes, 0, byte2, 0, docIdBytes.length);
                    System.arraycopy(wdtbyte, 0, byte2, 4, wdtbyte.length);
                    System.arraycopy(termFreqBytes, 0, byte2, 36, termFreqBytes.length);
                    System.arraycopy(byte1, 0, byte2, 40, byte1.length);
                    postingsFile.write(byte2, 0, byte2.length);
                    lastDocId = p.getDocId();
                }
                vocabI++;
            }
            vocabTable.close();
            postingsFile.close();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } finally {
            try {
                postingsFile.close();
            } catch (IOException ex) {
            }
        }
    }

    private static void buildBiwordPostingsFile(String folder, BiwordIndex bIndex, String[] biwordDictionary, long[] biwordVocabPositions) {
        FileOutputStream biwordPostingsFile = null;
        try {
            biwordPostingsFile = new FileOutputStream(
                    new File(folder, "biwordPostings.bin")
            );

            // simultaneously build the vocabulary table on disk, mapping a term index to a
            // file location in the postings file.
            FileOutputStream biwordVocabTable = new FileOutputStream(
                    new File(folder, "biwordVocabTable.bin")
            );
            // the first thing we must write to the vocabTable file is the number of vocab terms.
            byte[] tSize = ByteBuffer.allocate(4)
                    .putInt(biwordDictionary.length).array();
            biwordVocabTable.write(tSize, 0, tSize.length);
            int vocabI = 0;
            for (String s : biwordDictionary) {
                // for each String in dictionary, retrieve its postings.
                ArrayList<Integer> biwordPostings = bIndex.getList(s);
                byte[] byte3 = ByteBuffer.allocate(16).array();
                // write the vocab table entry for this term: the byte location of the term in the vocab list file,
                // and the byte location of the postings for the term in the postings file.
                byte[] vPositionBytes = ByteBuffer.allocate(8)
                        .putLong(biwordVocabPositions[vocabI]).array();
                byte[] pPositionBytes = ByteBuffer.allocate(8)
                        .putLong(biwordPostingsFile.getChannel().position()).array();
                System.arraycopy(vPositionBytes, 0, byte3, 0, vPositionBytes.length);
                System.arraycopy(pPositionBytes, 0, byte3, 8, pPositionBytes.length);
                biwordVocabTable.write(byte3, 0, byte3.length);
                byte[] byte1 = ByteBuffer.allocate(4 + 4 * biwordPostings.size()).array();
                // write the postings file for this term. first, the document frequency for the term, then
                // the document IDs, encoded as gaps.
                byte[] docFreqBytes = ByteBuffer.allocate(4)
                        .putInt(biwordPostings.size()).array();
                int lastDocId = 0;
                byte[] byte2 = ByteBuffer.allocate(4 * biwordPostings.size()).array();
                int pos = 0;
                for (int i = 0; i < biwordPostings.size(); i++) {
                    byte[] docIdBytes = ByteBuffer.allocate(4)
                            .putInt(biwordPostings.get(i) - lastDocId).array(); // encode a gap, not a doc ID
                    System.arraycopy(docIdBytes, 0, byte2, pos, docIdBytes.length);
                    lastDocId = biwordPostings.get(i);
                    pos = pos + 4;
                }
                System.arraycopy(docFreqBytes, 0, byte1, 0, docFreqBytes.length);
                System.arraycopy(byte2, 0, byte1, 4, byte2.length);
                biwordPostingsFile.write(byte1, 0, byte1.length);
                vocabI++;
            }
            biwordVocabTable.close();
            biwordPostingsFile.close();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } finally {
            try {
                biwordPostingsFile.close();
            } catch (IOException ex) {
            }
        }
    }

    private static void buildVocabFile(String folder, String[] dictionary,
            long[] vocabPositions) {
        OutputStreamWriter vocabList = null;
        try {
            // first build the vocabulary list: a file of each vocab word concatenated together.
            // also build an array associating each term with its byte location in this file.
            int vocabI = 0;
            vocabList = new OutputStreamWriter(
                    new FileOutputStream(new File(folder, "vocab.bin")), "ASCII"
            );
            StringBuilder vocabString = new StringBuilder();
            int vocabPos = 0;
            for (String vocabWord : dictionary) {
                // for each String in dictionary, save the byte position where that term will start in the vocab file.
                vocabPositions[vocabI] = vocabPos;
                vocabString.append(vocabWord);
                // then write the String
                vocabI++;
                vocabPos += vocabWord.length();
            }
            vocabList.write(vocabString.toString());
        } catch (FileNotFoundException ex) {
            System.out.println(ex.toString());
        } catch (UnsupportedEncodingException ex) {
            System.out.println(ex.toString());
        } catch (IOException ex) {
            System.out.println(ex.toString());
        } finally {
            try {
                vocabList.close();
            } catch (IOException ex) {
                System.out.println(ex.toString());
            }
        }
    }

    private static void buildBiwordVocabFile(String folder, String[] biwordDictionary, long[] biwordVocabPositions) {
        OutputStreamWriter biwordVocabList = null;
        try {
            // first build the vocabulary list: a file of each vocab word concatenated together.
            // also build an array associating each term with its byte location in this file.
            int vocabI = 0;
            biwordVocabList = new OutputStreamWriter(
                    new FileOutputStream(new File(folder, "biwordVocab.bin")), "ASCII"
            );
            StringBuilder vocabString = new StringBuilder();
            int vocabPos = 0;
            for (String vocabWord : biwordDictionary) {
                // for each String in dictionary, save the byte position where that term will start in the vocab file.
                biwordVocabPositions[vocabI] = vocabPos;
                vocabString.append(vocabWord);
                vocabI++;
                vocabPos += vocabWord.length();
            }
            biwordVocabList.write(vocabString.toString());
        } catch (FileNotFoundException ex) {
            System.out.println(ex.toString());
        } catch (UnsupportedEncodingException ex) {
            System.out.println(ex.toString());
        } catch (IOException ex) {
            System.out.println(ex.toString());
        } finally {
            try {
                biwordVocabList.close();
            } catch (IOException ex) {
                System.out.println(ex.toString());
            }
        }
    }

    private static void indexFiles(String folder, final PositionalInvertedIndex index, final BiwordIndex bIndex) {
        final Path currentWorkingPath = Paths.get(folder).toAbsolutePath();
        try {
            FileOutputStream docWeights = new FileOutputStream(
                    new File(folder, "docWeights.bin")
            );
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
                        indexFile(file.toFile(), index, bIndex, mDocumentID, docWeights);
                        mDocumentID++;
                        docId++;
                    }
                    return FileVisitResult.CONTINUE;
                }

                // don't throw exceptions if files are locked/other errors occur
                public FileVisitResult visitFileFailed(Path file,
                        IOException e) {

                    return FileVisitResult.CONTINUE;
                }

            });
            avgTokens /= docId;
            FileOutputStream docLengthA = new FileOutputStream(
                    new File(folder, "docLengthA.bin")
            );
            byte[] avgTokensByte = ByteBuffer.allocate(8)
                    .putDouble(avgTokens).array();
            docLengthA.write(avgTokensByte, 0, avgTokensByte.length);
            docLengthA.close();
            docWeights.close();

        } catch (IOException ex) {
            Logger.getLogger(IndexWriter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void indexFile(File fileName, PositionalInvertedIndex index,
            BiwordIndex bIndex, int documentID, FileOutputStream docWeights) {
        try {

            // TO-DO: finish this method for indexing a particular file.
            // Construct a SimpleTokenStream for the given File.
            // Read each token from the stream and add it to the index.           
            JSONParser jsonParser = new JSONParser();
            Object object = jsonParser.parse(new FileReader(fileName));
            JSONObject jsonObject = (JSONObject) object;
            SimpleTokenStream simpleTokenStream = new SimpleTokenStream(jsonObject.get("body").toString());
            //CAll Porter stemmer class before adding index
            HashMap<String, Integer> documentWeight = new HashMap<>();
            String hypenWord;
            String temp = new String();
            String[] stems;
            int position = 1;
            int count = 0;
            double ld;
            double avgtfd;
            //boolean checkNull=false;
            while (simpleTokenStream.hasNextToken()) {
                //checkNull=true;
                String term = simpleTokenStream.nextToken();
                if (term != null) {
                    //count++;
                    if (term.contains("-")) {
                        stems = QueryHandler.removehypen(term);
                        if (stems != null) {
                            for (String word : stems) {
                                if (word == null) {
                                    break;
                                } else {
                                    count++;
                                    String stemmed = PorterStemmer.processToken(word);
                                    index.addTerm(stemmed, documentID, position);
                                    if (documentWeight.containsKey(stemmed)) {
                                        documentWeight.put(stemmed, documentWeight.get(stemmed) + 1);
                                    } else {
                                        documentWeight.put(stemmed, 1);
                                    }
                                    position++;
                                }
                            }
                            hypenWord = PorterStemmer.processToken(term.replaceAll("-", ""));
                            temp = temp + hypenWord;
                            bIndex.addTerms(temp, documentID);
                            temp = hypenWord + " ";
                        }
                    } else {
                        count++;
                        String stemmed = PorterStemmer.processToken(term);
                        index.addTerm(stemmed, documentID, position);
                        position++;
                        if (documentWeight.containsKey(stemmed)) {
                            documentWeight.put(stemmed, documentWeight.get(stemmed) + 1);
                        } else {
                            documentWeight.put(stemmed, 1);
                        }

                        if (position == 2) {
                            temp = stemmed + " ";
                        } else {
                            temp = temp + stemmed;
                            bIndex.addTerms(temp, documentID);
                            temp = stemmed + " ";
                        }
                    }
                }
            }
            ld = calculateLd(documentWeight);
            byte[] byte1 = ByteBuffer.allocate(28).array();

            byte[] LdBytes = ByteBuffer.allocate(8)
                    .putDouble(ld).array();
            byte[] docLengthBytes = ByteBuffer.allocate(4)
                    .putInt(count).array();
            avgTokens += count;
            byte[] docByteSize = ByteBuffer.allocate(8)
                    .putDouble(Math.sqrt(fileName.length())).array();
            avgtfd = (double) count / documentWeight.keySet().size();
            byte[] avgtfdByteSize = ByteBuffer.allocate(8)
                    .putDouble(avgtfd).array();
            System.arraycopy(LdBytes, 0, byte1, 0, LdBytes.length);
            System.arraycopy(docLengthBytes, 0, byte1, 8, docLengthBytes.length);
            System.arraycopy(docByteSize, 0, byte1, 12, docByteSize.length);
            System.arraycopy(avgtfdByteSize, 0, byte1, 20, avgtfdByteSize.length);
            docWeights.write(byte1, 0, byte1.length);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SimpleEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(IndexWriter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(IndexWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static double calculateLd(HashMap<String, Integer> documentWeight) {
        double ld = 0;
        if (documentWeight.isEmpty()) {
            return 0;
        } else {
            for (String term : documentWeight.keySet()) {
                ld = ld + calculateWdt(documentWeight.get(term));
            }
            return Math.sqrt(ld);
        }
    }

    private static double calculateWdt(Integer tftd) {
        double a = java.lang.Math.log(tftd);
        double wdt = 1 + a;
        return wdt * wdt;
    }

    private static byte[] addWdtValuesInPostingsFile(String folder, int tftd, int documentId, RandomAccessFile docWeights, double avgDocLength) throws IOException {
        byte[] wdtBytes = ByteBuffer.allocate(32).array();
        docWeights.seek(0);
        docWeights.skipBytes(documentId * 28);
        docWeights.skipBytes(8);
        byte[] docLengthByte = new byte[4];
        docWeights.read(docLengthByte, 0, docLengthByte.length);
        docWeights.skipBytes(8);
        byte[] avgTftdByte = new byte[8];
        docWeights.read(avgTftdByte, 0, avgTftdByte.length);
        int docLength = ByteBuffer.wrap(docLengthByte).getInt();
        double avgTftd = ByteBuffer.wrap(avgTftdByte).getDouble();
        double preComputedAvgDocLength = (1 / avgDocLength);
        double a = docLength * preComputedAvgDocLength;
        a = 0.75 * a;
        a = 0.25 + a;
        double kd = 1.2 * a;
        a = java.lang.Math.log(tftd);
        double wdt1 = 1 + a;
        double wdt2 = tftd;
        a = kd + tftd;
        a = (1 / a);
        a = tftd * a;
        double wdt3 = 2.2 * a;
        a = java.lang.Math.log(avgTftd);
        a = 1 + a;
        a = (1 / a);
        double wdt4 = wdt1 * a;
        byte[] wdt1Bytes = ByteBuffer.allocate(8)
                .putDouble(wdt1).array();
        System.arraycopy(wdt1Bytes, 0, wdtBytes, 0, wdt1Bytes.length);
        wdt1Bytes = ByteBuffer.allocate(8)
                .putDouble(wdt2).array();
        System.arraycopy(wdt1Bytes, 0, wdtBytes, 8, wdt1Bytes.length);
        wdt1Bytes = ByteBuffer.allocate(8)
                .putDouble(wdt3).array();
        System.arraycopy(wdt1Bytes, 0, wdtBytes, 16, wdt1Bytes.length);
        wdt1Bytes = ByteBuffer.allocate(8)
                .putDouble(wdt4).array();
        System.arraycopy(wdt1Bytes, 0, wdtBytes, 24, wdt1Bytes.length);
        return wdtBytes;
    }
}
