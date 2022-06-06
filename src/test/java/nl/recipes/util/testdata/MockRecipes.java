package nl.recipes.util.testdata;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.recipes.domain.Recipe;
import nl.recipes.domain.RecipeType;

public class MockRecipes {
  
  private MockIngredients mockIngredients = new MockIngredients();

  public ObservableList<Recipe> getRecipeListBasic() {
    List<Recipe> recipeList = new ArrayList<>();
    recipeList.add(new Recipe.RecipeBuilder()
        .withName("First recipe")
        .withRecipeType(RecipeType.HOOFDGERECHT)
        .withIngredients(mockIngredients.getIngredientSet()).build(1L));
    recipeList.add(new Recipe.RecipeBuilder()
        .withName("Second recipe")
        .withRecipeType(RecipeType.AMUSE).build(2L));
    recipeList.add(new Recipe.RecipeBuilder()
        .withName("Third recipe")
        .withRecipeType(RecipeType.NAGERECHT).build(3L));
    recipeList.add(new Recipe.RecipeBuilder()
        .withName("Fourth recipe")
        .withRecipeType(RecipeType.HOOFDGERECHT).build(4L));

    return FXCollections.observableList(recipeList);
  }
}
