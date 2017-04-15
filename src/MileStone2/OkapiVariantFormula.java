package MileStone2;

import java.util.ArrayList;

/**
 *
 * @author Abhishek
 */
public class OkapiVariantFormula implements VariantFormulasInterface {

    double weightOfTermInQuery;
    double weightOfTermInDocument;

    @Override
    public double calculateWeightOfTermInQuery(int documentFrequency, int corpusSize) {
        double numerator = corpusSize - documentFrequency + 0.5;
        double denominator = documentFrequency + 0.5;
        double value = numerator / denominator;
        if (Math.log(value) > 0.1) {
            weightOfTermInQuery = Math.log(value);
        } else {
            weightOfTermInQuery = 0.1;
        }
        return weightOfTermInQuery;
    }

    @Override
    public double calculateWeightOfTermInDocument(int termFrequency, double averageTermFrequency, double documentWeight, double averageDocumentWeight) {
        double kd = 0.25 + 0.75 * (documentWeight / averageDocumentWeight);
        weightOfTermInDocument = (2.2 * termFrequency) / (kd + termFrequency);
        return weightOfTermInDocument;
    }

    @Override
    public double calculateDocumentWeight(ArrayList<Double> wdtVector, double documentByteSize) {
        return 1;
    }
}
