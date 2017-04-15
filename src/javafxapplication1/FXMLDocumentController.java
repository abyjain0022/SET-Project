package javafxapplication1;

import MileStone1.PorterStemmer;
import MileStone1.QueryHandler;
import MileStone2.DiskPositionalIndex;
import MileStone2.IndexWriter;
import MileStone2.RankedRetrieval;
import MileStone3.PlotPrecisionRecallGraph;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.Stage;
import javax.swing.JFileChooser;

/**
 *
 * @author Abhishek
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private Button queryIndex;
    @FXML
    private Button buildIndex;
    @FXML
    private Button booleanRetrieval;
    @FXML
    private Button rankedRetrieval;
    @FXML
    private Button SearchButton;
    @FXML
    private Button StemButton;
    @FXML
    private Button VocabButton;
    @FXML
    private Button QuitButton;
    @FXML
    private TextField enterQuery;
    @FXML
    private TextArea FileDisplay;
    @FXML
    private Button browse;
    @FXML
    private Button defaultFormula;
    @FXML
    private Button idtfdFormula;
    @FXML
    private Button okapiFormula;
    @FXML
    private Button wackyFormula;
    @FXML
    private ListView ResultList;
    @FXML
    private Label itemFound;
    @FXML
    private Label searchTypeLabel;
    @FXML
    private Button HomePage;
    @FXML
    private Button plotGraph;   
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML 
    private LineChart<Number,Number> lineChart;
    @FXML
    private CategoryAxis xAxis1;
    @FXML
    private NumberAxis yAxis1;
    @FXML 
    private BarChart<String,Number> barChart;
    @FXML
    private Button backToSearchScreen;
    @FXML
    private Button plotMAPGraph;
    @FXML
    private CategoryAxis xAxis2;
    @FXML
    private NumberAxis yAxis2;
    @FXML 
    private BarChart<String,Number> barChart2;    

    Stage stage;
    Parent root;
    Scene scene;
    Path currentWorkingPath;
    QueryHandler queryHandler = new QueryHandler();
    List<String> fileNames = new ArrayList<String>();

    @FXML
    private void handleIndexMode(ActionEvent event) throws IOException {
        Button button = (Button) event.getSource();
        String buttonName = button.getId();
        JavaFXApplication1.indexMode = buttonName;
        if (buttonName.equalsIgnoreCase("queryIndex")) {
            stage = (Stage) queryIndex.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("BrowseDirectory.fxml"));
        } else {
            stage = (Stage) buildIndex.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("BrowseDirectory.fxml"));
        }
        scene = new Scene(root);
        stage.setScene(scene);
    }

    @FXML
    private void handleFormula(ActionEvent event) throws IOException {
        Button button = (Button) event.getSource();
        String buttonName = button.getId();
        if (buttonName.equalsIgnoreCase("defaultFormula")) {
            JavaFXApplication1.formulaMode = 1;
            stage = (Stage) defaultFormula.getScene().getWindow();
        }
        if (buttonName.equalsIgnoreCase("idtfdFormula")) {
            JavaFXApplication1.formulaMode = 2;
            stage = (Stage) idtfdFormula.getScene().getWindow();
        }
        if (buttonName.equalsIgnoreCase("okapiFormula")) {
            JavaFXApplication1.formulaMode = 3;
            stage = (Stage) okapiFormula.getScene().getWindow();
        }
        if (buttonName.equalsIgnoreCase("wackyFormula")) {
            JavaFXApplication1.formulaMode = 4;
            stage = (Stage) wackyFormula.getScene().getWindow();
        }

        root = FXMLLoader.load(getClass().getResource("SearchScreen.fxml"));
        scene = new Scene(root);
        stage.setScene(scene);
    }

    @FXML
    private void handleBrowse(ActionEvent event) throws IOException {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("C:\\Users\\Abhishek\\Desktop\\SET\\Assignment"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.showOpenDialog(chooser);
        currentWorkingPath = chooser.getSelectedFile().toPath().toAbsolutePath();
        JavaFXApplication1.currentWorkingPath = currentWorkingPath;

        if (JavaFXApplication1.indexMode.equalsIgnoreCase("buildIndex")) {
            IndexWriter writer = new IndexWriter(currentWorkingPath.toString());
            writer.buildIndex();
        }

        stage = (Stage) browse.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("QuerySelectionMode.fxml"));
        scene = new Scene(root);
        stage.setScene(scene);
    }

    @FXML
    private void handleQueryMode(ActionEvent event) throws IOException {
        Button button = (Button) event.getSource();
        String buttonName = button.getId();
        JavaFXApplication1.queryMode = buttonName;
        if (buttonName.equalsIgnoreCase("rankedRetrieval")) {
            stage = (Stage) rankedRetrieval.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("FormulaSelectionMode.fxml"));
        } else {
            stage = (Stage) booleanRetrieval.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("SearchScreen.fxml"));
        }
        scene = new Scene(root);
        stage.setScene(scene);
    }

    @FXML
    private void handleSearchButton(ActionEvent event) {
        FileDisplay.setText("");
        String searchQuery = enterQuery.getText().trim();
        ObservableList<String> result = FXCollections.observableArrayList();
        if (searchQuery.indexOf(":") == 0) {
            if (searchQuery.contains(":q") && searchQuery.indexOf("q") == 1) {
                System.exit(0);
            } else if (searchQuery.contains(":stem")) {
                StringBuilder stemedString = new StringBuilder();
                String[] tokens = searchQuery.substring(5, searchQuery.length()).split(" ");
                for (String token : tokens) {
                    stemedString.append(PorterStemmer.processToken(token));
                    stemedString.append(" ");
                }
                result.add(stemedString.toString());
                ResultList.setItems(result);
            } else if (searchQuery.contains(":index")) {
                // Do nothing
            } else if (searchQuery.contains(":vocab")) {
                //Fetch vocab 
            }
        } else {
            if (JavaFXApplication1.queryMode.equalsIgnoreCase("booleanRetrieval")) {
                ArrayList<Integer> searchResult = queryHandler.searchQuery(searchQuery, JavaFXApplication1.currentWorkingPath.toString());
                if (searchResult.size() > 0 && (searchResult != null)) {
                    itemFound.setText(searchResult.size() + " Files found");
                    for (int i : searchResult) {
                        result.add(JavaFXApplication1.fileNames.get(i));
                    }
                } else {
                    itemFound.setText("No file found");
                }

            } else if (JavaFXApplication1.queryMode.equalsIgnoreCase("rankedRetrieval")) {
                RankedRetrieval rankedRetrieval = new RankedRetrieval(JavaFXApplication1.currentWorkingPath.toString());
                PriorityQueue<Map.Entry<Integer, Double>> topK = rankedRetrieval.calculateScore(searchQuery, JavaFXApplication1.formulaMode);
                if (topK != null && !topK.isEmpty()) {
                    itemFound.setText(topK.size() + " Files found");
                    int i = 0;
                    while (!topK.isEmpty() && i < 20) {
                        result.add(JavaFXApplication1.fileNames.get(topK.peek().getKey()) + "\t" + topK.poll().getValue());
                        i++;
                    }
                } else {
                    itemFound.setText("No file found");
                }
            }
            ResultList.setItems(result);
        }

        ResultList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    FileDisplay.setVisible(true);
                    BufferedReader reader = null;
                    try {
                        String selectedFile = (String) ResultList.getSelectionModel().getSelectedItem();
                        String directory = JavaFXApplication1.currentWorkingPath + "\\" + selectedFile.substring(0, selectedFile.indexOf(".json") + 5);
                        File file = new File(directory);
                        String line = "";
                        StringBuilder lines = new StringBuilder();
                        reader = new BufferedReader(new FileReader(file));
                        while ((line = reader.readLine()) != null) {
                            lines.append(line);
                            lines.append("\n");
                        }
                        FileDisplay.setFont(Font.font("Verdana", FontPosture.REGULAR, 15));
                        String body = "";
                        if (lines.length() > 9) {
                            body = lines.toString().substring(lines.indexOf("\",\"body\":\"") + 10, lines.indexOf("\",\"url\":"));
                        }
                        FileDisplay.setText(body);

                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        try {
                            reader.close();
                        } catch (IOException ex) {
                            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });
    }

    @FXML
    private void handleQuitButton(ActionEvent event) throws IOException {
        System.exit(0);
    }

    @FXML
    private void handleStemButton(ActionEvent event) throws IOException {
        StringBuilder stemedString = new StringBuilder();
        ObservableList<String> result = FXCollections.observableArrayList();
        String[] tokens = enterQuery.getText().split(" ");
        for (String token : tokens) {
            stemedString.append(PorterStemmer.processToken(token));
            stemedString.append(" ");
        }
        result.add(stemedString.toString());
        itemFound.setText("");
        ResultList.setItems(result);
    }

    @FXML
    private void handleVocabButton(ActionEvent event) throws IOException {
        ObservableList<String> result = FXCollections.observableArrayList();
        DiskPositionalIndex diskPositionalIndex = new DiskPositionalIndex(JavaFXApplication1.currentWorkingPath.toString());
        String[] vocab = diskPositionalIndex.getDictionary();
        int termCount = vocab.length;
        itemFound.setText(termCount + " terms found");
        for (String vo : vocab) {
            result.add(vo);
        }
        ResultList.setItems(result);
    }

    @FXML
    private void handleHomePage(ActionEvent event) throws IOException {
        stage = (Stage) HomePage.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("IndexSelectionMode.fxml"));
        scene = new Scene(root);
        stage.setScene(scene);
    }

    @FXML
    private void handlePlotGraph(ActionEvent event) throws IOException{
        xAxis = new NumberAxis();
        yAxis = new NumberAxis();
        xAxis.setLabel("Recall");
        yAxis.setLabel("Precision");
        lineChart = new LineChart<Number,Number>(xAxis,yAxis);
        stage = (Stage) plotGraph.getScene().getWindow();
        PlotPrecisionRecallGraph plotPrecisionRecallGraph  = new PlotPrecisionRecallGraph();     
        ArrayList<XYChart.Series> seriesList = plotPrecisionRecallGraph.plotGraph(enterQuery.getText().trim().toString());
        lineChart.setTitle("P-R Graph comparison for variant formulas");
        for(int i=0;i<seriesList.size();i++){
            lineChart.getData().add(seriesList.get(i)); 
        }
        Button backToSearchScreen = new Button("Back");
        backToSearchScreen.setLayoutX(300);
        backToSearchScreen.setLayoutY(500);
        backToSearchScreen.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                    stage = (Stage) backToSearchScreen.getScene().getWindow();
                try {
                    root = FXMLLoader.load(getClass().getResource("SearchScreen.fxml"));
                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
                    scene = new Scene(root);
                    stage.setScene(scene);
            }
        });
        
        lineChart.setLayoutX(125);
        lineChart.setLayoutY(75);
        backToSearchScreen.setLayoutX(395);
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().addAll(lineChart,backToSearchScreen);      
        scene  = new Scene(anchorPane,800,600);   
        stage.setScene(scene);
    
    }
    
    @FXML
    private void handlePlotMAPGraph(ActionEvent event) throws IOException{
        xAxis1 = new CategoryAxis();
        yAxis1 = new NumberAxis();
        xAxis1.setLabel("Variant Formulas");
        yAxis1.setLabel("Precision");
        barChart = new BarChart<String,Number>(xAxis1,yAxis1);
        stage = (Stage) plotMAPGraph.getScene().getWindow();
        PlotPrecisionRecallGraph plotPrecisionRecallGraph  = new PlotPrecisionRecallGraph();     
        ArrayList<XYChart.Series> seriesList = plotPrecisionRecallGraph.plotMAPGraph();
        barChart.setTitle("MAP comparison for variant formulas");
        for(int i=0;i<seriesList.size();i++){
            barChart.getData().add(seriesList.get(i)); 
        }       
        barChart.setLayoutX(10);
        barChart.setLayoutY(75);

        
        ArrayList<XYChart.Series> seriesList1 = plotPrecisionRecallGraph.plotAverageQueryTime(); 
        xAxis2 = new CategoryAxis();
        yAxis2 = new NumberAxis();
        xAxis2.setLabel("Variant Formulas");
        yAxis2.setLabel("Average query time in ms");
        barChart2 = new BarChart<String,Number>(xAxis2,yAxis2);
        barChart2.setTitle("Average query time comparison for variant formulas");
        for(int i=0;i<seriesList1.size();i++){
            barChart2.getData().add(seriesList1.get(i)); 
        }       
        barChart2.setLayoutX(200);
        barChart2.setLayoutY(75);
        Button backToSearchScreen = new Button("Back");
        backToSearchScreen.setLayoutX(300);
        backToSearchScreen.setLayoutY(500);
        backToSearchScreen.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                    stage = (Stage) backToSearchScreen.getScene().getWindow();
                try {
                    root = FXMLLoader.load(getClass().getResource("SearchScreen.fxml"));
                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
                    scene = new Scene(root);
                    stage.setScene(scene);
            }
        });
        FlowPane flowPane = new FlowPane();
        flowPane.getChildren().addAll(barChart,barChart2,backToSearchScreen);      
        scene  = new Scene(flowPane,1200,1000);   
        stage.setScene(scene);
    
    
    
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
}
