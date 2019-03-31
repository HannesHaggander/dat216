
package recipesearch;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import se.chalmers.ait.dat215.lab2.Recipe;
import se.chalmers.ait.dat215.lab2.RecipeDatabase;
import se.chalmers.ait.dat215.lab2.SearchFilter;

import static se.chalmers.ait.dat215.lab2.RecipeDatabase.getSharedInstance;

public class RecipeSearch extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        ResourceBundle bundle = java.util.ResourceBundle.getBundle("recipesearch/resources/RecipeSearch");
        Parent root = new FXMLLoader(getClass().getResource("recipe_search.fxml"), bundle).load();
        Scene scene = new Scene(root, 800, 500);
        stage.setTitle(bundle.getString("application.name"));
        stage.setScene(scene);
        stage.setMinHeight(250);
        stage.setMinWidth(300);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
