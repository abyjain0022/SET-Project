/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MileStone3;

import MileStone2.RankedRetrieval;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import javafx.scene.chart.XYChart;
import javafxapplication1.JavaFXApplication1;

/**
 *
 * @author Abhishek
 */
public class PlotPrecisionRecallGraph {
    private ArrayList<String> queryList = new ArrayList<String>();
    private HashMap<Integer,ArrayList<Coordinates>> coordinates;
    private HashMap<Integer, ArrayList<Integer>> relevanceList = new HashMap<Integer, ArrayList<Integer>>();
    private ArrayList<Long> timeToSatisfyQuery = new ArrayList<Long>();
    
    public void readQueryFile() throws FileNotFoundException, IOException{
        String line;
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("C:\\Users\\Abhishek\\Desktop\\SET\\Project\\MileStone 3 docs\\Milestone3_Relevance\\queries.txt")));
        while((line = bufferedReader.readLine())!= null){
           queryList.add(line.trim());        
        }
    }
    
    public void readRelevanceFile() throws FileNotFoundException, IOException{
        String line;
        ArrayList<Integer> list;    
        String[] l1;
        int i=1;
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("C:\\Users\\Abhishek\\Desktop\\SET\\Project\\MileStone 3 docs\\Milestone3_Relevance\\relevance.txt")));
        while((line = bufferedReader.readLine())!= null){
            l1 = line.split(" ");
            list = new ArrayList<Integer>();
            for(String docId : l1){
                list.add(Integer.parseInt(docId));
            }
           relevanceList.put(i, list);
           //list.clear();
           i++;
        }
    }
    
    private ArrayList<ArrayList<Integer>> retrieveSearchResults(String query){
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
        RankedRetrieval rankedRetrieval = new RankedRetrieval(JavaFXApplication1.currentWorkingPath.toString());
        int i=1; 
        while(i<=4){
            long startTime = System.currentTimeMillis();
            PriorityQueue<Map.Entry<Integer, Double>> topKResult = rankedRetrieval.calculateScore(query,i);            
            result.add(retrieveTop20Result(topKResult)); 
            long endTime = System.currentTimeMillis();
            if(timeToSatisfyQuery.size()<4)
                timeToSatisfyQuery.add(i-1, endTime-startTime);
            else
                timeToSatisfyQuery.set(i-1, timeToSatisfyQuery.get(i-1)+(endTime-startTime));
            i++;            
        }
           
        return result;
    }
    
    private ArrayList<Integer> retrieveTop20Result(PriorityQueue<Map.Entry<Integer, Double>> topKResult){
        ArrayList<Integer> top20Result = new ArrayList<Integer>();
        if (topKResult != null && !topKResult.isEmpty()) {                   
            int i = 0;
            while (!topKResult.isEmpty() && i < 20) {
                top20Result.add(topKResult.poll().getKey());
                i++;
            }
        }
        Collections.sort(top20Result);
        //System.out.println("top20Result "+top20Result);        
        return top20Result;
    }
    
    public ArrayList<XYChart.Series> plotAverageQueryTime(){
        XYChart.Series series;
        ArrayList<XYChart.Series> seriesList = new ArrayList<XYChart.Series>();
        for(int i=0;i<4;i++){          
          series = new XYChart.Series();
          switch(i){
              case 0 : series.setName("Default formula");
                       break;
              case 1 : series.setName("TF-IDF Fornula");
                       break;
              case 2 : series.setName("Okapi formula");
                       break;
              case 3 : series.setName("Wacky formula");
                       break;        
          }
         //System.out.println(" (long)((timeToSatisfyQuery.get(j))/queryList.size()) "+(long)((timeToSatisfyQuery.get(i))/queryList.size()));
         series.getData().add(new XYChart.Data(series.getName(),(long)((timeToSatisfyQuery.get(i))/queryList.size())));
         seriesList.add(series);
        }  
        return seriesList;    
    }
    
    public ArrayList<XYChart.Series> plotGraph(String query) throws IOException{
        readQueryFile();
        readRelevanceFile(); 
        int key = queryList.indexOf(query)+1;
        ArrayList<Integer> relevantDocList = relevanceList.get(key);
        //System.out.println(" relevantDocList "+relevantDocList);
        calculateCoordinates(relevantDocList,relevantDocList.size(),retrieveSearchResults(query));
        ArrayList<XYChart.Series> seriesList = plotGraphs(coordinates);
        return seriesList;
    }
    
    private void calculateCoordinates(ArrayList<Integer> relevantDocList,int numberOfRelevantDocumentsInCorpus,ArrayList<ArrayList<Integer>> searchResults){
        coordinates = new HashMap<Integer,ArrayList<Coordinates>>();
        ArrayList<Coordinates> c1;     
        for(int i = 0;i<searchResults.size();i++){
            double precision =0;
            double recall =0;   
            int numberOfRelevantDocumentReturned=0;
            ArrayList<Integer> top20Result = searchResults.get(i);
            int numberOfDocumentsReturned = 0;
            c1 = new ArrayList<Coordinates>();
            for(int j =0;j<top20Result.size();j++){
                numberOfDocumentsReturned++;                
                if(relevantDocList.contains(top20Result.get(j))){
                    numberOfRelevantDocumentReturned++;                    
                }
                precision = (double)numberOfRelevantDocumentReturned/numberOfDocumentsReturned;                
                recall = (double)numberOfRelevantDocumentReturned/numberOfRelevantDocumentsInCorpus;                
                c1.add(new Coordinates(precision, recall));
            }            
            coordinates.put(i, c1);
        }
    }
    
    private ArrayList<XYChart.Series> plotGraphs(HashMap<Integer,ArrayList<Coordinates>> coordinates){
        /*final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Recall");
        yAxis.setLabel("Precision");
        final LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);
        lineChart.setTitle("P-R Graph comparison for variant formulas");*/
        XYChart.Series series;
        ArrayList<XYChart.Series> seriesList = new ArrayList<XYChart.Series>();
        for(int i=0;i<4;i++){
          
          series = new XYChart.Series();
          switch(i){
              case 0 : series.setName("Default formula");
                       break;
              case 1 : series.setName("TF-IDF Fornula");
                       break;
              case 2 : series.setName("Okapi formula");
                       break;
              case 3 : series.setName("Wacky formula");
                       break;        
          }
          //System.err.println(" Formula change");
          for(int j=0;j<coordinates.get(i).size();j++){
            // System.out.println("Precision :- "+coordinates.get(i).get(j).getPrecision()+"   Recall :- "+coordinates.get(i).get(j).getRecall());
             series.getData().add(new XYChart.Data(coordinates.get(i).get(j).getRecall(),coordinates.get(i).get(j).getPrecision()));
       
          }
          seriesList.add(series);
          //lineChart.getData().add(series); 
        }  
        return seriesList;
    }
    
    public ArrayList<XYChart.Series> plotMAPGraph() throws IOException{
        readQueryFile();
        readRelevanceFile(); 
        ArrayList<XYChart.Series> seriesList = plotMAPGraphs();
        for(int i=0;i<timeToSatisfyQuery.size();i++)
            System.out.println(" Time for "+i+"  "+timeToSatisfyQuery.get(i));
        return seriesList;
    }
    
    private ArrayList<XYChart.Series> plotMAPGraphs(){
        ArrayList<Double> meanAveragePrecisionForVariantFormulas = calculateMAP();
        XYChart.Series series;
        ArrayList<XYChart.Series> seriesList = new ArrayList<XYChart.Series>();   
        for(int j=0;j<meanAveragePrecisionForVariantFormulas.size();j++){
            series = new XYChart.Series();
          
            switch(j){
              case 0 : series.setName("Default formula");
                       break;
              case 1 : series.setName("TF-IDF Fornula");
                       break;
              case 2 : series.setName("Okapi formula");
                       break;
              case 3 : series.setName("Wacky formula");
                       break;        
            }
            //System.err.println("meanAveragePrecisionForVariantFormulas.get(j) "+meanAveragePrecisionForVariantFormulas.get(j));
            series.getData().add(new XYChart.Data(series.getName(),meanAveragePrecisionForVariantFormulas.get(j)));   
            seriesList.add(series); 
          }      
        return seriesList;
    }
    
    private ArrayList<Double> calculateMAP(){
        ArrayList<Double> averagePrecisionForVariantFornulas = new ArrayList<Double>();
        ArrayList<Double> meanAveragePrecisionForVariantFormulas = new ArrayList<Double>();
        double meanAveragePrecisionDefaultFormula =0;
        double meanAveragePrecisionTfIDFFormula =0; 
        double meanAveragePrecisionOkapiFormula =0; 
        double meanAveragePrecisionWackyFormula =0; 
        
        for(int i=0;i<queryList.size();i++){
            averagePrecisionForVariantFornulas = calculatePrecision(relevanceList.get(i+1),relevanceList.get(i+1).size(),retrieveSearchResults(queryList.get(i)));
            //System.err.println(" relevant list "+relevanceList.get(i+1));
            System.err.println(" query :- "+i+" AP for Default :- "+averagePrecisionForVariantFornulas.get(0)+" AP for TF :- "+averagePrecisionForVariantFornulas.get(1)+" AP for Okapi :- "+averagePrecisionForVariantFornulas.get(2)+" AP for Wacky :- "+averagePrecisionForVariantFornulas.get(3));
            
            meanAveragePrecisionDefaultFormula = meanAveragePrecisionDefaultFormula + averagePrecisionForVariantFornulas.get(0) ;
            meanAveragePrecisionTfIDFFormula = meanAveragePrecisionTfIDFFormula + averagePrecisionForVariantFornulas.get(1) ;
            meanAveragePrecisionOkapiFormula = meanAveragePrecisionOkapiFormula + averagePrecisionForVariantFornulas.get(2) ;
            meanAveragePrecisionWackyFormula = meanAveragePrecisionWackyFormula + averagePrecisionForVariantFornulas.get(3) ;
        }
        meanAveragePrecisionForVariantFormulas.add((double)meanAveragePrecisionDefaultFormula/queryList.size());
        meanAveragePrecisionForVariantFormulas.add((double)meanAveragePrecisionTfIDFFormula/queryList.size());
        meanAveragePrecisionForVariantFormulas.add((double)meanAveragePrecisionOkapiFormula/queryList.size());
        meanAveragePrecisionForVariantFormulas.add((double)meanAveragePrecisionWackyFormula/queryList.size());
        
        return meanAveragePrecisionForVariantFormulas;
    }
    
    private ArrayList<Double> calculatePrecision(ArrayList<Integer> relevantDocList,int numberOfRelevantDocumentsInCorpus,ArrayList<ArrayList<Integer>> searchResults){
        ArrayList<Double> averagePrecisionForVariantFornulas = new ArrayList<Double>();
        //System.err.println(" Query Result obtained "+searchResults.toString());
        for(int i = 0;i<searchResults.size();i++){
            double precision =0;
            double averagePrecision =0;
            int numberOfRelevantDocumentReturned=0;
            ArrayList<Integer> top20Result = searchResults.get(i);
            int numberOfDocumentsReturned = 0;            
            for(int j =0;j<top20Result.size();j++){
                numberOfDocumentsReturned++;                
                if(relevantDocList.contains(top20Result.get(j))){
                    numberOfRelevantDocumentReturned++;   
                    precision = precision + (double)numberOfRelevantDocumentReturned/numberOfDocumentsReturned;     
                }
                               
                
            }   
             
            averagePrecision = numberOfRelevantDocumentReturned > 0 ? (double)precision/numberOfRelevantDocumentReturned  : 0;
           
            if(averagePrecisionForVariantFornulas.size()<4)
                averagePrecisionForVariantFornulas.add(i, averagePrecision);
            else
                averagePrecisionForVariantFornulas.set(i, averagePrecision);
            //averagePrecisionForVariantFornulas.add(averagePrecision);       
        }
        return averagePrecisionForVariantFornulas;
    }
}
