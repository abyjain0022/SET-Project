package MileStone2;

import MileStone1.PositionalPosting;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
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

public class DiskPositionalIndex {

    private String mPath;
    private RandomAccessFile mVocabList;
    private RandomAccessFile mPostings;
    private RandomAccessFile docWeights;
    private long[] mVocabTable;
    private List<String> mFileNames;

    public DiskPositionalIndex(String path) {
        try {
            mPath = path;
            mVocabList = new RandomAccessFile(new File(path, "vocab.bin"), "r");
            mPostings = new RandomAccessFile(new File(path, "postings.bin"), "r");
            mVocabTable = readVocabTable(path);
            mFileNames = readFileNames(path);
            docWeights = new RandomAccessFile(new File(path, "docWeights.bin"), "r");
        } catch (FileNotFoundException ex) {
            System.out.println(ex.toString());
        }
    }

    private ArrayList<PositionalPosting> readPositionalPostingsFromFile(RandomAccessFile postings,
            long postingsPosition) {
        try {
            // seek to the position in the file where the postings start.
            postings.seek(postingsPosition);

            // read the 4 bytes for the document frequency
            byte[] buffer = new byte[4];
            postings.read(buffer, 0, buffer.length);

            // use ByteBuffer to convert the 4 bytes into an int.
            int documentFrequency = ByteBuffer.wrap(buffer).getInt();

            // initialize the array that will hold the postings. 
            ArrayList<PositionalPosting> postingList = new ArrayList<PositionalPosting>();

            // write the following code:
            int lastDocId = 0, docId, termFrequency, lastTermPosition, position;
            for (int i = 0; i < documentFrequency; i++) {
                // read 4 bytes at a time from the file, until you have read as many
                //    postings as the document frequency promised.
                // 
                //byte[] bufferDocId=new byte[4];
                postings.read(buffer, 0, buffer.length);
                // after each read, convert the bytes to an int posting. this value
                //    is the GAP since the last posting. decode the document ID from
                //    the gap and put it in the array.
                //
                docId = ByteBuffer.wrap(buffer).getInt() + lastDocId;
                postings.skipBytes(32);
                postings.read(buffer, 0, buffer.length);
                termFrequency = ByteBuffer.wrap(buffer).getInt();
                ArrayList<Integer> positions = new ArrayList<Integer>(termFrequency);
                lastTermPosition = 0;
                for (int j = 0; j < termFrequency; j++) {
                    postings.read(buffer, 0, buffer.length);
                    position = ByteBuffer.wrap(buffer).getInt();
                    positions.add(position + lastTermPosition);
                    lastTermPosition += position;
                }
                PositionalPosting pp = new PositionalPosting(docId, positions);
                postingList.add(pp);

                lastDocId = docId;
            }

            return postingList;
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
        return null;
    }

    public ArrayList<PositionalPosting> GetPositionalPostings(String term) {
        long postingsPosition = binarySearchVocabulary(term);
        if (postingsPosition >= 0) {
            return readPositionalPostingsFromFile(mPostings, postingsPosition);
        }
        return null;
    }

    public ArrayList<Integer> GetPostings(String term) {
        long postingsPosition = binarySearchVocabulary(term);
        if (postingsPosition >= 0) {
            return readPostingsFromFile(mPostings, postingsPosition);
        }
        return null;
    }

    private long binarySearchVocabulary(String term) {
        // do a binary search over the vocabulary, using the vocabTable and the file vocabList.
        int i = 0, j = mVocabTable.length / 2 - 1;
        while (i <= j) {
            try {
                int m = (i + j) / 2;
                long vListPosition = mVocabTable[m * 2];
                int termLength;
                if (m == mVocabTable.length / 2 - 1) {
                    termLength = (int) (mVocabList.length() - mVocabTable[m * 2]);
                } else {
                    termLength = (int) (mVocabTable[(m + 1) * 2] - vListPosition);
                }

                mVocabList.seek(vListPosition);

                byte[] buffer = new byte[termLength];
                mVocabList.read(buffer, 0, termLength);
                String fileTerm = new String(buffer, "ASCII");

                int compareValue = term.compareTo(fileTerm);
                if (compareValue == 0) {
                    // found it!
                    return mVocabTable[m * 2 + 1];
                } else if (compareValue < 0) {
                    j = m - 1;
                } else {
                    i = m + 1;
                }
            } catch (IOException ex) {
                System.out.println(ex.toString());
            }
        }
        return -1;
    }

    private List<String> readFileNames(String indexName) {
        try {
            final List<String> names = new ArrayList<String>();
            final Path currentWorkingPath = Paths.get(indexName).toAbsolutePath();

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
                        names.add(file.toFile().getName());
                    }
                    return FileVisitResult.CONTINUE;
                }

                // don't throw exceptions if files are locked/other errors occur
                public FileVisitResult visitFileFailed(Path file,
                        IOException e) {

                    return FileVisitResult.CONTINUE;
                }

            });
            return names;
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
        return null;
    }

    private long[] readVocabTable(String indexName) {
        try {
            long[] vocabTable;

            RandomAccessFile tableFile = new RandomAccessFile(
                    new File(indexName, "vocabTable.bin"),
                    "r");

            byte[] byteBuffer = new byte[4];
            tableFile.read(byteBuffer, 0, byteBuffer.length);

            int tableIndex = 0;
            vocabTable = new long[ByteBuffer.wrap(byteBuffer).getInt() * 2];
            byteBuffer = new byte[8];

            while (tableFile.read(byteBuffer, 0, byteBuffer.length) > 0) { // while we keep reading 4 bytes
                vocabTable[tableIndex] = ByteBuffer.wrap(byteBuffer).getLong();
                tableIndex++;
            }
            tableFile.close();
            return vocabTable;
        } catch (FileNotFoundException ex) {
            System.out.println(ex.toString());
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
        return null;
    }

    public List<String> getFileNames() {
        return mFileNames;
    }

    public int getTermCount() {
        return mVocabTable.length / 2;
    }

    private ArrayList<Integer> readPostingsFromFile(RandomAccessFile postings, long postingsPosition) {
        try {
            // seek to the position in the file where the postings start.
            postings.seek(postingsPosition);

            // read the 4 bytes for the document frequency
            byte[] buffer = new byte[4];
            postings.read(buffer, 0, buffer.length);

            // use ByteBuffer to convert the 4 bytes into an int.
            int documentFrequency = ByteBuffer.wrap(buffer).getInt();

            // initialize the array that will hold the postings. 
            ArrayList<Integer> postingsList = new ArrayList<>(documentFrequency);

            // write the following code:
            int lastDocId = 0, docId, termFrequency;
            for (int i = 0; i < documentFrequency; i++) {
                // read 4 bytes at a time from the file, until you have read as many
                //    postings as the document frequency promised.
                // 
                //byte[] bufferDocId=new byte[4];
                postings.read(buffer, 0, buffer.length);
                // after each read, convert the bytes to an int posting. this value
                //    is the GAP since the last posting. decode the document ID from
                //    the gap and put it in the array.
                //
                docId = ByteBuffer.wrap(buffer).getInt() + lastDocId;
                postingsList.add(docId);
                postings.skipBytes(32);
                postings.read(buffer, 0, buffer.length);
                termFrequency = ByteBuffer.wrap(buffer).getInt();
                postings.skipBytes(termFrequency * 4);

                lastDocId = docId;
            }

            return postingsList;
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
        return null;
    }

    public void readValues(String term, int formulaMode, HashMap<Integer, Double> accumulator, int N) {
        long postingsPosition = binarySearchVocabulary(term);
        if (postingsPosition >= 0) {
            try {
                RandomAccessFile postings = mPostings;
                postings.seek(postingsPosition);
                byte[] buffer = new byte[4];
                postings.read(buffer, 0, buffer.length);
                int documentFrequency = ByteBuffer.wrap(buffer).getInt();
                VariantFormula variantFormula;
                double wqt = 0;
                double wdt = 0;
                byte[] wdtByte = new byte[8];

                int termFrequency;
                int lastDocId = 0;
                switch (formulaMode) {

                    case 1:
                        variantFormula = new VariantFormula(new DefaultVariantFormula());
                        wqt = variantFormula.callCalculateWeightOfTermInQuery(documentFrequency, N);
                        break;
                    case 2:
                        variantFormula = new VariantFormula(new idfVariantFormula());
                        wqt = variantFormula.callCalculateWeightOfTermInQuery(documentFrequency, N);
                        break;
                    case 3:
                        variantFormula = new VariantFormula(new OkapiVariantFormula());
                        wqt = variantFormula.callCalculateWeightOfTermInQuery(documentFrequency, N);
                        break;
                    case 4:
                        variantFormula = new VariantFormula(new WackyVariantFormula());
                        wqt = variantFormula.callCalculateWeightOfTermInQuery(documentFrequency, N);
                        break;
                }

                for (int i = 0; i < documentFrequency; i++) {
                    postings.read(buffer, 0, buffer.length);
                    int docId = ByteBuffer.wrap(buffer).getInt() + lastDocId;
                    postings.skipBytes((formulaMode - 1) * 8);
                    postings.read(wdtByte, 0, wdtByte.length);
                    wdt = ByteBuffer.wrap(wdtByte).getDouble();
                    double value = wqt * wdt;
                    if (accumulator.containsKey(docId)) {
                        accumulator.put(docId, accumulator.get(docId) + value);
                    } else {
                        accumulator.put(docId, value);
                    }
                    postings.skipBytes((4 - formulaMode) * 8);
                    postings.read(buffer, 0, buffer.length);
                    termFrequency = ByteBuffer.wrap(buffer).getInt();
                    postings.skipBytes(termFrequency * 4);
                    lastDocId = docId;
                }

            } catch (IOException ex) {
                Logger.getLogger(DiskPositionalIndex.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public void normalize(HashMap<Integer, Double> accumalator, int formulaMode) {
        RandomAccessFile docWeight = docWeights;
        Set<Integer> docIds = accumalator.keySet();
        byte[] ldByte = new byte[8];
        double ld = 0;
        for (int id : docIds) {
            try {
                docWeight.skipBytes(id * 28);
                docWeight.skipBytes((formulaMode - 3) * 12);
                docWeight.read(ldByte, 0, ldByte.length);
                ld = ByteBuffer.wrap(ldByte).getDouble();
                accumalator.put(id, accumalator.get(id) / ld);
                docWeight.seek(0);
            } catch (IOException ex) {
                Logger.getLogger(DiskPositionalIndex.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String[] getDictionary() {
        String[] dictionary = new String[getTermCount()];
        int i = 0;
        long vListPosition;
        int termLength;
        while (i < getTermCount()) {
            try {
                vListPosition = mVocabTable[i * 2];
                if (i == mVocabTable.length / 2 - 1) {
                    termLength = (int) (mVocabList.length() - mVocabTable[i * 2]);
                } else {
                    termLength = (int) (mVocabTable[(i + 1) * 2] - vListPosition);
                }
                mVocabList.seek(vListPosition);

                byte[] buffer = new byte[termLength];
                mVocabList.read(buffer, 0, termLength);
                dictionary[i] = new String(buffer, "ASCII");
                i++;
            } catch (IOException ex) {
                Logger.getLogger(DiskPositionalIndex.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return dictionary;
    }
}
