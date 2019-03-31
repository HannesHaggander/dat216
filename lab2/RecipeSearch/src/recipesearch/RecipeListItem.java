package recipesearch;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import se.chalmers.ait.dat215.lab2.Recipe;

import java.io.IOException;

public class RecipeListItem extends AnchorPane {
    private RecipeSearchController parentController;
    private Recipe recipe;


    @FXML
    protected Text recipeItemTitle;

    @FXML
    protected ImageView recipeItemImage;

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
    }
}