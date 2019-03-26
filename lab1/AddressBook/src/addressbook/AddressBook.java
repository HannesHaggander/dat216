
package addressbook;

import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import se.chalmers.cse.dat215.lab1.Model;

public class AddressBook extends Application {

    //private final int WINDOW_WIDTH = 1280, WINDOW_HEIGHT = 720;
    private final int WINDOW_WIDTH = 600, WINDOW_HEIGHT = 400;

    @Override
    public void start(Stage stage) throws Exception {
        
        ResourceBundle bundle = java.util.ResourceBundle.getBundle("addressbook/resources/AddressBook");
        Parent root = FXMLLoader.load(getClass().getResource("address_book.fxml"), bundle);
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setMinWidth(500);
        stage.setTitle(bundle.getString("application.name"));
        stage.setScene(scene);
        stage.getIcons().add(new Image("addressbook/resources/frameIcon32.gif"));
        stage.show();
        setShutdownHook();
    }

    private void setShutdownHook(){
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                Model.getInstance().shutDown();
            }
        }));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
