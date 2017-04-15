package JUnit;

import org.junit.runner.Result;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.Failure;

/**
 *
 * @author Abhishek
 */
public class TestRunner {

    public static void main(String args[]) {
        Result porterStemResult = JUnitCore.runClasses(PorterStemmerTest.class);
        for (Failure failure : porterStemResult.getFailures()) {
            System.out.println(failure.toString());
        }
        Result indexResult = JUnitCore.runClasses(IndexTest.class);
        for (Failure failure : indexResult.getFailures()) {
            System.out.println(failure.toString());
        }
        Result queryHandlerResult = JUnitCore.runClasses(BooleanRetrievalTest.class);
        for (Failure queryResult : queryHandlerResult.getFailures()) {
            System.out.println(queryResult.toString());
        }
        Result rankRetrievalResult = JUnitCore.runClasses(RankRetrievalTest.class);
        for (Failure rankResult : rankRetrievalResult.getFailures()) {
            System.out.println(rankResult.toString());
        }
    }
}
