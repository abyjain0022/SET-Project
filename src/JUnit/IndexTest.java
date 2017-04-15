/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JUnit;

import MileStone1.BiwordIndex;
import MileStone1.PositionalInvertedIndex;
import MileStone1.PositionalPosting;
import MileStone1.QueryHandler;
import MileStone1.SimpleEngine;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Abhishek
 */

public class IndexTest {

    SimpleEngine simpleEngine = new SimpleEngine();
    List<String> fileNames = new ArrayList<String>();
    PositionalInvertedIndex positionalInvertedIndex = new PositionalInvertedIndex();
    BiwordIndex biwordIndex = new BiwordIndex();
    QueryHandler queryHandler = new QueryHandler();
    ArrayList<PositionalPosting> positionalPostingList = new ArrayList<PositionalPosting>();
    ArrayList<PositionalPosting> positionalPostingListTest = new ArrayList<PositionalPosting>();
    PositionalPosting p;

    @Test
    public void testIndexFile() throws IOException {
        String actual;
        String expected;
        String currentWorkingPath = "C:\\Users\\darsh\\Downloads\\JUnitFiles";
        fileNames = simpleEngine.fileWalkThrough(Paths.get(currentWorkingPath), positionalInvertedIndex, biwordIndex);

        //Term present in all documents present in corpus where few documents has this term in two or more position        
        positionalPostingList.addAll(positionalInvertedIndex.getPostings(queryHandler.processQueryLiteral("the")));
        actual = buildString(positionalPostingList);

        p = new PositionalPosting(0, new ArrayList<Integer>(Arrays.asList(1, 6)));
        positionalPostingListTest.add(p);
        p = new PositionalPosting(1, new ArrayList<Integer>(Arrays.asList(10)));
        positionalPostingListTest.add(p);
        p = new PositionalPosting(2, new ArrayList<Integer>(Arrays.asList(1, 8)));
        positionalPostingListTest.add(p);
        p = new PositionalPosting(3, new ArrayList<Integer>(Arrays.asList(1, 6, 12)));
        positionalPostingListTest.add(p);
        p = new PositionalPosting(4, new ArrayList<Integer>(Arrays.asList(1, 4)));
        positionalPostingListTest.add(p);

        expected = buildString(positionalPostingListTest);

        assertEquals(actual, expected);

        positionalPostingList.addAll(positionalInvertedIndex.getPostings(queryHandler.processQueryLiteral("National")));
        actual = buildString(positionalPostingList);

        p = new PositionalPosting(0, new ArrayList<Integer>(Arrays.asList(7)));
        positionalPostingListTest.add(p);
        p = new PositionalPosting(1, new ArrayList<Integer>(Arrays.asList(11)));
        positionalPostingListTest.add(p);

        expected = buildString(positionalPostingListTest);

        assertEquals(actual, expected);

    }

    private String buildString(ArrayList<PositionalPosting> postingList) {
        StringBuilder postingListString = new StringBuilder();
        for (PositionalPosting obj : postingList) {
            postingListString.append(obj.toString());
        }
        return postingListString.toString();
    }
}
