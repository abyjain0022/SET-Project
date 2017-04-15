package JUnit;

import MileStone2.RankedRetrieval;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import javafxapplication1.JavaFXApplication1;
import junit.framework.Assert;
import static junit.framework.Assert.assertEquals;
import org.junit.Test;
/**
 *
 * @author Abhishek
 */
public class RankRetrievalTest {
    RankedRetrieval rankedRetrieval =new RankedRetrieval("C:\\Users\\darsh\\Downloads\\JUnitFiles");  
    PriorityQueue<Map.Entry<Integer,Double>> topK = null;
    String searchQuery;
    HashMap<Integer,Double> actualResult = new HashMap<Integer,Double>();
    HashMap<Integer,Double> expectedResult = new HashMap<Integer,Double>();
    
    @Test
    public void rankQueryTest(){    
       
        searchQuery = "the";
        
        // "the" for default formula
        topK= rankedRetrieval.calculateScore(searchQuery,1);
        int i=0;
        while (!topK.isEmpty()&&i<10) {    
            actualResult.put(topK.peek().getKey(), topK.poll().getValue());           
            i++;
        }           
        expectedResult.put(3, 0.3348561959722996);
        expectedResult.put(0, 0.32717927501779415);
        expectedResult.put(2, 0.2540894924059342);
        expectedResult.put(4, 0.25285919817957303);
        expectedResult.put(1, 0.1861393355175344);        
        assertEquals(actualResult.toString(),expectedResult.toString());

        actualResult.clear();
        expectedResult.clear();
        
        // "the" for tf-idf formula
        topK= rankedRetrieval.calculateScore(searchQuery,2);
        i=0;
        while (!topK.isEmpty()&&i<10) {    
            actualResult.put(topK.peek().getKey(), topK.poll().getValue());           
            i++;
        }           
        expectedResult.put(0, 0.0);
        expectedResult.put(4, 0.0);
        expectedResult.put(3, 0.0);
        expectedResult.put(2, 0.0);
        expectedResult.put(1, 0.0);        
        assertEquals(actualResult.toString(),expectedResult.toString());
        actualResult.clear();
        expectedResult.clear();
        
        // "the" for okapi formula
        topK= rankedRetrieval.calculateScore(searchQuery,3);
        i=0;
        while (!topK.isEmpty()&&i<10) {    
            actualResult.put(topK.peek().getKey(), topK.poll().getValue());           
            i++;
        }           
        expectedResult.put(3, 0.1585389930898322);
        expectedResult.put(0, 0.14474988733663816);
        expectedResult.put(4, 0.13142389525368248);
        expectedResult.put(2, 0.13142389525368248);
        expectedResult.put(1, 0.1046936114732725);       
        assertEquals(actualResult.toString(),expectedResult.toString());
        actualResult.clear();
        expectedResult.clear();
        
        // "the" for wacky formula
        topK= rankedRetrieval.calculateScore(searchQuery,4);
        i=0;
        while (!topK.isEmpty()&&i<10) {    
            actualResult.put(topK.peek().getKey(), topK.poll().getValue());           
            i++;
        }           
        expectedResult.put(0, 0.0);
        expectedResult.put(4, 0.0);
        expectedResult.put(3, 0.0);
        expectedResult.put(2, 0.0);
        expectedResult.put(1, 0.0);        
        assertEquals(actualResult.toString(),expectedResult.toString());
        actualResult.clear();
        expectedResult.clear();
        
        searchQuery = "national park";        
        // "National Park" for wacky formula
        topK= rankedRetrieval.calculateScore(searchQuery,4);
        i=0;
        while (!topK.isEmpty()&&i<10) {    
            actualResult.put(topK.peek().getKey(), topK.poll().getValue());           
            i++;
        }           
        expectedResult.put(0, 0.0511163213533439);
        expectedResult.put(1, 0.048771754729261166);        
        assertEquals(actualResult.toString(),expectedResult.toString());
        actualResult.clear();
        expectedResult.clear();
        
        // "National Park" for okapi formula
        topK= rankedRetrieval.calculateScore(searchQuery,3);
        i=0;
        while (!topK.isEmpty()&&i<10) {    
            actualResult.put(topK.peek().getKey(), topK.poll().getValue());           
            i++;
        }       
        expectedResult.put(0, 0.7258219100250745);
        expectedResult.put(1, 0.7045298722472855);        
        assertEquals(actualResult.toString(),expectedResult.toString());
        
        // "National Park" for td-idf formula
        topK= rankedRetrieval.calculateScore(searchQuery,2);
        i=0;
        while (!topK.isEmpty()&&i<10) {    
            actualResult.put(topK.peek().getKey(), topK.poll().getValue());           
            i++;
        }           
        expectedResult.put(0, 0.5108917649650107);
        expectedResult.put(1, 0.49212563436858764);        
        assertEquals(actualResult.toString(),expectedResult.toString());
        actualResult.clear();
        expectedResult.clear();
        
        // "National Park" for default formula
        topK= rankedRetrieval.calculateScore(searchQuery,1);
        i=0;
        while (!topK.isEmpty()&&i<10) {    
            actualResult.put(topK.peek().getKey(), topK.poll().getValue());           
            i++;
        }       
        expectedResult.put(0, 0.6984969527611755);
        expectedResult.put(1, 0.6728396884722952);        
        assertEquals(actualResult.toString(),expectedResult.toString());
        
    }
}
