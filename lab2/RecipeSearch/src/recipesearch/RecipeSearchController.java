package recipesearch;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.layout.FlowPane;
import se.chalmers.ait.dat215.lab2.Recipe;

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

    private ResourceBundle bundle;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.bundle = rb;
        setDifficultySelection(difficultyAll, BackendController.recipeDifficulty.all);
        setupMainIngredientComboBox();
        setupCuisineComboBox();
        updateRecipeList();
    }

    @FXML
    protected void onFoodSettingChange(ActionEvent event){

    }

    @FXML
    protected void onDiffAllAction(){
        if(difficultyAll.isDisable()){ return; }

        setDifficultySelection(difficultyAll, BackendController.recipeDifficulty.all);
    }

    @FXML
    protected void onDiffEasyAction(){
        if(difficultyEasy.isDisable()){ return; }

        setDifficultySelection(difficultyEasy, BackendController.recipeDifficulty.easy);
    }

    @FXML
    protected void onDiffMediumAction(){
        if(difficultyMedium.isDisable()){ return; }

        setDifficultySelection(difficultyMedium, BackendController.recipeDifficulty.medium);
    }

    @FXML
    protected void onDiffHardAction(){
        if(difficultyHard.isDisable()){ return; }

        setDifficultySelection(difficultyHard, BackendController.recipeDifficulty.hard);
    }

    private void setupMainIngredientComboBox(){
        final String defSelection = bundle.getString("showAll.text");
        mainIngredientSetting.getItems().add(defSelection);
        mainIngredientSetting.getItems().addAll(BackendController.getInstance()
                .getAllRecipes()
                .stream()
                .map(Recipe::getMainIngredient)
                .distinct()
                .collect(Collectors.toList()));

        mainIngredientSetting.getSelectionModel().select(0);
        mainIngredientSetting.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    BackendController.getInstance().setMainIngredient(newValue.equals(defSelection) ? "" : newValue.toString());
                    updateRecipeList();
                    System.out.println("Set main ingredient to: " + newValue);
                });
    }

    private void setupCuisineComboBox(){
        final String defSelection = bundle.getString("showAll.text");
        foodTypeSetting.getItems().add(defSelection);
        foodTypeSetting.getItems()
                .addAll(BackendController.getInstance()
                        .getAllRecipes()
                        .stream()
                        .map(Recipe::getCuisine)
                        .distinct()
                        .collect(Collectors.toList()));
        foodTypeSetting.getSelectionModel().select(0);
        foodTypeSetting.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    BackendController.getInstance().setCuisine(newValue.equals(defSelection) ? "" : newValue.toString());
                    updateRecipeList();
                    System.out.println("Set cuisine to: " + newValue);
                });
    }

    private void setDifficultySelection(RadioButton source, BackendController.recipeDifficulty difficulty){
        getRadioButtonStream().forEach(x -> x.setSelected(x == source));
        BackendController.getInstance().setDifficulty(difficulty);
        updateRecipeList();
    }

    private Stream<RadioButton> getRadioButtonStream(){
        return Arrays.stream(new RadioButton[] {difficultyEasy, difficultyMedium, difficultyHard, difficultyAll});
    }

    private void updateRecipeList(){
        recipeItemFlowPane.getChildren().clear();
        List<Recipe> list = BackendController.getInstance().getAnyMatchRecipe();
        recipeItemFlowPane.getChildren()
                .addAll(BackendController.getInstance()
                        .getAnyMatchRecipe()
                        .stream()
                        .map(x -> new RecipeListItem(x, this))
                        .collect(Collectors.toList()));
        System.out.println("Displaying " + list.size() + " items");
    }
}