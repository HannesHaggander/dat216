package recipesearch;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.FlowPane;
import javafx.util.Callback;
import se.chalmers.ait.dat215.lab2.Recipe;

import javax.swing.text.IconView;

public class RecipeSearchController implements Initializable {

    @FXML
    protected ComboBox foodTypeSetting, mainIngredientSetting, filterResultsComboBar;
    @FXML
    protected RadioButton difficultyAll, difficultyEasy, difficultyMedium, difficultyHard;
    @FXML
    protected Spinner maxPriceSetting;
    @FXML
    protected Slider maxTimeSetting;
    @FXML
    protected FlowPane recipeItemFlowPane;
    @FXML
    protected Label maxTimeLabel, headerText;
    @FXML
    protected AnchorPane recipeDetailed;


    //detail view
    @FXML
    protected ImageView detailedImage, detailMainIngredient, detailDifficulty, recipeDetailedExit;
    @FXML
    protected Label detailedText, detailTime, detailPrice, detailDescription, detailInstructions, detailPortions, detailIngredients;


    private ResourceBundle bundle;
    private ToggleGroup difficultyGroup;

    private HashMap<Recipe, RecipeListItem> listItemCache = new HashMap<>();
    private String activeFilter;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.bundle = rb;
        setupMainIngredientComboBox();
        setupCuisineComboBox();
        SetupResultFilterComboBox();
        setToggleGroup();
        setupPriceSpinner();
        setupTimeSlider();
        updateRecipeList();
        Platform.runLater(() -> mainIngredientSetting.requestFocus());
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

    @FXML
    protected void tmp(){
        headerText.setText(bundle.getString("tmp.text"));
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

        Callback<ListView<String>, ListCell<String>> cellFactory = new Callback<ListView<String>, ListCell<String>>(){
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>(){
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);

                        if(item == null || empty){ setGraphic(null); }
                        else {
                            ImageView iconImageView = new ImageView(BackendController.getInstance().getCuisineIconImage(item));
                            iconImageView.setFitHeight(12);
                            iconImageView.setPreserveRatio(true);
                            setGraphic(iconImageView);
                        }
                    }
                };
            }
        };

        mainIngredientSetting.setButtonCell(cellFactory.call(null));
        mainIngredientSetting.setCellFactory(cellFactory);

        mainIngredientSetting.getSelectionModel().select(0);
        mainIngredientSetting.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    BackendController.getInstance().setMainIngredient(newValue.equals(defSelection) ? "" : newValue.toString());
                    updateRecipeList();
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

        Callback<ListView<String>, ListCell<String>> cellFactory = new Callback<ListView<String>, ListCell<String>>() {

            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>(){
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);

                        if(empty){ setGraphic(null); }
                        else {
                            ImageView iconView = new ImageView(BackendController.getInstance().getFoodTypeIcon(item));
                            iconView.setFitHeight(12);
                            iconView.setPreserveRatio(true);
                            setGraphic(iconView);
                        }
                    }
                };
            }
        };

        foodTypeSetting.setButtonCell(cellFactory.call(null));
        foodTypeSetting.setCellFactory(cellFactory);

        foodTypeSetting.getSelectionModel().select(0);
        foodTypeSetting.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    BackendController.getInstance().setCuisine(newValue.equals(defSelection) ? "" : newValue.toString());
                    updateRecipeList();
                });
    }

    private void SetupResultFilterComboBox(){
        String[] filterOptions = new String[]{
                bundle.getString("filterResults.bestMatch"),
                bundle.getString("filterResults.onlyMatches")
        };
        activeFilter = filterOptions[0];
        filterResultsComboBar.getItems().addAll(filterOptions);
        filterResultsComboBar.getSelectionModel().select(0);
        filterResultsComboBar.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    activeFilter = newValue.toString();
                    updateRecipeList();
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
            try{
                BackendController.getInstance().setMaxPrice(Integer.valueOf(maxPriceSetting.getEditor().getText()));
            }
            catch (Exception ex){
                //ignore throw
                System.err.println(ex.toString());
            }
            updateRecipeList();
        });

        //update backend controller on lost focus
        maxPriceSetting.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) {
                try{
                    BackendController.getInstance().setMaxPrice(Integer.valueOf(maxPriceSetting.getEditor().getText()));
                    updateRecipeList();
                }
                catch (Exception ex){
                    //ignore throw
                    System.err.println(ex.toString());
                }
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
        maxTimeSetting.setValue(maxTimeSetting.getMax());
        maxTimeLabel.setText(String.format("%d %s", maxTimeSetting.valueProperty().getValue().intValue(), bundle.getString("timeMinutes.text")));
        maxTimeSetting.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null || newValue.equals(oldValue)){ return; }
            maxTimeLabel.setText(String.format("%d %s", newValue.intValue(), bundle.getString("timeMinutes.text")));
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
        List<Recipe> recipes;

        if(activeFilter.equals(bundle.getString("filterResults.onlyMatches"))){
            recipes = BackendController.getInstance().getAnyMatchRecipe();
        }
        else {
            recipes = BackendController.getInstance().getBestMatchRecipes();
        }

        recipeItemFlowPane.getChildren()
                .addAll(recipes.stream()
                        .map(this::insertListItem)
                        .collect(Collectors.toList()));
    }

    private RecipeListItem insertListItem(Recipe x){

        if(!listItemCache.containsKey(x)){
            RecipeListItem item = new RecipeListItem(x, this);
            listItemCache.put(x, item); }
        return listItemCache.get(x);
    }


    // Navigation functions

    @FXML
    protected void closeRecipeView(){
        recipeDetailed.toBack();
    }

    @FXML
    protected void hoverEnterDetailedExit(){
        recipeDetailedExit.setImage(loadImage("recipesearch/resources/icon_close_hover.png"));
    }

    @FXML
    protected void pressEnterDetailedExit(){
        recipeDetailedExit.setImage(loadImage("recipesearch/resources/icon_close_pressed.png"));
    }

    @FXML
    protected void hoverExitDetailedExit(){
        recipeDetailedExit.setImage(loadImage("recipesearch/resources/icon_close.png"));
    }

    @FXML
    protected void mouseTrap(Event event){
        event.consume();
    }

    private Image loadImage(String path){
        return new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(path)));
    }

    protected void showDetailedRecipe(Recipe recipe){
        populateDetailedView(recipe);
        recipeDetailed.toFront();
    }

    private void populateDetailedView(Recipe recipe){
        recipeDetailedExit.setImage(loadImage("recipesearch/resources/icon_close.png"));
        detailedImage.setImage(recipe.getFXImage());
        detailedText.setText(recipe.getName());
        detailMainIngredient.setImage(BackendController.getInstance().getCuisineIconImage(recipe.getMainIngredient()));
        detailDifficulty.setImage(BackendController.getInstance().getDifficultyImage(recipe.getDifficulty()));
        detailTime.setText(String.format("%d %s",recipe.getTime(), bundle.getString("timeMinutes.text")));
        detailPrice.setText(String.format("%d %s",recipe.getPrice(), bundle.getString("currency.text")));
        detailDescription.setText(recipe.getDescription());
        detailInstructions.setText(recipe.getInstruction());
        detailPortions.setText(String.format("%d %s", recipe.getServings(), bundle.getString("servings.text")));
        detailIngredients.setText(String.join("\n", recipe.getIngredients().stream().map(ingredient -> ingredient.toString()).collect(Collectors.toList())));
    }

    public ResourceBundle getBundle(){
        return bundle;
    }
}