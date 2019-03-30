
package recipesearch;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;


public class RecipeSearchController implements Initializable {

    @FXML
    ComboBox foodTypeSetting, mainIngredientSetting;
    @FXML
    RadioButton difficultyAll, difficultyEasy, difficultyMedium, difficultyHard;
    @FXML
    Spinner maxPriceSetting;
    @FXML
    Slider maxTimeSetting;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    protected void onFoodSettingChange(ActionEvent event){

    }

    @FXML
    protected void onDiffAllAction(){
        getRadioButtonStream().forEach(x -> {
            x.setSelected(difficultyAll.isSelected());
            x.setDisable(difficultyAll.isSelected());
        });

        BackendController.getInstance().setDifficulty(BackendController.recipeDifficulty.all);
    }

    @FXML
    protected void onDiffEasyAction(){
        if(difficultyEasy.isDisable()){ return; }

        setDifficultySelection(difficultyEasy);
        BackendController.getInstance().setDifficulty(BackendController.recipeDifficulty.easy);
    }

    @FXML
    protected void onDiffMediumAction(){
        if(difficultyMedium.isDisable()){ return; }

        setDifficultySelection(difficultyMedium);
        BackendController.getInstance().setDifficulty(BackendController.recipeDifficulty.medum);
    }

    @FXML
    protected void onDiffHardAction(){
        if(difficultyHard.isDisable()){ return; }

        setDifficultySelection(difficultyHard);
        BackendController.getInstance().setDifficulty(BackendController.recipeDifficulty.hard);
    }

    private void setDifficultySelection(RadioButton source){
        getRadioButtonStream().forEach(x -> x.setSelected(x == source));
    }

    private Stream<RadioButton> getRadioButtonStream(){
        return Arrays.stream(new RadioButton[] {difficultyEasy, difficultyMedium, difficultyHard});
    }
}