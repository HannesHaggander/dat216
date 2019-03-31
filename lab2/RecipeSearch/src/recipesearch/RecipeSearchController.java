package recipesearch;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.layout.FlowPane;


public class RecipeSearchController implements Initializable {

    @FXML
    protected ComboBox foodTypeSetting, mainIngredientSetting;
    @FXML
    protected RadioButton difficultyAll, difficultyEasy, difficultyMedium, difficultyHard;
    @FXML
    protected Spinner maxPriceSetting;
    @FXML
    protected Slider maxTimeSetting;
    @FXML
    protected FlowPane recipeItemFlowPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setDifficultySelection(difficultyAll);
        updateRecipeList();
    }

    @FXML
    protected void onFoodSettingChange(ActionEvent event){

    }

    @FXML
    protected void onDiffAllAction(){
        if(difficultyAll.isDisable()){ return; }

        setDifficultySelection(difficultyAll);
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
        return Arrays.stream(new RadioButton[] {difficultyEasy, difficultyMedium, difficultyHard, difficultyAll});
    }

    private void updateRecipeList(){
        recipeItemFlowPane.getChildren().clear();
        recipeItemFlowPane.getChildren()
                .addAll(BackendController.getInstance()
                        .getAnyMatchRecipe()
                        .stream()
                        .map(x -> new RecipeListItem(x, this))
                        .collect(Collectors.toList()));
        ;
    }
}