package MileStone2;

import java.util.ArrayList;

/**
 *
 * @author Abhishek
 */
public interface VariantFormulasInterface {

    public double calculateWeightOfTermInQuery(int documentFrequency, int corpusSize);

    public double calculateWeightOfTermInDocument(int termFrequency, double averageTermFrequency, double documentWeight, double averageDocumentWeight);

    public double calculateDocumentWeight(ArrayList<Double> wdtVector, double documentByteSize);

}
