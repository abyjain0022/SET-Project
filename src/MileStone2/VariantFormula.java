package MileStone2;

import java.util.ArrayList;

/**
 *
 * @author Abhishek
 */
public class VariantFormula {

    VariantFormulasInterface variantFormulasInterface;

    VariantFormula(VariantFormulasInterface variantFormulasInterface) {
        this.variantFormulasInterface = variantFormulasInterface;
    }

    public double callCalculateWeightOfTermInQuery(int documentFrequency, int corpusSize) {
        return variantFormulasInterface.calculateWeightOfTermInQuery(documentFrequency, corpusSize);

    }

    public double callCalculateWeightOfTermInDocument(int termFrequency, double averageTermFrequency, double documentWeight, double averageDocumentWeight) {
        return variantFormulasInterface.calculateWeightOfTermInDocument(termFrequency, averageTermFrequency, documentWeight, averageDocumentWeight);
    }

    public double callcalculateDocumentWeight(ArrayList<Double> wdtVector, double documentByteSize) {
        return variantFormulasInterface.calculateDocumentWeight(wdtVector, documentByteSize);
    }
}
