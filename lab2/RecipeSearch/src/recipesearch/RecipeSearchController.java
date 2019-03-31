package recipesearch;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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
    @FXML
    protected Label maxTimeLabel;

    private ResourceBundle bundle;
    private ToggleGroup difficultyGroup;

    private HashMap<Recipe, RecipeListItem> listItemCache = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.bundle = rb;
        setupMainIngredientComboBox();
        setupCuisineComboBox();
        setToggleGroup();
        setupPriceSpinner();
        setupTimeSlider();
        updateRecipeList();
    }

    @FXML
    protected void onFoodSettingChange(ActionEvent event){

    }

    @FXML
    protected void onDiffAllAction(){
        if(difficultyAll.isDisable()){ return; }

        setDifficultySelection(BackendController.recipeDifficulty.all);
    }

    @FXML
    protected void onDiffEasyAction(){
        if(difficultyEasy.isDisable()){ return; }

        setDifficultySelection(BackendController.recipeDifficulty.easy);
    }

    @FXML
    protected void onDiffMediumAction(){
        if(difficultyMedium.isDisable()){ return; }

        setDifficultySelection(BackendController.recipeDifficulty.medium);
    }

    @FXML
    protected void onDiffHardAction(){
        if(difficultyHard.isDisable()){ return; }

        setDifficultySelection(BackendController.recipeDifficulty.hard);
    }

    /***
     * Set main ingredient combo box with ingredients from the backend
     */
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

    /***
     * Add selectable items that are available in the database. No need to define them myself.
     */
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

    /***
     * Setup price spinner with lower bound of cheapest price and maximum price of the most expensive dish
     */
    private void setupPriceSpinner(){
         // get all prices in ascending order
         List<Integer> prices = BackendController.getInstance()
                 .getAllRecipes()
                 .stream()
                 .map(recipe -> recipe.getPrice())
                 .sorted()
                 .collect(Collectors.toList());
        //set min price to the cheapest price and the maximum to the highest price.
        SpinnerValueFactory<Integer> maxValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(
                prices.get(0), prices.get(prices.size()-1), prices.get(prices.size()-1), 10);
        maxPriceSetting.setValueFactory(maxValueFactory);

        // update backend controller on change
        maxPriceSetting.valueProperty().addListener((observable, oldValue, newValue) -> {
            BackendController.getInstance().setMaxPrice(Integer.valueOf(maxPriceSetting.getEditor().getText()));
            updateRecipeList();
        });

        //update backend controller on lost focus
        maxPriceSetting.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) {
                BackendController.getInstance().setMaxPrice(Integer.valueOf(maxPriceSetting.getEditor().getText()));
                updateRecipeList();
            }
        });
    }

    /***
     * Update slider if changed, also changes the text view with the value even if the value is currently changing.
     */
    private void setupTimeSlider(){
        // get times sorted in ascending order
        List<Integer> times = BackendController.getInstance()
                .getAllRecipes()
                .stream()
                .map(x -> x.getTime())
                .sorted()
                .collect(Collectors.toList());
        maxTimeSetting.setMin(times.get(0));        //set min time to lowest item time
        maxTimeSetting.setMax(times.get(times.size()-1)); //set max time to highest item time
        maxTimeSetting.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null || newValue.equals(oldValue)){ return; }
            maxTimeLabel.setText(String.format("%d %s", newValue.intValue(), bundle.getString("timeMinutes.text")));

            if(maxTimeSetting.isValueChanging()){ return; }
            BackendController.getInstance().setMaxTime(newValue.intValue());
            updateRecipeList();
        });
    }

    /***
     * Add difficulty buttons to the same toggle group
     */
    private void setToggleGroup(){
        difficultyGroup = new ToggleGroup();
        getRadioButtonStream().forEach(x -> x.setToggleGroup(difficultyGroup));
        difficultyAll.setSelected(true);
    }

    /***
     * Update backend difficulty setting.
     * @param difficulty
     */
    private void setDifficultySelection(BackendController.recipeDifficulty difficulty){
        BackendController.getInstance().setDifficulty(difficulty);
        updateRecipeList();
    }

    /***
     * Helper function to get all the buttons in a stream.
     * @return
     */
    private Stream<RadioButton> getRadioButtonStream(){
        return Arrays.stream(new RadioButton[] {difficultyEasy, difficultyMedium, difficultyHard, difficultyAll});
    }

    /***
     * Update the visual representation of the backend filtered data
     */
    private void updateRecipeList(){
        recipeItemFlowPane.getChildren().clear();
        List<Recipe> list = BackendController.getInstance().getAnyMatchRecipe();

        recipeItemFlowPane.getChildren()
                .addAll(BackendController.getInstance()
                        .getAnyMatchRecipe()
                        .stream()
                        .map(this::insertListItem)
                        .collect(Collectors.toList()));
        System.out.println("Displaying " + list.size() + " items");
    }

    private RecipeListItem insertListItem(Recipe x){
        if(!listItemCache.containsKey(x)){ listItemCache.put(x, new RecipeListItem(x, this)); }
        return listItemCache.get(x);
    }
}