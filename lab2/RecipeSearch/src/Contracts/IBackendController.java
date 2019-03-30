package Contracts;

import recipesearch.RecipeSearch;
import se.chalmers.ait.dat215.lab2.Recipe;

import java.util.List;

public interface IBackendController<T> {
    List<Recipe> getAnyMatchRecipe();
    T clearFilter();
    T setCuisine(String cuisine);
    T setMainIngredient(String mainIngredient);
    T setDifficulty(String difficulty);
    T setMaxPrice(int maxPrice);
    T setMaxTime(int maxTime);

}
