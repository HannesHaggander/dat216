package recipesearch;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import se.chalmers.ait.dat215.lab2.Recipe;

import java.io.IOException;

public class RecipeListItem extends AnchorPane {
    private RecipeSearchController parentController;
    private Recipe recipe;

    @FXML
    protected Label recipeItemTitle, cookingTimeLabel, mealPriceLabel, mealDescription;

    @FXML
    protected ImageView recipeItemImage, foodTypeIcon, difficultyIcon, mealCountryOrigin;

    public RecipeListItem(Recipe recipe, RecipeSearchController recipeSearchController){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("recipe_list_item.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.recipe = recipe;
        this.parentController = recipeSearchController;
        this.recipeItemTitle.setText(this.recipe.getName());
        this.recipeItemImage.setImage(this.recipe.getFXImage());
        this.cookingTimeLabel.setText(String.format("%d %s", this.recipe.getTime(), parentController.getBundle().getString("timeMinutes.text")));
        this.mealPriceLabel.setText(String.format("%d %s", this.recipe.getPrice(), parentController.getBundle().getString("currency.text")));
        this.mealDescription.setText(this.recipe.getDescription());
        this.foodTypeIcon.setImage(BackendController.getInstance().getCuisineIconImage(this.recipe.getMainIngredient()));
        this.difficultyIcon.setImage(BackendController.getInstance().getDifficultyImage(this.recipe.getDifficulty()));
        this.mealCountryOrigin.setImage(BackendController.getInstance().getFoodTypeIcon(this.recipe.getCuisine()));

        setOnMouseClicked(event -> onClick(event));
    }

    @FXML
    protected void onClick(Event event){
        parentController.showDetailedRecipe(recipe);
    }
}