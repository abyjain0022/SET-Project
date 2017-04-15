/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JUnit;

import MileStone1.QueryHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import static junit.framework.Assert.assertEquals;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Abhishek
 */
public class BooleanRetrievalTest {

    QueryHandler queryHandler = new QueryHandler();
    String searchQuery;
    String path = "C:\\Users\\darsh\\Downloads\\JUnitFiles";
    ArrayList<Integer> expectedResult = new ArrayList<Integer>();
    ArrayList<Integer> searchResult = new ArrayList<Integer>();

    @Test
    public void searchQueryTest() throws IOException {

        //Test for and query
        searchQuery = "National Park Service";
        searchResult = queryHandler.searchQuery(searchQuery, path);
        expectedResult.addAll(new ArrayList<Integer>(Arrays.asList(0)));
        assertEquals(searchResult.toString(), expectedResult.toString());
        expectedResult.clear();

        //Test for and query
        searchQuery = "National Park";
        searchResult = queryHandler.searchQuery(searchQuery, path);
        expectedResult.addAll(new ArrayList<Integer>(Arrays.asList(0, 1)));
        assertEquals(searchResult.toString(), expectedResult.toString());
        expectedResult.clear();

        //Test for or query 
        searchQuery = "National + Park ";
        searchResult = queryHandler.searchQuery(searchQuery, path);
        expectedResult.addAll(new ArrayList<Integer>(Arrays.asList(0, 1)));
        assertEquals(searchResult.toString(), expectedResult.toString());
        expectedResult.clear();

        //Test for or query 
        searchQuery = "House + Soldiers ";
        searchResult = queryHandler.searchQuery(searchQuery, path);
        expectedResult.addAll(new ArrayList<Integer>(Arrays.asList(2, 4)));
        assertEquals(searchResult.toString(), expectedResult.toString());
        expectedResult.clear();

        //Test for phrase query 
        searchQuery = "The West India Goods";
        searchResult = queryHandler.searchQuery(searchQuery, path);
        expectedResult.addAll(new ArrayList<Integer>(Arrays.asList(3)));
        assertEquals(searchResult.toString(), expectedResult.toString());
        expectedResult.clear();

        //Test for phrase query 
        searchQuery = "National Park Service";
        searchResult = queryHandler.searchQuery(searchQuery, path);
        expectedResult.addAll(new ArrayList<Integer>(Arrays.asList(0)));
        assertEquals(searchResult.toString(), expectedResult.toString());
        expectedResult.clear();

        //Test for biword query 
        searchQuery = "National Park";
        searchResult = queryHandler.searchQuery(searchQuery, path);
        expectedResult.addAll(new ArrayList<Integer>(Arrays.asList(0, 1)));
        assertEquals(searchResult.toString(), expectedResult.toString());
        expectedResult.clear();

        //Test for biword query 
        searchQuery = "Goods Store";
        searchResult = queryHandler.searchQuery(searchQuery, path);
        expectedResult.addAll(new ArrayList<Integer>(Arrays.asList(3)));
        assertEquals(searchResult.toString(), expectedResult.toString());
        expectedResult.clear();

        //Test for complex query 
        searchQuery = "National Park + \"Good Store\"";
        searchResult = queryHandler.searchQuery(searchQuery, path);
        expectedResult.addAll(new ArrayList<Integer>(Arrays.asList(0, 1, 3)));
        assertEquals(searchResult.toString(), expectedResult.toString());
        expectedResult.clear();

        //Test for complex query  
        searchQuery = "Goods Store + \" The Custom House \"";
        searchResult = queryHandler.searchQuery(searchQuery, path);
        expectedResult.addAll(new ArrayList<Integer>(Arrays.asList(3, 4)));
        assertEquals(searchResult.toString(), expectedResult.toString());
        expectedResult.clear();

        //Test for near queries
        searchQuery = "national near/5 park";
        searchResult = queryHandler.searchQuery(searchQuery, path);
        expectedResult.addAll(new ArrayList<Integer>(Arrays.asList(0, 1)));
        assertEquals(searchResult.toString(), expectedResult.toString());
        expectedResult.clear();

        searchQuery = "Salem near/5 dock ";
        searchResult = queryHandler.searchQuery(searchQuery, path);
        expectedResult.addAll(new ArrayList<Integer>(Arrays.asList(1)));
        assertEquals(searchResult.toString(), expectedResult.toString());
        expectedResult.clear();

        searchQuery = "the near/3 house";
        searchResult = queryHandler.searchQuery(searchQuery, path);
        expectedResult.addAll(new ArrayList<Integer>(Arrays.asList(4)));
        assertEquals(searchResult.toString(), expectedResult.toString());
        expectedResult.clear();

        //Test for no result found
        searchQuery = "Search";
        searchResult = queryHandler.searchQuery(searchQuery, path);
        Assert.assertEquals(searchResult.toString(), expectedResult.toString());
        expectedResult.clear();

        //Test for term present in all documents
        searchQuery = "The";
        searchResult = queryHandler.searchQuery(searchQuery, path);
        expectedResult.addAll(new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4)));
        assertEquals(searchResult.toString(), expectedResult.toString());
        expectedResult.clear();
    }
}
