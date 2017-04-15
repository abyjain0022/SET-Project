package MileStone2;

import java.util.ArrayList;

/**
 *
 * @author Abhishek
 */
public class idfVariantFormula implements VariantFormulasInterface {

    double value;
    double weightOfTermInQuery;

    @Override
    public double calculateWeightOfTermInQuery(int documentFrequency, int corpusSize) {
        if (documentFrequency > 0) {
            value = (double) corpusSize / documentFrequency;
        }
        weightOfTermInQuery = Math.log(value);
        return weightOfTermInQuery;
    }

    @Override
    public double calculateWeightOfTermInDocument(int termFrequency, double averageTermFrequency, double documentWeight, double averageDocumentWeight) {
        return termFrequency;
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
