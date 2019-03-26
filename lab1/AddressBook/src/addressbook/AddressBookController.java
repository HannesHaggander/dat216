
package addressbook;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import se.chalmers.cse.dat215.lab1.Presenter;

public class AddressBookController implements Initializable {

    private Presenter presenter;
    @FXML private MenuBar menuBar;
    @FXML private Button  buttonNew, buttonDelete;
    @FXML private ListView contactList;
    @FXML private TextField fnameTextField,
            lnameTextField,
            phoneTextField,
            emailTextField,
            addressTextField,
            postcodeTextField,
            cityTextField;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initPresenter();
        setContactListener();
        Arrays.stream(getAllTextFields()).forEach(this::addTextFieldListener);
    }

    private void setContactListener(){
        contactList.getSelectionModel()
                .selectedIndexProperty()
                .addListener((observable, oldValue, newValue) -> presenter.contactsListChanged());
    }

    private TextField[] getAllTextFields(){
        return new TextField[]{
                fnameTextField,
                lnameTextField,
                phoneTextField,
                emailTextField,
                addressTextField,
                postcodeTextField,
                cityTextField
        };
    }

    private void initPresenter(){
        presenter = new Presenter(contactList,
                fnameTextField,
                lnameTextField,
                phoneTextField,
                emailTextField,
                addressTextField,
                postcodeTextField,
                cityTextField);

        presenter.init();
    }


    private void addTextFieldListener(TextField tf){
        tf.focusedProperty().addListener(new TextFieldListener(tf, presenter));
    }

    @FXML
    protected void openAboutActionPerformed(ActionEvent event) throws IOException{
        ResourceBundle bundle = java.util.ResourceBundle.getBundle("addressbook/resources/AddressBook");
        Parent root = FXMLLoader.load(getClass().getResource("address_book_about.fxml"), bundle);
        Stage aboutStage = new Stage();
        aboutStage.setScene(new Scene(root));
        aboutStage.setTitle(bundle.getString("about.title.text"));
        aboutStage.initModality(Modality.APPLICATION_MODAL);
        aboutStage.setResizable(false);
        aboutStage.showAndWait();
    }
    
    @FXML 
    protected void closeApplicationActionPerformed(ActionEvent event) throws IOException{
        Stage addressBookStage = (Stage) menuBar.getScene().getWindow();
        addressBookStage.hide();
    }

    @FXML
    protected void newButtonActionPerformed(ActionEvent event){
        presenter.newContact();
    }

    @FXML
    protected void removeCurrentButtonActionPerformed(ActionEvent event){
        presenter.removeCurrentContact();
    }

}

class TextFieldListener implements ChangeListener<Boolean>{

    private TextField textField;
    private Presenter presenter;

    public TextFieldListener(TextField textField, Presenter presenter){
        this.textField = textField;
        this.presenter = presenter;
    }

    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if(newValue){
            presenter.textFieldFocusGained(textField);
        }
        else {
            presenter.textFieldFocusLost(textField);
        }
    }
}