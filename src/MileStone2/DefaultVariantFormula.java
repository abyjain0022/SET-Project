package MileStone2;

import java.util.ArrayList;

/**
 *
 * @author Abhishek
 */
public class DefaultVariantFormula implements VariantFormulasInterface {

    double weightOfTermInQuery;
    double weightOfTermInDocument;

    @Override
    public double calculateWeightOfTermInQuery(int documentFrequency, int corpusSize) {
        double value = (double) corpusSize / documentFrequency;
        weightOfTermInQuery = Math.log(1 + value);
        return weightOfTermInQuery;
    }

    @Override
    public double calculateWeightOfTermInDocument(int termFrequency, double averageTermFrequency, double documentWeight, double averageDocumentWeight) {
        weightOfTermInDocument = 1 + Math.log10(termFrequency);
        return weightOfTermInDocument;
    }

    @Override
    public double calculateDocumentWeight(ArrayList<Double> wdtVector, double documentByteSize) {
        double weightOfDocument;
        double sumOfWDTSquares = 0;
        for (double i : wdtVector) {
            sumOfWDTSquares = sumOfWDTSquares + Math.pow(i, 2);
        }
        weightOfDocument = Math.sqrt(sumOfWDTSquares);
        return weightOfDocument;
    }
}
