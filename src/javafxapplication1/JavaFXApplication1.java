package javafxapplication1;

import java.nio.file.Path;
import java.util.List;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Abhishek
 */
public class JavaFXApplication1 extends Application {

    public static List<String> fileNames;
    public static Path currentWorkingPath;
    public static String searchQuery;
    public static String indexMode;
    public static String queryMode;
    public static int formulaMode;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("IndexSelectionMode.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
