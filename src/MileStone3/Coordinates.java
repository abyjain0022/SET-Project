/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MileStone3;

/**
 *
 * @author Abhishek
 */
public class Coordinates {
    private double precision;
    private double recall;

    public Coordinates(double precision, double recall) {
        this.precision = precision;
        this.recall = recall;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public double getRecall() {
        return recall;
    }

    public void setRecall(double recall) {
        this.recall = recall;
    }
    
    
}
