package MileStone2;

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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author darsh
 */
public class DiskBiwordIndex {

    private String mPath;
    private RandomAccessFile mBiwordVocabList;
    private RandomAccessFile mBiwordPostings;
    private long[] mBiwordVocabTable;
    private List<String> mFileNames;

    public DiskBiwordIndex(String path) {
        try {
            mPath = path;
            mBiwordVocabList = new RandomAccessFile(new File(path, "biwordVocab.bin"), "r");
            mBiwordPostings = new RandomAccessFile(new File(path, "biwordPostings.bin"), "r");
            mBiwordVocabTable = readVocabTable(path);
            mFileNames = readFileNames(path);
        } catch (FileNotFoundException ex) {
            System.out.println(ex.toString());
        }
    }

    public ArrayList<Integer> GetPostings(String term) {
        long postingsPosition = binarySearchVocabulary(term);
        if (postingsPosition >= 0) {
            return readPostingsFromFile(mBiwordPostings, postingsPosition);
        }
        return null;
    }

    private long binarySearchVocabulary(String term) {
        // do a binary search over the vocabulary, using the vocabTable and the file vocabList.
        int i = 0, j = mBiwordVocabTable.length / 2 - 1;
        while (i <= j) {
            try {
                int m = (i + j) / 2;
                long vListPosition = mBiwordVocabTable[m * 2];
                int termLength;
                if (m == mBiwordVocabTable.length / 2 - 1) {
                    termLength = (int) (mBiwordVocabList.length() - mBiwordVocabTable[m * 2]);
                } else {
                    termLength = (int) (mBiwordVocabTable[(m + 1) * 2] - vListPosition);
                }

                mBiwordVocabList.seek(vListPosition);

                byte[] buffer = new byte[termLength];
                mBiwordVocabList.read(buffer, 0, termLength);
                String fileTerm = new String(buffer, "ASCII");

                int compareValue = term.compareTo(fileTerm);
                if (compareValue == 0) {
                    // found it!
                    return mBiwordVocabTable[m * 2 + 1];
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

    private static List<String> readFileNames(String indexName) {
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

    private static long[] readVocabTable(String indexName) {
        try {
            long[] vocabTable;

            RandomAccessFile biwordTableFile = new RandomAccessFile(
                    new File(indexName, "biwordVocabTable.bin"),
                    "r");

            byte[] byteBuffer = new byte[4];
            biwordTableFile.read(byteBuffer, 0, byteBuffer.length);

            int tableIndex = 0;
            vocabTable = new long[ByteBuffer.wrap(byteBuffer).getInt() * 2];
            byteBuffer = new byte[8];

            while (biwordTableFile.read(byteBuffer, 0, byteBuffer.length) > 0) { // while we keep reading 4 bytes
                vocabTable[tableIndex] = ByteBuffer.wrap(byteBuffer).getLong();
                tableIndex++;
            }
            biwordTableFile.close();
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
        return mBiwordVocabTable.length / 2;
    }

    private ArrayList<Integer> readPostingsFromFile(RandomAccessFile biwordPostings, long postingsPosition) {
        try {
            // seek to the position in the file where the postings start.
            biwordPostings.seek(postingsPosition);

            // read the 4 bytes for the document frequency
            byte[] buffer = new byte[4];
            biwordPostings.read(buffer, 0, buffer.length);

            // use ByteBuffer to convert the 4 bytes into an int.
            int documentFrequency = ByteBuffer.wrap(buffer).getInt();

            // initialize the array that will hold the postings. 
            ArrayList<Integer> biwordPostingsList = new ArrayList<>(documentFrequency);

            // write the following code:
            int lastDocId = 0, docId;
            for (int i = 0; i < documentFrequency; i++) {
                // read 4 bytes at a time from the file, until you have read as many
                //    postings as the document frequency promised.
                biwordPostings.read(buffer, 0, buffer.length);
                // after each read, convert the bytes to an int posting. this value
                //    is the GAP since the last posting. decode the document ID from
                //    the gap and put it in the array.
                docId = ByteBuffer.wrap(buffer).getInt() + lastDocId;
                biwordPostingsList.add(docId);
                lastDocId = docId;
            }

            return biwordPostingsList;
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
        return null;
    }
}
