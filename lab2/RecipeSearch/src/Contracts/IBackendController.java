package Contracts;

import recipesearch.BackendController;
import recipesearch.RecipeSearch;
import se.chalmers.ait.dat215.lab2.Recipe;

import java.util.List;

public interface IBackendController<T> {
    List<Recipe> getAnyMatchRecipe();
    T clearFilter();
    T setCuisine(String cuisine);
    T setMainIngredient(String mainIngredient);
    T setDifficulty(BackendController.recipeDifficulty difficulty);
    T setMaxPrice(int maxPrice);
    T setMaxTime(int maxTime);

}
