package recipesearch;

import se.chalmers.ait.dat215.lab2.Ingredient;
import se.chalmers.ait.dat215.lab2.Recipe;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class BackendController  {
    private String cuisine, mainIngredient, difficulty;
    private Integer maxPrice, maxTime;
    private static BackendController instance;

    public enum recipeDifficulty {
        easy,
        medium,
        hard,
        all
    }

    public BackendController(){ ReadFile(); }

    public static BackendController getInstance(){
        if(instance == null){ instance = new BackendController(); }
        return instance;
    }

    public List<Recipe> getAnyMatchRecipe() {
        return recipes.stream()
                .filter(x -> !nonEmptyOrNull(cuisine) || x.getCuisine().equals(cuisine))
                .filter(x -> !nonEmptyOrNull(mainIngredient) || x.getMainIngredient().equals(mainIngredient))
                .filter(x -> !nonEmptyOrNull(difficulty) || x.getDifficulty().equals(difficulty))
                .filter(x -> !nonNull(maxPrice) || x.getPrice() <= maxPrice)
                .filter(x -> !nonNull(maxTime) || x.getTime() <= maxTime)
                .collect(Collectors.toList());
    }

    public List<Recipe> getBestMatchRecipes(){
        recipes.forEach(x -> x.setMatch(0));

        recipes.stream().filter(x -> !nonEmptyOrNull(cuisine) || x.getCuisine().equals(cuisine))
                .forEach(x -> x.setMatch(x.getMatch()+1));

        recipes.stream().filter(x -> !nonEmptyOrNull(mainIngredient) || x.getMainIngredient().equals(mainIngredient))
                .forEach(x -> x.setMatch(x.getMatch()+1));

        recipes.stream().filter(x -> !nonEmptyOrNull(difficulty) || x.getDifficulty().equals(difficulty))
                .forEach(x -> x.setMatch(x.getMatch()+1));

        recipes.stream().filter(x -> !nonNull(maxPrice) || x.getPrice() <= maxPrice)
                .forEach(x -> x.setMatch(x.getMatch()+1));

        recipes.stream().filter(x -> !nonNull(maxTime) || x.getTime() <= maxTime)
                .forEach(x -> x.setMatch(x.getMatch()+1));

        return recipes.stream()
                .sorted((o1, o2) -> o1.getMatch() == o2.getMatch() ? 0 : o1.getMatch() - o2.getMatch() < 0 ? 1 : -1)
                .collect(Collectors.toList());
    }



    public List<Recipe> getAllRecipes(){
        return new ArrayList<>(recipes);
    }

    public BackendController clearFilter() {
        this.cuisine = null;
        this.mainIngredient = null;
        this.difficulty = null;
        this.maxTime = null;
        this.maxPrice = null;
        return this;
    }

    private boolean nonNull(Object o){
        return o != null;
    }

    private boolean nonEmptyOrNull(String s){ return nonNull(s) && !s.isEmpty(); }

    public BackendController setCuisine(String cuisine) {
        this.cuisine = cuisine;
        return this;
    }

    public BackendController setMainIngredient(String mainIngredient) {
        this.mainIngredient = mainIngredient;
        return this;
    }

    public BackendController setDifficulty(final recipeDifficulty difficulty) {
        switch (difficulty){
            case easy: this.difficulty = "Lätt"; break;
            case medium: this.difficulty = "Mellan"; break;
            case hard: this.difficulty = "Svår"; break;
            default: this.difficulty = "";
        }
        return this;
    }

    public BackendController setMaxPrice(int maxPrice) {
        if(maxPrice < 0){ maxPrice = 0; } //max price has to be positive
        this.maxPrice = maxPrice;
        return this;
    }

    public BackendController setMaxTime(int maxTime){
        if(maxTime < 0){ maxTime = 0; }// max time has to be a positive value
        this.maxTime = maxTime;
        return this;
    }

    private Set<Recipe> recipes;
    private void ReadFile(){
        File file = new File("recipes/recipes.txt");
        BufferedReader dis = null;

        try {
            FileReader reader = new FileReader(file);
            dis = new BufferedReader(reader);
            this.recipes = new HashSet();
            String line = null;
            String name = null;
            int servings = 0;
            int time = 0;
            int price = 0;
            String cuisine = null;
            String difficulty = null;
            String mainIngredient = null;
            String instruction = null;
            String description = null;
            String imagePath = null;
            ArrayList ingredients = null;

            while((line = dis.readLine()) != null) {
                if (line.equals("#name")) {
                    name = dis.readLine();
                } else if (line.equals("#servings")) {
                    servings = Integer.parseInt(dis.readLine());
                } else if (line.equals("#difficulty")) {
                    difficulty = dis.readLine();
                } else if (line.equals("#time")) {
                    time = Integer.parseInt(dis.readLine());
                } else if (line.equals("#cuisine")) {
                    cuisine = dis.readLine();
                } else if (line.equals("#price")) {
                    price = Integer.parseInt(dis.readLine());
                } else if (line.equals("#image")) {
                    imagePath = "recipes/" + dis.readLine();
                } else if (line.equals("#mainIngredient")) {
                    mainIngredient = dis.readLine();
                } else if (line.equals("#ingredients")) {
                    Set<Ingredient> temp = new HashSet();
                    ingredients = new ArrayList();

                    while(!(line = dis.readLine()).equals("#end")) {
                        int amount = Integer.parseInt(line);
                        String unit = dis.readLine();
                        String iName = dis.readLine();
                        Ingredient i = new Ingredient(iName, amount, unit);
                        if (!temp.contains(i)) {
                            ingredients.add(i);
                            temp.add(i);
                        }
                    }
                } else if (line.equals("#instruction")) {
                    instruction = dis.readLine();
                } else if (line.equals("#description")) {
                    description = dis.readLine();
                    this.recipes.add(new Recipe(name, servings, difficulty, time, cuisine, price, mainIngredient, instruction, description, new ImageIcon(imagePath), imagePath, ingredients));
                }
            }

            dis.close();
            reader.close();

        } catch (Exception var21) {
            System.out.println(var21.getMessage());
        }
    }
}
