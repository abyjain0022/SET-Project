package JUnit;

import MileStone1.PorterStemmer;
import static junit.framework.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Abhishek
 */

public class PorterStemmerTest {

    /**
     * Test of processToken method, of class PorterStemmer.
     */
    @Test
    public void testProcessToken() {

        assertEquals(PorterStemmer.processToken("replicate"), "replic");
        assertEquals(PorterStemmer.processToken("rational"), "ration");
        assertEquals(PorterStemmer.processToken("organization"), "organ");
        assertEquals(PorterStemmer.processToken("organize"), "organ");
        assertEquals(PorterStemmer.processToken("organizer"), "organ");
        assertEquals(PorterStemmer.processToken("really"), "realli");
        assertEquals(PorterStemmer.processToken("reed"), "reed");
        assertEquals(PorterStemmer.processToken("red"), "red");
        assertEquals(PorterStemmer.processToken("argument"), "argument");
        assertEquals(PorterStemmer.processToken("operate"), "oper");
        assertEquals(PorterStemmer.processToken("operating"), "oper");
        assertEquals(PorterStemmer.processToken("operates"), "oper");
        assertEquals(PorterStemmer.processToken("operation"), "oper");
        assertEquals(PorterStemmer.processToken("operative"), "oper");
        assertEquals(PorterStemmer.processToken("operatives"), "oper");
        assertEquals(PorterStemmer.processToken("circus"), "circu");
        assertEquals(PorterStemmer.processToken("canaries"), "canari");
        assertEquals(PorterStemmer.processToken("boss"), "boss");
        assertEquals(PorterStemmer.processToken("administration"), "administr");
        assertEquals(PorterStemmer.processToken("significance"), "signific");
        assertEquals(PorterStemmer.processToken("developed"), "develop");
        assertEquals(PorterStemmer.processToken("carports"), "carport");
        assertEquals(PorterStemmer.processToken("building"), "build");
        assertEquals(PorterStemmer.processToken("development"), "develop");
        assertEquals(PorterStemmer.processToken("maintenance"), "mainten");
        assertEquals(PorterStemmer.processToken("vegetation"), "veget");
        assertEquals(PorterStemmer.processToken("architectural"), "architectur");
        assertEquals(PorterStemmer.processToken("veneers"), "veneer");
        assertEquals(PorterStemmer.processToken("stromatolites"), "stromatolit");
        assertEquals(PorterStemmer.processToken("significant"), "signific");
        assertEquals(PorterStemmer.processToken("preventing"), "prevent");
        assertEquals(PorterStemmer.processToken("ability"), "abil");
        assertEquals(PorterStemmer.processToken("pollutants"), "pollut");
        assertEquals(PorterStemmer.processToken("tensile"), "tensil");
        assertEquals(PorterStemmer.processToken("sheaths"), "sheath");
        assertEquals(PorterStemmer.processToken("binding"), "bind");
        assertEquals(PorterStemmer.processToken("interpretive"), "interpret");
    }

}
