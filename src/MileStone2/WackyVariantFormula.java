package MileStone2;

import java.util.ArrayList;

/**
 *
 * @author Abhishek
 */
public class WackyVariantFormula implements VariantFormulasInterface {

    double weightOfTermInQuery;
    double weightOfTermInDocument;

    @Override
    public double calculateWeightOfTermInQuery(int documentFrequency, int corpusSize) {
        double a = corpusSize - documentFrequency;
        double b = a / documentFrequency;
        double value = Math.log(b);
        weightOfTermInQuery = Math.max(0, value);
        return weightOfTermInQuery;
    }

    @Override
    public double calculateWeightOfTermInDocument(int termFrequency, double averageTermFrequency, double documentWeight, double averageDocumentWeight) {
        weightOfTermInDocument = (1 + Math.log10(termFrequency)) / (1 + (Math.log10(averageTermFrequency)));
        return weightOfTermInDocument;
    }

    @Override
    public double calculateDocumentWeight(ArrayList<Double> wdtVector, double documentByteSize) {
        return Math.sqrt(documentByteSize);
    }

}
